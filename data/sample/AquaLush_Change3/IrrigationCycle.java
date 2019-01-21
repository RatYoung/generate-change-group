
package irrigation;

/**
 *  The IrrigationCycle is an abstract class whose instances are classes
 *  that oversee manual or automatic irrigation.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import device.DeviceFailureException;
import util.Day;

import java.util.Set;

abstract class IrrigationCycle {

      /* attributes
      /*************/

   protected final Set<Zone> zones;      // all the zones irrigated
   protected int             waterUsed;  // in this cycle

      /* constructors
      /***************/

      /**
       *  Initialize the IrrigationCycle
       */

   IrrigationCycle( Set<Zone> theZones ) {

         // initialize basic attributes
      zones     = theZones;
      waterUsed = 0;

      for ( Zone z : zones ) z.reset();

   } // IrrigationCycle


      /* methods
      /**********/

      /**
       *  See how much water has been used so far in this irrigation cycle.
       *
       *     @pre none
       *    @post @return is the gallons of water used
       */

   int getWaterUsed() { return waterUsed; }

      /**
       *  Receive notification that an irrigation cycle must start.
       *
       *     @pre none
       *    @post the irrigation cycle is begun
       */

   abstract void start() throws DeviceFailureException;

      /**
      /**
       *  Recieve notification that one minute has passed.
       *
       *     @pre none
       *    @post time-sensitive data is updated
       */

   abstract void tick() throws DeviceFailureException;

      /**
       *  Receive notification that an irrigation cycle must end.
       *
       *     @pre none
       *    @post clean up the irrigation cycle
       */

   abstract void end() throws DeviceFailureException;

      /**
       *  Indicate whether the irrigation cycle is done (used mainly for
       *  automatic irrigation cycles).
       *
       *     @pre none
       *    @post @return is true iff this irrigation cycle is complete
       */

   abstract boolean isDone();

} // IrrigationCycle
