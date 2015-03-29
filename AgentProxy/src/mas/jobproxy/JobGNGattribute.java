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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isConforming() {
		return conforming;
	}
	public void setConforming(boolean conforming) {
		this.conforming = conforming;
	}
	
}
