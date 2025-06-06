package com.example.rfq.api.dto

import java.util.UUID

data class AddSuppliersRequest(
    val suppliers: List<UUID>,
)
