package ru.nds.planfix.network

object PlanFixRequestTemplates {
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

}