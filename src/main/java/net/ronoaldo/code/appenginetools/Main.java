package net.ronoaldo.code.appenginetools;

import java.io.StringWriter;

/**
 * Main class that interprets the subcommands and run them.
 */
public class Main {

	public static void help() {
		StringWriter sw = new StringWriter();
		sw.write("App Engine Tools - CLI interface\n\n");

		sw.write("Usage: appengine-cli COMMAND [OPTIONS]\n\n");

		sw.write("Where COMMAND can be one of:\n");
		sw.write("\tconsole : starts an interactive shell session\n");
		sw.write("\tdump : export/import the local datastore\n");

		System.out.println(sw.toString());
	}

	public static void main (String [] args) {
		if (args.length == 0) {
			help();
			return;
		}

		// Consumes the command
		String command = args[0];
		String[] commandArgs = new String[args.length-1];
		System.arraycopy(args, 1, commandArgs, 0, args.length-1);

		if (command != null && !command.isEmpty()) {
			switch (command.toLowerCase()) {
				case "console":
					Console.main(commandArgs);
					break;
				case "dump":
					Dump.main(commandArgs);
					break;
				default:
					help();
			}
		} else {
			help();
		}
	}

}
