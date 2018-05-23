package java.nio.charset;

public final class StandardCharsets
{
  public static final Charset US_ASCII = Charset.forName("US-ASCII");
  public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
  public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
  public static final Charset UTF_16 = Charset.forName("UTF-16");
  
  private StandardCharsets()
  {
    throw new AssertionError("No java.nio.charset.StandardCharsets instances for you!");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\StandardCharsets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */