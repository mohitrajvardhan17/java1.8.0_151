package java.util.concurrent;

public class CompletionException
  extends RuntimeException
{
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected CompletionException() {}
  
  protected CompletionException(String paramString)
  {
    super(paramString);
  }
  
  public CompletionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public CompletionException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CompletionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */