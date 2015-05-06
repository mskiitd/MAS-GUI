package mas.machineproxy.component;

import java.io.Serializable;
import java.util.Random;

import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Anand Prajapati
 * <p>
 * The weibull distribution being used here follows a constructor
 * weibull ( shape parameter, scale parameter)
 * weibull ( beta  , eta )
 * 
 * The variable keep track of age is currentAge. Initial age is used to model
 * imperfect repairs of component. LifeToFailure is used as the life of component
 * i.e. when the component will fail i.e.
 * 'currentAge - initialAge' becomes equal to 'lifeToFailure' 
 *</p>
 */

public class Component implements IComponent,Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient static RandomGenerator jdkRndGenerator;
	private transient static GaussianRandomGenerator gaussianGen;
	private transient static Logger log;
	
	private String ComponentID;
	private double initialAge;
	private double currentAge;
	private double timeSinceLastFailure;
	private double beta;
	private double eta;
	private double timeToFailure;
	private double RestorationFactor;
	private int restorationType;
	private double MTTR;
	private double TTR_sd;
	private double MeanDelay;
	private double VarianceDelay;
	private MachineComponentStatus status;
	private double failureCost;
	private double replacementCost;
	private double preventiveMaintenanceCost;
	private long lastMaintTime;
	private double lifeToFailure;
	private transient Simulator mySimulator;

	public Component(Builder builder, Simulator s) {
		
		log = LogManager.getLogger();
		
		if(jdkRndGenerator == null) {
			jdkRndGenerator = new JDKRandomGenerator();
			gaussianGen = new GaussianRandomGenerator(jdkRndGenerator);
		}
		
		this.beta = builder.beta;
		this.eta = builder.eta;
		this.RestorationFactor = builder.RestorationFactor;
		this.restorationType = builder.restorationType;
		this.MTTR = builder.MTTR;
		this.TTR_sd = builder.TTR_sd;
		this.MeanDelay = builder.MeanDelay;
		this.VarianceDelay = builder.VarianceDelay;
		this.failureCost = builder.failureCost;
		this.replacementCost = builder.replacementCost;
		this.preventiveMaintenanceCost = builder.prevMaintCost;

		this.status = MachineComponentStatus.WORKING;
		// assign ages to this component
		this.initialAge = 0;
		this.currentAge = 0;
		this.timeSinceLastFailure = 0;
		this.lifeToFailure = this.generateConditionalLife();
		this.mySimulator = s;
		
	}

	public static class Builder {
		//Required parameters
		private double beta;
		private double eta;
		private double RestorationFactor;
		private int restorationType;
		private double MTTR;
		private double TTR_sd;
		private double MeanDelay;
		private double VarianceDelay;
		private double failureCost;
		private double prevMaintCost;
		private double replacementCost;

		public Builder(double eta, double beta) {
			this.eta = eta;
			this.beta = beta;
		}

		public Builder restorationFactor(double val)
		{ this.RestorationFactor = val; return this; }

		public Builder restorationFactorType(int type)
		{ this.restorationType = type; return this; }

		public Builder MTTR(double val)
		{ this.MTTR = val; return this; }

		public Builder TTR_sd(double val)
		{ this.TTR_sd = val; return this; }

		public Builder meanDelay(double val)
		{ this.MeanDelay = val; return this; }

		public Builder sdDelay(double val)
		{ this.VarianceDelay = val; return this; }

		public Builder failureCost(double val)
		{ this.failureCost = val; return this; }

		public Builder prevMaintCost(double val)
		{ this.prevMaintCost = val; return this; }

		public Builder replacementCost(double val)
		{ this.replacementCost = val; return this; }

		public Component build(Simulator s) {
			return new Component(this,s);
		}
	}

	/**
	 * Generates a life to failure for component based on
	 * accumulated time so far using conditional reliability equation
	 */

	public double generateConditionalLife() {
		Random unifEnd = new Random();
		double conditionalUnreliability = unifEnd.nextDouble();
		double life = this.eta * (Math.pow 
				( Math.pow( this.currentAge/eta,beta) - Math.log(1-conditionalUnreliability),
						(1/beta) )); 

		return life;
	}

	/**
	 * generate new initial age for the component after being repaired based on
	 * restoration factor type 1
	 * For theory of restoration factor, please refer to 
	 * http://www.reliawiki.org/index.php/Imperfect_Repairs
	 * 
	 * repairs will only fix the wear-out and damage incurred during
	 * the last period of operation
	 */

	public void generateLifeRFtype1() {

		WeibullDistribution wb = new WeibullDistribution(this.beta,this.eta);
		Random unifEnd = new Random();
		double uniformRandom =unifEnd.nextDouble();

		this.timeSinceLastFailure = this.currentAge - this.initialAge;
		
		this.initialAge = this.initialAge + 
				this.timeSinceLastFailure * (1 - this.RestorationFactor);

		double futureReliability = uniformRandom*
				(1.0 - wb.cumulativeProbability(this.initialAge));

		this.lifeToFailure = wb.inverseCumulativeProbability(1.0 - futureReliability) -
				this.initialAge;

		if(	this.lifeToFailure < 0) {
			this.lifeToFailure = 0;
			log.debug("Zero Life generated!");
		}
		this.currentAge = this.initialAge;
	}
	
	/**
	 * @param mean
	 * @param sd
	 * @return a random number with mean 'mean' and standard deviation 'sd'
	 */
	public static double normalRandom(double mean, double sd) {				
		return (mean + gaussianGen.nextNormalizedDouble()*sd );
	}

	/**
	 * generate new initial age for the component after being repaired based on
	 * restoration factor type 2
	 * For theory of restoration factor, please refer to 
	 * http://www.reliawiki.org/index.php/Imperfect_Repairs
	 * 
	 * repairs fix all of the wear-out and damage accumulated up to the current time
	 */

	public void generateLifeRFtype2() {

		WeibullDistribution wb = new WeibullDistribution(this.beta,this.eta);
		Random unifEnd = new Random();
		double uniformRandom =unifEnd.nextDouble();

		this.initialAge = this.currentAge * (1 - this.RestorationFactor);

		double futureReliability = uniformRandom*
				(1.0 - wb.cumulativeProbability(this.initialAge));

		this.lifeToFailure = wb.inverseCumulativeProbability(1.0 - futureReliability) -
				this.initialAge;

		if(	this.lifeToFailure < 0) {
			this.lifeToFailure = 0;
			log.debug("Zero Life generated!");
		}
		this.currentAge = this.initialAge;
	}

	/**
	 * @param shape
	 * @param scale
	 * @return weibull distributed random number
	 */
	public double WeibullRnd(double shape, double scale) {
		WeibullDistribution wb = new WeibullDistribution(shape,scale);
		Random unifEnd = new Random();
		double reliability = unifEnd.nextDouble();
		double wbrnd = wb.inverseCumulativeProbability( 1.0 - reliability);
		return wbrnd;
	}
	
	public void addAge(long millis) { 
		this.currentAge += millis;
		
		if(this.currentAge - this.initialAge >= this.lifeToFailure) {
			this.status = MachineComponentStatus.FAILED;
			this.mySimulator.setStatus(MachineStatus.FAILED);
		}
		
	}

	/**
	 * @return ID of this component
	 */
	public String getComponentID() {
		return ComponentID;
	}

	/**
	 * @param componentID
	 * </br> Sets id of this component
	 */
	public void setComponentID(String componentID) {
		this.ComponentID = componentID;
	}
	
	@Override
	public double getEta() {
		return this.eta;
	}

	@Override
	public double getBeta() {
		return this.beta;
	}

	@Override
	public double getAge() {
		return this.currentAge;
	}

	@Override
	public double getLife() {
		return this.lifeToFailure;
	}

	@Override
	public double getTTR() {
		return normalRandom(this.MTTR, this.TTR_sd);
	}

	@Override
	public MachineComponentStatus getStatus() {
		return this.status;
	}

	@Override
	public double getFailureCost() {
		return this.failureCost;
	}

	@Override
	public double getMTTR() {
		return this.MTTR;
	}

	@Override
	public double getDelayMean() {
		return this.MeanDelay;
	}

	@Override
	public double getDelayVariation() {
		return this.VarianceDelay;
	}

	@Override
	public void repair() {
		this.generateLifeRFtype2();
	}
}
