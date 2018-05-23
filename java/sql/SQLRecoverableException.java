package java.sql;

public class SQLRecoverableException
  extends SQLException
{
  private static final long serialVersionUID = -4144386502923131579L;
  
  public SQLRecoverableException() {}
  
  public SQLRecoverableException(String paramString)
  {
    super(paramString);
  }
  
  public SQLRecoverableException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLRecoverableException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLRecoverableException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLRecoverableException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLRecoverableException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLRecoverableException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLRecoverableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */