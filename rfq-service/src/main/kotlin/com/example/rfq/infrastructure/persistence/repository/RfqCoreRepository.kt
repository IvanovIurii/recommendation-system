package com.example.rfq.infrastructure.persistence.repository

import com.example.rfq.infrastructure.persistence.model.RfqCoreEntity
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Repository

@Repository
class RfqCoreRepository(
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) {
    // todo: cover the error
    //  org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "rfq_core_unique"
    fun save(entity: RfqCoreEntity) {
        jdbcAggregateTemplate.insert(entity)
    }

    fun findAll(): List<RfqCoreEntity> {
        return jdbcAggregateTemplate.findAll(RfqCoreEntity::class.java)
    }
}