package net.ronoaldo.code.appenginetools.fixtures;

import java.lang.reflect.Field;

/**
 * Small utilities to use on java beans.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class ReflectionUtil {

	/**
	 * Directly sets a field value in the target object.
	 * 
	 * @param obj
	 *            the target object.
	 * @param field
	 *            the field name.
	 * @param value
	 *            the field value.
	 * @throws RuntimeException
	 *            if there are permission issues, or if
	 *            types aren't compatible.
	 */
	public static void setField(Object obj, String field, Object value) {
		try {
			Field f = obj.getClass().getDeclaredField(field);
			f.setAccessible(true);
			f.set(obj, value);
		} catch (SecurityException e) {
			throw new RuntimeException("Unable to set field value "
					+ field, e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Unable to set field value "
					+ field, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Unable to set field value "
					+ field, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to set field value "
					+ field, e);
		}
	}

}
