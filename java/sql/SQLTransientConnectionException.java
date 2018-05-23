package java.sql;

public class SQLTransientConnectionException
  extends SQLTransientException
{
  private static final long serialVersionUID = -2520155553543391200L;
  
  public SQLTransientConnectionException() {}
  
  public SQLTransientConnectionException(String paramString)
  {
    super(paramString);
  }
  
  public SQLTransientConnectionException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLTransientConnectionException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLTransientConnectionException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLTransientConnectionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLTransientConnectionException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLTransientConnectionException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLTransientConnectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */