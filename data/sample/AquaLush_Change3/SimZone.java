
package simulation;

/**
 *  A SimZone simulates an irrigation zone with one SimSensor and several
 *  SimValves. It also keeps track of the simulated soil moisture in the zone.
 *
 *   @author C. Fox
 *  @version 01/05
 *
 *  @invariant 0 <= soilMoisture <= 100
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class SimZone {

      /* attributes
      /*************/

   private final static double FUDGE            = 497;  // gallons for 1% of 
                                                        // soil saturation
   private final static double DEFAULT_MOISTURE = 40.0; // starting zone wetness

   private final String    name;               // the unique zone identifier
   private final SimSensor sensor;             // SimSensor in this zone
   private final Collection<SimValve> valves;  // SimValves in this zone

   private double soilMoisture;                // simulated wetness in this zone

      /* constructors
      /***************/

      /**
       *  Creates a simulated irrigation zone.
       *
       *    @pre None
       *   @post All attributes are initialized
       *  @param zoneID  The new zone's identifier
       */

   SimZone( String zoneID, SimSensor theSensor ) {
      name         = zoneID;
      sensor       = theSensor;
      valves       = new ArrayList<SimValve>(4);
      soilMoisture = DEFAULT_MOISTURE;
      sensor.setLevel( (int)soilMoisture );
   }

      /* methods
      /**********/

      /**
       *  Adjust the simulated soil moisture level
       *
       *     @pre 0 <= pctPerDay <= 100
       *    @post The soil moisture level is reduced by an amount based on
       *          percentage of moisture lost per day for one minute; it is
       *          then increased by the amount of water that has been put
       *          on it by sprinkler valves (by the fudge factor); the zone
       *          sensor is apprised of the new level
       *   @param rate  The percentage of evaporation per day
       *  @throws IllegalArgumentException if @pre is violated
       */

   void adjustMoisture( int pctPerDay ) {

      if ( (pctPerDay < 0) || (100 < pctPerDay) )
         throw new IllegalArgumentException();

         // reduce the level by evaporation
      soilMoisture = soilMoisture - (soilMoisture * (pctPerDay/100.0/24/60));

         // increase the level based on sprinklers
      int totalFlow = 0;
      for ( SimValve v : valves ) 
         if ( v.isOpen() ) totalFlow += v.getFlowRate();
      soilMoisture += totalFlow / FUDGE;

         // set the sensor to the new level
      sensor.setLevel( Math.max(0,Math.min(100,(int)soilMoisture)) );
   }

      /**
       *  Add a valve to the zone.
       *
       *     @pre valve is not null; valve is not already in the zone
       *    @post the new valve is added to the zone
       *   @param valve  The new valve
       *  @throws NullPointerException if valve in null
       */

   void addValve( SimValve valve ) {

      if ( valve == null )
         throw new NullPointerException();

      valves.add( valve );
   }

      /**
       *  Standard get operations
       *
       *     @pre none
       *    @post the requested value is returned
       */

   String    getName()       { return name; }
   SimSensor getSensor()     { return sensor; }
   Iterator  valveIterator() { return valves.iterator(); }

} // SimZone
