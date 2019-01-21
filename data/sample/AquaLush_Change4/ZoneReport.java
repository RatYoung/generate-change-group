
package irrigation;

/**
 *  The ZoneReport holds data about a particular zone for access by clients.
 *  It is a record class.
 *
 *   @author C. Fox
 *  @version 07/06
 */


public class ZoneReport {

      /* attributes
      /**************/

   public final String id;            // unique zone identifier
   public final String location;      // description from configuration
   public final int    criticalLevel; // when this zone needs irrigation
   public final int    maxLevel;	  // when this zone needs to stop being irrigated

   
      /* constructors
      /****************/

      /**
       *  Initialize the ZoneReport
       */

   public ZoneReport( String theID, 
                      String theLocation, 
                      int theCriticalLevel,
                      int theMaxLevel){

      id            = theID;
      location      = theLocation;
      criticalLevel = theCriticalLevel;
      maxLevel      = theMaxLevel;

   } // ZoneReport

} // ZoneReport
