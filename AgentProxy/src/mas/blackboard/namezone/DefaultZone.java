package mas.blackboard.namezone;

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
