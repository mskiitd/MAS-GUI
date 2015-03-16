package mas.blackboard.namezone;

public interface ZoneName {
	 public static final ZoneName DEFAULT = new DefaultZone();

	 public String name();
}
