package java.util;

public class IllegalFormatWidthException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 16660902L;
  private int w;
  
  public IllegalFormatWidthException(int paramInt)
  {
    w = paramInt;
  }
  
  public int getWidth()
  {
    return w;
  }
  
  public String getMessage()
  {
    return Integer.toString(w);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllegalFormatWidthException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */