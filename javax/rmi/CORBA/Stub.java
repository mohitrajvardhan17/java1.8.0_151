package javax.rmi.CORBA;

import com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public abstract class Stub
  extends ObjectImpl
  implements Serializable
{
  private static final long serialVersionUID = 1087775603798577179L;
  private transient StubDelegate stubDelegate = null;
  private static Class stubDelegateClass = null;
  private static final String StubClassKey = "javax.rmi.CORBA.StubClass";
  
  public Stub() {}
  
  public int hashCode()
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null) {
      return stubDelegate.hashCode(this);
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null) {
      return stubDelegate.equals(this, paramObject);
    }
    return false;
  }
  
  public String toString()
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null)
    {
      String str = stubDelegate.toString(this);
      if (str == null) {
        return super.toString();
      }
      return str;
    }
    return super.toString();
  }
  
  public void connect(ORB paramORB)
    throws RemoteException
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null) {
      stubDelegate.connect(this, paramORB);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null) {
      stubDelegate.readObject(this, paramObjectInputStream);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (stubDelegate == null) {
      setDefaultDelegate();
    }
    if (stubDelegate != null) {
      stubDelegate.writeObject(this, paramObjectOutputStream);
    }
  }
  
  private void setDefaultDelegate()
  {
    if (stubDelegateClass != null) {
      try
      {
        stubDelegate = ((StubDelegate)stubDelegateClass.newInstance());
      }
      catch (Exception localException) {}
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
      return new StubDelegateImpl();
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
  
  static
  {
    Object localObject = createDelegate("javax.rmi.CORBA.StubClass");
    if (localObject != null) {
      stubDelegateClass = localObject.getClass();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\CORBA\Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */