package mas.maintenanceproxy.classes;

import java.io.Serializable;

/**
 * @author Anand Prajapati
 * <p>
 * Class to represent response of maintenance agent when maintenance is delayed.
 * It consists of a message along with a degree. Message is the content of notification which will be displayed on the machine
 * when the maintenance is delayed. Degree tells how severe the case is. For degree <=2, a warning message will be displayed.
 * For degree > 2, machine will stop working. 
 * </p>
 */
public class MaintenanceResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int degree;
	private String msg;
	
	public MaintenanceResponse() {
		this(0,"null");
	}

	public MaintenanceResponse(int ticks) {
		this(ticks,"null");
	}
	
	public MaintenanceResponse(int degree, String msg) {
		this.degree = degree;
		this.msg = msg;
	}
	
	public int getDegree() {
		return degree;
	}
	
	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
