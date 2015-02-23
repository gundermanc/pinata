#!/bin/sh

# Pinata API Database Install Script
# (C) 2015 Christian Gunderman

PINATA_MYSQL_DB=PinataAPI
PINATA_TOMCAT_USER=tomcat

clear
echo "Pinata API Database Installer"
echo "(C) 2015 Christian Gunderman"
echo
echo "**MAKE SURE YOU INSTALL MySQL and other deps. first***"
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

echo "Installing..."

# Link to MySQL Connector
echo "Create mysql-connector link ? You only need do this once (Y,n)"
read PINATA_YN
if [ $PINATA_YN = 'Y' ]
then
    echo "Creating mysql-connector link..."
    sudo ln -rs "/usr/share/java/mysql.jar /usr/share/"$PINATA_TOMCAT_VERSION"/lib/mysql.jar"
else
    echo "Skipping mysql-connector link..."
fi

# Copy server.xml configuration file.
echo "Install server.xml file ?"
echo "You only need to do this once unless you changed it (Y,n)"
read PINATA_YN
if [ $PINATA_YN = 'Y' ]
then
    echo Installing server.xml...
    sudo cp -f "etc/server.xml" "/var/lib/"$PINATA_TOMCAT_VERSION"/conf/server.xml"
else
    echo "Skipping server.xml install..."
fi

# Generate keystore.

echo "Generate SSL/TLS keystore ?"
echo "You only need to do this once. This will delete old keystore (Y, n)"
read PINATA_YN
if [ $PINATA_YN = 'Y' ]
then
    echo "Deleting old keystore, if there is one..."
    sudo rm /usr/share/$PINATA_TOMCAT_VERSION/.keystore

    echo "Generating keystore..."
    sudo keytool -noprompt -storepass pinatasmash -keypass pinatasmash -genkey -alias tomcat -keystore /usr/share/$PINATA_TOMCAT_VERSION/.keystore -keyalg RSA
else
    echo "Skipping keystore generation..."
fi

# Prompt for MySQL root user and username.
echo "Wipe and (re)install database ?"
echo "CAUTION: you will lose all server data!"
echo "Proceed (Y, n)"
read PINATA_YN
if [ $PINATA_YN = 'Y' ]
then
    
    echo "MySQL username ?"
    read PINATA_MYSQL_USER
    echo "MySQL password ?"
    read PINATA_MYSQL_PASS

    # Querying install access point on server.
    # If setup correctly, server should now perform all DB and table setup.
    echo "Trying to contact PinataAPI Server setup endpoint..."
    curl -H "Content-Type: application/json" -d '{"root_user":"'$PINATA_MYSQL_USER'","root_pass":"'$PINATA_MYSQL_PASS'"}' "http://localhost:8080/api/install"
    echo
fi

sudo service $PINATA_TOMCAT_VERSION restart

echo "Finshed."
echo "***REMEMBER*** Remove outside access to root MySQL user."
