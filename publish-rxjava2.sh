#!/usr/bin/env bash

./gradlew clean :library-rxjava2:build :library-rxjava2:bintrayUpload $@
