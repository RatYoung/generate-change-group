
package ui;

/**
 *  The Item class holds data about each of the lines is a scrollable region
 *  of a scrolling screen. It is a record class.
 *
 *   @author C. Fox
 *  @version 07/08
 */

final class Item {

      /* attributes
      /**************/

   public final String text;       // the text of the line
   public final String value1;     // to take action when this item is selected
   public final String value2;     // to take action when this item is selected

      /* constructors
      /****************/

      /**
       *  Initialize the Item
       */

   public Item( String theText, String theValue1, String theValue2 ) {

      text   = theText;
      value1 = theValue1;
      value2 = theValue2;
   }

} // Item
