package mas.machineproxy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.behaviors.AcceptBatchBehavior;
import mas.machineproxy.behaviors.GiveMeJobBehavior;
import mas.machineproxy.behaviors.TakeJobFromBatchBehavior;
import mas.machineproxy.behaviors.GetRootCauseDataBehavior;
import mas.machineproxy.behaviors.HandleSimulatorFailedBehavior;
import mas.machineproxy.behaviors.LoadMachineParameterBehavior;
import mas.machineproxy.behaviors.LoadSimulatorParamsBehavior;
import mas.machineproxy.behaviors.ParameterShifterBehavaior;
import mas.machineproxy.behaviors.Register2DF;
import mas.machineproxy.behaviors.RegisterMachine2BlackBoardBehvaior;
import mas.machineproxy.behaviors.ShiftInProcessBahavior;
import mas.machineproxy.gui.MachineGUI;
import mas.machineproxy.parametrer.Parameter;
import mas.machineproxy.parametrer.RootCause;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Anand Prajapati
 * Class to Machine Simulator modeled as a JADE Agent
 */
public class Simulator extends Agent implements IMachine,Serializable {

	private static final long serialVersionUID = 1L;

	// machine's status property (used in decoding the failure event of machine
	public transient static String machineStatusProperty = "_machineStatusProperty";

	// time step in milliseconds
	public static int TIME_STEP = 500;

	public static long healthReportTimeMillis = 5000;

	// AID of blackboard agent to which it will connect and publish-receive information from
	public transient static AID blackboardAgent;

	// name of data store used in behavior's to update this object
	public transient static String simulatorStoreName = "simulatorStoreName";

	// ID of this simulator
	public transient String ID_Simulator;

	// details about simulator
	private SimulatorInternals internals;

	// property change support for status of simulator
	protected transient PropertyChangeSupport statusChangeSupport;

	// percentage variation in processing time of jobs
	private transient double percentProcessingTimeVariation = 0.10;		

	// parameters of loading time normal distribution ( in Milliseconds)
	private transient  double meanLoadingTime = 1000.0;					
	private transient double sdLoadingTime = 1.0;

	// parameters of loading time normal distribution ( in Milliseconds)
	private transient double meanUnloadingTime = 1000.0;				
	private transient double sdUnloadingTime = 1.0;

	// fraction defective for attributes of jobs
	private transient  double fractionDefective = 0.10;

	// parameters of process
	private transient double mean_shift = 0;					
	private transient double sd_shift = 1;

	// parameters of normal distribution causing shift in process mean
	private transient double mean_shiftInMean = 0;				
	private transient double sd_shiftInMean = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private transient double mean_shiftInSd = 0;				
	private transient double sd_shiftInSd = 1;

	//rate of process mean shifting (per hour)
	private transient double rateProcessShift = 0.01;				

	// parameters of process parameters
	private transient double mean_shiftParam = 0;					
	private transient double sd_shiftparam = 1;

	// parameters of normal distribution causing shift in process parameters
	private transient double mean_shiftInMeanParam = 0;				
	private transient double sd_shiftInMeanParam = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private transient double mean_shiftInSdParam = 0;				
	private transient double sd_shiftInSdparam = 1;

	// machine parameters
	private transient ArrayList<Parameter> machineParameters ;

	// root causes for machine parameters which will make a shift in parameter's generative process
	private transient ArrayList<ArrayList<RootCause>> mParameterRootcauses; 

	private boolean unloadFlag = false;

	private Batch currentBatch = null;
	private job currentJob = null;

	private transient MachineGUI gui = null;
	private transient LocalSchedulingAgent lAgent;

	private transient ScheduledThreadPoolExecutor waitOnGuiThreadExecutor;

	public Simulator(LocalSchedulingAgent localSchedulingAgent) {
		this.lAgent = localSchedulingAgent;
		//		System.out.println("gui is : "  + localSchedulingAgent + "-----");
	}

	/**
	 * 
	 * @return the current batch whose job is being processed/ going to be processed on the machine
	 */
	public Batch getCurrentBatch() {
		return currentBatch;
	}

	/**
	 * @param currentBatch 
	 * sets current batch to be processed on machine
	 */
	public void setCurrentBatch(Batch currentBatch) {
		this.currentBatch = currentBatch;
	}

	/** 
	 * initialize machine parameters here
	 */
	private void init() {
		//		log = LogManager.getLogger();
		statusChangeSupport = new PropertyChangeSupport(this);

		tbf = new  ThreadedBehaviourFactory();
		internals = new SimulatorInternals();
		internals.setEpochTime(System.currentTimeMillis() );

		machineParameters = new ArrayList<Parameter>();
		mParameterRootcauses = new ArrayList<ArrayList<RootCause> >();
	}

	private transient SequentialBehaviour loadData;

	private transient Behaviour loadSimulatorParams;
	private transient Behaviour loadMachineParams;
	private transient Behaviour loadRootCause;
	private transient Behaviour registerthis;
	private transient Behaviour registerMachineOnBB;
	private transient Behaviour acceptIncomingBatch;
	private transient Behaviour reportHealth;
	private transient Behaviour processDimensionShifter;
	private transient Behaviour machineParameterShifter;

	private transient ParallelBehaviour functionality ;
	private transient ThreadedBehaviourFactory tbf;

	/**
	 * Define and add behaviors to simulator
	 */
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
		loadMachineParams = new LoadMachineParameterBehavior();
		loadRootCause = new GetRootCauseDataBehavior();
		registerthis = new Register2DF();
		registerMachineOnBB = new RegisterMachine2BlackBoardBehvaior();

		loadData.addSubBehaviour(loadSimulatorParams);
		//		loadData.addSubBehaviour(loadMachineParams);
		//		loadData.addSubBehaviour(loadRootCause);

		loadData.addSubBehaviour(registerthis);
		loadData.addSubBehaviour(registerMachineOnBB);

		addBehaviour(loadData);

		functionality = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
		functionality.getDataStore().put(simulatorStoreName, Simulator.this);

		acceptIncomingBatch = new AcceptBatchBehavior(Simulator.this);
		reportHealth = new ReportHealthBehavior(this, healthReportTimeMillis);
		processDimensionShifter = new ShiftInProcessBahavior(true, true);
		processDimensionShifter.getDataStore().put(simulatorStoreName, Simulator.this);
		machineParameterShifter = new ParameterShifterBehavaior();
		machineParameterShifter.getDataStore().put(simulatorStoreName, Simulator.this);

		//		functionality.addSubBehaviour(reportHealth);

		functionality.addSubBehaviour(processDimensionShifter);
		//		functionality.addSubBehaviour(machineParameterShifter);
		addBehaviour(tbf.wrap(functionality));
		addBehaviour(tbf.wrap(acceptIncomingBatch));
		
		// Add givemeJobBehavior to agent
		addBehaviour(tbf.wrap(new GiveMeJobBehavior(Simulator.this)));
		/**
		 *  Adding a listener to the change in value of the status of simulator 
		 */
		statusChangeSupport.addPropertyChangeListener (
				new SimulatorStatusListener(Simulator.this) );

		gui = lAgent.mGUI;
		// wait until GUI has started for Machine 
		waitOnGuiThreadExecutor = new ScheduledThreadPoolExecutor(1);

		waitOnGuiThreadExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(gui != null) {
					gui.setMachineSimulator(Simulator.this);
					waitOnGuiThreadExecutor.shutdown();
				} else {
					gui = lAgent.mGUI;
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS );
	}

	/**
	 * @return Reference to GUI of machine
	 */
	public MachineGUI getGui() {
		return gui;
	}

	/**
	 * Handle failure of machine case in this method. This method is called when machine fails
	 */
	public void HandleFailure() {
		addBehaviour(tbf.wrap(new HandleSimulatorFailedBehavior(Simulator.this,
				internals) ));
	}

	/**
	 * @author Anand Prajapati
	 * Behavior to report health of machine on blackboard
	 */
	class ReportHealthBehavior extends TickerBehaviour {

		private static final long serialVersionUID = 1L;

		public ReportHealthBehavior(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {

			ZoneDataUpdate machineHealthUpdate = new ZoneDataUpdate.Builder(
					ID.Machine.ZoneData.myHealth).
					value(internals).
					Build();

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					machineHealthUpdate, myAgent);

		}
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {
		}
	}

	@Override
	public long getStartTime() {
		return this.internals.getEpochTime();
	}

	@Override
	public MachineStatus getStatus() {
		return internals.getStatus();
	}

	/**
	 * @param newStatus status to be set for simualtor
	 * 
	 * Set current status of the simulator
	 */
	public void setStatus(MachineStatus newStatus) {
		MachineStatus oldStatus = this.getStatus();
		this.internals.setStatus(newStatus);
		if(newStatus == MachineStatus.FAILED) {
			statusChangeSupport.firePropertyChange(machineStatusProperty,oldStatus, newStatus);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * This method repairs machine when maintenance arrives
	 */
	public void repair() {
		setStatus(MachineStatus.IDLE);

		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ZoneDataUpdate machineHealthUpdate = new ZoneDataUpdate.Builder(
						ID.Machine.ZoneData.myHealth).
						value(internals).
						Build();

				AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
						machineHealthUpdate, myAgent);
			}
		});
	}

	/**
	 * @return ID of the simulator
	 */
	public String getID_Simulator() {
		return ID_Simulator;
	}

	/**
	 * @param iD_Simulator
	 * Sets ID of the simulator
	 */
	public void setID_Simulator(String iD_Simulator) {
		this.ID_Simulator = iD_Simulator;
	}

	/**
	 * @return fraction defectives for this machine
	 */
	public double getFractionDefective() {
		return fractionDefective;
	}

	/**
	 * @param fractionDefective
	 * sets fraction defectives for this machine
	 */
	public void setFractionDefective(double fractionDefective) {
		this.fractionDefective = fractionDefective;
	}

	/**
	 * @return mean of shift in mean of the normal distribution that is used to add perturbations in the dimensions of 
	 * processed jobs
	 */
	public double getMean_shiftInMean() {
		return mean_shiftInMean;
	}

	/**
	 * @param mean_shiftInMean
	 * Sets mean of shift in mean of the normal distribution that is used to add perturbations in the dimensions of 
	 * processed jobs
	 */
	public void setMean_shiftInMean(double mean_shiftInMean) {
		this.mean_shiftInMean = mean_shiftInMean;
	}

	/**
	 * @return standard deviation of shift in mean of the normal distribution that is used to add perturbations in the dimensions of 
	 * processed jobs
	 */
	public double getSd_shiftInMean() {
		return sd_shiftInMean;
	}

	/**
	 * @param sd_shiftInMean
	 * Sets standard deviation of shift in mean of the normal distribution that is used to add perturbations in
	 * the dimensions of processed jobs
	 */
	public void setSd_shiftInMean(double sd_shiftInMean) {
		this.sd_shiftInMean = sd_shiftInMean;
	}

	/**
	 * @return mean of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the dimensions of processed jobs
	 */
	public double getMean_shiftInSd() {
		return mean_shiftInSd;
	}

	/**
	 * @param mean_shiftInSd
	 * Sets mean of shift in standard deviation of the normal distribution that is used to add perturbations in
	 * the dimensions of processed jobs
	 */
	public void setMean_shiftInSd(double mean_shiftInSd) {
		this.mean_shiftInSd = mean_shiftInSd;
	}

	/**
	 * @return standard deviation of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the dimensions of processed jobs
	 */
	public double getSd_shiftInSd() {
		return sd_shiftInSd;
	}

	/**
	 * @param sd_shiftInSd
	 * Sets standard deviation of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the dimensions of processed jobs
	 */
	public void setSd_shiftInSd(double sd_shiftInSd) {
		this.sd_shiftInSd = sd_shiftInSd;
	}

	/**
	 * @return mean of the normal distribution which is used to bring perturbation in the dimension of processed jobs
	 */
	public double getMean_shift() {
		return mean_shift;
	}

	/**
	 * @param mean_shift
	 * Sets mean of the normal distribution which is used to bring perturbation in the dimension of processed jobs
	 */
	public void setMean_shift(double mean_shift) {
		this.mean_shift = mean_shift;
	}

	/**
	 * @return standard deviation of the normal distribution which is used to bring perturbation in the
	 * dimension of processed jobs
	 */
	public double getSd_shift() {
		return sd_shift;
	}

	/**
	 * @param sd_shift
	 * 
	 * Sets standard deviation of the normal distribution which is used to bring perturbation
	 * in the dimension of processed jobs
	 */
	public void setSd_shift(double sd_shift) {
		this.sd_shift = sd_shift;
	}

	/**
	 * @return rate of process shift. Inter-arrival for process shift is assumed to follow
	 * exponential distribution
	 */
	public double getRateShift() {
		return rateProcessShift;
	}

	/**
	 * @param rateShift
	 * Sets rate of process shift.
	 */
	public void setRateShift(double rateShift) {
		this.rateProcessShift = rateShift;
	}

	/**
	 * @return mean of shift in mean of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public double getMean_shiftInMeanParam() {
		return mean_shiftInMeanParam;
	}

	/**
	 * @param mean_shiftInMeanParam
	 * Sets mean of shift in mean of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public void setMean_shiftInMeanParam(double mean_shiftInMeanParam) {
		this.mean_shiftInMeanParam = mean_shiftInMeanParam;
	}

	/**
	 * @return standard deviation of shift in mean of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public double getSd_shiftInMeanParam() {
		return sd_shiftInMeanParam;
	}

	/**
	 * @param sd_shiftInMeanParam
	 * Sets standard deviation of shift in mean of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public void setSd_shiftInMeanParam(double sd_shiftInMeanParam) {
		this.sd_shiftInMeanParam = sd_shiftInMeanParam;
	}

	/**
	 * @return mean of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public double getMean_shiftInSdParam() {
		return mean_shiftInSdParam;
	}

	/**
	 * @param mean_shiftInSdParam
	 * Sets mean of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public void setMean_shiftInSdParam(double mean_shiftInSdParam) {
		this.mean_shiftInSdParam = mean_shiftInSdParam;
	}

	/**
	 * @return standard deviation of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public double getSd_shiftSdparam() {
		return sd_shiftInSdparam;
	}

	/**
	 * @param sd_shiftSdparam
	 * Sets standard deviation of shift in standard deviation of the normal distribution that is used to add
	 * perturbations in the parameters of this machine
	 */
	public void setSd_shiftSdparam(double sd_shiftSdparam) {
		this.sd_shiftInSdparam = sd_shiftSdparam;
	}

	/**
	 * @return mean of the normal distribution which is used to bring perturbation in the parameters of simulator
	 */
	public double getMean_shiftParam() {
		return mean_shiftParam;
	}

	/**
	 * @param mean_shiftParam
	 * Sets mean of the normal distribution which is used to bring perturbation in the parameters of simulator
	 */
	public void setMean_shiftParam(double mean_shiftParam) {
		this.mean_shiftParam = mean_shiftParam;
	}

	/**
	 * @return standard deviation of the normal distribution which is used to bring perturbation
	 * in the parameters of simulator
	 */
	public double getSd_shiftparam() {
		return sd_shiftparam;
	}

	/**
	 * @param sd_shiftparam
	 * Sets standard deviation of the normal distribution which is used to bring perturbation
	 * in the parameters of simulator
	 */
	public void setSd_shiftparam(double sd_shiftparam) {
		this.sd_shiftparam = sd_shiftparam;
	}

	/**
	 * @return List of parameters of machine simulator
	 */
	public ArrayList<Parameter> getMachineParameters() {
		return machineParameters;
	}

	/**
	 * @param p : parameter of machine.</br>
	 * Add the passed argument to list of parameters of machine
	 */
	public void addMachineParameter(Parameter p ){
		this.machineParameters.add(p);
	}

	/**
	 * @return List of root causes for machine parameters which will make a shift in parameter's generative process
	 */
	public ArrayList<ArrayList<RootCause>> getmParameterRootcauses() {
		return mParameterRootcauses;
	}

	/**
	 * @param rootcause </br>
	 * Add passed root-cause to list of root-causes for machine parameters
	 */
	public void addmParameterRootCause(ArrayList<RootCause> rootcause) {
		this.mParameterRootcauses.add(rootcause);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
				append(ID_Simulator).
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
			.isEquals();
		} else {
			return false;
		}
	}

	/**
	 * Loads the next job from the current batch
	 */
	public void loadJob() {
		addBehaviour(new TakeJobFromBatchBehavior(Simulator.this));
	}

	/**
	 * Unload the currently loaded job from the machine
	 */
	public void unloadJob() {
		this.setUnloadFlag(true);
	}

	/**
	 * @return true if the user has pressed unload button in the gui of this machine.
	 * This variable is as a flag to determine when to unload the job from the machine
	 * as well as from the GUI.
	 */
	public boolean isUnloadFlag() {
		return unloadFlag;
	}

	/**
	 * set the value of the unload flag variable which is used to determine when to unload the job
	 * from the machine
	 * @param unloadFlag
	 */
	public void setUnloadFlag(boolean unloadFlag) {
		this.unloadFlag = unloadFlag;
	}

	/**
	 * @return the current loaded job on the machine simulator
	 */
	public job getCurrentJob() {
		return currentJob;
	}

	/**
	 * @param currentJob
	 * </br>Sets the current job on the machine
	 */
	public void setCurrentJob(job currentJob) {
		this.currentJob = currentJob;
	}

}
