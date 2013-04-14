//
//   Copyright 2012 Ronoaldo JLP
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
package net.ronoaldo.code.appenginetools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * Interactive console to manage your Google AppEngine application.
 * 
 * <p>
 * This is a simple console app that allows you to manage your AppEngine
 * application from a command prompt. It also has support to run scripts in
 * BeanShell language.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 * @see <a href="http://www.beanshell.org/">The BeanShell home page.</a>
 * @see <a href="http://jline.sourceforge.net/">JLine home page.</a>
 */
public class Console {

	public static final String PATH = "/_ah/remote_api";

	public RemoteApiHelper helper;
	public OptionSet opt;

	private void run(String[] args) {
		if (!parseCommandLineOptions(args)) {
			return;
		}
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

	private boolean parseCommandLineOptions(String[] args) {
		opt = getParser().parse(args);
		if (opt.has("h")) {
			try {
				getParser().printHelpOn(System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
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
		parser.accepts("remote", "Connect with production app at *.appspot.com");
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

	/**
	 * Run a {@link Console} session, parsing command-line arguments and
	 * conecting with the AppEngine instance locally or remotely.
	 * 
	 * @param args
	 *            the command line options.
	 */
	public static void main(String[] args) {
		Console console = new Console();
		console.run(args);
	}
}
