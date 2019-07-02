#!/usr/bin/env bash

./gradlew clean :library-livedata:build :library-livedata:bintrayUpload $@
