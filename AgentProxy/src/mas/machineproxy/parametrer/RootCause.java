package mas.machineproxy.parametrer;

import jade.util.leap.Serializable;

public class RootCause implements Serializable{

	private static final long serialVersionUID = 1L;
	private int indexParams;
	private double mean,sd;
	
	public RootCause(int i,double a,double b) {
		this.indexParams = i;
		this.mean = a;
		this.sd = b;
	}

	public int getIndex() {
		return this.indexParams;
	}
	
	public double getmean() {
		return this.mean;
	}
	
	public double getsd() {
		return this.sd;
	}
}
