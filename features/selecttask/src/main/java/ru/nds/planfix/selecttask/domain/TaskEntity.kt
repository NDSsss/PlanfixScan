package ru.nds.planfix.selecttask.domain

import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

data class TaskEntity(
    val name: String,
    val taskId: String,
    val statuses: List<TaskStatusEntity>
)

data class TaskStatusEntity(
    val name: String,
    val statusId: String
)