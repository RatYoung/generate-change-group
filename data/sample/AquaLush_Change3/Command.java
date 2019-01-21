
package util;

/**
 *  The Command interface is used to realize the Command pattern in which
 *  an operation may be passed to an object for execution by encapsulating
 *  it in another object--specifically, a Command object.
 *
 *   @author C. Fox
 *  @version 03/02
 */

public interface Command {

      /**
       *  Execute this operation when some event occurs.
       *
       *  @param arg  An arbitrary object argument.
       */

   void execute( Object arg );

} // Command
