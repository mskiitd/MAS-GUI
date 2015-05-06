package mas.machineproxy.component;

public interface IComponent {

	/**
	 * @return Eta value of this component
	 */
	public double getEta();
	/**
	 * @return Beta value of this component
	 */
	public double getBeta();
	/**
	 * @return Age of the component
	 */
	public double getAge();
	/**
	 * @return Life of the component
	 */
	public double getLife();
	/**
	 * @return Time to repair for this component
	 */
	public double getTTR();
	/**
	 * @return Current status of the component i.e. working/failed/critical etc.
	 */
	public MachineComponentStatus getStatus();
	/**
	 * @return Failure cost of this component
	 */
	public double getFailureCost();
	/**
	 * @return Mean time to repair of this component
	 */
	public double getMTTR();
	/**
	 * @return mean of delay in repair when component fails
	 */
	public double getDelayMean();
	/**
	 * @return Standard deviation of delay in repair when component fails
	 */
	public double getDelayVariation();
	/**
	 * Repair this component
	 */
	public void repair();
	/**
	 * Age the component by amount of millis
	 * @param millis
	 */
	public void addAge(long millis) ;
}
