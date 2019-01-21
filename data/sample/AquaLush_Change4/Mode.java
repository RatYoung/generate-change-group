
package irrigation;

/**
 *  The Mode enumeration specifies enumeration values for AquaLush irrigation
 *  modes.
 *
 *   @author C. Fox
 *  @version 07/08
 */

public final class Mode {

      /* enumeration values
      /*********************/

   public static final Mode AUTOMATIC = new Mode( 0, "Automatic" );
   public static final Mode MANUAL    = new Mode( 1, "Manual"    );

      /* attributes 
      /*************/

   private final int     ordinal;   // numeric representation
   private final String  name;      // string representation

      /* constructors
      /***************/

      /**
       *  Create the enumeration values.
       *  Note that this constructor is private!
       *  
       *     @pre 0 <= ordinal; name != null
       *    @post new object has attributes set by parameters
       *   @param ordinal  Numeric keypress representation
       *   @param name     String keypress representation
       */

   private Mode( int ordinal, String name ) {
      this.ordinal = ordinal;
      this.name    = name;
   }

      /* methods
      /**********/

      /**
       *  Get the ordinal of this kepress.
       *  
       *    @pre none
       *   @post @return == ordinal number of this kepress
       */

   public int toInt() {
      return ordinal;
   }

      /**
       *  Get the name of this keypress.
       *  
       *    @pre none
       *   @post @return == name of this keypress as a String.
       */

   public String toString() {
      return name;
   }

      /**
       *  Get the mode with a particular ordinal value.
       *  
       *    @pre 0 <= ord <= 1
       *   @post ord == @return.toInt()
       *  @param ord  The numeric representation of a mode
       */

   public static Mode valueOf( int ord ) {

      switch ( ord ) {
         case 0 : return AUTOMATIC;
         case 1 : return MANUAL;
         default: throw new IllegalArgumentException( "Bad Mode ordinal." );
      }
   }

      /**
       *  Get the mode with a particular string value.
       *  
       *    @pre theName is automatic or manual (case irrelevant)
       *   @post theName.equalIgnoreCase(@result.toString())
       *  @param theName  The string representation of a mode
       */

   public static Mode valueOf( String theName ) {

      if ( theName.equalsIgnoreCase("automatic") ) return AUTOMATIC;
      if ( theName.equalsIgnoreCase("manual") )    return MANUAL;
      throw new IllegalArgumentException( "Bad Mode name." );
   }

} // Mode
