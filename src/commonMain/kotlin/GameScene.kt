import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.interpolation.*
import kotlinx.coroutines.*
import kotlin.random.*

class GameScene(
    override var views: Views,
    val st: Stage
    //val globalEventBus: EventBus,
    //val mapBridge: MapBridge,
    ): Scene() {
    override suspend fun SContainer.sceneInit() {
        //VIEWS_INSTANCE = views
        score.observe {
            if (it > best.value) best.update(it)
        }

        cellSize = views.virtualWidth / 5.0
        fieldSize = 50 + 4 * cellSize
        leftIndent = (views.virtualWidth - fieldSize) / 2
        topIndent = 150.0


    }

    override suspend fun SContainer.sceneMain(){
        val bgField = roundRect(fieldSize, fieldSize, 5.0, fill = Colors["#b9aea0"]) {
            position(leftIndent, topIndent)
            addTo(st)
        }

        st.graphics {
            it.position(leftIndent, topIndent)
            fill(Colors["cec0b2"]) {
                for (i in 0..3) {
                    for (j in 0..3) {
                        roundRect(10 + (10 + cellSize) * i, 10 + (10 + cellSize) * j, cellSize, cellSize, 5.0)
                    }
                }
            }
        }

        val bgLogo = roundRect(cellSize, cellSize, 5.0, fill = Colors["edc403"]) {
            position(leftIndent, 30.0)
            addTo(st)
        }

        val bgBest = roundRect(cellSize * 1.5, cellSize * 0.8, 5.0, fill = Colors["bbae9e"]) {
            alignRightToRightOf(bgField)
            alignTopToTopOf(bgLogo)
        }

        val bgScore = roundRect(cellSize * 1.5, cellSize * 0.8, 5.0, fill = Colors["bbae9e"]) {
            alignRightToLeftOf(bgBest, 24)
            alignTopToTopOf(bgLogo)
        }

        loadImg.await()
        val btnSize = cellSize * 0.3
        //restart block
        container {
            val background = roundRect(btnSize, btnSize, 5.0, fill = RGBA(185, 174, 160))
            image(restartImg) {
                size(btnSize * 0.8, btnSize * 0.8)
                centerOn(background)
            }
            alignTopToBottomOf(bgBest, 5)
            alignRightToRightOf(bgField)

            onClick {
                if (!isGameOver)
                    st.restart()
            }
        }

        loadFont.await()
        with(st) {
            text("2048", cellSize * 0.3, Colors.WHITE, font) {
                centerXOn(bgLogo)
                alignBottomToBottomOf(bgLogo, 20)
            }
            text("전설의", cellSize * 0.2, Colors.WHITE, font) {
                centerXOn(bgLogo)
                alignTopToTopOf(bgLogo, 15)
            }

            text("최고점수", cellSize * 0.2, RGBA(239, 226, 210), font) {
                centerXOn(bgBest)
                alignTopToTopOf(bgBest, 5.0)
                onOver { color = RGBA(255, 255, 255) }
                onOut { color = RGBA(239, 226, 210) }
                onDown { color = RGBA(120, 120, 120) }
                onUp { color = RGBA(255, 255, 255) }
                //onClick { showRanking(views) }
            }

            text(ranking.best.toString(), cellSize * 0.3, Colors.WHITE, font) {
                setTextBounds(Rectangle(0.0, 0.0, bgBest.width, cellSize - 24.0))
                alignment = TextAlignment.MIDDLE_CENTER
                alignTopToTopOf(bgBest, 12.0)
                centerXOn(bgBest)
                best.observe {
                    text = if (it > ranking.best) it.toString()
                    else ranking.best.toString()
                }
            }

            text("점수", cellSize * 0.2, RGBA(239, 226, 210), font) {
                centerXOn(bgScore)
                alignTopToTopOf(bgScore, 5.0)
            }

            text("0", cellSize * 0.3, Colors.WHITE, font) {
                setTextBounds(Rectangle(0.0, 0.0, bgScore.width, cellSize - 24.0))
                alignment = TextAlignment.MIDDLE_CENTER
                centerXOn(bgScore)
                alignTopToTopOf(bgScore, 12.0)
                score.observe {
                    text = it.toString()
                }
            }
        }
        generateBlock()

//        views.stage.addEventListener<KeyEvent> {e->
//            when(e.key){
//                Key.LEFT -> moveBlocksTo(Direction.LEFT)
//                Key.RIGHT -> moveBlocksTo(Direction.RIGHT)
//                Key.UP -> moveBlocksTo(Direction.TOP)
//                Key.DOWN -> moveBlocksTo(Direction.BOTTOM)
//                else -> Unit
//            }
//        }

        st.keys {
            down {
                if (!isGameOver) {
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
//        views.stage.onSwipe(40.0) {
//            if (!isGameOver) {
//                when (it.direction) {
//                    SwipeDirection.LEFT -> moveBlocksTo(Direction.LEFT)
//                    SwipeDirection.RIGHT -> moveBlocksTo(Direction.RIGHT)
//                    SwipeDirection.TOP -> moveBlocksTo(Direction.TOP)
//                    SwipeDirection.BOTTOM -> moveBlocksTo(Direction.BOTTOM)
//                }
//            }
//        }

    }

//    fun setupKey(listener: EventListener){
//        listener.onEvent(KeyEvent.Type.DOWN){
//            onKeyEvent(it)
//        }
//    }
//
//    fun onKeyEvent(event: KeyEvent) {
//        when (event.key) {
//            Key.LEFT -> views.stage.moveBlocksTo(Direction.LEFT)
//            Key.RIGHT -> views.stage.moveBlocksTo(Direction.RIGHT)
//            Key.UP -> views.stage.moveBlocksTo(Direction.TOP)
//            Key.DOWN -> views.stage.moveBlocksTo(Direction.BOTTOM)
//            else -> Unit
//        }
//    }

    override suspend fun sceneDestroy() {
        //stage.dispatch<KeyEvent>(KeyEvent())
    }

    fun Container.block(num: Number) = Block(num).addTo(st)

    fun columnX(number: Int) = leftIndent + 10 + (cellSize + 10) * number
    fun rowY(number: Int) = topIndent + 10 + (cellSize + 10) * number

    fun Container.createNewBlockWithId(id: Int, number: Number, position: Position) {
        blocks[id] = block(number).position(columnX(position.x), rowY(position.y))
    }

    fun Container.createNewBlock(number: Number, position: Position): Int {
        val id = freeId++
        createNewBlockWithId(id, number, position)
        return id
    }

    fun Container.generateBlock(): Int? {
        val position = map.getRandomFreePosition() ?: return null
        val number = if (Random.nextDouble() < 0.9) Number.ZERO else Number.ONE
        val newId = createNewBlock(number, position)
        map[position.x, position.y] = newId
        return newId
    }

    fun moveBlocksTo(direction: Direction) {
        if (isAnimationRunning) return
        if (animator.isActive) return

        animator.cancel()
        isAnimationRunning = true

        val moves = mutableListOf<Pair<Int, Position>>()
        val merges = mutableListOf<Triple<Int, Int, Position>>()
        val newMap = calculateNewMap(map.copy(), direction, moves, merges)

        if (map != newMap) {
            st.showAnimation(moves, merges, newMap) {
                val points = merges.sumOf { numberFor(it.first).value }
                score.update(score.value + points)
            }
        }
        isAnimationRunning = false

        if (!map.hasAvailableMoves()) {
            if (!isGameOver) {
                isGameOver = true
                st.showGameOver {
                    isGameOver = false
                    st.restart()
                }
            }
            return
        }
    }

    fun Container.showGameOver(onRestart: () -> Unit) = container {
        fun restart() {
            this@container.removeFromParent()
            onRestart()
        }

        position(leftIndent, topIndent)

        roundRect(fieldSize, fieldSize, 5.0, fill = Colors["#BBBBBB77"])
        text("Game Over", 60.0, Colors.BLACK, font) {
            centerBetween(0.0, 0.0, fieldSize, fieldSize)
            y -= 60
        }
        text("다시 시작", 40.0, Colors.BLACK, font) {
            centerBetween(0.0, 0.0, fieldSize, fieldSize)

            y += 20
            textSize = 40.0
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
        generateBlock()
        isAnimationRunning = false
    }

    fun numberFor(blockId: Int) = blocks[blockId]!!.number

    fun deleteBlock(blockId: Int) = blocks.remove(blockId)!!.removeFromParent()

    fun Stage.showAnimation(
        moves: List<Pair<Int, Position>>,
        merges: List<Triple<Int, Int, Position>>,
        newMap: PositionMap,
        onEnd: () -> Unit
    ) {
        animator = launchImmediately {
            scaleAnimationList.clear()

            animateSequence {
                parallel {
                    moves.forEach { (id, pos) ->
                        blocks[id]!!.moveTo(columnX(pos.x), rowY(pos.y), 0.1.seconds, Easing.EASE_OUT)
                    }
                    merges.forEach { (id1, id2, pos) ->
                        sequence {
                            parallel {
                                blocks[id1]!!.moveTo(columnX(pos.x), rowY(pos.y), 0.1.seconds, Easing.EASE_OUT)
                                blocks[id2]!!.moveTo(columnX(pos.x), rowY(pos.y), 0.1.seconds, Easing.EASE_OUT)
                            }
                            block {
                                val nextNumber = numberFor(id1).next()
                                deleteBlock(id1)
                                deleteBlock(id2)
                                createNewBlockWithId(id1, nextNumber, pos)
                            }
                            scaleAnimationList.add(id1)
                        }
                    }
                }
                map = newMap
                val newId: Int? = generateBlock()

                if (newId != null) {
                    scaleAnimationList.add(newId)
                }
                parallel {
                    scaleAnimationList.forEach {
                        sequenceLazy {
                            runBlocking { animateScale(blocks[it]!!) }//////////////////////////////
                        }
                    }
                }

                block {
                    onEnd()
                }
            }
        }

        moves.forEach {
            val block = blocks[it.first]
            val pos = it.second
            block?.position(columnX(pos.x), rowY(pos.y))
        }

        merges.forEach {
            val bl1 = blocks[it.first]
            val bl2 = blocks[it.second]
            val pos = it.third

            bl1?.position(columnX(pos.x), rowY(pos.y))
            bl2?.position(columnX(pos.x), rowY(pos.y))
        }
    }

    suspend fun Animator.animateScale(block: Block) {
        val x = block.x
        val y = block.y
        val scale = block.scale

        val multiplier = 0.8////////////////////////////////////////////////////////////////

        st.tween(
            block::x[x - 4 * multiplier],
            block::y[y - 4 * multiplier],
            block::scale[scale + 0.1 * multiplier],
            time = (0.1 * multiplier).seconds,
            easing = Easing.EASE_IN
        )
        st.tween(
            block::x[x],
            block::y[y],
            block::scale[scale],
            time = (0.1 * multiplier).seconds,
            easing = Easing.EASE_OUT
        )
    }

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
}
