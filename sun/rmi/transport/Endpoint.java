package sun.rmi.transport;

import java.rmi.RemoteException;

public abstract interface Endpoint
{
  public abstract Channel getChannel();
  
  public abstract void exportObject(Target paramTarget)
    throws RemoteException;
  
  public abstract Transport getInboundTransport();
  
  public abstract Transport getOutboundTransport();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\Endpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */