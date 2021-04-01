#!/bin/sh

JAVA_CMD="java -server -jar ${JAVA_TOOL_OPTIONS} app.jar"
echo $JAVA_CMD
exec ${JAVA_CMD}
