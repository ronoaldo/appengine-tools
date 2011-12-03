package net.ronoaldo.code.appenginetools;

import java.io.File;

public class FileUtils {

	public static File atHomeDir(String name) {
		File f = new File(System.getProperty("user.home")
				.concat(System.getProperty("file.separator")).concat(name));
		return f;
	}

}
