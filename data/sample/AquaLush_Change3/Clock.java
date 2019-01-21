
package device;

/**
 *  The Clock object is the device used by the entire program (outside the
 *  simulation) to keep time.
 *
 *  Clock is a singleton class, so there can only be one instance of it.
 *
 *  Clock is also a subject in the Observer pattern, so many objects can
 *  register for time passage notification. The Clock object notifies 
 *  its observers every minute.
 *
 *  The Clock uses a ClockDevice to get the time. The ClockDevice must be
 *  registered with the Clock before it is instantiated or it will throw
 *  a NullPointerException.
 *
 *     @author  C. Fox
 *    @version  06/06
 *
 *  @invariant  hour is in range 0..23
 *              minute is in range 0..59
 */

import util.Day;
import device.ClockDevice;
import java.util.Observable;

public class Clock extends Observable implements TickListener {

      /* class attributes
       *******************/

   private static Clock       instance = null;  // the unique instance
   private static ClockDevice works    = null;  // drives the clock

      /* class methods
       ****************/
     
      /**
       *  Register the required ClockDevice.
       *
       *     @pre device != null
       *    @post @result == the single instance
       *  @throws IllegalArgumentException if precondition is violated
       */

   public static void setClockDevice( ClockDevice device ) {
      if ( device == null ) throw new IllegalArgumentException();
      works = device;
   }

      /**
       *  Return the single instance of the class.
       *
       *     @pre none
       *    @post @result == the single instance
       */

   public static Clock instance() {
      if ( instance == null ) instance = new Clock();
      return( instance );
   }


      /* constructors
      /***************/

      /**
       *  Create the unique Clock object.
       *
       *     @pre a ClockDevice is registered
       *    @post Clock is registered as a ClockDevice listener
       *  @throws NullPointerException if precondition violated
       */

   private Clock() {

      works.setListener( this );
   }

      /* methods
      /**********/

      /**
       *  Set the hour and minute on the clock.
       *  Note that the clock is only accurate to one minute.
       *  A valid military time spec is on the range 0..2359 with the last
       *  two digits in the range 0..59.
       *  
       *     @pre milTime is a valid military time specification
       *    @post the clock's time is changed
       *   @param milTime  The current time in military notation. Military
       *                   time notation is in the range 0..2359, with the 
       *                   last two digits in the range 0..59
       *  @throws  IllegalArgumentException if precondition violated
       */

   public void setTime( int milTime ) { works.setTime( milTime ); }

      /**
       *  Get the time on the clock.
       *  
       *    @pre none
       *   @post @return is the current time as in integer in military time
       *         notation, hence in the range 0..2359 (with gaps).
       */

   public int getTime() { return works.getTime(); }

      /**
       *  Set the day of the week on the clock.
       *  
       *     @pre none
       *    @post the clock is changed
       *   @param newDay  The day of the week in Day.MONDAY..Day.SUNDAY.
       */

   public void setDay( Day newDay ) { works.setDay( newDay ); }

      /**
       *  Get the day of the week from the clock.
       *  
       *    @pre  none
       *   @post  @result is a Day value
       */

   public Day getDay() { return works.getDay(); }

      /**
       *  Notify observers that time has passed.
       *  
       *    @pre none
       *   @post all observers are notified
       */

   public void tick() {
      setChanged();
      notifyObservers();
   }

} // Clock
