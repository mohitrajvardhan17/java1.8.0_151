package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.omg.CORBA.SystemException;

public class ServerTableEntry
{
  private static final int DE_ACTIVATED = 0;
  private static final int ACTIVATING = 1;
  private static final int ACTIVATED = 2;
  private static final int RUNNING = 3;
  private static final int HELD_DOWN = 4;
  private static final long waitTime = 2000L;
  private static final int ActivationRetryMax = 5;
  private int state;
  private int serverId;
  private HashMap orbAndPortInfo;
  private Server serverObj;
  private ServerDef serverDef;
  private Process process;
  private int activateRetryCount = 0;
  private String activationCmd;
  private ActivationSystemException wrapper;
  private static String javaHome = System.getProperty("java.home");
  private static String classPath = System.getProperty("java.class.path");
  private static String fileSep = System.getProperty("file.separator");
  private static String pathSep = System.getProperty("path.separator");
  private boolean debug = false;
  
  private String printState()
  {
    String str = "UNKNOWN";
    switch (state)
    {
    case 0: 
      str = "DE_ACTIVATED";
      break;
    case 1: 
      str = "ACTIVATING  ";
      break;
    case 2: 
      str = "ACTIVATED   ";
      break;
    case 3: 
      str = "RUNNING     ";
      break;
    case 4: 
      str = "HELD_DOWN   ";
      break;
    }
    return str;
  }
  
  public String toString()
  {
    return "ServerTableEntry[state=" + printState() + " serverId=" + serverId + " activateRetryCount=" + activateRetryCount + "]";
  }
  
  ServerTableEntry(ActivationSystemException paramActivationSystemException, int paramInt1, ServerDef paramServerDef, int paramInt2, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    wrapper = paramActivationSystemException;
    serverId = paramInt1;
    serverDef = paramServerDef;
    debug = paramBoolean2;
    orbAndPortInfo = new HashMap(255);
    activateRetryCount = 0;
    state = 1;
    activationCmd = (javaHome + fileSep + "bin" + fileSep + "java " + serverVmArgs + " -Dioser=" + System.getProperty("ioser") + " -D" + "org.omg.CORBA.ORBInitialPort" + "=" + paramInt2 + " -D" + "com.sun.CORBA.activation.DbDir" + "=" + paramString + " -D" + "com.sun.CORBA.POA.ORBActivated" + "=true -D" + "com.sun.CORBA.POA.ORBServerId" + "=" + paramInt1 + " -D" + "com.sun.CORBA.POA.ORBServerName" + "=" + serverName + " " + (paramBoolean1 ? "-Dcom.sun.CORBA.activation.ORBServerVerify=true " : "") + "-classpath " + classPath + (serverClassPath.equals("") == true ? "" : pathSep) + serverClassPath + " com.sun.corba.se.impl.activation.ServerMain " + serverArgs + (paramBoolean2 ? " -debug" : ""));
    if (paramBoolean2) {
      System.out.println("ServerTableEntry constructed with activation command " + activationCmd);
    }
  }
  
  public int verify()
  {
    try
    {
      if (debug) {
        System.out.println("Server being verified w/" + activationCmd);
      }
      process = Runtime.getRuntime().exec(activationCmd);
      int i = process.waitFor();
      if (debug) {
        printDebug("verify", "returns " + ServerMain.printResult(i));
      }
      return i;
    }
    catch (Exception localException)
    {
      if (debug) {
        printDebug("verify", "returns unknown error because of exception " + localException);
      }
    }
    return 4;
  }
  
  private void printDebug(String paramString1, String paramString2)
  {
    System.out.println("ServerTableEntry: method  =" + paramString1);
    System.out.println("ServerTableEntry: server  =" + serverId);
    System.out.println("ServerTableEntry: state   =" + printState());
    System.out.println("ServerTableEntry: message =" + paramString2);
    System.out.println();
  }
  
  synchronized void activate()
    throws SystemException
  {
    state = 2;
    try
    {
      if (debug) {
        printDebug("activate", "activating server");
      }
      process = Runtime.getRuntime().exec(activationCmd);
    }
    catch (Exception localException)
    {
      deActivate();
      if (debug) {
        printDebug("activate", "throwing premature process exit");
      }
      throw wrapper.unableToStartProcess();
    }
  }
  
  synchronized void register(Server paramServer)
  {
    if (state == 2)
    {
      serverObj = paramServer;
      if (debug) {
        printDebug("register", "process registered back");
      }
    }
    else
    {
      if (debug) {
        printDebug("register", "throwing premature process exit");
      }
      throw wrapper.serverNotExpectedToRegister();
    }
  }
  
  synchronized void registerPorts(String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
    throws ORBAlreadyRegistered
  {
    if (orbAndPortInfo.containsKey(paramString)) {
      throw new ORBAlreadyRegistered(paramString);
    }
    int i = paramArrayOfEndPointInfo.length;
    EndPointInfo[] arrayOfEndPointInfo = new EndPointInfo[i];
    for (int j = 0; j < i; j++)
    {
      arrayOfEndPointInfo[j] = new EndPointInfo(endpointType, port);
      if (debug) {
        System.out.println("registering type: " + endpointType + "  port  " + port);
      }
    }
    orbAndPortInfo.put(paramString, arrayOfEndPointInfo);
    if (state == 2)
    {
      state = 3;
      notifyAll();
    }
    if (debug) {
      printDebug("registerPorts", "process registered Ports");
    }
  }
  
  void install()
  {
    Server localServer = null;
    synchronized (this)
    {
      if (state == 3) {
        localServer = serverObj;
      } else {
        throw wrapper.serverNotRunning();
      }
    }
    if (localServer != null) {
      localServer.install();
    }
  }
  
  void uninstall()
  {
    Server localServer = null;
    Process localProcess = null;
    synchronized (this)
    {
      localServer = serverObj;
      localProcess = process;
      if (state == 3) {
        deActivate();
      } else {
        throw wrapper.serverNotRunning();
      }
    }
    try
    {
      if (localServer != null)
      {
        localServer.shutdown();
        localServer.uninstall();
      }
      if (localProcess != null) {
        localProcess.destroy();
      }
    }
    catch (Exception localException) {}
  }
  
  synchronized void holdDown()
  {
    state = 4;
    if (debug) {
      printDebug("holdDown", "server held down");
    }
    notifyAll();
  }
  
  synchronized void deActivate()
  {
    state = 0;
    if (debug) {
      printDebug("deActivate", "server deactivated");
    }
    notifyAll();
  }
  
  synchronized void checkProcessHealth()
  {
    if (state == 3)
    {
      try
      {
        int i = process.exitValue();
      }
      catch (IllegalThreadStateException localIllegalThreadStateException)
      {
        return;
      }
      synchronized (this)
      {
        orbAndPortInfo.clear();
        deActivate();
      }
    }
  }
  
  synchronized boolean isValid()
  {
    if ((state == 1) || (state == 4))
    {
      if (debug) {
        printDebug("isValid", "returns true");
      }
      return true;
    }
    try
    {
      int i = process.exitValue();
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
      return true;
    }
    if (state == 2)
    {
      if (activateRetryCount < 5)
      {
        if (debug) {
          printDebug("isValid", "reactivating server");
        }
        activateRetryCount += 1;
        activate();
        return true;
      }
      if (debug) {
        printDebug("isValid", "holding server down");
      }
      holdDown();
      return true;
    }
    deActivate();
    return false;
  }
  
  synchronized ORBPortInfo[] lookup(String paramString)
    throws ServerHeldDown
  {
    while ((state == 1) || (state == 2)) {
      try
      {
        wait(2000L);
        if (!isValid()) {
          break;
        }
      }
      catch (Exception localException) {}
    }
    ORBPortInfo[] arrayOfORBPortInfo = null;
    if (state == 3)
    {
      arrayOfORBPortInfo = new ORBPortInfo[orbAndPortInfo.size()];
      Iterator localIterator = orbAndPortInfo.keySet().iterator();
      try
      {
        for (int i = 0; localIterator.hasNext(); i++)
        {
          String str = (String)localIterator.next();
          EndPointInfo[] arrayOfEndPointInfo = (EndPointInfo[])orbAndPortInfo.get(str);
          int k = -1;
          for (int j = 0; j < arrayOfEndPointInfo.length; j++)
          {
            if (debug) {
              System.out.println("lookup num-ports " + arrayOfEndPointInfo.length + "   " + endpointType + "   " + port);
            }
            if (endpointType.equals(paramString))
            {
              k = port;
              break;
            }
          }
          arrayOfORBPortInfo[i] = new ORBPortInfo(str, k);
        }
      }
      catch (NoSuchElementException localNoSuchElementException) {}
      return arrayOfORBPortInfo;
    }
    if (debug) {
      printDebug("lookup", "throwing server held down error");
    }
    throw new ServerHeldDown(serverId);
  }
  
  synchronized EndPointInfo[] lookupForORB(String paramString)
    throws ServerHeldDown, InvalidORBid
  {
    while ((state == 1) || (state == 2)) {
      try
      {
        wait(2000L);
        if (!isValid()) {
          break;
        }
      }
      catch (Exception localException) {}
    }
    EndPointInfo[] arrayOfEndPointInfo1 = null;
    if (state == 3)
    {
      try
      {
        EndPointInfo[] arrayOfEndPointInfo2 = (EndPointInfo[])orbAndPortInfo.get(paramString);
        arrayOfEndPointInfo1 = new EndPointInfo[arrayOfEndPointInfo2.length];
        for (int i = 0; i < arrayOfEndPointInfo2.length; i++)
        {
          if (debug) {
            System.out.println("lookup num-ports " + arrayOfEndPointInfo2.length + "   " + endpointType + "   " + port);
          }
          arrayOfEndPointInfo1[i] = new EndPointInfo(endpointType, port);
        }
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        throw new InvalidORBid();
      }
      return arrayOfEndPointInfo1;
    }
    if (debug) {
      printDebug("lookup", "throwing server held down error");
    }
    throw new ServerHeldDown(serverId);
  }
  
  synchronized String[] getORBList()
  {
    String[] arrayOfString = new String[orbAndPortInfo.size()];
    Iterator localIterator = orbAndPortInfo.keySet().iterator();
    try
    {
      int i = 0;
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        arrayOfString[(i++)] = str;
      }
    }
    catch (NoSuchElementException localNoSuchElementException) {}
    return arrayOfString;
  }
  
  int getServerId()
  {
    return serverId;
  }
  
  boolean isActive()
  {
    return (state == 3) || (state == 2);
  }
  
  synchronized void destroy()
  {
    Server localServer = null;
    Process localProcess = null;
    synchronized (this)
    {
      localServer = serverObj;
      localProcess = process;
      deActivate();
    }
    try
    {
      if (localServer != null) {
        localServer.shutdown();
      }
      if (debug) {
        printDebug("destroy", "server shutdown successfully");
      }
    }
    catch (Exception localException1)
    {
      if (debug) {
        printDebug("destroy", "server shutdown threw exception" + localException1);
      }
    }
    try
    {
      if (localProcess != null) {
        localProcess.destroy();
      }
      if (debug) {
        printDebug("destroy", "process destroyed successfully");
      }
    }
    catch (Exception localException2)
    {
      if (debug) {
        printDebug("destroy", "process destroy threw exception" + localException2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ServerTableEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */