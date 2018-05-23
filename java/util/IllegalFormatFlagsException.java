package java.util;

public class IllegalFormatFlagsException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 790824L;
  private String flags;
  
  public IllegalFormatFlagsException(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    flags = paramString;
  }
  
  public String getFlags()
  {
    return flags;
  }
  
  public String getMessage()
  {
    return "Flags = '" + flags + "'";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllegalFormatFlagsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */