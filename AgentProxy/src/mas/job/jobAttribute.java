package mas.job;
import java.io.Serializable;

public class jobAttribute implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String Name;
	private boolean Conforming;
	
	public jobAttribute(String name) {
		this.setName(name);
	}

	public boolean isConforming() {
		return Conforming;
	}

	public void setConforming(boolean conforming) {
		Conforming = conforming;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
}