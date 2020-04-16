#!/bin/bash

    . ./run-variables.sh

    for (( i=1; i<=$INSTANCES; i++ ))
    do
        docker exec -ti app-$i bash -c "pkill -2 java"
        docker kill app-$i
    done

    docker rm $(docker ps -a -q)
