
package device;

/**
 *  The KeypadDevice interface specifies the operations that an AquaLush
 *  virtual keypad must have. The virtual keypad has ten numeric keys, a
 *  delete key, and an escape key.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface KeypadDevice {

      /**
       *  Register the keypad device listener with the keypad device.
       *  
       *    @pre  none
       *   @post  if listener is null, no notifications take place;
       *          otherwise, listener is notified of every keypress
       *  @param  listener  The keypad listener that gets key-presses.
       */

   void setListener( KeypadListener listener );

} // KeypadDevice
