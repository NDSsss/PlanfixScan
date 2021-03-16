package ru.nds.planfix.selecttask.domain

import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import ru.nds.planfix.models.CustomValue
import ru.nds.planfix.models.Field
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class TaskActionsListResponse(
    val actions: List<TaskAction>
)

class MutableTaskActionsListResponse(
    var actions: List<MutableTaskAction>? = listOf()
)

fun MutableTaskActionsListResponse.toImmutable(): TaskActionsListResponse {
    return TaskActionsListResponse(this.actions?.map { it.toImmutable() } ?: listOf())
}

data class TaskAction(
    val id: String,
    val description: String,
    val analitics: List<AnaliticAction>,
)

data class MutableTaskAction(
    var id: String? = "",
    var description: String? = "",
    var analitics: List<MutableAnaliticAction>? = listOf(),
)

fun MutableTaskAction.toImmutable(): TaskAction {
    return TaskAction(
        id = id ?: "",
        description = description ?: "",
        analitics = analitics?.map { it.toImmutable() } ?: listOf(),
    )
}

data class AnaliticAction(
    val id: String,
    val key: String,
    val name: String,
)

data class MutableAnaliticAction(
    var id: String? = "",
    var key: String? = "",
    var name: String? = "",
)

fun MutableAnaliticAction.toImmutable(): AnaliticAction {
    return AnaliticAction(
        id = id ?: "",
        key = key ?: "",
        name = name ?: "",
    )
}

fun Response<ResponseBody>.toTaskActionsResponse(): TaskActionsListResponse {
    val rawResponse = this.body()?.byteStream()?.readBytes()?.decodeToString() ?: ""
    val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
    parser.setInput(
        ByteArrayInputStream(rawResponse.toByteArray(Charset.defaultCharset())),
        null
    )
    return parser.parseTaskActionsResponse().toImmutable()
}

fun XmlPullParser.parseTaskActionsResponse(): MutableTaskActionsListResponse {

    var lastTag = ""

    var allActions = mutableListOf<MutableTaskAction>()
    var currentAction = MutableTaskAction()

    var allAnaliticActions = mutableListOf<MutableAnaliticAction>()
    var currentAnaliticAction = MutableAnaliticAction()

    var currentCustomValue = CustomValue()
    var currentField = Field()


    //TODO: complete parsing
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                lastTag = name
                when (name) {
                    "actions" -> allActions = mutableListOf()
                    "action" -> currentAction = MutableTaskAction()
                    "analitics" -> allAnaliticActions = mutableListOf()
                    "analitic" -> currentAnaliticAction = MutableAnaliticAction()
                }
            }
            XmlPullParser.TEXT -> {
                when (lastTag) {
                    "id" -> currentField = Field(text)
                    "value" -> currentCustomValue.value = text
                    "text" -> currentCustomValue.text = text
                }
            }
        }
        next()
    }
 return MutableTaskActionsListResponse()
}

fun Response<ResponseBody>.toTaskActionsKeys(): List<String> {
    val rawResponse = this.body()?.byteStream()?.readBytes()?.decodeToString() ?: ""
    val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
    parser.setInput(
        ByteArrayInputStream(rawResponse.toByteArray(Charset.defaultCharset())),
        null
    )
    return parser.parseTaskActionsKeysList()
}

fun XmlPullParser.parseTaskActionsKeysList(): List<String> {

    var lastTag = ""

    var lastKey = ""

    val keys= mutableListOf<String>()



    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                lastTag = name
            }
            XmlPullParser.TEXT -> {
                when (lastTag) {
                    "key" -> lastKey = text
                }
            }
            XmlPullParser.END_TAG -> {
                when (name) {
                    "analitic" -> keys.add(lastKey)
                }
            }
        }
        next()
    }

    return keys
}