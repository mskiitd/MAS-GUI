package mas.maintenanceproxy.classes;

import java.io.Serializable;

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
