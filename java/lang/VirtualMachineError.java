package java.lang;

public abstract class VirtualMachineError
  extends Error
{
  private static final long serialVersionUID = 4161983926571568670L;
  
  public VirtualMachineError() {}
  
  public VirtualMachineError(String paramString)
  {
    super(paramString);
  }
  
  public VirtualMachineError(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public VirtualMachineError(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\VirtualMachineError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */