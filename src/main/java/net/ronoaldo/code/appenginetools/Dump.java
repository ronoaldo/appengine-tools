package net.ronoaldo.code.appenginetools;

import java.util.List;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import joptsimple.OptionParser;

import net.ronoaldo.code.appenginetools.fixtures.EntitySerializer;
import net.ronoaldo.code.appenginetools.fixtures.Fixture;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

/**
 * Local development and unit test datastore utility. 
 * 
 * Dumps the entire datastore entities as Yaml.
 *
 * This is usefull to have a one-off backup of your local
 * datastore in a human readable format.
 *
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class Dump extends AbstractCliApp {

	@SuppressWarnings("unchecked")
	public void run() {
		// If --load, load all files as fixtures
		if (opt.has("load")) {
			List<String> fixtures = (List<String>) opt.valuesOf("load");
			for (String f : fixtures) {
				try {
					File file = new File(f);
					FileInputStream is = new FileInputStream(file);
					Fixture.load(is);
				} catch (FileNotFoundException e) {
					System.err.println("File " + f + " not found");
				}
			}
		} else {
			Query q = new Query();
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Iterable<Entity> entities = ds.prepare(q).asIterable();
			EntitySerializer s = new EntitySerializer();
			System.out.println(s.serialize(entities.iterator()));
		}
	}

	@Override
	protected OptionParser getParser() {
		OptionParser parser = super.getParser();
		parser.accepts("load", "Load a fixture file. Can be used multiple times.")
			.withRequiredArg()
			.ofType(String.class);
		return parser;
	}

	public static void main (String [] args) {
		Dump dump = new Dump();
		dump.runFromMain(args);
	}
}
