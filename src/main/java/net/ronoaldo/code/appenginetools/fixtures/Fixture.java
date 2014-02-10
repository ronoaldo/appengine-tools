package net.ronoaldo.code.appenginetools.fixtures;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

/**
 * Simple utility to load yaml data into the AppEngine
 * datastore.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class Fixture {

	private static Logger logger = Logger.getLogger(Fixture.class.getName());

	/**
	 * Lookup the classpath for a .yaml file, named like the
	 * specified Class.
	 *
	 * <p>For a class with name com.mypackage.Class, it will try
	 * to load a file at /com/mypackage/Class.yaml in the classpath.
	 * 
	 * @param clazz the Class to lookup for a resource name.
	 */
	public static void load(Class<?> clazz) {
		load(clazz.getPackage(), clazz.getSimpleName().concat(".yaml"));
	}

	/**
	 * Looup the classpath for a resource relative to the specified
	 * package.
	 * 
	 * @param pkg
	 *            the package where the resource can be found.
	 * @param resourceName
	 *            the resource name.
	 */
	public static void load(Package pkg, String resourceName) {
		String template = "/%s/%s";
		load(String.format(template, //
				pkg.getName().replaceAll("[.]", "/"), resourceName));
	}

	/**
	 * Looads a Ymal fixture from the specified path in the classpath.
	 * 
	 * @param resourcePath
	 *            o caminho no classpath onde está localizado o recurso.
	 */
	public static void load(String resourcePath) {
		logger.info(String.format("Loading '%s'", resourcePath));
		InputStream is = Fixture.class.getClass()//
				.getResourceAsStream(resourcePath);
		load(is);
	}

	/**
	 * Loads a fixture from the specified InputStream.
	 */
	public static void load(InputStream is) {
		EntitySerializer s = new EntitySerializer();
		Iterator<Entity> i = s.deserialize(loadResourceAsString(is));
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		while (i.hasNext()) {
			Entity entity = i.next();
			try {
				ds.get(ds.put(entity));
			} catch (EntityNotFoundException e) {
				throw new IllegalStateException(
						"Get after put failed for entity: " + entity);
			}
		}

	}

	/**
	 * Load the specified input stream as a resource.
	 * 
	 * <p>Load the InputStream data with UTF-8.
	 *
	 * @param is the InputStream to read data from.
	 * @return the loaded data as string.
	 */
	private static String loadResourceAsString(InputStream is) {
		StringWriter sw = new StringWriter();
		try {
			IOUtils.copy(is, sw, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sw.toString();
	}
}
