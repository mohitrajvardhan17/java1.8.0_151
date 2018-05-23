package java.rmi;

public class ConnectException
  extends RemoteException
{
  private static final long serialVersionUID = 4863550261346652506L;
  
  public ConnectException(String paramString)
  {
    super(paramString);
  }
  
  public ConnectException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\ConnectException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */