
package device;

/**
 *  The KeyPress enumeration specifies enumeration values for AquaLush keypad
 *  keypresses.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public final class KeyPress {

      /* enumeration values
      /*********************/

   public static final KeyPress ZERO_KEY  = new KeyPress( 0,  "0",  '0' );
   public static final KeyPress ONE_KEY   = new KeyPress( 1,  "1",  '1' );
   public static final KeyPress TWO_KEY   = new KeyPress( 2,  "2",  '2' );
   public static final KeyPress THREE_KEY = new KeyPress( 3,  "3",  '3' );
   public static final KeyPress FOUR_KEY  = new KeyPress( 4,  "4",  '4' );
   public static final KeyPress FIVE_KEY  = new KeyPress( 5,  "5",  '5' );
   public static final KeyPress SIX_KEY   = new KeyPress( 6,  "6",  '6' );
   public static final KeyPress SEVEN_KEY = new KeyPress( 7,  "7",  '7' );
   public static final KeyPress EIGHT_KEY = new KeyPress( 8,  "8",  '8' );
   public static final KeyPress NINE_KEY  = new KeyPress( 9,  "9",  '9' );
   public static final KeyPress DEL_KEY   = new KeyPress( 101,"DEL",'D' );
   public static final KeyPress ESC_KEY   = new KeyPress( 102,"ESC",'E' );

      /* attributes 
      /*************/

   private final int    ordinal;   // numeric representation
   private final String name;      // string representation
   private final char   ch;        // character representation

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

   private KeyPress( int ordinal, String name, char ch ) {
      this.ordinal = ordinal;
      this.name    = name;
      this.ch      = ch;
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
       *  Get the char for this keypress.
       *  
       *    @pre none
       *   @post @return == this keypress as a character
       */

   public char toChar() {
      return ch;
   }

      /**
       *  Get the keypress with a particular ordinal value.
       *  
       *    @pre 0 <= ord <= 9 or ord == 101 or ord = 102
       *   @post ord == @result.toInt()
       *  @param ord  The numeric representation of a keypress
       */

   public static KeyPress valueOf( int ord ) {

      switch ( ord ) {
         case 0   : return ZERO_KEY;
         case 1   : return ONE_KEY;
         case 2   : return TWO_KEY;
         case 3   : return THREE_KEY;
         case 4   : return FOUR_KEY;
         case 5   : return FIVE_KEY;
         case 6   : return SIX_KEY;
         case 7   : return SEVEN_KEY;
         case 8   : return EIGHT_KEY;
         case 9   : return NINE_KEY;
         case 101 : return DEL_KEY;
         case 102 : return ESC_KEY;
         default: throw new IllegalArgumentException( "Bad KeyPress ordinal." );
      }
   }

} // KeyPress
