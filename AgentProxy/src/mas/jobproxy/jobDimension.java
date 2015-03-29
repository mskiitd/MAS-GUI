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
	
	public boolean isConforming() {
		return Conforming;
	}

	public void setConforming(boolean conforming) {
		Conforming = conforming;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTargetDimension(double d){
		this.target = d;
	}
	public void setAchievedDimension(double d){
		this.achieved = d;
	}

	public double getTargetDimension() {
		return target;
	}

	public double getAchievedDimension() {
		return achieved;
	}

}
