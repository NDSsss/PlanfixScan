package ru.nds.planfix.models

data class ProductSettingsQr(
    val account: String,
    val userLogin: String,
    val userPassword: String,
    val apiKey: String,
    val token: String,
    val taskId: String,
    val analiticId: String,
    val analiticFieldId: String,
)