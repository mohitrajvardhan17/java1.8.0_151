package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import java.io.IOException;
import java.rmi.Remote;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import javax.security.auth.Subject;

public class RMIIIOPServerImpl
  extends RMIServerImpl
{
  private final Map<String, ?> env;
  private final AccessControlContext callerACC;
  
  public RMIIIOPServerImpl(Map<String, ?> paramMap)
    throws IOException
  {
    super(paramMap);
    env = (paramMap == null ? Collections.emptyMap() : paramMap);
    callerACC = AccessController.getContext();
  }
  
  protected void export()
    throws IOException
  {
    IIOPHelper.exportObject(this);
  }
  
  protected String getProtocol()
  {
    return "iiop";
  }
  
  public Remote toStub()
    throws IOException
  {
    Remote localRemote = IIOPHelper.toStub(this);
    return localRemote;
  }
  
  protected RMIConnection makeClient(String paramString, Subject paramSubject)
    throws IOException
  {
    if (paramString == null) {
      throw new NullPointerException("Null connectionId");
    }
    RMIConnectionImpl localRMIConnectionImpl = new RMIConnectionImpl(this, paramString, getDefaultClassLoader(), paramSubject, env);
    IIOPHelper.exportObject(localRMIConnectionImpl);
    return localRMIConnectionImpl;
  }
  
  protected void closeClient(RMIConnection paramRMIConnection)
    throws IOException
  {
    IIOPHelper.unexportObject(paramRMIConnection);
  }
  
  protected void closeServer()
    throws IOException
  {
    IIOPHelper.unexportObject(this);
  }
  
  RMIConnection doNewClient(final Object paramObject)
    throws IOException
  {
    if (callerACC == null) {
      throw new SecurityException("AccessControlContext cannot be null");
    }
    try
    {
      (RMIConnection)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public RMIConnection run()
          throws IOException
        {
          return superDoNewClient(paramObject);
        }
      }, callerACC);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getCause());
    }
  }
  
  RMIConnection superDoNewClient(Object paramObject)
    throws IOException
  {
    return super.doNewClient(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\RMIIIOPServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */