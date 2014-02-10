// Copyright 2014 Ronoaldo JLP (http://www.ronoaldo.net)
// 
//   Licensed under the Apache License, Version 2.0 (the 'License');
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an 'AS IS' BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package net.ronoaldo.code.appenginetools;

import java.io.IOException;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Abstract class to be used to build a custom App Engine
 * Remote API CLI.
 *
 * Use this as your base class to implement a custom command
 * to manage the local development server, or a production app.
 */
public abstract class AbstractCliApp  {

	public static final String PATH = "/_ah/remote_api";

	/**
	 * The RemoteApiHelper instance, used to configura access
	 * to the AppEngine Remote API.
	 */
	protected RemoteApiHelper helper;

	/**
	 * Parsed options and arguments from the command line.
	 */
	protected OptionSet opt;

	/**
	 * This is the entry-point for a CLI app, that implementors
	 * supply. This code is executed after the Remote API initializations
	 * takes place, and the options from the command line are available
	 * in the {@link opt} variable.
	 */
	public abstract void run();

	/**
	 * This is the main function you must call from the main Method.
	 *
	 * This is a wrapper that will call the {@link run} method, after
	 * option parsing and Remote API initialization.
	 */
	public void runFromMain(String[] args) {
		if (!parseCommandLineOptions(args)) {
			return;
		}
		configureRemoteApiHelper();

		try {
			helper.connect();
			run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helper.disconnect();
		}
	}

	private void configureRemoteApiHelper() {
		StringBuilder url = new StringBuilder("");
		if (opt.has("appid")) {
			// If appid was informed, connects with production app,
			// over SSL, using the appspot.com domain.
			url.append("https://")
				.append(String.format("%s.appspot.com", opt.valueOf("appid")))
				.append(":443");
		} else {
			url.append("http://")
				.append(opt.valueOf("host"))
				.append(":")
				.append(opt.valueOf("port").toString());
		}
		
		// Set the path
		url.append(opt.valueOf("path"));

		helper = new RemoteApiHelper(url.toString());
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

	/**
	 * This is the default common set of options.
	 *
	 * Implementations  can override this methos to add more
	 * options to the CLI, but the original options have a special
	 * meaning, and must be kept:
	 *
	 * <ol>
	 * <li>appid</li>
	 * <li>remote</li>
	 * <li>path</li>
	 * <li>help</li>
	 * </ol>
	 */
	protected OptionParser getParser() {
		OptionParser parser = new OptionParser();
		parser.accepts("host", "Hostname to connect with")
			.withRequiredArg()
			.ofType(String.class).
			defaultsTo("localhost");
		parser.accepts("port", "Port to connect with")
			.withRequiredArg()
			.ofType(Integer.class)
			.defaultsTo(8888);
		parser.accepts("appid", "Application ID to connects with." +
				" Overrides host and port arguments.")
			.withRequiredArg()
			.ofType(String.class);
		parser.accepts("path", "Remote API handler path")
			.withRequiredArg()
			.ofType(String.class)
			.defaultsTo(PATH);
		parser.acceptsAll(Arrays.asList("?", "h", "help"), "Print help and exit");
		return parser;
	}
}
