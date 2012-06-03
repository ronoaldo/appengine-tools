#!/bin/sh

# Working directories
basedir="$(dirname $0)"
basedir="$(readlink -f $basedir)"

# Classpath support
CLASSPATH="${CLASSPATH:-.}:bin:src"
for jar in $basedir/lib/**/*.jar; do
	CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH

exec -a appengine-console java jline.ConsoleRunner net.ronoaldo.code.appenginetools.Console "$@"
