
package device.sim;

/**
 *  A SimScreenButtonDevice provides a virtual interface to a simulated
 *  bank of screen buttons. It translates button presses into notifications
 *  of screen button presses.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import device.ScreenButtonDevice;
import device.ScreenButtonListener;

class SimScreenButtonDevice implements ScreenButtonDevice, ActionListener {

      /* attributes
      /*************/

   private ScreenButtonListener listener;    // consumes screen button presses

      /**
       *  Translate button presses into screen button presses and then
       *  dispatch them to the registered ScreenButtonListener.
       *
       *     @pre  event is not null
       *    @post  the listener (if any) is passed a button press
       *   @param  event  The button press event containing the button text.
       *  @throws  IllegalArgumentException if precondition violated
       */

   public void actionPerformed( ActionEvent event ) {

         // check parameters and state
      if ( event == null ) throw new IllegalArgumentException();
      if ( listener == null ) return;

         // translate the button press and pass it on
      String btnText = event.getActionCommand();  // for id'ing the button
      int whichButton = 0;                        // translation

      if      ( btnText.equals("ScrnBtn0") ) whichButton = 0;
      else if ( btnText.equals("ScrnBtn1") ) whichButton = 1;
      else if ( btnText.equals("ScrnBtn2") ) whichButton = 2;
      else if ( btnText.equals("ScrnBtn3") ) whichButton = 3;
      else if ( btnText.equals("ScrnBtn4") ) whichButton = 4;
      else if ( btnText.equals("ScrnBtn5") ) whichButton = 5;
      else if ( btnText.equals("ScrnBtn6") ) whichButton = 6;
      else if ( btnText.equals("ScrnBtn7") ) whichButton = 7;
      else throw new IllegalArgumentException("Invalid screen button");

         // send the listener the correct key indication
      listener.screenButtonPress( whichButton );

   } // actionPerformed

      /**
       *  Assigns a new ScreenButtonListener for the SimScreenButtonDevice.
       *
       *     @pre  none
       *    @post  the new listener is assigned
       *   @param  newListener  The screen button listener that consumes
       *                        button presses.
       */

   public void setListener( ScreenButtonListener newListener ) {
      listener = newListener;
   }

} // SimScreenButtonDevice
