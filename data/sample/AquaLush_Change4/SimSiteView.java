
package simulation;

/**
 *  A SimSiteView depicts an irrigation site showing the irrigations zones
 *  in a simulated site. The site shows when a valve is on or off, and the 
 *  moisture level read by the sensor in each zone.
 *
 *  The SimSiteView participates in the Model-View-Controller (MVC) and
 *  Observer paterns. Specifically, the SimSiteView is a view of the SimSite 
 *  model and it is an Observer of the SimSite.
 *
 *   @author C. Fox
 *  @version 02/05
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

class SimSiteView extends JPanel implements Observer {

      /* static fields
      /****************/

   private static final int BORDER_WIDTH   = 4;  // for an empty border
   private static final int ZONE_GAP       = 1;  // for space between zones
   private static final int NUM_ZONE_COLS  = 3;  // zones across the display
   private static final int NUM_ZONE_ROWS  = 1;  // zone down the display
   private static final int NUM_VALVE_COLS = 2;  // valves across a zone
   private static final int NUM_VALVE_ROWS = 2;  // valves down a zone

   private static final Color GRASS_GREEN = new Color( 120, 255, 160 );
   private static final Color WATER_BLUE  = new Color(   0, 205, 255 );

      /* attributes
      /*************/

   private final SimSite site;  // the site depicted

      /* constructors
      /***************/

      /**
       *  Creates a JPanel showing the simulated site. It needs to be
       *  set up after the GUI has stabilized.
       *
       *    @note The insides of the panel are redrawn when either the JPanel 
       *          is painted or a notification is received from the SimSite
       *     @pre theSite is not null
       *    @post the JPanel is created and bordered, and all attributes are 
       *          initialized
       *  @throws NullPointerException when the precondition is violated
       *   @param theSite The model SimSite viewed through this view
       */

   SimSiteView( SimSite theSite ) {

         // put a bevel border around the simulated site display
      setBorder (
         BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(
                 BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH ),
            BorderFactory.createBevelBorder( BevelBorder.LOWERED ) ) );

         // record the SimSite that is the model for this view
      site = theSite;

         // register as a SimSite observer
      site.addObserver( this );

   } // SimSiteView

      /* methods
      /**********/

      /**
       *  The model notifies the view that it has changed--this is a public
       *  abstract method from Observer.
       *
       *     @pre  none
       *    @post  the simulated site is redrawn to reflect it current state
       *   @param  subject  The calling subject--ignored (it must be site)
       *   @param  arg      Extra parameters--ignored (should be null)
       */

   public void update( Observable subject, Object arg ) {
      draw( getGraphics() );
   }

      /**
       *  Render this component to show the component or repair damage.
       *  This routine is here to everride the JPanel's paint routine so
       *  that the inside of the JPanel can be drawn the way we want.
       *
       *     @pre none
       *    @post the simulated site is redrawn to reflect it current state
       *   @param gc  The component's graphics context.
       */

   public void paint( Graphics gc ) {
      draw( gc );
   }

      /* private methods
      /******************/

      /**
       *  Redraw the entire site, reflecting the current state of the valves
       *  and sensors at the moment of drawing. This operation uses double
       *  buffering to remove screen flicker.
       *
       *   @param gc  The component's graphics context.
       */

   private void draw( Graphics gc ) {

         // get data about the view itself
      int width  = getSize().width-getInsets().left-getInsets().right;
      int height = getSize().height-getInsets().top-getInsets().bottom;

         // create a site image for double-buffering
      Image    imageBuffer   = createImage( width, height );
      Graphics imageGraphics = imageBuffer.getGraphics();
      imageGraphics.clearRect( 0, 0, width, height );

         // iterate through the zones and draw each one
      int zoneWidth  = (width  - (NUM_ZONE_COLS-1)*ZONE_GAP)/NUM_ZONE_COLS;
      int zoneHeight = (height - (NUM_ZONE_ROWS-1)*ZONE_GAP)/NUM_ZONE_ROWS;
      int zoneX = 0;
      int zoneY = 0;

      Iterator zoneIter = site.zoneIterator();
      while ( zoneIter.hasNext() ) {

         SimZone zone = (SimZone)zoneIter.next();

            // write the zone display to the image buffer
         drawZone( imageGraphics, zone, zoneX, zoneY, zoneWidth, zoneHeight );

            // move to the next zone
         zoneX += zoneWidth + ZONE_GAP;
         if ( width < zoneX+zoneWidth ) {
            zoneX = 0; zoneY += zoneHeight + ZONE_GAP;
         }
      }

         // dump the buffer to the display screen
      gc.drawImage( imageBuffer, getInsets().left, getInsets().top, this );

   } // draw

      /**
       *  Redraw the portion of the JPanel where a single irrigation zone
       *  is displayed. The region irrigated by a valve is drawn green or
       *  blue to indicate whether the valve is open and its name is written
       *  at the center of the region. The sensor name and its reading are
       *  displayed at the center of the irrigation zone.
       *
       *   @param gc     The image buffer's graphics context.
       *   @param zone   The zone drawn (used to fetch its valves & sensor)
       *   @param x      The zone's display region x coordinate
       *   @param y      The zone's display region y coordinate
       *   @param width  The zone's display region width
       *   @param height The zone's display region height
       */

   private void drawZone( Graphics gc, SimZone zone, int x, int y, 
                                       int width, int height ) {

      Font font;       // for making the display and control fonts the same
      FontMetrics fm;  // for centering labels in regions
      String label;    // a valve name or sensor reading

         // 1: Set the font and gets its font metrics object
      font = new Font( "Dialog", Font.BOLD, 12 );
      gc.setFont( font );
      fm = gc.getFontMetrics();

         // 2: Iterate through each valve and draw its background and name
      Iterator valveIter = zone.valveIterator();
      int valveX = x;
      int valveY = y;
      int valveWidth  = width/NUM_VALVE_COLS;
      int valveHeight = height/NUM_VALVE_ROWS;
      int colCount = 0;
      int rowCount = 0;

      while ( valveIter.hasNext() ) {

         SimValve valve = (SimValve)valveIter.next();

            // blue for watering, green for not
         if ( valve.isOpen() ) gc.setColor( WATER_BLUE );
         else                  gc.setColor( GRASS_GREEN );
         gc.fillRect( valveX, valveY, valveWidth, valveHeight );

            // write the valve name in the middle of the region
         label = valve.getName();
         gc.setColor( Color.black );
         gc.drawString( label, valveX+(valveWidth-fm.stringWidth(label))/2,
                               valveY+(valveHeight+fm.getAscent())/2        );

            // move to the next valve region
         colCount++;
         valveX += valveWidth;
         if ( NUM_VALVE_COLS-1 == colCount )
            valveWidth += width%NUM_VALVE_COLS;
         else if ( NUM_VALVE_COLS == colCount ) {
            colCount = 0; rowCount++;
            valveX = x; valveY = valveY+valveHeight;
            valveWidth = width/NUM_VALVE_COLS;
            if ( NUM_VALVE_ROWS-1 == rowCount )
               valveHeight += height%NUM_VALVE_ROWS;
         }
      }

         // 3: Write the sensor name and reading in the middle of the zone
      SimSensor sensor = zone.getSensor();
      int level = sensor.getLevel();
      String levelString = (level < 0) ? "--" : String.valueOf(level) +"%";
      label = sensor.getName() +": "+ levelString;
      gc.setColor( Color.black );
      gc.drawString( label, x+(width-fm.stringWidth(label))/2,
                            y+(height+fm.getAscent())/2        );
   }

} // SimSiteView
