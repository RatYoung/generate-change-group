
package device.sim;

/**
 *  The SimDisplay provides a virtual device interface to a simulated display
 *  screen hardware device provided by swing components in SimDisplay.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.awt.Point;

import device.DisplayDevice;
import simulation.SimDisplay;

class SimDisplayDevice implements DisplayDevice {

      /* attributes
      /*************/

   private static final String BLANK_LINE
                           = "                                        ";
                           // 1234567890123456789012345678901234567890

   private final StringBuffer[] line;        // the display's contents
   private final SimDisplay     display;     // the simulated hardware

      /* constructors
      /***************/

      /**
       *  Creates a virtual display device.
       *
       *    @pre display is not null
       *   @post the display device is ready to be used
       */

   public SimDisplayDevice( SimDisplay display ) {

         // check the precondition
      if ( display == null ) throw new IllegalArgumentException();
      this.display = display;

         // create and initialize the character buffer for all screen data
      line = new StringBuffer[NUM_LINES];
      for ( int i = 0; i < NUM_LINES; i++ )
         line[i] = new StringBuffer(BLANK_LINE);

   } // SimDisplayDevice

      /* methods
      /**********/

      /**
       *  Blank the display screen.
       *
       *    @pre none
       *   @post the display screen is blank
       */

   public void clear() {

         // clear out the line buffer and the display lines
      for ( int i = 0; i < NUM_LINES; i++ ) {
         line[i] = new StringBuffer(BLANK_LINE);
         display.writeLine( i, line[i].toString() );
      }
   }

      /**
       *  Write the contents of the screen without any highlighting.
       *
       *    @pre none
       *   @post the display screen is rewritten without highlighting
       */

   public void clearHighlight() {

         // rewrite each line without highlighting
      for ( int i = 0; i < NUM_LINES; i++ )
         display.writeLine( i, line[i].toString() );
   }

      /**
       *  Blank a selected line
       *
       *     @pre 0 <= lineNumber < NUM_LINES
       *    @post the selected line is blanked
       *   @param lineNumber which line to clear
       *  @throws IllegalArgumentException if precondition is violated
       */

   public void clearLine( int lineNumber ) {

         // check precondition
      if ( (lineNumber < 0) || ( NUM_LINES <= lineNumber) )
         throw new IllegalArgumentException();

         // blank the line and rewrite it
      line[lineNumber] = new StringBuffer(BLANK_LINE);
      display.writeLine( lineNumber, line[lineNumber].toString() );
   }

      /**
       *  Highlight a portion of a line
       *
       *     @pre 0 <= lineNumber < NUM_LINES
       *          0 <= column < NUM_COLS
       *          0 <= length
       *    @post the portion of line lineNumber starting at the column
       *          character and extending for length characters (or until
       *          the end of the line) is highlighted
       *   @param lineNumber  Which line to highlight
       *   @param column      Which character to start highlighting at 
       *   @param length      How many characters to highlight
       *  @throws IllegalArgumentException if precondition is violated
       */

   public void highlight( int lineNumber, int column, int length ) {

         // check precondition
      if ( (lineNumber < 0) || ( NUM_LINES <= lineNumber) ||
           (column < 0)     || ( NUM_COLS <= column)      || (length < 0) )
         throw new IllegalArgumentException();

         // use html for highlighting
      StringBuffer newLine = new StringBuffer( "<html>" );
      int colCount = 0;
      while ( colCount < column ) {
         char nextChar = line[lineNumber].charAt( colCount++ );
         if ( nextChar == ' ' ) newLine.append( "&nbsp;" );
         else                   newLine.append( nextChar );
      }

      newLine.append( "<font style=\"background-color:white; color:black\">" );

      for ( int i = 0; (i < length) && (colCount < NUM_COLS); i++ ) {
         char nextChar = line[lineNumber].charAt( colCount++ );
         if ( nextChar == ' ' ) newLine.append( "&nbsp;" );
         else                   newLine.append( nextChar );
      }

      newLine.append( "</font>" );

      while ( colCount < NUM_COLS )
         newLine.append( line[lineNumber].charAt(colCount++) );

         // write the new line to the display
      display.writeLine( lineNumber, newLine.toString() );
   }

      /**
       *  Display a String from the start of a designated line.
       *  Lines do not wrap--they are truncated. Short lines are padded
       *  with blanks. Any previous highligting is lost.
       *
       *    @pre 0 <= lineNumber < NUM_LINES
       *         str is not null
       *   @post the line is written to the display
       *  @param lineNumber  Which line to write
       *  @param str         The string written at the line
       *  @throws IllegalArgumentException if precondition is violated
       */

   public void writeLine( int lineNumber, String str ) {

         // check precondition
      if ( (lineNumber < 0) || ( NUM_LINES <= lineNumber) || (str == null) )
         throw new IllegalArgumentException();

         // truncate the string if necessary
      if ( NUM_COLS < str.length() ) str = str.substring(0,NUM_COLS-1);

         // replace the line with the new string
      line[lineNumber] = new StringBuffer( str );

         // pad the line if necessary
      for ( int i = str.length(); i < NUM_COLS; i++ )
         line[lineNumber].append(' ');

         // write the new line to the display
      display.writeLine( lineNumber, line[lineNumber].toString() );
   }

} // SimDisplayDevice
