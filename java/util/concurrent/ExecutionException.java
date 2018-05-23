package java.util.concurrent;

public class ExecutionException
  extends Exception
{
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected ExecutionException() {}
  
  protected ExecutionException(String paramString)
  {
    super(paramString);
  }
  
  public ExecutionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public ExecutionException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ExecutionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */