#!/usr/bin/env bash

sh publish-lib.sh $@ && sleep 10 && sh publish-livedata.sh $@ && sh publish-rxjava2.sh $@
