
package ui;

/**
 *  The SetTimesScrnState class keeps track of the time and the days
 *  when irrigation is supposed to occur, as displayed in the setTimesScrn.
 *  It also processes keypresses and handles changing the time if directed 
 *  to do so.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DisplayDevice;
import device.KeyPress;
import irrigation.Irrigator;
import util.Day;

import java.util.HashSet;
import java.util.Set;

class SetTimesScrnState extends ScreenState {

      /* attributes
      /*************/

   private static final int MIL_DIGITS = 4;  // how long a valid time must be

   private final Irrigator irrigator; // to query and set irrigation times

   private StringBuffer time;      // the displayed irrigation time
   private String       days;      // the displayed irrigation days
   private Set<Day>     daySet;    // keep track of irrigation days
   private boolean      isChanged; // whether the user has changed the days/time

      /* constructors
      /***************/

   SetTimesScrnState( Irrigator theIrrigator ) {

      irrigator = theIrrigator;
      time = new StringBuffer( milTime.format(irrigator.getIrrigationTime()) );
      daySet = new HashSet<Day>();
      daySet.addAll( irrigator.getIrrigationDays() );
      days = daySetToString( daySet );
      isChanged = false;
   }

      /* methods
      /**********/

      /**
       *  Put the irrigation days in a line with the required label.
       *
       *     @pre none
       *    @post @result is a String with the irrigation days
       */

   String getDaysString() {

      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace(  6, 22, "Irrigation Days:" );
      line.replace( 23, 23+days.length(), days );
      return line.toString();
   }

      /**
       *  Put the irrigation time in a line with the required label.
       *
       *     @pre none
       *    @post @result is a String with the irrigation time
       */

   String getTimeString() {

      StringBuffer line = new StringBuffer( BLANK_LINE );
      line.replace(  6, 22, "Irrigation Time:" );
      line.replace( 23, 23+time.length(), time.toString() );
      return line.toString();
   }

      /**
       *  Change the current irrigation days displayed (as directed by the 
       *  user). Note that the whichDay values are associated with days based
       *  on the setTimesScrn screen buttons.
       *
       *     @pre 0 <= whichDay.intValue() < 7
       *    @post the displayed day is changed as directed, or an exception is
       *          thrown
       *   @param whichBtn The screen button pressed by the user
       *  @throws IllegalArgumentException if @pre is violated
       */

   void toggleDay( Integer whichBtn ) {

         // check the precondition
      int whichDay = whichBtn.intValue();
      if ( (whichDay < 0) || (6 < whichDay) )
         throw new IllegalArgumentException( "Screen button out of range." );

         // convert the day number to a Day enumeration value
      Day toggleDay = null;
      switch ( whichDay ) {
         case 0 : toggleDay = Day.MONDAY;    break;
         case 1 : toggleDay = Day.FRIDAY;    break;
         case 2 : toggleDay = Day.TUESDAY;   break;
         case 3 : toggleDay = Day.SATURDAY;  break;
         case 4 : toggleDay = Day.WEDNESDAY; break;
         case 5 : toggleDay = Day.SUNDAY;    break;
         case 6 : toggleDay = Day.THURSDAY;  break;
      }

         // either add or remove the value from the set
      if ( daySet.contains(toggleDay) ) daySet.remove( toggleDay );
      else                              daySet.add( toggleDay );

         // update the display string
      isChanged = true;
      days = daySetToString( daySet );

   } // toggleDay
         
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
         time = new StringBuffer(milTime.format(irrigator.getIrrigationTime()));
         daySet.addAll( irrigator.getIrrigationDays() );
         days = daySetToString( daySet );
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
       *  If anything is altered, change the set of irrigation days, and
       *  the irrigation time, provided it is valid.
       *
       *     @pre none
       *    @post if the user has made changes, the irrigation days are 
       *          changed, and the irrigation time is changed it is a
       *          valid military time; otherwise, values are reset to
       *          what they were.
       *   @param whichBtn  The screen button pressed by the user--ignored
       */

   void acceptSettings( Integer whichBtn ) {

         // do nothing if the days or time are not changed
      if ( !isChanged ) return;

         // change the days
      irrigator.setIrrigationDays( daySet );

         // change the time only if a valid time is specified
      if ( time.length() == 4 ) {
         try {
            int timeValue = Integer.parseInt( time.toString() );
            irrigator.setIrrigationTime( timeValue );
         }
         catch ( NumberFormatException e ) {
            throw new Error( "Time parsing exception." );
         }
      }

         // reset attributes so everything will be ready next time
      time = new StringBuffer( milTime.format(irrigator.getIrrigationTime()) );
      isChanged = false;

   } // acceptSettings

      /* private methods
      /******************/

      /**
       *  Change a set of days to its string representation for display.
       *
       *   @param daySet  The set of irrigation days converted to a String
       */

   private String daySetToString( Set<Day> daySet ) {
      StringBuffer dayBuffer = new StringBuffer();
      if ( daySet.contains(Day.MONDAY) )    dayBuffer.append( "M" );
      if ( daySet.contains(Day.TUESDAY) )   dayBuffer.append( "Tu" );
      if ( daySet.contains(Day.WEDNESDAY) ) dayBuffer.append( "W" );
      if ( daySet.contains(Day.THURSDAY) )  dayBuffer.append( "Th" );
      if ( daySet.contains(Day.FRIDAY) )    dayBuffer.append( "F" );
      if ( daySet.contains(Day.SATURDAY) )  dayBuffer.append( "Sa" );
      if ( daySet.contains(Day.SUNDAY) )    dayBuffer.append( "Su" );
      return dayBuffer.toString();
   }

} // SetTimesScrnState
