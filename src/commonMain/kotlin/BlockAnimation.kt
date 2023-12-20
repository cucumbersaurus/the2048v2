import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import com.soywiz.korma.interpolation.*
import kotlin.math.*
import kotlin.random.*

var scaleAnimationScale = 0.8
var moveAnimationScale = 0.8

fun Stage.showAnimation(
    moves: List<Pair<Int, Position>>,
    merges: List<Triple<Int, Int, Position>>,
    newMap: PositionMap,
    onEnd: () -> Unit
) {
    animator = launchImmediately {
        scaleAnimationList.clear()
        val moveAnimScale = getMoveAnimScale()
        val scaleAnimScale = getScaleAnimScale()

        animateSequence {
            parallel {
                moves.forEach { (id, pos) ->
                    blocks[id]!!.moveTo(columnX(pos.x), rowY(pos.y), (0.1 * moveAnimScale).seconds, Easing.EASE_OUT)
                }
                merges.forEach { (id1, id2, pos) ->
                    sequence {
                        parallel {
                            blocks[id1]!!.moveTo(
                                columnX(pos.x),
                                rowY(pos.y),
                                (0.1 * moveAnimScale).seconds,
                                Easing.EASE_OUT
                            )
                            blocks[id2]!!.moveTo(
                                columnX(pos.x),
                                rowY(pos.y),
                                (0.1 * moveAnimScale).seconds,
                                Easing.EASE_OUT
                            )
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
                        animateScale(blocks[it], scaleAnimScale)
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

fun Animator.animateScale(block: Block?, animScale: Double) {
    if(block == null) return

    val x = block.x
    val y = block.y
    val scale = block.scale

    tween(
        block::x[x - if(animScale!=0.0) 4*uiScale else 0.0],
        block::y[y - if(animScale!=0.0) 4*uiScale else 0.0],
        block::scale[scale + if(animScale!=0.0) 0.1 else 0.0],
        time = (0.1 * animScale).seconds,
        easing = Easing.EASE_IN
    )
    tween(
        block::x[x],
        block::y[y],
        block::scale[scale],
        time = (0.1 * animScale).seconds,
        easing = Easing.EASE_OUT
    )
}

fun getMoveAnimScale():Double{
    return if(isOnRandomAnimationSpeedMode) abs(Random.nextDouble(-3.0, 10.0))
    else moveAnimationScale
}

fun getScaleAnimScale():Double{
    return if(isOnRandomAnimationSpeedMode) abs(Random.nextDouble(-3.0, 10.0))
    else scaleAnimationScale
}
