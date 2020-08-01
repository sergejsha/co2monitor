package de.halfbit.co2monitor.graphql

import de.halfbit.co2monitor.Co2Database
import de.halfbit.co2monitor.graphql.fetchers.CreateMeasurementFetcher
import de.halfbit.co2monitor.graphql.fetchers.CurrentMeasurementFetcher
import graphql.GraphQL
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import java.nio.charset.Charset
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQuery

fun createGraphql(database: Co2Database): GraphQL {

    val asInstant = TemporalQuery<Instant> { temporal -> Instant.from(temporal) }
    val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC"))

    val typeRegistry = SchemaParser()
        .parse(
            object {}::class.java
                .getResourceAsStream("/graphql/schema.graphql")
                .readBytes().toString(Charset.forName("UTF-8"))
        )

    val runtimeWiring = RuntimeWiring
        .newRuntimeWiring()
        .scalar(
            GraphQLScalarType.newScalar()
                .name("DateTime")
                .coercing(
                    object : Coercing<Instant, String> {
                        override fun serialize(dataFetcherResult: Any?): String =
                            (dataFetcherResult as? Instant)?.let { dateTimeFormatter.format(it) }
                                ?: throw CoercingSerializeException("Expect Instant, received: $dataFetcherResult")

                        override fun parseValue(input: Any?): Instant =
                            (input as? String)?.let { dateTimeFormatter.parse(it, asInstant) }
                                ?: throw CoercingSerializeException("Expect String, received: $input")

                        override fun parseLiteral(input: Any?): Instant =
                            (input as? StringValue)?.let { dateTimeFormatter.parse(it.value, asInstant) }
                                ?: throw CoercingSerializeException("Expect StringValue, received: $input")
                    }
                )
                .build()
        )
        .type(
            TypeRuntimeWiring
                .newTypeWiring("Query")
                .dataFetcher("current", CurrentMeasurementFetcher(database))
                .build()
        )
        .type(
            TypeRuntimeWiring
                .newTypeWiring("Mutation")
                .dataFetcher("createMeasurement", CreateMeasurementFetcher(database))
                .build()
        )
        .build()

    return GraphQL
        .newGraphQL(
            SchemaGenerator()
                .makeExecutableSchema(
                    typeRegistry,
                    runtimeWiring
                )
        )
        .build()
}
