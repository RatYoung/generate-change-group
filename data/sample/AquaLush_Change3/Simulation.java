
package simulation;

/**
 *  A Simulation is a GUI simulation of an AquaLush control panel and
 *  a simulated irrigation site, along with its controls. It also includes
 *  other simulated entities in the environment, particularly a simulated
 *  persistent store.
 *
 *   @author C. Fox
 *  @version 02/05
 */

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JPanel;

public class Simulation extends JPanel {

      /* static attributes
      /********************/

   private static final int FRAME_SIZE = 4;   // control panel border size

      /* attributes
      /*************/

   private final SimControlPanel controlPanel;   // AquaLush control panel
   private final SimEnvironment  environment;    // irrigation site + controls

      /* constructor
      /**************/

      /**
       *  Create the AquaLush simulation, which includes a simulated
       *  AquaLush control panel, a simulated world, and a simulated
       *  data store.
       *
       *     @pre none
       *    @post The simulation is created and initialized.
       *   @param stateFile  A file that may be used as the persistent store.
       *                     If null, then the program only simulated a
       *                     persisten store.
       */

   public Simulation( File stateFile ) {

      controlPanel = new SimControlPanel();
      environment  = new SimEnvironment( stateFile );

      setLayout( new BorderLayout() );
      add( controlPanel, BorderLayout.EAST );
      add( environment,  BorderLayout.WEST );
   }

      /* methods
      /**********/

      /**
       *  Return the simulated display hardware for use in device interfaces.
       *
       *    @pre none
       *   @post @result is the simulated display hardware.
       */

   public SimDisplay getDisplay() { return controlPanel.getDisplay(); }

      /**
       *  Return a simulated sensor given its identifier.
       *
       *     @pre name identifies an existing SimSensor
       *    @post the named SimSensor is returned
       *   @param name  The identifier of the desired SimSensor
       */

   public SimSensor getSensor( String name ) {
      return environment.getSensor( name );
   }

      /**
       *  Return a simulated valve given its identifier.
       *
       *     @pre name identifies an existing SimValve
       *    @post the named SimValve is returned
       *   @param name  The identifier of the desired SimValve
       */

   public SimValve getValve( String name ) {
      return environment.getValve( name );
   }

      /**
       *  Add a listener to the simulated keypad hardware so it can notify
       *  a virtual device of keypresses.
       *
       *    @pre none
       *   @post listener is passed to the simulated keypad hardware.
       *  @param listener  The keypad listener
       */

   public void setKeypadListener( ActionListener listener ) {
      controlPanel.setKeypadListener( listener );
   }


      /**
       *  Add a listener to the simulated screen button hardware so it 
       *  can notify a virtual device of keypresses.
       *
       *    @pre none
       *   @post listener is passed to the simulated screen button hardware.
       *  @param listener  The screen button listener
       */

   public void setScreenButtonListener( ActionListener listener ) {
      controlPanel.setScreenButtonListener( listener );
   }

      /**
       *  Tell the SimEnvironment about the Irrigator so it can obtain the
       *  irrigation times for the Jump button.
       *
       *    @pre none
       *   @post the simulated environment has been told about the irrigator
       *  @param irrigator  The irrigation control object
       */

   public void setIrrigator( irrigation.Irrigator irrigator ) {
      environment.setIrrigator( irrigator );
   }

} // Simulation
