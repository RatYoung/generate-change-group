
package device;

/**
 *  The TickListener interface specifies the tick() operation that a
 *  clock will use to notify its clients of the passage of time. This is
 *  the command interface in a Command pattern, while a ClockDevice is the
 *  invoker.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface TickListener {

      /**
       *  Notify a TickListener client that time has passed.
       *  
       *    @pre  one minute has passed since the last call of tick()
       *   @post  none
       */

   void tick();

} // TickListener
