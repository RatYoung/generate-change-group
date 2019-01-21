
package simulation;

/**
 *  A SimEnvironment is a GUI simulation of an irrigation site, plus
 *  controls for the simulation itself.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

class SimEnvironment extends JPanel {

      /* static attributes
      /********************/

   private static final int GAP = 4;   // for the etched titled border

      /* attributes
      /*************/

      private final SimSite           simSite;      // model for simulated site
      private final SimSiteView       simSiteView;  // view of simulated site
      private final SimFailureControl failControl;  // for device failure/repair
      private final SimEvapControl    evapControl;  // control evaporation rate
      private final SimTimeControl    timeControl;  // control and set time

      /* constructor
      /**************/

      /**
       *  Create a simulated world for irrigation, including valves,
       *  sensors, a site, a persisten store, and control for all this.
       *
       *     @pre  none
       *    @post  The simulated environment is created and initialized.
       *   @param  stateFile A file used for persisten store, or null if
       *                     the persisten store is simulated.
       */

   SimEnvironment( File stateFile ) {

         // create the new simulated irrigation site
      simSite = new SimSite();

         // put an etched, labeled border around the JPanel
      Border title = BorderFactory.createEtchedBorder();
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title, " Environment "),
            BorderFactory.createEmptyBorder( GAP,GAP,GAP,GAP ) ) );

         // create the SimEnvironment display components
      simSiteView = new SimSiteView( simSite );
      failControl = new SimFailureControl( simSite );
      evapControl = new SimEvapControl( simSite );
      timeControl = new SimTimeControl();

         // put the controls in a box in the south of the Border
      Box controls = new Box( BoxLayout.Y_AXIS );
      add( controls, BorderLayout.SOUTH );
      controls.add( failControl );
      controls.add( evapControl );
      controls.add( timeControl );

         // make the JPanel have a BorderLayout
      setLayout( new BorderLayout() );

         // put the SimSite in the center and the controls in the south
      add( simSiteView, BorderLayout.CENTER );
      add( controls,    BorderLayout.SOUTH );
   }

      /* methods
      /**********/

      /**
       *  Fetch a SimSensor given its name.
       *
       *     @pre name identifies an existing SimSensor
       *    @post the named SimSensor is returned
       *   @param name The identifier of the desired SimSensor
       *  @throws IllegalArgumentException if the precondition is violated
       */

   SimSensor getSensor( String name ) {
         // iterate through the zones looking for the sensor
      Iterator zoneIter = simSite.zoneIterator();
      while ( zoneIter.hasNext() ) {
         SimZone zone = (SimZone)zoneIter.next();
         SimSensor sensor = zone.getSensor();
         if ( name.equals(sensor.getName()) ) return sensor;
      }

         // we should never get here!
      throw new IllegalArgumentException();
   }

      /**
       *  Fetch a SimValve given its name.
       *
       *     @pre name identifies an existing SimValve
       *    @post the named SimValve is returned
       *   @param name The identifier of the desired SimValve
       *  @throws IllegalArgumentException if the precondition is violated
       */

   SimValve getValve( String name ) {
         // iterate through the zones looking for the valve
      Iterator zoneIter = simSite.zoneIterator();
      while ( zoneIter.hasNext() ) {
         SimZone zone = (SimZone)zoneIter.next();

            // iterate through the valves looking for the culprit
         Iterator valveIter = zone.valveIterator();
         while ( valveIter.hasNext() ) {
            SimValve valve = (SimValve)valveIter.next();
            if ( name.equals(valve.getName()) ) return valve;
         }
      }

         // we should never get here!
      throw new IllegalArgumentException();
   }

      /**
       *  Tell the time control about the Irrigator so it can use it to
       *  implement the function of the Jump button.
       *
       *    @pre  none
       *   @post  the time control has been told about the irrigator
       *  @param  irrigator  The irrigation control object
       */

   void setIrrigator( irrigation.Irrigator irrigator ) {
      timeControl.setIrrigator( irrigator );
   }

} // SimEnvironment
