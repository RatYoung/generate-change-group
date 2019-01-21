
package simulation;

/**
 *  A SimControlPanel is a GUI simulation of the physical AquaLush system
 *  control panel consisting of a keypad, a monochrome character display,
 *  and several screen buttons.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

class SimControlPanel extends JPanel {

      /* attributes
      /*************/

   private static final int GAP   = 4;    // between display and keypad
   private static final int SPACE = 12;   // control panel border size

   private final SimDisplay display;      // monitor & screen buttons
   private final SimKeypad  keypad;       // 12-key keypad

      /* constructor
      /**************/

      /**
       *  Create a simulated AquaLush control panel component.
       *  Note that this component can be used in a larger GUI.
       *
       *    @pre none
       *   @post the control panel GUI exists, but is not connected to
       *         any listeners.
       */

   SimControlPanel() {

         // create the simulated display and keypad
      display = new SimDisplay();
      keypad  = new SimKeypad();

         // add the components to the containing JPanel
      setBackground( keypad.getBackground() );
      setLayout( new BorderLayout(0,SPACE) );
      add( display, BorderLayout.NORTH );
      add( keypad,  BorderLayout.SOUTH );

         // put a border around the control panel
      Border title = BorderFactory.createEtchedBorder();
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title, " Control Panel "),
            BorderFactory.createEmptyBorder( SPACE,SPACE,SPACE,SPACE ) ) );
   }

      /* methods
      /**********/

      /**
       *  Return the simulated display hardware for use in device interfaces.
       *
       *    @pre none
       *   @post @result is the simulated display hardware.
       */

   SimDisplay getDisplay() { return display; }

      /**
       *  Add a listener to the simulated keypad hardware so it can notify
       *  a virtual device of keypresses.
       *
       *    @pre none
       *   @post listener is passed to the simulated keypad hardware.
       */

   void setKeypadListener( ActionListener l ) {
      keypad.setListener( l );
   }


      /**
       *  Add a listener to the simulated screen button hardware so it 
       *  can notify a virtual device of keypresses.
       *
       *    @pre none
       *   @post listener is passed to the simulated screen button hardware.
       */

   void setScreenButtonListener( ActionListener l ) {
      display.setListener( l );
   }

} // SimControlPanel
