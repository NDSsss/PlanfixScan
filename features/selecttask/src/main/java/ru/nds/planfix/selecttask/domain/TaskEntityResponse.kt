package ru.nds.planfix.selecttask.domain

import android.util.Log
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

data class TaskEntityResponse(
    val tasks: List<TaskEntity>
)


fun Response<ResponseBody>.toTasksEntityResponse(robotId: String): TaskEntityResponse {
    val rawResponse = this.body()?.byteStream()?.readBytes()?.decodeToString() ?: ""
    val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser()
    parser.setInput(
        ByteArrayInputStream(rawResponse.toByteArray(Charset.defaultCharset())),
        null
    )
    return parser.parsetasksEntityResponse(robotId)
}

fun XmlPullParser.parsetasksEntityResponse(robotId: String): TaskEntityResponse {

    var lastTag = ""

    val analytics = mutableListOf<List<AnaliticItemData>>()

    var items = mutableListOf<AnaliticItemData>()

    var lastId = ""
    var lastName = ""
    var lastValue = ""
    var lastValueId = ""

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                lastTag = name
            }
            XmlPullParser.TEXT -> {
                when (lastTag) {
                    "id" -> lastId = text
                    "name" -> lastName = text
                    "value" -> lastValue = text
                    "valueId" -> lastValueId = text
                }
            }
            XmlPullParser.END_TAG -> {
                when (name) {
                    "itemData" -> items.add(
                        AnaliticItemData(
                            id = lastId,
                            name = lastName,
                            value = lastValue,
                            valueId = lastValueId
                        )
                    )
                    "analiticData" -> {
                        analytics.add(items)
                        items = mutableListOf()
                    }
                }
            }
        }
        next()
    }
    Log.d(
        "APP_TAG",
        "${this::class.java.simpleName} ${this::class.java.hashCode()} analytics: $analytics"
    );
    // 1 and 4

    val taskActionPairs: List<Pair<AnaliticItemDataTask, AnaliticItemDataAction>> =
        analytics.filter { it[2].valueId == robotId }.map { taskActionAnalitics ->
            val taskDataRaw = taskActionAnalitics.get(0)
            val taskData = AnaliticItemDataTask(
                id = taskDataRaw.id,
                name = taskDataRaw.name,
                value = taskDataRaw.value,
                valueId = taskDataRaw.valueId,
            )
            val actionRaw = taskActionAnalitics.get(3)
            val action = AnaliticItemDataAction(
                id = actionRaw.id,
                name = actionRaw.name,
                value = actionRaw.value,
                valueId = actionRaw.valueId,
            )
            taskData to action
        }

    val tasksMap: MutableMap<AnaliticItemDataTask, MutableList<AnaliticItemDataAction>> =
        hashMapOf()
    taskActionPairs.forEach { (task, action) ->
        if (tasksMap.containsKey(task)) {
            tasksMap[task]?.add(action)
        } else {
            tasksMap[task] = mutableListOf(action)
        }
    }

    Log.d(
        "APP_TAG",
        "${this::class.java.simpleName} ${this::class.java.hashCode()} tasksMap: $tasksMap"
    );

    return TaskEntityResponse(tasksMap.map { (taskRaw, actionsRaw) ->
        TaskEntity(
            name = taskRaw.value,
            taskId = taskRaw.valueId,
            statuses = actionsRaw.map { actionRawItem ->
                TaskStatusEntity(
                    name = actionRawItem.value,
                    statusId = actionRawItem.valueId
                )
            }
        )
    })
}

private data class AnaliticItemData(
    val id: String,
    val name: String,
    val value: String,
    val valueId: String,
)

private data class AnaliticItemDataTask(
    val id: String,
    val name: String,
    val value: String,
    val valueId: String,
)

private data class AnaliticItemDataAction(
    val id: String,
    val name: String,
    val value: String,
    val valueId: String,
)