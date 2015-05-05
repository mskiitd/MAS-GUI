package mas.blackboard.nameZoneData;

/**
 * Creates zone data with default name "DEFAULT_ZONE_DATA"
 * Recommondation: do not use unless you are changing library code 
 * @author NikhilChilwant
 *
 */
public final class DefaultZoneData implements ZoneDataName
{
   public String getName() {
      return "DEFAULT_ZONE_DATA";
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
