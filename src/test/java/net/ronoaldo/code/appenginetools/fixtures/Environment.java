package net.ronoaldo.code.appenginetools.fixtures;

import java.util.HashSet;
import java.util.Set;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class Environment {

	private LocalServiceTestHelper helper;

	private Set<LocalServiceTestConfig> cfg = new HashSet<LocalServiceTestConfig>();

	private LocalDatastoreServiceTestConfig ds = new LocalDatastoreServiceTestConfig();

	public Environment ds() {
		cfg.add(ds);
		return this;
	}

	public Environment consistent() {
		return consistent(true);
	}

	public Environment consistent(boolean consistent) {
		ds();
		if (consistent) {
			ds.setDefaultHighRepJobPolicyUnappliedJobPercentage(0);
		} else {
			ds.setDefaultHighRepJobPolicyUnappliedJobPercentage(100);
		}
		return this;
	}

	public void setUp() {
		if (helper == null) {
			helper = new LocalServiceTestHelper(
					cfg.toArray(new LocalServiceTestConfig[0]));
		}
		helper.setUp();
	}

	public void tearDown() {
		helper.tearDown();
	}

}
