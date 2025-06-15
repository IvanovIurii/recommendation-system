### Recommender CLI

Use to train:

```shell
python recommender.py train \             
--dataset data/combined_dataset.csv \       
--model rfq_model.pkl
```

Use to predict:

```shell
python recommender.py predict \           
  --suppliers data/suppliers_data_unique.csv \
  --model rfq_model.pkl \
  --rfq_title "Request: Shirt" \
  --rfq_description "T-Shirt" \
  --rfq_location "Moscow" \
  --rfq_product_type "Car" \
  --top_n 5
```

### API

Use to run: `uvicorn api:app --host 0.0.0.0 --port 8000`

To access API Docs: `http://localhost:8000/docs`