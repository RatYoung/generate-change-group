
package ui;

/**
 *  The DeviceFailureScrnState obtains and formats information about a
 *  failed hardware device (valve or sensor).
 *
 *   @author C. Fox
 *  @version 08/06
 */

import irrigation.Irrigator;
import irrigation.FailedDeviceReport;

import java.util.List;

class DeviceFailureScrnState extends ScreenState {

      /* attributes
      /*************/

   private final Irrigator    irrigator; // to get the failed device report
   private FailedDeviceReport report;    // data about the failed device

      /* constructors
      /***************/

   DeviceFailureScrnState( Irrigator theIrrigator ) {
      irrigator = theIrrigator;
   }

      /* methods
      /**********/

      /**
       *  Get a report about the next failed device from the list of 
       *  recently failed devices maintained by the irrigator.
       *
       *     @pre there is a recently failed device
       *    @post a new report is generated
       *  @throws IllegalStateException if @pre is violated
       */

   void updateData() {

      if ( !irrigator.isFailedDevice() )
         throw new IllegalStateException( "No recently failed devices." );

      report = irrigator.getFailedDeviceReport(irrigator.getNextFailedDevice());
   }

      /**
       *  Put the device type (valve or sensor) and the device identifier
       *  centered in a line.
       *
       *     @pre none
       *    @post @result is a String with the device data
       */

   String getDeviceString() {

      StringBuffer line = new StringBuffer( BLANK_LINE );
      String deviceDataString = ((report.deviceID.charAt(0) == 'S')
                                  ? "Sensor " : "Valve ") + report.deviceID;
      int start = 20 - (deviceDataString.length()/2);
      line.replace( start, start+deviceDataString.length(), deviceDataString );
      return line.toString();
   }

      /**
       *  Put the failed device's zone centered in a line.
       *
       *     @pre none
       *    @post @result is a String with the labeled zone identifier
       */

   String getZoneString() {

      StringBuffer line = new StringBuffer( BLANK_LINE );
      String zoneDataString = "Zone "+ report.zoneID;
      int start = 20 - (zoneDataString.length()/2);
      line.replace( start, start+zoneDataString.length(), zoneDataString );
      return line.toString();
   }

      /**
       *  Put the failed device's location centered in a line.
       *
       *     @pre none
       *    @post @result is a String with the location
       */

   String getLocationString() {

      StringBuffer line = new StringBuffer( BLANK_LINE );
      int start = 20 - (report.location.length()/2);
      line.replace( start, start+report.location.length(), report.location );
      return line.toString();
   }

      /**
       *  Put the failed device's location centered in a line.
       *
       *     @pre none
       *    @post @result is a String with the location
       */

   boolean isRecorded() { return report.isRecorded; }

} // DeviceFailureScrnState
