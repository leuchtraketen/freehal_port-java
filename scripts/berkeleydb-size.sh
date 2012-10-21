#!/bin/bash

echo "Please provide the directory containing the berkeley database as first argument!"

java -jar libs/je-5.0.58.jar DbSpace -h "$1"
