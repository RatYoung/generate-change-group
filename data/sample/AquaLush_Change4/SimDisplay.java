
package simulation;

/**
 *  The SimDisplay simulates an AquaLush control panel hardware display 
 *  screen and its screen buttons.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimDisplay extends JPanel {

      /* static fields
      /*****************/

      // the display screen colors mimicing a monochrome display
   private static final Color  SCRN_FOREGROUND = Color.white;
   private static final Color  SCRN_BACKGROUND = Color.black;

      // a constant width font mimics is monochrome display
   private static final String FONT_NAME = "Monospaced";
   private static final int    FONT_SIZE = 12;

      // screen is 16 lines by 40 spaces with 4 buttons on each side
   private static final int    NUM_LINES   = 16;
   private static final int    NUM_BUTTONS = 8;

      // blanks are used to size the swing components
   private static final String BLANK_LABEL = "        ";
   private static final String BLANK_LINE = 
                     "                                        ";
                   // 0123456789012345678901234567890123456789

   private static final int    BTN_GAP     = 5;   // button srround size
   private static final int    FRAME_SIZE  = 5;   // for surrounding border


      /* attributes
      /*************/

   private final JLabel[]  lineLbl;  // constitute the display screen
   private final JButton[] scrnBtn;  // all screen buttons

      /* constructors
      /***************/

      /**
       *  Creates a JPanel simulating a control panel display screen along
       *  with screen buttons usable in a larger application.
       *
       *  To get the screen buttons to align with the lines in the display,
       *  the JLabels constituting the lines of the display are grouped with
       *  JButtons in a JPanel, and these are stacked to form the simulated
       *  device.
       *
       *    @pre none
       *   @post the simulated display and buttons are created; the buttons
       *         have no listeners registered
       */

   SimDisplay() {

         // the main JPanel is a vertical box that holds "unitPanels," where
         // a unitPanel consists of 3 (or 4) lines flanked by two buttons
      setLayout( new BoxLayout(this,BoxLayout.Y_AXIS) );
      setBorder( BorderFactory.createEmptyBorder( FRAME_SIZE, FRAME_SIZE,
                                                  FRAME_SIZE, FRAME_SIZE ) );

         // create the unitPanels as JPanels with BorderLayouts
      JPanel unitPanel[] = new JPanel[5];
      for ( int unitCount = 0; unitCount < 5; unitCount++ ) {
         unitPanel[unitCount] = new JPanel( new BorderLayout() );
         add( unitPanel[unitCount] );
         }

         // create arrays to hold lines and screen buttons so we can work
         // with them later; the lines are JLabels and the buttons, JButtons
      lineLbl = new JLabel[NUM_LINES];
      scrnBtn = new JButton[NUM_BUTTONS];

         // fill the arrays with properly configured JLabels and JButtons
      int lineCnt = 0;    // what line we are up to
      int btnCnt  = 0;    // what button we are up to
      Font font   = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE );

      for ( int unitCount = 0; unitCount < unitPanel.length; unitCount++ ) {
         int numLinesInGroup;   // the first group has 4 lines, the rest 3
         JButton westButton;    // goes in WEST region of unitPanel
         JButton eastButton;    // goes in EAST region of unitPanel

            // create the east and west buttons
         westButton = new JButton( BLANK_LABEL );
         eastButton = new JButton( BLANK_LABEL );

            // unitPanel 0 has 4 lines and invisible buttons
            // the rest have 3 lines and buttons with an action command
         if ( unitCount == 0 ) {
            westButton.setVisible( false );
            eastButton.setVisible( false );
            numLinesInGroup = 4;
            }
         else {
            westButton.setActionCommand( "ScrnBtn"+btnCnt );
            scrnBtn[btnCnt++] = westButton;
            eastButton.setActionCommand( "ScrnBtn"+btnCnt );
            scrnBtn[btnCnt++] = eastButton;
            numLinesInGroup = 3;
            }

            // configure the buttons so they look right
         westButton.setFont( font );
         westButton.setForeground( westButton.getBackground() );
         westButton.setBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED) );

         eastButton.setFont( font );
         eastButton.setForeground( eastButton.getBackground() );
         eastButton.setBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED) );

            // put the buttons in their own panel to that they can
            // have a border to space them out (what a pain)
         JPanel westPanel = new JPanel( new GridLayout(1,1,0,0) );
         westPanel.add( westButton );
         westPanel.setBorder(
            BorderFactory.createEmptyBorder(BTN_GAP,BTN_GAP,BTN_GAP,BTN_GAP) );

         JPanel eastPanel = new JPanel( new GridLayout(1,1,0,0) );
         eastPanel.add( eastButton );
         eastPanel.setBorder(
            BorderFactory.createEmptyBorder(BTN_GAP,BTN_GAP,BTN_GAP,BTN_GAP) );

            // now create a panel for the lines and put them in it
         JPanel linePanel = new JPanel( new GridLayout(numLinesInGroup,1,0,0) );
         linePanel.setBackground( SCRN_BACKGROUND );
         for ( int l = 0; l < numLinesInGroup; l++ ) {
            lineLbl[lineCnt] = new JLabel( BLANK_LINE );
            lineLbl[lineCnt].setFont( font );
            lineLbl[lineCnt].setForeground( SCRN_FOREGROUND );
            linePanel.add( lineLbl[lineCnt] );
            lineCnt++;
            }

            // finally, put the buttons and the lines in the unitPanel
         unitPanel[unitCount].add( westPanel, BorderLayout.WEST );
         unitPanel[unitCount].add( linePanel, BorderLayout.CENTER );
         unitPanel[unitCount].add( eastPanel, BorderLayout.EAST );
         }

      } // SimDisplay

      /**
       *  Display a String from the current cursor position.
       *
       *    @pre 0 <= line < 16
       *         str is not null
       *   @post throws IllegalArgumentException if preconditions violated
       *         otherwise str is written to the designated line
       *
       *  @param line which line the string is written to; nothing is written
       *              if line is less than 0 or greater than 15
       *  @param str  the String written to the display screen; nothing
       *              is written if str is null.
       */

   public void writeLine( int line, String str ) {

         // check preconditions
      if ( (line < 0) || (NUM_LINES <= line) || (str == null) )
         throw new IllegalArgumentException();

         // write the line
      lineLbl[line].setText( str );
   }

      /**
       *  Assigns the action listener to all the screen buttons (this is the
       *  screen button device).
       *
       *   @pre none
       *  @post all JButtons are assigned listener as their action listener
       *
       *  @param listener The virtual screen button device that interprets
       *                  simulated hardware screen button presses.
       */

   void setListener( ActionListener listener ) {
      for ( int i = 0; i < NUM_BUTTONS; i++ )
         scrnBtn[i].addActionListener( listener );
   }

} // SimDisplay
