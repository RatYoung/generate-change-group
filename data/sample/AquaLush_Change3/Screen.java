
package ui;

/**
 *  Screen class instances are states in the user interface state machine.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DisplayDevice;
import device.ScreenButtonDevice;
import device.KeyPress;
import java.util.ArrayList;
import java.util.Collection;

class Screen {

      /* attributes
      /*************/

   protected static final String BLANK_LINE 
                              = "                                        ";
                   //            1234567890123456789012345678901234567890

   protected final DisplayDevice display;         // where to display all output
   protected final Collection<Prompt> prompts;    // strings written to display
   private final Screen[]        transition;      // on screen button press
   private final EventAction[]   btnPressAction;  // on screen button press

   protected ScreenState state;           // tracks extra state data
   private Screen        escapeTarget;    // on ESC key press
   private EventAction   keyPressAction;  // on keypad key press
   private EventAction   tickAction;      // on clock tick
   private EventAction   entryAction;     // on activation

      /* constructors
      /***************/

      /**
       *  Creates a user interface screen that displays prompts and
       *  responds to button and key presses.
       */

   Screen( DisplayDevice theDisplay ) {

      display        = theDisplay;
      prompts        = new ArrayList<Prompt>();
      transition     = new Screen[ScreenButtonDevice.NUM_SCREEN_BUTTONS];
      btnPressAction = new EventAction[ScreenButtonDevice.NUM_SCREEN_BUTTONS];
      escapeTarget   = null;
      keyPressAction = null;
      tickAction     = null;
      entryAction    = null;
      state          = null;
   }

      /* methods
      /**********/

      /**
       *  Give the screen another prompt to display when it is activated.
       *  If the prompt parameter or its text field are null, the prompt
       *  is not added to the screen.
       *
       *   @param thePrompt The Prompt object added to a list of prompts.
       */

   void addPrompt( Prompt thePrompt ) {
      if ( (thePrompt == null) || (thePrompt.text == null) ) return;
      prompts.add( thePrompt );
   }

      /**
       *  Register a Screen as the one transitioned to when a screen button
       *  is pressed.
       *
       *     @pre 0 <= button < ScreenButtonDevice.NUM_SCREEN_BUTTONS
       *    @post the new transition is registered
       *   @param button  The screen button that causes the change
       *   @param screen  The screen changed to
       *  @throws IllegalArgumentException when @pre is violated
       */

   void setTransition( int button, Screen screen ) {
      if ( (button < 0) || (transition.length <= button) )
         throw new IllegalArgumentException(
            "Bad transition screen button " + button );
      transition[button] = screen;
   }

      /**
       *  Register a ScreenState object with this screen
       *
       *     @pre none
       *    @post the new ScreenState is registered
       *   @param newState  The new ScreenState instance
       */

   void setScreenState( ScreenState newState ) { state = newState; }

      /**
       *  Retrieve the ScreenState object from this screen
       *
       *     @pre none
       *    @post @return is the ScreenState object or null
       */

   ScreenState getScreenState() { return state; }

      /**
       *  Register an EventAction executed when a screen button is pressed.
       *
       *     @pre 0 <= button < ScreenButtonDevice.NUM_SCREEN_BUTTONS
       *    @post the new EventAction is registered
       *   @param button  The screen button that causes the execution
       *   @param action  The action taken
       *  @throws IllegalArgumentException when @pre is violated
       */

   void setButtonPressAction( int button, EventAction action ) {
      if ( (button < 0) || (btnPressAction.length <= button) )
         throw new IllegalArgumentException(
            "Bad action screen button " + button );
      btnPressAction[button] = action;
   }

      /**
       *  Register a screen for transition on an ESC key press.
       *
       *     @pre none
       *    @post the new EventAction is registered
       *   @param action  The action taken
       */

   void setEscapeTarget( Screen theTarget ) { escapeTarget = theTarget; }

      /**
       *  Register an EventAction executed when a key is pressed.
       *
       *     @pre none
       *    @post the new EventAction is registered
       *   @param action  The action taken
       */

   void setKeyPressAction( EventAction action ) { keyPressAction = action; }

      /**
       *  Register an EventAction executed when a minute passes.
       *
       *     @pre none
       *    @post the new EventAction is registered
       *   @param action  The action taken
       */

   void setTickAction( EventAction action ) { tickAction = action; }

      /**
       *  Register an EventAction executed when this screen is activated.
       *
       *     @pre none
       *    @post the new EventAction is registered
       *   @param action  The action taken
       */

   void setEntryAction( EventAction action ) { entryAction = action; }

      /**
       *  Run this method when a user interface screen becomes the
       *  current screen. This routine clears the screen, writes in 
       *  all prompts, and runs any registered activation actions.
       *
       *     @pre none
       *    @post display is cleared and the new screen prompts written
       *          to it; any registered activation action is executed
       *          with its arg value set to null.
       */

   void activate() {

      StringBuffer[] lines = new StringBuffer[DisplayDevice.NUM_LINES];

         // put the prompts into StringBuffers preparatory to being written
      for ( Prompt p : prompts ) {

            // ignore bad prompts
         if (    (p.text == null)
              || (p.column < 0)
              || (DisplayDevice.NUM_COLS <= p.column)
              || (p.line < 0)
              || (DisplayDevice.NUM_LINES <= p.line) ) continue;
                               
            // fill in the lines array
         if ( lines[p.line] == null )
            lines[p.line] = new StringBuffer( BLANK_LINE );
         lines[p.line].replace( p.column, p.column+p.text.length(), p.text );
      }

         // clear the display and write non-null lines to it
      display.clear();
      for ( int i = 0; i < lines.length; i++ )
         if ( lines[i] != null )
            display.writeLine(i, lines[i].substring(0,DisplayDevice.NUM_COLS));

         // if there is an entry action, then do it
      if ( entryAction != null ) entryAction.execute( this, null );

   } //activate

      /**
       *  Call this method from the current screen when the clock 
       *  ticks (every minute). It executes any registered tick action.
       *
       *     @pre none
       *    @post any registered tick action is executed with its arg
       *          value set to null.
       */

   void tick() {
      if ( tickAction != null ) tickAction.execute( this, null );
   }

      /**
       *  Process a screen button press. This involves executing any action
       *  associated with the button press, and determining the next screen
       *  to transition to, if any.
       *
       *     @pre 0 <= button < ScreenButtonDevice.NUM_SCREEN_BUTTONS
       *    @post any registered screen button press action is executed
       *          with its arg value set to the button number; @return is
       *          the associated transition target screen.
       *   @param button  The screen button that is pressed
       *  @throws IllegalArgumentException when @pre is violated
       */

   Screen screenButtonPress( int button ) {

      if ( (button < 0) || (transition.length <= button) )
         throw new IllegalArgumentException(
            "Bad transition screen button " + button );

      if ( btnPressAction[button] != null )
         btnPressAction[button].execute( this, new Integer(button) );

      return transition[button];

   } // screenButtonPress

      /**
       *  Respond to key strokes directed at this screen. Any registered 
       *  key press action is executed with its arg value set to the key.
       *
       *     @pre none
       *    @post any registered key press action is executed with its 
       *          arg value set to the key.
       *   @param key  The key pressed by a user.
       */

   Screen keyPress( KeyPress key ) {

       if ( keyPressAction != null ) keyPressAction.execute( this, key );

       return (key == KeyPress.ESC_KEY) ? escapeTarget : null;

   } // keyPress

} // Screen
