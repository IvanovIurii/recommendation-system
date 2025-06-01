package com.example.rfq.domain

import java.util.UUID

// todo: should DTOs go to domain?
// Are DTOs domain or not?

data class ResponseRfqDto(
    val rfqId: UUID,
    val title: String,
    val description: String,
    val deliveryLocation: String,
)