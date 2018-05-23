package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.RMIExporter;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.DeserializationChecker;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;

public class RMIJRMPServerImpl
  extends RMIServerImpl
{
  private final ExportedWrapper exportedWrapper;
  private final int port;
  private final RMIClientSocketFactory csf;
  private final RMIServerSocketFactory ssf;
  private final Map<String, ?> env;
  
  public RMIJRMPServerImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, Map<String, ?> paramMap)
    throws IOException
  {
    super(paramMap);
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative port: " + paramInt);
    }
    port = paramInt;
    csf = paramRMIClientSocketFactory;
    ssf = paramRMIServerSocketFactory;
    env = (paramMap == null ? Collections.emptyMap() : paramMap);
    String[] arrayOfString1 = (String[])env.get("jmx.remote.rmi.server.credential.types");
    ArrayList localArrayList = null;
    if (arrayOfString1 != null)
    {
      localArrayList = new ArrayList();
      for (String str : arrayOfString1)
      {
        if (str == null) {
          throw new IllegalArgumentException("A credential type is null.");
        }
        ReflectUtil.checkPackageAccess(str);
        localArrayList.add(str);
      }
    }
    exportedWrapper = (localArrayList != null ? new ExportedWrapper(this, localArrayList, null) : null);
  }
  
  protected void export()
    throws IOException
  {
    if (exportedWrapper != null) {
      export(exportedWrapper);
    } else {
      export(this);
    }
  }
  
  private void export(Remote paramRemote)
    throws RemoteException
  {
    RMIExporter localRMIExporter = (RMIExporter)env.get("com.sun.jmx.remote.rmi.exporter");
    boolean bool = EnvHelp.isServerDaemon(env);
    if ((bool) && (localRMIExporter != null)) {
      throw new IllegalArgumentException("If jmx.remote.x.daemon is specified as true, com.sun.jmx.remote.rmi.exporter cannot be used to specify an exporter!");
    }
    if (bool)
    {
      if ((csf == null) && (ssf == null)) {
        new UnicastServerRef(port).exportObject(paramRemote, null, true);
      } else {
        new UnicastServerRef2(port, csf, ssf).exportObject(paramRemote, null, true);
      }
    }
    else if (localRMIExporter != null) {
      localRMIExporter.exportObject(paramRemote, port, csf, ssf);
    } else {
      UnicastRemoteObject.exportObject(paramRemote, port, csf, ssf);
    }
  }
  
  private void unexport(Remote paramRemote, boolean paramBoolean)
    throws NoSuchObjectException
  {
    RMIExporter localRMIExporter = (RMIExporter)env.get("com.sun.jmx.remote.rmi.exporter");
    if (localRMIExporter == null) {
      UnicastRemoteObject.unexportObject(paramRemote, paramBoolean);
    } else {
      localRMIExporter.unexportObject(paramRemote, paramBoolean);
    }
  }
  
  protected String getProtocol()
  {
    return "rmi";
  }
  
  public Remote toStub()
    throws IOException
  {
    if (exportedWrapper != null) {
      return RemoteObject.toStub(exportedWrapper);
    }
    return RemoteObject.toStub(this);
  }
  
  protected RMIConnection makeClient(String paramString, Subject paramSubject)
    throws IOException
  {
    if (paramString == null) {
      throw new NullPointerException("Null connectionId");
    }
    RMIConnectionImpl localRMIConnectionImpl = new RMIConnectionImpl(this, paramString, getDefaultClassLoader(), paramSubject, env);
    export(localRMIConnectionImpl);
    return localRMIConnectionImpl;
  }
  
  protected void closeClient(RMIConnection paramRMIConnection)
    throws IOException
  {
    unexport(paramRMIConnection, true);
  }
  
  protected void closeServer()
    throws IOException
  {
    if (exportedWrapper != null) {
      unexport(exportedWrapper, true);
    } else {
      unexport(this, true);
    }
  }
  
  private static class ExportedWrapper
    implements RMIServer, DeserializationChecker
  {
    private final RMIServer impl;
    private final List<String> allowedTypes;
    
    private ExportedWrapper(RMIServer paramRMIServer, List<String> paramList)
    {
      impl = paramRMIServer;
      allowedTypes = paramList;
    }
    
    public String getVersion()
      throws RemoteException
    {
      return impl.getVersion();
    }
    
    public RMIConnection newClient(Object paramObject)
      throws IOException
    {
      return impl.newClient(paramObject);
    }
    
    public void check(Method paramMethod, ObjectStreamClass paramObjectStreamClass, int paramInt1, int paramInt2)
    {
      String str = paramObjectStreamClass.getName();
      if (!allowedTypes.contains(str)) {
        throw new ClassCastException("Unsupported type: " + str);
      }
    }
    
    public void checkProxyClass(Method paramMethod, String[] paramArrayOfString, int paramInt1, int paramInt2)
    {
      if ((paramArrayOfString != null) && (paramArrayOfString.length > 0)) {
        for (String str : paramArrayOfString) {
          if (!allowedTypes.contains(str)) {
            throw new ClassCastException("Unsupported type: " + str);
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\RMIJRMPServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */