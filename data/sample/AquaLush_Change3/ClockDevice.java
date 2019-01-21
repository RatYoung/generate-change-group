
package device;

/**
 *  The ClockDevice interface specifies the operations that any virtual
 *  clock must implement.
 *  Note that:
 *    - clocks use military time accurate to one minute
 *    - keep the day of the week using the Day enumeration
 *    - do not keep the date.
 *
 *   @author C. Fox
 *  @version 06/06
 */

import util.Day;

public interface ClockDevice {

      /**
       *  Set the hour and minute on the clock.
       *  Note that the clock is only accurate to one minute.
       *  
       *     @pre milTime is a valid military time specification
       *    @post the clock device is changed
       *   @param milTime  The current time in military notation. Military
       *                   time notation is in the range 0..2359, with the
       *                   last two digits in the range 0..59
       *  @throws IllegalArgumentException if precondition violated
       */

   void setTime( int milTime );

      /**
       *  Get the time on the clock.
       *  
       *    @pre none
       *   @post @return is the current time as in integer in military time
       *         notation, hence in the range 0..2359 (with gaps).
       */

   int getTime();

      /**
       *  Set the day of the week on the clock.
       *  
       *    @pre none
       *   @post the clock device is changed
       */

   void setDay( Day dayOfTheWeek );

      /**
       *  Get the day of the week from the clock.
       *  
       *    @pre none
       *   @post @result is a Day value
       */

   Day getDay();

      /**
       *  Register for tick notifications every minute. Only a single tick
       *  listener may be registered at one time. Registering a null object
       *  in effect deregisters all objects.
       *  
       *    @pre none
       *   @post if listener == null no object is notified;
       *         otherwise listener.tick() is called every minute
       *  
       *  @param listener  The object whose tick() operation will be called
       *                   every minute.
       */

   void setListener( TickListener listener );

} // ClockDevice
