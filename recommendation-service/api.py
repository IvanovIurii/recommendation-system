from fastapi import FastAPI, APIRouter
from pydantic import BaseModel
import joblib
import pandas as pd
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

    # precompute supplier embeddings
    texts = (sup_df['supplier_product_name'] + ' ' + sup_df['supplier_product_description']).tolist()
    sup_emb = EMBEDDER.encode(texts, convert_to_numpy=True, normalize_embeddings=True)

    print(f"Loaded resources: {len(sup_df)} suppliers and embeddings")


@api_router.post("/recommend")
def recommend(rfq: RFQRequest):
    global pipeline, sup_df, geolocator, sup_emb

    result = recommender.recommend_suppliers(
        sup_df, pipeline, geolocator, sup_emb,
        rfq_title=rfq.rfq_title,
        rfq_description=rfq.rfq_description,
        rfq_location=rfq.rfq_location,
        rfq_product_type=rfq.rfq_product_type,
        top_n=rfq.top_n
    )

    return [format_result(item) for item in result]


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
