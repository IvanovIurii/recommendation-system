package com.example.rfq.domain

import com.example.rfq.infrastructure.persistence.model.RfqCoreEntity

interface RequestsService {
    fun createRfq(rfq: Rfq)
    // todo: this is wrong, we should return domain object from here, not an entity
    // entity is an internal implementation
    fun listAll(): List<RfqCoreEntity>
}