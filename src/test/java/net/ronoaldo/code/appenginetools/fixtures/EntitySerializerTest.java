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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

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
	@SuppressWarnings("unchecked")
	public void testSerializeEntity() throws EntityNotFoundException {
		Entity e = new Entity("test");
		e.setProperty("a", (short) 1);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(e);
		System.out.println(ds.get(e.getKey()).getProperty("a").getClass());		
		
		Entity entity = new Entity("TestEntity", 1l);
		entity.setProperty("str", "short string property value");
		entity.setProperty("long", 27);
		entity.setProperty("double", 37d);
		entity.setProperty("bool", true);
		entity.setProperty("date", new Date());
		entity.setProperty("a.b", asList("a", "b", "c"));
		entity.setProperty("c.d", asList("c", "d", "e"));
		entity.setProperty("mixed liset", asList("a", 1, true, 2d));
		entity.setProperty("mixed set", asSet("one", 2, new Integer(3)));

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
			assertTrue(format("%s não encontrada", src.getKey()),
					dest.containsKey(src.getKey()));

			if (Collection.class.isAssignableFrom(src.getValue().getClass())) {
				Collection<?> destColl = (Collection<?>) dest.get(src.getKey());
				assertTrue(format("Valor inesperado: %s", destColl),
						destColl.containsAll((Collection<?>) src.getValue()));
			} else {
				assertEquals(src.getValue(),
						deserialized.getProperties().get(src.getKey()));
			}
		}
	}

	private static <T> List<T> asList(T... items) {
		List<T> l = new ArrayList<T>();
		for (T i : items)
			l.add(i);
		return l;
	}

	private static <T> Set<T> asSet(T... items) {
		Set<T> s = new HashSet<T>();
		for (T i : items)
			s.add(i);
		return s;
	}
}
