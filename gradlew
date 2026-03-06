#!/bin/sh
# Gradle wrapper script — standard Gradle 8.7 gradlew
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

die () { echo; echo "$*"; echo; exit 1; } >&2

# Find java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
    [ -x "$JAVACMD" ] || die "JAVA_HOME ($JAVA_HOME) is invalid"
else
    JAVACMD="java"
    command -v java >/dev/null 2>&1 || die "No java found in PATH and JAVA_HOME not set"
fi

# Increase file descriptors
MAX_FD=maximum
case "$(uname)" in
  Linux*)
    MAX_FD=$(ulimit -H -n 2>/dev/null || echo "")
    [ -n "$MAX_FD" ] && ulimit -n "$MAX_FD" 2>/dev/null || true
    ;;
esac

exec "$JAVACMD" \
  $DEFAULT_JVM_OPTS \
  $JAVA_OPTS \
  $GRADLE_OPTS \
  "-Dorg.gradle.appname=$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
