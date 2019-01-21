
package device;

/**
 *  The DisplayDevice interface specifies the operations of a virtual AquaLush
 *  control panel display screen.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface DisplayDevice {

      /* static fields
      /*****************/

   static final int NUM_LINES = 16;    // in this display screen
   static final int NUM_COLS  = 40;    // in this display screen

      /* methods
      /***********/

      /**
       *  Blank the display screen
       *
       *    @pre none
       *   @post the display screen is blank
       */

   void clear();

      /**
       *  Write the contents of the screen without any highlighting.
       *
       *    @pre none
       *   @post the display screen is rewritten without highlighting
       */

   void clearHighlight();

      /**
       *  Blank a selected line
       *
       *    @pre 0 <= lineNumber < NUM_LINES
       *   @post the selected line is blanked
       *  @param lineNumber  Which line to clear
       * @throws IllegalArgumentException if precondition is violated
       */

   void clearLine( int lineNumber );

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

   void highlight( int lineNumber, int column, int length );

      /**
       *  Display a String from the start of a designated line.
       *  Lines do not wrap--they are truncated. Short lines are padded
       *  with blanks. Any previous highlighting is lost.
       *
       *    @pre 0 <= lineNumber < NUM_LINES
       *         str is not null
       *   @post if str is more than 40 characters it is truncated to 40 
       *         characters;
       *         if str is less than 40 characters, it is padded with 
       *         blanks until it is 40 characters long;
       *         then str is written to the display at column 0;
       *         the line is not highlighted.
       *  @param lineNumber  Which line to write
       *  @param str         The string written at the line
       *  @throws IllegalArgumentException if precondition is violated
       */

   void writeLine( int lineNumber, String str );

} // DisplayDevice
