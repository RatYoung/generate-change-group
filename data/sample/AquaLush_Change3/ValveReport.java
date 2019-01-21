
package irrigation;

/**
 *  The ValveReport holds data about a particular valve for access by clients.
 *  It is a record class.
 *
 *   @author C. Fox
 *  @version 07/08
 */


public class ValveReport {

      /* attributes
      /*************/

   public final String  id;           // unique valve identifier
   public final String  location;     // description from configuration
   public final String  zoneID;       // unique zone identifier
   public final boolean isOpen;       // true iff valve is now open
   public final boolean isFailed;     // true iff valve has failed
   public final boolean isRecorded;   // true iff valve failure state in store
   public final int     flowRate;     // gallons per minute
   public final int     minutesOpen;  // since start of cycle

      /* constructors
      /***************/

      /**
       *  Initialize the ValveReport
       */

   public ValveReport( String theID,
                       String theLocation,
                       String theZoneID,
                       boolean isOpenFlag,
                       boolean isFailedFlag,
                       boolean isRecordedFlag,
                       int theFlowRate,
                       int theMinutesOpen ) {

      id          = theID;
      location    = theLocation;
      zoneID      = theZoneID;
      isOpen      = isOpenFlag;
      isFailed    = isFailedFlag;
      isRecorded  = isRecordedFlag;
      flowRate    = theFlowRate;
      minutesOpen = theMinutesOpen;

   } // ValveReport

} // ValveReport
