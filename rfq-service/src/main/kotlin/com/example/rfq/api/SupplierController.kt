package com.example.rfq.api

import com.example.rfq.api.dto.AddSuppliersRequest
import com.example.rfq.api.dto.MatchedSupplierItemResponse
import com.example.rfq.api.dto.toCommand
import com.example.rfq.api.dto.toResponse
import com.example.rfq.application.service.MatchingCreationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/internal_api/")
class SupplierController(
    private val matchingCreationService: MatchingCreationService,
) {
    @PostMapping("/rfqs/{rfqId}/suppliers")
    fun addSuppliers(
        @RequestBody suppliers: AddSuppliersRequest,
        @PathVariable rfqId: UUID,
    ): ResponseEntity<String> {
        val command = suppliers.toCommand(rfqId)
        matchingCreationService.addSuppliers(command)
        return ResponseEntity.ok("Suppliers have been added to RFQ")
    }

    @GetMapping("/rfqs/{rfqId}/suppliers")
    fun getMatches(
        @PathVariable rfqId: UUID,
    ): ResponseEntity<List<MatchedSupplierItemResponse>> {
        return ResponseEntity.ok(
            matchingCreationService.getSuppliers(rfqId).map { item -> item.toResponse() },
        )
    }
}
