package java.rmi;

public class MarshalException
  extends RemoteException
{
  private static final long serialVersionUID = 6223554758134037936L;
  
  public MarshalException(String paramString)
  {
    super(paramString);
  }
  
  public MarshalException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\MarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */