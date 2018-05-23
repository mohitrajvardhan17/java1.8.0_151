package java.rmi.activation;

import java.rmi.RemoteException;

public class ActivateFailedException
  extends RemoteException
{
  private static final long serialVersionUID = 4863550261346652506L;
  
  public ActivateFailedException(String paramString)
  {
    super(paramString);
  }
  
  public ActivateFailedException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivateFailedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */