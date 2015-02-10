#!/bin/sh

# Pinata deployment script
# (C) 2015 Christian Gunderman

export JAVA_HOME=/usr/lib/jvm/java-7-oracle

gradle clean build test

if [ $? -eq 0 ]
then
    sudo cp -f build/libs/service.war /var/lib/tomcat8/webapps/ROOT.war
fi
