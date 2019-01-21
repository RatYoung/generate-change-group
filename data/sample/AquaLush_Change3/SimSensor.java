
package simulation;

/**
 *  A SimSensor simulates a moisture sensor device.
 *
 *   @author C. Fox
 *  @version 01/05
 */

import java.util.Observable;

public class SimSensor extends Observable {

      /* static fields
      /****************/

      /* attributes
      /*************/

   private static final int DEFAULT_LEVEL = 40;

   private final String name;       // the unique sensor identifier
   private int          level;      // moisture level in range 0..100
   private boolean      isFailed;   // true iff failure is simulated

      /* constructors
      /***************/

      /**
       *  Creates a simulated moisture sensor.
       *
       *    @pre none
       *   @post all attributes are initialized
       *  @param sensorID The new sensor's identifier
       */

   public SimSensor( String sensorID ) {
      name     = sensorID;
      level    = DEFAULT_LEVEL;
      isFailed = false;
   }

      /* methods
      /***********/

      /**
       *  Trivial get and set operations
       */

   public String  getName()     { return name; }
   public boolean getIsFailed() { return isFailed; }

      /**
       *  Read the simulated moisture level.
       *
       *     @pre None
       *    @post If the device has failed, @return == -1
       *          If the device has not failed, @return is in range 0..100
       */

   public int getLevel() {
      if ( isFailed ) return -1;
      return level;
   }

      /**
       *  Set the moisture level for this sensor.
       *
       *     @pre newLevel must be in range 0..100
       *    @post the level is set, and it changed, observers are notified,
       *          or an exception is thown
       *   @param newLevel  The new moisture level from the environment
       *  @throws IllegalArgumentException if the precondition is violated
       */

   public void setLevel( int newLevel ) {

      if ( (newLevel < 0) || (100 < newLevel) )
         throw new IllegalArgumentException();

      if ( level != newLevel ) {
         setChanged();
         level = newLevel;
         notifyObservers();
      }
   }

      /**
       *  Make the sensor fail.
       *
       *     @pre none
       *    @post the failed flag is set to true and observers are notified
       */

   public void setIsFailed() {
      isFailed = true;
      setChanged();
      notifyObservers();
   }

      /**
       *  Make the sensor work again.
       *
       *     @pre none
       *    @post the failed flag is set to false and observers are notified
       */

   public void resetIsFailed() {
      isFailed = false;
      setChanged();
      notifyObservers();
   }

} // SimSensor
