export $(cat .env | xargs) && \
export DATABASE_URL="jdbc:postgresql://localhost:5478/${POSTGRES_DB}"
./gradlew bootRun --debug

