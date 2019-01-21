
package device;

/**
 *  The ScreenButtonListener interface specifies the operations that a virtual 
 *  screen button device will expect to be implemented somewhere in the system.
 *  This is the command class in a Command pattern, while a ScreenButtonDevice
 *  is the invoker.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface ScreenButtonListener {

      /**
       *  Notify a screen-buttton listener that a screen button has been pressed.
       *  
       *    @pre  screen button whichButton has been pressed
       *          whichButton is in range 0..ScreenButton.NUM_SCREEN_BUTTONS-1
       *   @post  none
       *  @param  whichButton  Which button has been pressed in the range
       *                       0..ScreenButton.NUM_SCREEN_BUTTONS-1
       */

   void screenButtonPress( int whichButton );

} // ScreenButtonListener
