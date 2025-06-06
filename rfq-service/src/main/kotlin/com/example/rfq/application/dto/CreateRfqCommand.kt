package com.example.rfq.application.dto

data class CreateRfqCommand(
    val title: String,
    val description: String,
    val deliveryLocation: String,
    val productType: String,
)
