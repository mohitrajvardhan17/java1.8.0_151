package java.rmi;

public class UnknownHostException
  extends RemoteException
{
  private static final long serialVersionUID = -8152710247442114228L;
  
  public UnknownHostException(String paramString)
  {
    super(paramString);
  }
  
  public UnknownHostException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\UnknownHostException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */