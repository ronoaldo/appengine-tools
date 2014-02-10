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
public class Console extends AbstractCliApp {

	@Override
	protected OptionParser getParser() {
		OptionParser parser = super.getParser();
		parser.acceptsAll(Arrays.asList("f", "file"),
				"Runs the specified script and exists")
			.withRequiredArg()
			.ofType(String.class)
			.describedAs("file");
		return parser;
	}

	@Override
	public void run() {
		Interpreter shell = new Interpreter(
			new InputStreamReader(System.in),
			System.out, System.err, true
		);

		try {
			String prompt = String.format("appengine@%s: %% ", opt.valueOf("appid"));
			shell.set("bsh.prompt", prompt);
			sourceDefaults(shell);

			if (opt.has("f")) {
				source(shell, (String) opt.valueOf("f"));
			} else {
				shell.run();
			}
		} catch (EvalError e ) {
			e.printStackTrace();
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

	/**
	 * Run a {@link Console} session, parsing command-line arguments and
	 * conecting with the AppEngine instance locally or remotely.
	 * 
	 * @param args
	 *            the command line options.
	 */
	public static void main(String[] args) {
		Console console = new Console();
		console.runFromMain(args);
	}
}
