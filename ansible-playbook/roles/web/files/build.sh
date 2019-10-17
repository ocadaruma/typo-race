#!/bin/bash

set -eu

export JAVA_HOME="/opt/AdoptOpenJDK/jdk-11.0.4+11"
export BASE_URL="//typo-race.mayreh.com/api"

cd /home/ocadaruma/typo-race

./gradlew :web:nuxtGenerate
./gradlew :web:build

cp web/build/libs/web-0.1.0-SNAPSHOT.jar /home/ocadaruma/deploy/web-0.1.0-SNAPSHOT.jar
