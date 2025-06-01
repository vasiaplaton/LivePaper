export $(cat .env | xargs) && \
export DATABASE_URL="jdbc:postgresql://localhost:5478/${POSTGRES_DB}"
export CLICKHOUSE_URL="jdbc:ch://localhost:8123/docs"
./gradlew bootRun

