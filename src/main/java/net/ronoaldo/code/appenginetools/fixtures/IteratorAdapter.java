package net.ronoaldo.code.appenginetools.fixtures;

import java.util.Iterator;

/**
 * Iterator wrapper to cast from types F into T.
 *
 * <p>There is no type checking, just pure cast from F into T.
 * This class allows for returning a typed iterator from an
 * untype source. The source must be compatible to the cast.
 * 
 * @author Ronoaldo JLP &lt;ronoaldo@ronoaldo.net&gt;
 */
public class IteratorAdapter<F, T> implements Iterator<T> {

	private Iterator<F> wrapped;

	public IteratorAdapter(Iterator<F> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean hasNext() {
		return wrapped.hasNext();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T next() {
		return (T) wrapped.next();
	}

	@Override
	public void remove() {
		wrapped.remove();
	}
}
