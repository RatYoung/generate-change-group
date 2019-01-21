
package irrigation;

/**
 *  A Sensor is the Irrigator's representation of a zone sensor.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DeviceFailureException;
import device.SensorDevice;

class Sensor {

      /* attributes
      /*************/

   private final String       id;        // unique sensor identifier
   private final String       location;  // description from configuration
   private final SensorDevice device;    // hardware read for moisture level

   private boolean isFailed;             // true iff hardware has failed
   private boolean isRecorded;           // true iff failure state in store

      /* constructors
      /***************/

      /**
       *  Initialize the sensor
       */

   Sensor( String theID, SensorDevice theDevice, String theLocation ) {

         // initialize basic attributes
      id         = theID;
      location   = theLocation;
      device     = theDevice;
      isFailed   = false;
      isRecorded = true;

   } // Sensor

      /* methods
      /**********/

      /**
       *  Trivial attribute get operations
       */

   String  getID()       { return id; }
   String  getLocation() { return location; }
   boolean isFailed()    { return isFailed; }
   boolean isRecorded()  { return isRecorded; }

      /**
       *  Create a report about this sensor. This may result in a sensor
       *  failure.
       *
       *     @pre none
       *    @post the sensor fields, plus a read of the sensor, is put into
       *          a sensor report.
       */

   SensorReport getSensorReport( String zoneID ) 
                                        throws DeviceFailureException {

      int level = 1;   // result of reading from the device

         // try to get the level
      try {
         level = read();
      }
      finally {
         return new SensorReport( id, location, zoneID, 
                                  isFailed, isRecorded, level );
      }
   }

      /**
       *  Read the sensor device.
       *
       *     @pre none
       *    @post try as many as three times to read the hardware device and
       *          return its value; if it fails three times, mark it as
       *          failed and throw an exception.
       *  @throws DeviceFailureException if the device fails
       */

   int read() throws DeviceFailureException {

      try {
         return device.read();
      }
      catch ( DeviceFailureException e1 ) {
         try {
            return device.read();
         }
         catch ( DeviceFailureException e2 ) {
            try {
               return device.read();
            }
            catch ( DeviceFailureException e3 ) {
               isFailed = true;
               e3.addDevice( id );
               throw e3;
            }
         }
      }

   } // read

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

} // Sensor
