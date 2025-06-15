import argparse
import os
import json
import pandas as pd
import joblib
import time
from geopy.geocoders import Nominatim
from geopy.distance import geodesic
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import roc_auc_score, average_precision_score
from sentence_transformers import SentenceTransformer
import numpy as np

CACHE_PATH = 'city_cache.json'
EMBEDDER = SentenceTransformer('all-MiniLM-L6-v2')


def load_cache(path=CACHE_PATH):
    if os.path.exists(path):
        with open(path, 'r') as f:
            return json.load(f)
    return {}


def save_cache(cache, path=CACHE_PATH):
    with open(path, 'w') as f:
        json.dump(cache, f)


def geocode_cities(cities, geolocator, cache):
    updated = False
    # on prediction cities are already in cache
    for city in cities:
        if city in cache:
            continue

        try:
            loc = geolocator.geocode(city)
            cache[city] = {'lat': loc.latitude, 'lon': loc.longitude} if loc else {'lat': None, 'lon': None}
            updated = True

        except Exception:
            cache[city] = {'lat': None, 'lon': None}
            updated = True

    if updated:
        save_cache(cache)

    return cache


def compute_min_distance(rfq_city, sup_cities, cache):
    rfq = cache.get(rfq_city)

    if not rfq or rfq['lat'] is None:
        return None

    distances = []
    for city in sup_cities:
        sup = cache.get(city)

        if sup and sup['lat'] is not None:
            distances.append(geodesic((rfq['lat'], rfq['lon']), (sup['lat'], sup['lon'])).km)

    return min(distances) if distances else None


def train_model(combined_path, model_path):
    start = time.time()
    df = pd.read_csv(combined_path)

    geolocator = Nominatim(user_agent="rfq_cli")
    cache = load_cache()
    rfq_cities = set(df['rfq_location'].unique())
    sup_cities = set(c.strip() for areas in df['supplier_delivery_area'] for c in areas.split(','))
    geocode_cities(rfq_cities | sup_cities, geolocator, cache)

    # compute normalized distance
    distances = []
    for _, row in df.iterrows():
        areas = [c.strip() for c in row['supplier_delivery_area'].split(',')]
        d = compute_min_distance(row['rfq_location'], areas, cache)
        distances.append(d if d is not None else 0.0)

    max_d = max(distances) or 1.0
    df['dist_norm'] = [1 - d / max_d for d in distances]

    # compute semantic text similarity
    rfq_texts = (df['rfq_title'] + ' ' + df['rfq_description']).tolist()
    sup_texts = (df['supplier_product_name'] + ' ' + df['supplier_product_description']).tolist()

    rfq_emb = EMBEDDER.encode(rfq_texts, convert_to_numpy=True, normalize_embeddings=True)
    sup_emb = EMBEDDER.encode(sup_texts, convert_to_numpy=True, normalize_embeddings=True)

    df['text_sim'] = np.einsum('ij,ij->i', rfq_emb, sup_emb)

    x = df[['dist_norm', 'text_sim']]
    y = df['product_match']

    X_train, X_test, y_train, y_test = train_test_split(
        x, y, test_size=0.2, stratify=y, random_state=42
    )

    pipeline = Pipeline([
        ('scale', StandardScaler()),
        ('clf', RandomForestClassifier(
            n_estimators=100, max_depth=None,
            class_weight='balanced', random_state=42
        ))
    ])

    pipeline.fit(X_train, y_train)

    y_proba = pipeline.predict_proba(X_test)[:, 1]

    print("=== Classification Report ===")
    print(f"ROC AUC: {roc_auc_score(y_test, y_proba):.3f}")
    print(f"PR AUC:  {average_precision_score(y_test, y_proba):.3f}")

    joblib.dump(pipeline, model_path)
    print(f"Model saved to {model_path}")

    end = time.time()
    print(f"Elapsed: {end - start:.4f} seconds")


def recommend_suppliers(
        sup_df,
        pipeline,
        geolocator,
        sup_emb,
        rfq_title,
        rfq_description,
        rfq_location,
        rfq_product_type,
        top_n
):
    start = time.time()

    cache = load_cache()
    rfq_cities = {rfq_location}
    sup_cities = set(c.strip() for areas in sup_df['supplier_delivery_area'] for c in areas.split(','))
    geocode_cities(sup_cities | rfq_cities, geolocator, cache)

    if cache[rfq_location]['lat'] is None:
        raise Exception("Could not geocode RFQ location")

    dists = []
    for areas in sup_df['supplier_delivery_area']:
        cities = [c.strip() for c in areas.split(',')]
        d = compute_min_distance(rfq_location, cities, cache)
        dists.append(d if d is not None else 0.0)

    max_d = max(dists) or 1.0
    dist_norm = [1 - d / max_d for d in dists]

    rfq_texts = [rfq_title + ' ' + rfq_description]
    rfq_emb = EMBEDDER.encode(rfq_texts, convert_to_numpy=True, normalize_embeddings=True)[0]

    text_sim = np.dot(sup_emb, rfq_emb)

    X_pred = pd.DataFrame({'dist_norm': dist_norm, 'text_sim': text_sim})
    probs = pipeline.predict_proba(X_pred)[:, 1]
    sup_df['score'] = probs

    candidates = sup_df[sup_df['supplier_product_type'] == rfq_product_type]

    if candidates.empty:
        candidates = sup_df

    top = candidates.nlargest(top_n, 'score')

    end = time.time()
    print(f"Elapsed: {end - start:.3f} seconds")

    return top.to_dict(orient="records")


def main():
    parser = argparse.ArgumentParser(description="RFQ Recommender CLI")
    subparsers = parser.add_subparsers(dest='command', required=True)

    train = subparsers.add_parser('train')
    train.add_argument('--dataset', required=True)
    train.add_argument('--model', required=True)

    predict = subparsers.add_parser('predict')
    predict.add_argument('--suppliers', required=True)
    predict.add_argument('--model', required=True)
    predict.add_argument('--rfq_title', required=True)
    predict.add_argument('--rfq_description', required=True)
    predict.add_argument('--rfq_location', required=True)
    predict.add_argument('--rfq_product_type', required=True)
    predict.add_argument('--top_n', type=int, default=5)

    args = parser.parse_args()

    if args.command == 'train':
        train_model(args.dataset, args.model)
    else:
        sup_df = pd.read_csv(args.suppliers)
        pipeline = joblib.load(args.model)
        geolocator = Nominatim(user_agent="rfq_cli")

        sup_texts = (sup_df['supplier_product_name'] + ' ' + sup_df['supplier_product_description']).tolist()
        sup_emb = EMBEDDER.encode(sup_texts, convert_to_numpy=True, normalize_embeddings=True)

        recommend_suppliers(sup_df,
                            pipeline,
                            geolocator,
                            sup_emb,
                            args.rfq_title,
                            args.rfq_description,
                            args.rfq_location,
                            args.rfq_product_type,
                            args.top_n)


if __name__ == '__main__':
    main()
