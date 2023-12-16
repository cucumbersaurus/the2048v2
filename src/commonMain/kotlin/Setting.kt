import com.soywiz.kds.*
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.serialization.json.*
import kotlinx.coroutines.*
import kotlin.math.*

class SettingScreen: Container() {


    private val border:RoundRect = st.roundRect(404.0*uiScale, 604.0*uiScale, 6.0*uiScale, fill = Colors["#282c34"]) {
        position((st.views.virtualWidth-404*uiScale)/2.0, (st.views.virtualHeight-604*uiScale)/2.0)

        zIndex += 1.0
    }
    private val winBackground = st.roundRect(400.0*uiScale, 600.0*uiScale, 5.0*uiScale, fill = Colors["#f0e4da"]) {
        position((st.views.virtualWidth-400*uiScale)/2.0, (st.views.virtualHeight-600*uiScale)/2.0)

        zIndex += 2.0
    }

    val closeButton = winBackground.circle {
        position(380.0*uiScale, 4.0*uiScale)
        radius = 8.0*uiScale
        color = Colors["#df6263"]
        zIndex += 3.0

        onOver { color =  Colors["#c76667"]}
        onOut { color = Colors["#ff5356"] }
        onDown { color = Colors["#c76667"] }
        onUp { color = Colors["#ff5356"] }
        onClick { closeSetting() }
    }

    private val title = winBackground.text("설정", 40.0*uiScale, Colors["000000"], font){
        centerXOn(winBackground)
        alignTopToTopOf(winBackground, 20.0*uiScale)
    }

    private val moveAnimationScaleSliderName = winBackground.text("블럭 움직임 속도", 20.0*uiScale, Colors["000000"], font){
        centerXOn(winBackground)
        alignTopToTopOf(title, 100.0*uiScale)
    }

    private val moveAnimationScaleSlider = winBackground.uiSlider(value = if(moveAnimationScale==0.0) 0.0 else ln(moveAnimationScale)+1, min = 0.0, max = 10.0, step = 0.01, ) {
        size(300*uiScale, 30*uiScale)
        centerXOn(winBackground)
        alignTopToTopOf(moveAnimationScaleSliderName, 50*uiScale)
        decimalPlaces = 2
        changed {
            moveAnimationScale = if(it==0.0) 0.0 else exp(it-1)
        }
    }

    private val sizeAnimationScaleSliderName = winBackground.text("블럭 크기 속도", 20.0*uiScale, Colors["000000"], font){
        centerXOn(winBackground)
        alignTopToTopOf(moveAnimationScaleSlider, 100.0*uiScale)
    }

    private val sizeAnimationScaleSlider = winBackground.uiSlider(value = if(scaleAnimationScale==0.0) 0.0 else ln(scaleAnimationScale)+1, min = 0.0, max = 10.0, step = 0.01) {
        size(300*uiScale, 30*uiScale)
        centerXOn(winBackground)
        alignTopToTopOf(sizeAnimationScaleSliderName, 50*uiScale)
        decimalPlaces = 2
        changed {
            scaleAnimationScale = if(it==0.0) 0.0 else exp(it-1)
        }
    }

    private fun closeSetting(){
        border.removeFromParent()
        winBackground.removeFromParent()
        anim.save()

        isOnSettingScreen = false
    }
}

class AnimationScale{

    private var anim =  LinkedHashMap<String, String>()

    suspend fun read(){
        var jsonString = ""
        lateinit var loadFile: Deferred<Unit>
        try {
            loadFile = async(currentCoroutineContext()) { jsonString = animationFile.readString() }
        }
        catch(e: Exception) {
            loadFile = async(currentCoroutineContext()) {
                animationFile.writeString(
                    "{\n" +
                        "  \"move\": \"1.0\",\n" +
                        "  \"scale\": \"1.0\"\n" +
                        "}\n")}
        }
        loadFile.await()
        anim = try{ jsonString.fromJson() ?: LinkedHashMap() }
        catch (e: Exception){ LinkedHashMap() }

        moveAnimationScale = anim["move"]?.toDoubleOrNull()?:1.0
        scaleAnimationScale = anim["scale"]?.toDoubleOrNull()?:1.0
    }

    fun save(){
        anim["move"] = moveAnimationScale.toString()
        anim["scale"] = scaleAnimationScale.toString()
        val jsonString = anim.toJson(pretty = true)
        async(currentCoroutineScope){ animationFile.writeString(jsonString) }
    }

    private fun String.fromJson(): LinkedHashMap<String, String>? = Json.parse(this).fastCastTo()
    private fun Map<*, *>.toJson(pretty: Boolean = false): String = Json.stringify(this, pretty)

    companion object {
        val animationFile = resourcesVfs["animation.json"]

    }
}
