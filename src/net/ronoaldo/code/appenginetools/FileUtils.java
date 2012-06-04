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

}
