package mas.machineproxy.component;

import jade.util.leap.Serializable;

public interface IComponent extends Serializable{

	public double getEta();
	public double getBeta();
	public double getAge();
	public double getLife();
	public double getTTR();
	public MachineComponentStatus getStatus();
	public double getFailureCost();
	public double getMTTR();
	public double getDelayMean();
	public double getDelayVariation();
	public void repair();
	public void addAge(long millis) ;
}
