package java.sql;

public class SQLTimeoutException
  extends SQLTransientException
{
  private static final long serialVersionUID = -4487171280562520262L;
  
  public SQLTimeoutException() {}
  
  public SQLTimeoutException(String paramString)
  {
    super(paramString);
  }
  
  public SQLTimeoutException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLTimeoutException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLTimeoutException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLTimeoutException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLTimeoutException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLTimeoutException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */