package java.sql;

public class SQLTransactionRollbackException
  extends SQLTransientException
{
  private static final long serialVersionUID = 5246680841170837229L;
  
  public SQLTransactionRollbackException() {}
  
  public SQLTransactionRollbackException(String paramString)
  {
    super(paramString);
  }
  
  public SQLTransactionRollbackException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLTransactionRollbackException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLTransactionRollbackException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLTransactionRollbackException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLTransactionRollbackException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLTransactionRollbackException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLTransactionRollbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */