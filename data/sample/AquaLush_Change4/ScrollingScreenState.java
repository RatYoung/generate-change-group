
package ui;

/**
 *  The ScreenState is an abstract superclass for subclasses whose instances 
 *  keep track of internal details of a screen's state. This state data is
 *  used to supply the UIController with what it needs to maintain the 
 *  display screen. 
 *
 *  This class has many subclasses that are particularized to the needs of 
 *  different screens. This provides a way to decompose the UIController 
 *  and Screen classes. Common resourcs are maintained in this superclass.
 *
 *   @author C. Fox
 *  @version 07/08
 */


interface ScrollingScreenState {

      /**
       *  Get the array of items displayed in the scrolled region.
       *
       *     @pre none
       *    @post @return is the scolled array of items
       */

   Item[] getItems();

      /**
       *  Set the current item in response to user actions.
       *
       *     @pre theItem must be between 0 and items.length-1
       *    @post adjust the current item in the scrolling screen state
       *  @throws IllegalArgumentException if theItem is out of range
       */

   void setCurrentItem( int theItem ) throws IllegalArgumentException;

      /**
       *  Get the current item for display in response to changes in the
       *  items array.
       *
       *     @pre none
       *    @post @return is in the range 0 to items.length-1; if this range
       *          is empty, the result is undefined
       */

   int getCurrentItem();

} // ScrollingScreenState
