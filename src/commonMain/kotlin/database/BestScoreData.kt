package database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

object BestScoreData: Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", 24)

    val score = integer("score")

    override val primaryKey by lazy {
        super.primaryKey ?: PrimaryKey(id)
    }

    fun get(id: Int): ResultRow {
        return transaction {
            select { BestScoreData.id eq id }.toList()[0]
        }
    }

    fun insertBest(data: Int, playerName: String){

        transaction {
            BestScoreData.update ({BestScoreData.id eq 11}){
                it[score] = data
                it[name] = playerName
            }

            val scoreList = selectAll().toMutableList()
            scoreList.sortedBy { it[score] }.reversed()

            for(i in 1..11){
                BestScoreData.update({BestScoreData.id eq i}) {
                    it[score] = scoreList[i][score]
                    it[name] = scoreList[i][name]
                }
            }

        }
    }

    fun getBest():List<ResultRow>{
        return transaction {
            BestScoreData.selectAll().toList()
        }
    }

    fun read(): Column<Int> {

        TODO()
    }
}
