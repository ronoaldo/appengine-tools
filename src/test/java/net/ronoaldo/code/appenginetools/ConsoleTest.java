package net.ronoaldo.code.appenginetools;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;

import org.junit.Test;

public class ConsoleTest {

	@Test
	public void testShowHelp() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		Console.main(new String[]{"-h"});

		String stdout = new String(out.toByteArray());
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

		assertTrue(stdout.toLowerCase().contains("application id"));
	}

}
