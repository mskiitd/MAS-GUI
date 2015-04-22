package mas.jobproxy;

import java.io.Serializable;
/**
 * @author Anand Prajapati
 * Represents a non-measurable Go/No-Go attribute of any job.
 * E.g. Texture, surface finish etc.
 *
 */
public class JobGNGattribute implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private boolean conforming;
	
	public JobGNGattribute() {
	}
	
	public JobGNGattribute(String name) {
		this.name = name;
	}
	
	/**
	 * @return Name of this dimension
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set name for this dimension
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Conforming status of this dimension
	 */
	public boolean isConforming() {
		return conforming;
	}
	
	/**
	 * Set conforming status of this dimension
	 * @param conforming
	 */
	public void setConforming(boolean conforming) {
		this.conforming = conforming;
	}
	
}
