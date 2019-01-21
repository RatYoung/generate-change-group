
package device.sim;

/**
 *  The SimStorageDevice simulates a persistent store. Because it is used
 *  in a web simulation, no persistent storage actually occurs at all.
 *  Instead, the default in the getData operation is always returned.
 *
 *   @author C. Fox
 *  @version 08/02
 */

import device.StorageDevice;
import device.StoreFailureException;

public class SimStorageDevice implements StorageDevice {

      /* attributes
      /*************/


      /* constructors
      /***************/


      /* methods
      /**********/

      /**
       *  Pretend to store a name-value pair in persistent store.
       *
       *     @pre name != null and value != null
       *    @post check the precondition but otherwise do nothing
       *   @param name  The property given a value
       *   @param value The value associated with a property
       *  @throws IllegalArgumentException if precondition is violated
       */

   public void setData( String name, String value )
                        throws StoreFailureException {

      if ( name == null )
         throw new IllegalArgumentException( "Can't store a null name." );
      if ( value == null )
         throw new IllegalArgumentException( "Can't store a null value." );
   }

      /**
       *  Pretend to retrieve a name-value pair from persistent store--
       *  really just return the default value.
       *
       *     @pre name != null
       *    @post check the precondition and return the default value
       *   @param name         The property whose value is retrieved
       *   @param defaultValue The value associated with the property if
       *                       no associated currently exists.
       *  @throws IllegalArgumentException if precondition is violated
       */


   public String getData( String name, String defaultValue )
                                       throws StoreFailureException {
      if ( name == null )
         throw new IllegalArgumentException( "Can't retrieve a null name." );

      return defaultValue;
   }

      /**
       *  Pretend to remove a name-value pair from persistent store--
       *  really do nothing.
       *
       *     @pre name != null
       *    @post check the precondition and otherwise do nothing
       *   @param name The property whose association is removed
       *  @throws IllegalArgumentException if precondition is violated
       */

   public void removeData( String name ) throws StoreFailureException {

      if ( name == null )
         throw new IllegalArgumentException("Can't remove a null name.");
   }

} // SimStorageDevice
