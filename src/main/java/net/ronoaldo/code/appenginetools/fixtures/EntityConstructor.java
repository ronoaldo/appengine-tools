package net.ronoaldo.code.appenginetools.fixtures;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 * Deserializes {@link Entity} objects from Yaml documents..
 * 
 * @author Ronoaldo JLP
 */
class EntityConstructor extends Constructor {

	EntityConstructor() {
		this.yamlConstructors.put(new Tag("!entity"), new EntityConstruct());
		this.yamlConstructors.put(new Tag("!blob"), new BlobConstruct());
		this.yamlConstructors.put(new Tag("!text"), new TextConstruct());
		this.yamlConstructors.put(Tag.BINARY, new ShortBlobConstruct());
	}

	/**
	 * Deserializes {@link Entity} objects from the Yaml tag !entity.
	 */
	private class EntityConstruct extends AbstractConstruct implements
			Construct {
		@Override
		@SuppressWarnings("unchecked")
		public Object construct(Node node) {
			Map<Object, Object> mapping = constructMapping((MappingNode) node);
			// Decodes the key, required to initialize the Entity.
			List<Object> seq = (List<Object>) mapping.get("key");
			Key key = KeyPath.fromKeyPath(seq.toArray());
			// Build the entity, and set all properties.
			Entity entity = new Entity(key);
			Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
			for (Map.Entry<String, Object> entry : properties.entrySet()) {
				entity.setProperty(entry.getKey(), entry.getValue());
			}
			return entity;
		}
	}

	/**
	 * Deserializes {@link Text} objects from the Yaml tag !text.
	 */
	private class TextConstruct extends AbstractConstruct implements Construct {
		@Override
		public Object construct(Node node) {
			String value = constructScalar((ScalarNode) node).toString();
			return new Text(value);
		}
	}

	/**
	 * Deserializes {@link Blob} objects from the Yaml tag !blob.
	 */
	private class BlobConstruct extends AbstractConstruct implements Construct {
		@Override
		public Object construct(Node node) {
			char[] binary = constructScalar((ScalarNode) node).toString().toCharArray();
			byte[] decoded = Base64Coder.decode(binary);
			return new Blob(decoded);
		}
	}

	/**
	 * Deserialies {@link ShortBlob} objects from tye Yaml tag !!binary.
	 */
	private class ShortBlobConstruct extends AbstractConstruct implements Construct {
		@Override
		public Object construct(Node node) {
			char[] binary = constructScalar((ScalarNode) node).toString().toCharArray();
			byte[] decoded = Base64Coder.decode(binary);
			return new ShortBlob(decoded);
		}
	}
}
