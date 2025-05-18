package com.example.suppliers.application.service

import com.example.suppliers.domain.DataUploader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CsvUploaderService(
    private val dataUploader: DataUploader,
) {
    fun uploadData(file: MultipartFile) {
        dataUploader.uploadData(file)
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(CsvUploaderService::class.java)
    }
}