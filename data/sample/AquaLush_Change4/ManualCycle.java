
package irrigation;

/**
 *  A ManualCycle is an episode of irrigation done under the control
 *  of the operator. This class is mainly used to monitor the process.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import java.util.Set;

import device.DeviceFailureException;

class ManualCycle extends IrrigationCycle {

      /* attributes
      /*************/

      /* constructors
      /***************/

      /**
       *  Initialize the ManualCycle
       */

   ManualCycle( Set<Zone> theZones ) {

      super( theZones );
   }

      /* methods
      /**********/

      /**
       *  Receive notification that an irrigation cycle must start.
       *  In a manual cycle, there is nothing to do.
       *
       *     @pre none
       *    @post the irrigation cycle is begun
       */

   void start() throws DeviceFailureException {
   }

      /**
       *  Recieve notification that one minute has passed.
       *
       *     @pre none
       *    @post all zones are notified that time has passed by calling their
       *          tick routines; the water used counter is updated.
       */

   void tick() {

         // notify all zones and update the water used
      waterUsed = 0;
      for ( Zone z : zones ) {
         z.tick();
         waterUsed += z.getWaterUsed();
      }
   }

      /**
       *  Close all valves
       *
       *     @pre none
       *    @post all valves are closed
       */

   void end() throws DeviceFailureException {

      DeviceFailureException allExceptions = null;

         // try to close all valves in all zones
      for ( Zone z : zones )
         try {
            z.closeAllValves();
         }
         catch ( DeviceFailureException e ) {
            if ( allExceptions == null ) allExceptions = e;
            else allExceptions.addException( e );
         }

         // if there were exceptions, propagate them
      if ( allExceptions != null ) throw allExceptions;

   } // end

      /**
       *  The user decides whether the cycle is done, so always return false.
       *
       *     @pre none
       *    @post @return is false
       */

   boolean isDone() { return false; }

} // ManualCycle
