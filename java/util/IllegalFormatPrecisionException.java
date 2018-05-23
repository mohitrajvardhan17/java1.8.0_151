package java.util;

public class IllegalFormatPrecisionException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 18711008L;
  private int p;
  
  public IllegalFormatPrecisionException(int paramInt)
  {
    p = paramInt;
  }
  
  public int getPrecision()
  {
    return p;
  }
  
  public String getMessage()
  {
    return Integer.toString(p);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllegalFormatPrecisionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */