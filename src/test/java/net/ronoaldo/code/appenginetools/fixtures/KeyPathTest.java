package net.ronoaldo.code.appenginetools.fixtures;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class KeyPathTest {

	Environment e = new Environment().ds();

	@Before
	public void setUp() {
		e.setUp();
	}

	@After
	public void tearDown() {
		e.tearDown();
	}

	@Test
	public void testCriarKeyPath() {
		Key grandParent = KeyFactory.createKey("GrandParent", 1);
		Key parent = KeyFactory.createKey(grandParent, "Parent", 2);
		Key child = KeyFactory.createKey(parent, "Child", 3);

		Object[] keyPath = KeyPath.toKeyPath(child);
		System.out.println(Arrays.asList(keyPath));

		assertEquals(6, keyPath.length);
		assertEquals("GrandParent", keyPath[0]);
		assertEquals(1l, keyPath[1]);
		assertEquals("Parent", keyPath[2]);
		assertEquals(2l, keyPath[3]);
		assertEquals("Child", keyPath[4]);
		assertEquals(3l, keyPath[5]);

		checkDecode(child, keyPath);

		keyPath = KeyPath.toKeyPath(grandParent);
		assertEquals(2, keyPath.length);
		assertEquals("GrandParent", keyPath[0]);
		assertEquals(1l, keyPath[1]);

		checkDecode(grandParent, keyPath);
	}

	private void checkDecode(Key grandParent, Object[] keyPath) {
		assertEquals(grandParent, KeyPath.fromKeyPath(keyPath));
		System.out.println("raw(decoded)=" + KeyPath.fromKeyPath(keyPath));
	}
}
