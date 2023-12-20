import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlin.collections.set
import kotlin.coroutines.*
import kotlin.random.*

var uiScale = 2.0

lateinit var sceneContainer:SceneContainer
lateinit var currentCoroutineScope: CoroutineContext
lateinit var st: Stage

lateinit var loadFont : Deferred<Unit>
lateinit var loadImg : Deferred<Unit>

var animator : Job = launch(Dispatchers.Main){}

lateinit var font:TtfFont
lateinit var restartImg:Bitmap
lateinit var leaderboardImg: Bitmap
lateinit var trophyImg: Bitmap
lateinit var settingImg: Bitmap

var cellSize :Double = 0.0
var fieldSize :Double = 0.0
var leftIndent :Double = 0.0
var topIndent :Double = 0.0

var map = PositionMap()
val blocks = mutableMapOf<Int, Block>()
var freeId = 0

var isAnimationRunning = false
var isOnRankingScreen = false
var isOnSettingScreen = false
var isGameOver = false
var isOnRandomAnimationSpeedMode = false

val score = ObservableProperty(0)
val best= ObservableProperty(0)
val ranking = Ranking()
val anim = AnimationScale()

val scaleAnimationList = ArrayList<Int>()


suspend fun main() = Korge(width = 720, height = 960, virtualWidth = (480*uiScale).toInt(), virtualHeight  = (640*uiScale).toInt(), title = "The2048", bgcolor = RGBA(253, 247, 240), icon = resourcesVfs["icon.png"].readBitmap()) {

    sceneContainer = sceneContainer()
    st = this
    currentCoroutineScope = coroutineContext

    loadResources()
    readFiles()
    score.observe {
        if (it > best.value) best.update(it)
    }

    cellSize = views.virtualWidth/5.0
    fieldSize = 50*uiScale + 4 * cellSize
    leftIndent = (views.virtualWidth - fieldSize)/2
    topIndent = 150.0*uiScale

    createScreen()
    generateBlock()
    registerInputs()
}

private suspend fun readFiles() {
    ranking.read()
    anim.read()
}

private fun Stage.loadResources() {
    loadFont = async { font = resourcesVfs["bmdh.ttf"].readTtfFont() }
    loadImg = async {
        restartImg = resourcesVfs["img/restart.png"].readBitmap()
        leaderboardImg = resourcesVfs["img/leaderboards.png"].readBitmap()
        trophyImg = resourcesVfs["img/trophy.png"].readBitmap()
        settingImg = resourcesVfs["img/setting.png"].readBitmap()
    }
}

private fun Stage.registerInputs() {
    keys {
        down {
            if (!isGameOver && !isOnRankingScreen && !isOnSettingScreen) {
                when (it.key) {
                    Key.LEFT -> moveBlocksTo(Direction.LEFT)
                    Key.RIGHT -> moveBlocksTo(Direction.RIGHT)
                    Key.UP -> moveBlocksTo(Direction.TOP)
                    Key.DOWN -> moveBlocksTo(Direction.BOTTOM)
                    else -> Unit
                }
            }
        }
    }
    onSwipe(80.0*uiScale) {
        if (!isGameOver && !isOnRankingScreen && !isOnSettingScreen) {
            when (it.direction) {
                SwipeDirection.LEFT -> moveBlocksTo(Direction.LEFT)
                SwipeDirection.RIGHT -> moveBlocksTo(Direction.RIGHT)
                SwipeDirection.TOP -> moveBlocksTo(Direction.TOP)
                SwipeDirection.BOTTOM -> moveBlocksTo(Direction.BOTTOM)
            }
        }
    }
}

fun Container.block(number: Number) = Block(number).addTo(this)

fun columnX(number: Int) = leftIndent + 10*uiScale + (cellSize + 10*uiScale) * number
fun rowY(number: Int) = topIndent + 10*uiScale + (cellSize + 10*uiScale) * number

fun Container.createNewBlockWithId(id: Int, number: Number, position: Position) {
    blocks[id] = block(number).position(columnX(position.x), rowY(position.y))
}

fun Container.createNewBlock(number: Number, position: Position): Int {
    val id = freeId++
    createNewBlockWithId(id, number, position)
    return id
}

fun Container.generateBlock():Int? {
    val position = map.getRandomFreePosition() ?: return null
    val number = if (Random.nextDouble() < 0.9) Number.ZERO else Number.ONE
    val newId = createNewBlock(number, position)
    map[position.x, position.y] = newId
    return newId
}

fun Stage.moveBlocksTo(direction: Direction) {
    if (isAnimationRunning) return
    if(animator.isActive) return

    animator.cancel()
    isAnimationRunning = true

    val moves = mutableListOf<Pair<Int, Position>>()
    val merges = mutableListOf<Triple<Int, Int, Position>>()
    val newMap = calculateNewMap(map.copy(), direction, moves, merges)

    if (map != newMap) {
        showAnimation(moves, merges, newMap) {
            val points = merges.sumOf { numberFor(it.first).value }
            score.update(score.value + points)
            isAnimationRunning = false
            checkGameOver()
        }
    }
    else isAnimationRunning = false
}
private fun Stage.checkGameOver() {
    if (!map.hasAvailableMoves() && !isGameOver) {
        isGameOver = true
        showGameOver {
            isGameOver = false
            restart()
        }
    }
}

fun Container.showGameOver(onRestart: () -> Unit) = container {
    fun restart() {
        this@container.removeFromParent()
        onRestart()
    }

    position(leftIndent, topIndent)

    roundRect(fieldSize, fieldSize, 5.0*uiScale, fill = Colors["#BBBBBB77"])
    text("Game Over", 60.0*uiScale, Colors.BLACK, font) {
        centerBetween(0.0, 0.0, fieldSize, fieldSize)
        y -= 60*uiScale
    }
    text("다시 시작", 40.0*uiScale, Colors.BLACK, font) {
        centerBetween(0.0, 0.0, fieldSize, fieldSize)

        y += 20*uiScale
        textSize = 40.0*uiScale
        onOver { color = RGBA(90, 90, 90) }
        onOut { color = RGBA(0, 0, 0) }
        onDown { color = RGBA(120, 120, 120) }
        onUp { color = RGBA(120, 120, 120) }
        onClick { restart() }
    }

    keys.down {
        when (it.key) {
            Key.ENTER, Key.SPACE -> restart()
            else -> Unit
        }
    }
}

fun Container.restart() {
    map = PositionMap()
    blocks.values.forEach { it.removeFromParent() }
    blocks.clear()
    ranking.addRank(DateTimeTz.nowLocal(), score.value)
    score.update(0)

    val moves = mutableListOf<Pair<Int, Position>>()
    val merges = mutableListOf<Triple<Int, Int, Position>>()
    st.showAnimation(moves, merges, map){ isAnimationRunning = false }
    //generateBlock() 쓰면 블럭이 이상한데 생김 그래서 위와 같이 스파게티 완성

    isAnimationRunning = false
}

fun numberFor(blockId: Int) = blocks[blockId]!!.number

fun deleteBlock(blockId: Int) = blocks.remove(blockId)!!.removeFromParent()

fun calculateNewMap(
    map: PositionMap,
    direction: Direction,
    moves: MutableList<Pair<Int, Position>>,
    merges: MutableList<Triple<Int, Int, Position>>
): PositionMap {
    val newMap = PositionMap()
    val startIndex = when (direction) {
        Direction.LEFT, Direction.TOP -> 0
        Direction.RIGHT, Direction.BOTTOM -> 3
    }
    var columnRow = startIndex

    fun newPosition(line: Int) = when (direction) {
        Direction.LEFT -> Position(columnRow++, line)
        Direction.RIGHT -> Position(columnRow--, line)
        Direction.TOP -> Position(line, columnRow++)
        Direction.BOTTOM -> Position(line, columnRow--)
    }

    for (line in 0..3) {
        var curPos = map.getNotEmptyPositionFrom(direction, line)
        columnRow = startIndex

        while (curPos != null) {
            val newPos = newPosition(line)
            val curId = map[curPos.x, curPos.y]
            map[curPos.x, curPos.y] = -1

            val nextPos = map.getNotEmptyPositionFrom(direction, line)
            val nextId = nextPos?.let { map[it.x, it.y] }
            //two blocks are equal
            if (nextId != null && numberFor(curId) == numberFor(nextId)) {
                //merge these blocks
                map[nextPos.x, nextPos.y] = -1
                newMap[newPos.x, newPos.y] = curId
                merges += Triple(curId, nextId, newPos)
            } else {
                //add old block
                newMap[newPos.x, newPos.y] = curId
                moves += Pair(curId, newPos)
            }
            curPos = map.getNotEmptyPositionFrom(direction, line)
        }
    }
    return newMap
}


