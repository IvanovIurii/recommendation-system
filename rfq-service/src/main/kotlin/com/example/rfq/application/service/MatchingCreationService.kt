package com.example.rfq.application.service

import com.example.rfq.application.dto.AddSuppliersCommand
import com.example.rfq.application.dto.MatchedSupplierItem
import com.example.rfq.domain.model.MatchingStatus
import com.example.rfq.infrastructure.clients.SupplierFactsClientService
import com.example.rfq.infrastructure.persistence.model.MatchingCoreEntity
import com.example.rfq.infrastructure.persistence.repository.MatchingCoreRepository
import com.example.rfq.infrastructure.persistence.repository.RfqCoreRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class MatchingCreationService(
    // repositories technically should be handled via interfaces implemented in the infrastructure layer
    private val matchingCoreRepository: MatchingCoreRepository,
    private val rfqCoreRepository: RfqCoreRepository,
    private val supplierFactsClientService: SupplierFactsClientService,
) {
    // todo: we have to check here that those suppliers were already added to this rfq
    fun addSuppliers(addSuppliersCommand: AddSuppliersCommand) {
        val rfq =
            rfqCoreRepository.findById(addSuppliersCommand.rfqId)
                ?: throw RuntimeException("RFQ has not been found by id: ${addSuppliersCommand.rfqId}")

        // todo: its better to return an objects with errors if duplicates to the client to handle it
        val existedSuppliers =
            matchingCoreRepository
                .findAll(rfq.rfqId)
                .map { entity -> entity.supplierId }

        val suppliers = addSuppliersCommand.suppliers.filterNot { supplier -> supplier in existedSuppliers }
        suppliers.forEach { supplierId ->
            val entity =
                MatchingCoreEntity(
                    matchingId = UUID.randomUUID(),
                    rfqId = rfq.rfqId,
                    status = MatchingStatus.CREATED.name,
                    created = Instant.now(),
                    supplierId = supplierId,
                )
            matchingCoreRepository.save(entity)
        }
    }

    // todo: should be called async
    fun getSuppliers(rfqId: UUID): List<MatchedSupplierItem> {
        return matchingCoreRepository.findAll(rfqId).map { entity ->
            val supplier = supplierFactsClientService.getSupplierById(entity.supplierId)

            MatchedSupplierItem(
                id = entity.matchingId,
                // fixme: just returns a list of errors to show on UI
                name = supplier?.supplierName ?: "ERROR: Supplier Not Found",
                created = entity.created,
            )
        }
    }
}
