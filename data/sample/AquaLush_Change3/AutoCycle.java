
package irrigation;

/**
 *  A AutoCycle is an episode of irrigation under the control of AuqaLush.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.util.ArrayList;
import java.util.Set;

import device.DeviceFailureException;

class AutoCycle extends IrrigationCycle {

      /* attributes
      /**************/

   private ArrayList<Zone> zoneList;   // zones to be irrigated
   private int             allocation; // water that can be used in this cycle
   private Zone            crntZone;   // zone now being irrigated

      /* constructors
      /***************/

      /**
       *  Initialize the AutoCycle
       */

   AutoCycle( Set<Zone> theZones, int theAllocation ) {

      super( theZones );
      allocation = theAllocation;
      zoneList   = new ArrayList<Zone>();
      crntZone   = null;
   }

      /* methods
      /**********/

      /**
       *  Start this irrigation cycle
       *
       *     @pre none
       *    @post the irrigation cycle is begun
       *  @throws DeviceFailureException if one or more hardware devices fail
       */

   void start() throws DeviceFailureException {

         // arrange to collect all the exceptions that might occur
      DeviceFailureException allExceptions = null;

         // make the active zone list
      for ( Zone z : zones ) 
         try {
            if ( z.getMeasuredMoistureLevel() < z.getCriticalMoistureLevel() )
               zoneList.add( z );
         }
         catch ( DeviceFailureException e ) {
            if ( allExceptions == null ) allExceptions = e;
            else allExceptions.addException( e );
         }

         // start irrigation with the first active zone, if any
      if ( 0 < zoneList.size() ) {

            // make zone water allocations
         assignZoneAllocations();

            // get the first active zone and open all its valves
         crntZone = zoneList.remove(0);
         try {
            crntZone.openAllValves();
         }
         catch( DeviceFailureException e ) {
            if ( allExceptions == null ) allExceptions = e;
            else allExceptions.addException( e );
         }
      }

         // throw any device failure exceptions
      if ( allExceptions != null ) throw allExceptions;

   } // start

      /**
       *  Recieve notification that one minute has passed.
       *
       *     @pre none
       *    @post (1) the current zone is notified that time has passed by 
       *              calling its tick routine
       *          (2) the water used counter is updated
       *          (3) if the current zone is done, turn off all its valves
       *              and if there are more active zones, open the valves in
       *              the next one
       *  @throws DeviceFailureException if a hardware device fails
       */

   void tick() throws DeviceFailureException {

         // if there is no current zone, we are done
      if ( crntZone == null ) return;

         // notify the current zone so it can update itself
      crntZone.tick();

         // figure out the water used so far
      waterUsed = 0;
      for ( Zone z : zones ) waterUsed += z.getWaterUsed();

        // check to see whether the current zone is done
      DeviceFailureException allExceptions = null;
      boolean isDone = true;
      try {
         isDone = crntZone.isIrrigated();
      }
      catch ( DeviceFailureException e ) {
         allExceptions = e;
         isDone = true;  // the sensor failed, so this zone is done
      }

         // if this zone is done, close its valves, and if there are
         // more zones, compute the new zone allocations, and start
         // irrigating the next zone
      if ( isDone ) {

            // turn off all the valves
         try {
            crntZone.closeAllValves();
         }
         catch( DeviceFailureException e ) {
            if ( allExceptions == null ) allExceptions = e;
            else allExceptions.addException( e );
         }

            // get the next active zone, if any, and open its valves
         if ( 0 < zoneList.size() ) {
            assignZoneAllocations();
            crntZone = zoneList.remove(0);
            try {
               crntZone.openAllValves();
            }
            catch( DeviceFailureException e ) {
               if ( allExceptions == null ) allExceptions = e;
               else allExceptions.addException( e );
            }
         }
         else crntZone = null;
      }

         // propagate any caught exceptions
      if ( allExceptions != null ) throw allExceptions;

   } // tick

      /**
       *  Reset the water allocation for this cycle.
       *
       *     @pre 0 <= theAllocation
       *    @post the allocation is reset, which may involve interrupting,
       *          and either stopping or restarting the cycle, or an 
       *          exception is thrown
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws DeviceFailureException if a hardware device fails
       */

   void setAllocation( int theAllocation ) throws DeviceFailureException {

         // check the precondition
      if ( theAllocation < 0 )
         throw new IllegalArgumentException( "Negative allocation." );

         // if there is no current zone, the cycle must be done
      if ( crntZone == null ) return;

         // interrupt this cycle
      DeviceFailureException allExceptions = null;
      try {
         crntZone.closeAllValves();
      }
      catch( DeviceFailureException e ) {
         if ( allExceptions == null ) allExceptions = e;
         else allExceptions.addException( e );
      }

         // save the current zone for restarting irrigation
      zoneList.add( crntZone );
      crntZone = null;

         // change the allocation--stop if we are already done
      allocation = theAllocation;
      if ( allocation <= waterUsed ) {
         zoneList.clear();
         return;
      }

         // restart the cycle by recomputing water allocations
      assignZoneAllocations();

         // get the first active zone and open all its valves
      crntZone = zoneList.remove(0);
      try {
         crntZone.openAllValves();
      }
      catch( DeviceFailureException e ) {
         if ( allExceptions == null ) allExceptions = e;
         else allExceptions.addException( e );
      }

         // throw any device failure exceptions
      if ( allExceptions != null ) throw allExceptions;

   } // setAllocation

      /**
       *  Stop this irrigation cycle
       *
       *     @pre none
       *    @post all valves are closed
       *  @throws DeviceFailureException if a hardware device fails
       */

   void end() throws DeviceFailureException {

         // if there is no current zone, we are done
      if ( crntZone == null ) return;

         // empty the active zone list
      zoneList.clear();

         // close all the valves
      try {
         crntZone.closeAllValves();
      }
      catch( DeviceFailureException e ) {
         throw e;
      }
      finally {
         crntZone = null;
      }

   } // end

      /**
       *  The cycle is done if the water allocation is met or exceeded, or
       *  if all zones are done.
       *
       *     @pre none
       *    @post @return is true iff allocation <= waterUsed or there are
       *          are done.
       */

   boolean isDone() {
       return    (allocation <= waterUsed)
              || ((zoneList.size() == 0) && (crntZone == null));
   }

      /* private methods
      /******************/

      /**
       *  Allocate water to each active zone.
       *
       *     @pre none
       *    @post Water is allocated to each active zone by finding the
       *          fraction of the remaining water for each valve in the
       *          active zones (the valve allocation), and multiplying
       *          it by the number of active valves in a zone.
       */

   private void assignZoneAllocations() {

         // figure out the water left
      int waterLeft = Math.max( 0, allocation-waterUsed );

         // figure out the valve allocation
      int numWorkingValves = 0;
      for ( Zone z : zoneList ) numWorkingValves += z.getNumWorkingValves();
      double valveAllocation = (double)waterLeft/numWorkingValves;

         // set the allocation for each active zone
      for ( Zone z : zoneList )
         z.setAllocation( (int)(z.getNumWorkingValves() * valveAllocation) );

   } // assignZoneAllocations

} // AutoCycle
