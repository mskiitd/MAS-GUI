package mas.blackboard.namezonespace;

public interface ZoneSpaceName {
	 public static final ZoneSpaceName DEFAULT = new DefaultZoneSpace();

	 /**
	  * 
	  * 
	  * @return Local name of agent stored in zone space
	  */
	 public String getLocalName();
}
