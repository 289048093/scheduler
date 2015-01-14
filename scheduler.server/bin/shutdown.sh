#!/bin/sh

cd $(dirname "$0")
PID_FILE=../tmp/pid
echo "kill pid: `cat $PID_FILE`"
kill -9 `cat $PID_FILE`
rm -rf $PID_FILE