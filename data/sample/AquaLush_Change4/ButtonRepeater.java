
package simulation;

/**
 *  The ButtonRepeater is a special MouseAdapter that uses a pulsing 
 *  notification to implement action in response to a mouse button being 
 *  held down. When the mouse is pressed, a pulse object is started--it 
 *  notifies a command object at regular intervals. The command object 
 *  in effect realizes the mouse button's action listener operation. 
 *  When the user releases the mouse button, or leaves the container
 *  associated with the MouseAdapter, the pulsing object is stopped, and
 *  the button action stops.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import util.Command;

class ButtonRepeater extends MouseAdapter implements Observer {

   private final Pulse repeater;  // pulse object driving repitition
   private final Command cmd;     // what gets done repeatedly

   public ButtonRepeater( int interval, Command theCmd ) {
      repeater = new Pulse( interval );
      repeater.addObserver( this );
      cmd = theCmd;
   }

      /**
       *  Start repeating when the mouse button is pressed.
       */

   public void mousePressed( MouseEvent e )  { repeater.start(); }

      /**
       *  Stop repeating when the mouse button is released.
       */

   public void mouseReleased( MouseEvent e ) { repeater.stop(); }

      /**
       *  Stop repeating when the mouse leaves the button.
       */

   public void mouseExited( MouseEvent e )   { repeater.stop(); }

      /**
       *  Execute the command every time the pulse beats.
       */

   public void update( Observable pulse, Object arg ) {
      cmd.execute(this);
   }

} // ButtonRepeater
