import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korim.text.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

lateinit var loadFont : Deferred<Unit>
lateinit var loadImg : Deferred<Unit>
lateinit var font:BitmapFont
lateinit var restartImg:Bitmap
lateinit var undoImg:Bitmap

var cellSize :Double = 0.0
var fieldSize :Double = 0.0
var leftIndent :Double = 0.0
var topIndent :Double = 0.0

val blocks = mutableMapOf<Int, Block>()

var freeId = 0

suspend fun main() = Korge(width = 480, height = 640, title = "The2048", bgcolor = RGBA(253, 247, 240)) {


    GlobalScope.launch {
        loadFont = async{ font = resourcesVfs["bmdh.fnt"].readBitmapFont() }
        loadImg = async {
            restartImg = resourcesVfs["restart.png"].readBitmap()
            undoImg = resourcesVfs["undo.png"].readBitmap()
        }
    }


    cellSize = views.virtualWidth/5.0
    fieldSize = 50 + 4 * cellSize
    leftIndent = (views.virtualWidth - fieldSize)/2
    topIndent = 150.0

    val bgField = roundRect(fieldSize, fieldSize, 5.0, fill = Colors["#b9aea0"]) {
        position(leftIndent, topIndent)
        addTo(this@Korge)
    }

    graphics {
        it.position(leftIndent, topIndent)
        fill(Colors["cec0b2"]){
            for (i in 0..3){
                for (j in 0..3){
                    roundRect(10+(10+cellSize)*i, 10+(10+cellSize)*j, cellSize, cellSize, 5.0)
                }
            }
        }
    }

    val bgLogo = roundRect(cellSize, cellSize, 5.0, fill = Colors["edc403"]) {
        position(leftIndent, 30.0)
        addTo(this@Korge)
    }

    val bgBest = roundRect(cellSize*1.5, cellSize*0.8, 5.0, fill = Colors["bbae9e"]){
        alignRightToRightOf(bgField)
        alignTopToTopOf(bgLogo)
    }

    val bgScore = roundRect(cellSize*1.5, cellSize*0.8, 5.0, fill = Colors["bbae9e"]){
        alignRightToLeftOf(bgBest, 24)
        alignTopToTopOf(bgLogo)
    }

    loadImg.await()
    val btnSize = cellSize * 0.3
    val restartBlock = container {
        val background = roundRect(btnSize, btnSize, 5.0, fill = RGBA(185, 174, 160))
        image(restartImg) {
            size(btnSize * 0.8, btnSize * 0.8)
            centerOn(background)
        }
        alignTopToBottomOf(bgBest, 5)
        alignRightToRightOf(bgField)
    }
    val undoBlock = container {
        val background = roundRect(btnSize, btnSize, 5.0, fill = RGBA(185, 174, 160))
        image(undoImg) {
            size(btnSize * 0.6, btnSize * 0.6)
            centerOn(background)
        }
        alignTopToTopOf(restartBlock)
        alignRightToLeftOf(restartBlock, 5.0)
    }

    loadFont.await()
    text("2048", cellSize*0.4, Colors.WHITE, font){
        centerXOn(bgLogo)
        alignBottomToBottomOf(bgLogo, 20)
    }
    text("전설의", cellSize*0.25, Colors.WHITE, font){
        centerXOn(bgLogo)
        alignTopToTopOf(bgLogo, 15)
    }

    text("최고점수", cellSize * 0.25, RGBA(239, 226, 210), font) {
        centerXOn(bgBest)
        alignTopToTopOf(bgBest, 5.0)
    }

    text("0", cellSize * 0.4, Colors.WHITE, font) {
        setTextBounds(Rectangle(10.0, 0.0, bgBest.width, cellSize - 24.0))
        alignment = TextAlignment.MIDDLE_CENTER
        alignTopToTopOf(bgBest, 12.0)
        centerXOn(bgBest)
    }

    text("점수", cellSize * 0.25, RGBA(239, 226, 210), font) {
        centerXOn(bgScore)
        alignTopToTopOf(bgScore, 5.0)
    }

    text("0", cellSize * 0.4, Colors.WHITE, font) {
        setTextBounds(Rectangle(10.0, 0.0, bgScore.width, cellSize - 24.0))
        alignment = TextAlignment.MIDDLE_CENTER
        centerXOn(bgScore)
        alignTopToTopOf(bgScore, 12.0)
    }

}

fun Container.block(number: Number) = Block(number).addTo(this)

fun columnX(number: Int) = leftIndent + 10 + (cellSize + 10) * number
fun rowY(number: Int) = topIndent + 10 + (cellSize + 10) * number
