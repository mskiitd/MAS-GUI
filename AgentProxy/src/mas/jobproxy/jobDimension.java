package mas.jobproxy;
import java.io.Serializable;


public class jobDimension implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String attribute;
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

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
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
