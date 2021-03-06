package ru.nds.planfix.scan.data

import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

data class SidResponse(
    val sid: String
) {
    companion object {
        fun createFromRawResponse(response: Response<ResponseBody>): SidResponse {
            val rawResponse = response.body()?.byteStream()?.readBytes()?.decodeToString() ?: ""
            val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(
                ByteArrayInputStream(rawResponse.toByteArray(Charset.defaultCharset())),
                null
            )
            return parser.parseSidResponse()
        }
    }
}

fun XmlPullParser.parseSidResponse(): SidResponse {
    var sid = ""
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.TEXT -> {
                sid = text
            }
        }
        next()
    }
    return SidResponse(sid)
}
/*
<?xml version="1.0" encoding="UTF-8"?>
<response status="ok">
    <records>
        <record>
            <parentKey>0</parentKey>
            <isGroup>0</isGroup>
            <key>2</key>
            <customData>
                <customValue>
                    <field>
                        <id>36722</id>
                    </field>
                    <value>Внутренняя отделка</value>
                    <text>Внутренняя отделка</text>
                </customValue>
                <customValue>
                    <field>
                        <id>36770</id>
                    </field>
                    <value>0</value>
                    <text>0</text>
                </customValue>
            </customData>
        </record>
        <record>
            <parentKey>0</parentKey>
            <isGroup>0</isGroup>
            <key>1</key>
            <customData>
                <customValue>
                    <field>
                        <id>36722</id>
                    </field>
                    <value>Заливка ленточного фундамента</value>
                    <text>Заливка ленточного фундамента</text>
                </customValue>
                <customValue>
                    <field>
                        <id>36770</id>
                    </field>
                    <value>1</value>
                    <text>1</text>
                </customValue>
            </customData>
        </record>
        <record>
            <parentKey>0</parentKey>
            <isGroup>0</isGroup>
            <key>3</key>
            <customData>
                <customValue>
                    <field>
                        <id>36722</id>
                    </field>
                    <value>Монтаж кровли</value>
                    <text>Монтаж кровли</text>
                </customValue>
                <customValue>
                    <field>
                        <id>36770</id>
                    </field>
                    <value>3</value>
                    <text>3</text>
                </customValue>
            </customData>
        </record>
    </records>
</response>
 */