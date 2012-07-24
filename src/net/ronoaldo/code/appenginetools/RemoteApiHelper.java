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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.appengine.repackaged.org.apache.commons.httpclient.Credentials;
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

		RemoteApiOptions options = new RemoteApiOptions().server(//
				url.getHost(), url.getPort()).remoteApiPath(url.getPath());

		Properties credentials = getCredentials();

		// If we have a previous session, reuse it:
		if (credentials.containsKey("host")) {
			options.credentials(//
					credentials.getProperty("email"), serialize(credentials));
		} else {
			options.credentials(credentials.getProperty("email"),
					credentials.getProperty("password"));
		}

		remoteApi = new RemoteApiInstaller();
		try {
			remoteApi.install(options);
			storeCredentials(remoteApi.serializeCredentials());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String serialize(Properties credentials) {
		StringWriter sw = new StringWriter();
		credentials.list(new PrintWriter(sw));
		return sw.toString();
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
	protected Properties getCredentials() {
		Properties p = new Properties();
		File f = FileUtils.atHomeDir(".remoteapi.properties");
		if (f.exists()) {
			try {
				p.load(new FileReader(f));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			String email = new String(System.console().readLine("Email: "));
			String password = new String(System.console().readPassword(
					"Password: "));
			p.setProperty("email", email);
			p.setProperty("password", password);
		}
		return p;
	}

	/**
	 * Stores the current file credentials.
	 * 
	 * @param credentials
	 *            the credentials to be stored.
	 */
	protected void storeCredentials(String credentials) {
		FileUtils.saveFileContents(
				FileUtils.atHomeDir(".remoteapi.properties"), credentials);
	}
}
