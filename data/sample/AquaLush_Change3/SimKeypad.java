
package simulation;

/**
 *  A SimKeypad simulates a hardware control panel keypad for AquaLush.
 *
 *  The action listener assigned to each of the buttons is the virtual
 *  keypad device. This is how the simulated keypad hardware sends keypresses
 *  to the virtual device, which interprets them.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

class SimKeypad extends JPanel {

      /* static fields
      /*****************/

   private static final int NUM_ROWS    = 4;   // in the keypad
   private static final int NUM_COLUMNS = 3;   // in the keypad
   private static final int KEY_GAP     = 3;   // to look nice

      /* attributes
      /**************/

   private final JButton[] key;       // one for each button on the keypad
   private final JPanel    keyPanel;  // holds the keypad buttons
   private ActionListener  listener;  // consumes key presses from this keypad

      /* constructors
      /***************/

      /**
       *  Creates a JPanel simulating a control panel keypad usable 
       *  in a larger component.
       *
       *   @pre none
       *  @post the keypad is created but has not listener
       *  @note Another panel is placed inside this one so that the grid
       *        of buttons shrinks to the size of the buttons rather than
       *        being stretched to fit the area where the keypad resides.
       */

   SimKeypad() {

         // the inner keyPanel is a grid of buttons
      keyPanel = new JPanel();
      keyPanel.setLayout(new GridLayout(NUM_ROWS,NUM_COLUMNS,KEY_GAP,KEY_GAP));

         // make it look nice
      Border padBorder = BorderFactory.createCompoundBorder(
         BorderFactory.createBevelBorder( BevelBorder.RAISED ),
         BorderFactory.createEmptyBorder( KEY_GAP,KEY_GAP,KEY_GAP,KEY_GAP ) );
      keyPanel.setBorder( padBorder );

         // make NUM_BUTTONS buttons and add them to the grid
      key = new JButton[NUM_ROWS*NUM_COLUMNS];

      for ( int i = 0; i < 9; i++ )
         key[i]  = new JButton( String.valueOf(i+1) );

      key[9]  = new JButton( "DEL" );
      key[10] = new JButton( "0" );
      key[11] = new JButton( "ESC" );

         // change the font and add the buttons to the panel
      Font font = new Font( "Courier", Font.BOLD, 14 );
      for ( int i = 0; i < NUM_ROWS*NUM_COLUMNS; i++ ) {
         key[i].setFont( font );
         keyPanel.add( key[i] );
      }

         // put the keyPanel in the outer JPanel (which has a flowlayout)
      add( keyPanel );
   }

      /**
       *  Assigns the action listener to all the buttons (this is the
       *  keypad device).
       *
       *   @pre none
       *  @post all JButtons are assigned listener as their action listener
       *  @param listener  The virtual keypad device that interprets
       *                   simulated hardware keypad presses.
       */

   void setListener( ActionListener listener ) {
      for ( int i = 0; i < NUM_ROWS*NUM_COLUMNS; i++ )
         key[i].addActionListener( listener );
   }

} // SimKeypad
