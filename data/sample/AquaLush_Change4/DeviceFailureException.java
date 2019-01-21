
package device;

/**
 *   This checked exception class is meant to signal a hardware device 
 *   failure detected by a virtual device. It is intended to be caught 
 *   by the user of the virtual device, or propagated to a class that
 *   can handle the failure.
 *
 *   Clients can then add device ids and examine them later.
 *
 *   @author C. Fox
 *  @version 07/27
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DeviceFailureException extends Exception {

      /* attributes
      /*************/

   private Set<String> deviceIDs;   // to record the failed devices

      /* constructors
      /***************/

      /**
       *  Create a device failure exception with no device ids.
       *
       *     @pre none
       *    @post an exception with an empty device id set is created
       */

   public DeviceFailureException() {
      deviceIDs = new HashSet<String>();
   }

      /**
       *  Create a device failure exception with a given set of device ids.
       *
       *     @pre  c is not null
       *    @post  an exception with a set of device ids based on the 
       *           parameter is created
       *   @param  c  A collection of IDs of other failed devices
       *  @throws  NullPointerException if precondition is violated
       */

   public DeviceFailureException( Collection<String> c ) {
      deviceIDs = new HashSet<String>(c);
   }

      /* public methods
      /*****************/

      /**
       *  Add the devices from another DeviceFailureException to this one.
       *
       *     @pre  e is not null
       *    @post  the set of device ID is autmented by those from e
       *   @param  e  Another device failure exception
       *  @throws  IllegalArgumentException if precondition is violated
       */

   public void addException( DeviceFailureException e ) {

      if ( e == null )
         throw new IllegalArgumentException( "Null device failure exception." );

      deviceIDs.addAll( e.deviceIDs );
   }

      /**
       *  Create a device failure exception with no device ids.
       *
       *     @pre  theDeviceID != null
       *    @post  a particular device id is added to this exception
       *   @param  theDeviceID  The unique failed device identifier
       *  @throws  IllegalArgumentException if precondition is violated
       */

   public void addDevice( String theDeviceID ) {

      if ( theDeviceID == null )
         throw new IllegalArgumentException( "Null device identifer." );

      deviceIDs.add( theDeviceID );
   }

      /**
       *  Return the failed device identifiers.
       *
       *     @pre  none
       *    @post  @result is a set of failed device identifiers
       */

   public Set<String> getDevices() {
      return new HashSet<String>(deviceIDs); }

} // DeviceFailureException
