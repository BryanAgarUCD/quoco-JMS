#!/bin/bash

cd ./core
mvn compile
mvn package
mvn install

cd ../auldfellas
mvn compile
mvn package

cd ../girlpower
mvn compile
mvn package

cd ../dodgydrivers
mvn compile
mvn package

cd ../broker
mvn compile
mvn package

cd ../client
mvn compile
mvn package

systemctl start docker
docker pull rmohr/activemq