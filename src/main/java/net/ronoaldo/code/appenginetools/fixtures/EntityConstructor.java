package net.ronoaldo.code.appenginetools.fixtures;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

/**
 * Deserializes {@link Entity} objects from Yaml documents..
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
class EntityConstructor extends Constructor {

	EntityConstructor() {
		this.yamlConstructors.put(//
				new Tag("!entity"), new EntityConstruct());
		this.yamlConstructors.put(new Tag("!key"), new KeyConstruct());
	}

	/**
	 * Deserializes {@link Entity} objects from the Yaml tag !entity.
	 * 
	 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
	 */
	private class EntityConstruct extends AbstractConstruct implements
			Construct {
		@Override
		@SuppressWarnings("unchecked")
		public Object construct(Node node) {
			Map<Object, Object> mapping = constructMapping((MappingNode) node);
			Entity entity = new Entity((Key) mapping.get("key"));
			Map<String, Object> properties = //
			(Map<String, Object>) mapping.get("properties");
			for (Map.Entry<String, Object> entry : properties.entrySet()) {
				entity.setProperty(entry.getKey(), entry.getValue());
			}
			return entity;
		}
	}

	/**
	 * Deserializes {@link Key} objects from the Yaml  tag !key.
	 * 
	 * Keys are encoded as a <i>key path</i>, formed by pairs of
	 * <code>kind, id</code> values, with kind being a string and
	 * the id a string or integer.
	 *
	 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
	 */
	private class KeyConstruct extends AbstractConstruct implements Construct {
		@Override
		@SuppressWarnings("unchecked")
		public Object construct(Node node) {
			List<Object> seq = (List<Object>) constructSequence((SequenceNode) node);
			return KeyPath.fromKeyPath(seq.toArray());
		}
	}
}
