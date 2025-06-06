package com.example.rfq.application.dto

import java.util.UUID

// separation of presentation layer and application layer is necessary to enforce validations

data class AddSuppliersCommand(
    val rfqId: UUID,
    val suppliers: List<UUID>,
) {
    init {
        // todo: check what exception is thrown and handle it in controller advice as 400
        require(suppliers.size <= 5) {
            "Only up to 5 suppliers are allowed"
        }
    }
}
