package java.rmi.server;

import java.rmi.RemoteException;

public class ExportException
  extends RemoteException
{
  private static final long serialVersionUID = -9155485338494060170L;
  
  public ExportException(String paramString)
  {
    super(paramString);
  }
  
  public ExportException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\ExportException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */