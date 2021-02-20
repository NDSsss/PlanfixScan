package ru.nds.planfix.scan.data

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
        "<request method=\"action.add\"><account>radix</account><sid>87c42ae316cdf1c2bf7205192143a07f</sid><action><description>Запись из Приложения</description><task><id>5204432</id></task><analitics><analitic><id>10752</id><analiticData><itemData><fieldId>45846</fieldId><value>1437056</value></itemData><itemData><fieldId>45146</fieldId><value>2</value></itemData><itemData><fieldId>45148</fieldId><value>01-02-2021</value></itemData></analiticData></analitic></analitics></action></request>"

}