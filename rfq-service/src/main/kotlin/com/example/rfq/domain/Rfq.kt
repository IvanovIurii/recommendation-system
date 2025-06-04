package com.example.rfq.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class Rfq(
    val title: String,
    val description: String,
    @JsonProperty("delivery_location")
    val deliveryLocation: String,
    @JsonProperty("product_type")
    val productType: String,
)

// todo: crete DTO with Jackson
