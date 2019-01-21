
package ui;

/**
 *  The Prompt class is merely a container for a screen prompt String and
 *  the line and column where it is to be displayed.
 *
 *   @author C. Fox
 *  @version 07/06
 */

final class Prompt {

      /* attributes
      /*************/

   final String text;      // content of the prompt
   final int    line;      // line to write prompt
   final int    column;    // column to write prompt

      /* constructors
      /***************/

      /**
       *  Assign values to the Prompt container fields.
       *
       *   @param theLine   Where the prompt text is displayed
       *   @param theColumn Where the prompt text is displayed
       *   @param theText   The String displayed
       */

   Prompt( String theText, int theLine, int theColumn ) {
      text   = theText;
      line   = theLine;
      column = theColumn;
   }

      /**
       *  Assign a Prompt String for display at line 0 column 0.
       *
       *   @param theText The String displayed
       */

   Prompt( String theText ) { this(theText,0,0); }

} // Prompt
