#!/bin/sh

source /etc/profile

#JAVA_OPTS=

cd $(dirname "$0")

ROOT_PATH=../
TMP_DIR=$ROOT_PATH/tmp
SCHEDULER_PID=$TMP_DIR/pid
MAIN_CLASS=com.mokylin.gm.scheduler.Main

if [ -e $SCHEDULER_PID ]; then
    echo "scheduler is aready running !!!"
    exit 1
fi

# class path
CLASS_PATH=.:$JAVA_HOME/lib/tools.jar

for jar in $ROOT_PATH/lib/*.jar; do
  CLASS_PATH=$CLASS_PATH:$jar
done

echo $CLASS_PATH


cd $ROOT_PATH/lib
#startup
java $JAVA_OPTS -classpath $CLASS_PATH $MAIN_CLASS &

echo pid:$!


if [ ! -d $TMP_DIR ]; then
    mkdir $TMP_DIR
fi

echo $! > $TMP_DIR/pid
