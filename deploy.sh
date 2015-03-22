#!/bin/bash

# Pinata deployment script
# (C) 2015 Christian Gunderman

export JAVA_HOME=/usr/lib/jvm/java-7-oracle

clear
echo "Pinata Deployment Script"
echo "(C) 2015 Christian Gunderman"
echo 

# Get tomcat version.
echo "What version of tomcat are you using ?"
echo " 1. tomcat6"
echo " 2. tomcat7"
echo " 3. tomcat8"
read PINATA_MENU

if [ $PINATA_MENU = '1' ]
then
    PINATA_TOMCAT_VERSION="tomcat6"
elif [ $PINATA_MENU = '2' ]
then
    PINATA_TOMCAT_VERSION="tomcat7"
elif [ $PINATA_MENU = '3' ]
then
    PINATA_TOMCAT_VERSION="tomcat8"
else
    echo "Invalid menu option."
    echo "Aborting."
    exit
fi

# Prompt for rebuild.
echo "Rebuild ?"
echo "(Y, n)"
read PINATA_YN
if [ $PINATA_YN = 'Y' ]
then
    echo "Cleaning, building, testing..."
    gradle clean build test
else
    echo "Skipping rebuild..."
fi

# Install if Rebuild was successful.
if [ $? -eq 0 ]
then
    echo "Installing..."
    sudo rm -rf /var/lib/$PINATA_TOMCAT_VERSION/webapps/*
    sudo cp -f service/build/libs/service.war /var/lib/$PINATA_TOMCAT_VERSION/webapps/ROOT.war
fi
echo "Finished."
