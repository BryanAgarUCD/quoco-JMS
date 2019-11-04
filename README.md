### Introduction
This is 16206781 Yuehao Sui's answer of the UCD module COMP30220 Lab 5.

### How to use
1. run build shell script
`sh ./build.sh`
    > 1. The script will start daemon, it may ask your root permession
    > 2. The script will pull the necessary docker image, you may need login docker hub firstly.
2. run docker-compose
`docker-compose up`
3. To check the result, to see the logout of the client container.

### services' port
+ ActiveMQ 8161, 61616
+ To check is activemq work fine, try to login the admin dashboard `localhost:8161/admin/`, the default username and passport are `admin` and `admin`