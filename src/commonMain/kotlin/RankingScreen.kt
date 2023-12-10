import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.onClick
import com.soywiz.korim.color.*

class RankingScreen(val st:Stage):Container() {

    val border:RoundRect = st.roundRect(404.0, 604.0, 6.0, fill = Colors["#282c34"]) {
        position((st.views.virtualWidth-404)/2.0, (st.views.virtualHeight-604)/2.0)

        zIndex += 1.0
    }
    val winBackground:RoundRect = st.roundRect(400.0, 600.0, 5.0, fill = Colors["#f0e4da"]) {
        position((st.views.virtualWidth-400)/2.0, (st.views.virtualHeight-600)/2.0)

        zIndex += 2.0
    }

    val close = winBackground.circle {
        position(388.0, 2.0)
        radius = 5.0
        fill = Colors["#ff5356"]
        zIndex += 3.0

        onOver { color =  Colors["#ff9192"]}
        onOut { color = Colors["#ff5356"] }
        onDown { color = Colors["#ff9192"] }
        onUp { color = Colors["#ff5356"] }
        onClick { closeRanking() }
    }

//    val X = close.text("×", textSize = 10.0, Colors["#ffffff"], font){
//        centerXOn(close)
//        zIndex += 4.0
//
//        onOver { color =  Colors["#ffffff"]}
//        onOut { color = Colors["#ffffff"] }
//        onDown { color = Colors["#ffffff"] }
//        onUp { color = Colors["#ffffff"] }
//        onClick { closeRanking()}
//    }

    fun closeRanking(){
        border.removeFromParent()
        winBackground.removeFromParent()

    }

}
