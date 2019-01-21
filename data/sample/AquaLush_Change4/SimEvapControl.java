
package simulation;

/**
 *  The SimEvapControl object shows the simulated rate of evaporation
 *  and has controls for increasing and decreasing it.
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
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Command;

class SimEvapControl extends JPanel {

      /* static fields
      /****************/

   private static final int    GAP    = 4;
   private static final String remark = "Percent per day:  ";

      /* attributes
      /*************/

   private final SimSite site;          // the simulated irrigation site
   private final JLabel  rateLbl;       // displays current evaporation rate
   private final JButton fasterBtn;     // speeds up evaporation rate
   private final JButton slowerBtn;     // slows down evaporation rate
   private final JButton defaultBtn;    // reset to default value

      /* constructors
      /***************/

      /**
       *  Creates a simulated evaporation control panel showing the
       *  evaporation rate with buttons for increasing and decreasing 
       *  it, and for returning to the default value.
       *
       *    @pre none
       *   @post the evaporation control panel is created
       *  @param theSite the simulated site being irrigated
       */

   SimEvapControl( SimSite theSite ) {

      site = theSite;

         // layout strategy: make a border layout with the label in the 
         // west and buttons in the east. The buttons are in a grid layout
      setLayout( new BorderLayout(GAP,0) );
      Border title = BorderFactory.createEtchedBorder();
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title, " Evaporation Rate "),
            BorderFactory.createEmptyBorder( GAP,GAP,GAP,GAP ) ) );

         // set up the label
      JPanel labelPanel = new JPanel( new GridLayout(1,1,GAP,GAP) );
      rateLbl = new JLabel( remark+site.getEvaporationRate()+"     ",
                            JLabel.LEFT );
      labelPanel.add( rateLbl );
      add( labelPanel, "West" );

         // set up the button grid
      JPanel buttonPanel = new JPanel( new GridLayout(1,3,GAP,GAP) );
      fasterBtn = new JButton( "Faster" );
      slowerBtn = new JButton( "Slower" );
      defaultBtn = new JButton( " Default " );  // extra spaces for cosmetics
      buttonPanel.add( fasterBtn );
      buttonPanel.add( slowerBtn );
      buttonPanel.add( defaultBtn );
      add( buttonPanel, "East" );

         // add Faster button mouse listener for held down mouse button
      fasterBtn.addMouseListener( new ButtonRepeater( 50, new Command() {
            public void execute( Object caller ) {
               int rate = site.getEvaporationRate();
               if ( rate < 100 ) {
                  site.setEvaporationRate( ++rate );
                  rateLbl.setText( remark + site.getEvaporationRate() );

                     // enable or disable the faster and slower buttons
                  fasterBtn.setEnabled( rate < 100 );
                  slowerBtn.setEnabled( true );
               }
            }
         }
      ) );

         // add Slower button mouse listener for held down mouse button
      slowerBtn.addMouseListener( new ButtonRepeater( 50, new Command() {
            public void execute( Object caller ) {
               int rate = site.getEvaporationRate();
               if ( 0 < rate ) {
                  site.setEvaporationRate( --rate );
                  rateLbl.setText( remark + site.getEvaporationRate() );

                     // enable or disable the faster and slower buttons
                  fasterBtn.setEnabled( true );
                  slowerBtn.setEnabled( 0 < rate );
               }
            }
         }
      ) );

         // add Default button action listener
      defaultBtn.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            site.setEvaporationRateToDefault();
            rateLbl.setText( remark + site.getEvaporationRate() );
            fasterBtn.setEnabled( true );
            slowerBtn.setEnabled( true );
         }
      } );

   }

      /* methods
      /**********/

} // SimEvapControl
