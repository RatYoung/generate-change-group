
package device.sim;

/**
 *  A KeypadDevice privides a virtual device that registers keypresses
 *  on a simulated AquaLush control panel keypad. This class translates
 *  simulated keypad button presses into the KeyPress enumeration and 
 *  then passes them on to a listener.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import device.KeypadDevice;
import device.KeypadListener;
import device.KeyPress;
import simulation.SimTime;

class SimKeypadDevice implements KeypadDevice, ActionListener {

      /* attributes
      /*************/

   private KeypadListener listener;  // consumes key presses from this keypad

      /**
       *  Translate button presses into KeyPress enumeration values and then
       *  dispatch them to the registered KeypadListener.
       *
       *     @pre  event is not null
       *    @post  the listener (if any) is passed a KeyPress value
       *   @param  event  The key press event containing the button text.
       *  @throws  IllegalArgumentException if precondition violated
       *  @throws  AssertionException if the keypress is invalid (should
       *           not be possible)
       */

   public void actionPerformed( ActionEvent event ) {

         // check parameters and state
      if ( event == null ) throw new IllegalArgumentException();
      if ( listener == null ) return;

      String btnText = event.getActionCommand();  // for id'ing the button

         // send the key pad listener the correct key indication
      if ( btnText.equals("DEL") )
         listener.keyPress( KeyPress.DEL_KEY );
      else if ( btnText.equals("ESC") )
         listener.keyPress( KeyPress.ESC_KEY );
      else {
         int keyNumber;  // remaining key numbers correspond to their text

         try {
            keyNumber = Integer.parseInt(btnText); 
         }
         catch ( NumberFormatException e )
         {
            throw new AssertionError( "Invalid keyboard button" );
         }

         listener.keyPress( KeyPress.valueOf(keyNumber) );
      }

   } // actionPerformed

      /**
       *  Assigns a new KeypadListener for the SimKeypadDevice.
       *
       *     @pre  none
       *    @post  the new listener is assigned
       *   @param  newListener  The key pad listener that consumes the key
       *                        strokes entered on this SimKeypadDevice.
       */

   public void setListener( KeypadListener newListener ) {
      listener = newListener;
   }

} // SimKeypadDevice
