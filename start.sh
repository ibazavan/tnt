./mvnw clean package
docker build -t aggregator .
docker run -p 8080:8080 -d aggregator
docker run -p 9090:8080 -d xyzassessment/backend-services