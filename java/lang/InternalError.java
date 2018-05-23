package java.lang;

public class InternalError
  extends VirtualMachineError
{
  private static final long serialVersionUID = -9062593416125562365L;
  
  public InternalError() {}
  
  public InternalError(String paramString)
  {
    super(paramString);
  }
  
  public InternalError(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public InternalError(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\InternalError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */