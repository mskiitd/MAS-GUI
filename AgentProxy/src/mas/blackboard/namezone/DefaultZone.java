package mas.blackboard.namezone;

/**
 * Creates zone without name "DEFAULT_ZONE". Used in creation of Named Workspace.
 * Recommendation: not to use unless you are changing code in library
 * @author NikhilChilwant
 *
 */
public final class DefaultZone implements ZoneName
{
   public String name() {
      return "DEFAULT_ZONE";
   }

   public boolean equals(Object obj) {
      return (obj != null && (obj instanceof DefaultZone));
   }

   public int hashCode() {
      return DefaultZone.class.hashCode();
   }

   public String toString() {
      return "the default Zone";
   }
}
