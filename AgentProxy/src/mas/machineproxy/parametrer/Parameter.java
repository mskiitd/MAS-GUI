package mas.machineproxy.parametrer;

import jade.util.leap.Serializable;

public class Parameter implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private double value;
	private boolean inspect;
	private int frequency;

	public Parameter(String name, double value,
			boolean inspect,int frequency) {
		this.name = name;
		this.value = value;
		this.inspect = inspect;
		this.frequency = frequency;
	}

	public String getname() {
		return this.name;
	}

	public double getvalue() {
		return this.value;
	}

	public boolean getInspect() {
		return this.inspect;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setvalue(double v) {
		this.value=v;
	}
}
