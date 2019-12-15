object Libs {
    const val kotlinVersion = "1.3.60"

    private const val ktorVersion = "1.3.0-beta-1"
    private const val moshiVersion = "1.8.0"

    val ktorServerCore = "io.ktor:ktor-server-core:$ktorVersion"
    val ktorNetty = "io.ktor:ktor-server-netty:$ktorVersion"

    val moshi = "com.squareup.moshi:moshi:$moshiVersion"
    val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"

    val graphQlJava = "com.graphql-java:graphql-java:13.0"
    val loggerSimple = "org.slf4j:slf4j-simple:1.7.29"

    val mysql = "mysql:mysql-connector-java:8.0.18"
    val exposed = "org.jetbrains.exposed:exposed:0.17.7"

    val kaml = "com.charleskorn.kaml:kaml:0.15.0"
    val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0"
}
