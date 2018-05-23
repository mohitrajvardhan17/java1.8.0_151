package java.sql;

public class SQLSyntaxErrorException
  extends SQLNonTransientException
{
  private static final long serialVersionUID = -1843832610477496053L;
  
  public SQLSyntaxErrorException() {}
  
  public SQLSyntaxErrorException(String paramString)
  {
    super(paramString);
  }
  
  public SQLSyntaxErrorException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLSyntaxErrorException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLSyntaxErrorException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLSyntaxErrorException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLSyntaxErrorException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLSyntaxErrorException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLSyntaxErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */