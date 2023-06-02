package database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

object ScoreData: Table() {

    val id = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", 24)

    val score = integer("score")

    override val primaryKey by lazy {
        super.primaryKey ?: PrimaryKey(id)
    }

    fun get(id: Int): ResultRow {
        return transaction {
            select { ScoreData.id eq id }.toList()[0]
        }
    }

    fun insertScore(data: Int, playerName: String){
        transaction {
            insert {
                it[score] = data
                it[name] = playerName
            }
        }
    }

    fun read(id:Int):List<ResultRow>{
        return transaction {
            select { ScoreData.id eq id }.toList()
        }
    }

}
