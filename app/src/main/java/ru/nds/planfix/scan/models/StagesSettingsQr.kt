package ru.nds.planfix.scan.models

data class StagesSettingsQr(
    val account: String,
    val userLogin: String,
    val userPassword: String,
    val apiKey: String,
    val token: String,
    val taskId: String,
    val contactId: String,
    val analiticId: String,
    val stageTypeFieldId: String,
    val contactIdFieldId: String,
    val dateFieldId: String,
)