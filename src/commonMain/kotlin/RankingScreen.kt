import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import kotlin.math.*

class RankingScreen:Container() {

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
        onClick { closeRanking() }
    }

    private val title = winBackground.text("랭킹", 40.0*uiScale, Colors["000000"], font){
        centerXOn(winBackground)
        alignTopToTopOf(winBackground, 20.0*uiScale)
    }

    private val rankList = ranking.getTop9()

    init{
        for(i in 0 .. min(8, rankList.size-1)){
            RankCard(winBackground, title, rankList[i], i)
        }
    }

    private fun closeRanking(){
        border.removeFromParent()
        winBackground.removeFromParent()

        isOnRankingScreen = false
    }
}

class RankCard(private val background: Container, private val title : Container, private val info: Pair<String, Int>, private val rank:Int): Container() {
    init{
        val card = background.roundRect(350.0*uiScale, 45.0*uiScale, 5.0*uiScale, fill = getColor()){
            centerXOn(background)
            alignTopToBottomOf(title, 12*uiScale+57*uiScale*rank)
        }

        if(rank==0){
            card.image(trophyImg){
                centerYOn(card)
                y -= 10*uiScale
                alignLeftToLeftOf(card, -5*uiScale)
                size(60*uiScale, 60*uiScale)
            }

            card.text("${rank+1}", 35.0*uiScale, Colors.BLACK){
                centerYOn(card)
                alignLeftToLeftOf(card, 22*uiScale)
            }
        }
        else {
            card.text("${rank + 1}", 35.0 * uiScale, Colors.BLACK) {
                centerYOn(card)
                alignLeftToLeftOf(card, 15 * uiScale)
            }
        }

        card.text(info.first.split(" ")[0] , 25.0*uiScale, Colors.BLACK){
            centerYOn(card)
            alignLeftToLeftOf(card, 55*uiScale)
        }

        card.text(info.second.toString() , 35.0*uiScale, Colors.BLACK){
            centerYOn(card)
            alignRightToRightOf(card, 15*uiScale)
        }
    }

    private fun getColor(): RGBA {
        return when (rank) {
            0 -> Colors["#edd35e"]
            1 -> Colors["#c4c0b2"]
            2 -> Colors["#9a765b"]
            else -> Colors["#fdf7f0"]
        }
    }
}
