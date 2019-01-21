
package startup;

/**
 *  The AquaLushApplet is the top-level object in the AquaLush simulation.
 *  Program configuration information is read from a program paramter set
 *  in the applet tag called "configuration." It has the format of an
 *  AquaLush configuration file.
 *
 *   @author C. Fox
 *  @version 01/06
 */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import device.DeviceFactory;
import device.sim.SimDeviceFactory;
import simulation.SimTime;
import simulation.Simulation;

public class AquaLushApplet extends Applet {

      /* static attributes
      /********************/

  private static final int SPACE = 6;  // around internalpanels

      /* attributes
      /*************/

   private Simulation simulation;    // simulation (includes GUI)
   private Configurer configurer;    // sets up major objects

      /* public methods
      /*****************/

      /**
       *  Initialize the applet by creating the simulation (which includes
       *  the graphical user interface) and then configuring the program 
       *  as a simulation.
       *
       *     @pre none
       *    @post applet is ready to run
       */

   public void init() {

         // create the applet UI components--these will also be the source
         // of most of the simulated hardware devices used by the program
      simulation   = new Simulation( null );
      Border bevel = BorderFactory.createBevelBorder( BevelBorder.RAISED );
      Border space = BorderFactory.createEmptyBorder( SPACE,SPACE,SPACE,SPACE );
      simulation.setBorder ( BorderFactory.createCompoundBorder(bevel,space) );
      add( simulation );

         // create a device factory for use by the program configurer;
         // note that the device factory would be different for a fielded 
         // program rather than a simulation, but the configurer would 
         // be just the same--this is where the Abstract Factory pattern
         // comes into play to make it easy to change the hardware
      DeviceFactory deviceFactory = new SimDeviceFactory( simulation );

         // make a configurer instance to create and connect components
         // and to restore the system state from persistent store
      configurer          = new Configurer( deviceFactory );
      Reader configSource = new StringReader( getParameter("configuration") );
      configurer.configure( configSource );

         // now we must violate layered architecture constraints by providing
         // access to the irrigation controller from the simulation to allow
         // the time control Jump button to work
      simulation.setIrrigator( configurer.getIrrigator() );

   } // init

      /**
       *  Tell the simulated time object (which drives everything) to resume
       *  execution
       *
       *     @pre none
       *    @post SimTime instance is running
       */

   public void start() {
      SimTime.instance().start();
   }

      /**
       *  Tell the simulated time object (which drives everything) to pause
       *  execution
       *
       *     @pre none
       *    @post SimTime instance is paused
       */

   public void stop() {
      SimTime.instance().stop();
   }

} // AquaLushApplet
