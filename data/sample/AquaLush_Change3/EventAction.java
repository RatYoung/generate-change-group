
package ui;

/**
 *  An EventAction is a Command interface in the Command pattern used to
 *  implement state machine actions.
 *
 *   @author C. Fox
 *  @version 03/05
 */


interface EventAction {

   public void execute( Screen host, Object arg );

} // EventAction
