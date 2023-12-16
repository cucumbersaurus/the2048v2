import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*

lateinit var bgField: RoundRect
lateinit var bgLogo: RoundRect
lateinit var bgBest: RoundRect
lateinit var bgScore: RoundRect
suspend fun Stage.createScreen() {
    createGameBackground()
    crateContainers()
    addTexts()
    createButtons()
}

private fun Stage.createGameBackground() {
    with (st) {
        bgField = roundRect(fieldSize, fieldSize, 5.0*uiScale, fill = Colors["#b9aea0"]) {
            position(leftIndent, topIndent)
        }

        graphics {
            it.position(leftIndent, topIndent)
            fill(Colors["cec0b2"]) {
                for (i in 0..3) {
                    for (j in 0..3) {
                        roundRect(10*uiScale + (10*uiScale + cellSize) * i, 10*uiScale + (10*uiScale + cellSize) * j, cellSize, cellSize, 5.0*uiScale)
                    }
                }
            }
        }
    }
}


private fun Stage.crateContainers() {
    with (st) {
        bgLogo = roundRect(cellSize, cellSize, 5.0*uiScale, fill = Colors["edc403"]) {
            position(leftIndent, 30.0*uiScale)
        }

        bgBest = roundRect(cellSize * 1.5, cellSize * 0.8, 5.0*uiScale, fill = Colors["bbae9e"]) {
            alignRightToRightOf(bgField)
            alignTopToTopOf(bgLogo)
        }

        bgScore = roundRect(cellSize * 1.5, cellSize * 0.8, 5.0*uiScale, fill = Colors["bbae9e"]) {
            alignRightToLeftOf(bgBest, 24)
            alignTopToTopOf(bgLogo)
        }
    }
}

private suspend fun Stage.addTexts() {
    loadFont.await()

    with (st) {
        text("2048", cellSize * 0.3, Colors.WHITE, font) {
            centerXOn(bgLogo)
            alignTopToTopOf(bgLogo, 25*uiScale)
        }
        text("by Overflow", cellSize * 0.15, Colors.WHITE, font) {
            centerXOn(bgLogo)
            alignBottomToBottomOf(bgLogo, 20*uiScale)
        }

        text("최고점수", cellSize * 0.2, RGBA(239, 226, 210), font) {
            centerXOn(bgBest)
            alignTopToTopOf(bgBest, 5.0*uiScale)
        }

        text(ranking.best.toString(), cellSize * 0.3, Colors.WHITE, font) {
            setTextBounds(Rectangle(0.0, 0.0, bgBest.width, cellSize - 24.0*uiScale))
            alignment = TextAlignment.MIDDLE_CENTER
            alignTopToTopOf(bgBest, 12.0*uiScale)
            centerXOn(bgBest)
            best.observe {
                text = if (it > ranking.best) it.toString()
                else ranking.best.toString()
            }
        }

        text("점수", cellSize * 0.2, RGBA(239, 226, 210), font) {
            centerXOn(bgScore)
            alignTopToTopOf(bgScore, 5.0*uiScale)
        }

        text("0", cellSize * 0.3, Colors.WHITE, font) {
            setTextBounds(Rectangle(0.0, 0.0, bgScore.width, cellSize - 24.0*uiScale))
            alignment = TextAlignment.MIDDLE_CENTER
            centerXOn(bgScore)
            alignTopToTopOf(bgScore, 12.0*uiScale)
            score.observe { text = it.toString() }
        }
    }
}

private suspend fun Stage.createButtons() {
    loadImg.await()
    val btnSize = cellSize * 0.3

    with (st) {
        val restartBlock = container {
            val background = roundRect(btnSize, btnSize, 5.0*uiScale, fill = RGBA(185, 174, 160))
            image(restartImg) {
                size(btnSize * 0.8, btnSize * 0.8)
                centerOn(background)
            }
            alignTopToBottomOf(bgBest, 5)
            alignRightToRightOf(bgField)

            onClick {
                if (!isGameOver)
                    this.restart()
            }
        }

        val rankingBlock = container {
            val leaderboardBlock = roundRect(btnSize, btnSize, 5.0*uiScale, fill = RGBA(185, 174, 160))
            image(leaderboardImg) {
                size(btnSize * 0.8, btnSize * 0.8)
                centerOn(leaderboardBlock)
            }
            alignTopToBottomOf(bgBest, 5)
            alignRightToLeftOf(restartBlock, 5)

            onClick { showRanking() }
        }
    }
}
