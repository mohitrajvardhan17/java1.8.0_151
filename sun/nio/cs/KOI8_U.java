package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class KOI8_U
  extends Charset
  implements HistoricallyNamedCharset
{
  private static final String b2cTable = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ёє╔ії╗╘╙╚╛ґ╝╞╟╠╡ЁЄ╣ІЇ╦╧╨╩╪Ґ╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  private static final char[] b2c = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ёє╔ії╗╘╙╚╛ґ╝╞╟╠╡ЁЄ╣ІЇ╦╧╨╩╪Ґ╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();
  private static final char[] c2b = new char['Ԁ'];
  private static final char[] c2bIndex = new char['Ā'];
  
  public KOI8_U()
  {
    super("KOI8-U", StandardCharsets.aliases_KOI8_U);
  }
  
  public String historicalName()
  {
    return "KOI8_U";
  }
  
  public boolean contains(Charset paramCharset)
  {
    return (paramCharset.name().equals("US-ASCII")) || ((paramCharset instanceof KOI8_U));
  }
  
  public CharsetDecoder newDecoder()
  {
    return new SingleByte.Decoder(this, b2c);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new SingleByte.Encoder(this, c2b, c2bIndex);
  }
  
  static
  {
    char[] arrayOfChar1 = b2c;
    char[] arrayOfChar2 = null;
    SingleByte.initC2B(arrayOfChar1, arrayOfChar2, c2b, c2bIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\KOI8_U.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */