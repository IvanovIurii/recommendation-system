# Supplier-Facts Service

## Database Schema

id uuid \
product_name text \
product_description text \
product_type text \
supplier_name text \
delivery_area array[text] \

### Assumptions and out of scopes

1. No Spring Security (needs service-to-service)
2. No cloud deployments (runs only locally)
3. DB is not normalized
4. The data (suppliers and products) is artificial,
   was created by LLM, meaning it is not clear what result will be
   acquired at the end
5. There is no unit/integrations tests as well (DDD/debug-driven-development has been used)