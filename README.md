mvn clean package

mvn wildfly-swarm:run

http://brochain.hd.free.fr:8181/OHLC?grain=5

http://brochain.hd.free.fr:8181/AlerteMACD?grain=5

http://brochain.hd.free.fr:8181/macd/?grain=5

mvn wildfly-swarm:stop

netstat -antu | grep 8181

jps

kill -9