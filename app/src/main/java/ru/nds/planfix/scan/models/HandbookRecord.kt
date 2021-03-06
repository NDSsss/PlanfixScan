package ru.nds.planfix.scan.models

import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

data class HandbookRecord(
    var parentKey: String? = null,
    var isGroup: String? = null,
    var key: String? = null,
    var customData: List<CustomValue> = listOf(),
)

data class CustomValue(
    var field: Field? = null,
    var value: String? = null,
    var text: String? = null,
)

data class Field(
    var id: String? = null
)

fun Response<ResponseBody>.parseHandbookRecords(): List<HandbookRecord> {
    val rawResponse = this.body()?.byteStream()?.readBytes()?.decodeToString() ?: ""
    val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
    parser.setInput(
        ByteArrayInputStream(rawResponse.toByteArray(Charset.defaultCharset())),
        null
    )
    return parser.parseHandbookRecords()
}

fun XmlPullParser.parseHandbookRecords(): List<HandbookRecord> {

    var lastTag = ""

    var allRecords = mutableListOf<HandbookRecord>()
    var currentRecord = HandbookRecord()
    var customValues = mutableListOf<CustomValue>()
    var currentCustomValue = CustomValue()
    var currentField = Field()

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                lastTag = name
                when (name) {
                    "records" -> allRecords = mutableListOf()
                    "record" -> currentRecord = HandbookRecord()
                    "customData" -> customValues = mutableListOf()
                    "customValue" -> currentCustomValue = CustomValue()
                    "field" -> currentField = Field()
                }
            }
            XmlPullParser.TEXT -> {
                when (lastTag) {
                    "id" -> currentField = Field(text)
                    "value" -> currentCustomValue.value = text
                    "text" -> currentCustomValue.text = text
                }
            }
            XmlPullParser.END_TAG -> {
                when (name) {
                    "field" -> currentCustomValue.field = currentField
                    "customValue" -> customValues.add(currentCustomValue)
                    "customData" -> currentRecord.customData = customValues
                    "record" -> allRecords.add(currentRecord)
                }
            }
        }
        next()
    }


    return allRecords
}