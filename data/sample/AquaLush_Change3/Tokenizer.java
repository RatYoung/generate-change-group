
package startup;

/**
 *  A Tokenizer reads characters from a Reader and assembles them into
 *  tokens that it classifies and passes on to a client. This Tokenizer
 *  recognizes the tokens used in an AquaLush configuration file.
 *
 *   @author C. Fox
 *  @version 07/06; revised from version 03/02
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

class Tokenizer {

      /*  static operations
      /***********************/

      /**
       *  Test this tokenizer from the keyboard.
       *
       *  @param argv Command line arguments (none expected--ignored).
       */

   public static void main( String[] argv )
      {
      Tokenizer scan;         // what we are testing
      TokenType tokenType;    // the current token type
      String token;           // the current token string

         // prompt the tester and create the scanner
      System.out.println( "Input: " );
      scan = new Tokenizer( new BufferedReader(
                            new InputStreamReader(System.in) ) );
      
         // process until the user hits eof
      tokenType = scan.nextToken();
      while ( tokenType != TokenType.EOF )
         {
         System.out.println( "Type: "+ tokenType +
                             " token: "+ scan.getToken() );
         tokenType = scan.nextToken();
         }
      }

      /* attributes
      /*************/

   private final Reader source;       // the character stream tokenized

   private int          nextChar;     // the look-ahead character buffer
   private StringBuffer token;        // the current token
   private TokenType    tokenType;    // the current token's type

      /* constructors
      /***************/

      /**
       *  Create a tokenizer to scan a Reader source for use as an
       *  AquaLush configuration specification.
       *
       *  @param theSource  The Reader whose characters are assembled into
       *                    tokens. Note that this can by any Reader--a file,
       *                    an array, etc.
       */

   Tokenizer( Reader theSource ) {

         // initialize attributes
      source    = theSource;
      tokenType = TokenType.BAD;
      token     = new StringBuffer();

         // prime the look-ahead character buffer
      try
         { nextChar = source.read(); }
      catch ( IOException e)
         { tokenType = TokenType.EXCEPTION; }
      }

      /* methods
      /**********/

      /**
       *  Advance to the next token and return its type.
       *
       *     @pre none
       *    @post the next token is removed from the input stream; its text
       *          is stored and its type is returned.
       */

   TokenType nextToken() {

         // clear the current token
      token.setLength(0);

         // don't read any more at EOF
      if ( nextChar == -1 ) {
         tokenType = TokenType.EOF;
         return tokenType;
      }

      try {
            // read past white space
         while ( Character.isWhitespace((char)nextChar) ) {
            nextChar = source.read();
         }

            // don't read any more at EOF
         if ( nextChar == -1 ) {
            tokenType = TokenType.EOF;
            return tokenType;
         }

            // get the next token
         if ( Character.isDigit((char)nextChar) ) {
            tokenType = TokenType.NUMBER;
            while ( Character.isDigit((char)nextChar) ) {
               token.append( (char)nextChar );
               nextChar = source.read();
            }
         }
         else switch ( (char)nextChar ) {

            case ';':
               tokenType = TokenType.SEMICOLON;
               token.append(';');
               nextChar = source.read();
               break;

            case '{':
               tokenType = TokenType.LEFT_BRACE;
               token.append('{');
               nextChar = source.read();
               break;

            case '}':
               tokenType = TokenType.RIGHT_BRACE;
               token.append('}');
               nextChar = source.read();
               break;

            case '<':
               tokenType = TokenType.DESCRIPTION;
               token.append('<');
               nextChar = source.read();
               while ( (nextChar != -1) && ((char)nextChar != '>') ) {
                  token.append( (char)nextChar );
                  nextChar = source.read();
               }
               if ( nextChar == -1 ) tokenType = TokenType.BAD;
               else {
                  token.append( '>' );
                  nextChar = source.read();
               }
               break;

            case 'Z':
               token.append('Z');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.ZONE_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else tokenType = TokenType.BAD;
               break;

            case 'z':
               token.append('z');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.ZONE_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else {
                  while ( Character.isLetter((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
                  if ( token.toString().equals("zone") )
                     tokenType = TokenType.ZONE_KWD;
                  else
                     tokenType = TokenType.BAD;
               }
               break;
               
            case 'S':
               token.append('S');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.SENSOR_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else tokenType = TokenType.BAD;
               break;

            case 's':
               token.append('s');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.SENSOR_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else {
                  while ( Character.isLetter((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
                  if ( token.toString().equals("sensor") )
                     tokenType = TokenType.SENSOR_KWD;
                  else
                     tokenType = TokenType.BAD;
               }
               break;
               
            case 'V':
               token.append('V');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.VALVE_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else tokenType = TokenType.BAD;
               break;

            case 'v':
               token.append('v');
               nextChar = source.read();
               if ( Character.isDigit((char)nextChar) ) {
                  tokenType = TokenType.VALVE_ID;
                  while ( Character.isDigit((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
               }
               else {
                  while ( Character.isLetter((char)nextChar) ) {
                     token.append( (char)nextChar );
                     nextChar = source.read();
                  }
                  if ( token.toString().equals("valve") )
                     tokenType = TokenType.VALVE_KWD;
                  else
                     tokenType = TokenType.BAD;
               }
               break;
               
            default:
               tokenType = TokenType.BAD;
               break;

         } // switch

            // try to flush bad tokens to avoid a stream of BAD token types
         if ( tokenType == TokenType.BAD ) {
            do {
               token.append( (char)nextChar );
               nextChar = source.read();
            } while ( (nextChar != -1)
                       && (   Character.isDigit((char)nextChar) 
                           || Character.isLetter((char)nextChar) ) );
         }

      }
      catch ( IOException e) {
         tokenType = TokenType.EXCEPTION;
      }

         // return whatever the new token type is
      return tokenType;

   } // nextToken

      /**
       *  Fetch the type of the current token.
       *
       *    @pre none
       *   @post @return is the type of the last token from the input stream
       */

   TokenType getTokenType() { return tokenType; }

      /**
       *  Fetch the current token as a String.
       *
       *    @pre none
       *   @post @return is the text of the last token from the input stream.
       */

   String getToken() { return token.toString(); }

} // Tokenizer
