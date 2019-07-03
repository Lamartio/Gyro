#!/usr/bin/env bash

sh publish-lib.sh $@

for i in 1 2 3 4 5 6 7 8 9 10
do
   echo .
   sleep 1
done

sh publish-livedata.sh $@
sh publish-rxjava2.sh $@
