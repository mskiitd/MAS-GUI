package mas.util.formatter.stringformatter;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

public class FormattedStringField extends JFormattedTextField implements Serializable{

	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport propertySupport;

	public FormattedStringField() {  
		setValue("");
		propertySupport = new PropertyChangeSupport(this);     
		StringFormatter ef = new StringFormatter();  
		setInputVerifier(new ValidStringVerifier());
		DefaultFormatterFactory dff = new DefaultFormatterFactory(ef);
		setFormatterFactory(dff);
	}

}
