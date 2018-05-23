package java.util;

public class IllformedLocaleException
  extends RuntimeException
{
  private static final long serialVersionUID = -5245986824925681401L;
  private int _errIdx = -1;
  
  public IllformedLocaleException() {}
  
  public IllformedLocaleException(String paramString)
  {
    super(paramString);
  }
  
  public IllformedLocaleException(String paramString, int paramInt)
  {
    super(paramString + (paramInt < 0 ? "" : new StringBuilder().append(" [at index ").append(paramInt).append("]").toString()));
    _errIdx = paramInt;
  }
  
  public int getErrorIndex()
  {
    return _errIdx;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllformedLocaleException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */