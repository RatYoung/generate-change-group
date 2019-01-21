
package ui;

/**
 *  The SetLevelScrnState class keeps track of the critical moisture level
 *  for all the zones. It also processes keypresses and handles changing 
 *  the critical moisture levels if directed to do so.
 *
 *  Note: In the Item objects, the text field is the line displayed, the
 *        value1 field is the zoneID, and the value2 field is the critical
 *        moisture level (as a String).
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DisplayDevice;
import device.KeyPress;
import irrigation.Irrigator;
import irrigation.ZoneReport;

import java.util.Collection;

class SetLevelScrnState extends ScreenState implements ScrollingScreenState {

      /* attributes
      /*************/

   private static final int MAX_DIGITS = 3;  // longest valid level spec

   private final Irrigator irrigator;   // for querying and changing levels

   private boolean isChanged;   // true iff user has changed the levels
   private Item[]  items;       // displayed in the scrolled region
   private int     crntItem;    // currently selected item

      /* constructors
      /***************/

   SetLevelScrnState( Irrigator theIrrigator ) {

      irrigator = theIrrigator;
      isChanged = false;
   }

      /* methods
      /**********/

      /**
       *  Return the array of items scrolled in the display, creating it
       *  if necessary.
       *
       *     @pre none
       *    @post @result is an array of items with the displayed text
       *          and zone identifier values.
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
            "Current item setting is our of range." );

      crntItem = theItem;
   }

   public int getCurrentItem() { return crntItem; }

      /**
       *  Process keypad key presses for specifying a new moisture level.
       *  Note that:
       *    - any illegal keypress elicits a beep
       *    - the DEL key erases characters until the start of the field
       *    - at most 3 characters may be specified
       *    - the 1st character cannot be 0
       *    - there can be a 3rd character only if the first two are "10"
       *    - the 3rd character can only be a 0
       *    - the ESC key resets all moisture levels
       *
       *     @pre none
       *    @post the display is changed as directed or the terminal beeps
       *   @param key  The keypad key pressed by a user
       */

   void keyPress( KeyPress key ) {

         // if the escape key is pressed, then reset everything
      if ( key == KeyPress.ESC_KEY ) {
         createItems();
         isChanged = false;
         return;
      }

         // process non-ESC keys
      StringBuffer levelBuffer = new StringBuffer( items[crntItem].value2 );
      int levelLength = levelBuffer.length();

      if ( key == KeyPress.DEL_KEY ) {
         if ( levelLength == 0 ) {
            beep();
            return;
         }
         levelBuffer.deleteCharAt( levelLength-1 );
         isChanged = true;
      }
      else if ( levelLength == MAX_DIGITS ) beep();
      else {
         int ordinal = key.toInt();
         if ( (levelLength == 0) && (ordinal == 0) ) {
            beep();
            return;
         }
         if ( (levelLength == 2)
              && (    (levelBuffer.charAt(0) != '1') 
                   || (levelBuffer.charAt(1) != '0')
                   || (ordinal != 0) ) ) {
            beep();
            return;
         }
         levelBuffer.append( key.toChar() );
         isChanged = true;
      }

         // redo the current line
      StringBuffer textBuffer = new StringBuffer( items[crntItem].text );
      int start = 12-levelBuffer.length();
      textBuffer.replace( 9, 12, "   " );
      textBuffer.replace( start, 12, levelBuffer.toString() );
      items[crntItem] = new Item( textBuffer.toString(), 
                                  items[crntItem].value1,
                                  levelBuffer.toString() );

   } // keyPress

      /**
       *  If anything is altered, change the moisture levels for all zones
       *  for which a valid (non-empty) moisture level is specified.
       *
       *     @pre none
       *    @post if the user has made changes, change the irrigation levels
       *          for all zones for which a valid specification is present.
       *   @param whichBtn  The screen button pressed by the user--ignored
       */

   void acceptSettings( Integer whichBtn ) {

         // do nothing if no specification is changed
      if ( !isChanged ) return;

         // set the critical moisture levels for each zone (if valid)
      for ( int i = 0; i < items.length; i++ ) {
         if ( 0 < items[i].value2.length() ) {
            try {
               int level = Integer.parseInt( items[i].value2 );
               irrigator.setCriticalMoistureLevel( items[i].value1, level );
            }
            catch ( NumberFormatException e ) {
               throw new Error( "Moisture level parsing exception." );
            }
         }
      }

         // reset attributes so everything will be ready next time
      createItems();
      isChanged = false;

   } // acceptSettings

      /**
       *  Make the current zone's level the level for all zones.
       *
       *     @pre none
       *    @post The current zone's level is made the level for all zones,
       *          even if it is empty.
       *   @param whichBtn  The screen button pressed by the user--ignored
       */

   void propagateSetting( Integer whichBtn ) {

         // change the displayed levels for each zone to current zone's level
      for ( int i = 0; i < items.length; i++ ) {
         StringBuffer textBuffer = new StringBuffer( items[i].text );
         int start = 12-items[crntItem].value2.length();
         textBuffer.replace( 9, 12, "   " );
         textBuffer.replace( start, 12, items[crntItem].value2 );
         items[i] = new Item( textBuffer.toString(),
                              items[i].value1,
                              items[crntItem].value2 );
      }

      isChanged = true;

   } // propagateSetting

      /* private methods
      /******************/

      /**
       *  Create an array of Items that will be scrolled through.
       *  Note that the text of the items goes from the beginning of
       *  the displayed line through the end of the location. This is
       *  to make it possible to know where to place the highlighting
       *  for the current item when the text is displayed.
       */

   private void createItems() {

      Collection<ZoneReport> zoneReports = irrigator.getZoneReports();
      items = new Item[zoneReports.size()];
      if ( items.length <= crntItem ) crntItem = items.length - 1;

         // create the arrays of items and levelBuffers
      int cnt = 0;
      for ( ZoneReport report : zoneReports ) {
         String zoneID           = report.id;
         String levelString      = String.valueOf( report.criticalLevel );
         StringBuffer textBuffer = new StringBuffer( BLANK_LINE );

            // put in the zone id
         textBuffer.replace( 3, 3+zoneID.length(), zoneID );

            // put in the level
         int start = 12-levelString.length();
         textBuffer.replace( start, 12, levelString );

            // append the location
         textBuffer.setLength( 18 );
         textBuffer.append( report.location );

            // create an item with the text, zoneID, and levelString
         items[cnt++] = new Item( textBuffer.toString(),
                                  zoneID, levelString );
      }

         // insertion sort the arrays by the item.value1 field (zoneID)
      for ( int i = 1; i < items.length; i++ ) {
         Item tmp = items[i];
         int j = i-1;
         while ( (0 <= j) && itemIsLessThan(tmp,items[j]) ) {
            items[j+1] = items[j];
            j--;
         }
         items[j+1] = tmp;
      }

   } // createItems

      /**
       *  Compare the value fields of two items and return true iff 
       *  item1.value1 < item2.value1.
       */

   private boolean itemIsLessThan( Item item1, Item item2 ) {

      return item1.value1.compareTo(item2.value1) < 0;
   }

} // SetLevelScrnState
