object Libs {
    val kotlinVersion = "1.3.60"

    private val http4kVersion = "3.196.0"
    private val moshiVersion = "1.8.0"

    val http4kCore = "org.http4k:http4k-core:$http4kVersion"
    val http4kUndertow = "org.http4k:http4k-server-undertow:$http4kVersion"
    val http4kJetty = "org.http4k:http4k-server-jetty:$http4kVersion"
    val http4kNetty = "org.http4k:http4k-server-netty:$http4kVersion"

    val moshi = "com.squareup.moshi:moshi:$moshiVersion"
    val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"

    val graphQlJava = "com.graphql-java:graphql-java:13.0"
    val loggerSimple = "org.slf4j:slf4j-simple:1.7.29"

    val sqlite = "org.xerial:sqlite-jdbc:3.28.0"
    val mysql = "mysql:mysql-connector-java:8.0.18"
    val exposed = "org.jetbrains.exposed:exposed:0.17.7"

    val kaml = "com.charleskorn.kaml:kaml:0.15.0"
    val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0"
}
