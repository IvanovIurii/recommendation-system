package com.example.suppliers.infrastructure.service

import com.example.suppliers.domain.DataUploader
import com.example.suppliers.infrastructure.persistence.repository.SupplierProductRepository
import com.example.suppliers.infrastructure.persistence.model.SupplierProductEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.UUID

@Component
class CsvDataUploader(
    private val supplierProductRepository: SupplierProductRepository,
) : DataUploader {
    override fun uploadData(file: MultipartFile) {
        BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8))
            .use { reader ->

                val csvFormat = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build()

                CSVParser(
                    reader, csvFormat
                )
                    .use { parser ->
                        val extractLocations: (String) -> List<String> =
                            { locations: String ->
                                locations
                                    .split(",")
                                    .map { location -> location.trim() }
                            }

                        val entries = parser.map { record ->
                            SupplierProductEntity(
                                // todo: refactor this, UUID should be in the repository,
                                //  because it is an internal implementation, not a component level
                                id = UUID.randomUUID(),
                                productName = record.get("product_name"),
                                productDescription = record.get("product_description"),
                                productType = record.get("product_type"),
                                supplierName = record.get("supplier_name"),
                                deliveryArea = extractLocations(record.get("delivery_area"))
                            )
                        }

                        supplierProductRepository.saveAll(entries)
                    }
            }
    }
}