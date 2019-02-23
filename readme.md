# Vert.x Asynch JAX-WS

## Run

    verty>java -jar target\spring-vertx-0.0.1-SNAPSHOT.jar
    

## Test

    echo GET http://localhost:8080/status | vegeta attack -duration=5s -rate=1000 | tee results.bin | vegeta report
