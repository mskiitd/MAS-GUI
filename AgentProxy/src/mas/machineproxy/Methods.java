package mas.machineproxy;

import jade.util.leap.Serializable;

import java.util.ArrayList;

import mas.machineproxy.component.IComponent;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * @author Anand Prajapati
 * 
 * Contains utility methods which are called by simulator class or component class
 * to generate random numbers, get loading time, get unloading time 
 *
 */
public class Methods implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static RandomGenerator jdkRndGenerator = new JDKRandomGenerator();
	private static GaussianRandomGenerator gaussianGen = new GaussianRandomGenerator(jdkRndGenerator);
	
	/**
	 * @param mean
	 * @param sd
	 * @param arraySize
	 * @return random number array following a normal distribution with mean and sd
	 */
	
	public static double[] normalRandom(double mean, double sd, int arraySize) {
		double[] a = new double[arraySize];
		for(int i = 0; i < arraySize; i++){
			a[i] = mean + gaussianGen.nextNormalizedDouble()*sd;
		}
		return a;
	}

	/**
	 * @param mean
	 * @param sd
	 * @return a random number with mean 'mean' and standard deviation 'sd'
	 */
	public static double normalRandom(double mean, double sd) {				
		double a = mean + gaussianGen.nextNormalizedDouble()*sd;
		return a;
	}

	/**
	 * 
	 * @param rate
	 * @param sizeArray
	 * @return random number array following exponential distribution
	 */
	public static double[] rexp(double rate,int sizeArray) {
		ExponentialDistribution g = new ExponentialDistribution(rate);
		
		int i=0;
		double[] arr = new double[sizeArray];
		while(i < sizeArray){
			arr[i] = g.sample();
			i++;
		}
		return arr;
	}

	/**
	 * @param numSamples
	 * @param weights
	 * @return random number(between 0 and 1) following discrete distribution of weights
	 */
	public static int[] runif(int numSamples,int[] weights) {
		
		int[] numsToGenerate = new int[] {0 ,1  , 2  ,  3,  4 ,  5,    6,  7 ,  8 , 9 };

		int[] temp = weights;
		double sum = 0.0;
		double[] discreteProbabilities = new double[numsToGenerate.length];
		int i;
		for(i=0;i < numsToGenerate.length; i++) {

			discreteProbabilities[i] = temp[i];
			sum += temp[i];
		}
		for(i=0;i<numsToGenerate.length;i++) {
			discreteProbabilities[i]=discreteProbabilities[i]/sum;
		}

		EnumeratedIntegerDistribution distribution = 
				new EnumeratedIntegerDistribution(numsToGenerate, discreteProbabilities);

		int[] samples = distribution.sample(numSamples);
		return samples;
	}
	
	public static double getLoadingTime(double mean,double sd)	{
		double[] r_arr = normalRandom(0, sd, 1);
		return (mean + r_arr[0]);
	}

	public static double getunloadingTime(double mean,double sd) {
		double[] r_arr = normalRandom(0, sd, 1);
		return (mean + r_arr[0]);
	}
	
	//returns index of component with minimum Time to failure
	public static int findMin(ArrayList<IComponent> components) { 
		int IndMin = 0;
		for(int index = 0;index < components.size(); index++) {
//			if(components.get(IndMin).TTF > components.get(index).TTF){
//				IndMin = index;
//			}
		}
		return IndMin;
	}
	
	public static int findMinElemet(double[] arr) {
		double m = 0;
		int ind = 0;
		for (int i=0; i < arr.length ; i++) {
			if( m < arr[i]) {
				m = arr[i];
				ind=i;
			}
		}
		return ind;
	}
}
