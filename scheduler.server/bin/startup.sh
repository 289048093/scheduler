#! /bin/sh
#JAVA_OPTS=

ROOT_PATH=../

# class path
CLASS_PATH=.:"$JAVA_HOME"/lib/tools.jar

for jar in $ROOT_PATH/lib/*.jar; do
  CLASS_PATH=$CLASS_PATH:$jar
done

MAIN_CLASS=com.mokylin.gm.scheduler.Main

#startup
java $JAVA_OPTS -classpath $CLASS_PATH -Duser.dir=$DIR $SYSTEM_PROPERTY $MAIN_CLASS