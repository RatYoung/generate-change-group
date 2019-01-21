
package simulation;

/**
 *   The Pulse class provide a reliable pulse at arbitrary intervals
 *   to its observers. The default interval is 1000 milliseconds.
 *
 *   To make the pulse as accurate as possible, a Pulse object grabs the
 *   current time when it starts running, then notifies its observers
 *   thereafter every interval milliseconds that pass, even if the
 *   notification does not occur every interval milliseconds exactly.
 *   Thus individual observer notifications that a time period has passed
 *   won't be very accurate, but notifications over some period of time
 *   will be--in other words, the pulse is accurate over long stretches.
 *   Given the vissitudes of Java thread management, this is about the
 *   best that can be expected.
 *
 *    @author C. Fox
 *   @version 01/02 modified 03/02 to add observer pattern
 */

import java.util.Date;
import java.util.Observable;

class Pulse extends Observable implements Runnable {

      /* attributes
       *************/

   private Thread  pulseThread;   // what this object runs in
   private int     interval;      // milliseconds between pulses
   private boolean keepRunning;   // to control starting and stopping

      /* constructors
       ***************/

      /**
       *  Create a pulse object ready but not yet running with the
       *  specified interval.
       *
       *  @param theInterval How long between pulses.
       */

   public Pulse( int theInterval ) {

      if ( theInterval < 0 ) theInterval = 1000;
      interval    = theInterval;
      keepRunning = true;
      pulseThread = null;
   }

      /**
       *  Create a pulse object ready but not yet running with interval
       *  1000 milliseconds.
       */

   public Pulse() { this(1000); }

      /*  public methods
      /*******************/

      /**
       *  Create a thread if necessary and start it running. The pulse
       *  thread is NOT a daemon thread.
       *
       *    @pre none
       *   @post if the pulse is not already running, it is begun; otherwise
       *         nothing is changed
       */

   public void start() {

      if ( null == pulseThread ) {
         pulseThread = new Thread( this );
         keepRunning = true;
         pulseThread.start();
      }
   }

      /**
       *  Stop pulsing.
       *
       *    @pre none
       *   @post a running pulse is stopped; nothing happens to a stopped
       *         pulse
       */

   public void stop() {

      keepRunning = false;
      pulseThread = null;
   }

      /**
       *  Set the interval to a new value if it is positive. If it is
       *  non-positive, change nothing.
       *  
       *    @pre none
       *   @post if milliseconds is positive, change the interval; otherwise,
       *         do nothing
       *  @param milliseconds  The new interval--must be positive.
       */

   public void setInterval( int milliseconds ) {

      if ( milliseconds <= 0 ) return;
      interval = milliseconds;
   }

      /**
       *  Wake up every interval milliseconds and notify the client.
       *  
       *    @pre none
       *   @post notify observers unless the pulse has been stopped
       */

   public void run() {

      Date now;             // this moment
      long wakeUpTime = 0;  // when to next awake
      long sleepTime = 0;   // how long to sleep

      now = new Date();
      wakeUpTime = now.getTime();
      while ( keepRunning ) {
            // sleep until interval has passed
         wakeUpTime += interval;
         now = new Date();
         sleepTime = wakeUpTime-now.getTime();
         try {
            if ( 5 < sleepTime ) pulseThread.sleep( sleepTime );
         }
         catch (InterruptedException e) {}

            // call the client tick() operation
         if ( keepRunning ) { setChanged(); notifyObservers(); }
      }

   } // run

} // Pulse
