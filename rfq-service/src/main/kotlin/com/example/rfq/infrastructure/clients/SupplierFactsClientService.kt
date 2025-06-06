package com.example.rfq.infrastructure.clients

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.UUID

// todo: maybe package name is a bit misleading

@Service
class SupplierFactsClientService(
    private val supplierFactsClient: RestClient,
) {
    fun getSupplierById(supplierId: UUID): SupplierDto? =
        try {
            supplierFactsClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/suppliers/$supplierId")
                        .build()
                }.retrieve()
                .body<SupplierDto>()!!
        } catch (e: Exception) {
            logger.error("Error getting supplier by id: $supplierId", e)
            null
        }

    private companion object {
        private val logger = LoggerFactory.getLogger(SupplierFactsClientService::class.java)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplierDto(
    val supplierId: UUID,
    val supplierName: String,
)