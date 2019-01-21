
package device;

/**
 *   This checked exception class is meant to signal a persistent store
 *   failure detected by a virtual store device. It is intended to be caught 
 *   by the user of the virtual device, or propagated to a class that
 *   can handle the failure.
 *
 *   @author C. Fox
 *  @version 08/06
 */

public class StoreFailureException extends Exception {

} // StoreFailureException
