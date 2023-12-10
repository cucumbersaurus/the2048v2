import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class RankingScreen(val st:Stage):Container() {

    val border:RoundRect = st.roundRect(404.0, 604.0, 6.0, fill = Colors["#282c34"]) {
        position((st.views.virtualWidth-404)/2.0, (st.views.virtualHeight-604)/2.0)

        zIndex += 1.0
    }
    val winBackground = st.roundRect(400.0, 600.0, 5.0, fill = Colors["#f0e4da"]) {
        position((st.views.virtualWidth-400)/2.0, (st.views.virtualHeight-600)/2.0)

        zIndex += 2.0
    }

    val close = winBackground.circle {
        position(388.0, 2.0)
        radius = 5.0
        color = Colors["#df6263"]
        zIndex += 3.0


        onOver { color =  Colors["#c76667"]}
        onOut { color = Colors["#ff5356"] }
        onDown { color = Colors["#c76667"] }
        onUp { color = Colors["#ff5356"] }
        onClick { closeRanking() }
    }

    val title = winBackground.text("랭킹", 40.0, Colors["000000"], font){
        centerXOn(winBackground)
        alignTopToTopOf(winBackground, 20.0)
    }


    val rankList = ranking.getTop8()

    init{
        for(i in 0 .. 7){
            RankCard(winBackground, title, rankList[i], i)
        }
    }

    fun closeRanking(){
        border.removeFromParent()
        winBackground.removeFromParent()

        isOnRankingScreen = false
    }
}

class RankCard(val background: Container, val title : Container, val info: Pair<String, Int>, val rank:Int): Container() {
    init{
        val card = background.roundRect(350.0, 45.0, 5.0, fill = getColor()){
            centerXOn(background)
            alignTopToBottomOf(title, 12+57*rank)
        }

        card.text("${rank+1}", 35.0, Colors.BLACK){
            centerYOn(card)
            alignLeftToLeftOf(card, 15)
        }

        card.text(info.first.split(" ")[0] , 25.0, Colors.BLACK){
            centerYOn(card)
            alignLeftToLeftOf(card, 55)
        }

        card.text(info.second.toString() , 35.0, Colors.BLACK){
            centerYOn(card)
            alignRightToRightOf(card, 15)
        }
    }

    fun getColor(): RGBA {
        if(rank==0){
            return Colors["#edd35e"]
        }
        else if (rank==1){
            return Colors["#c4c0b2"]
        }
        else if (rank==2){
            return Colors["#9a765b"]
        }
        else{
            return Colors["#fdf7f0"]
        }
    }
}
