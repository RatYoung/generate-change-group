
package irrigation;

/**
 *  The Irrigator is the main component of the irrigation layer. It oversees 
 *  irrigation, querying the sensors and controlling the valves. It is also
 *  the facade that supplies reports about devices, failures, and zones to
 *  clients.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.HashSet;
import java.util.Set;

import device.Clock;
import device.DeviceFailureException;
import device.StoreFailureException;
import device.SensorDevice;
import device.StorageDevice;
import device.ValveDevice;
import startup.StartupException;
import util.Day;

public class Irrigator implements Observer {

      /* attributes
      /*************/

      // system default values
   private final static String DEFAULT_MODE            = "automatic";
   private final static String DEFAULT_ALLOCATION      = "10000";
   private final static String DEFAULT_IRRIGATION_TIME = "200";
   private final static String DEFAULT_IRRIGATION_DAYS = "MTuWThFSaSu";
   private final static String DEFAULT_CRITICAL_LEVEL  = "50";
   private final static String DEFAULT_MAX_LEVEL       = "50";
   private final static String DEFAULT_IS_DEVICE_OK    = "true";

      // tags used in the persistent store property list
   private final static String MODE_TAG            = "mode";
   private final static String ALLOCATION_TAG      = "allocation";
   private final static String IRRIGATION_TIME_TAG = "irrigationTime";
   private final static String IRRIGATION_DAYS_TAG = "irrigationDays";
   private final static String CRITICAL_LEVEL_TAG  = ".criticalMoistureLevel";
   private final static String MAXIMAL_LEVEL_TAG   = ".maximalMoistureLevel";
   private final static String DEVICE_OK_TAG       = ".isWorking";


   private StorageDevice   store;           // persistent data store
   private Mode            mode;            // AUTOMATIC or MANUAL
   private int             allocation;      // gallons allowed for a cycle
   private int             irrigationTime;  // when an irrigation cycle begins
   private Set<Day>        irrigationDays;  // when irrigation occurs
   private Clock           clock;           // to ask for the current time
   private Set<Zone>       zones;           // all irrigation zones
   private IrrigationCycle cycle;           // irrigation cycle controller
   private List<String>    failedDevices;   // ids for display on failure
   private boolean         isStoreFailure;  // true iff read/write fails

      /* constructors
      /***************/

      /**
       *  Initialize the Irrigator and prepare for controlling irrigation.
       */

   public Irrigator( StorageDevice theStore ) {

         // initialize all attributes
      store          = theStore;
      mode           = Mode.AUTOMATIC;
      allocation     = 0;
      irrigationTime = 0;
      irrigationDays = new HashSet<Day>();
      clock          = Clock.instance();
      zones          = new HashSet<Zone>();
      cycle          = null;
      failedDevices  = new ArrayList<String>();
      isStoreFailure = false;

         // register as a listener with the clock
      clock.addObserver( this );

   } // Irrigator

      /* methods
      /**********/

      /**
       *  Read the persistent store and set the mode, water allocation,
       *  irrigation time, and irrigation days to what they were before
       *  or to defaults.
       *
       *     @pre none
       *    @post the irrigation state is restored, or an exception is thrown
       *  @throws StartupException if the persistent store cannot be read or
       *          its contents cannot be interpreted
       */

   public void restoreState() throws StartupException {

      try {

            // set the mode
         String storedString = store.getData( MODE_TAG, DEFAULT_MODE );
         try {
            mode = Mode.valueOf( storedString );
         }
         catch ( IllegalArgumentException e ) {
            throw new StartupException();
         }

            // set the allocation
         storedString = store.getData( ALLOCATION_TAG, DEFAULT_ALLOCATION );
         try {
            allocation = Integer.parseInt( storedString );
         }
         catch ( NumberFormatException e ) {
            throw new StartupException();
         }

            // set the irrigation time
         storedString = store.getData( IRRIGATION_TIME_TAG,
                                       DEFAULT_IRRIGATION_TIME);
         try {
            irrigationTime = Integer.parseInt( storedString );
         }
         catch ( NumberFormatException e ) {
            throw new StartupException();
         }

            // set the irrigation day
         storedString = store.getData( IRRIGATION_DAYS_TAG,
                                       DEFAULT_IRRIGATION_DAYS );
         for ( int i = 0; i < storedString.length(); i++ )
            switch ( storedString.charAt(i) ) {

               case 'M': irrigationDays.add( Day.MONDAY );    break;
               case 'W': irrigationDays.add( Day.WEDNESDAY ); break;
               case 'F': irrigationDays.add( Day.FRIDAY );    break;

               case 'T':
                  i++;
                  if ( i < storedString.length() )
                     switch ( storedString.charAt(i) ) {
                        case 'u': irrigationDays.add( Day.TUESDAY );  break;
                        case 'h': irrigationDays.add( Day.THURSDAY ); break;
                        default : throw new StartupException();
                     }
                  else
                     throw new StartupException();
                  break;

               case 'S':
                  i++;
                  if ( i < storedString.length() )
                     switch ( storedString.charAt(i) ) {
                        case 'a': irrigationDays.add( Day.SATURDAY ); break;
                        case 'u': irrigationDays.add( Day.SUNDAY );   break;
                        default : throw new StartupException();
                     }
                  else
                     throw new StartupException();
                  break;

               default : throw new StartupException();
            }
      }
      catch ( StoreFailureException e ) {
         throw new StartupException();
      }

   } // restoreState

      /**
       *  Add a new zone to the irrigation site.
       *
       *     @pre zoneID is not null
       *    @post a new Zone is created with the specified id and location,
       *          or an exception is thrown
       *   @param zoneID    Unique zone identifier for this zone
       *   @param location  Description from the configuration file
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws StartupException if the store cannot be read or interpreted
       */

   public void addZone( String zoneID, String location ) 
                                       throws StartupException {

         // check the precondition
      if ( zoneID == null )
         throw new IllegalArgumentException( "Null zone identifier. " );

         // add a new zone
      Zone zone = new Zone( zoneID, location );
      zones.add( zone );

         // restore the zone's critical moisture level
      String storedString;    // for retrieved data
      int    level;           // for data converted to int
      try {
         storedString = store.getData( zoneID + CRITICAL_LEVEL_TAG,
                                       DEFAULT_CRITICAL_LEVEL );
         try {
            level = Integer.parseInt( storedString );
         }
         catch ( NumberFormatException e ) {
            throw new StartupException();
         }
      }
      catch ( StoreFailureException e ) {
         throw new StartupException();
      }
      zone.setCriticalMoistureLevel( level );
      //TODO: zone.setMaximalMoistureLevel( level );
   }

      /**
       *  Add a new sensor to a zone.
       *
       *     @pre zoneID is not null; zoneID identifies an existing zone
       *    @post a new sensor is created and added to the zone, or an
       *          an exception is thrown
       *   @param zoneID    Unique zone identifier for this zone
       *   @param sensorID  Unique sensor identifier
       *   @param device    The hardware sensor device
       *   @param location  Description from the configuration file
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws StartupException if the store cannot be read or interpreted
       */

   public void addSensor( String zoneID, String sensorID, SensorDevice device,
                          String location ) throws StartupException {

         // add the a new sensor to the right zone
      Zone zone = findZone( zoneID );
      zone.addSensor( sensorID, device, location );

         // restore the sensor's failure state
      String storedString;    // for retrieved data
      try {
         storedString = store.getData( sensorID + DEVICE_OK_TAG,
                                                  DEFAULT_IS_DEVICE_OK );
      }
      catch ( StoreFailureException e ) {
         throw new StartupException();
      }
      if ( storedString.equals("false") ) zone.setIsFailed( sensorID, true );

   } // addSensor

      /**
       *  Add a new valve to a zone.
       *
       *     @pre zoneID is not null; zoneID identifies an existing zone
       *    @post a new valve is created and added to the zone, or an
       *          an exception is thrown
       *   @param zoneID    Unique zone identifier for this zone
       *   @param valveID   Unique sensor identifier
       *   @param device    The hardware sensor device
       *   @param location  Description from the configuration file
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws StartupException if the store cannot be read or interpreted
       */

   public void addValve( String zoneID, String valveID, ValveDevice device,
                         String valveType, int flowRate, String location )
                         throws StartupException {

         // add the a new valve to the right zone
      Zone zone = findZone( zoneID );
      zone.addValve( valveID,device,valveType,flowRate,location );

         // restore the valves's failure state
      String storedString;    // for retrieved data
      try {
         storedString = store.getData( valveID + DEVICE_OK_TAG,
                                                 DEFAULT_IS_DEVICE_OK );
      }
      catch ( StoreFailureException e ) {
         throw new StartupException();
      }
      if ( storedString.equals("false") ) zone.setIsFailed( valveID, true );

   } // addValve

      /**
       *  Trivial get methods.
       */

   public Mode    getMode()           { return mode; }
   public int     getIrrigationTime() { return irrigationTime; }
   public int     getAllocation()     { return allocation; }
   public boolean isStoreFailure()    { return isStoreFailure; }

      /**
       *  Determine whether there are any unreported failed devices.
       *
       *     @pre none
       *    @post @return is true iff there are unreported failed devices
       */

   public synchronized boolean isFailedDevice() {
      return 0 < failedDevices.size();
   }

      /**
       *  Return the id of an unreported failed device.
       *
       *     @pre none
       *    @post @return is the id of an unreported failed device or
       *          null if there is no such device
       */

   public synchronized String getNextFailedDevice() {
      if ( 0 < failedDevices.size() ) return failedDevices.remove(0);
      else                            return null;
   }

      /**
       *  Set the unreported store failure flag.
       *
       *     @pre none
       *    @post set the unreported store failure flag to newValue
       *   @param newValue  What to set the unreported failed store flag to
       */

   public synchronized void setIsStoreFailure( boolean newValue ) {
      isStoreFailure = newValue;
   }

      /**
       *  Get the set of irrigation days.
       *
       *     @pre none
       *    @post @return is a copy of the set of irrigation days
       */

   public Set<Day> getIrrigationDays() {
      return new HashSet<Day>( irrigationDays );
   }

      /**
       *  Get all the zone reports.
       *
       *     @pre none
       *    @post @return is a list of zone reports
       */

   public synchronized List<ZoneReport> getZoneReports() {
      ArrayList<ZoneReport> result = new ArrayList<ZoneReport>();
      for ( Zone z : zones )
         result.add( z.getZoneReport() );
      return result;
   } 

      /**
       *  Get all the valve reports.
       *
       *     @pre none
       *    @post @return is a list of valve reports
       */

   public synchronized List<ValveReport> getValveReports() {
      ArrayList<ValveReport> result = new ArrayList<ValveReport>();
      for ( Zone z : zones )
         result.addAll( z.getValveReports() );
      return result;
   } 

      /**
       *  Get a falure report.
       *
       *     @pre none
       *    @post @return is a failure report
       */

   public synchronized FailureReport getFailureReport() {
      ArrayList<SensorReport> failedSensors = new ArrayList<SensorReport>();
      ArrayList<ValveReport>  failedValves  = new ArrayList<ValveReport>();
      for ( Zone z : zones ) {
         try {
            failedSensors.addAll( z.getFailedSensorReports() );
         }
         catch ( DeviceFailureException e ) {
            handleDeviceFailure( e );
         }
         failedValves.addAll( z.getFailedValveReports() );
      }
      return new FailureReport( failedSensors, failedValves );
   } 

      /**
       *  Create a new failed device report for a given failed device.
       *
       *     @pre deviceID names a device; the named device has failed
       *    @post the requested report is returned, or an exception is thrown
       *   @param deviceID  The device that has failed
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized FailedDeviceReport getFailedDeviceReport(
                                                            String deviceID ) {

      FailedDeviceReport result;
      for ( Zone z : zones ) {
         result = z.getFailedDeviceReport( deviceID );
         if ( result != null ) return result;
      }

      throw new IllegalArgumentException( "Bad device id "+ deviceID +"." );
   }

      /**
       *  Fetch the critical moisture level for a zone
       *
       *     @pre zoneID names a zone
       *    @post the critical moisture level for the zone is returned
       *   @param zoneID  The zone whose critical moisture level is returned
       *  @throws IllegalArgumentException if @pre is violated
       */

   public int getCriticalMoistureLevel( String zoneID ) {

      return findZone(zoneID).getCriticalMoistureLevel();
      
   } // getCriticalMoistureLevel

      /**
       *  Fetch the measured moisture level for a zone (from its sensor)
       *
       *     @pre zoneID names a zone
       *    @post the measured moisture level for the zone is returned
       *   @param zoneID  The zone whose measured moisture level is returned
       *  @throws IllegalArgumentException if @pre is violated
       *  @throws DeviceFailureException if the sensor fails
       */

   public synchronized int getMeasuredMoistureLevel( String zoneID )
                       throws DeviceFailureException {

      Zone zone  = findZone( zoneID );
      int result = 0;

      try {
         result = zone.getMeasuredMoistureLevel();
      }
      catch ( DeviceFailureException e ) {
         handleDeviceFailure( e );
         throw e;
      }

      return result;
      
   } // getMeasuredMoistureLevel

      /**
       *  Fetch the water used so far in this irrigation cycle (if any).
       *
       *     @pre none
       *    @post the water used in the irrigation cycle, or 0 if there is
       *          no current irrigaiton cycle
       */

   public int getWaterUsed() {

      return (cycle == null) ? 0 : cycle.getWaterUsed();
   }

      /**
       *  Set the irrigation mode (AUTOMATIC or MANUAL). This may entail
       *  ending or starting an automatic irrigation cycle.
       *
       *     @pre none
       *    @post the mode is reset; if an automatic mode is in process and
       *          the mode is set to manual, the automatic mode is ended;
       *          if the mode is manual and is set to automatic, a new
       *          automatic irrigation cycle is begun.
       *   @param theMode  The new mode
       */

   public synchronized void setMode( Mode newMode ) {

         // do nothing if the mode is not changed
      if ( mode == newMode ) return;

         // end auto cycle if one is under way
      if ( (mode == Mode.AUTOMATIC) && (cycle != null) ) {
         try {
            cycle.end();
         }
         catch ( DeviceFailureException e ) {
            handleDeviceFailure( e );
         }
         finally {
            cycle = null;
            mode  = newMode;
         }
      }
      else mode = newMode;

         // try to record the new mode in persistent store
      storeData( MODE_TAG, mode.toString() );

   } // setMode

      /**
       *  Assign the set of irrigation days.
       *
       *     @pre none
       *    @post the irrigation days are reset
       *   @param newDays  The new set of irrigation days
       */

   public synchronized void setIrrigationDays( Set<Day> newDays ) {

      irrigationDays = new HashSet<Day>( newDays );

         // try to record the new days in persistent store
      StringBuffer dayBuffer = new StringBuffer();
      for ( Day day : irrigationDays )
         switch ( day.toInt() ) {
            case 0 : dayBuffer.append( "M" );  break;
            case 1 : dayBuffer.append( "Tu" ); break;
            case 2 : dayBuffer.append( "W" );  break;
            case 3 : dayBuffer.append( "Th" ); break;
            case 4 : dayBuffer.append( "F" );  break;
            case 5 : dayBuffer.append( "Sa" ); break;
            case 6 : dayBuffer.append( "Su" ); break;
         }

         // record the new irrigation days in persistent store
      storeData( IRRIGATION_DAYS_TAG, dayBuffer.toString() );

   } // setIrrigationDays

      /**
       *  Set the irrigation time.
       *
       *     @pre milTime is a valid military format time
       *    @post the time is changed or an exception is thrown
       *   @param milTime  A time inmilitary time format
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void setIrrigationTime( int milTime ) {

      if (    (milTime < 0)
           || (59 < (milTime % 100) )
           || (23 < (milTime / 100) ) )
         throw new IllegalArgumentException( "Bad military time." );

      irrigationTime = milTime;

         // record the new time in persistent store
      storeData( IRRIGATION_TIME_TAG, String.valueOf(milTime) );

   } // setIrrigationTime

      /**
       *  Set the irrigation water allocation. Note that if an automatic
       *  irrigation cycle is in progress, it is notified and the allocation
       *  used for the cycle is changed, with consequences for the remainder
       *  of the cycle.
       *
       *     @pre 0 <= theAllocation
       *    @post the allocation is set or an exception is thrown
       *   @param theAllocation  The irrigation allocation in gallons per cycle
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void setAllocation( int theAllocation ) {

         // check the precondition
      if ( theAllocation < 0 )
         throw new IllegalArgumentException( 
            "Negative allocation"+ theAllocation +"." );

         // change the allocation
      allocation = theAllocation;

         // change the allocation in any in-progress auto irrigation cycle
      if ( (mode == Mode.AUTOMATIC) && (cycle != null) )
         try {
            ((AutoCycle)cycle).setAllocation( allocation );
         }
         catch ( DeviceFailureException e ) {
            handleDeviceFailure( e );
         }

         // record the new allocation in persistent store
      storeData( ALLOCATION_TAG, String.valueOf(allocation) );

   } // setAllocation

      /**
       *  Set the critical moisture level for a zone.
       *
       *     @pre zoneID is valid and 0 <= theLevel <= 100
       *    @post the moisture level is changed or an exception is thrown
       *   @param   zoneID  A zone identifier
       *   @param theLevel  The new moisture level for the zone
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void setCriticalMoistureLevel( String zoneID,
                                                      int theLevel ) {

      findZone(zoneID).setCriticalMoistureLevel( theLevel );
      storeData( zoneID + CRITICAL_LEVEL_TAG, String.valueOf(theLevel) );

   } // setCriticalMoistureLevel
   
   
	   /**
	    *  Set the maximal moisture level for a zone.
	    *
	    *     @pre zoneID is valid and 0 <= theLevel <= 100
	    *    @post the maximal moisture level is changed or an exception is thrown
	    *   @param   zoneID  A zone identifier
	    *   @param theLevel  The new moisture level for the zone
	    *  @throws IllegalArgumentException if @pre is violated
	    */
	
	public synchronized void setMaximalMoistureLevel( String zoneID,
	                                                   int theLevel ) {
	
	   findZone(zoneID).setMaximalMoistureLevel( theLevel );
	   storeData( zoneID + MAXIMAL_LEVEL_TAG, String.valueOf(theLevel) );
	
	} // setMaximalMoistureLevel

      /**
       *  Create and start up a new manual irrigation cycle, provided a
       *  cycle is not already in progress.
       *
       *     @pre none
       *    @post a new cycle is created if one is not already in progress
       */

   public synchronized void startManualCycle() {

      if ( cycle ==null ) cycle = new ManualCycle( zones );
   }

      /**
       *  Stop a manual irrigation cycle.
       *
       *     @pre none
       *    @post if a cycle is in progress, end it and destroy it
       */

   public synchronized void stopManualCycle() {

         // do nothing if there is no cycle
      if ( cycle == null ) return;

         // tell the cycle to stop
      try {
         cycle.end();
      }
      catch ( DeviceFailureException e ) {
         handleDeviceFailure( e );
      }
      finally {
         cycle = null;
      }
     
   } // stopManualCycle

      /**
       *  Open a closed valve or close an open valve.
       *
       *     @pre zoneID and valveID must be valid
       *    @post if a valve is working, it is closed if open and opened if
       *          closed; a valve that fails is taken out of service
       *   @param zoneID   The zone where this valve is located
       *   @param valveID  The valve that is toggled
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void toggleValve( String zoneID, String valveID ) {

      try {
         findZone(zoneID).toggleValve( valveID );
      }
      catch ( DeviceFailureException e ) {
         handleDeviceFailure( e );
      }
   }

      /**
       *  Open all valves in a zone.
       *
       *     @pre zoneID must be valid
       *    @post all the working valves in the designated zone are opened;
       *          a valve that fails is taken out of service
       *   @param zoneID  The zone where these valves are located
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void openAllValves( String zoneID ) {

      try {
         findZone(zoneID).openAllValves();
      }
      catch ( DeviceFailureException e ) {
         handleDeviceFailure( e );
      }
   }

      /**
       *  Close all valves in a zone.
       *
       *     @pre zoneID must be valid
       *    @post all the working valves in the designated zone are closed;
       *          a valve that fails is taken out of service
       *   @param zoneID  The zone where these valves are located
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void closeAllValves( String zoneID ) {

      try {
         findZone(zoneID).closeAllValves();
      }
      catch ( DeviceFailureException e ) {
         handleDeviceFailure( e );
      }
   }

      /**
       *  Repair a failed device.
       *
       *     @pre deviceID must be valid
       *    @post the indicated device is marked as repaired, or an exception
       *          is thrown
       *   @param deviceID  The identifier for the repaired device
       *  @throws IllegalArgumentException if @pre is violated
       */

   public synchronized void repairDevice( String deviceID ) {

      for ( Zone z : zones )
         if ( z.repairDevice(deviceID) ) {
            failedDevices.remove( deviceID );
            recordDeviceIsOK( deviceID, "true" );
            return;
         }

         // if we get here, the deviceID was bad
      throw new IllegalArgumentException( "Bad device id "+ deviceID +"." );
   }

      /**
       *  Observer pattern update operation--may start or stop an automatic
       *  cycle, or notify any active irrigation cycles that one minute has
       *  passed.
       *
       *  Note that this operation requires synchronization along with a lot
       *  of other operations in this class. If an update occurs in the midst
       *  of a change being made in response to a user action, concurrent
       *  updates can occur that will corrupt the program state. Synchronizing
       *  operations prevents this problem.
       *
       *     @pre none
       *    @post if there is no active cycle but it is time to start an
       *          automatic cycle, it is started; any active irrigation 
       *          cycle's tick() operation is called; if an automatic cycle 
       *          is done, it is ended.
       *   @param o The subject instance that called this operation
       *   @param arg Optional data--not used
       */

   public synchronized void update( Observable o, Object arg ) {

      if ( cycle == null ) {  // no cycle in progress
         if ( (mode == mode.AUTOMATIC)  // see if it is time to start a cycle
              && (irrigationTime == clock.getTime())
              && (irrigationDays.contains(clock.getDay())) ) {

            cycle = new AutoCycle( zones, allocation );
            try {
               cycle.start();
            }
            catch ( DeviceFailureException e ) {
               handleDeviceFailure( e );
            }
         }
      }
      else { // notify the cycle that time has passed
         try {
            cycle.tick();
         }
         catch ( DeviceFailureException e ) {
            handleDeviceFailure( e );
         }
         finally {
            if ( cycle.isDone() ) {
               cycle = null;
            }
         }
      }

   } // update

      /* private methods
      /******************/

      /**
       *  Find a zone in the zone set.
       *
       *   @param zoneID  Unique zone identifier for this zone
       *  @throws IllegalArgumentException if @pre is violated
       */

   private Zone findZone( String zoneID ) {

         // make sure the zone is not null
      if ( zoneID == null )
         throw new IllegalArgumentException( "Null zone identifier. " );

         // find the zone
      for ( Zone z : zones )
         if ( zoneID.equals(z.getID()) ) return z;

         // if we get here, we did not find the zone
      throw new IllegalArgumentException( "Bad zone identifier "+ zoneID +"." );

   } // findZone

      /**
       *  Record some data in the persisten store. If the attempt fails, 
       *  note that the store has failed.
       *
       *   @param name   Tag used to identify the value
       *   @param value  String version of value associated with the name
       */

   private void storeData( String name, String value ) {

      try {
         store.setData( name, value );
      }
      catch ( StoreFailureException e ) {
         isStoreFailure = true;
      }

   } // storeData

      /**
       *  Record that one or more devices have failed in persistent store
       *  and in the list of so far unreported failed devices.
       *
       *   @param failures  FailureException with the set of failed device IDs
       */

   private void handleDeviceFailure( DeviceFailureException failures ) {

      Set<String> devices = failures.getDevices();  // newly failed devices

         // record these failures in persistent store
      for ( String deviceID : devices )
         recordDeviceIsOK( deviceID, "false" );

         // add the device IDs to the list of unreported failed devices
      failedDevices.addAll( devices );

   } // handleDeviceFailure

      /**
       *  Record the failure state of a device in peristent store. Also
       *  keep track of wehether persistent store is up-to-date with respect
       *  to this device.
       *
       *   @param deviceID  The unique ID of the device whose data is recorded
       *   @param isOK      True iff this device is not failed
       */

   private void recordDeviceIsOK( String deviceID, String isOK ) {
         try {
            store.setData( deviceID + DEVICE_OK_TAG, isOK );
            for ( Zone z : zones ) z.setIsRecorded( deviceID, true );
         }
         catch ( StoreFailureException e ) {
            isStoreFailure = true;
            for ( Zone z : zones ) z.setIsRecorded( deviceID, false );
         }

   } // recordDeviceFailureState

} // Irrigator
