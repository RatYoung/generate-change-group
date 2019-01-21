
package ui;

/**
 *  The FixFailuresScrnState class keeps track of failed devices and
 *  allows users to record that they are repaired.
 *
 *  Note: In the Item objects, the text field is the line displayed, the
 *        value1 field is the zoneID, and the value2 field is the valveID.
 *
 *   @author C. Fox
 *  @version 07/06
 *
 *  @invariant if 0 < items.length then 0 <= crntItem < items.length
 *             else crntItem is undefined
 */

import device.DisplayDevice;
import irrigation.Irrigator;
import irrigation.FailureReport;
import irrigation.SensorReport;
import irrigation.ValveReport;

import java.util.Set;
import java.util.HashSet;

class FixFailuresScrnState extends ScreenState 
                           implements ScrollingScreenState {

      /* attributes
      /*************/

   private final Irrigator irrigator;    // for querying and fixing devices
 
   private FailureReport   report;       // lists of failed sensors and zones
   private Item[]          items;        // displayed in the scrolled region
   private int             crntItem;     // currently selected item
   private Set<String>     fixedDevices; // devices marked as repaired

      /* constructors
      /***************/

   FixFailuresScrnState( Irrigator theIrrigator ) {
      irrigator    = theIrrigator;
      fixedDevices = new HashSet<String>();
   }

      /* methods
      /**********/

      /**
       *  Return the array of items scrolled in the display, creating it
       *  if necessary.
       *
       *     @pre none
       *    @post @result is an array of items with the displayed text
       *          and device and zone identifier values.
       */

   public Item[] getItems() {

      if ( items == null ) createItems();
      return items;
   }

      /**
       *  Update the current item attribute to stay current with what is
       *  displayed.
       *
       *     @pre 0 <= theItem < items.length
       *    @post the current item attribute is set, or an exception is thrown
       *   @param theItem  The new current item value
       *  @throws IllegalArgumentException if @pre is violated
       */

   public void setCurrentItem( int theItem ) {

      if ( (theItem < 0) || (items.length <= theItem) )
         throw new IllegalArgumentException( 
            "Current item setting is out of range." );

      crntItem = theItem;
   }

   public int getCurrentItem() { return crntItem; }

      /**
       *  Make a line showing the gallons used, labelled appropriately.
       *
       *     @pre none
       *    @post @return is gallons used so far in the cycle
       */

   String getSummaryString() {

      StringBuffer line  = new StringBuffer( BLANK_LINE );

         // write the number of failed sensors
      int numFailures    = report.failedSensors.size();
      String countString = String.valueOf( numFailures ) 
                              + ((numFailures == 1 ) ? " Failed Sensor, "
                                                     : " Failed Sensors, ");

         // write the number of failed valves
      numFailures  = report.failedValves.size();
      countString += String.valueOf( numFailures ) 
                              + ((numFailures == 1 ) ? " Failed Valve"
                                                     : " Failed Valves");

      line.replace( 20-(countString.length()/2),
                    20+countString.length(), countString );

      return line.toString();
   }

      /**
       *  Generate the items data for display with none marked for repair.
       *
       *     @pre none
       *    @post the items are regenerated for an updated display
       */

   void displayData() {

      report = irrigator.getFailureReport();
      fixedDevices.clear();
      createItems();
   }

      /**
       *  Regenerate the items data for display.
       *
       *     @pre none
       *    @post the items are regenerated for an updated display
       */

   void updateData() {

      report = irrigator.getFailureReport();
      createItems();
   }

      /**
       *  Mark the current device as repaired.
       *
       *     @pre crntItem must be defined
       *    @post the current device is registered as repaired
       */

   void markCurrentDevice() {

         // check the precondition
      if ( items.length == 0 ) return;

         // mark the selected item as repaired
      fixedDevices.add( items[crntItem].value2 );

         // regenerate the items
      updateData();

   } // markCurrentDevice

      /**
       *  Tell the irrigator about the repaired devices.
       *
       *     @pre none
       *    @post the irrigator is told about all the devices marked as 
       *          repaired
       */

   void recordRepairs() {

      for ( String deviceID : fixedDevices )
         irrigator.repairDevice( deviceID );
   }

      /* private methods
      /******************/

      /**
       *  Create an array of Items that will be scrolled through on the
       *  manual irrigation data screen.
       *
       *  Note that the text of the items goes from the beginning of
       *  the displayed line through the end of the location. This is
       *  to make it possible to know where to place the highlighting
       *  for the current item when the text is displayed.
       */

   private void createItems() {

      int numSensors = report.failedSensors.size();
      int numValves  = report.failedValves.size();
      int numDevices = numSensors + numValves - fixedDevices.size();

         // create an Item array for the failed devices
      items = new Item[numDevices];
      if ( items.length == 0 ) return;

         // adjust the crntItem if it is out of range
      if ( items.length <= crntItem ) crntItem = items.length - 1;

         // populate the array of items, sensors first, then valves
      int cnt = 0;
      for ( SensorReport sensorReport : report.failedSensors ) {

            // skip any repaired sensors
         if ( fixedDevices.contains(sensorReport.id) ) continue;

         StringBuffer textBuffer = new StringBuffer( BLANK_LINE );

            // put in the device id
         String deviceID = sensorReport.id;
         textBuffer.replace( 3, 3+deviceID.length(), deviceID );

            // put in the zone id
         String zoneID = sensorReport.zoneID;
         textBuffer.replace( 9, 9+zoneID.length(), zoneID );

            // put in the location
         String location = sensorReport.location;
         textBuffer.replace( 14, 14+location.length(), location );

            // shorten the buffer
         textBuffer.setLength( 14+location.length() );

            // create an item for this line
         items[cnt++] = new Item( textBuffer.toString(), zoneID, deviceID );
      }

      for ( ValveReport valveReport : report.failedValves ) {

            // skip any repaired valves
         if ( fixedDevices.contains(valveReport.id) ) continue;

         StringBuffer textBuffer = new StringBuffer( BLANK_LINE );

            // put in the device id
         String deviceID = valveReport.id;
         textBuffer.replace( 3, 3+deviceID.length(), deviceID );

            // put in the zone id
         String zoneID = valveReport.zoneID;
         textBuffer.replace( 9, 9+zoneID.length(), zoneID );

            // put in the location
         String location = valveReport.location;
         textBuffer.replace( 14, 14+location.length(), location );

            // shorten the buffer
         textBuffer.setLength( 14+location.length() );

            // create an item for this line
         items[cnt++] = new Item( textBuffer.toString(), zoneID, deviceID );
      }

         // insertion sort the sensors portion of the item arrays by the 
         // item value1 field (zoneID) and then by the item value2 field 
         // (deviceID)
      for ( int i = 1; i < numSensors; i++ ) {
         Item tmp = items[i];
         int j = i-1;
         while ( (0 <= j) && itemIsLessThan(tmp,items[j]) ) {
            items[j+1] = items[j];
            j--;
         }
         items[j+1] = tmp;
      }

         // insertion sort the valves portion of the item arrays by the 
         // item value1 field (zoneID) and then by the item value2 field 
         // (deviceID)
      for ( int i = numSensors+1; i < items.length; i++ ) {
         Item tmp = items[i];
         int j = i-1;
         while ( (numSensors <= j) && itemIsLessThan(tmp,items[j]) ) {
            items[j+1] = items[j];
            j--;
         }
         items[j+1] = tmp;
      }

   } // createDataItems

      /**
       *  Compare the value fields of two items and return true iff either
       *  item1.value1 < item2.value1, or item1.value1 == item2.value1 and 
       *  item1.value2 < item2.value2
       */

   private boolean itemIsLessThan( Item item1, Item item2 ) {

      return   (item1.value1.compareTo(item2.value1) < 0)
               || (     item1.value1.equals(item2.value1)
                    && (item1.value2.compareTo(item2.value2) < 0) );
   }

} // FixFailuresScrnState
