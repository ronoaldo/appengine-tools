package net.ronoaldo.code.appenginetools.fixtures;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 * Yaml representation of an {@link Entity} with some custom type mappings.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
class EntityRepresenter extends Representer {

	EntityRepresenter() {
		this.representers.put(Entity.class, new EntityRepresent());
		this.representers.put(Blob.class, new BlobRepresent());
		this.representers.put(Text.class, new TextRepresent());
		this.representers.put(ShortBlob.class, new ShortBlobRepresent());
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
			mapping.put("key", KeyPath.toKeyPath(entity.getKey()));
			mapping.put("properties", sortedProperties);

			return representMapping(new Tag("!entity"), mapping, false);
		}
	}

	/**
	 * Represents a {@link Blob} using the custom tag !blob.
	 */
	private class BlobRepresent implements Represent {
		@Override
		public Node representData(Object data) {
			Blob blob = (Blob) data;
			char[] binary = Base64Coder.encode(blob.getBytes());
			return representScalar(new Tag("!blob"), String.valueOf(binary), '|');
		}
	}

	/**
	 * Represents a {@link Text} using the custom tag !text.
	 */
	private class TextRepresent implements Represent {
		@Override
		public Node representData(Object data) {
			Text text = (Text) data;
			return representScalar(new Tag("!text"), text.getValue(), '|');
		}
	}

	/**
	 * Represents a {@link ShortBlob} using the built-in tag !!binary.
	 */
	private class ShortBlobRepresent  extends RepresentByteArray implements Represent {
		@Override
		public Node representData(Object data) {
			ShortBlob blob = (ShortBlob) data;
			return super.representData(blob.getBytes());
		}
	}
}
