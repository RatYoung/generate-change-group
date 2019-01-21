
package irrigation;

/**
 *  A Valve is the Irrigator's representation of a zone sensor.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DeviceFailureException;
import device.ValveDevice;

class Valve {

      /* attributes
      /**************/

   private final String      id;       // unique valve identifier
   private final String      location; // description from configuration
   private final String      type;     // what sort of balve this is
   private final ValveDevice device;   // hardware controlled by this object

   private int     flowRate;           // gallons per minute through the valve
   private boolean isOpen;             // current status
   private boolean isFailed;           // true iff hardware has failed
   private boolean isRecorded;         // true iff failure state in store
   private int     minutesOpen;        // how long during this cycle

      /* constructors
      /****************/

      /**
       *  Initialize the Valve
       */

   Valve( String theID, ValveDevice theDevice, 
          String theType, int theRate, String theLocation ) {

         // initialize basic attributes
      id          = theID;
      location    = theLocation;
      device      = theDevice;
      type        = theType;
      flowRate    = theRate;
      isOpen      = false;
      isFailed    = false;
      isRecorded  = true;
      minutesOpen = 0;

   } // Valve

      /* methods
      /**********/

      /**
       *  Trivial get methods
       */

   String  getID()        { return id; }
   String  getLocation()  { return location; }
   int     getWaterUsed() { return flowRate * minutesOpen; }
   boolean isOpen()       { return isOpen; }
   boolean isFailed()     { return isFailed; }
   boolean isRecorded()   { return isRecorded; }

      /**
       *  Set the device failure flag to a desired value.
       *
       *     @pre none
       *    @post the device failure flag is set as directed
       *   @param newValue  How the flag is to be set
       */

   void setIsFailed( boolean newValue ) { isFailed = newValue; }

      /**
       *  Set the device failure recorded flag to a desired value.
       *
       *     @pre none
       *    @post the device failure recorded flag is set as directed
       *   @param newValue  How the flag is to be set
       */

   void setIsRecorded( boolean newValue ) { isRecorded = newValue; }

      /**
       *  Open a valve.
       *
       *     @pre none
       *    @post as many as three attempts are made to open the valve;
                  if the device fails three times, it is marked as failed
       *          and closed, and an exception is thrown
       *  @throws DeviceFailureException if the hardware valve fails
       */

   void open() throws DeviceFailureException {

      isOpen = true;
      try { 
         device.open();
      }
      catch ( DeviceFailureException e1 ) {
         try { 
            device.open();
         }
         catch ( DeviceFailureException e2 ) {
            try { 
               device.open();
            }
            catch ( DeviceFailureException e3 ) {
               isFailed = true;
               isOpen   = false;
               e3.addDevice( id );
               throw e3;
            }
         }
      }

   } // open

      /**
       *  Close a valve.
       *
       *     @pre none
       *    @post as many as three attempts are made to close the valve;
                  if the device fails three times, it is marked as failed
       *          and closed, and an exception is thrown
       *  @throws DeviceFailureException if the hardware valve fails
       */

   void close() throws DeviceFailureException {

      isOpen = false;
      try { 
         device.close();
      }
      catch ( DeviceFailureException e1 ) {
         try { 
            device.close();
         }
         catch ( DeviceFailureException e2 ) {
            try { 
               device.close();
            }
            catch ( DeviceFailureException e3 ) {
               isFailed = true;
               e3.addDevice( id );
               throw e3;
            }
         }
      }

   } // close

      /**
       *  Create and return a new ValveReport
       *
       *     @pre none
       *    @post a new valve report is created and initialized
       *   @param zoneID  Zone identifier the zone where this valve lives
       */

   ValveReport getValveReport( String zoneID ) {
      return new ValveReport( id, location, zoneID, isOpen, isFailed, 
                              isRecorded, flowRate, minutesOpen );
   }

      /**
       *  Reset the minutes open counter.
       *
       *     @pre none
       *    @post the minutes open counter is set to 0
       */

   void reset() { minutesOpen = 0; }

      /**
       *  Increment the minutes open counter if the valve is open.
       *
       *     @pre none
       *    @post the minutes open counter is incremented if the valve is open
       */

   void tick() { if ( isOpen ) minutesOpen++; }

} // Valve
