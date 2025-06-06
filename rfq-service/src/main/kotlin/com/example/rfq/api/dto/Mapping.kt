package com.example.rfq.api.dto

import com.example.rfq.application.dto.AddSuppliersCommand
import com.example.rfq.application.dto.CreateRfqCommand
import com.example.rfq.application.dto.MatchedSupplierItem
import com.example.rfq.application.dto.RfqItem
import java.util.UUID

// conversion of api request to application use-case command

fun CreateRfqRequest.toCommand(): CreateRfqCommand {
    return CreateRfqCommand(
        title = title,
        description = description,
        deliveryLocation = deliveryLocation,
        productType = productType,
    )
}

fun AddSuppliersRequest.toCommand(rfqId: UUID): AddSuppliersCommand {
    return AddSuppliersCommand(
        rfqId = rfqId,
        suppliers = suppliers,
    )
}

// conversion of application use-case used domain to api response

fun RfqItem.toResponse(): RfqItemResponse {
    return RfqItemResponse(
        rfqId = rfqId,
        title = title,
        description = description,
        deliveryLocation = deliveryLocation,
        createdAt = createdAt,
    )
}

fun MatchedSupplierItem.toResponse(): MatchedSupplierItemResponse {
    return MatchedSupplierItemResponse(
        id = id,
        name = name,
        created = created,
    )
}
