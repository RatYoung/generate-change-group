
package device;

/**
 *  The KeypadListener interface specifies the operations that a virtual 
 *  keypad will expect to be implemented by an object receiving keypad input.
 *  This is the command class in a Command pattern, while a KeypadDevice is
 *  the invoker.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface KeypadListener {

      /**
       *  Notify a keypad device user that a keypad key has been pressed.
       *  
       *    @pre  a keypad key has been pressed
       *   @post  none
       *  @param  key  The key pressed on the keypad
       */

   void keyPress( KeyPress key );

} // KeypadListener
