package net.ronoaldo.code.appenginetools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import bsh.EvalError;
import bsh.Interpreter;

public class Console {

	public static final String PATH = "/_ah/remote_api";

	public RemoteApiHelper helper;
	public OptionSet opt;

	private void run(String[] args) {
		parseCommandLineOptions(args);
		configureRemoteApiHelper();

		try {
			helper.connect();
			shell();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helper.disconnect();
		}
	}

	private void configureRemoteApiHelper() {
		if (opt.has("remote")) {
			helper = new RemoteApiHelper(String.format(
					"https://%s.appspot.com:%s%s", opt.valueOf("appid"), 443,
					opt.valueOf("path")));
		} else {
			helper = new RemoteApiHelper(String.format(
					"http://localhost:8888%s", opt.valueOf("path")));
		}
	}

	private void parseCommandLineOptions(String[] args) {
		opt = getParser().parse(args);
		if (opt.has("h")) {
			try {
				getParser().printHelpOn(System.out);
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	private void shell() throws EvalError {
		Interpreter shell = new Interpreter(new InputStreamReader(System.in),
				System.out, System.err, true);

		shell.set("bsh.prompt",
				String.format("appengine@%s: %% ", opt.valueOf("appid")));
		sourceDefaults(shell);

		if (opt.has("f")) {
			source(shell, (String) opt.valueOf("f"));
		} else {
			shell.run();
		}
	}

	private void sourceDefaults(Interpreter shell) {
		source(shell, FileUtils.atHomeDir(".gaeconsolerc").getAbsolutePath());
		source(shell, ".gaeconsolerc");
	}

	private void source(Interpreter shell, String path) {
		File f = new File(path);
		if (f.exists()) {
			try {
				shell.source(f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private OptionParser getParser() {
		OptionParser parser = new OptionParser();
		parser.accepts("appid", "Application ID").withRequiredArg()
				.ofType(String.class);
		parser.accepts("remote", "Connect with appspot.com");
		parser.accepts("path", "Remote API handler path").withRequiredArg()
				.ofType(String.class).defaultsTo(PATH);
		parser.acceptsAll(Arrays.asList("f", "file"),
				"Runs the specified script and exists.").withRequiredArg()
				.ofType(String.class).describedAs("file");
		parser.acceptsAll(Arrays.asList("?", "h", "help"),
				"Print help and exit");
		parser.accepts("gui");
		return parser;
	}

	public static void main(String[] args) {
		Console console = new Console();
		console.run(args);
	}
}
