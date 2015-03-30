package mas.blackboard.workspace;

import java.util.HashMap;
import bdi4jade.belief.Belief;
import mas.blackboard.namedWorkspace.NamedWorkspace;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;

public class Workspace<T> extends Belief<T>implements WorkspaceIFace{

	private static final long serialVersionUID = 1L;
	private String WSname;
	private HashMap<NamedZoneSpace, ZoneSpace> ZSpaces;
	
	public Workspace(NamedWorkspace n){
		super(n.name());
		this.WSname = n.name();
		this.ZSpaces = new HashMap<NamedZoneSpace, ZoneSpace>();
	}
	
	@Override
	public void dropZoneSpace(NamedZoneSpace title) {
		
	}

	@Override
	public ZoneSpace findZoneSpace(NamedZoneSpace title) {
		return ZSpaces.get(title);
	}

/*	@Override
	public boolean insertItem(NamedZoneSpace zSpaceName,NamedZoneData var, Object obj) {
		findZoneSpace(zSpaceName).insertItem(var, obj);
		return false;
	}
*/
	@Override
	public void removeItem(Object obj) {
		
	}

	@Override
	public T getValue() {
		return null;
	}

	@Override
	public void setValue(T value) {
	}
}
