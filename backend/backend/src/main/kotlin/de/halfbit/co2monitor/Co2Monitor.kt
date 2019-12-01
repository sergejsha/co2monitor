package de.halfbit.co2monitor

import com.charleskorn.kaml.Yaml
import de.halfbit.co2monitor.database.createDatabaseCleaner
import de.halfbit.co2monitor.web.createGraphqlHandler
import kotlinx.serialization.Serializable
import org.http4k.core.HttpHandler
import java.io.File
import java.nio.charset.Charset
import java.util.*

class Co2Monitor(
    val args: Arguments,
    val graphqlHandler: HttpHandler,
    val databaseCleaner: Timer
)

@Serializable
data class Arguments(
    val server: Server,
    val database: Database
) {
    @Serializable
    data class Server(
        val port: Int
    )

    @Serializable
    data class Database(
        val url: String,
        val driver: String,
        val user: String,
        val password: String,
        val cleaner: Cleaner
    ) {

        @Serializable
        data class Cleaner(
            val periodDays: Int,
            val retentionDays: Int
        )
    }
}

inline fun initialize(block: Co2Monitor.() -> Unit) =
    File("./data/co2monitor.yml")
        .readBytes()
        .toString(charset = Charset.forName("UTF-8"))
        .let {
            val args = Yaml.default.parse(Arguments.serializer(), it)
            val app = Co2Monitor(
                args = args,
                graphqlHandler = createGraphqlHandler(),
                databaseCleaner = createDatabaseCleaner(args.database.cleaner)
            )
            block(app)
        }
