# AppEngine Tools

This is a simple project that allows you to manage
an AppEngine application from the command line.

## Installation

At the moment, you must build from sources to have a
executable jar:

	hg clone https://bitbucket.org/ronoaldo/appengine-tools
	mvn clean package

## Running

After building it, you can see the comand help with the --help flag:

	$ java -jar target/appengine-cli.jar --help
	
	App Engine Tools - CLI interface
	
	Usage: appengine-cli COMMAND [OPTIONS]
	
	Where COMMAND can be one of:
		console : starts an interactive shell session
		dump : export/import the local datastore

### Remote API Console

You can then use the console command to connect to
your local or production instance, and execute java
commands into it.

	$ java -jar target/appengine-cli.jar console --appid=myappid

Note: this is not a remote shell, like ssh. You're running code
in your machine, delegating AppEngine RPCs to your production
application Datastore. Read the AppEngine datastore documentation
on Remote API form more details.

### DevAppServer Datastore Dump Tool

You can also use the dump command, to export your local
datastore as a Yaml backup, or to load it back again.

	$ java -jar target/appengine-cli.jar dump > backup.yaml
	$ java -jar target/appengine-cli.jar dump --load backup.yaml

Note: This tool will download *all* your datastore data using the
RemoteAPI connection. This is not a backup tool! If your dataset
is very small, you can do backups of it, but not ALL data is backed
up: some property value types are not handled properly. To make
restorable backups, use the Backup Tool from Datastore Admin console.

## Fixtures for local development

One usefull aspect of the `dump` command is that it can be used
to generate local fixtures and store a local state of your entities,
in a human readable, reproducible and editable way.

The format of the `Entity` Yaml document is pretty simple:

	--- !entity
	key: [Profile, 1]
	properties:
	  activeProfile: true
	  age: 27
	  height: 1.87
	  name: John Doe
	  preferences.cars: [Porshe, Camaro, Mustang]
	  preferences.fruits: [Apple, Orange, Lemon]
	  registrationDate: 2014-02-10T21:34:45.085Z
	  system.mixedList: [public, 12, true, 2.0]
	  system.mixedSet: !!set {2: null, 3: null, one: null}

There is a basic support for some custom types that has special meaning
`Strings` are 500 byte limited character sequences, when stored in the
Datastore, and are serialied as Yaml `!!str` scalars. `Text` values are
stored with the custom tag `!text`:

	--- !entity
	key: [TextEntity, key-name]
	properties:
	  long-string: !text |-
	    This is a long text string
	  short-string: This is a simple short string

`Shortblobs` are `!!binary` (byte[]) yaml arrays when serialized, and
Blob objects are tagged with `!blob`:

	--- !entity
	key: [ComplextBlobs, blob-holder-1]
	properties:
	  long-blob: !blob |-
	    TG9uZ0Jsb2I=
	  short-blob: !!binary |-
	    U2hvcnRCbG9i

