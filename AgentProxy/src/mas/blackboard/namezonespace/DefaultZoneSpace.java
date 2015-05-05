package mas.blackboard.namezonespace;

/**
 * Used in creation of Named Zone Space.
 * Recommonded to use only when you understand library code
 * @author NikhilChilwant
 *
 */
public final class DefaultZoneSpace implements ZoneSpaceName
{
   public String getLocalName() {
      return "DEFAULT_ZONE";
   }

   public boolean equals(Object obj) {
      return (obj != null && (obj instanceof DefaultZoneSpace));
   }

   public int hashCode() {
      return DefaultZoneSpace.class.hashCode();
   }

   public String toString() {
      return "the default Zone";
   }
}
