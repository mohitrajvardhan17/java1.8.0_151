package java.util;

public class DuplicateFormatFlagsException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 18890531L;
  private String flags;
  
  public DuplicateFormatFlagsException(String paramString)
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
    return String.format("Flags = '%s'", new Object[] { flags });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\DuplicateFormatFlagsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */