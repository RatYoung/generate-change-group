

package irrigation;

/**
 *  The FailureReport holds data about valve and sensor failures for 
 *  access by clients. It is a record class.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import java.util.Collection;

public class FailureReport {

      /* attributes
      /*************/

   public final Collection<SensorReport> failedSensors;
   public final Collection<ValveReport>  failedValves;

      /* constructors
      /***************/

      /**
       *  Initialize the FailureReport
       */

   public FailureReport( Collection<SensorReport> theFailedSensors,
                         Collection<ValveReport>  theFailedValves ) {

      failedSensors = theFailedSensors;
      failedValves  = theFailedValves;
   }

} // FailureReport
