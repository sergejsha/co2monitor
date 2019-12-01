package de.halfbit.co2monitor.database

import de.halfbit.co2monitor.Arguments
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.sql.Connection

fun initializeDatabase(database: Arguments.Database) {
    val database = Database.connect(database.url, database.driver, database.user, database.password)
    database.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(Measurements)
    }
}

object Measurements : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val time = long("time").uniqueIndex()
    val temperature = short("temperature").index()
    val co2 = short("co2").index()
}
