
package irrigation;

/**
 *  The FailedDeviceReport holds data about a particular failed device.
 *  It is a record class.
 *
 *   @author C. Fox
 *  @version 08/06
 */


public class FailedDeviceReport {

      /* attributes
      /*************/

   public final String  deviceID;   // unique identifier for the failed device
   public final String  zoneID;     // unique zone identifier for device's zone
   public final String  location;   // description from configuration
   public final boolean isRecorded; // true iff this failure is in the store

      /* constructors
      /***************/

      /**
       *  Initialize the FailedDeviceReport
       */

   public FailedDeviceReport( String theID,
                              String theZoneID,
                              String theLocation,
                              boolean recordedFlag ) {

      deviceID   = theID;
      zoneID     = theZoneID;
      location   = theLocation;
      isRecorded = recordedFlag;

   } // FailedDeviceReport

} // FailedDeviceReport
