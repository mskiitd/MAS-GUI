package mas.job;
import java.io.Serializable;


public class jobDimension implements Serializable{
	private static final long serialVersionUID = 1L;
	private String title = "length";
	private double target;
	private double achieved;

	public void setTitle(String name){
		this.title = name;
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

	public String getTitle() {
		return title;
	}
}
