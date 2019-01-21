
package device;

/**
 *  The StorageDevice interface specifies the operations of a virtual 
 *  persisten store.
 *
 *   @author C. Fox
 *  @version 08/06
 */

public interface StorageDevice {

      /* static fields
      /****************/

   public static final String STORE_FILE = "store.txt";  // where data is stored

      /* methods
      /**********/

      /**
       *  Store a name-value pair in persistent store.
       *
       *     @pre name != null and value != null
       *    @post the name-value pair is placed in persistent store, replacing
       *          and previous association with name
       *   @param name  The property given a value
       *   @param value The value associated with a property
       *  @throws IllegalArgumentException if precondition is violated
       *  @throws StoreFailureException if the operation fails
       */

   void setData( String name, String value ) throws StoreFailureException;

      /**
       *  Retrieve a name-value pair from persistent store.
       *
       *     @pre name != null
       *    @post if the name is associated with a value in persistent store
       *          then return it;
       *          if the name has no associated value and defaultValue is
       *          not null, associate defaultValue with name and return
       *          default value;
       *          if name is not associated with a value in persistent store
       *          and defaultValue is null, make no association and return null
       *   @param name         The property whose value is retrieved
       *   @param defaultValue The value associtaed with the property if
       *                      no associated currently exists.
       *  @throws IllegalArgumentException if precondition is violated
       *  @throws StoreFailureException if the operation fails
       */


   String getData( String name, String defaultValue )
                                throws StoreFailureException;

      /**
       *  Remove a name-value pair from persistent store.
       *
       *     @pre name != null
       *    @post if the name is associated with a value in persistent store
       *          then remove it;
       *          if the name has no associated value then do nothing.
       *   @param name The property whose association is removed
       *  @throws IllegalArgumentException if precondition is violated
       *  @throws StoreFailureException if the operation fails
       */

   void removeData( String name ) throws StoreFailureException;

} // StorageDevice
