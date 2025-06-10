package com.example.suppliers.api

import com.example.suppliers.application.service.CsvUploaderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/internal_api/")
class DataManagementController(
    private val csvUploader: CsvUploaderService,
) {
    @PostMapping("/data/upload", consumes = ["multipart/form-data"])
    fun uploadData(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        csvUploader.uploadData(file)

        return ResponseEntity.ok("OK")
    }
}