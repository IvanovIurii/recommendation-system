package com.example.rfq.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

// validation and JSON creation goes here on presentation layer

data class CreateRfqRequest(
    val title: String,
    val description: String,
    @JsonProperty("delivery_location")
    val deliveryLocation: String,
    @JsonProperty("product_type")
    val productType: String,
)
