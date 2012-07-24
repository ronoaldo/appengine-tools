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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Filesystem utility functions.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class FileUtils {

	/**
	 * Creates a new {@link File} prepending the user home directory to the
	 * informed filename.
	 * 
	 * @param name
	 *            the filename to be located at the user home directory.
	 * @return the {@link File} object. Note that the file existence must be
	 *         checked using {@link File#exists()}.
	 */
	public static File atHomeDir(String name) {
		File f = new File(System.getProperty("user.home")
				.concat(System.getProperty("file.separator")).concat(name));
		return f;
	}

	/**
	 * Load a file contents as a String.
	 * 
	 * @param file
	 *            the file to be loaded.
	 * @return the file contents as a String.
	 */
	public static String loadFileContents(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			StringWriter sw = new StringWriter();
			String line;
			while ((line = reader.readLine()) != null) {
				sw.write(line);
			}
			reader.close();

			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveFileContents(File file, String contents) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(contents);
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves the file contents to a file.
	 * 
	 * @param filename
	 *            the filename.
	 * @param contents
	 *            the file contents.
	 */
	public static void saveFileContents(String filename, String contents) {
		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			saveFileContents(file, contents);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
