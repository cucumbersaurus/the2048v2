import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.serialization.json.*
import kotlinx.coroutines.*
import kotlin.collections.set

class Ranking {

    private var ranking =  LinkedHashMap<String, Int>()
    var best = 0

    fun addRank(time: DateTimeTz, score: Int){
        val localTime = time.local

        ranking[localTime.format(ISO8601.DATE_CALENDAR_COMPLETE) + " " + localTime.time.format(ISO8601.TIME_LOCAL_COMPLETE)]=score
        save()
    }

    fun read(){
        val vfs = localVfs("ranking.json")
        var jsonString = ""
        try {
            runBlocking {
                jsonString = vfs.readString()
            }
        }
        catch(e: Exception) {
            runBlocking {
                vfs.writeString("")
            }
        }
        ranking = try{
            jsonString.fromJson() ?: LinkedHashMap()
        } catch (e: Exception){
            LinkedHashMap()
        }

        ranking.forEach { e ->
            if(e.value>best) best=e.value
        }
    }
    private fun save(){
        val jsonString = ranking.toJson(pretty = true)
        print(jsonString)

        val vfs = localVfs("ranking.json")

        launch(Dispatchers.Default){
            vfs.writeString(jsonString)
        }
    }

    fun toList(): List<Pair<String, Int>>{
        val list = ranking.toList()
        return list.sortedByDescending { it.second }
    }
    private fun String.fromJson(): LinkedHashMap<String, Int>? = Json.parse(this).fastCastTo()
    private fun Map<*, *>.toJson(pretty: Boolean = false): String = Json.stringify(this, pretty)
}
