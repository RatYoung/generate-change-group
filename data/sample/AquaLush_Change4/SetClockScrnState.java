
package ui;

/**
 *  The SetClockScrnState class keeps track of the current day and time
 *  display in the setClockScrn, including processing keypresses, and 
 *  handles changing the time if directed to do so.
 *
 *  Note that many operations are synchronized. This is because the thread
 *  running the Clock may also call operations that change the attributes,
 *  so we must be careful of interference.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DisplayDevice;
import device.KeyPress;
import util.Day;

class SetClockScrnState extends ScreenState {

      /* attributes
      /*************/

   private StringBuffer time;      // the displayed time
   private String       day;       // the displayed day
   private boolean      isChanged; // whether the user has changed the day/time

      /* constructors
      /***************/

   SetClockScrnState() {
      time      = new StringBuffer( milTime.format(clock.getTime()) );
      day       = clock.getDay().toString();
      isChanged = false;
   }

      /**
       *  Fetch the displayed day and put it in a line with the required
       *  label.
       *
       *     @pre none
       *    @post @return is a String with the displayed day
       */

   String getDayString() {

         // if the user has not changed the display, get the current day
      if ( !isChanged ) day = clock.getDay().toString();

      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace( 11, 23, "Current Day:" );
      line.replace( 24, 24+day.length(), day );
      return line.toString();
   }

      /**
       *  Fetch the displayed time string and put it in a line with the
       *  required label.
       *
       *     @pre none
       *    @post @result is a String with the current time centered in it
       */

   String getTimeString() {

         // if the user has not changed the display, get the current day
      if ( !isChanged )
         time = new StringBuffer( milTime.format(clock.getTime()) );

      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace( 10, 23, "Current Time:" );
      line.replace( 24, 24+time.length(), time.toString() );
      return line.toString();
   }

      /**
       *  Change the current day displayed (as directed by the user).
       *  Note that the whichDay values are associated with days based on
       *  the setClockScrn screen buttons.
       *
       *     @pre 0 <= whichDay.intValue() < 7
       *    @post the displayed day is changed as directed, or an exception is
       *          thrown
       *   @param whichBtn The screen button pressed by the user
       *  @throws IllegalArgumentException if @pre is violated
       */

   void setDay( Integer whichBtn ) {

      int whichDay = whichBtn.intValue();
      if ( (whichDay < 0) || (6 < whichDay) )
         throw new IllegalArgumentException( "Screen button out of range." );

      isChanged = true;
      switch ( whichDay ) {
         case 0 : day = "Monday";    break;
         case 1 : day = "Friday";    break;
         case 2 : day = "Tuesday";   break;
         case 3 : day = "Saturday";  break;
         case 4 : day = "Wednesday"; break;
         case 5 : day = "Sunday";    break;
         case 6 : day = "Thursday";  break;
      }

   } // setDay
         
      /**
       *  Process keypad key presses for specifying a new time.
       *  Note that:
       *    - any illegal keypress elicits a beep
       *    - the DEL key erases characters until the end of the line
       *    - at most 4 characters may be specified
       *    - the 1st character must be 0, 1, or 2
       *    - the 2nd character cannot be greater than 3 if the first is 2
       *    - the third cannot be greater than 5
       *    - the ESC key returns to the current time display
       *
       *     @pre none
       *    @post the display is changed as directed or the terminal beeps
       *   @param key The keypad key pressed by a user
       */

   void keyPress( KeyPress key ) {

         // if the escape key is pressed, then reset everything
      if ( key == KeyPress.ESC_KEY ) {
         time = new StringBuffer( milTime.format(clock.getTime()) );
         day = clock.getDay().toString();
         isChanged = false;
         return;
      }

         // process non-ESC keys
      int timeLength = time.length();
      if ( key == KeyPress.DEL_KEY ) {
         if ( timeLength == 0 ) beep();
         else {
            time.deleteCharAt( timeLength-1 );
            isChanged = true;
         }
      }
      else if ( timeLength == 4 ) beep();
      else {
         int ordinal = key.toInt();
         if ( ((timeLength == 0) && (2 < ordinal)) ||
              ((timeLength == 1) && (time.charAt(0) == '2') && (3 < ordinal)) ||
              ((timeLength == 2) && (5 < ordinal)) ) beep();
         else {
            time.append( key.toChar() );
            isChanged = true;
         }
      }

   } // keyPress

      /**
       *  Reset the Clock as specified
       *
       *     @pre none
       *    @post if a valid new day/time is specified, the Clock is reset;
       *          otherwise, nothing is changed; in any case, attributes
       *          are reset to prepare for the next screen activation.
       *   @param whichBtn The screen button pressed by the user--ignored
       */

   void acceptSettings( Integer whichBtn ) {

         // do nothing if the day/time is not changed
      if ( !isChanged ) return;

         // 
         // change the day/time only if a valid time is specified
      if ( time.length() == 4 ) {
         try {
            int timeValue = Integer.parseInt( time.toString() );
            clock.setTime( timeValue );
            clock.setDay( Day.valueOf(day) );
         }
         catch ( NumberFormatException e ) {
            throw new Error( "Time parsing exception." );
         }
      }

         // reset attributes so everything will be ready next time
      time = new StringBuffer( milTime.format(clock.getTime()) );
      isChanged = false;

   } // acceptSettings

} // SetClockScrnState
