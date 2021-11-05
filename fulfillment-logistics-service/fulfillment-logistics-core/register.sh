#!/bin/sh 
echo "curl -v -X PUT -H 'Content-Type: application/json' -d '{\"ID\": \"cm-fulfillment-logistics\",\"Name\": \"cm-fulfillment-logistics\",\"Address\": \"${PODIP}\",\"Port\": 9095,\"Tags\":[\"v1.0.0\"],\"EnableTagOverride\": false,\"Check\": {\"DeregisterCriticalServiceAfter\": \"5m\",\"HTTP\": \"http://127.0.0.1:8085/actuator/health\",\"Interval\": \"10s\"},\"Weights\": {\"Passing\": 10,\"Warning\": 1}}' http://127.0.0.1:8500/v1/agent/service/register" > register.sh

chmod +x register.sh

sh register.sh

java ${JVM_OPTS} -jar ${APP_OPTS} /app.jar
