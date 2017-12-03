# Various parameters can be specified: conf ha instance workPoolSize
# java -jar target/boruto-1.0.0-fat.jar -conf src/main/resources/test.json -instances 24 &

# Maven Build
mvn clean package

# Run Vert.x Instance
java -jar target/boruto-1.0.0-fat.jar \
-DisDebug=false \
-conf src/main/resources/prod.json &
