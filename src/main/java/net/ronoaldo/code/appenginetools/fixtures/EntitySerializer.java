package net.ronoaldo.code.appenginetools.fixtures;

import java.util.Iterator;
import java.util.logging.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.appengine.api.datastore.Entity;

/**
 * Utility to serialize {@link Entity} in Yaml format.
 *
 * <p>This implementation allows one to serialize the datastore
 * entities as Yaml strings, that can later be restored back into
 * the datastore.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class EntitySerializer {

	private Yaml yaml;

	private static Logger logger = Logger.getLogger(//
			EntitySerializer.class.getName());

	/**
	 * Creates a new instance of {@link EntitySerializer}.
	 */
	public EntitySerializer() {
		DumperOptions dumper = configureDumper();
		EntityConstructor c = new EntityConstructor();
		EntityRepresenter r = new EntityRepresenter();
		this.yaml = new Yaml(c, r, dumper);
	}

	/**
	 * Default SnakeYaml Dumper configurations.
	 * 
	 * @return the DumperOptions with sane default values.
	 */
	private DumperOptions configureDumper() {
		DumperOptions dumper = new DumperOptions();
		// dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
		dumper.setExplicitStart(true);
		// dumper.setPrettyFlow(true);
		dumper.setWidth(80);
		logger.fine("tags:" + dumper.getTags());
		return dumper;
	}

	/**
	 * Serialize the entity iterator into a Yaml String.
	 * 
	 * @param entities
	 *            the Entity iterator to read from
	 * @return the Yaml documents as a single string.
	 */
	public String serialize(Iterator<Entity> entities) {
		return this.yaml.dumpAll(entities);
	}

	/**
	 * Deserializes the Yaml document as string into a
	 * list of Datastore entities.
	 * 
	 * @param yaml
	 *            the Yaml documents to read from.
	 * @return the resulting Entities after deserialization.
	 */
	public Iterator<Entity> deserialize(String yaml) {
		final Iterator<Object> wrapped = this.yaml.loadAll(yaml).iterator();
		return new IteratorAdapter<Object, Entity>(wrapped);
	}
}
