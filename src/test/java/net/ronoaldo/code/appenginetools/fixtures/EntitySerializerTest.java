package net.ronoaldo.code.appenginetools.fixtures;

import static java.lang.String.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.blobstore.BlobKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;

public class EntitySerializerTest {

	Environment e = new Environment().ds().consistent();

	@Before
	public void setUp() {
		e.setUp();
	}

	@After
	public void tearDown() {
		e.tearDown();
	}

	@Test
	public void testSerializeStringAndText() throws Exception {
		Entity e = new Entity("ComplexEntity", "key-name");
		e.setProperty("short-string", "This is a simple short string");
		e.setProperty("long-string", new Text("This is a long text string"));

		EntitySerializer s = new EntitySerializer();
		String yaml = s.serialize(asList(e).iterator());
		System.out.println("Yaml:\n" + yaml);

		Entity d = s.deserialize(yaml).next();
		assertEquals(e.getProperty("short-string"), d.getProperty("short-string"));
		assertEquals(e.getProperty("long-string"), d.getProperty("long-string"));
	}

	@Test
	public void testSerializeLongAndShortBlobs() throws Exception {
		Entity e = new Entity("ComplextBlobs", "blob-holder-1");
		e.setProperty("short-blob", new ShortBlob("ShortBlob".getBytes()));
		e.setProperty("long-blob", new Blob("LongBlob".getBytes()));

		EntitySerializer s = new EntitySerializer();
		String yaml = s.serialize(asList(e).iterator());
		System.out.println("Yaml:\n" + yaml);

		Entity d = s.deserialize(yaml).next();
		assertEquals(e.getProperty("short-blob"), d.getProperty("short-blob"));
		assertEquals(e.getProperty("long-blob"), d.getProperty("long-blob"));
	}

	@Test
	public void testSerializeKeys() throws Exception {
		Entity e = new Entity("File", "/tmp/my-file.csv");
		e.setProperty("fileName", "my-file-renamed.csv");
		e.setProperty("blobKey", new BlobKey(KeyFactory.createKeyString("__BlobInfo__", "test")));
		e.setProperty("parentKey", KeyFactory.createKey("File", "/tmp/"));

		EntitySerializer s = new EntitySerializer();
		String yaml = s.serialize(asList(e).iterator());
		System.out.println("Yaml:\n" + yaml);
		
		Entity d = s.deserialize(yaml).next();
		assertEquals(e.getProperty("fileName"), d.getProperty("fileName"));
		assertEquals(e.getProperty("blobKey"), d.getProperty("blobKey"));
		assertEquals(e.getProperty("parentKey"), d.getProperty("parentKey"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSerializeEntity() throws EntityNotFoundException {
		Entity entity = new Entity("Profile", 1l);
		entity.setProperty("name", "John Doe");
		entity.setProperty("age", 27);
		entity.setProperty("height", 1.87d);
		entity.setProperty("activeProfile", true);
		entity.setProperty("registrationDate", new Date());
		entity.setProperty("preferences.fruits", asList("Apple", "Orange", "Lemon"));
		entity.setProperty("preferences.cars", asList("Porshe", "Camaro", "Mustang"));
		entity.setProperty("system.mixedList", asList("public", 12, true, 2d));
		entity.setProperty("system.mixedSet", asSet("one", 2, new Integer(3)));

		EntitySerializer s = new EntitySerializer();
		String yaml = s.serialize(asList(entity).iterator());
		System.out.println(yaml);

		Iterator<Entity> iterator = s.deserialize(yaml);
		assertTrue(iterator.hasNext());

		Entity deserialized = iterator.next();
		assertEquals(entity, deserialized);
		assertEquals(entity.getProperties().size(), //
				deserialized.getProperties().size());
		for (Map.Entry<String, Object> src : entity.getProperties().entrySet()) {
			Map<String, Object> dest = deserialized.getProperties();
			assertTrue(format("Missing property '%s'", src.getKey()),
					dest.containsKey(src.getKey()));

			if (Collection.class.isAssignableFrom(src.getValue().getClass())) {
				Collection<?> destColl = (Collection<?>) dest.get(src.getKey());
				assertTrue(format("Unexpected value: %s", destColl),
						destColl.containsAll((Collection<?>) src.getValue()));
			} else {
				assertEquals(src.getValue(),
						deserialized.getProperties().get(src.getKey()));
			}
		}
	}

	@SafeVarargs
	private static <T> List<T> asList(T... items) {
		List<T> l = new ArrayList<T>();
		for (T i : items)
			l.add(i);
		return l;
	}

	@SafeVarargs
	private static <T> Set<T> asSet(T... items) {
		Set<T> s = new HashSet<T>();
		for (T i : items)
			s.add(i);
		return s;
	}
}
