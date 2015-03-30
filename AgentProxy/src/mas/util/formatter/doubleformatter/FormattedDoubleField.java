
package mas.util.formatter.doubleformatter;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Provides a JFormattedTextField that accepts only doubles. Allows for the setting of the number
 * of decimal places displayed, and min/max values allowed.
 * 
 * @author Mark Pendergast
 * Copyright Mark Pendergast
 */
public class FormattedDoubleField extends JFormattedTextField implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String PROP_MINVALUE_PROPERTY = "minimum value";
	public static final String PROP_MAXVALUE_PROPERTY = "maximum value";
	public static final String PROP_DECIMALPLACES_PROPERTY = "decimal places";

	public static final String displayFormat = "#,###,##0.000000000000000";
	public static final String editFormat = "#.###############";


	private double minValue = -Double.MAX_VALUE;
	private double maxValue = Double.MAX_VALUE;
	private int decimalPlaces = DEFAULT_DECIMALPLACES;
	private DefaultFormatterFactory dff;
	private DoubleVerifier verifier;


	private static final int DEFAULT_DECIMALPLACES = 2;
	private static final int MAX_DECIMALPLACES = 15;
	private static final int POSITIONS_LEFT_OF_DECIMALDISPLAY = displayFormat.indexOf('.');
	private static final int POSITIONS_LEFT_OF_DECIMALEDIT = editFormat.indexOf('.');

	private PropertyChangeSupport propertySupport;
	/**
	 * Default constructor
	 */
	public FormattedDoubleField() {
		this(-Double.MAX_VALUE, Double.MAX_VALUE,0, DEFAULT_DECIMALPLACES);
	}
	/**
	 * Constructor that accepts the number of decimal places to display 
	 *
	 * @param places number of decimal places to display
	 */
	public FormattedDoubleField(int places) {
		this(-Double.MAX_VALUE, Double.MAX_VALUE,0, places);
	}
	/**
	 *  Constructor that accepts a min and max allowed value
	 * 
	 * @param min min allowed value
	 * @param max max alloed value
	 * @throws java.lang.IllegalArgumentException
	 */
	public FormattedDoubleField(double min, double max) throws IllegalArgumentException{
		this(min,max,0,DEFAULT_DECIMALPLACES);
	}
	/**
	 *  Working Constructor
	 * 
	 * @param min min allowed value
	 * @param max max allowed value
	 * @param value inital value
	 * @param places number of decimal places to display
	 * @throws java.lang.IllegalArgumentException
	 */   
	public FormattedDoubleField(double min, double max, double value, int places) throws IllegalArgumentException{
		propertySupport = new PropertyChangeSupport(this);

		setValue(new Double(value));
		minValue = min;
		maxValue = max;
		if(min > max)
			throw new IllegalArgumentException("min value cannot be greater than max value");
		verifier = new DoubleVerifier(min, max);
		setInputVerifier(verifier);
		NumberFormatter def = new NumberFormatter();
		def.setValueClass(Double.class);
		NumberFormatter disp = new NumberFormatter((new DecimalFormat(displayFormat.substring(0,POSITIONS_LEFT_OF_DECIMALDISPLAY+places+1))));
		disp.setValueClass(Double.class);
		NumberFormatter ed = new NumberFormatter((new DecimalFormat(editFormat.substring(0,POSITIONS_LEFT_OF_DECIMALEDIT+places+1))));
		ed.setValueClass(Double.class);
		dff = new DefaultFormatterFactory(def,disp,ed);
		setFormatterFactory(dff);

	}
	/*
	 * Accessor methods
	 */
	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public int getDecimalPlaces()
	{
		return decimalPlaces;
	}
	/*
	 * mutator methods
	 */
	public void setMinValue(double value) throws IllegalArgumentException{
		if(value > maxValue)
			throw new IllegalArgumentException("min value cannot be greater than max value");
		double oldValue = minValue;
		minValue  = value;
		propertySupport.firePropertyChange(PROP_MINVALUE_PROPERTY, oldValue, minValue);
		verifier.setMinValue(value);
	}

	public void setMaxValue(double value) throws IllegalArgumentException {
		if(value < minValue)
			throw new IllegalArgumentException("max value cannot be less than min value");
		double oldValue = maxValue;
		maxValue  = value;
		propertySupport.firePropertyChange(PROP_MAXVALUE_PROPERTY, oldValue, maxValue);
		verifier.setMaxValue(value);
	}

	public void setDecimalPlaces(int value) throws IllegalArgumentException {
		if(value < 0 || value > MAX_DECIMALPLACES)
			throw new IllegalArgumentException("decimal places cannot be negative");
		int oldValue = decimalPlaces;
		decimalPlaces  = value;
		propertySupport.firePropertyChange(PROP_DECIMALPLACES_PROPERTY, oldValue, decimalPlaces);
		dff.setDisplayFormatter(new NumberFormatter((new DecimalFormat(displayFormat.substring(0,POSITIONS_LEFT_OF_DECIMALDISPLAY+value+1)))));
		dff.setEditFormatter(new NumberFormatter((new DecimalFormat(editFormat.substring(0,POSITIONS_LEFT_OF_DECIMALEDIT+value+1)))));
		setFormatterFactory(dff);
		repaint();
	}    
}

