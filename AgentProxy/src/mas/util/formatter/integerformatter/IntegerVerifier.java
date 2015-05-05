
package mas.util.formatter.integerformatter;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.*;
import javax.swing.text.*;

public class IntegerVerifier extends InputVerifier {
	private int minValue = 1;
	private int maxValue = Integer.MAX_VALUE;
	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black;

	public IntegerVerifier() {          
	}
	/**
	 * Creates an Verifier object that makes sure the text can be parsed into an interger between min and max
	 * @param min lowest valid value
	 * @param max highest valid value
	 * @throws java.lang.IllegalArgumentException
	 */    
	public IntegerVerifier(int min, int max) throws IllegalArgumentException {
		if(min  > max)
			throw new IllegalArgumentException("min value must be less than max value");

		minValue = min;
		maxValue = max;
	}

	public boolean verify(JComponent jc) {
		try {
			String text = ((JTextComponent)jc).getText();
			int val = Integer.parseInt(text);
			if(val < minValue || val > maxValue) {
				Toolkit.getDefaultToolkit().beep();
				jc.setForeground(INVALID_COLOR);
				return false;
			}
		}
		catch(Exception e) {
			Toolkit.getDefaultToolkit().beep();
			jc.setForeground(INVALID_COLOR);  
			return false;  
		}
		jc.setForeground(VALID_COLOR);
		return true;        
	}
	/**
	 * Mutator method for minValue, minValue is used to set the lower range of valid
	 * numbers.
	 * @param value
	 * @throws java.lang.IllegalArgumentException
	 */       
	public void setMinValue(int value) throws IllegalArgumentException {
		if(value > maxValue)
			throw new IllegalArgumentException("value must be less than maxvalue");
		minValue = value;
	}
	/**
	 * Mutator method for maxValue, maxValue is used to set the upper range of valid
	 * numbers.
	 * @param value new maximum value
	 * @throws java.lang.IllegalArgumentException
	 */       
	public void setMaxValue(int value) throws IllegalArgumentException {
		if(value < minValue)
			throw new IllegalArgumentException("value must be greater than minvalue");
		maxValue = value;
	}


}
