#!/bin/bash

export JAVA_HOME="/opt/bitnami/java"
export JAVA_OPTS="-Djava.awt.headless=true -XX:+UseG1GC -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -Duser.home=/opt/bitnami/tomcat"
export CATALINA_PID="/opt/bitnami/tomcat/temp/catalina.pid"

# Load Tomcat Native library
export LD_LIBRARY_PATH="/opt/bitnami/tomcat/lib:${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"

# JENNIFER Java Agent
AGENT_HOME=/opt/agent.java
#export CATALINA_OPTS="$CATALINA_OPTS -Djennifer.config=$AGENT_HOME/conf/jennifer.conf -javaagent:$AGENT_HOME/jennifer.jar"
export JAVA_OPTS="$JAVA_OPTS -Djennifer.config=$AGENT_HOME/conf/YWMC-FHIR-001.conf -javaagent:$AGENT_HOME/jennifer.jar"