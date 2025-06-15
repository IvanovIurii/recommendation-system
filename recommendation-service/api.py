from fastapi import FastAPI, HTTPException, APIRouter
from pydantic import BaseModel
import joblib
import pandas as pd
import numpy as np
from geopy.geocoders import Nominatim
from sentence_transformers import SentenceTransformer

import recommender

MODEL_PATH = 'rfq_model.pkl'
# this one has can be updated regularly and should be taken most likely from Cache (Redis)
SUPPLIERS_PATH = 'data/suppliers_data_unique.csv'
EMBEDDER = SentenceTransformer('all-MiniLM-L6-v2')

app = FastAPI()
api_router = APIRouter(prefix="/recommendation-service/internal_api")

pipeline = None
sup_df = None
geolocator = None
sup_emb = None


class RFQRequest(BaseModel):
    rfq_title: str
    rfq_description: str
    rfq_location: str
    rfq_product_type: str
    top_n: int = 5


@app.on_event("startup")
def load_resources():
    global pipeline, sup_df, geolocator, sup_emb

    pipeline = joblib.load(MODEL_PATH)
    sup_df = pd.read_csv(SUPPLIERS_PATH)
    geolocator = Nominatim(user_agent="rfq_api")

    # Precompute supplier embeddings
    texts = (sup_df['supplier_product_name'] + ' ' + sup_df['supplier_product_description']).tolist()
    sup_emb = EMBEDDER.encode(texts, convert_to_numpy=True, normalize_embeddings=True)

    print(f"Loaded resources: {len(sup_df)} suppliers and embeddings")


# ---------- Recommendation Endpoint ----------

# todo: its better call recommender.recommend(..)
@api_router.post("/recommend")
def recommend(rfq: RFQRequest):
    global pipeline, sup_df, geolocator, sup_emb

    cache = recommender.load_cache()
    recommender.geocode_cities({rfq.rfq_location}, geolocator, cache)

    if cache[rfq.rfq_location]['lat'] is None:
        raise HTTPException(status_code=400, detail="Could not geocode RFQ location")

    dists = []
    for areas in sup_df['supplier_delivery_area']:
        cities = [c.strip() for c in areas.split(',')]
        d = recommender.compute_min_distance(rfq.rfq_location, cities, cache)
        dists.append(d if d is not None else 0.0)

    max_d = max(dists) or 1.0
    dist_norm = [1 - d / max_d for d in dists]

    # 3) Compute text similarity
    rfq_text = rfq.rfq_title + ' ' + rfq.rfq_description
    rfq_emb = EMBEDDER.encode([rfq_text], convert_to_numpy=True, normalize_embeddings=True)[0]
    text_sim = np.dot(sup_emb, rfq_emb)

    # 4) Predict & score
    X_pred = np.vstack([dist_norm, text_sim]).T
    probs = pipeline.predict_proba(X_pred)[:, 1]
    sup_df['score'] = probs

    candidates = sup_df[sup_df['supplier_product_type'] == rfq.rfq_product_type]

    if candidates.empty:
        candidates = sup_df

    top = candidates.nlargest(rfq.top_n, 'score')
    result = top.to_dict(orient='records')

    print(result)

    return [format_result(item) for item in top.to_dict(orient="records")]


def format_result(result_item):
    profile = {
        "supplier_name": result_item['supplier_name'],
        "supplier_product_name": result_item['supplier_product_name'],
        "supplier_product_description": result_item['supplier_product_description'],
        "supplier_product_type": result_item['supplier_product_type'],
        "supplier_delivery_area": list(
            map(
                lambda city: city.strip(),
                result_item['supplier_delivery_area'].split(',')
            )
        ),
        "score": result_item['score']
    }
    return profile


app.include_router(api_router)
