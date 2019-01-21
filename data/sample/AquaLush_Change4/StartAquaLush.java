package startup;

import irrigation.Irrigator;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import simulation.SimTime;
import simulation.Simulation;
import startup.Configurer;
import device.DeviceFactory;
import device.sim.SimDeviceFactory;


public class StartAquaLush extends JFrame {

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
     
      
	      String config=null;
		try {
			config = readFile("config.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  //	System.out.println(config);
	  	
      Reader configSource = new StringReader( config);
      //Reader configSource = new StringReader( getParameter("configuration") );
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

   public Irrigator getIrrigator(){
	   return configurer.getIrrigator();
   }
   
   private String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String line  = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    return stringBuilder.toString();
	 }
   
   public static void main(String[] args) {
	 StartAquaLush panel = new StartAquaLush();
	 panel.init();
	 panel.start();
	 panel.setBounds(0, 0, 1000, 700);
	 panel.repaint();
	 panel.setVisible(true);
	 
   }
} // AquaLushApplet
