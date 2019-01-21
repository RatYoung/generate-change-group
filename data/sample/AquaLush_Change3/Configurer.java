
package startup;

/**
 *  The Configurer creates all the objects in AquaLush (above the simulated
 *  or actual hardware) and hooks them all together using a DeviceFactory
 *  and a configuration specification. The configuration specification is
 *  provided by an input stream containing a description of the valves, 
 *  sensors, and irrigation zones. Virtual devices are created by the
 *  DeviceFactory and hooked up with the rest of the program.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.io.Reader;

import device.Clock;
import device.ClockDevice;
import device.DeviceFactory;
import device.DisplayDevice;
import device.KeypadDevice;
import device.ScreenButtonDevice;
import device.SensorDevice;
import device.StorageDevice;
import device.ValveDevice;
import irrigation.Irrigator;
import ui.UIController;

class Configurer {

      /* attributes
      /*************/

   private static final int MAX_DESCRIPTION = 24;  // largest allowed

   private final DeviceFactory factory;      // creates virtual devices
   private final Irrigator     irrigator;    // controls irrigation
   private final UIController  uiController; // manages the user interface

   private Tokenizer scan;         // get tokens from configuration
   private TokenType tokenType;    // shared by parsing routines

      /* constructors
      /***************/

   Configurer( DeviceFactory theFactory ) {

         // remember the device factory for later
      factory = theFactory;

         // hook up the stuff that does not depend on configuration
      Clock.setClockDevice( factory.createClockDevice() );

      KeypadDevice keypad             = factory.createKeypadDevice();
      DisplayDevice display           = factory.createDisplayDevice();
      ScreenButtonDevice screenButton = factory.createScreenButtonDevice();

      irrigator    = new Irrigator( factory.createStorageDevice() );
      uiController = new UIController( display, irrigator );

      keypad.setListener( uiController );
      screenButton.setListener( uiController );

         // attempt to restore the state of the irrigator, then
         // initialize the screens in the user interface
      try {
         irrigator.restoreState();
         uiController.initializeScreens();
      }
      catch ( StartupException e ) {
         uiController.initializeScreens();
         uiController.configurationFailure();
      }
   }

      /* methods
      /**********/

      /**
       *  Process a configuration specification and use it in conjunction
       *  with a device factory to tell the Irrigator about the physical
       *  structure of the irrigation site.
       *
       *  This operation creates ad primes a Tokenizer, and calls the start 
       *  operation of a recursive descent parser to parse the configuration
       *  specification.
       *
       *     @pre configuration is not null
       *    @post parses the configuration file and uses it to configure
       *          AquaLush, or throws an exception
       *   @param configuration  A reader providing a stream of characters 
       *                         specifying zones, sensors, and valves  
       *  @throws IllegalArgumentexception if @pre is violated
       */

   void configure( Reader configuration ) {

         // check the precondition
      if ( configuration == null )
         throw new IllegalArgumentException( "No configuration file reader." );

         // initialize and start the Tokenizer
      scan      = new Tokenizer( configuration );
      tokenType = scan.nextToken();

         // call the top operation in the recursive descent parser;
         // if configuration fails, tell the ui controller
      try {
         configFile();
      }
      catch ( StartupException e ) {
         uiController.configurationFailure();
      }
   }

      /**
       *  Return the Irrigator object.
       *
       *     @pre none
       *    @post @return is the newly created and configured irrigation
       *          object
       */

   Irrigator getIrrigator() { return irrigator; }

      /* private methods
      /******************/

      /**
       *  Parse the configuration file specification. This operation 
       *  implements the following grammar specification:
       *
       *   configFile = 1:32{ zoneSpec }
       */

   private void configFile() throws StartupException {
      int zoneCount = 0;

      if ( tokenType == TokenType.EOF ) parseError( "Empty config file" );

      while ( (zoneCount < 32) && (tokenType != TokenType.EOF) ) {
         zoneCount++;
         zoneSpec();
      }

      if ( zoneCount == 32 )
         parseError( "Too many zoneSpecs or garbage at end of config file" );

   } // configFile

      /**
       *  Parse zone specification. This operation implements the following
       *  grammar specification:
       *
       *   zoneSpec = "zone" + zoneIdentifier + location + "{" + zoneBody + "}"
       */

   private void zoneSpec() throws StartupException {

      if ( tokenType == TokenType.ZONE_KWD ) {
         tokenType = scan.nextToken();
         if ( tokenType == TokenType.ZONE_ID ) {
            String zoneID = scan.getToken();
            tokenType = scan.nextToken();
            if ( tokenType == TokenType.DESCRIPTION ) {
               String zoneLocation = trim( scan.getToken() );
               tokenType = scan.nextToken();
               irrigator.addZone( zoneID, zoneLocation );
               if ( tokenType == TokenType.LEFT_BRACE ) {
                  tokenType = scan.nextToken();
                  zoneBody( zoneID );
                  if ( tokenType == TokenType.RIGHT_BRACE )
                     tokenType = scan.nextToken();
                  else parseError( "Missing right brace in zoneSpec" );
               }
               else parseError( "Missing left brace in zoneSpec" );
            }
            else parseError( "Missing description in zoneSpec" );
         }
         else parseError( "Missing zone identifier in zoneSpec" );
      }
      else parseError( "Missing zone keyword in zoneSpec" );

   } // zoneSpec

      /**
       *  Parse a zone body. This operation implements the following
       *  grammar specification:
       *
       *   zoneBody = sensorSpec + 1:32{ valveSpec }
       *
       *  @param zoneID The irrigation zone whose contents are specified by
       *                this zone body--the contents are created and added
       *                during parsing.
       */

   private void zoneBody( String zoneID ) throws StartupException {

      if ( tokenType == TokenType.SENSOR_KWD ) {
         sensorSpec( zoneID );
         if ( tokenType == TokenType.VALVE_KWD ) {
            int valveCount = 0;
            while ( (valveCount < 32) && (tokenType == TokenType.VALVE_KWD ) ) {
               valveCount++;
               valveSpec( zoneID );
            }
            if ( valveCount == 32 ) parseError( "Too many valveSpecs" );
         }
         else
            parseError( "Missing valveSpec" );
      }
      else parseError( "Missing sensorSpec" );

   } // zoneBody

      /**
       *  Parse a sensor specification. This operation implements the
       *  following grammar specfication:
       *
       *   sensorSpec = "sensor" + sensorIdentifier + location + ";"
       *
       *  @param zoneID The irrigation zone whose contents are specified by
       *                this zone body--the contents are created and added
       *                during parsing.
       */

   private void sensorSpec( String zoneID ) throws StartupException {

      if ( tokenType == TokenType.SENSOR_KWD ) {
         tokenType = scan.nextToken();
         if ( tokenType == TokenType.SENSOR_ID ) {
            String sensorID = scan.getToken();
            SensorDevice sensorDevice = null;
            try {
               sensorDevice = factory.createSensorDevice( sensorID );
            }
            catch ( IllegalArgumentException e ) {
               parseError( "Bad sensor identifier "+ sensorID );
            }
            tokenType = scan.nextToken();
            if ( tokenType == TokenType.DESCRIPTION ) {
               String sensorLocation = trim( scan.getToken() );
                irrigator.addSensor( zoneID, sensorID, 
                                     sensorDevice, sensorLocation );
               tokenType = scan.nextToken();
               if ( tokenType == TokenType.SEMICOLON )
                  tokenType = scan.nextToken();
               else
                  parseError( "Missing semicolon terminating sensorSpec" );
            }
            else
               parseError( "Missing sensor location in sensorSpec" );
         }
         else
            parseError( "Missing sensor identifier in sensorSpec" );
      }
      else
         parseError( "Missing keyword sensor in sensorSpec" );

   } // sensorSpec

      /**
       *  Parse a valve specification. This operation implements the
       *  following grammar specification:
       *
       *   valveSpec = "valve" + valveIdentifier + valveType 
                               + flowRate + location + ";"
       *
       *  @param zoneID The irrigation zone whose contents are specified by
       *                this zone body--the contents are created and added
       *                during parsing.
       */

   private void valveSpec( String zoneID ) throws StartupException {

      if ( tokenType == TokenType.VALVE_KWD ) {
         tokenType = scan.nextToken();
         if ( tokenType == TokenType.VALVE_ID ) {
            String valveID = scan.getToken();
            ValveDevice valveDevice = null;
            try {
               valveDevice = factory.createValveDevice( valveID );
            }
            catch ( IllegalArgumentException e ) {
               parseError( "Bad valve identifier "+ valveID );
            }
            tokenType = scan.nextToken();
            if ( tokenType == TokenType.DESCRIPTION ) {
               String valveType = trim( scan.getToken() );
               tokenType = scan.nextToken();
               if ( tokenType == TokenType.NUMBER ) {
                  int flowRate = 1;
                  try {
                     flowRate = Integer.parseInt( scan.getToken() );
                  }
                  catch ( NumberFormatException e ) {
                     parseError( "Bad flow rate in valveSpec" );
                  }
                  tokenType = scan.nextToken();
                  if ( tokenType == TokenType.DESCRIPTION ) {
                     String valveLocation = trim( scan.getToken() );
                     irrigator.addValve( zoneID, valveID, valveDevice,
                                         valveType, flowRate, valveLocation );
                     tokenType = scan.nextToken();
                     if ( tokenType == TokenType.SEMICOLON )
                        tokenType = scan.nextToken();
                     else
                        parseError( "Missing semicolon terminating valvSpec" );
                  }
                  else
                     parseError( "Missing description in valveSpec" );
               }
               else
                  parseError( "Missing flowrate in valveSpec" );
            }
            else
               parseError( "Missing valve typ in valveSpec" );
         }
         else
            parseError( "Missing valve identifier in valveSpec" );
      }
      else
         parseError( "Missing keyword valve in valveSpec" );

   } // valveSpec

      /**
       *  Print an error message to stderr and throw a StartupException.
       *  The error message states that there is a configuration parse error 
       *  and the particular type of error, and also the token found at the 
       *  point of error.
       *
       *  @param description The particular error encountered.
       */

   private void parseError( String description ) throws StartupException {
      System.err.println( "Configuration Parse Error: "+ description +"." );
      System.err.println( "\tFound token: <<"+ scan.getToken() +">>" );
      throw new StartupException();
   }

      /**
       *  Remove the delimiting angle brackets from a description token,
       *  and truncate it to MAX_DESCRIPTION chracters if required.
       *
       *  @param description The text of a description token
       */

   private String trim( String description ) {
      return description.substring( 1, Math.min( description.length()-1,
                                                 MAX_DESCRIPTION+1 ) );
   }

} // Configurer
