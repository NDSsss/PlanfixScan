package ru.nds.planfix.models

data class TasksSettingsQr(
    val account: String,
    val apiKey: String,
    val token: String,
    val robotName: String,
    val generalTaskNumber: String,
    val analiticId: String,
    val analiticFieldId: String,
)