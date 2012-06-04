#!/bin/bash
#
#   Copyright 2012 Ronoaldo JLP
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#

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
