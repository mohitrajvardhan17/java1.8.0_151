package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation._ServerImplBase;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;

class ServerCallback
  extends _ServerImplBase
{
  private ORB orb;
  private transient Method installMethod;
  private transient Method uninstallMethod;
  private transient Method shutdownMethod;
  private Object[] methodArgs;
  
  ServerCallback(ORB paramORB, Method paramMethod1, Method paramMethod2, Method paramMethod3)
  {
    orb = paramORB;
    installMethod = paramMethod1;
    uninstallMethod = paramMethod2;
    shutdownMethod = paramMethod3;
    paramORB.connect(this);
    methodArgs = new Object[] { paramORB };
  }
  
  private void invokeMethod(Method paramMethod)
  {
    if (paramMethod != null) {
      try
      {
        paramMethod.invoke(null, methodArgs);
      }
      catch (Exception localException)
      {
        ServerMain.logError("could not invoke " + paramMethod.getName() + " method: " + localException.getMessage());
      }
    }
  }
  
  public void shutdown()
  {
    ServerMain.logInformation("Shutdown starting");
    invokeMethod(shutdownMethod);
    orb.shutdown(true);
    ServerMain.logTerminal("Shutdown completed", 0);
  }
  
  public void install()
  {
    ServerMain.logInformation("Install starting");
    invokeMethod(installMethod);
    ServerMain.logInformation("Install completed");
  }
  
  public void uninstall()
  {
    ServerMain.logInformation("uninstall starting");
    invokeMethod(uninstallMethod);
    ServerMain.logInformation("uninstall completed");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ServerCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */