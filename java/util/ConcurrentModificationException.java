package java.util;

public class ConcurrentModificationException
  extends RuntimeException
{
  private static final long serialVersionUID = -3666751008965953603L;
  
  public ConcurrentModificationException() {}
  
  public ConcurrentModificationException(String paramString)
  {
    super(paramString);
  }
  
  public ConcurrentModificationException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public ConcurrentModificationException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ConcurrentModificationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */