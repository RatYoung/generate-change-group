
package simulation;

/**
 *  The SimTimeControl object shows the simulated day and time to the second,
 *  and has controls for starting, stopping, speeding up, and slowing down
 *  the simulated time.
 *
 *  This object is a JPanel that can be used as a component.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import irrigation.Irrigator;
import util.Command;
import util.Day;

class SimTimeControl extends JPanel implements Observer {

      /* static fields
      /****************/

   private static final int    GAP         = 4;  // for etched border spacing
   private static final String speedRemark = "Times real time:  ";

      /* attributes
      /*************/

   private final SimTime       time;        // the simulated time object
   private final JLabel        timeLbl;     // displays current simulated time
   private final JButton       pauseBtn;    // stops time
   private final JButton       resumeBtn;   // restarts time
   private final JButton       jumpBtn;     // jump to next irrigation time
   private final JLabel        speedLbl;    // displays multiple of real time
   private final JButton       fasterBtn;   // speeds up time
   private final JButton       slowerBtn;   // slows down time
   private final JButton       defaultBtn;  // return to default value
   private final DecimalFormat timeUnit;    // to format time display
   private       Irrigator     irrigator;   // to get irrigation time and days

      /* constructors
      /***************/

      /**
       *  Creates a simulated time control panel showing the weekday and
       *  the time to the second, with buttons for starting and stopping 
       *  simulated time, and for jumping to just before the next irrigation
       *  time. It also displays the speed of the simulation, with buttons 
       *  for speeding it up and slowing it down, and returning to real time.
       *
       *    @pre none
       *   @post the time control panel is created
       */

   SimTimeControl() {

         // register as a SimTime observer
      time = SimTime.instance();
      time.addObserver( this );

         // set the irrigator to null for now
      irrigator = null;

         // layout strategy: make a border layout with the labels in the 
         // west and buttons in the east. The labels are in a grid layout 
         // and the buttons in another grid layout
      setLayout( new BorderLayout(GAP,0) );
      Border title = BorderFactory.createEtchedBorder();
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title, " Time "),
            BorderFactory.createEmptyBorder( GAP,GAP,GAP,GAP ) ) );

         // set up the label grid
      JPanel labelPanel = new JPanel( new GridLayout(2,1,0,0) );
      timeLbl  = new JLabel( "Wednesday  00:00:00     ", JLabel.LEFT );
      speedLbl = new JLabel( speedRemark + "1     ", JLabel.LEFT );
      labelPanel.add( timeLbl );
      labelPanel.add( speedLbl );
      add( labelPanel, BorderLayout.WEST );

         // set up the button grid
      JPanel buttonPanel = new JPanel( new GridLayout(2,3,GAP,GAP) );
      pauseBtn  = new JButton( "Pause" );
      resumeBtn = new JButton( "Resume" );
      jumpBtn   = new JButton( "Jump" );
      fasterBtn = new JButton( "Faster" );
      slowerBtn = new JButton( "Slower" );
      defaultBtn = new JButton( "Default" );
      buttonPanel.add( pauseBtn );
      buttonPanel.add( resumeBtn );
      buttonPanel.add( jumpBtn );
      buttonPanel.add( fasterBtn );
      buttonPanel.add( slowerBtn );
      buttonPanel.add( defaultBtn );
      add( buttonPanel, BorderLayout.EAST );

         // add Pause button action listener
      pauseBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               pauseBtn.setEnabled( false );
               time.stop();
               resumeBtn.setEnabled( true );
            }
         }
      );

         // add Resume button action listener
      resumeBtn.setEnabled( false );
      resumeBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               resumeBtn.setEnabled( false );
               time.start();
               pauseBtn.setEnabled( true );
            }
         }
      );

         // add Jump button action listener
      jumpBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

               int currentTime         = time.getHour()*100+time.getMinute();
               int irrigationTime      = irrigator.getIrrigationTime();
               
               //Only allows jumps if more than one hour until next irrigation
               if (currentTime - irrigationTime < 60)
            	   return;
               
               Day currentDay          = time.getDay();
               Set<Day> irrigationDays = irrigator.getIrrigationDays();

               if ( irrigationDays.isEmpty() ) return;

                  // find the time/day one 1:01 after the current time
               int timePlusOneHour = currentTime + 101;
               Day dayPlusOneHour  = currentDay;
               if ( 2359 < timePlusOneHour ) {
                  timePlusOneHour -= 2400;
                  dayPlusOneHour = dayPlusOneHour.succ();
               }

                  // find the first irrigation day after one hour from now
               Day nextIrrigationDay;
               if ( irrigationDays.contains(dayPlusOneHour)
                    && (timePlusOneHour <= irrigationTime) ) {
                  nextIrrigationDay = dayPlusOneHour;
               }
               else {
                  nextIrrigationDay = dayPlusOneHour.succ();
                  while ( !irrigationDays.contains(nextIrrigationDay) )
                     nextIrrigationDay = nextIrrigationDay.succ();
               }

                  // set the jump time to one hour before next irrigation
               Day jumpDay  = nextIrrigationDay;
               int jumpTime = irrigationTime - 100;
               if ( jumpTime < 0 ) {
                  jumpTime += 2400;
                  jumpDay = jumpDay.pred();
               }
               time.setDay( jumpDay );
               time.setHour( jumpTime/100 );
               time.setMinute( jumpTime%100 );
               time.setSecond( 0 );
            }
         }
      );

         // add Faster button mouse listener for held down mouse button
      fasterBtn.addMouseListener( new ButtonRepeater( 50, new Command() {
            public void execute( Object caller ) {
               int speed = time.getSpeed();

                  // deal with the messy step function
               if ( speed == 1000 ) return;
               if ( speed < 31 ) speed++;
               else if ( speed == 31 ) speed = 62;
               else if ( speed == 62 ) speed = 125;
               else speed *= 2;
               time.setSpeed( speed );
               speedLbl.setText( speedRemark + speed );

                  // enable or disable the faster and slower buttons
               fasterBtn.setEnabled( speed < 1000 );
               slowerBtn.setEnabled( true );
            }
         }
      ) );

         // add Slower button mouse listener for held down mouse button
      slowerBtn.setEnabled( false );
      slowerBtn.addMouseListener( new ButtonRepeater( 50, new Command() {
            public void execute( Object caller ) {
               int speed = time.getSpeed();
               if ( speed == 1 ) return;

                  // deal with the messy step function
               if ( 125 < speed ) speed /= 2;
               else if ( speed == 125 ) speed = 62;
               else if ( speed == 62 ) speed = 31;
               else speed--;
               time.setSpeed( speed );
               speedLbl.setText( speedRemark + speed );

                  // enable or disable the faster and slower buttons
               fasterBtn.setEnabled( true );
               slowerBtn.setEnabled( 1 < speed );
            }
         }
      ) );

         // add Default button action listener
      defaultBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               time.setSpeedToDefault();
               speedLbl.setText( speedRemark + time.getSpeed() );
               slowerBtn.setEnabled( false );
               fasterBtn.setEnabled( true );
            }
         }
      );

         // prepare the format for the hours, minutes, and seconds display
      timeUnit = new DecimalFormat( "00" );
   }

      /* methods
      /**********/

      /**
       *  Set the irrigator attribute so the Jump button can get the
       *  irrigation time and days.
       *
       *    @pre none
       *   @post the irrigator attribute has been set
       *  @param irrigator  The irrigation control object
       */

   void setIrrigator( Irrigator theIrrigator ) {
      irrigator = theIrrigator;
   }


      /**
       *  Respond to a notification from the SimTime object that a simulated
       *  second has passed.
       *
       *    @pre none
       *   @post the simulated time display is updated
       *  @param subject   The observable--ignored
       *  @param argument  Arbitrary argument--ignored.
       */

   public void update( Observable subject, Object argument ) {

      timeLbl.setText(          time.getDay().toString() + " " +
                       timeUnit.format(time.getHour())   + ":" +
                       timeUnit.format(time.getMinute()) + ":" +
                       timeUnit.format(time.getSecond()) );
   }

} // SimTimeControl
