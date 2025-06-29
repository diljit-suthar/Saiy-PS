#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Resolve symbolic links
while [ -h "$0" ]; do
  ls=$(ls -ld "$0")
  link=$(expr "$ls" : '.*-> .*$')
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT="$(dirname "$0")/$link"
  fi
done

DIR=$(cd "$(dirname "$0")" && pwd)

GRADLE_HOME=$DIR/gradle/wrapper

exec "$DIR/gradle/wrapper/gradle-wrapper" "$@"
