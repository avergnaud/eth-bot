mvn clean package

mvn wildfly-swarm:run

http://localhost:8181/OHLC?grain=5

http://localhost:8181/AlerteMACD?grain=30

http://localhost:8181/macd/

mvn wildfly-swarm:stop

netstat -antu | grep 8181

jps

kill -9