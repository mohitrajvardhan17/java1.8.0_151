package java.sql;

public class SQLTransientException
  extends SQLException
{
  private static final long serialVersionUID = -9042733978262274539L;
  
  public SQLTransientException() {}
  
  public SQLTransientException(String paramString)
  {
    super(paramString);
  }
  
  public SQLTransientException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLTransientException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLTransientException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLTransientException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLTransientException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLTransientException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLTransientException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */