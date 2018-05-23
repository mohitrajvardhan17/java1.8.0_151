package java.rmi;

public class ServerError
  extends RemoteException
{
  private static final long serialVersionUID = 8455284893909696482L;
  
  public ServerError(String paramString, Error paramError)
  {
    super(paramString, paramError);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\ServerError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */