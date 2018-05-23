package java.util;

public class IllegalFormatCodePointException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 19080630L;
  private int c;
  
  public IllegalFormatCodePointException(int paramInt)
  {
    c = paramInt;
  }
  
  public int getCodePoint()
  {
    return c;
  }
  
  public String getMessage()
  {
    return String.format("Code point = %#x", new Object[] { Integer.valueOf(c) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllegalFormatCodePointException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */