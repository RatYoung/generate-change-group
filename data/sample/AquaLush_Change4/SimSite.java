
package simulation;

/**
 *  A SimSite simulates an irrigation site with valves, moisture sensors,
 *  and moisture evaporation over time. It keeps track of the zones and
 *  oversees simulation user control of valve and sensor failures.
 *
 *  The rate of simulated evaporation may be set from 0 to 100 percent
 *  of the existing moisture per day.
 *
 *  The SimSite participates in the Model-View-Controller (MVC) and
 *  Observer paterns. Specifically, the SimSiteView is a view of the SimSite 
 *  model and it is an Observer of the SimSite. Futhermore, the SimSite is
 *  also an observer of the SimClock, which it uses as a cue to tell the
 *  zones to adjust their soil moisture levels every minute. The SimSite is
 *  also an observer of the SimSensors and SimValves. When they change state,
 *  the SimSite notifies the SimSiteView to update the display.
 *
 *   @author C. Fox
 *  @version 01/05
 *
 *  @invariant 0 <= rate <= 100
 */

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

class SimSite extends Observable implements Observer {

      /* static fields
      /****************/

   private static final boolean SET_TO_FAIL  = true; // failure simulation flag
   private static final int     DEFAULT_RATE = 10;   // nominal evaporation rate

      /* attributes
      /*************/

   private final Collection<SimZone> zones; // simulated irrigation zones
   private int                 rate;  // simulated evaporation rate as the ...
                                      // ... percentage of moisture lost per day

      /* constructors
      /***************/

      /**
       *  Create and populate the simulated irrigation zones and register
       *  as an observer.
       *
       *     @pre None
       *    @post All attributes have been set; the simulated site has been 
       *          populated with simulated zones, valves, and sensors
       */

   SimSite() {

         // initialize attributes
      rate  = DEFAULT_RATE;
      zones = new ArrayList<SimZone>(3);

         // create all the zones, sensors, and valves in the simulation
      SimZone zone;       // handle needed for adding to collection
      SimSensor sensor;   // handle needed for adding observer
      SimValve valve;     // handle needed for adding observer

      zone = new SimZone( "Z1", sensor = new SimSensor("S1") );
      zones.add( zone );                              sensor.addObserver(this);
      zone.addValve( valve = new SimValve("V01",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V02",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V03",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V04",12) ); valve.addObserver(this);

      zone = new SimZone( "Z2", sensor = new SimSensor("S2") );
      zones.add( zone );                              sensor.addObserver(this);
      zone.addValve( valve = new SimValve("V05",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V06",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V07",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V08",12) ); valve.addObserver(this);

      zone = new SimZone( "Z3", sensor = new SimSensor("S3") );
      zones.add( zone );                              sensor.addObserver(this);
      zone.addValve( valve = new SimValve("V09", 12) );valve.addObserver(this);
      zone.addValve( valve = new SimValve("V10",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V11",12) ); valve.addObserver(this);
      zone.addValve( valve = new SimValve("V12",12) ); valve.addObserver(this);

         // register as an observer of the SimTime
      SimTime.instance().addObserver( this );

   } // SimSite

      /* methods
      /**********/

      /**
       *  Set the site's evaporation rate. This is the percentage of moisture
       *  lost per day through evaporation.
       *
       *     @pre newRate must be in range 0..100
       *    @post rate = newRate
       *   @param newRate The new evaporation rate.
       *  @throws IllegalArgumentException if the precondition is violated
       */

   void setEvaporationRate( int newRate ) {
      if ( (newRate < 0) || (100 < newRate) ) return;
      rate = newRate;
   }

      /**
       *  Set the site's evaporation rate to its default value.
       *
       *     @pre none
       *    @post rate = DEFAULT_RATE
       */

   void setEvaporationRateToDefault() { rate = DEFAULT_RATE; }

      /**
       *  Get the site's evaporation rate as the percentage of moisture
       *  lost per day through evaporation.
       *
       *     @pre none
       *    @post 0 <= @return <=100
       */

   int getEvaporationRate() { return rate; }

      /**
       *  Provide an iterator over the zones
       *
       *    @pre none
       *   @post @return is a Collection iterator
       */

   Iterator zoneIterator() { return zones.iterator(); }

      /**
       *  Set a sim device to simulate failure or to work again.
       *
       *     @pre deviceName identifies an existing device
       *    @post the named device is set to simulate failure or not
       *   @param deviceName  The identifier of the device set to fail or 
       *                      be repaired.
       */

   void setFailed( String deviceName ) {
      setFailureStatus( deviceName, SET_TO_FAIL );
   }

   void setRepaired( String deviceName ) {
      setFailureStatus( deviceName, !SET_TO_FAIL );
   }

      /**
       *  Respond to notifications from a subject of observation, either
       *  the time (every second), or a valve or sensor (which has changed).
       *
       *     @pre none
       *    @post if a minute has passed, the zones have adjusted soil
       *          moisture; if a valve or sensor has changed state, the
       *          site has been redisplayed
       *   @param subject The subject sending this notification
       *          arg     Should be null--ignored
       */

   public void update( Observable subject, Object arg ) {

      SimTime time = SimTime.instance();  // notifies every second

      if ( subject == time ) {

            // if a minute has passed, have the zones adjust soil moisture
         if ( time.getSecond() != 0 ) return;
         Iterator zoneIter = zoneIterator();
         while ( zoneIter.hasNext() ) {
            SimZone zone = (SimZone)zoneIter.next();
            zone.adjustMoisture( rate );
         }
      }
      else {

            // a valve or sensor has changed, so redisplay the site
         setChanged();
         notifyObservers();
      }
   }

      /* private methods
      /******************/

      /**
       *  Set a sim device to simulate failure or to work again.
       *
       *     @pre deviceName identifies an existing device
       *    @post the named device is set to simulate failure or not
       *   @param deviceName  The identifier of the device set to fail or 
       *                      be repaired.
       *  @throws IllegalArgumentException if the precondition is violated
       */

   private void setFailureStatus( String deviceName, boolean setToFail ) {
      boolean isDone = false;   // true if the culprit is found

         // iterate through the zones looking for the device to set
      Iterator zoneIter = zoneIterator();
      while ( !isDone && zoneIter.hasNext() ) {
         SimZone zone = (SimZone)zoneIter.next();
         SimSensor sensor = zone.getSensor();
         if ( deviceName.equals(sensor.getName()) ) {
            if ( setToFail ) sensor.setIsFailed();
            else             sensor.resetIsFailed();
            isDone = true;
         }
         else {
               // iterate through the valves looking for the culprit
            Iterator valveIter = zone.valveIterator();
            while ( valveIter.hasNext() ) {
               SimValve valve = (SimValve)valveIter.next();
               if ( deviceName.equals(valve.getName()) ) {
                  if ( setToFail ) valve.setIsFailed();
                  else             valve.resetIsFailed();
                  isDone = true;
               }
            }
         }
      }
         // we should never get here without isDone = true!
      if ( !isDone ) throw new IllegalArgumentException();
   }

} // SimSite
