
package simulation;

/**
 *  A SimValve simulates an irrigation valve device.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.util.Observable;

public class SimValve extends Observable {

      /* static fields
      /****************/

      /* attributes
      /*************/

   private final String name;       // the unique valve identifier
   private final int    flowRate;   // gallons per minute

   private boolean isOpen;          // true iff valve is open
   private boolean isFailed;        // true iff failure is simulated

      /* constructors
      /****************/

      /**
       *  Creates a simulated irrigation valve.
       *
       *    @pre none
       *   @post all attributes are initialized
       *  @param valveID The new valve's identifier
       */

   public SimValve( String valveID, int theFlowRate ) {
      name     = valveID;
      flowRate = theFlowRate;
      isOpen   = false;
      isFailed = false;
   }

      /* methods
      /**********/

      /**
       *  Open the valve.
       *
       *     @pre None
       *    @post If the device has failed, @return == -1
       *          If the device has not failed, @return == 1, the valve
       *          opens, and observers are notified of a change
       */

   public int open() {
      if ( isFailed ) return -1;
      isOpen = true;
      setChanged();
      notifyObservers();
      return 1;
   }

      /**
       *  Close the valve.
       *
       *     @pre None
       *    @post If the device has failed, @return == -1
       *          If the device has not failed, @return == 1, the valve
       *          closes, and observers are notified of a change
       */

   public int close() {
      if ( isFailed ) return -1;
      isOpen = false;
      setChanged();
      notifyObservers();
      return 1;
   }

      /**
       *  Make a valve fail.
       *
       *     @pre None
       *    @post set the failed flag to true and the isOpen flag to false;
       *          notify observers to redraw the site with the valve closed
       */

   public void setIsFailed() { 
      isFailed = true;
      isOpen   = false; 
      setChanged();
      notifyObservers();
   }

      /**
       *  Standard get and set operations.
       *
       *    @pre None
       *   @post The attribute is set or fetched
       */

   public String  getName()     { return name; }
   public boolean isOpen()      { return isOpen; }
   public int     getFlowRate() { return flowRate; }
   public boolean getIsFailed() { return isFailed; }

   public void resetIsFailed() { isFailed = false; }

} // SimValve
