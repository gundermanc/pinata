#!/bin/sh

# Pinata API Database Install Script
# (C) 2015 Christian Gunderman

PINATA_MYSQL_DB=PinataAPI
PINATA_TOMCAT_USER=tomcat

echo **MAKE SURE YOU INSTALL MySQL and other deps. first***
echo Installing DB...

# Link to MySQL Connector
echo "Create mysql-connector link ? You only need do this once (Y,n)"
read PINATA_YN
if [ $PINATA_YN == "Y" ]
then
    echo Creating mysql-connector link...
    sudo ln -rs /usr/share/java/mysql.jar /usr/share/tomcat8/lib/mysql.jar
else
    echo Skipping mysql-connector link...
fi

# Prompt for MySQL root user and username.
echo MySQL username ?
read PINATA_MYSQL_USER
echo MySQL password ?
read PINATA_MYSQL_PASS

# Querying install access point on server.
# If setup correctly, server should now perform all DB and table setup.
echo Trying to contact PinataAPI Server setup endpoint...
curl -H "Content-Type: application/json" -d '{"root_user":"'$PINATA_MYSQL_USER'","root_pass":"'$PINATA_MYSQL_PASS'"}' http://localhost:8080/api/install
echo

echo Finshed.
echo ***REMEMBER*** Remove outside access to root MySQL user.
