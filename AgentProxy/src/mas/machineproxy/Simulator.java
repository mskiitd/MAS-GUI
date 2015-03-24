package mas.machineproxy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.util.leap.Serializable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import mas.job.OperationType;
import mas.machineproxy.behaviors.AcceptJobBehavior;
import mas.machineproxy.behaviors.GetRootCauseDataBehavior;
import mas.machineproxy.behaviors.LoadMachineComponentBehavior;
import mas.machineproxy.behaviors.LoadMachineParameterBehavior;
import mas.machineproxy.behaviors.LoadSimulatorParamsBehavior;
import mas.machineproxy.behaviors.ParameterShifterBehavaior;
import mas.machineproxy.behaviors.Register2DF;
import mas.machineproxy.behaviors.RegisterMachine2BlackBoardBehvaior;
import mas.machineproxy.behaviors.ShiftInProcessBahavior;
import mas.machineproxy.component.Component;
import mas.machineproxy.component.IComponent;
import mas.machineproxy.parametrer.Parameter;
import mas.machineproxy.parametrer.RootCause;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

public class Simulator extends Agent implements IMachine,Serializable {

	private static final long serialVersionUID = 1L;

	// machine's status property (used in decoding the failure event of machine
	public transient static String machineStatusProperty = "_machineStatusProperty";

	// machine's type i.e. ability to perform number of operations
	private EnumSet<OperationType> supportedOperations;

	// time step in milliseconds
	public transient static int TIME_STEP = 100;

	// ID of this simulator
	public transient String ID_Simulator;

	// AID of blackboard agent to which it will connect and publish-receive information from
	public transient static AID blackboardAgent;

	// name of data store used in behavior's to update this object
	public transient static String simulatorStoreName = "simulatorStoreName";

	public transient static long healthReportTimeMillis = 5000;

	// list of components of machine
	private ArrayList<IComponent> myComponents;

	//starting time of this machine agent 
	private long epochTime;

	// status of this machine i.e. working/failed etc.
	private MachineStatus status;

	// property change support for status of simulator
	protected transient PropertyChangeSupport statusChangeSupport;

	// percentage variation in processing time of jobs
	private transient double percentProcessingTimeVariation = 0.10;		

	// parameters of loading time normal distribution ( in Milliseconds)
	private transient  double meanLoadingTime = 1000.0;					
	private  transient double sdLoadingTime = 1.0;

	// parameters of loading time normal distribution ( in Milliseconds)
	private  transient double meanUnloadingTime = 1000.0;				
	private  transient double sdUnloadingTime = 1.0;

	// fraction defective for attributes of jobs
	private transient  double fractionDefective = 0.10;

	// parameters of process
	private  transient double mean_shift = 0;					
	private  transient double sd_shift = 1;

	// parameters of normal distribution causing shift in process mean
	private  transient double mean_shiftInMean = 0;				
	private  transient double sd_shiftInMean = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private  transient double mean_shiftInSd = 0;				
	private  transient double sd_shiftInSd = 1;

	//rate of process mean shifting (per hour)
	private  transient double rateProcessShift = 0.01;				

	// parameters of process parameters
	private  transient double mean_shiftParam = 0;					
	private  transient double sd_shiftparam = 1;

	// parameters of normal distribution causing shift in process parameters
	private transient  double mean_shiftInMeanParam = 0;				
	private transient  double sd_shiftInMeanParam = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private transient  double mean_shiftInSdParam = 0;				
	private transient  double sd_shiftInSdparam = 1;

	// machine parameters
	private transient ArrayList<Parameter> machineParameters ;

	// root causes for machine parameters which will make a shift in parameter's generative process
	private transient ArrayList<ArrayList<RootCause>> mParameterRootcauses; 

	//	private transient Logger log;

	private void init() {
		//		log = LogManager.getLogger();
		statusChangeSupport = new PropertyChangeSupport(this);
		epochTime = System.currentTimeMillis();
		myComponents = new ArrayList<IComponent>();
		machineParameters = new ArrayList<Parameter>();
		mParameterRootcauses = new ArrayList<ArrayList<RootCause> >();

		this.supportedOperations = EnumSet.of( OperationType.Operation_1,
				OperationType.Operation_2, OperationType.Operation_3 );

	}

	private transient SequentialBehaviour loadData;

	private transient Behaviour loadSimulatorParams;
	private transient Behaviour loadComponentData;
	private transient Behaviour loadMachineParams;
	private transient Behaviour loadRootCause;
	private transient Behaviour registerthis;
	private transient Behaviour registerMachineOnBB;
	private transient Behaviour acceptIncomingJobs;
	private transient Behaviour reportHealth;
	private transient Behaviour processDimensionShifter;
	private transient Behaviour machineParameterShifter;

	private transient ParallelBehaviour functionality ;

	@Override
	protected void setup() {
		super.setup();

		/**
		 * initialize the variables and setup machine's class i.e. type
		 */
		init();

		loadData = new SequentialBehaviour(this);
		loadData.getDataStore().put(simulatorStoreName, Simulator.this);

		loadSimulatorParams = new LoadSimulatorParamsBehavior();
		loadComponentData = new LoadMachineComponentBehavior();
		loadMachineParams = new LoadMachineParameterBehavior();
		loadRootCause = new GetRootCauseDataBehavior();
		registerthis = new Register2DF();
		registerMachineOnBB = new RegisterMachine2BlackBoardBehvaior();

		loadData.addSubBehaviour(loadSimulatorParams);
		loadData.addSubBehaviour(loadComponentData);
		loadData.addSubBehaviour(loadMachineParams);
		loadData.addSubBehaviour(loadRootCause);

		loadData.addSubBehaviour(registerthis);
		loadData.addSubBehaviour(registerMachineOnBB);

		addBehaviour(loadData);

		functionality = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
		functionality.getDataStore().put(simulatorStoreName, Simulator.this);

		acceptIncomingJobs = new AcceptJobBehavior(Simulator.this);
		reportHealth = new ReportHealthBehavior(this, healthReportTimeMillis);
		processDimensionShifter = new ShiftInProcessBahavior(true, true);
		processDimensionShifter.getDataStore().put(simulatorStoreName, Simulator.this);
		machineParameterShifter = new ParameterShifterBehavaior();
		machineParameterShifter.getDataStore().put(simulatorStoreName, Simulator.this);

		functionality.addSubBehaviour(acceptIncomingJobs);
//		functionality.addSubBehaviour(reportHealth);
		functionality.addSubBehaviour(processDimensionShifter);
		//		functionality.addSubBehaviour(machineParameterShifter);

		addBehaviour(functionality);

		/**
		 *  Adding a listener to the change in value of the status of simulator 
		 */
		statusChangeSupport.addPropertyChangeListener (
				new SimulatorStatusListener(Simulator.this) );
	}

	class ReportHealthBehavior extends TickerBehaviour {

		private static final long serialVersionUID = 1L;

		public ReportHealthBehavior(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {

			ZoneDataUpdate machineHealthUpdate = new ZoneDataUpdate.Builder(
					ID.Machine.ZoneData.myHealth).
					value(Simulator.this).
					Build();

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					machineHealthUpdate, myAgent);

			//			System.out.println("Sending machine's health " + Simulator.blackboardAgent);
		}
	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}

	@Override
	public ArrayList<IComponent> getComponents() {
		return myComponents;
	}

	@Override
	public long getStartTime() {
		return this.epochTime;
	}

	@Override
	public MachineStatus getStatus() {
		return status;
	}

	public void setStatus(MachineStatus newStatus) {
		MachineStatus oldStatus = this.status;
		status = newStatus;
		if(newStatus == MachineStatus.FAILED){
			statusChangeSupport.
			firePropertyChange(
					machineStatusProperty,oldStatus, newStatus);
		}
	}

	public void addComponent(Component c) {
		myComponents.add(c);
	}

	public long getNextFailureTime() {
		return epochTime;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param arr is index of all components to get repaired
	 * 
	 * this function calls repair method on the passed components 
	 */

	public void repair(ArrayList<Integer> arr) {

		for (int index = 0; index < arr.size(); index++) { 
			myComponents.get(arr.get(index)).repair();
		}
		this.status = MachineStatus.IDLE;
	}

	/**
	 * @param millis
	 * Age all the components of this simulator by an amount millis
	 * 
	 */
	public synchronized void AgeComponents(long millis) {
		for(int index = 0; index < myComponents.size(); index++) {
			myComponents.get(index).addAge(millis);
		}
	}

	public String getID_Simulator() {
		return ID_Simulator;
	}

	public void setID_Simulator(String iD_Simulator) {
		this.ID_Simulator = iD_Simulator;
	}

	public double getPercentProcessingTimeVariation() {
		return percentProcessingTimeVariation;
	}

	public void setPercentProcessingTimeVariation(
			double percentProcessingTimeVariation) {
		this.percentProcessingTimeVariation = percentProcessingTimeVariation;
	}

	public double getMeanLoadingTime() {
		return meanLoadingTime;
	}

	public void setMeanLoadingTime(double meanLoadingTime) {
		this.meanLoadingTime = meanLoadingTime;
	}

	public double getSdLoadingTime() {
		return sdLoadingTime;
	}

	public void setSdLoadingTime(double sdLoadingTime) {
		this.sdLoadingTime = sdLoadingTime;
	}

	public double getMeanUnloadingTime() {
		return meanUnloadingTime;
	}

	public void setMeanUnloadingTime(double meanUnloadingTime) {
		this.meanUnloadingTime = meanUnloadingTime;
	}

	public double getSdUnloadingTime() {
		return sdUnloadingTime;
	}

	public void setSdUnloadingTime(double sdUnloadingTime) {
		this.sdUnloadingTime = sdUnloadingTime;
	}

	public double getFractionDefective() {
		return fractionDefective;
	}

	public void setFractionDefective(double fractionDefective) {
		this.fractionDefective = fractionDefective;
	}

	public double getMean_shiftInMean() {
		return mean_shiftInMean;
	}

	public void setMean_shiftInMean(double mean_shiftInMean) {
		this.mean_shiftInMean = mean_shiftInMean;
	}

	public double getSd_shiftInMean() {
		return sd_shiftInMean;
	}

	public void setSd_shiftInMean(double sd_shiftInMean) {
		this.sd_shiftInMean = sd_shiftInMean;
	}

	public double getMean_shiftInSd() {
		return mean_shiftInSd;
	}

	public void setMean_shiftInSd(double mean_shiftInSd) {
		this.mean_shiftInSd = mean_shiftInSd;
	}

	public double getSd_shiftInSd() {
		return sd_shiftInSd;
	}

	public void setSd_shiftInSd(double sd_shiftInSd) {
		this.sd_shiftInSd = sd_shiftInSd;
	}

	public double getMean_shift() {
		return mean_shift;
	}

	public void setMean_shift(double mean_shift) {
		this.mean_shift = mean_shift;
	}

	public double getSd_shift() {
		return sd_shift;
	}

	public void setSd_shift(double sd_shift) {
		this.sd_shift = sd_shift;
	}

	public double getRateShift() {
		return rateProcessShift;
	}

	public void setRateShift(double rateShift) {
		this.rateProcessShift = rateShift;
	}

	public double getMean_shiftInMeanParam() {
		return mean_shiftInMeanParam;
	}

	public void setMean_shiftInMeanParam(double mean_shiftInMeanParam) {
		this.mean_shiftInMeanParam = mean_shiftInMeanParam;
	}

	public double getSd_shiftInMeanParam() {
		return sd_shiftInMeanParam;
	}

	public void setSd_shiftInMeanParam(double sd_shiftInMeanParam) {
		this.sd_shiftInMeanParam = sd_shiftInMeanParam;
	}

	public double getMean_shiftInSdParam() {
		return mean_shiftInSdParam;
	}

	public void setMean_shiftInSdParam(double mean_shiftInSdParam) {
		this.mean_shiftInSdParam = mean_shiftInSdParam;
	}

	public double getSd_shiftSdparam() {
		return sd_shiftInSdparam;
	}

	public void setSd_shiftSdparam(double sd_shiftSdparam) {
		this.sd_shiftInSdparam = sd_shiftSdparam;
	}

	public double getMean_shiftParam() {
		return mean_shiftParam;
	}

	public void setMean_shiftParam(double mean_shiftParam) {
		this.mean_shiftParam = mean_shiftParam;
	}

	public double getSd_shiftparam() {
		return sd_shiftparam;
	}

	public void setSd_shiftparam(double sd_shiftparam) {
		this.sd_shiftparam = sd_shiftparam;
	}

	public ArrayList<Parameter> getMachineParameters() {
		return machineParameters;
	}

	public void addMachineParameter(Parameter p ){
		this.machineParameters.add(p);
	}

	public ArrayList<ArrayList<RootCause>> getmParameterRootcauses() {
		return mParameterRootcauses;
	}

	public void addmParameterRootCause(ArrayList<RootCause> rootcause) {
		this.mParameterRootcauses.add(rootcause);
	}

	public EnumSet<OperationType> getSupportedOperations() {
		return supportedOperations;
	}

	public void setSupportedOperations(EnumSet<OperationType> supportedOperations) {
		this.supportedOperations = supportedOperations;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
				append(ID_Simulator).
				append(myComponents).
				hashCode();
	}

	/**
	 *  to check for equality
	 *  required for serializability
	 *  correct the logic inside
	 */

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Simulator){
			final Simulator other = (Simulator) obj;
			return new EqualsBuilder()
			.append(ID_Simulator, other.ID_Simulator)
			.append(myComponents, other.myComponents)
			.isEquals();
		} else {
			return false;
		}
	}
}
