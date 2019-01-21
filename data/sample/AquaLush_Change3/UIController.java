
package ui;

/**
 *  The UIController oversees user interaction through the AquaLush control
 *  panel by running a state machine whose states are screens.
 *
 *  Note that the public operations are synchronized so that user input
 *  operations that change the display do not conflict with clock updates
 *  that may also change the display.
 *
 *   @author C. Fox
 *  @version 07/08
 */

import java.util.Observer;
import java.util.Observable;

import device.Clock;
import device.DisplayDevice;
import device.KeypadListener;
import device.KeyPress;
import device.ScreenButtonListener;
import irrigation.Irrigator;
import irrigation.Mode;
import util.Command;
import util.Day;

public class UIController implements KeypadListener,
                                     ScreenButtonListener,
                                     Observer {

      /* attributes
      /*************/

   private final DisplayDevice display;     // where to display all output
   private final Irrigator     irrigator;   // controls irrigation
   private final Clock         clock;       // to ask for the current time

   private final Screen LAST_SCRN;      // lastScrn placeholder in tables
   private final Screen LAST_MAIN_SCRN; // lastMainScrn placeholder in tables

   private Screen crntScrn;           // current screen in state machine
   private Screen lastScrn;           // previous screen when failure displayed
   private Screen lastMainScrn;       // previous main screen during execution

   private Screen autoMainScrn;       // auto irrigation main menu
   private Screen manualMainScrn;     // manual irrigation main menu
   private Screen setClockScrn;       // set main clock
   private Screen setUpAutoScrn;      // set up automatic irrigation menu
   private Screen setAllocationScrn;  // set water allocation
   private Screen setTimesScrn;       // set automatic irrigation times
   private Screen deviceFailureScrn;  // notify that a device failed
   private Screen storeFailureScrn;   // notify that persistent store failed
   private Screen startFailureScrn;   // notify that AquaLush cannot start

   private ScrollingScreen manualDataScrn;     // manual irrigation data display
   private ScrollingScreen manualLocationScrn; // manual device location display
   private ScrollingScreen fixFailuresScrn;    // fix failed devices
   private ScrollingScreen setLevelScrn;       // set zone moisture levels

      /* constructors
      /***************/

      /**
       *  Define all attributes, in particular all the screens in the user
       *  interface.
       *
       *   @param theDisplay    Virtual device where all output goes
       *   @param theIrrigator  The facade to the irrigation control layer
       */

   public UIController( DisplayDevice theDisplay, Irrigator theIrrigator ) {

         // initialize basic attributes
      display   = theDisplay;
      irrigator = theIrrigator;
      clock     = Clock.instance();

         // register as a listener with the clock
      clock.addObserver( this );

         // create all screens in the user interface
      LAST_SCRN          = new Screen( display );
      LAST_MAIN_SCRN     = new Screen( display );
      autoMainScrn       = new Screen( display );
      manualMainScrn     = new Screen( display );
      setClockScrn       = new Screen( display );
      setUpAutoScrn      = new Screen( display );
      setAllocationScrn  = new Screen( display );
      setTimesScrn       = new Screen( display );
      deviceFailureScrn  = new Screen( display );
      storeFailureScrn   = new Screen( display );
      startFailureScrn   = new Screen( display );

      manualDataScrn     = new ScrollingScreen( display );
      manualLocationScrn = new ScrollingScreen( display );
      fixFailuresScrn    = new ScrollingScreen( display );
      setLevelScrn       = new ScrollingScreen( display );
   }

      /* methods
      /**********/

      /**
       *  Initializes the entire user interface by building all screens,
       *  their transisions to one another (in a state machine), and the
       *  operations that get executed when they are active.
       *
       *    Note: Several states return to a history state. This is true of 
       *          the states that return to one of the main screens, and of 
       *          the failure handling screens. This behavior is handled by 
       *          putting dummy screens (called LAST_SCRN and LAST_MAIN_SCRN)
       *          into the Screen transition tables and the escapte target
       *          screen, and keeping track of the previous screen here in 
       *          the UIController (in lastScrn and lastMainScrn).
       *          When there is a transition to one of the dummy previous 
       *          screens, then the the crntScrn becomes the appropriate 
       *          last screen. 
       *
       *     @pre The screens and irrigator are defined
       *    @post the UI state machine is built, all actions are defined, and
       *          the machine started in its normal initial state.
       */

   public void initializeScreens() {

         // initialize all screens in the user interface
      buildAutoMainScrn();
      buildManualMainScrn();
      buildManualDataScrn();
      buildManualLocationScrn();
      buildFixFailuresScrn();
      buildSetClockScrn();
      buildSetUpAutoScrn();
      buildSetAllocationScrn();
      buildSetTimesScrn();
      buildSetLevelScrn();
      buildDeviceFailureScrn();
      buildStoreFailureScrn();
      buildStartFailureScrn();

         // start the finite state machine at the automatic main screen
      lastScrn = lastMainScrn = crntScrn = autoMainScrn;
      crntScrn.activate();
   }

      /**
       *  Arrange for AquaLush to abort because of a startup failure.
       *  This routine resets the initial screen for machine startup to
       *  the startFailureScrn dead state.
       *
       *     @pre none
       *    @post the current screen is the start failure screen
       */

   public void configurationFailure() {
      lastScrn = lastMainScrn = crntScrn = startFailureScrn;
      crntScrn.activate();
   }

      /**
       *  Handle keypad input from the user. Each keypad key press is passed
       *  to the currently active screen, which does the appropriate thing.
       *  In the case of the ESC key, a keypress may change to a different
       *  screen. This is indicated by the return value of the keyPress
       *  operation.
       *
       *     @pre none
       *    @post crntScrn.keyPress() is called
       *   @param key  A keypad key press from the user
       */

   public synchronized void keyPress( KeyPress key ) {

         // remember the current main screen for return transitions
      if ( (crntScrn == autoMainScrn) || (crntScrn == manualMainScrn) )
         lastMainScrn = crntScrn;

         // current screen processes the key press and indicates transition
      Screen nextScrn = crntScrn.keyPress( key );

         // activate the next screen, if different from the current screen
      if ( nextScrn != null ) {
         if      ( nextScrn == LAST_SCRN )      crntScrn = lastScrn;
         else if ( nextScrn == LAST_MAIN_SCRN ) crntScrn = lastMainScrn;
         else                                   crntScrn = nextScrn;
         crntScrn.activate();
      }
   }

      /**
       *  Handle screen button input from the user. The screen button number
       *  is passed to the currently active screen. The active screen takes
       *  any associated actions and returns the next screen to transition to,
       *  or null if the current screen is to remain current.
       *
       *  If a new screen becomes current, it is activated.
       *
       *     @pre 0 <= whichBrn < DisplayScreen.NUM_SCREEN_BUTTONS
       *    @post 
       *   @param key  A key press from the user. The key press drives
       *               (a) processing within a screen and (b) transition
       *               between screens.
       */

   public synchronized void screenButtonPress( int whichBtn ) {

         // remember the current main screen for return transitions
      if ( (crntScrn == autoMainScrn) || (crntScrn == manualMainScrn) )
         lastMainScrn = crntScrn;

         // current screen processes the button press and indicates transition
      Screen nextScrn = crntScrn.screenButtonPress( whichBtn );

         // activate the next screen, if different from the current screen
      if ( nextScrn != null ) {
         if      ( nextScrn == LAST_SCRN )      crntScrn = lastScrn;
         else if ( nextScrn == LAST_MAIN_SCRN ) crntScrn = lastMainScrn;
         else                                   crntScrn = nextScrn;
         crntScrn.activate();
      }
   }

      /**
       *  Observer pattern update operation--the current screen is altered
       *  if it shows the current day and time.
       *
       *     @pre none
       *    @post the altered day and time are posted
       *   @param o    The subject instance that called this operation
       *   @param arg  Optional data--not used
       */

   public synchronized void update( Observable o, Object arg ) {

         // update the current screen
      crntScrn.tick();

         // in the case of a device or persistent store failure, jump to 
         // the store or device failure screen, much as one would handle 
         // an exception (with return)
      if (irrigator.isStoreFailure() && (crntScrn != storeFailureScrn)) {
         lastScrn = crntScrn;
         crntScrn = storeFailureScrn;
         crntScrn.activate();
      }
      else if (irrigator.isFailedDevice() && (crntScrn != deviceFailureScrn)) {
         lastScrn = crntScrn;
         crntScrn = deviceFailureScrn;
         crntScrn.activate();
      }

   } // update

      /* methods
      /**********/

      /**
       *  Add prompts, transitions, and commands to the automatic irrigation
       *  mode main screen.
       */

   private void buildAutoMainScrn() {

         // state the prompts
      autoMainScrn.addPrompt( new Prompt("Automatic Mode",     4,13) );
      autoMainScrn.addPrompt( new Prompt("<-Set the Clock",    8, 0) );
      autoMainScrn.addPrompt( new Prompt("<-Set Up Automatic",11, 0) );
      autoMainScrn.addPrompt( new Prompt("  Irrigation",      12, 0) );
      autoMainScrn.addPrompt( new Prompt("Change Mode->",      8,27) );
      autoMainScrn.addPrompt( new Prompt("Fix Failures->",    11,26) );

         // enter state machine transitions to other screens
      autoMainScrn.setTransition( 2, setClockScrn );
      autoMainScrn.setTransition( 3, manualMainScrn );
      autoMainScrn.setTransition( 4, setUpAutoScrn );
      autoMainScrn.setTransition( 5, fixFailuresScrn );

         // create a screen state object
      autoMainScrn.setScreenState( new MainScrnState(irrigator) );

         // display the current day, time and device
         // states on activation and when the clock ticks
      EventAction doMainDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 MainScrnState state = (MainScrnState)scrn.getScreenState();

                    // write the current day and time
                 display.writeLine( 1, state.getDayString() );
                 display.writeLine( 2, state.getTimeString() );

                    // write the device status summary
                 state.updateFailures();
                 display.writeLine( 5, state.getFailedValveCount() );
                 display.writeLine( 6, state.getFailedSensorCount() );
              }
           }; // doMainDisplay

      autoMainScrn.setEntryAction( doMainDisplay );
      autoMainScrn.setTickAction( doMainDisplay );

         // change the mode when screen button 3 is pressed
      EventAction doSetMode 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 irrigator.setMode( Mode.MANUAL );
              }
           }; // doSetMode

      autoMainScrn.setButtonPressAction( 3, doSetMode );

   } // buildAutoMainScrn

      /**
       *  Add prompts, transitions, and commands to the manual irrigation
       *  mode main screen.
       */

   private void buildManualMainScrn() {

         // state the prompts
      manualMainScrn.addPrompt( new Prompt("Manual Mode",        4,15) );
      manualMainScrn.addPrompt( new Prompt("<-Set the Clock",    8, 0) );
      manualMainScrn.addPrompt( new Prompt("<-Set Up Automatic",11, 0) );
      manualMainScrn.addPrompt( new Prompt("  Irrigation",      12, 0) );
      manualMainScrn.addPrompt( new Prompt("Change Mode->",      8,27) );
      manualMainScrn.addPrompt( new Prompt("Fix Failures->",    11,26) );
      manualMainScrn.addPrompt( new Prompt("Control->",         14,31) );
      manualMainScrn.addPrompt( new Prompt("Irrigation",        15,28) );

         // enter state machine transitions to other screens
      manualMainScrn.setTransition( 2, setClockScrn );
      manualMainScrn.setTransition( 3, autoMainScrn );
      manualMainScrn.setTransition( 4, setUpAutoScrn );
      manualMainScrn.setTransition( 5, fixFailuresScrn );
      manualMainScrn.setTransition( 7, manualDataScrn );

         // create a screen state object
      manualMainScrn.setScreenState( new MainScrnState(irrigator) );

         // display the current day, time and device
         // states on activation and when the clock ticks
      EventAction doMainDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 MainScrnState state = (MainScrnState)scrn.getScreenState();

                    // write the current day and time
                 display.writeLine( 1, state.getDayString() );
                 display.writeLine( 2, state.getTimeString() ); 

                    // write the device status summary
                 state.updateFailures();
                 display.writeLine( 5, state.getFailedValveCount() );
                 display.writeLine( 6, state.getFailedSensorCount() );
              }
           }; // doMainDisplay

      manualMainScrn.setEntryAction( doMainDisplay );
      manualMainScrn.setTickAction( doMainDisplay );

         // change the mode when screen button 3 is pressed
      EventAction doSetMode 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 irrigator.setMode( Mode.AUTOMATIC );
              }
           }; // doSetMode

      manualMainScrn.setButtonPressAction( 3, doSetMode );

   } // buildManualMainScrn

      /**
       *  Add prompts, transitions, and commands to the manual irrigation
       *  control screen that displays irrigation data.
       */

   private void buildManualDataScrn() {

         // state the prompts
      manualDataScrn.addPrompt( new Prompt(
                            "Valve Zone Wet% Time    Water Used", 0, 3) );
      manualDataScrn.addPrompt( new Prompt(
                            "----------------------------------", 1, 3) );
      manualDataScrn.addPrompt( new Prompt(
                            "----------------------------------", 5, 3) );
      manualDataScrn.addPrompt( new Prompt("<-Open/Close Valve",  8, 0) );
      manualDataScrn.addPrompt( new Prompt("<-Propagate to Zone",11, 0) );
      manualDataScrn.addPrompt( new Prompt("<-Show Locations",   14, 0) );
      manualDataScrn.addPrompt( new Prompt("Scroll Up->",         8,29) );
      manualDataScrn.addPrompt( new Prompt("Scroll Down->",      11,27) );
      manualDataScrn.addPrompt( new Prompt("Finished->",         14,30) );

         // enter state machine transitions to other screens
      manualDataScrn.setTransition( 6, manualLocationScrn );
      manualDataScrn.setTransition( 7, manualMainScrn );
      manualDataScrn.setEscapeTarget( manualMainScrn );

         // create a screen state object
      manualDataScrn.setScreenState(new ManualControlScrnState(irrigator,true));

         // start a new manual irrigation cycle on entry
      EventAction doStartCycle 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.startCycle();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doStartCycle

      manualDataScrn.setEntryAction( doStartCycle );

         // update the display every minute to show water usage
      EventAction doUpdateDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.updateData();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doUpdateDisplay

      manualDataScrn.setTickAction( doUpdateDisplay );

         // toggle the current valve open or closed
      EventAction doToggleValve 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.toggleCurrentValve();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doToggleValve

      manualDataScrn.setButtonPressAction( 2, doToggleValve );

         // propagate the current valve's state to all the valves in the zone
      EventAction doPropagateValve 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.propagateCurrentValve();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doToggleValve

      manualDataScrn.setButtonPressAction( 4, doPropagateValve );

         // stop the irrigation cycle
      EventAction doStopCycle 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.stopCycle();
              }
           }; // doStopCycle

      manualDataScrn.setButtonPressAction( 7, doStopCycle );

         // also stop the cycle if ESC is pressed
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
              }
           }; // doKeyPress

      manualDataScrn.setKeyPressAction( doKeyPress );

   } // buildManualDataScrn

      /**
       *  Add prompts, transitions, and commands to the manual irrigation
       *  control screen that displays device locations.
       */

   private void buildManualLocationScrn() {

         // state the prompts
      manualLocationScrn.addPrompt( new Prompt(
                                "Valve Zone Location",                0, 3) );
      manualLocationScrn.addPrompt( new Prompt(
                                "----------------------------------", 1, 3) );
      manualLocationScrn.addPrompt( new Prompt(
                                "----------------------------------", 5, 3) );
      manualLocationScrn.addPrompt( new Prompt("<-Open/Close Valve",  8, 0) );
      manualLocationScrn.addPrompt( new Prompt("<-Propagate to Zone",11, 0) );
      manualLocationScrn.addPrompt( new Prompt("<-Show Data",        14, 0) );
      manualLocationScrn.addPrompt( new Prompt("Scroll Up->",         8,29) );
      manualLocationScrn.addPrompt( new Prompt("Scroll Down->",      11,27) );
      manualLocationScrn.addPrompt( new Prompt("Finished->",         14,30) );

         // enter state machine transitions to other screens
      manualLocationScrn.setTransition( 6, manualDataScrn );
      manualLocationScrn.setTransition( 7, manualMainScrn );
      manualLocationScrn.setEscapeTarget( manualMainScrn );

         // create a screen state object
      manualLocationScrn.setScreenState(
                      new ManualControlScrnState(irrigator,false) );

         // display the current manual cycle location data
         // on entry and every minute to show changes
      EventAction doUpdateDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.updateData();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doUpdateDisplay

      manualLocationScrn.setEntryAction( doUpdateDisplay );
      manualLocationScrn.setTickAction( doUpdateDisplay );

         // toggle the current valve open or closed
      EventAction doToggleValve 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.toggleCurrentValve();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doToggleValve

      manualLocationScrn.setButtonPressAction( 2, doToggleValve );

         // propagate the current valve's state to all the valves in the zone
      EventAction doPropagateValve 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.propagateCurrentValve();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getGallonsUsedString() );
              }
           }; // doToggleValve

      manualLocationScrn.setButtonPressAction( 4, doPropagateValve );

         // stop the irrigation cycle
      EventAction doStopCycle 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state 
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.stopCycle();
              }
           }; // doStopCycle

      manualLocationScrn.setButtonPressAction( 7, doStopCycle );

         // also stop the cycle if ESC is pressed
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 ManualControlScrnState state
                    = (ManualControlScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
              }
           }; // doKeyPress

      manualLocationScrn.setKeyPressAction( doKeyPress );

   } // buildManualLocationScrn

      /**
       *  Add prompts, transitions, and commands to the screen where
       *  failed devices are displayed and AquaLush is informed they
       *  are fixed.
       */

   private void buildFixFailuresScrn() {

         // state the prompts
      fixFailuresScrn.addPrompt( new Prompt(
                       "Device Zone Location",                 0, 2) );
      fixFailuresScrn.addPrompt( new Prompt(
                       "------------------------------------", 1, 2) );
      fixFailuresScrn.addPrompt( new Prompt(
                       "------------------------------------", 5, 2) );
      fixFailuresScrn.addPrompt( new Prompt("<-Repaired",      8, 0) );
      fixFailuresScrn.addPrompt( new Prompt("Scroll Up->",     8,29) );
      fixFailuresScrn.addPrompt( new Prompt("Scroll Down->",  11,27) );
      fixFailuresScrn.addPrompt( new Prompt("Finished->",     14,30) );

         // enter state machine transitions to other screens
      fixFailuresScrn.setTransition( 7, LAST_MAIN_SCRN );
      fixFailuresScrn.setEscapeTarget( LAST_MAIN_SCRN );

         // create a screen state object
      fixFailuresScrn.setScreenState( new FixFailuresScrnState(irrigator) );

         // display data on entry
      EventAction doDisplayFailures 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 FixFailuresScrnState state 
                    = (FixFailuresScrnState)scrn.getScreenState();
                 state.displayData();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getSummaryString() );
              }
           }; // doDisplayFailures

      fixFailuresScrn.setEntryAction( doDisplayFailures );

         // update the data every minute
      EventAction doUpdateFailures 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 FixFailuresScrnState state 
                    = (FixFailuresScrnState)scrn.getScreenState();
                 state.updateData();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getSummaryString() );
              }
           }; // doUpdateFailures

      fixFailuresScrn.setTickAction( doUpdateFailures );

         // mark the current device as repaired and remove it
      EventAction doRepairDevice 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 FixFailuresScrnState state 
                    = (FixFailuresScrnState)scrn.getScreenState();
                 state.markCurrentDevice();
                 state.updateData();
                 ((ScrollingScreen)scrn).displayScrolledItems();
                 display.writeLine( 6, state.getSummaryString() );
              }
           }; // doRepairDevice

      fixFailuresScrn.setButtonPressAction( 2, doRepairDevice );

         // tell the irrigator about the repaired devices
      EventAction doRecordRepairs
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 FixFailuresScrnState state 
                    = (FixFailuresScrnState)scrn.getScreenState();
                 state.recordRepairs();
              }
           }; // doRecordRepairs

      fixFailuresScrn.setButtonPressAction( 7, doRecordRepairs );

   } // buildFixFailuresScrn

      /**
       *  Add prompts, transitions, and commands to the screen where
       *  the current time and day are set.
       */

   private void buildSetClockScrn() {

         // state the prompts
      setClockScrn.addPrompt( new Prompt(
                         "Buttons change day; keys change time.", 3, 2) );
      setClockScrn.addPrompt( new Prompt("<-Monday",              5, 0) );
      setClockScrn.addPrompt( new Prompt("<-Tuesday",             8, 0) );
      setClockScrn.addPrompt( new Prompt("<-Wednesday",          11, 0) );
      setClockScrn.addPrompt( new Prompt("<-Thursday",           14, 0) );
      setClockScrn.addPrompt( new Prompt("Friday->",              5,32) );
      setClockScrn.addPrompt( new Prompt("Saturday->",            8,30) );
      setClockScrn.addPrompt( new Prompt("Sunday->",             11,32) );
      setClockScrn.addPrompt( new Prompt("Accept New Settings->",14,19) );

         // enter state machine transitions to other screens
      setClockScrn.setTransition( 7, LAST_MAIN_SCRN );
      setClockScrn.setEscapeTarget( LAST_MAIN_SCRN );

         // create a screen state object
      setClockScrn.setScreenState( new SetClockScrnState() );

         // display the current day/time on activation and when the clock ticks
      EventAction doClockDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetClockScrnState state
                    = (SetClockScrnState)scrn.getScreenState();
                 display.writeLine( 0, state.getDayString() );
                 display.writeLine( 1, state.getTimeString() );
              }
           }; // doMainDisplay

      setClockScrn.setEntryAction( doClockDisplay );
      setClockScrn.setTickAction( doClockDisplay );

         // change the day when there is an appropriate button press
      EventAction doSetDay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetClockScrnState state
                    = (SetClockScrnState)scrn.getScreenState();
                 state.setDay( (Integer)arg );
                 display.writeLine( 0, state.getDayString() );
              }
           }; // doSetDay

      setClockScrn.setButtonPressAction( 0, doSetDay );
      setClockScrn.setButtonPressAction( 1, doSetDay );
      setClockScrn.setButtonPressAction( 2, doSetDay );
      setClockScrn.setButtonPressAction( 3, doSetDay );
      setClockScrn.setButtonPressAction( 4, doSetDay );
      setClockScrn.setButtonPressAction( 5, doSetDay );
      setClockScrn.setButtonPressAction( 6, doSetDay );

         // accept the new settings
      EventAction doAccept 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetClockScrnState state
                    = (SetClockScrnState)scrn.getScreenState();
                 state.acceptSettings( (Integer)arg );
              }
           }; // doAccept

      setClockScrn.setButtonPressAction( 7, doAccept );

         // arrange to accept key presses
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetClockScrnState state
                    = (SetClockScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
                 display.writeLine( 0, state.getDayString() );
                 display.writeLine( 1, state.getTimeString() );
              }
           }; // doKeyPress

      setClockScrn.setKeyPressAction( doKeyPress );

   } // buildSetClockScrn

      /**
       *  Add prompts, transitions, and commands to the menu screen where
       *  users select which automatic irrigation parameter to see/set.
       */

   private void buildSetUpAutoScrn() {

         // state the prompts
      setUpAutoScrn.addPrompt( new Prompt(
                                "Select one of the actions below.", 2, 4) );
      setUpAutoScrn.addPrompt( new Prompt("Set Water Allocation->", 5,18) );
      setUpAutoScrn.addPrompt( new Prompt("Set Irrigation Times->", 8,18) );
      setUpAutoScrn.addPrompt( new Prompt(
                                  "Set Critical Moisture Levels->",11,10) );
      setUpAutoScrn.addPrompt( new Prompt("Finished->",            14,30) );

         // enter state machine transitions to other screens
      setUpAutoScrn.setTransition( 1, setAllocationScrn );
      setUpAutoScrn.setTransition( 3, setTimesScrn );
      setUpAutoScrn.setTransition( 5, setLevelScrn );
      setUpAutoScrn.setTransition( 7, LAST_MAIN_SCRN );
      setUpAutoScrn.setEscapeTarget( LAST_MAIN_SCRN );

   } // buildSetUpAutoScrn

      /**
       *  Add prompts, transitions, and commands to the screen where the
       *  water allocation for automatic irrigation is displayed and set.
       */

   private void buildSetAllocationScrn() {

         // state the prompts
      setAllocationScrn.addPrompt( new Prompt(
                                       "Press DEL and digit keys to",  5, 6) );
      setAllocationScrn.addPrompt( new Prompt(
                                       "change the water allocation.", 6, 6) );
      setAllocationScrn.addPrompt( new Prompt("Accept New Settings->",14,19) );

         // enter state machine transitions to other screens
      setAllocationScrn.setTransition( 7, setUpAutoScrn );
      setAllocationScrn.setEscapeTarget( setUpAutoScrn );

         // create a screen state object
      setAllocationScrn.setScreenState( new SetAllocationScrnState(irrigator) );

         // display the allocation on activation
      EventAction doAllocationDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetAllocationScrnState state
                    = (SetAllocationScrnState)scrn.getScreenState();
                 display.writeLine( 2, state.getAllocationString() );
              }
           }; // doAllocationDisplay

      setAllocationScrn.setEntryAction( doAllocationDisplay );

         // accept the new settings
      EventAction doAccept 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetAllocationScrnState state
                    = (SetAllocationScrnState)scrn.getScreenState();
                 state.acceptSettings( (Integer)arg );
              }
           }; // doAccept

      setAllocationScrn.setButtonPressAction( 7, doAccept );

         // arrange to accept key presses
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetAllocationScrnState state
                    = (SetAllocationScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
                 display.writeLine( 2, state.getAllocationString() );
              }
           }; // doKeyPress

      setAllocationScrn.setKeyPressAction( doKeyPress );

   } // buildSetAllocationScrn

      /**
       *  Add prompts, transitions, and commands to the screen where
       *  the irrigation time and days are displayed and set.
       */

   private void buildSetTimesScrn() {

         // state the prompts
      setTimesScrn.addPrompt( new Prompt(
                         "Buttons change day; keys change time.", 3, 2) );
      setTimesScrn.addPrompt( new Prompt("<-Monday",              5, 0) );
      setTimesScrn.addPrompt( new Prompt("<-Tuesday",             8, 0) );
      setTimesScrn.addPrompt( new Prompt("<-Wednesday",          11, 0) );
      setTimesScrn.addPrompt( new Prompt("<-Thursday",           14, 0) );
      setTimesScrn.addPrompt( new Prompt("Friday->",              5,32) );
      setTimesScrn.addPrompt( new Prompt("Saturday->",            8,30) );
      setTimesScrn.addPrompt( new Prompt("Sunday->",             11,32) );
      setTimesScrn.addPrompt( new Prompt("Accept New Settings->",14,19) );

         // enter state machine transitions to other screens
      setTimesScrn.setTransition( 7, setUpAutoScrn );
      setTimesScrn.setEscapeTarget( setUpAutoScrn );

         // create a screen state object
      setTimesScrn.setScreenState( new SetTimesScrnState(irrigator) );

         // display the irrigation time and days on activation
      EventAction doIrrigationTimeDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetTimesScrnState state
                    = (SetTimesScrnState)scrn.getScreenState();
                 display.writeLine( 0, state.getTimeString() );
                 display.writeLine( 1, state.getDaysString() );
              }
           }; // doIrrigationTimeDisplay

      setTimesScrn.setEntryAction( doIrrigationTimeDisplay );

         // toggle the days when there is an appropriate button press
      EventAction doToggleDays 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetTimesScrnState state
                    = (SetTimesScrnState)scrn.getScreenState();
                 state.toggleDay( (Integer)arg );
                 display.writeLine( 1, state.getDaysString() );
              }
           }; // doSetDay

      setTimesScrn.setButtonPressAction( 0, doToggleDays );
      setTimesScrn.setButtonPressAction( 1, doToggleDays );
      setTimesScrn.setButtonPressAction( 2, doToggleDays );
      setTimesScrn.setButtonPressAction( 3, doToggleDays );
      setTimesScrn.setButtonPressAction( 4, doToggleDays );
      setTimesScrn.setButtonPressAction( 5, doToggleDays );
      setTimesScrn.setButtonPressAction( 6, doToggleDays );

         // accept the new settings
      EventAction doAccept 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetTimesScrnState state
                    = (SetTimesScrnState)scrn.getScreenState();
                 state.acceptSettings( (Integer)arg );
              }
           }; // doAccept

      setTimesScrn.setButtonPressAction( 7, doAccept );

         // arrange to accept key presses
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetTimesScrnState state
                    = (SetTimesScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
                 display.writeLine( 0, state.getTimeString() );
                 display.writeLine( 1, state.getDaysString() );
              }
           }; // doKeyPress

      setTimesScrn.setKeyPressAction( doKeyPress );

   } // buildSetTimesScrn

      /**
       *  Add prompts, transitions, and commands to the screen where
       *  each zone's critical moisture level is displayed and set.
       */

   private void buildSetLevelScrn() {

         // state the prompts
      setLevelScrn.addPrompt( new Prompt(
                            "Zone Level Location",                0, 3) );
      setLevelScrn.addPrompt( new Prompt(
                            "----------------------------------", 1, 3) );
      setLevelScrn.addPrompt( new Prompt(
                            "----------------------------------", 5, 3) );
      setLevelScrn.addPrompt( new Prompt(
                            "Keys change zone's moisture level.", 6, 3) );
      setLevelScrn.addPrompt( new Prompt("<-Propagate Setting",   8, 0) );
      setLevelScrn.addPrompt( new Prompt("  to All Zones",        9, 0) );
      setLevelScrn.addPrompt( new Prompt("Scroll Up->",           8,29) );
      setLevelScrn.addPrompt( new Prompt("Scroll Down->",        11,27) );
      setLevelScrn.addPrompt( new Prompt("Accept New Settings->",14,19) );

         // enter state machine transitions to other screens
      setLevelScrn.setTransition( 7, setUpAutoScrn );
      setLevelScrn.setEscapeTarget( setUpAutoScrn );

         // create a screen state object
      setLevelScrn.setScreenState( new SetLevelScrnState(irrigator) );

         // arrange to accept key presses
      EventAction doKeyPress
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetLevelScrnState state
                    = (SetLevelScrnState)scrn.getScreenState();
                 state.keyPress( (KeyPress)arg );
                 ((ScrollingScreen)scrn).displayScrolledItems();
              }
           }; // doKeyPress

      setLevelScrn.setKeyPressAction( doKeyPress );

         // propagate the current zone's level to all
      EventAction doPropagate 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetLevelScrnState state
                    = (SetLevelScrnState)scrn.getScreenState();
                 state.propagateSetting( (Integer)arg );
                 ((ScrollingScreen)scrn).displayScrolledItems();
              }
           }; // doPropogate

      setLevelScrn.setButtonPressAction( 2, doPropagate );

         // accept the new settings
      EventAction doAccept 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 SetLevelScrnState state
                    = (SetLevelScrnState)scrn.getScreenState();
                 state.acceptSettings( (Integer)arg );
              }
           }; // doAccept

      setLevelScrn.setButtonPressAction( 7, doAccept );

   } // buildSetLevelScrn

      /**
       *  Add prompts, transitions, and commands to the screen that 
       *  notifies users of a device failures.
       */

   private void buildDeviceFailureScrn() {

         // state the prompts
      deviceFailureScrn.addPrompt( new Prompt(
                 "The following device has failed:",  1, 5) );
      deviceFailureScrn.addPrompt( new Prompt("OK->",14,36) );

         // enter state machine transitions to other screens
      deviceFailureScrn.setTransition( 7, LAST_SCRN );
      deviceFailureScrn.setEscapeTarget( LAST_SCRN );

         // create a screen state object
      deviceFailureScrn.setScreenState( new DeviceFailureScrnState(irrigator) );

         // display the irrigation time and days on activation
      EventAction doDeviceDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 DeviceFailureScrnState state
                    = (DeviceFailureScrnState)scrn.getScreenState();
                 state.updateData();
                 display.writeLine( 3, state.getDeviceString() );
                 display.writeLine( 4, state.getZoneString() );
                 display.writeLine( 5, state.getLocationString() );
                 if ( !state.isRecorded() ) {
                    display.writeLine( 8,
                                   "   AquaLush was unable to record this" );
                    display.writeLine( 9,
                                   "   failure in its persistent store." );
                 }
              }
           }; // doDeviceDisplay

      deviceFailureScrn.setEntryAction( doDeviceDisplay );

   } // buildDeviceFailureScrn

      /**
       *  Add prompts, transitions, and commands to the screen that 
       *  notifies users of a persistent store failure.
       */

   private void buildStoreFailureScrn() {

         // state the prompts
      storeFailureScrn.addPrompt( new Prompt(
             "AquaLush was unable to read from or",  5, 3) );
      storeFailureScrn.addPrompt( new Prompt(
             "write to its persistent data store.",  6, 3) );
      storeFailureScrn.addPrompt( new Prompt("OK->",14,36) );

         // enter state machine transitions to other screens
      storeFailureScrn.setTransition( 7, LAST_SCRN );
      storeFailureScrn.setEscapeTarget( LAST_SCRN );

         // reset the store failure flag in the irrigator
      EventAction doStoreDisplay 
         = new EventAction() {
              public void execute( Screen scrn, Object arg ) {
                 irrigator.setIsStoreFailure( false );
              }
           }; // doDeviceDisplay

      storeFailureScrn.setEntryAction( doStoreDisplay );

   } // buildStoreFailureScrn

      /**
       *  Add prompts, transitions, and commands to the screen that 
       *  notifies users that AquaLush has failed at startup and so is
       *  unable to run.
       */

   private void buildStartFailureScrn() {

         // state the prompts
      startFailureScrn.addPrompt( new Prompt(
                 "********************************",4,4) );
      startFailureScrn.addPrompt( new Prompt(
                 "* AquaLush is unable to start. *",5,4) );
      startFailureScrn.addPrompt( new Prompt(
                 "* Please call your installer   *",6,4) );
      startFailureScrn.addPrompt( new Prompt(
                 "* or supplier for help.        *",7,4) );
      startFailureScrn.addPrompt( new Prompt(
                 "********************************",8,4) );

   } // buildStartFailureScrn

} // UIController
