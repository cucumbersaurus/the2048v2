import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korgw.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korim.text.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.interpolation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlin.collections.set
import kotlin.random.*

lateinit var loadFont : Deferred<Unit>
lateinit var loadImg : Deferred<Unit>
var animator : Job = launch(Dispatchers.Main){}

lateinit var font:TtfFont
//lateinit var font2:TtfFont
lateinit var restartImg:Bitmap
//lateinit var undoImg:Bitmap

var cellSize :Double = 0.0
var fieldSize :Double = 0.0
var leftIndent :Double = 0.0
var topIndent :Double = 0.0

val blocks = mutableMapOf<Int, Block>()
var freeId = 0
var map = PositionMap()

var isAnimationRunning = false
var isGameOver = false
var isOnRankingView = false

val score = ObservableProperty(0)
val ranking = Ranking()
val best= ObservableProperty(0)

val scaleAnimationList = ArrayList<Int>()

suspend fun main() = Korge(
    width = 680, height = 900,
    virtualWidth = 480, virtualHeight  = 640,
    title = "The2048", bgcolor = RGBA(253, 247, 240),
    quality = GameWindow.Quality.AUTOMATIC) {

    loadFont = async { font = resourcesVfs["bmdh.ttf"].readTtfFont() }
    loadImg = async { restartImg = resourcesVfs["restart.png"].readBitmap() }

    ranking.read()
    print(ranking.best)
    print(ranking.toList())


    /////////////////////////////

    val sceneContainer = sceneContainer()
    sceneContainer.changeTo({ GameScene(views(), this) })

    //////////////////////////
}




fun Container.showRanking(views:Views){
    /*
    isOnRankingView = true
    roundRect(views.virtualWidthDouble, views.virtualHeightDouble, 5.0, fill = Colors["#BBBBBB77"]){
        this.globalX = 0.0
        this.globalY = 0.0
    }
    */
}
