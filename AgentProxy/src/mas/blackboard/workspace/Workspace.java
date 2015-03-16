package mas.blackboard.workspace;

import java.util.HashMap;

import bdi4jade.belief.Belief;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namedWorkspace.NamedWorkspace;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;

public class Workspace<T> extends Belief<T>implements WorkspaceIFace{

	/*public Workspace(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}*/

	private String WSname;
	private HashMap<NamedZoneSpace, ZoneSpace> ZSpaces;
	
	public Workspace(NamedWorkspace n){
		super(n.name());
		this.WSname = n.name();
		this.ZSpaces = new HashMap<NamedZoneSpace, ZoneSpace>();
	}
	
	/*public static Workspace newInstance(NamedWorkspace name){
		return new Workspace(name.name());
	}*/
	
/*	@Override
	public void createZoneSpace(NamedZoneSpace name) {
		if(! ZSpaces.containsKey(name)){
			ZoneSpace zs = new ZoneSpace(name);
			ZSpaces.put(name, zs);
		}
	}*/

	@Override
	public void dropZoneSpace(NamedZoneSpace title) {
		
	}

	@Override
	public ZoneSpace findZoneSpace(NamedZoneSpace title) {
		return ZSpaces.get(title);
	}

	@Override
	public boolean insertItem(NamedZoneSpace zSpaceName,NamedZoneData var, Object obj) {
		findZoneSpace(zSpaceName).insertItem(var, obj);
		return false;
	}

	@Override
	public void removeItem(Object obj) {
		
	}

	@Override
	public T getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(T value) {
		// TODO Auto-generated method stub
		
	}

}
