package mas.blackboard.workspace;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import bdi4jade.belief.BeliefSet;

/**
 * This class extends the {@link BeliefSet} and represents a transient belief
 * set, which is not persisted in a permanent memory.
 * 
 */

public class WorkspaceBeliefSet<T> extends BeliefSet<T> {

	private static final long serialVersionUID = 8345025506647930L;

	protected Set<T> values;

	/**
	 * Creates a transient belief set.
	 * 
	 * @param name
	 *            the name of the belief set.
	 */
	public WorkspaceBeliefSet(String name) {
		super(name);
		this.values = new HashSet<T>();
	}

	/**
	 * Creates a transient belief set.
	 * 
	 * @param name
	 *            the name of the belief set.
	 * @param values
	 *            the initial values of this belief set.
	 */
	public WorkspaceBeliefSet(String name, Set<T> values) {
		super(name);
		this.values = values;
	}

	/**
	 * @see bdi4jade.belief.BeliefSet#addValue(java.lang.Object)
	 */
	public void addValue(T value) {
		this.values.add(value);
	}

	/**
	 * @see bdi4jade.belief.Belief#getValue()
	 */
	@Override
	public Set<T> getValue() {
		return values;
	};

	/**
	 * @see bdi4jade.belief.BeliefSet#hasValue(java.lang.Object)
	 */
	public boolean hasValue(T value) {
		return this.values.contains(value);
	}

	/**
	 * @see bdi4jade.belief.BeliefSet#iterator()
	 */
	public Iterator<T> iterator() {
		return this.values.iterator();
	}

	/**
	 * @see bdi4jade.belief.BeliefSet#removeValue(java.lang.Object)
	 */
	public boolean removeValue(T value) {
		return this.values.remove(value);
	}

	/**
	 * @see bdi4jade.belief.Belief#setValue(java.lang.Object)
	 */
	public void setValue(Set<T> values) {
		this.values = values;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.values.toString();
	}

}