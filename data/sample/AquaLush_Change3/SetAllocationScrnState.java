
package ui;

/**
 *  The SetAllocationScrnState class keeps track of the display for the
 *  irrigation water allocation. It also processes keypresses and handles 
 *  changing the allocation as directed by the user.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import device.DisplayDevice;
import device.KeyPress;
import irrigation.Irrigator;

class SetAllocationScrnState extends ScreenState {

      /* attributes
      /*************/

   private final static int MAX_DIGITS = 9;  // largest allowed allocation

   private final Irrigator irrigator;  // for querying and changing allocations

   private StringBuffer allocation; // the displayed allocation
   private boolean      isChanged;  // true iff user has changed allocation

      /* constructors
      /***************/

   SetAllocationScrnState( Irrigator theIrrigator ) {
      irrigator  = theIrrigator;
      allocation = new StringBuffer(String.valueOf(irrigator.getAllocation()));
      isChanged  = false;
   }

      /**
       *  Put the allocation in a line with the required label.
       *
       *     @pre none
       *    @post @return is a String with the allocation
       */

   String getAllocationString() {

         // format the line with the allocation and return it
      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace(  7, 25, "Gallons Allocated:" );
      line.replace( 26, 26+allocation.length(), allocation.toString() );
      return line.toString();
   }

      /**
       *  Process keypad key presses for specifying a new allocation.
       *  Note that:
       *    - any illegal keypress elicits a beep
       *    - the DEL key erases characters until the start of the field
       *    - at most MAX_DIGITS digits may be specified in an allocation
       *    - the ESC key returns to the current allocation
       *
       *     @pre none
       *    @post the display is changed as directed or the terminal beeps
       *   @param key  The keypad key pressed by a user
       */

   void keyPress( KeyPress key ) {

         // if the escape key is pressed, then reset everything
      if ( key == KeyPress.ESC_KEY ) {
         allocation = new StringBuffer(
                             String.valueOf(irrigator.getAllocation()) );
         isChanged  = false;
         return;
      }

         // process non-ESC keys
      if ( key == KeyPress.DEL_KEY ) {
         if ( allocation.length() == 0 ) beep();
         else {
            allocation.deleteCharAt( allocation.length()-1 );
            isChanged = true;
         }
      }
      else if ( MAX_DIGITS <= allocation.length() ) beep();
      else {
         allocation.append( key.toChar() );
         isChanged = true;
      }

   } // keyPress

      /**
       *  Reset the allocation as specified
       *
       *     @pre none
       *    @post if a valid allocation is specified, it becomes the new
       *          allocation; otherwise, nothing is changed; in any case, 
       *          attributes are reset to prepare for the next activation.
       *   @param whichBtn  The screen button pressed by the user--ignored
       */

   void acceptSettings( Integer whichBtn ) {

         // do nothing if the allocation is not changed
      if ( !isChanged ) return;

         // change the allocation (if it is a positive number)
      if ( 0 < allocation.length() ) {
         try {
            int gallons = Integer.parseInt( allocation.toString() );
            if ( 0 <= gallons ) irrigator.setAllocation( gallons );
         }
         catch ( NumberFormatException e ) {
            throw new Error( "Allocation parsing exception." );
         }
      }

         // reset attributes so everything will be ready next time
      allocation = new StringBuffer(String.valueOf(irrigator.getAllocation()));
      isChanged = false;

   } // acceptSettings

} // SetAllocationScrnState
