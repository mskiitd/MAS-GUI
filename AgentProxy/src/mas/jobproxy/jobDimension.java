package mas.jobproxy;
import java.io.Serializable;

/**
 * @author Anand Prajapati
 * Represents a measurable dimension of a manufacturing job.
 * It can be Go/No-Go type as well, but has to be measurable
 *
 */

public class jobDimension implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private double target;
	private double achieved;

	private boolean Conforming;

	public jobDimension() {
	}
	
	/**
	 * @return Conforming status for this dimension
	 */
	public boolean isConforming() {
		return Conforming;
	}

	/**
	 * Set the conforming status for this dimension
	 * @param conforming
	 */
	public void setConforming(boolean conforming) {
		Conforming = conforming;
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
	 * Set target value for this dimension
	 * @param d
	 */
	public void setTargetDimension(double d){
		this.target = d;
	}

	/**
	 * Set achieved value for this dimension
	 * @param d
	 */
	public void setAchievedDimension(double d){
		this.achieved = d;
	}

	/**
	 * @return Target value for this dimension
	 */
	public double getTargetDimension() {
		return target;
	}

	/**
	 * @return Target value for this dimension
	 */
	public double getAchievedDimension() {
		return achieved;
	}
}
