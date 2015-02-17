#!/bin/sh

# Pinata deployment script
# (C) 2015 Christian Gunderman

export JAVA_HOME=/usr/lib/jvm/java-7-oracle

gradle clean build test

if [ $? -eq 0 ]
then
    sudo cp -f service/build/libs/service.war /var/lib/tomcat6/webapps/ROOT.war
#    cd /usr/share/tomcat8/lib
 #   sudo wget http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar
fi
