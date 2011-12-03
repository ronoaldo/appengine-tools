#!/bin/bash

basedir="$(dirname $0)"
basedir="$(readlink -f $basedir)"

classpath="bin:src"
for jar in $basedir/lib/**/*.jar; do
	classpath="${classpath}:${jar}"
done

java -cp "$classpath" net.ronoaldo.code.appenginetools.Console $*
