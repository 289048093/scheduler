#!/bin/sh

source /etc/profile

JAVA_OPTS=" -server -Xmx1g -Xms1g -Xmn128m -XX:PermSize=256m -Xss256k"

cd $(dirname "$0")

ROOT_PATH=$(cd ..; pwd)
echo root path: $ROOT_PATH
TMP_DIR=$ROOT_PATH/tmp
SCHEDULER_PID=$TMP_DIR/pid
MAIN_CLASS=com.mokylin.gm.scheduler.Main
LOG_DIR=$ROOT_PATH/logs
STDOUT_LOG=$LOG_DIR/stdout.log

if [ -e $SCHEDULER_PID ]; then
     ps -p `cat $SCHEDULER_PID`>/dev/null
    if [ $? -eq 0 ]; then
        echo "scheduler is aready running !!!"
        exit 1
    fi
fi

if [ ! -d $LOG_DIR ]; then
    mkdir $LOG_DIR
fi

# class path
CLASS_PATH=.:$JAVA_HOME/lib/tools.jar

for jar in $ROOT_PATH/lib/*.jar; do
  CLASS_PATH=$CLASS_PATH:$jar
done

echo $CLASS_PATH

cd $ROOT_PATH/lib
#startup
echo "nohup java $JAVA_OPTS -classpath $CLASS_PATH -Dscheduler.root=$ROOT_PATH $MAIN_CLASS >$STDOUT_LOG 2>&1 &"
nohup java $JAVA_OPTS -classpath $CLASS_PATH -Dscheduler.root=$ROOT_PATH $MAIN_CLASS >$STDOUT_LOG 2>&1 &

echo

if [ $? -eq 0 ]; then

    echo pid:$!


    if [ ! -d $TMP_DIR ]; then
        mkdir $TMP_DIR
    fi

    echo $! > $SCHEDULER_PID
    echo "scheduler startup success."
else
    echo "startup failure!!! bye!"
fi
echo ""