#!/bin/bash

    if [ ! -f logs ]; then
        mkdir logs
    fi

    cp ../target/prueba-1.0.0-SNAPSHOT.jar .

    docker build --network host --tag base-image:1.0 .

    docker build --network host -f DockerJava --tag dockerjava:1.11 .

    docker build --network host -f DockerApp --tag app:1.0.0 .
