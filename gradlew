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

# Resolve symlinks
while [ -h "$0" ]; do
  ls=$(ls -ld "$0")
  link=$(expr "$ls" : '.*-> .*$')
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT="$(dirname "$0")/$link"
  fi
done

SCRIPT_DIR=$(cd "$(dirname "$SCRIPT")" && pwd)
GRADLE_WRAPPER_PROPERTIES="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.properties"

# Read Gradle distribution URL
DISTRIBUTION_URL=$(grep distributionUrl "$GRADLE_WRAPPER_PROPERTIES" | sed 's/.*=//')

# Download if missing
ZIP_NAME=$(basename "$DISTRIBUTION_URL")
GRADLE_DIR="$SCRIPT_DIR/.gradle-wrapper"
GRADLE_ZIP="$GRADLE_DIR/$ZIP_NAME"
GRADLE_UNZIP_DIR="$GRADLE_DIR/unzip"

mkdir -p "$GRADLE_DIR"

if [ ! -f "$GRADLE_ZIP" ]; then
  echo "Downloading $DISTRIBUTION_URL"
  curl -L "$DISTRIBUTION_URL" -o "$GRADLE_ZIP"
fi

if [ ! -d "$GRADLE_UNZIP_DIR" ]; then
  unzip -q "$GRADLE_ZIP" -d "$GRADLE_UNZIP_DIR"
fi

GRADLE_BIN=$(find "$GRADLE_UNZIP_DIR" -name gradle | grep /bin/gradle$)

exec "$GRADLE_BIN" "$@"
