package com.example.suppliers.api

import com.example.suppliers.application.service.SupplierProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/internal_api/")
class MainController(
    private val supplierProductService: SupplierProductService,
) {
    @GetMapping("/suppliers/{supplierId}")
    fun getSupplierById(
        @PathVariable supplierId: UUID,
    ): ResponseEntity<SupplierDto> {
        val supplier = supplierProductService.getSupplier(supplierId)
            ?: throw RuntimeException("Supplier not found")

        return ResponseEntity.ok(
            SupplierDto(
                supplierId = supplier.supplierId,
                supplierName = supplier.supplierName,
            )
        )
    }

    @GetMapping("/suppliers")
    fun getSupplierByName(
        @RequestParam name: String,
    ): ResponseEntity<SupplierDto> {
        val supplier = supplierProductService.getSupplier(name)
            ?: throw RuntimeException("Supplier with name $name not found")

        return ResponseEntity.ok(
            SupplierDto(
                supplierId = supplier.supplierId,
                supplierName = supplier.supplierName,
            )
        )
    }

    // todo: it should be /suppliers/products
    @GetMapping("/products")
    fun listProducts(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "25") size: Int,
    ): ResponseEntity<Page<SupplierProductDto>> {
        val pageable = PageRequest.of(page, size)
        val result =
            supplierProductService.getAll(pageable).map { page ->
                SupplierProductDto(
                    supplierId = page.supplierId!!,
                    supplierName = page.supplierName,
                    deliveryArea = page.deliveryArea,
                    productName = page.productName,
                    productDescription = page.productDescription,
                    productType = page.productType,
                )
            }

        return ResponseEntity.ok(result)
    }
}

data class SupplierProductDto(
    val supplierId: UUID,
    val supplierName: String,
    val deliveryArea: List<String>,
    val productName: String,
    val productDescription: String,
    val productType: String,
)

data class SupplierDto(
    val supplierId: UUID,
    val supplierName: String,
)
