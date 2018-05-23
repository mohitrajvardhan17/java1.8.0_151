package java.rmi;

public class UnmarshalException
  extends RemoteException
{
  private static final long serialVersionUID = 594380845140740218L;
  
  public UnmarshalException(String paramString)
  {
    super(paramString);
  }
  
  public UnmarshalException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\UnmarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */