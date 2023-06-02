package database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.*
import java.io.*

object Database {

    private const val url = "jdbc:sqlite:data/database.db"
    private const val driver = "org.sqlite.JDBC"

    fun connect() {
        val file = File("data/database.db")
        if(!file.exists()) file.createNewFile()
        try {
            TransactionManager.defaultDatabase = Database.connect(url, driver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        transaction{
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(BestScoreData)
            SchemaUtils.create(ScoreData)

        }
    }

    fun writeScore(score: Int, playerName: String){
        ScoreData.run {
            insertScore(score, playerName)
        }
    }

    fun readBestScore(){


    }

    fun readScore(id:Int) = ScoreData.read(id)
}
