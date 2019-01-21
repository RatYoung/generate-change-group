
package ui;

/**
 *  The ManualControlScrnState class keeps track of the progress of manual
 *  irrigation for display to the operator. It also processes key and
 *  button presses that control irrigation. It has two sorts of instances:
 *  one manages display of irrigation data and the other display of valve
 *  locations. Otherwise, these instances behave identically.
 *
 *  Note: In the Item objects, the text field is the line displayed, the
 *        value1 field is the zoneID, and the value2 field is the valveID.
 *
 *   @author C. Fox
 *  @version 07/26
 *
 *  @inv if 0 < items.length then 0 <= crntItem < items.length
 *       else crntItem is undefined
 */

import device.DeviceFailureException;
import device.DisplayDevice;
import device.KeyPress;
import irrigation.Irrigator;
import irrigation.ValveReport;

import java.util.List;

class ManualControlScrnState extends ScreenState 
                             implements ScrollingScreenState {

      /* attributes
      /**************/

   private final Irrigator irrigator;   // for querying and controlling valves
   private final boolean   isDataScrn;  // is instance for data or location?

   private Item[]          items;       // displayed in the scrolled region
   private int             crntItem;    // currently selected item

      /* constructors
      /***************/

   ManualControlScrnState( Irrigator theIrrigator, boolean isTheDataScrn ) {

      irrigator  = theIrrigator;
      isDataScrn = isTheDataScrn;
   }

      /* methods
      /**********/

      /**
       *  Return the array of items scrolled in the display, creating it
       *  if necessary.
       *
       *     @pre none
       *    @post @result is an array of items with the displayed text
       *          and valve identifier values.
       */

   public Item[] getItems() {

      if ( items == null ) {
         if ( isDataScrn ) createDataItems();
         else              createLocationItems();
      }
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

   String getGallonsUsedString() {

      int gallonsUsed = irrigator.getWaterUsed();
      String gallonsUsedString = String.valueOf(gallonsUsed) + " Gallons Used";
      StringBuffer line = new StringBuffer( BLANK_LINE );
      int start = 20 - (gallonsUsedString.length()/2); 
      line.replace(start, start+gallonsUsedString.length(), gallonsUsedString);
      return line.toString();
   }

      /**
       *  Regenerate the items data for display.
       *
       *     @pre none
       *    @post the items are regenerated for an updated display
       */

   void updateData() {

      if ( isDataScrn ) createDataItems();
      else              createLocationItems();
   }

      /**
       *  Tell the irrigator to start a manual irrigation cycle.
       *
       *     @pre none
       *    @post a new cycle is started (or continued if under way); the
       *          items are regenerated in case they have changed
       */

   void startCycle() {

      irrigator.startManualCycle();
      if ( isDataScrn ) createDataItems();
      else              createLocationItems();
   }

      /**
       *  Tell the irrigator to stop the current manual irrigation cycle.
       *
       *     @pre none
       *    @post the current cycle is stopped
       */

   void stopCycle() {

      irrigator.stopManualCycle();
   }

      /**
       *  Tell the irrigator to stop the current manual irrigation cycle if
       *  the escate key is pressed.
       *
       *     @pre none
       *    @post the current cycle is stopped
       */

   void keyPress( KeyPress key ) {

      if ( key == KeyPress.ESC_KEY ) irrigator.stopManualCycle();

   }

      /**
       *  Tell the irrigator to open a closed valve or close an open valve.
       *
       *     @pre crntItem must be defined
       *    @post the state of the current valve is reversed; if there is
       *          no current valve, nothing happens
       */

   void toggleCurrentValve() {

         // check precondition
      if ( items.length == 0 ) return;

         // toggle the valve
      irrigator.toggleValve( items[crntItem].value1, items[crntItem].value2 );

         // since the valve may have changed, we need to change the items
      if ( isDataScrn ) createDataItems();
      else              createLocationItems();
   }

      /**
       *  Tell the irrigator to open all the valves in the current valve's 
       *  zone if the current valve is open, or vice-versa.
       *
       *     @pre none
       *    @post all the valves in the current valve's zone are set to
       *          the current valve's state; if there is no current valve,
       *          nothing happens
       */

   void propagateCurrentValve() {

         // check precondition
      if ( items.length == 0 ) return;

         // find out the state of the current valve
      boolean isOpen = items[crntItem].text.charAt(3) == '+';

         // set all the valves in the current valve's zone
      if ( isOpen )
         irrigator.openAllValves( items[crntItem].value1 );
      else
         irrigator.closeAllValves( items[crntItem].value1 );

         // since the valve may have changed, we need to change the items
      if ( isDataScrn ) createDataItems();
      else              createLocationItems();

   } // propagateCurrentValve

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

   private void createDataItems() {

      List<ValveReport> valveReports = irrigator.getValveReports();

         // count the number of working valves
      int numWorkingValves = 0;
      for ( ValveReport report : valveReports )
         if ( !report.isFailed ) numWorkingValves++;

         // create an Item array for all the working valves
      items = new Item[numWorkingValves];
      if ( items.length == 0 ) return;

         // adjust the crntItem if it is out of range
      if ( items.length <= crntItem ) crntItem = items.length - 1;

         // create the arrays of items and levelBuffers
      int cnt = 0;
      for ( ValveReport report : valveReports ) {

            // skip failed valves
         if ( report.isFailed ) continue;

         String       valveID    = report.id;
         String       zoneID     = report.zoneID;
         StringBuffer textBuffer = new StringBuffer( BLANK_LINE );

            // put in the valve open status
         textBuffer.setCharAt( 3, (report.isOpen ) ? '+' : '-' );

            // put in the valve id
         textBuffer.replace( 4, 4+valveID.length(), valveID );

            // put in the zone id
         textBuffer.replace( 9, 9+zoneID.length(), zoneID );

            // put in the moisture level
         String levelString;  // level or -- if sensor has failed
         try {
            levelString 
               = String.valueOf( irrigator.getMeasuredMoistureLevel(zoneID) );
          }
          catch ( DeviceFailureException e ) {
            levelString = "--";
          }
         textBuffer.replace( 17-levelString.length(), 17, levelString );

            // put in the time open
         int timeOpen = (report.minutesOpen/60)*100 + report.minutesOpen%60;
         textBuffer.replace( 19, 23, milTime.format(timeOpen) );

            // put in the water used
         String waterUsed = String.valueOf(report.flowRate*report.minutesOpen);
         textBuffer.replace( 37-waterUsed.length(), 37, waterUsed );

            // shorten the buffer
         textBuffer.setLength( 37 );

            // create an item for this line
         items[cnt++] = new Item( textBuffer.toString(), zoneID, valveID );
      }

         // insertion sort the item arrays by the item value1 field
         // (zoneID) and then by the item value2 field (valveID)
      for ( int i = 1; i < items.length; i++ ) {
         Item tmp = items[i];
         int j = i-1;
         while ( (0 <= j) && itemIsLessThan(tmp,items[j]) ) {
            items[j+1] = items[j];
            j--;
         }
         items[j+1] = tmp;
      }

   } // createDataItems

      /**
       *  Create an array of Items that will be scrolled through on the
       *  manual irrigation location screen.
       *
       *  Note that the text of the items goes from the beginning of
       *  the displayed line through the end of the location. This is
       *  to make it possible to know where to place the highlighting
       *  for the current item when the text is displayed.
       */

   private void createLocationItems() {

      List<ValveReport> valveReports = irrigator.getValveReports();

         // count the number of working valves
      int numWorkingValves = 0;
      for ( ValveReport report : valveReports )
         if ( !report.isFailed ) numWorkingValves++;

         // create an Item array for all the working valves
      items = new Item[numWorkingValves];
      if ( items.length == 0 ) return;

         // adjust the crntItem if it is out of range
      if ( items.length <= crntItem ) crntItem = items.length - 1;

         // create the arrays of items and levelBuffers
      int cnt = 0;
      for ( ValveReport report : valveReports ) {

            // skip failed valves
         if ( report.isFailed ) continue;

         String       valveID    = report.id;
         String       zoneID     = report.zoneID;
         StringBuffer textBuffer = new StringBuffer( BLANK_LINE );

            // put in the valve open status
         textBuffer.setCharAt( 3, (report.isOpen ) ? '+' : '-' );

            // put in the valve id
         textBuffer.replace( 4, 4+valveID.length(), valveID );

            // put in the zone id
         textBuffer.replace( 9, 9+zoneID.length(), zoneID );

            // put in the location
         textBuffer.replace( 14, 14+report.location.length(), report.location );

            // shorten the buffer
         textBuffer.setLength( 37 );

            // create an item for this line
         items[cnt++] = new Item( textBuffer.toString(), zoneID, valveID );
      }

         // insertion sort the item arrays by the item value1 field
         // (zoneID) and then by the item value2 field (valveID)
      for ( int i = 1; i < items.length; i++ ) {
         Item tmp = items[i];
         int j = i-1;
         while ( (0 <= j) && itemIsLessThan(tmp,items[j]) ) {
            items[j+1] = items[j];
            j--;
         }
         items[j+1] = tmp;
      }

   } // createLocationItems

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

} // ManualControlScrnState
