
package irrigation;

/**
 *  The SensorReport holds data about a particular sensor for access by clients.
 *  It is a record class.
 *
 *   @author C. Fox
 *  @version 07/08
 */


public class SensorReport {

      /* attributes
      /**************/

   public final String  id;           // unique sensor identifier
   public final String  location;     // description from configuration
   public final String  zoneID;       // unique zone identifier
   public final boolean isFailed;     // true iff sensor has failed
   public final boolean isRecorded;   // true iff failure state in store
   public final int     level;        // measured moisture level

      /* constructors
      /****************/

      /**
       *  Initialize the SensorReport
       */

   public SensorReport( String theID,
                        String theLocation,
                        String theZoneID,
                        boolean isFailedFlag,
                        boolean isRecordedFlag,
                        int theLevel ) {

      id         = theID;
      location   = theLocation;
      zoneID     = theZoneID;
      isFailed   = isFailedFlag;
      isRecorded = isRecordedFlag;
      level      = theLevel;

   } // SensorReport

} // SensorReport
