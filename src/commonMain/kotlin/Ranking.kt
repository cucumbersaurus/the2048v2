import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import com.soywiz.korio.serialization.json.*
import kotlinx.coroutines.*
import kotlin.collections.set
import kotlin.coroutines.*

class Ranking {

    private var ranking =  LinkedHashMap<String, Int>()
    var best = 0

    fun addRank(time: DateTimeTz, score: Int, coroutineContext: CoroutineContext){
        val localTime = time.local

        ranking[localTime.format(ISO8601.DATE_CALENDAR_COMPLETE) + " " + localTime.time.format(ISO8601.TIME_LOCAL_COMPLETE)]=score
        async(coroutineContext) {save()}
    }

    suspend fun read(){
        var jsonString = ""
        lateinit var loadFile:Deferred<Unit>
        try {
            loadFile = async(currentCoroutineContext()) { jsonString = rankingFile.readString() }
        }
        catch(e: Exception) {
            loadFile = async(context = currentCoroutineContext()) { rankingFile.writeString("") }
        }
        loadFile.await()

        ranking = try{
            jsonString.fromJson() ?: LinkedHashMap()
        } catch (e: Exception){
            LinkedHashMap()
        }

        ranking.forEach { e ->
            if(e.value>best) best=e.value
        }
    }
    private suspend fun save(){
        val jsonString = ranking.toJson(pretty = true)
        print(jsonString)

        launch(currentCoroutineContext()){
            rankingFile.writeString(jsonString)
        }
    }

    fun toList(): List<Pair<String, Int>>{
        val list = ranking.toList()
        return list.sortedByDescending { it.second }
    }

    fun getTop8():List<Pair<String, Int>> {
        val list = toList()

        return if(list.size>=8) list.slice(0..7)
        else return list
    }

    private fun String.fromJson(): LinkedHashMap<String, Int>? = Json.parse(this).fastCastTo()
    private fun Map<*, *>.toJson(pretty: Boolean = false): String = Json.stringify(this, pretty)

    companion object {
        //val rankingFile = localVfs(SystemProperties["user.dir"] +"/ranking.json")
        val rankingFile = resourcesVfs["ranking.json"]

    }
}
