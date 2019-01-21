
package startup;

/**
 *  The TokenType enumeration specifies the tokens used in parsing AquaLush
 *  configuration files.
 *
 *   @author C. Fox
 *  @version 07/08
 */

final class TokenType {

      /* enumeration values
      /*********************/

   public static final TokenType ZONE_ID     = new TokenType(0, "zoneID"     );
   public static final TokenType SENSOR_ID   = new TokenType(1, "sensorID"   );
   public static final TokenType VALVE_ID    = new TokenType(2, "valveID"    );
   public static final TokenType ZONE_KWD    = new TokenType(3, "zone"       );
   public static final TokenType SENSOR_KWD  = new TokenType(4, "sensor"     );
   public static final TokenType VALVE_KWD   = new TokenType(5, "valve"      );
   public static final TokenType LEFT_BRACE  = new TokenType(6, "{"          );
   public static final TokenType RIGHT_BRACE = new TokenType(7, "}"          );
   public static final TokenType SEMICOLON   = new TokenType(8, ";"          );
   public static final TokenType NUMBER      = new TokenType(9, "number"     );
   public static final TokenType DESCRIPTION = new TokenType(10,"description");
   public static final TokenType EOF         = new TokenType(11,"eof"        );
   public static final TokenType BAD         = new TokenType(12,"bad"        );
   public static final TokenType EXCEPTION   = new TokenType(13,"exception"  );

      /* attributes 
      /*************/

   private final int    ordinal;   // numeric representation
   private final String name;      // string representation

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

   private TokenType( int ordinal, String name ) {
      this.ordinal = ordinal;
      this.name    = name;
   }

      /* methods
      /**********/

      /**
       *  Get the ordinal of this token type.
       *  
       *    @pre none
       *   @post @result == ordinal number of this token type
       */

   public int toInt() { return ordinal; }

      /**
       *  Get the name of this token type.
       *  
       *    @pre none
       *   @post @result == name of this token type as a String.
       */

   public String toString() { return name; }

      /**
       *  Get the token type with a particular ordinal value.
       *  
       *    @pre 0 <= ord <= 9 or ord == 101 or ord = 102
       *   @post ord == @result.toInt()
       *  @param ord  The numeric representation of a token type
       */

   public static TokenType valueOf( int ord ) {

      switch ( ord ) {
         case 0 : return ZONE_ID;
         case 1 : return SENSOR_ID;
         case 2 : return VALVE_ID;
         case 3 : return ZONE_KWD;
         case 4 : return SENSOR_KWD;
         case 5 : return VALVE_KWD;
         case 6 : return LEFT_BRACE;
         case 7 : return RIGHT_BRACE;
         case 8 : return SEMICOLON;
         case 9 : return NUMBER;
         case 10: return DESCRIPTION;
         case 11: return EOF;
         case 12: return BAD;
         case 13: return EXCEPTION;
         default: throw new IllegalArgumentException("Bad TokenType ordinal.");
      }
   }

} // TokenType
