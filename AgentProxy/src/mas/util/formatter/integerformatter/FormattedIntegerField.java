package mas.util.formatter.integerformatter;


import java.beans.*;
import java.io.Serializable;
import javax.swing.*;
import java.text.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class FormattedIntegerField extends JFormattedTextField implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String PROP_MINVALUE_PROPERTY = "minimum value";
	public static final String PROP_MAXVALUE_PROPERTY = "maximum value";

	private int minValue = Integer.MIN_VALUE;
	private int maxValue = Integer.MAX_VALUE;
	private DefaultFormatterFactory dff;
	private IntegerVerifier verifier;

	private PropertyChangeSupport propertySupport;
	/**
	 * Default constructor
	 */
	public FormattedIntegerField() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE,0);
	}
	/**
	 *  Constructor that accepts a min and max allowed value
	 * 
	 * @param min min allowed value
	 * @param max max alloed value
	 * @throws java.lang.IllegalArgumentException
	 */
	public FormattedIntegerField(int min, int max) throws IllegalArgumentException{
		this(min,max,0);
	}
	/**
	 *  Working Constructor
	 * 
	 * @param min min allowed value
	 * @param max max allowed value
	 * @param value inital value
	 * @throws java.lang.IllegalArgumentException
	 */      
	public FormattedIntegerField(int min, int max, int value) throws IllegalArgumentException{
		propertySupport = new PropertyChangeSupport(this);

		minValue = min;
		maxValue = max;
		if(min > max)
			throw new IllegalArgumentException("min value cannot be greater than max value");
		verifier = new IntegerVerifier(min, max);
		setInputVerifier(verifier);
		setValue(new Integer(value));
		NumberFormatter def = new NumberFormatter();
		def.setValueClass(Integer.class);
		NumberFormatter disp = new NumberFormatter((new DecimalFormat("#,###,##0")));
		disp.setValueClass(Integer.class);
		NumberFormatter ed =  new NumberFormatter((new DecimalFormat("#,###,##0")));
		ed.setValueClass(Integer.class);
		dff = new DefaultFormatterFactory(def,disp,ed);
		setFormatterFactory(dff);
	}
	/*
	 * Accessor methods
	 */ 
	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}
	/*
	 * mutator methods
	 */
	public void setMinValue(int value) throws IllegalArgumentException{
		if(value > maxValue)
			throw new IllegalArgumentException("min value cannot be greater than max value");
		int oldValue = minValue;
		minValue  = value;
		propertySupport.firePropertyChange(PROP_MINVALUE_PROPERTY, oldValue, minValue);
		verifier.setMinValue(value);
	}

	public void setMaxValue(int value) throws IllegalArgumentException {
		if(value < minValue)
			throw new IllegalArgumentException("max value cannot be less than min value");
		int oldValue = maxValue;
		maxValue  = value;
		propertySupport.firePropertyChange(PROP_MAXVALUE_PROPERTY, oldValue, maxValue);
		verifier.setMaxValue(value);
	}


}
