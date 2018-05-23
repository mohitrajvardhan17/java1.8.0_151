package java.util;

public class MissingResourceException
  extends RuntimeException
{
  private static final long serialVersionUID = -4876345176062000401L;
  private String className;
  private String key;
  
  public MissingResourceException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    className = paramString2;
    key = paramString3;
  }
  
  MissingResourceException(String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
  {
    super(paramString1, paramThrowable);
    className = paramString2;
    key = paramString3;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getKey()
  {
    return key;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\MissingResourceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */