package com.example.suppliers.infrastructure.persistence.repository

import com.example.suppliers.infrastructure.persistence.model.ProductEntity
import com.example.suppliers.infrastructure.persistence.model.SupplierEntity
import com.example.suppliers.infrastructure.persistence.model.SupplierProductAggregate
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query.query
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class SupplierProductRepository(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) {
    @Transactional
    fun saveAll(aggregates: List<SupplierProductAggregate>) {
        // not ideal, but keep it for now
        val supplierMap = mutableMapOf<String, SupplierEntity>()

        aggregates.forEach { aggregate ->
            supplierMap.getOrPut(aggregate.supplierName) {
                SupplierEntity(
                    supplierId = UUID.randomUUID(),
                    supplierName = aggregate.supplierName,
                    deliveryArea = aggregate.deliveryArea,
                    createdAt = Instant.now(),
                )
            }
        }

        val productList =
            aggregates.map { aggregate ->
                ProductEntity(
                    productId = UUID.randomUUID(),
                    productName = aggregate.productName,
                    productDescription = aggregate.productDescription,
                    productType = aggregate.productType,
                    supplierId = supplierMap[aggregate.supplierName]!!.supplierId,
                    createdAt = Instant.now(),
                )
            }

        jdbcAggregateTemplate.insertAll(supplierMap.values)
        jdbcAggregateTemplate.insertAll(productList)
    }

    // todo: should not really return entity, but domain object
    fun findById(supplierId: UUID): SupplierEntity? {
        return jdbcAggregateTemplate.findById(supplierId, SupplierEntity::class.java)
    }

    fun findByName(name: String): SupplierEntity? {
        val query = query(Criteria.where("supplier_name").`is`(name))
        return jdbcAggregateTemplate.findOne(query, SupplierEntity::class.java).getOrNull()
    }

    fun findAll(pageable: Pageable): Page<SupplierProductAggregate> {
        val content = fetchProductSuppliersPage(pageable)
        val total = countProductSuppliers()
        return PageImpl(content, pageable, total)
    }

    private fun fetchProductSuppliersPage(pageable: Pageable): List<SupplierProductAggregate> {
        val size = pageable.pageSize
        val offset = pageable.pageNumber * size

        val sql =
            """
            SELECT
              s.supplier_id,
              p.product_id,
              s.supplier_name,
              s.delivery_area,
              p.product_name,
              p.product_description,
              p.product_type 
            $BASE_QUERY 
            ORDER BY s.supplier_name
            LIMIT :limit
            OFFSET :offset
            """.trimIndent()

        val params =
            MapSqlParameterSource()
                .addValue("limit", size)
                .addValue("offset", offset)

        return namedParameterJdbcTemplate.query(
            sql,
            params,
        ) { rs, _ ->
            SupplierProductAggregate(
                supplierId = UUID.fromString(rs.getString("supplier_id")),
                productId = UUID.fromString(rs.getString("product_id")),
                supplierName = rs.getString("supplier_name"),
                // fixme
                deliveryArea =
                rs.getString("delivery_area")
                    .replace("{", "")
                    .replace("}", "")
                    .split(","),
                productName = rs.getString("product_name"),
                productDescription = rs.getString("product_description"),
                productType = rs.getString("product_type"),
            )
        }
    }

    private fun countProductSuppliers(): Long {
        val sql = "SELECT COUNT(*) $BASE_QUERY"

        return try {
            namedParameterJdbcTemplate.queryForObject(sql, emptyMap<String, Any>(), Long::class.java)
                ?: 0L
        } catch (ex: EmptyResultDataAccessException) {
            0L
        }
    }

    private companion object {
        private val BASE_QUERY =
            """
            FROM supplier s
            JOIN supplier_product p
            ON s.supplier_id = p.supplier_id 
            """.trimIndent()
    }
}
