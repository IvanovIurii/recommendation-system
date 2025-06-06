package com.example.rfq.infrastructure.persistence.repository

import com.example.rfq.infrastructure.persistence.model.MatchingCoreEntity
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MatchingCoreRepository(
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) {
    fun save(entity: MatchingCoreEntity) {
        jdbcAggregateTemplate.insert(entity)
    }

    fun findAll(rfqId: UUID): List<MatchingCoreEntity> {
        val query =
            Query.query(
                Criteria.where("rfq_id").`is`(rfqId),
            )
        return jdbcAggregateTemplate.findAll(query, MatchingCoreEntity::class.java)
    }
}
