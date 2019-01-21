
package simulation;

/**
 *  The SimFailureControl object allows users to cause simulated sensors and
 *  valves to fail and be fixed.
 *
 *  This object is a JPanel that can be used as a component.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Iterator;

class SimFailureControl extends JPanel implements ItemListener {

      /* static fields
      /****************/

   private static final int GAP = 4;  // space around the etched border

      /* attributes
      /*************/

   private final SimSite site;   // whose devices this widget controls

      /* constructors
      /***************/

      /**
       *  Creates a simulated device failure control panel with checkboxes
       *  for each device. Checking a box makes it fail and unchecking it
       *  repairs it again.
       *
       *    @pre none
       *   @post the failure control panel is created
       */

   SimFailureControl( SimSite theSite ) {

         // remember the site so we can control it
      site = theSite;

         // put an etched titled border around the control
      setLayout( new BorderLayout(GAP,0) );
      Border title = BorderFactory.createEtchedBorder();
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title, " Failed Devices "),
            BorderFactory.createEmptyBorder( GAP,GAP,GAP,GAP ) ) );

         // layout strategy: make a grid with one row for the devices in
         // a zone. The four zones comprise the four rows of the grid.
         // Place the grid in the center of a JPanal with a BorderLayout
         // with an explanatory label in the North.
      setLayout( new BorderLayout(0,3) );
      JPanel deviceGrid = new JPanel( new GridLayout(3,6,0,0) );
      add( new JLabel("Select devices to simulate failure."),
                       BorderLayout.NORTH );
      add( deviceGrid, BorderLayout.CENTER );

         // create grid row elements by iterating through the zones
      Iterator zoneIter = site.zoneIterator();
      while ( zoneIter.hasNext() ) {
         SimZone zone = (SimZone)zoneIter.next();

            // add the zone name and sensor name
         deviceGrid.add( new JLabel("Zone "+ zone.getName() +":") );
         JCheckBox checkBox = new JCheckBox( zone.getSensor().getName() );
         checkBox.addItemListener( this );
         deviceGrid.add( checkBox );

            // iterate through the valves to add their names
         Iterator valveIter = zone.valveIterator();
         while ( valveIter.hasNext() ) {
            SimValve valve = (SimValve)valveIter.next();
            checkBox = new JCheckBox( valve.getName() );
            checkBox.addItemListener( this );
            deviceGrid.add( checkBox );
         }
      }
   }

      /* methods
      /**********/

      /**
       *  Respond to a check or uncheck event in the control panel.
       *
       *    @pre none
       *   @post the selected sim device is failed or repaired
       *
       *  @param e  The event reported--used to get the checkbox (for the
       *            device) and its state (for failure or repair).
       */

   public void itemStateChanged( ItemEvent e ) {

      JCheckBox deviceCheck = (JCheckBox)e.getItemSelectable();
      String deviceName = deviceCheck.getText();

      if ( e.getStateChange() == ItemEvent.SELECTED )
         site.setFailed( deviceName );
      else
         site.setRepaired( deviceName );
   }

} // SimFailureControl
