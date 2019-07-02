#!/usr/bin/env bash

sh publish-lib.sh $@ && sh publish-livedata.sh $@ && sh publish-rxjava2.sh $@
