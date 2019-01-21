
package ui;

/**
 *  The MainScrnState class hosts operations that obtain and format the
 *  current day and time for display on a main screen.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import java.util.Collection;

import device.DisplayDevice;
import irrigation.Irrigator;
import irrigation.FailureReport;
import irrigation.SensorReport;
import irrigation.ValveReport;

class MainScrnState extends ScreenState {

      /* attributes
      /*************/

   private final Irrigator irrigator;   // for obtaining failure reports
   private FailureReport   report;      // holder for failed device reports

      /* constructors
      /***************/

   MainScrnState( Irrigator theIrrigator ) {

      irrigator = theIrrigator;
   }

      /* methods
      /**********/

      /**
       *  Fetch the current day and put it centered in a line as long as the 
       *  display screen is wide.
       *
       *     @pre none
       *    @post @result is a String with the current day centered in it
       */

   String getDayString() {
      String day = clock.getDay().toString();
      StringBuffer line = new StringBuffer( BLANK_LINE );
      int start= DisplayDevice.NUM_COLS/2 - day.length()/2; 
      line.replace( start, start+day.length(), day );
      return line.toString();
   }

      /**
       *  Fetch the current time and put it centered, in military time format,
       *  in a line as long as the display screen is wide.
       *
       *     @pre none
       *    @post @result is a String with the current time centered in it
       */

   String getTimeString() {
      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace( DisplayDevice.NUM_COLS/2 - 2, 22,
                     milTime.format(clock.getTime()) );
      return line.toString();
   }

      /**
       *  Fetch the latest failure report from the irrigator.
       *
       *     @pre none
       *    @post the report attribute is updated
       */

   void updateFailures() {

      report = irrigator.getFailureReport();
   }

      /**
       *  Fetch the number of failed valves and put it centered
       *  in a line as long as the display screen is wide.
       *
       *     @pre none
       *    @post @result is a String with the current time centered in it
       */

   String getFailedValveCount() {

      StringBuffer line  = new StringBuffer( BLANK_LINE );
      int numFailures    = report.failedValves.size();
      String countString = String.valueOf( numFailures ) 
                              + ((numFailures == 1 ) ? " Failed Valve"
                                                     : " Failed Valves");

      line.replace( 20-(countString.length()/2),
                    20+countString.length(), countString );

      return line.toString();
   }

      /**
       *  Fetch the number of failed sensors and put it centered
       *  in a line as long as the display screen is wide.
       *
       *     @pre none
       *    @post @result is a String with the current time centered in it
       */

   String getFailedSensorCount() {

      StringBuffer line  = new StringBuffer( BLANK_LINE );
      int numFailures    = report.failedSensors.size();
      String countString = String.valueOf( numFailures ) 
                              + ((numFailures == 1 ) ? " Failed Sensor"
                                                     : " Failed Sensors");

      line.replace( 20-(countString.length()/2),
                    20+countString.length(), countString );

      return line.toString();
   }

} // MainScrnState
