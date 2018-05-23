package java.lang;

public class Exception
  extends Throwable
{
  static final long serialVersionUID = -3387516993124229948L;
  
  public Exception() {}
  
  public Exception(String paramString)
  {
    super(paramString);
  }
  
  public Exception(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public Exception(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  protected Exception(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Exception.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */