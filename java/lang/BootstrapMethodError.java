package java.lang;

public class BootstrapMethodError
  extends LinkageError
{
  private static final long serialVersionUID = 292L;
  
  public BootstrapMethodError() {}
  
  public BootstrapMethodError(String paramString)
  {
    super(paramString);
  }
  
  public BootstrapMethodError(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public BootstrapMethodError(Throwable paramThrowable)
  {
    super(paramThrowable == null ? null : paramThrowable.toString());
    initCause(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\BootstrapMethodError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */