package mas.blackboard.workspace;

import jade.content.Concept;

import java.io.Serializable;

import bdi4jade.belief.Belief;

public class WorkspaceBelief<T> extends Belief<T> implements Serializable,
			Concept {

		protected T value;
		/**
		 * Initializes a belief with its name.
		 * 
		 * @param name
		 *            the belief name.
		 */
		public WorkspaceBelief(String name) {
			super(name);
		}

		/**
		 * Initializes a belief with its name and a initial value.
		 * 
		 * @param name
		 *            the belief name.
		 * @param value
		 *            the initial belief value.
		 */
		public WorkspaceBelief(String name, T value) {
			super(name);
			this.value = value;
		}

		/**
		 * @see bdi4jade.belief.Belief#getValue()
		 */
		@Override
		public T getValue() {
			return this.value;
		}

		/**
		 * @see bdi4jade.belief.Belief#setValue(java.lang.Object)
		 */
		public void setValue(T value) {
			this.value = value;
		}
	}
