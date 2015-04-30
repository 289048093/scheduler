#!/bin/sh

cd $(dirname "$0")
PID_FILE=../tmp/pid
echo "kill pid: `cat $PID_FILE`"
kill -9 `cat $PID_FILE`
if [ $? -eq 0 ]; then
    rm -rf $PID_FILE
    echo "shutdown success"
else
    echo "shutdown failure!!!please sure have the user premission"
fi
