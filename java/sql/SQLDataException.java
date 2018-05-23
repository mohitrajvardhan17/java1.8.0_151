package java.sql;

public class SQLDataException
  extends SQLNonTransientException
{
  private static final long serialVersionUID = -6889123282670549800L;
  
  public SQLDataException() {}
  
  public SQLDataException(String paramString)
  {
    super(paramString);
  }
  
  public SQLDataException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLDataException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLDataException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLDataException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLDataException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLDataException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */