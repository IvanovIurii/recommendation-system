package com.example.rfq.infrastructure.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@ConfigurationProperties(prefix = "app.api.supplier-facts-api")
data class SupplierFactsProperties(
    val url: String,
)

@ConfigurationProperties(prefix = "app.api.recommendation-service-api")
data class RecommendationServiceProperties(
    val url: String,
)

@Configuration
class ServiceClientConfiguration {
    @Bean
    fun supplierFactsClient(
        builder: RestClient.Builder,
        properties: SupplierFactsProperties,
    ): RestClient =
        builder.baseUrl(properties.url).build()

    @Bean
    fun recommendationServiceClient(
        builder: RestClient.Builder,
        properties: RecommendationServiceProperties,
    ): RestClient =
        builder.baseUrl(properties.url).build()
}