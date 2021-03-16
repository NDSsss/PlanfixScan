package ru.nds.planfix.network

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object PlanFixRequestTemplates {

    const val PLANFIX_METHOD_ACTION_ADD = "action.add"
    const val PLANFIX_METHOD_ACTION_GET_LIST = "action.getList"
    const val PLANFIX_METHOD_ANALITIC_GET_DATA = "analitic.getData"

    const val BARCODE_PARSE_URL =
        "https://barcode-list.ru/barcode/RU/%D0%9F%D0%BE%D0%B8%D1%81%D0%BA.htm"
    const val PLANFIX_API_URL = "https://api.planfix.ru/xml/"


    const val XML_REQUEST_TEMPLATE =
        "<request method=\u0022action.add\u0022><account>%1\$s</account><sid>%2\$s</sid><action><description>Запись из Приложения от %3\$s</description><task><id>%4\$s</id></task><analitics><analitic><id>%5\$s</id><analiticData><itemData><fieldId>%6\$s</fieldId><value>%7\$s</value></itemData></analiticData></analitic></analitics></action></request>"
    const val XML_SID_GENERATE =
        "<request method=\u0022auth.login\u0022><account>%1\$s</account><login>%2\$s</login><password>%3\$s</password></request>"
    const val XML_GET_STAGES =
        "<request method=\"handbook.getRecords\"><account>radix</account><sid>d1790e602aa175313bc1da03009abf3b</sid><handbook><id>13108</id></handbook></request>"
    const val XML_SEND_STAGE_TEMPLATE =
        "<request method=\"action.add\"><account>%1\$s</account><sid>%2\$s</sid><action><description>Запись из Приложения %3\$s</description><task><id>%4\$s</id></task><analitics><analitic><id>%5\$s</id><analiticData><itemData><fieldId>%6\$s</fieldId><value>%7\$s</value></itemData><itemData><fieldId>%8\$s</fieldId><value>%9\$s</value></itemData><itemData><fieldId>%10\$s</fieldId><value>%11\$s</value></itemData></analiticData></analitic></analitics></action></request>"


    fun createGetTasksAndStatusesKeysRequest(
        method: String = PLANFIX_METHOD_ACTION_GET_LIST,
        account: String,
        taskNumber: String
    ): RequestBody {
        return """<request method="$method"><account>$account</account><task><general>$taskNumber</general></task></request>"""
            .toRequestBody("text/plain".toMediaType())
    }

    fun createStatuscreateGetTasksAndStatusesDataRequest(
        method: String = PLANFIX_METHOD_ANALITIC_GET_DATA,
        account: String,
        keys: List<String>
    ): RequestBody {
        val keysFormatted = keys.joinToString(
            prefix = "<key>",
            postfix = "</key>",
            separator = "</key><key>"
        )
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} keysFormatted: $keysFormatted"
        );
        return """<request method="$method"><account>$account</account><analiticKeys>$keysFormatted</analiticKeys></request>"""
            .toRequestBody("text/plain".toMediaType())
    }

    fun createSetStatusRequest(
        method: String = PLANFIX_METHOD_ACTION_ADD,
        account: String,
        description: String,
        taskId: String,
        analiticId: String,
        analiticFieldId: String,
        statusId: String
    ): RequestBody {
        return """<request method="$method">
    <account>$account</account>
    <action>
        <description>$description</description>
        <task>
            <id>$taskId</id>
        </task>
        <analitics>
            <analitic>
                <id>$analiticId</id>
                <analiticData>
                    <itemData>
                        <fieldId>$analiticFieldId</fieldId>
                        <value>$statusId</value>
                    </itemData>
                </analiticData>
            </analitic>
        </analitics>
    </action>
</request>
"""
            .toRequestBody("text/plain".toMediaType())
    }

}