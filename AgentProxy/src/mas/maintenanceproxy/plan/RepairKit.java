package mas.maintenanceproxy.plan;

import java.util.Random;
import mas.MAS;
import mas.machineproxy.SimulatorInternals;
import mas.machineproxy.component.IComponent;

public class RepairKit {

	private	SimulatorInternals myMachine; 
	private static int NUM_ITERATIONS = 10000;
	private static int MICRO_ITERATIONS = 100;

	class CalculateMTTF {

		private double eta, beta;

		/**
		 *  the weibull distribution reliability 
		 *   R = exp(-pow((x-gamma)/eta,beta)
		 * @param x
		 * @return
		 */

		private double f(double x) {
			return Math.exp(- Math.pow(x/this.eta, this.beta) );
		}

		public double reliability(double a,double eta , double beta) {
			this.eta = eta;
			this.beta = beta;
			double LIM = 100;

			return integrateTrapezoid(a,LIM, (int) (100*LIM));
		}

		private double integrateTrapezoid(double a, double b, int N) {

			double h = (b - a) / N;              // step size
			double sum = 0.5 * (f(a) + f(b));    // area
			for (int i = 1; i < N; i++) {
				double x = a + h * i;
				sum = sum + f(x);
			}

			return sum*h;
		}

		private double IntegrateSimpson(double a, double eta, double beta) {
			this.eta = eta;
			this.beta = beta;
			int i, z;
			double h, s;
			long b = 20000000;
			int n = 10000;
			n = n + n;
			s = f(a) * f(b);
			h = (b - a) / n;
			z = 4;

			for (i = 1; i < n; i++) {
				s = s + z * f(a + i * h);
				z = 6 - z;
			}
			return (((s * h) / 3) / f(a));
		}
	}

	public double[] residualLife(double t) {

		CalculateMTTF temp = new CalculateMTTF();
		int numComponents = myMachine.getComponents().size();

		double[] remainingLife = new double[numComponents];

		for (int i = 0; i < numComponents; i++) {
			//			System.out.println("Current life :" + myMachine.getComponents().get(i).getAge());
			remainingLife[i] = temp.reliability( myMachine.getComponents().get(i).getAge(),
					myMachine.getComponents().get(i).getEta(),
					myMachine.getComponents().get(i).getBeta());
		}
		return remainingLife;
	}

	public double[] yellowZone(double t) {
		double[] remainingLife = residualLife(t);
		int numComponents = myMachine.getComponents().size();
		double[] yZone = new double[numComponents];

		for (int i = 0; i < numComponents; i++) {
			yZone[i] =  - myMachine.getComponents().get(i).getAge() +
					0.6 * (remainingLife[i] + 
							myMachine.getComponents().get(i).getAge());

		}
		return yZone;
	}

	public double[] redZone(double t) {
		double[] rtemp = residualLife(t);
		int n = myMachine.getComponents().size();
		double[] rZone = new double[n];
		for (int i = 0; i < n; i++) {

			rZone[i] =  - myMachine.getComponents().get(i).getAge() +
					0.9 * (rtemp[i] +  myMachine.getComponents().get(i).getAge());
		}
		return rZone;
	}

	/**
	 * 
	 * @param t
	 * @return Due date of maintenance job
	 * will be equal to starting time + processing time
	 * starting time is taken as the time whenever any component goes into red zone
	 * processing time is taken as total maintenance time of components which are into
	 * red zone or yellow zone
	 */
	public double maintenanceJobDueDate(double t) {
		int index = 0;

		double[] rzone = redZone(t);
		double min = rzone[0];
		int numComponents = myMachine.getComponents().size();
		for (index = 0; index < numComponents ; index++) {
			if (min > (rzone[index])) {			
				min = rzone[index];
			}
		}
		//		System.out.println("mainttime="+totalMaintenanceTime(t)+"  duedate="+(min/MAS.scale+totalMaintenanceTime(t)));
		return min + totalMaintenanceTime(t);
	}

	/**
	 * 
	 * @param t
	 * @return return total time required to perform
	 *  maintenance of machine
	 */

	public double totalMaintenanceTime(double t) {
		double time = 0;
		double[] rzone = redZone(t);
		double[] yzone = yellowZone(t);
		int numComponents = myMachine.getComponents().size();

		for (int i = 0; i < numComponents ; i++) {
			if ( rzone[i] <= 0 ||
					yzone[i] <= 0 ) {
				time = time + myMachine.getComponents().get(i).getTTR(); // repair time
			}
		}
		
		return time;
	}

	public String getCoorectiveMaintenanceData() {
		double mtime = totalMaintenanceTime(0);
		StringBuilder data = new StringBuilder();
		data.append(mtime);

		double[] rzone = redZone(myMachine.getEpochTime());
		double[] yzone = yellowZone(myMachine.getEpochTime());
		int numComponents = myMachine.getComponents().size();

		for (int i=0; i < numComponents;i++) {
			if (0 >= rzone[i] ||
					0 >= yzone[i]) {
				data.append(" " + i);
			}
		}
		//		for (int k = 0; k < brokencomp.length; k++) {
		//			if (0 < rzone[brokencomp[k]] && 0 < yzone[brokencomp[k]]) {
		//				data=data+" "+brokencomp[k];
		//			}
		//		}
		
		return data.toString();
	}

	/**
	 * @param furtureTime
	 * @param componentIndex
	 * @return conditional probability that the component will fail in the given time
	 * given that it has already been in operation for a time of it's current age
	 */

	public double conditionalReliability(double furtureTime, int componentIndex )
	{
		IComponent comp = myMachine.getComponents().get(componentIndex);
		double value = (1 - Math.exp
				( Math.pow( comp.getAge()/comp.getEta(), comp.getBeta() ) -
						Math.pow((furtureTime + comp.getAge())/comp.getEta(), comp.getBeta()))
				);
		return value;
	}

	/**
	 * 
	 * @param timeWindow
	 * @param profitPerUnitTime
	 * @param averageSlack
	 * @param averagePenalty
	 * @return the value of break even equation at the given values of parameters
	 */
	double breakEvenEquation(double timeWindow, double profitPerUnitTime,
			double averageSlack, double averagePenalty) {

		Random rng = new Random();
		double value = 0;
		double expectedDelay = 0;
		double meanDownTime = 0;
		double profitLoss = 0;			

		for (int i= 0; i < myMachine.getComponents().size(); i++) {

			IComponent comp = myMachine.getComponents().get(i);
			value = value + comp.getFailureCost()*conditionalReliability(timeWindow,i);
			expectedDelay = expectedDelay + conditionalReliability(timeWindow,i)*comp.getDelayMean();

			meanDownTime = meanDownTime + conditionalReliability(timeWindow,i)*(comp.getMTTR() + Math.abs(
					comp.getDelayMean() +
					comp.getDelayVariation() * rng.nextGaussian()));

		}		
		if (averageSlack < meanDownTime)
			profitLoss = (meanDownTime - averageSlack)*averagePenalty;		

		/////////////////////////////////correct this////////////////////////////
		double expressionValue = value + expectedDelay*profitPerUnitTime -
				profitPerUnitTime*(timeWindow) + profitLoss;		
		return expressionValue;
	}

	/**
	 * 
	 * @param maxProfitPerTime
	 * @param averageSlack
	 * @param averagePenalty
	 * @return solution for break even equation(if it exists) to decide starting time
	 *  for red zone of the machine
	 */

	public double breakEvenMachineRedZone(double maxProfitPerTime,
			double averageSlack, double averagePenalty) {		

		double etamax = myMachine.getComponents().get(0).getEta();
		for (int k = 0; k < myMachine.getComponents().size(); k++) {
			etamax = Math.max(myMachine.getComponents().get(k).getEta(), etamax);
		}

		//		double signchange1 = breakEvenEquation(etamax*1/10000.0 ,
		//							maxProfitPerTime, averageSlack,averagePenalty);
		//		double signchange2 = breakEvenEquation(etamax*2/10000.0 ,
		//							maxProfitPerTime, averageSlack,averagePenalty);

		double[] iterations = new double[NUM_ITERATIONS];
		boolean solutionFound = false;
		double signChange1, signChange2;
		int requiredIteration = 0;
		double requiredLife1 = 0;
		double requiredLife2 = 0;

		double requiredFineLife1 = 0;
		double requiredFineLife2 = 0;
		double required_solution = 0;

		for (int i = 1; i < NUM_ITERATIONS; i++) {

			iterations[i] = breakEvenEquation(etamax*i/NUM_ITERATIONS ,
					maxProfitPerTime,  averageSlack,averagePenalty);

			if ( (i > 1 && i < NUM_ITERATIONS) &&
					iterations[i]*iterations[i-1] <= 0) {

				solutionFound = true;
				signChange1 = iterations[i-1];
				signChange2 = iterations[i];
				requiredIteration = i;
				requiredLife1 = etamax*(i-1)/NUM_ITERATIONS;
				requiredLife2 = etamax*i/NUM_ITERATIONS;
				required_solution = (requiredLife1 + requiredLife2)/2;
				break;
			}
		}

		double[] fineIterations = new double[MICRO_ITERATIONS];

		if (solutionFound == true) {
			for (int j = 0; j < MICRO_ITERATIONS; j++) {

				fineIterations[j] = breakEvenEquation(
						requiredLife1 + (requiredLife2 - requiredLife1)*j/(MICRO_ITERATIONS-1),
						maxProfitPerTime, averageSlack,averagePenalty);

				if (j > 1 && j < MICRO_ITERATIONS &&
						fineIterations[j]*fineIterations[j-1]<=0) {

					signChange1 = fineIterations[j-1];
					signChange2 = fineIterations[j];
					requiredIteration = j;

					requiredFineLife1 = requiredLife1 + (requiredLife2 - requiredLife1)*(j-1)/(MICRO_ITERATIONS-1);
					requiredFineLife2 = requiredLife1 + (requiredLife2 - requiredLife1)*j/(MICRO_ITERATIONS-1);

					required_solution = (requiredFineLife1+requiredFineLife2)/2;
				}
			}
		}
		//SystemyMachine.out.println("Found="+found +" signchange1="+signchange1+" signchange2="+signchange2+" required_red_Zone=" +required_solution);		
		return required_solution;			
	}

	/**
	 * @param minProfitPerTime
	 * @param averageSlack
	 * @param averagePenalty
	 * @return solution for break even equation(if it exists) to decide starting time
	 *  for yellow zone of the machine
	 */

	public double breakEvenMachineYellowZone(double minProfitPerTime,
			double averageSlack, double averagePenalty) {	

		double etamax = myMachine.getComponents().get(0).getEta();
		for (int index = 0; index < myMachine.getComponents().size(); index++) {
			etamax = Math.max(myMachine.getComponents().get(index).getEta(), etamax);
		}

		double signchange1 = breakEvenEquation(etamax*1/10000.0 , minProfitPerTime,  averageSlack,averagePenalty);
		double signchange2 = breakEvenEquation(etamax*2/10000.0 , minProfitPerTime,  averageSlack,averagePenalty);;

		double[] iterations = new double[NUM_ITERATIONS];
		boolean solutionFound = false;
		int requiredIteration = 0;
		double required_life1 = 0;
		double required_life2 = 0;
		double required_life11 = 0;
		double required_life21 = 0;
		double required_solution = 0;

		for (int i=1; i<= NUM_ITERATIONS; i++) {

			iterations[i] = breakEvenEquation(etamax*i/NUM_ITERATIONS,
					minProfitPerTime, averageSlack,averagePenalty);

			if (i > 1 && i < NUM_ITERATIONS &&
					iterations[i]*iterations[i-1] <= 0) {

				solutionFound=true;
				signchange1 = iterations[i-1];
				signchange2 = iterations[i];
				requiredIteration = i;
				required_life1 = etamax*(i-1)/NUM_ITERATIONS;
				required_life2 = etamax*i/NUM_ITERATIONS;
				required_solution = (required_life1 + required_life2)/2;
				break;
			}
		}

		double[] fineIterations = new double[MICRO_ITERATIONS];

		if (solutionFound == true) {
			for (int j = 0; j < MICRO_ITERATIONS; j++) {

				fineIterations[j] = breakEvenEquation(required_life1+
						(required_life2-required_life1)*j/MICRO_ITERATIONS,
						minProfitPerTime, averageSlack,averagePenalty);

				if (j > 1 && j < MICRO_ITERATIONS &&
						iterations[j]*iterations[j-1]<=0) {

					signchange1 = iterations[j-1];
					signchange2 = iterations[j];
					requiredIteration = j;
					required_life11 = required_life1 +
							(required_life2 - required_life1)*(j-1)/MICRO_ITERATIONS;
					required_life21 = required_life1 +
							(required_life2 - required_life1)*j/MICRO_ITERATIONS;
					required_solution = (required_life11 + required_life21)/2;
				}
			}
		}
		//SystemyMachine.out.println("Found="+found +" signchange1="+signchange1+" signchange2="+signchange2+" required_yellow_Zone=" +required_solution);		
		return required_solution;			
	}

	public double penalty_increment (double custtime) {

		double expectedFailureCost = 0;
		double dueDatePeriod = maintenanceJobDueDate(0);

		int numComponents = myMachine.getComponents().size();
		for (int i = 0; i < numComponents; i++) {

			expectedFailureCost = expectedFailureCost +
					conditionalReliability(dueDatePeriod*MAS.Scale, i)*
					myMachine.getComponents().get(i).getFailureCost();		
		}	

		double adjustedPenalty = expectedFailureCost/dueDatePeriod;
		if (dueDatePeriod <= 0)
			adjustedPenalty = 1000000;

		System.out.println("Penalty increment=" + adjustedPenalty +
				"  PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");

		return adjustedPenalty;
	}

	public String getPreventiveMaintenanceData()
	{
		double mtime = totalMaintenanceTime(0);
		StringBuilder data = new StringBuilder();
		data.append(mtime);
		double[] timeToRedZone = redZone(myMachine.getEpochTime());
		double[] timeToYellowZone = yellowZone(myMachine.getEpochTime());
		int numComponents = myMachine.getComponents().size();

		for (int i=0; i < numComponents ;i++) {
			if (timeToRedZone[i] <= 0 ||
					timeToYellowZone[i] <= 0) {
				data.append(" " + i);
			}
		}
		return data.toString();
	}

	public SimulatorInternals getMachine() {
		return myMachine;
	}

	public void setMachine(SimulatorInternals machine) {
		this.myMachine = machine;
	}
}
