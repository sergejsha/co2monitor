

# Update GraphQl schema

```bash
# Download schema
apollo schema:download --endpoint http://raspberrypi:8081/graphql

# Generate models
./gradlew :generateApolloClasses
```