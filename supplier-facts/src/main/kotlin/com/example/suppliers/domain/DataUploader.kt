package com.example.suppliers.domain

import org.springframework.web.multipart.MultipartFile

interface DataUploader {
    fun uploadData(file: MultipartFile)
}