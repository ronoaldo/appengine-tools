package net.ronoaldo.code.appenginetools.fixtures;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Tool to convert a {@link Key} into a String representing it as a KeyPath.
 *
 * <p>The format of the <i>key path</i> is a sequence of pairs of a kind and
 * an id, from the first ancestor until the final key component.
 * 
 * <p>
 * Examples:
 * 
 * <pre>
 * key('GrandParent', 1) =&gt; ["GrandParent", 1l]
 * key('GrandParent', 1, 'Parent', 2) =&gt; ["GrandParent", 1l, "Parent", 2l]
 * ...
 * </pre>
 * 
 * Both numeric and String ids are accpeted.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 * 
 */
public class KeyPath {

	/**
	 * Encodes the key as a key path.
	 * 
	 * @param key the Key to be encoded.
	 * @return the encoded key path.
	 */
	public static Object[] toKeyPath(Key key) {
		List<Key> keys = new ArrayList<Key>();
		while (key != null) {
			keys.add(0, key);
			key = key.getParent();
		}

		Object[] result = new Object[keys.size() * 2];
		for (int i = 0; i < result.length; i += 2) {
			Key k = keys.get(i / 2);
			result[i] = k.getKind();
			result[i + 1] = k.getName() == null ? k.getId() : k.getName();
		}

		return result;
	}

	/**
	 * Decodes the key path componentes into a Key.
	 * 
	 * @param path the key path components.
	 * @return the key from the key path.
	 */
	public static Key fromKeyPath(Object[] path) {
		checkNotNull(path);
		checkArgument(path.length >= 2 && (path.length % 2 == 0),
				"Key path não é par: '%s'", Arrays.asList(path));

		// parts = ['Kind', 'a', 'Kind', 1 ]
		Key ancestor = null;
		Key resultado = null;
		for (int i = 0; i < path.length; i += 2) {
			String kind = (String) path[i];
			Object nameOrId = path[i + 1];

			if (nameOrId instanceof String) {
				resultado = KeyFactory.createKey(//
						ancestor, kind, nameOrId.toString());
			} else {
				Long id;
				if (nameOrId instanceof Integer) {
					id = new Long((Integer) nameOrId);
				} else {
					id = (Long) nameOrId;
				}
				resultado = KeyFactory.createKey(// ]
						ancestor, kind, (Long) id);
			}
			ancestor = resultado;
		}

		return resultado;
	}
}
