#!/bin/sh

basedir="$(dirname $0)"
basedir="$(readlink -f $basedir)"

CLASSPATH="${CLASSPATH:-.}:bin:src"
for jar in $basedir/lib/**/*.jar; do
	CLASSPATH="${CLASSPATH}:${jar}"
done
export CLASSPATH

exec java net.ronoaldo.code.appenginetools.Console "$@"