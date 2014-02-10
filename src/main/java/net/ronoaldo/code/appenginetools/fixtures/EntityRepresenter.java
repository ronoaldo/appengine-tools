package net.ronoaldo.code.appenginetools.fixtures;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

/**
 * Yaml representation of an {@link Entity} with some custom type mappings.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
class EntityRepresenter extends Representer {

	EntityRepresenter() {
		this.representers.put(Entity.class, new EntityRepresent());
		this.representers.put(Key.class, new KeyRepresent());
	}

	/**
	 * Represents an {@link Entity} using the custom tag !entity.
	 * 
	 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
	 */
	private class EntityRepresent implements Represent {
		@Override
		public Node representData(Object data) {
			Entity entity = (Entity) data;
			Map<String, Object> sortedProperties = new TreeMap<String, Object>();
			sortedProperties.putAll(entity.getProperties());

			Map<String, Object> mapping = new LinkedHashMap<String, Object>();
			mapping.put("key", entity.getKey());
			mapping.put("properties", sortedProperties);

			return representMapping(new Tag("!entity"), mapping, false);
		}
	}

	/**
	 * Represents a {@link Key} using the custom tag !key.
	 * 
	 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
	 */
	private class KeyRepresent implements Represent {
		@Override
		public Node representData(Object data) {
			Key key = (Key) data;
			return representSequence(new Tag("!key"),
					Arrays.asList(KeyPath.toKeyPath(key)), true);
		}
	}
}
