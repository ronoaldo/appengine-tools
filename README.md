# AppEngine Tools

This is a simple project that allows you to manage
an AppEngine application from the command line.

## Installation

At the moment, you must build from sources to have a
executable jar:

	hg clone https://bitbucket.org/ronoaldo/appengine-tools
	mvn clean package

## Running

After building it, you can start the tool with:

	java -jar target/appengine-cli.jar --help
	.
	App Engine Tools - CLI interface
	.
	Usage: appengine-cli COMMAND [OPTIONS]
	.
	Where COMMAND can be one of:
		console : starts an interactive shell session
		dump : export/import the local datastore

You can then use the console command to connect to
your local or production instance, and execute java
commands into it.

	java -jar target/appengine-cli.jar --appid=myappid

You can also use the dump command, to export your local
datastore as a Yaml backup, or to load it back again.

	java -jar target/appengine-cli.jar dump > backup.yaml
	java -jar target/appengine-cli.jar dump --load backup.yaml

## Fixtures for local development

One usefull aspect of the ```dump``` command is that it can be used
to generate local fixtures and store a local state of your entities.

The format of the yaml document is like:

	--- !entity
	key: [ AncestorKind, 1, EntityKind 2 ]
	properties: {
	  property1: 'Value 1'
	  property2: 234
