package java.util.concurrent;

public class RejectedExecutionException
  extends RuntimeException
{
  private static final long serialVersionUID = -375805702767069545L;
  
  public RejectedExecutionException() {}
  
  public RejectedExecutionException(String paramString)
  {
    super(paramString);
  }
  
  public RejectedExecutionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public RejectedExecutionException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\RejectedExecutionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */