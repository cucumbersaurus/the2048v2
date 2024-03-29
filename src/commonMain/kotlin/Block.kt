import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korge.view.roundRect
import Number.*

class Block(val number: Number) : Container() {
    init {
        roundRect(cellSize, cellSize, 5.0*uiScale, fill = number.color)
        val textColor = when (number) {
            ZERO, ONE -> Colors.BLACK
            else -> Colors.WHITE
        }
        text(number.value.toString(), textSizeFor(number)*0.8, textColor, font) {
            centerBetween(0.0, 0.0, cellSize, cellSize)
        }
    }

    private fun textSizeFor(number: Number) = when (number) {
        ZERO, ONE, TWO, THREE, FOUR, FIVE -> cellSize / 2
        SIX, SEVEN, EIGHT -> cellSize * 4 / 9
        NINE, TEN, ELEVEN, TWELVE -> cellSize * 2 / 5
        THIRTEEN, FOURTEEN, FIFTEEN -> cellSize * 7 / 20
        SIXTEEN -> cellSize * 3 / 10
    }
}
