
package device;

/**
 *  The ScreenButtonDevice interface specifies the operations of a virtual 
 *  AquaLush control panel device consisting of several buttons adjacent to 
 *  the display screen (called screen buttons). This single device implements 
 *  all the screen buttons.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface ScreenButtonDevice {

      /**
       *  Specify the number of button in the virtual screen button device
       */

   static final int NUM_SCREEN_BUTTONS = 8;

      /**
       *  Register the screen button listener with the screen button device.
       *  
       *    @pre  none
       *   @post  if listener is null, no notifications take place;
       *          otherwise, listener is notified of every screen-button press
       *  @param  listener The listener that gets screen-button presses
       */

   void setListener( ScreenButtonListener listener );

} // ScreenButtonDevice
