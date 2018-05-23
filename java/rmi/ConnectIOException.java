package java.rmi;

public class ConnectIOException
  extends RemoteException
{
  private static final long serialVersionUID = -8087809532704668744L;
  
  public ConnectIOException(String paramString)
  {
    super(paramString);
  }
  
  public ConnectIOException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\ConnectIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */