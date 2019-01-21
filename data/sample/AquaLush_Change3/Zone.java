
package irrigation;

/**
 *  The Zone holds data about part of the irrigation site.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import device.DeviceFailureException;
import device.SensorDevice;
import device.ValveDevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class Zone {

      /* attributes
      /*************/

   private final String     id;       // unique zone identifier
   private final String     location; // description from configuration
   private final Set<Valve> valves;   // all the valves in this zone

   private int    criticalLevel;      // when this zone needs irrigation
   private int    allocation;         // gallons per irrigation cycle
   private int    waterUsed;          // gallons used so far in a cycle
   private Sensor sensor;             // this zone's moisture sensor

      /* constructors
      /***************/

      /**
       *  Initialize the Zone
       */

   Zone( String theID, String theLocation ) {

         // initialize basic attributes
      id            = theID;
      location      = theLocation;
      valves        = new HashSet<Valve>();
      criticalLevel = 0;
      allocation    = 0;
      waterUsed     = 0;
      sensor        = null;

   } // Zone

      /* methods
      /**********/

      /**
       *  Trivial attributes get methods
       */

   String getID()                    { return id; }
   String getLocation()              { return location; }
   int    getWaterUsed()             { return waterUsed; }
   int    getCriticalMoistureLevel() { return criticalLevel; }

      /**
       *  Count the working valves in this zone.
       *
       *     @pre none
       *    @post @return is the number of working valves in this zone
       */

   int getNumWorkingValves() {
      int result = 0;
      for ( Valve v : valves ) if ( !v.isFailed() ) result++;
      return result;
   }

      /**
       *  Return a zone report.
       *
       *     @pre none
       *    @post @return is a new zone report
       */

   ZoneReport getZoneReport() {
      return new ZoneReport( id, location, criticalLevel );
   }

      /**
       *  Return a collection of valve reports for all valves in this zone.
       *
       *     @pre none
       *    @post @return is a new collection of valve reports
       */

   Collection<ValveReport> getValveReports() {
      ArrayList<ValveReport> result = new ArrayList<ValveReport>();
      for ( Valve v : valves )
         result.add( v.getValveReport(id) );
      return result;
   }

      /**
       *  Return a collection of valve reports for the failed valves in 
       *  this zone.
       *
       *     @pre none
       *    @post @return is a new collection of valve reports
       */

   Collection<ValveReport> getFailedValveReports() {
      ArrayList<ValveReport> result = new ArrayList<ValveReport>();
      for ( Valve v : valves )
         if ( v.isFailed() )
            result.add( v.getValveReport(id) );
      return result;
   }

      /**
       *  Return a collection of sensor reports for the failed sensors in 
       *  this zone (though there can be at most one).
       *
       *     @pre none
       *    @post @return is a new collection of valve reports
       *  @throws DeviceFailureException if checking the sensor causes a
       *          hardware failure
       */

   Collection<SensorReport> getFailedSensorReports() 
                                   throws DeviceFailureException {

      ArrayList<SensorReport> result = new ArrayList<SensorReport>();
      if ( sensor.isFailed() )
         result.add( sensor.getSensorReport(id) );
      return result;
   }

      /**
       *  Return a failed device reports for a designated device or null
       *  if the device is not in the zone; furthermore, if the device is
       *  found but has not failed, throw an exception.
       *
       *     @pre deviceID must name a failed device or no device
       *    @post @return is a new collection of valve reports
       *  @throws IllegalArgumentException if @pre is violated
       */

   FailedDeviceReport getFailedDeviceReport( String deviceID ) {

         // see if the failed device is the sensor
      if ( deviceID.equals(sensor.getID()) ) {
         if ( !sensor.isFailed() )
            throw new IllegalArgumentException(
               "Device "+ deviceID +" has not failed." );

         return new FailedDeviceReport( deviceID, id, 
                                        sensor.getLocation(),
                                        sensor.isRecorded() );
      }

         // see if the failed device is one of the valves
      for ( Valve v : valves )
         if ( deviceID.equals(v.getID()) ) {
            if ( !v.isFailed() )
               throw new IllegalArgumentException(
                  "Device "+ deviceID +" has not failed." );

            return new FailedDeviceReport( deviceID, id, 
                                           v.getLocation(),
                                           v.isRecorded() );
         }

        // if we get here, the device is not in this zone, so return null
      return null;

   } // getDeviceFailedDeviceReport

      /**
       *  Set the failure flag for a particular device, or do nothing if
       *  the device is not found.
       *
       *     @pre none
       *    @post the failure flag for the designated device is set to the
       *          specified value, or nothing is changed if the device is
       *          not found
       *   @param deviceID  The device whose flag is set
       *   @param newValue  How to set the failure flag
       */

   void setIsFailed( String deviceID, boolean newValue ) {

         // see if the failed device is the sensor
      if ( deviceID.equals(sensor.getID()) ) {
         sensor.setIsFailed( newValue );
         return;
      }

         // see if the failed device is one of the valves
      for ( Valve v : valves )
         if ( deviceID.equals(v.getID()) ) {
            v.setIsFailed( newValue );
            
            //Close valve
            try {
				v.close();
				//no further action has to be taken since the Failure 
				//is already being handled
			} catch (DeviceFailureException e) {}   
            return;
         }

   } // setIsFailed

      /**
       *  Set the state recorded flag for a particular device, or do nothing if
       *  the device is not found.
       *
       *     @pre none
       *    @post the state recorded flag for the designated device is set to
       *          the specified value, or nothing is changed if the device is
       *          not found
       *   @param deviceID  The device whose flag is set
       *   @param newValue  How to set the state recorded flag
       */

   void setIsRecorded( String deviceID, boolean newValue ) {

         // see if the failed device is the sensor
      if ( deviceID.equals(sensor.getID()) ) {
         sensor.setIsRecorded( newValue );
         return;
      }

         // see if the failed device is one of the valves
      for ( Valve v : valves )
         if ( deviceID.equals(v.getID()) ) {
            v.setIsRecorded( newValue );
            return;
         }

   } // setIsRecorded

      /**
       *  Read the moisture sensor to get the current level.
       *
       *     @pre none
       *    @post @return is the level or an exception is thrown
       *  @throws DeviceFailureException if the sensor hardware fails
       */

   int getMeasuredMoistureLevel() throws DeviceFailureException {

      if ( sensor.isFailed() ) throw new DeviceFailureException();
      return sensor.read();
   }

      /**
       *  Set the zone's critical moisture level
       *
       *     @pre 0 <= theLevel <= 100
       *    @post the critical moisture level is set or an exception is thrown
       *   @param theLevel  The new moisture level
       *  @throws IllegalArgumentException if @pre is violated
       */

   void setCriticalMoistureLevel( int theLevel ) {

      if ( (theLevel < 0) || (100 < theLevel) )
         throw new IllegalArgumentException(
             "Critical moisture level out of range." );

      criticalLevel = theLevel;

   } // setCriticalMoistureLevel
      
      /**
       *  Set the zone's irrigation water allocation.
       *
       *     @pre 0 <= theAllocation
       *    @post the allocation is set or an exception is thrown
       *   @param theAllocation  The new water allocation
       *  @throws IllegalArgumentException if @pre is violated
       */

   void setAllocation( int theAllocation ) {

      if ( theAllocation < 0 )
         throw new IllegalArgumentException(
             "Water allocation "+ theAllocation +"is out of range." );

      allocation = theAllocation;

   } // setAllocation
      
      /**
       *  Add a new sensor to this zone.
       *
       *     @pre sensorID is not null; device is not null
       *    @post a new sensor is created and added to the zone, or an
       *          an exception is thrown
       *   @param sensorID Unique sensor identifier
       *   @param device   The hardware sensor device
       *   @param location Description from the configuration file
       *  @throws IllegalArgumentException if @pre is violated
       */

   void addSensor( String sensorID, SensorDevice device, String location ) {

      if ( sensorID == null )
         throw new IllegalArgumentException( "Null sensor identifier. " );
      if ( device == null )
         throw new IllegalArgumentException( "Null sensor device. " );

      sensor = new Sensor( sensorID, device, location );
   }

      /**
       *  Add a new valve to this zone.
       *
       *     @pre valveID is not null; device is not null; 0 < flowRate
       *    @post a new valve is created and added to the zone, or an
       *          an exception is thrown
       *   @param valveID   Unique valve identifier
       *   @param device    The hardware valve device
       *   @param valveType Description from the configuration file
       *   @param flowRate  Gallons per minute from this valve
       *   @param location  Description from the configuration file
       *  @throws IllegalArgumentException if @pre is violated
       */

   void addValve( String valveID, ValveDevice device,
                  String valveType, int flowRate, String location) {

      if ( valveID == null )
         throw new IllegalArgumentException( "Null valve identifier. " );
      if ( device == null )
         throw new IllegalArgumentException( "Null valve device. " );
      if ( flowRate < 1 )
         throw new IllegalArgumentException( "Non-positive flow rate. " );

      valves.add( new Valve(valveID,device,valveType,flowRate,location) );
   }

      /**
       *  Mark a device as repaired, returning true iff the indicated
       *  device was repaired.
       *
       *     @pre none
       *    @post if the deviceID is found in this zone, the device is marked
       *          as repaired and @result is true; otherwise, @result is false
       *   @param deviecID  Unique device identifier
       */

   boolean repairDevice( String deviceID ) {

         // see if the repaired device is the sensor
      if ( deviceID.equals(sensor.getID()) ) {
         sensor.setIsFailed( false );
         return true;
      }

         // see if the repaired device is one of the valves
      for ( Valve v : valves )
         if ( deviceID.equals(v.getID()) ) {
            v.setIsFailed( false );
            return true;
         }

         // if we get here, the device is not in this zone
      return false;

   } // repairDevice

      /**
       *  Reset data at the start of an irrigation cycle.
       *
       *     @pre none
       *    @post all valves are reset and counters are initialized.
       */

   void reset() {

      waterUsed = 0;
      for ( Valve v : valves ) {
         if ( !v.isFailed() ) v.reset();
      }

   } // reset

      /**
       *  Notify all the valves that one minute has passed and increment
       *  the waterUsed counter.
       *
       *     @pre none
       *    @post all valves are notified that time has passed by calling 
       *          their tick routines; the water used counter is updated
       */

   void tick() {

      waterUsed = 0;
      for ( Valve v : valves ) {
         if ( !v.isFailed() ) {
            v.tick();
            waterUsed += v.getWaterUsed();
         }
      }

   } // tick

      /**
       *  Open a closed valve or close an open valve.
       *
       *     @pre valveID must be valid
       *    @post if a valve is working, it is closed if open and opened if
       *          closed; a valve that fails is taken out of service
       *   @param valveID  The valve that is toggled
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws DeviceFailureException if the valve hardware fails
       */

   void toggleValve( String valveID ) throws DeviceFailureException {

         // toggle the designated valve if it is here and working
      for ( Valve v : valves ) {
         if ( valveID.equals(v.getID()) ) {
            if ( !v.isFailed() ) {
               if ( v.isOpen() ) v.close();
               else              v.open();
            }
            return;
         }
      }

         // if we get here, the valve id is bad
      throw new IllegalArgumentException( "Bad valve identifer." );

   } // toggelValve

      /**
       *  Open all valves in the zone.
       *
       *     @pre none
       *    @post all working valves are opened or an exception is thrown
       *  @throws DeviceFailureException if one or more valves fail
       */

   void openAllValves() throws DeviceFailureException {

         // arrange to collect all exceptions
      DeviceFailureException allExceptions = null;

         // try to open all the valves
      for ( Valve v : valves ) {
         if ( !v.isFailed() ) {
            try   { v.open(); }
            catch ( DeviceFailureException e ) {
               if ( allExceptions == null ) allExceptions = e;
               else allExceptions.addException( e );
            }
         }
      }

         // propagate any accumulated exceptions
      if ( allExceptions != null ) throw allExceptions;

   } // openAllValves

      /**
       *  Close all valves in the zone.
       *
       *     @pre none
       *    @post all working valves are closed or an exception is thrown
       *  @throws DeviceFailureException if one or more valves fail
       */

   void closeAllValves() throws DeviceFailureException {

         // arrange to collect all exceptions
      DeviceFailureException allExceptions = null;

         // try to close all the valves
      for ( Valve v : valves ) {
         if ( !v.isFailed() ) {
            try   { v.close(); }
            catch ( DeviceFailureException e ) {
               if ( allExceptions == null ) allExceptions = e;
               else allExceptions.addException( e );
            }
         }
      }

         // propagate any accumulated exceptions
      if ( allExceptions != null ) throw allExceptions;

   } // closeAllValves

      /**
       *  Determine whether watering is done in this zone.
       *
       *     @pre none
       *    @post @returns true iff either allocation <= waterUsed, or
       *          the critical moisture level <= actual moisture level
       */

   boolean isIrrigated() throws DeviceFailureException {

         // return true if the water allocation is exhausted
      if ( allocation <= waterUsed ) return true;

         // return true if the ground is wet enough
      if ( criticalLevel <= sensor.read() ) return true;

         // return true if all the valves have failed
      if ( getNumWorkingValves() == 0 ) return true;

         // if we get here, we're not done
      return false;
   }

} // Zone
