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

public class RemoteApiHelper {

	private static final Logger logger = Logger.getLogger(RemoteApiHelper.class.getName());
	
	class Credentials {
		String username;
		String password;

		Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}

	private URL url;
	private RemoteApiInstaller remoteApi;

	public RemoteApiHelper(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public void connect() {
		logger.info("Connecting with " + url.getHost() +", port " + url.getPort() + ", path=" + url.getPath());
		
		Credentials c = getCredentials();
		
		RemoteApiOptions options = new RemoteApiOptions().server(url.getHost(),
				url.getPort()).remoteApiPath(url.getPath()).credentials(c.username, c.password);

		remoteApi = new RemoteApiInstaller();
		try {
			remoteApi.install(options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void disconnect() {
		try {
			remoteApi.uninstall();
		} catch (Exception e) {
			logger.info("Unable to uninstall Remote API: " + e.getMessage());
		}
	}

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
