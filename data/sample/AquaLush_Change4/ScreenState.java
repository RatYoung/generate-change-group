
package ui;

/**
 *  The ScreenState is an abstract superclass for subclasses whose instances 
 *  keep track of internal details of a screen's state. This state data is used 
 *  to supply the UIController with what it needs to maintain the display screen. 
 *
 *  This class has many subclasses that are particularized to the needs of 
 *  different screens. This provides a way to decompose the UIController 
 *  and Screen classes. Common resourcs are maintained in this superclass.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.Clock;

import java.text.DecimalFormat;

abstract class ScreenState {

      /* attributes
      /*************/

   protected static final String BLANK_LINE 
                              = "                                        ";
                   //            1234567890123456789012345678901234567890


   protected final DecimalFormat milTime; // for military format time strings
   protected final Clock         clock;   // for fetching the current time

      /* constructors
      /***************/

      /**
       *  Initialize protected attributes.
       */

   ScreenState() {
      milTime = new DecimalFormat( "0000" );
      clock = Clock.instance();
   }

      /**
       *  Beep the terminal.
       *
       *     @pre none
       *    @post the terminal is beeped
       */

   protected void beep() {
      System.out.print("");
   }

} // ScreenState
