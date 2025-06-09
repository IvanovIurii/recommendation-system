package com.example.rfq.infrastructure.clients

import com.example.rfq.infrastructure.persistence.repository.RfqCoreRepository
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.UUID

@Service
class RecommendationServiceClientService(
    @Qualifier("recommendationServiceClient")
    private val recommendationServiceClient: RestClient,
    private val rfqCoreRepository: RfqCoreRepository,
    private val objectMapper: ObjectMapper,
) {
    // todo: refactor, this has to be in the application service
    fun getRecommendations(rfqId: UUID): List<RecommendedSupplierResponseDto>? {

        val rfqEntity = rfqCoreRepository.findById(rfqId)!!
        val request = RecommendSuppliersRequestDto(
            rfqTitle = rfqEntity.title,
            rfqDescription = rfqEntity.description,
            rfqProductType = rfqEntity.productType,
            rfqLocation = rfqEntity.deliveryLocation,
            topN = 5
        )

        return try {
            recommendationServiceClient
                .post()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/recommend")
                        .build()

                }
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    objectMapper.writeValueAsString(request)
                )
                .retrieve()
                .body<List<RecommendedSupplierResponseDto>>()!!
        } catch (e: Exception) {
            logger.error("Error getting recommendations", e)
            null
        }
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(RecommendationServiceClientService::class.java)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecommendedSupplierResponseDto(
    @JsonProperty("supplier_name")
    val supplierName: String,
    @JsonProperty("supplier_product_name")
    val supplierProductName: String,
    @JsonProperty("supplier_product_description")
    val supplierProductDescription: String,
    @JsonProperty("supplier_product_type")
    val supplierProductType: String,
    @JsonProperty("supplier_delivery_area")
    val supplierDeliveryArea: List<String>,
    val score: String,

    // not in recommendation service, added later from supplier-facts-service
    @JsonProperty("supplier_id")
    val supplierId: UUID? = null,
)

data class RecommendSuppliersRequestDto(
    @JsonProperty("rfq_title")
    val rfqTitle: String,
    @JsonProperty("rfq_description")
    val rfqDescription: String,
    @JsonProperty("rfq_product_type")
    val rfqProductType: String,
    @JsonProperty("rfq_location")
    val rfqLocation: String,
    @JsonProperty("top_n")
    val topN: Int,
)
