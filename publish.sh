#!/usr/bin/env bash

sh ./gradlew clean :library:build :library:bintrayUpload $@
sh ./gradlew clean :library-livedata:build :library-livedata:bintrayUpload $@
sh ./gradlew clean :library-rxjava2:build :library-rxjava2:bintrayUpload $@
