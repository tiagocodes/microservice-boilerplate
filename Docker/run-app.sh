#!/bin/bash

    . ./run-variables.sh

    export MILIS_START=$(date +%s)
    mkdir $CONTEXT/logs/$MILIS_START

    CONTEXT_PATH=`realpath $CONTEXT`

    for (( i=1; i<=$INSTANCES; i++ )); do

        export PORT=$(($BASE_PORT+$i))

        docker run -d -t \
            -l Puerto=$PORT \
            --name app-$i \
            --mount type=bind,source="$CONTEXT_PATH/logs",target=/trazas \
            --network host \
            --env 'MILIS_START' \
            --env 'PORT' \
            app:1.0.0
    done

    docker ps
