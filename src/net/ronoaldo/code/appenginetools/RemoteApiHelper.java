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
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.apphosting.api.ApiProxy;

/**
 * Google AppEngine Remote API helper class.
 * 
 * <p>
 * Just a few helper functionality to load previously stored user credentials
 * from configuration file.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class RemoteApiHelper {

	private static final Logger logger = Logger.getLogger(RemoteApiHelper.class
			.getName());

	/**
	 * Class that holds the username and password to connect with AppEngine.
	 * 
	 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
	 */
	class Credentials {

		/**
		 * Google account username.
		 */
		String username;

		/**
		 * Clear text Google account password.
		 */
		String password;

		/**
		 * Creates a new Credentials object with the informed username and
		 * password.
		 * 
		 * @param username
		 *            the username.
		 * @param password
		 *            the password.
		 */
		Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}

	private URL url;

	private RemoteApiInstaller remoteApi;

	/**
	 * Creates a new helper instance to connect with the specified URL.
	 * 
	 * @param url
	 *            the URL to be used to connect with the Google AppEngine Remote
	 *            API.
	 */
	public RemoteApiHelper(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Start the connection, and perform the {@link ApiProxy} setup for the
	 * current {@link Thread}.
	 */
	public void connect() {
		logger.info("Connecting with " + url.getHost() + ", port "
				+ url.getPort() + ", path=" + url.getPath());

		Credentials c = getCredentials();

		RemoteApiOptions options = new RemoteApiOptions()
				.server(url.getHost(), url.getPort())
				.remoteApiPath(url.getPath())
				.credentials(c.username, c.password);

		remoteApi = new RemoteApiInstaller();
		try {
			remoteApi.install(options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uninstall the Remote API proxy delegate.
	 */
	public void disconnect() {
		try {
			remoteApi.uninstall();
		} catch (Exception e) {
			logger.info("Unable to uninstall Remote API: " + e.getMessage());
		}
	}

	/**
	 * Retrieve the user {@link Credentials} to connect with the AppEngine
	 * Remote API.
	 * 
	 * <p>
	 * Currently, try to load a file from $HOME/.remoteapi.properties, a
	 * {@link Properties} file that is suposed to store the email and password
	 * for your Google Account. If this file is not found, ask for user
	 * credentials from the command-line.
	 * 
	 * @return the informed user {@link Credentials}.
	 */
	protected Credentials getCredentials() {
		File f = FileUtils.atHomeDir(".remoteapi.properties");
		if (f.exists()) {
			Properties p = new Properties();
			try {
				p.load(new FileReader(f));
				return new Credentials(p.getProperty("email"),
						p.getProperty("password"));
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			String email = new String(System.console().readLine("Email: "));
			String password = new String(System.console().readPassword(
					"Password: "));
			return new Credentials(email, password);
		}
	}
}
