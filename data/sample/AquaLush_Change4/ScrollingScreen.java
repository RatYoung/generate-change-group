
package ui;

/**
 *  A ScrollingScreen is a special kind of Screen that scrolls a region of
 *  the screen between the rows 2 and 4. It augments the Screen class by
 *  including routines to handle scrolling.
 *
 *   @author C. Fox
 *  @version 07/08
 *
 *  @invariant if 0 < items.length then 0 <= crntItem < items.length
 *             else crntItem is undefined
 */

import device.DisplayDevice;

class ScrollingScreen extends Screen {

      /* attributes
      /*************/

   private Item[] items;       // shown in the scrolling display
   private int    crntItem;    // which item is now selected

      /* constructors
      /***************/

      /**
       *  Creates a user interface screen that displays prompts, scrolls
       *  a region of the screen, and responds to button and key presses.
       */

   ScrollingScreen( DisplayDevice theDisplay ) {

      super( theDisplay );
    }

      /* methods
      /**********/

      /**
       *  This method overrides a method in the superclass, but calls it to
       *  handle common activation processing. Then it displays the scrolled
       *  items.
       *
       *     @pre none
       *    @post display is cleared and the new screen prompts written
       *          to it; the scrolled region is displayed; any registered 
       *          activation action is executed with its arg value se to null.
       */

   void activate() {

         // call the ancestor operation for normal screen activation
      super.activate();

         // obtain the scrolling items and write them out
      ScrollingScreenState scrnState = (ScrollingScreenState)state;
      items = scrnState.getItems();
      if ( 0 < items.length ) {
         crntItem = Math.min( 1, items.length-1 );
         scrnState.setCurrentItem( crntItem );
      }
      displayScrolledItems();

   } //activate


      /**
       *  Process a screen button press. This method first invokes the
       *  method in the superclass that it overrides to handle normal 
       *  button press processing, and then it handles button presses
       *  involving scolling.
       *
       *     @pre 0 <= button < ScreenButtonDevice.NUM_SCREEN_BUTTONS
       *    @post any registered screen button press action is executed
       *          with its arg value set to the button number; scrolling
       *          is implemented; @return is the associated transition 
       *          target screen.
       *   @param button  The screen button that is pressed
       *  @throws IllegalArgumentException when @pre is violated
       */

   Screen screenButtonPress( int button ) {

      ScrollingScreenState scrnState = (ScrollingScreenState)state;

         // call ancestor for standard screen processing
      Screen result = super.screenButtonPress( button );

         // implement scrolling
      if ( (button == 3) && (crntItem+1 < items.length) ) { // scroll up
            scrnState.setCurrentItem( ++crntItem );
            displayScrolledItems();
      }
      if ( (button == 5) && (0 < crntItem) ) { // scroll down
            scrnState.setCurrentItem( --crntItem );
            displayScrolledItems();
      }

      return result;

   } // screenButtonPress

      /**
       *  Redisplay the scrolled portion of the screen, highlighting the
       *  current item.
       *
       *     @pre none
       *    @post any refreshed items are retireived and the visible
       *          items are redisplayed, with the current item highlighted
       */

   void displayScrolledItems() {

         // refresh the items array and the crntItem
      ScrollingScreenState scrnState = (ScrollingScreenState)state;
      items    = scrnState.getItems();

         // if there are no items, blank the scrolling region and return
      if ( items.length == 0 ) {
         display.writeLine( 2, BLANK_LINE );
         display.writeLine( 3, BLANK_LINE );
         display.writeLine( 4, BLANK_LINE );
         return;
      }

         // write stuff in the window
      crntItem = scrnState.getCurrentItem();
      display.writeLine( 2, (crntItem == 0) ?
                                 BLANK_LINE : items[crntItem-1].text );
      display.writeLine( 3, items[crntItem].text );
      display.writeLine( 4, (crntItem == items.length-1) ?
                                 BLANK_LINE : items[crntItem+1].text );

      display.highlight( 3, 3, items[crntItem].text.length()-3 );

   } // displayScrolledItems

} // ScrollingScreen
