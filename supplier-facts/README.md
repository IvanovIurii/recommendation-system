# Supplier-Facts Service

It is MVP built in Kotlin and Spring Boot.
It ingests suppliers and product data from CSV files and exposes REST endpoints to retrieve this data.

## Architecture Overview

### Components

- REST API: Defined under `/internal_api/*`, handles CSV upload and supplier/product queries
- DB: `PostgreSQL` (uses Flyway for migrations, running in a container)

### API Endpoints

Hosted locally at: http://localhost:8080/supplier-facts-service/internal_api/swagger-ui/index.html

1. `POST /data/upload` to upload LLM synthetically generated data
2. `GET /suppliers/{supplierId}` to fetch supplier by UUID

Response:

```json
{
  "supplierId": "UUID",
  "supplierName": "Example Supplier"
}
```

3. `GET /suppliers?name=SupplierName` to get supplier by name

Response:

```json
{
  "supplierId": "UUID",
  "supplierName": "Same Example Supplier"
}
```

4. `GET /products?page=0&size=25` to returns a paginated list of supplier-product mappings

Response:

```json
[
  {
    "supplierId": "UUID",
    "supplierName": "Supplier A",
    "deliveryArea": [
      "Zone1",
      "Zone2"
    ],
    "productName": "Widget",
    "productDescription": "A useful widget",
    "productType": "Hardware"
  },
  ...
]

```

## Design Choices & Constraints

1. Security is not included: MVP stage, only local dev, not production-ready
2. Cloud Deployment is not supported: MVP stage
3. Test Coverage: lack of unit/integration tests because of not having enough time
4. Data for the service is generated synthetically via LLM
5. None of CI/CD: not relevant at the current stage
6. Error handling is minimal as well

## Tech Stack

- Kotlin 2.0
- Spring Boot 3.4.3
- Spring Data JDBC
- PostgreSQL
- Flyway (for migrations)
- Swagger / OpenAPI 3
- Gradle (Kotlin DSL)
- Docker-compose (for postgres DB container)
