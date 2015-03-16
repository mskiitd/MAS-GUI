package mas.blackboard.nameZoneData;

public final class DefaultZoneData implements ZoneDataName
{
   public String getName() {
      return "DEFAULT_PARAMETER";
   }

   public boolean equals(Object obj) {
      return (obj != null && (obj instanceof DefaultZoneData));
   }

   public int hashCode() {
      return DefaultZoneData.class.hashCode();
   }

   public String toString() {
      return "the default Parameter";
   }
}
