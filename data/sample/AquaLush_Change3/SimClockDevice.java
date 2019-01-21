
package device.sim;

/**
 *  A SimClockDevice is a virtual clock device used as the simulated basis
 *  for any program clock. A SimClockDevice object notifies its TickListener 
 *  every simulated second, calling its TickListener's tick() operation.
 *
 *  This device works by monitoring the time in the simulated environment,
 *  SimeTime, which is the basis for what happens in the simulated world.
 *
 *     @author  C. Fox
 *    @version  07/06
 */

import java.util.Observable;
import java.util.Observer;

import device.ClockDevice;
import device.TickListener;
import simulation.SimTime;
import util.Day;

class SimClockDevice implements ClockDevice, Observer {

      /* attributes
      /*************/

   private TickListener  listener;   // listener notified every minute
   private final SimTime time;       // simulated time driving this device

      /* constructors
      /***************/

      /**
       *  Create a new simulated clock device.
       */

   public SimClockDevice() {

      listener = null;
      time = SimTime.instance();
      time.addObserver( this );
   }

      /* methods
      /**********/

      /**
       *  Set the day of the week.
       *
       *     @pre none
       *    @post simulated time is changed
       *   @param newDay The day of the week in Day.MONDAY..Day.SUNDAY.
       */

   public void setDay( Day newDay ) {
      time.setDay( newDay );
   }

      /**
       *  Fetch the current day of the week.
       *
       *     @pre none
       *    @post @result is the simulated day of the week
       */

   public Day getDay() { return time.getDay(); }

      /**
       *  Set the clock time to the minute.
       *
       *     @pre newTime is a valid military time
       *    @post the clock is changed
       *   @param newTime  The new clock time in a military time number format,
       *                   so in the range 0..2359 (with gaps).
       *  @throws IllegalArgumentException if precondition violated
       */

   public void setTime( int newTime ) {

      int newHour   = newTime / 100;
      int newMinute = newTime % 100;
      if (   (newHour < 0) || (23 < newHour) ||
           (newMinute < 0) || (59 < newMinute) )
         throw new IllegalArgumentException();

      time.setHour( newHour );
      time.setMinute( newMinute );
   }

      /**
       *  Fetch the clock's current time as a military time format number.
       *
       *     @pre none
       *    @post A value in the range 0..2359 (with gaps) indicating the
       *          current time in a military time format value.
       */

   public int getTime() {
      return time.getHour()*100 + time.getMinute();
   }

      /**
       *  Register a TickListener to recieve tick() calls from this clock
       *  every simulated minute.
       *
       *     @pre none
       *    @post The registered listener is notified every minute
       *   @param newListener  The TickListener that wishes to be notified
       *                       every minute
       */

   public void setListener( TickListener newListener ) {
      listener = newListener;
   }

      /**
       *  Respond to notifications from the simulated time object. These
       *  come in every second, so notify tick listeners every time the
       *  seconds return to 0.
       *
       *     @pre none
       *    @post The listener (if any) is notified when a minute has
       *          passed since the last notification.
       *   @param o    The observable (the time object) that sent this 
       *               notification--not used.
       *   @param arg  Extra data from the observable--not used.
       */

   public void update( Observable o, Object arg ) {

      if ( listener == null ) return;
      if ( 0 == time.getSecond() ) listener.tick();
   }

} // SimClockDevice
