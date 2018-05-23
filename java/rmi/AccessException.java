package java.rmi;

public class AccessException
  extends RemoteException
{
  private static final long serialVersionUID = 6314925228044966088L;
  
  public AccessException(String paramString)
  {
    super(paramString);
  }
  
  public AccessException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\AccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */