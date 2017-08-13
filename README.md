mvn clean package

mvn wildfly-swarm:run

http://localhost:8181/


mvn wildfly-swarm:stop

netstat -antu | grep 8181
jps
kill -9