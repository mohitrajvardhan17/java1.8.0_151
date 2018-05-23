package javax.rmi;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.util.Properties;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import org.omg.CORBA.INITIALIZE;

public class PortableRemoteObject
{
  private static final PortableRemoteObjectDelegate proDelegate = (PortableRemoteObjectDelegate)createDelegate("javax.rmi.CORBA.PortableRemoteObjectClass");
  private static final String PortableRemoteObjectClassKey = "javax.rmi.CORBA.PortableRemoteObjectClass";
  
  protected PortableRemoteObject()
    throws RemoteException
  {
    if (proDelegate != null) {
      exportObject((Remote)this);
    }
  }
  
  public static void exportObject(Remote paramRemote)
    throws RemoteException
  {
    if (proDelegate != null) {
      proDelegate.exportObject(paramRemote);
    }
  }
  
  public static Remote toStub(Remote paramRemote)
    throws NoSuchObjectException
  {
    if (proDelegate != null) {
      return proDelegate.toStub(paramRemote);
    }
    return null;
  }
  
  public static void unexportObject(Remote paramRemote)
    throws NoSuchObjectException
  {
    if (proDelegate != null) {
      proDelegate.unexportObject(paramRemote);
    }
  }
  
  public static Object narrow(Object paramObject, Class paramClass)
    throws ClassCastException
  {
    if (proDelegate != null) {
      return proDelegate.narrow(paramObject, paramClass);
    }
    return null;
  }
  
  public static void connect(Remote paramRemote1, Remote paramRemote2)
    throws RemoteException
  {
    if (proDelegate != null) {
      proDelegate.connect(paramRemote1, paramRemote2);
    }
  }
  
  private static Object createDelegate(String paramString)
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null)
    {
      Properties localProperties = getORBPropertiesFile();
      if (localProperties != null) {
        str = localProperties.getProperty(paramString);
      }
    }
    if (str == null) {
      return new com.sun.corba.se.impl.javax.rmi.PortableRemoteObject();
    }
    try
    {
      return loadDelegateClass(str).newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localINITIALIZE = new INITIALIZE("Cannot instantiate " + str);
      localINITIALIZE.initCause(localClassNotFoundException);
      throw localINITIALIZE;
    }
    catch (Exception localException)
    {
      INITIALIZE localINITIALIZE = new INITIALIZE("Error while instantiating" + str);
      localINITIALIZE.initCause(localException);
      throw localINITIALIZE;
    }
  }
  
  private static Class loadDelegateClass(String paramString)
    throws ClassNotFoundException
  {
    try
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      return Class.forName(paramString, false, localClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        return RMIClassLoader.loadClass(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        String str = "Could not load " + paramString + ": " + localMalformedURLException.toString();
        ClassNotFoundException localClassNotFoundException2 = new ClassNotFoundException(str);
        throw localClassNotFoundException2;
      }
    }
  }
  
  private static Properties getORBPropertiesFile()
  {
    return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\PortableRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */