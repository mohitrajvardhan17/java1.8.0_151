package sun.rmi.server;

import com.sun.rmi.rmid.ExecOptionPermission;
import com.sun.rmi.rmid.ExecPermission;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.Activator;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.rmi.log.LogHandler;
import sun.rmi.log.ReliableLog;
import sun.rmi.registry.RegistryImpl;
import sun.rmi.transport.LiveRef;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;
import sun.security.provider.PolicyFile;

public class Activation
  implements Serializable
{
  private static final long serialVersionUID = 2921265612698155191L;
  private static final byte MAJOR_VERSION = 1;
  private static final byte MINOR_VERSION = 0;
  private static Object execPolicy;
  private static Method execPolicyMethod;
  private static boolean debugExec;
  private Map<ActivationID, ActivationGroupID> idTable = new ConcurrentHashMap();
  private Map<ActivationGroupID, GroupEntry> groupTable = new ConcurrentHashMap();
  private byte majorVersion = 1;
  private byte minorVersion = 0;
  private transient int groupSemaphore;
  private transient int groupCounter;
  private transient ReliableLog log;
  private transient int numUpdates;
  private transient String[] command;
  private static final long groupTimeout = getInt("sun.rmi.activation.groupTimeout", 60000);
  private static final int snapshotInterval = getInt("sun.rmi.activation.snapshotInterval", 200);
  private static final long execTimeout = getInt("sun.rmi.activation.execTimeout", 30000);
  private static final Object initLock = new Object();
  private static boolean initDone = false;
  private transient Activator activator;
  private transient Activator activatorStub;
  private transient ActivationSystem system;
  private transient ActivationSystem systemStub;
  private transient ActivationMonitor monitor;
  private transient Registry registry;
  private volatile transient boolean shuttingDown = false;
  private volatile transient Object startupLock;
  private transient Thread shutdownHook;
  private static ResourceBundle resources = null;
  
  private static int getInt(String paramString, int paramInt)
  {
    return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue();
  }
  
  private Activation() {}
  
  private static void startActivation(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, String paramString, String[] paramArrayOfString)
    throws Exception
  {
    ReliableLog localReliableLog = new ReliableLog(paramString, new ActLogHandler());
    Activation localActivation = (Activation)localReliableLog.recover();
    localActivation.init(paramInt, paramRMIServerSocketFactory, localReliableLog, paramArrayOfString);
  }
  
  private void init(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, ReliableLog paramReliableLog, String[] paramArrayOfString)
    throws Exception
  {
    log = paramReliableLog;
    numUpdates = 0;
    shutdownHook = new ShutdownHook();
    groupSemaphore = getInt("sun.rmi.activation.groupThrottle", 3);
    groupCounter = 0;
    Runtime.getRuntime().addShutdownHook(shutdownHook);
    ActivationGroupID[] arrayOfActivationGroupID = (ActivationGroupID[])groupTable.keySet().toArray(new ActivationGroupID[0]);
    synchronized (startupLock = new Object())
    {
      activator = new ActivatorImpl(paramInt, paramRMIServerSocketFactory);
      activatorStub = ((Activator)RemoteObject.toStub(activator));
      system = new ActivationSystemImpl(paramInt, paramRMIServerSocketFactory);
      systemStub = ((ActivationSystem)RemoteObject.toStub(system));
      monitor = new ActivationMonitorImpl(paramInt, paramRMIServerSocketFactory);
      initCommand(paramArrayOfString);
      registry = new SystemRegistryImpl(paramInt, null, paramRMIServerSocketFactory, systemStub);
      if (paramRMIServerSocketFactory != null) {
        synchronized (initLock)
        {
          initDone = true;
          initLock.notifyAll();
        }
      }
    }
    startupLock = null;
    int i = arrayOfActivationGroupID.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      try
      {
        getGroupEntry(arrayOfActivationGroupID[i]).restartServices();
      }
      catch (UnknownGroupException localUnknownGroupException)
      {
        System.err.println(getTextResource("rmid.restart.group.warning"));
        localUnknownGroupException.printStackTrace();
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (!(groupTable instanceof ConcurrentHashMap)) {
      groupTable = new ConcurrentHashMap(groupTable);
    }
    if (!(idTable instanceof ConcurrentHashMap)) {
      idTable = new ConcurrentHashMap(idTable);
    }
  }
  
  /* Error */
  private void checkShutdown()
    throws ActivationException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 664	sun/rmi/server/Activation:startupLock	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnull +17 -> 23
    //   9: aload_1
    //   10: dup
    //   11: astore_2
    //   12: monitorenter
    //   13: aload_2
    //   14: monitorexit
    //   15: goto +8 -> 23
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    //   23: aload_0
    //   24: getfield 661	sun/rmi/server/Activation:shuttingDown	Z
    //   27: iconst_1
    //   28: if_icmpne +13 -> 41
    //   31: new 396	java/rmi/activation/ActivationException
    //   34: dup
    //   35: ldc 15
    //   37: invokespecial 714	java/rmi/activation/ActivationException:<init>	(Ljava/lang/String;)V
    //   40: athrow
    //   41: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	Activation
    //   4	6	1	localObject1	Object
    //   11	9	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	15	18	finally
    //   18	21	18	finally
  }
  
  private static void unexport(Remote paramRemote)
  {
    try
    {
      while (UnicastRemoteObject.unexportObject(paramRemote, false) != true) {
        Thread.sleep(100L);
      }
    }
    catch (Exception localException) {}
  }
  
  private ActivationGroupID getGroupID(ActivationID paramActivationID)
    throws UnknownObjectException
  {
    ActivationGroupID localActivationGroupID = (ActivationGroupID)idTable.get(paramActivationID);
    if (localActivationGroupID != null) {
      return localActivationGroupID;
    }
    throw new UnknownObjectException("unknown object: " + paramActivationID);
  }
  
  private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID, boolean paramBoolean)
    throws UnknownGroupException
  {
    if (paramActivationGroupID.getClass() == ActivationGroupID.class)
    {
      GroupEntry localGroupEntry;
      if (paramBoolean) {
        localGroupEntry = (GroupEntry)groupTable.remove(paramActivationGroupID);
      } else {
        localGroupEntry = (GroupEntry)groupTable.get(paramActivationGroupID);
      }
      if ((localGroupEntry != null) && (!removed)) {
        return localGroupEntry;
      }
    }
    throw new UnknownGroupException("group unknown");
  }
  
  private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID)
    throws UnknownGroupException
  {
    return getGroupEntry(paramActivationGroupID, false);
  }
  
  private GroupEntry removeGroupEntry(ActivationGroupID paramActivationGroupID)
    throws UnknownGroupException
  {
    return getGroupEntry(paramActivationGroupID, true);
  }
  
  private GroupEntry getGroupEntry(ActivationID paramActivationID)
    throws UnknownObjectException
  {
    ActivationGroupID localActivationGroupID = getGroupID(paramActivationID);
    GroupEntry localGroupEntry = (GroupEntry)groupTable.get(localActivationGroupID);
    if ((localGroupEntry != null) && (!removed)) {
      return localGroupEntry;
    }
    throw new UnknownObjectException("object's group removed");
  }
  
  private String[] activationArgs(ActivationGroupDesc paramActivationGroupDesc)
  {
    ActivationGroupDesc.CommandEnvironment localCommandEnvironment = paramActivationGroupDesc.getCommandEnvironment();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add((localCommandEnvironment != null) && (localCommandEnvironment.getCommandPath() != null) ? localCommandEnvironment.getCommandPath() : command[0]);
    if ((localCommandEnvironment != null) && (localCommandEnvironment.getCommandOptions() != null)) {
      localArrayList.addAll(Arrays.asList(localCommandEnvironment.getCommandOptions()));
    }
    Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
    if (localProperties != null)
    {
      Enumeration localEnumeration = localProperties.propertyNames();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        localArrayList.add("-D" + str + "=" + localProperties.getProperty(str));
      }
    }
    for (int i = 1; i < command.length; i++) {
      localArrayList.add(command[i]);
    }
    String[] arrayOfString = new String[localArrayList.size()];
    System.arraycopy(localArrayList.toArray(), 0, arrayOfString, 0, arrayOfString.length);
    return arrayOfString;
  }
  
  private void checkArgs(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
    throws SecurityException, ActivationException
  {
    if (execPolicyMethod != null)
    {
      if (paramArrayOfString == null) {
        paramArrayOfString = activationArgs(paramActivationGroupDesc);
      }
      try
      {
        execPolicyMethod.invoke(execPolicy, new Object[] { paramActivationGroupDesc, paramArrayOfString });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getTargetException();
        if ((localThrowable instanceof SecurityException)) {
          throw ((SecurityException)localThrowable);
        }
        throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localInvocationTargetException);
      }
      catch (Exception localException)
      {
        throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localException);
      }
    }
  }
  
  private void addLogRecord(LogRecord paramLogRecord)
    throws ActivationException
  {
    synchronized (log)
    {
      checkShutdown();
      try
      {
        log.update(paramLogRecord, true);
      }
      catch (Exception localException1)
      {
        numUpdates = snapshotInterval;
        System.err.println(getTextResource("rmid.log.update.warning"));
        localException1.printStackTrace();
      }
      if (++numUpdates < snapshotInterval) {
        return;
      }
      try
      {
        log.snapshot(this);
        numUpdates = 0;
      }
      catch (Exception localException2)
      {
        System.err.println(getTextResource("rmid.log.snapshot.warning"));
        localException2.printStackTrace();
        try
        {
          system.shutdown();
        }
        catch (RemoteException localRemoteException) {}
        throw new ActivationException("log snapshot failed", localException2);
      }
    }
  }
  
  private void initCommand(String[] paramArrayOfString)
  {
    command = new String[paramArrayOfString.length + 2];
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          command[0] = (System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        }
        catch (Exception localException)
        {
          System.err.println(Activation.getTextResource("rmid.unfound.java.home.property"));
          command[0] = "java";
        }
        return null;
      }
    });
    System.arraycopy(paramArrayOfString, 0, command, 1, paramArrayOfString.length);
    command[(command.length - 1)] = "sun.rmi.server.ActivationGroupInit";
  }
  
  private static void bomb(String paramString)
  {
    System.err.println("rmid: " + paramString);
    System.err.println(MessageFormat.format(getTextResource("rmid.usage"), new Object[] { "rmid" }));
    System.exit(1);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    int i = 0;
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
    try
    {
      Exception localException1 = 1098;
      ActivationServerSocketFactory localActivationServerSocketFactory = null;
      Channel localChannel = (Channel)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Channel run()
          throws IOException
        {
          return System.inheritedChannel();
        }
      });
      if ((localChannel != null) && ((localChannel instanceof ServerSocketChannel)))
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws IOException
          {
            File localFile = Files.createTempFile("rmid-err", null, new FileAttribute[0]).toFile();
            PrintStream localPrintStream = new PrintStream(new FileOutputStream(localFile));
            System.setErr(localPrintStream);
            return null;
          }
        });
        localObject = ((ServerSocketChannel)localChannel).socket();
        localException1 = ((ServerSocket)localObject).getLocalPort();
        localActivationServerSocketFactory = new ActivationServerSocketFactory((ServerSocket)localObject);
        System.err.println(new Date());
        System.err.println(getTextResource("rmid.inherited.channel.info") + ": " + localChannel);
      }
      Object localObject = null;
      ArrayList localArrayList = new ArrayList();
      for (int j = 0; j < paramArrayOfString.length; j++) {
        if (paramArrayOfString[j].equals("-port"))
        {
          if (localActivationServerSocketFactory != null) {
            bomb(getTextResource("rmid.syntax.port.badarg"));
          }
          if (j + 1 < paramArrayOfString.length) {
            try
            {
              localException1 = Integer.parseInt(paramArrayOfString[(++j)]);
            }
            catch (NumberFormatException localNumberFormatException)
            {
              bomb(getTextResource("rmid.syntax.port.badnumber"));
            }
          } else {
            bomb(getTextResource("rmid.syntax.port.missing"));
          }
        }
        else if (paramArrayOfString[j].equals("-log"))
        {
          if (j + 1 < paramArrayOfString.length) {
            localObject = paramArrayOfString[(++j)];
          } else {
            bomb(getTextResource("rmid.syntax.log.missing"));
          }
        }
        else if (paramArrayOfString[j].equals("-stop"))
        {
          i = 1;
        }
        else if (paramArrayOfString[j].startsWith("-C"))
        {
          localArrayList.add(paramArrayOfString[j].substring(2));
        }
        else
        {
          bomb(MessageFormat.format(getTextResource("rmid.syntax.illegal.option"), new Object[] { paramArrayOfString[j] }));
        }
      }
      if (localObject == null) {
        if (localActivationServerSocketFactory != null) {
          bomb(getTextResource("rmid.syntax.log.required"));
        } else {
          localObject = "log";
        }
      }
      debugExec = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.activation.debugExec"))).booleanValue();
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.activation.execPolicy", null));
      if (str == null)
      {
        if (i == 0) {
          DefaultExecPolicy.checkConfiguration();
        }
        str = "default";
      }
      if (!str.equals("none"))
      {
        if ((str.equals("")) || (str.equals("default"))) {
          str = DefaultExecPolicy.class.getName();
        }
        try
        {
          Class localClass = getRMIClass(str);
          execPolicy = localClass.newInstance();
          execPolicyMethod = localClass.getMethod("checkExecCommand", new Class[] { ActivationGroupDesc.class, String[].class });
        }
        catch (Exception localException3)
        {
          if (debugExec)
          {
            System.err.println(getTextResource("rmid.exec.policy.exception"));
            localException3.printStackTrace();
          }
          bomb(getTextResource("rmid.exec.policy.invalid"));
        }
      }
      if (i == 1)
      {
        localException3 = localException1;
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            System.setProperty("java.rmi.activation.port", Integer.toString(val$finalPort));
            return null;
          }
        });
        ActivationSystem localActivationSystem = ActivationGroup.getSystem();
        localActivationSystem.shutdown();
        System.exit(0);
      }
      startActivation(localException1, localActivationServerSocketFactory, (String)localObject, (String[])localArrayList.toArray(new String[localArrayList.size()]));
      try
      {
        for (;;)
        {
          Thread.sleep(Long.MAX_VALUE);
        }
      }
      catch (InterruptedException localInterruptedException) {}
    }
    catch (Exception localException2)
    {
      System.err.println(MessageFormat.format(getTextResource("rmid.unexpected.exception"), new Object[] { localException2 }));
      localException2.printStackTrace();
      System.exit(1);
    }
  }
  
  private static String getTextResource(String paramString)
  {
    if (resources == null)
    {
      try
      {
        resources = ResourceBundle.getBundle("sun.rmi.server.resources.rmid");
      }
      catch (MissingResourceException localMissingResourceException1) {}
      if (resources == null) {
        return "[missing resource file: " + paramString + "]";
      }
    }
    String str = null;
    try
    {
      str = resources.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException2) {}
    if (str == null) {
      return "[missing resource: " + paramString + "]";
    }
    return str;
  }
  
  private static Class<?> getRMIClass(String paramString)
    throws Exception
  {
    return RMIClassLoader.loadClass(paramString);
  }
  
  private synchronized String Pstartgroup()
    throws ActivationException
  {
    for (;;)
    {
      checkShutdown();
      if (groupSemaphore > 0)
      {
        groupSemaphore -= 1;
        return "Group-" + groupCounter++;
      }
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  private synchronized void Vstartgroup()
  {
    groupSemaphore += 1;
    notifyAll();
  }
  
  private static class ActLogHandler
    extends LogHandler
  {
    ActLogHandler() {}
    
    public Object initialSnapshot()
    {
      return new Activation(null);
    }
    
    public Object applyUpdate(Object paramObject1, Object paramObject2)
      throws Exception
    {
      return ((Activation.LogRecord)paramObject1).apply(paramObject2);
    }
  }
  
  class ActivationMonitorImpl
    extends UnicastRemoteObject
    implements ActivationMonitor
  {
    private static final long serialVersionUID = -6214940464757948867L;
    
    ActivationMonitorImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory)
      throws RemoteException
    {
      super(null, paramRMIServerSocketFactory);
    }
    
    public void inactiveObject(ActivationID paramActivationID)
      throws UnknownObjectException, RemoteException
    {
      try
      {
        Activation.this.checkShutdown();
      }
      catch (ActivationException localActivationException)
      {
        return;
      }
      RegistryImpl.checkAccess("Activator.inactiveObject");
      Activation.this.getGroupEntry(paramActivationID).inactiveObject(paramActivationID);
    }
    
    public void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
      throws UnknownObjectException, RemoteException
    {
      try
      {
        Activation.this.checkShutdown();
      }
      catch (ActivationException localActivationException)
      {
        return;
      }
      RegistryImpl.checkAccess("ActivationSystem.activeObject");
      Activation.this.getGroupEntry(paramActivationID).activeObject(paramActivationID, paramMarshalledObject);
    }
    
    public void inactiveGroup(ActivationGroupID paramActivationGroupID, long paramLong)
      throws UnknownGroupException, RemoteException
    {
      try
      {
        Activation.this.checkShutdown();
      }
      catch (ActivationException localActivationException)
      {
        return;
      }
      RegistryImpl.checkAccess("ActivationMonitor.inactiveGroup");
      Activation.this.getGroupEntry(paramActivationGroupID).inactiveGroup(paramLong, false);
    }
  }
  
  private static class ActivationServerSocketFactory
    implements RMIServerSocketFactory
  {
    private final ServerSocket serverSocket;
    
    ActivationServerSocketFactory(ServerSocket paramServerSocket)
    {
      serverSocket = paramServerSocket;
    }
    
    public ServerSocket createServerSocket(int paramInt)
      throws IOException
    {
      return new Activation.DelayedAcceptServerSocket(serverSocket);
    }
  }
  
  class ActivationSystemImpl
    extends RemoteServer
    implements ActivationSystem
  {
    private static final long serialVersionUID = 9100152600327688967L;
    
    ActivationSystemImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory)
      throws RemoteException
    {
      LiveRef localLiveRef = new LiveRef(new ObjID(4), paramInt, null, paramRMIServerSocketFactory);
      Activation.SameHostOnlyServerRef localSameHostOnlyServerRef = new Activation.SameHostOnlyServerRef(localLiveRef, "ActivationSystem.nonLocalAccess");
      ref = localSameHostOnlyServerRef;
      localSameHostOnlyServerRef.exportObject(this, null);
    }
    
    public ActivationID registerObject(ActivationDesc paramActivationDesc)
      throws ActivationException, UnknownGroupException, RemoteException
    {
      Activation.this.checkShutdown();
      ActivationGroupID localActivationGroupID = paramActivationDesc.getGroupID();
      ActivationID localActivationID = new ActivationID(activatorStub);
      Activation.this.getGroupEntry(localActivationGroupID).registerObject(localActivationID, paramActivationDesc, true);
      return localActivationID;
    }
    
    public void unregisterObject(ActivationID paramActivationID)
      throws ActivationException, UnknownObjectException, RemoteException
    {
      Activation.this.checkShutdown();
      Activation.this.getGroupEntry(paramActivationID).unregisterObject(paramActivationID, true);
    }
    
    public ActivationGroupID registerGroup(ActivationGroupDesc paramActivationGroupDesc)
      throws ActivationException, RemoteException
    {
      Thread.dumpStack();
      Activation.this.checkShutdown();
      Activation.this.checkArgs(paramActivationGroupDesc, null);
      ActivationGroupID localActivationGroupID = new ActivationGroupID(systemStub);
      Activation.GroupEntry localGroupEntry = new Activation.GroupEntry(Activation.this, localActivationGroupID, paramActivationGroupDesc);
      groupTable.put(localActivationGroupID, localGroupEntry);
      Activation.this.addLogRecord(new Activation.LogRegisterGroup(localActivationGroupID, paramActivationGroupDesc));
      return localActivationGroupID;
    }
    
    public ActivationMonitor activeGroup(ActivationGroupID paramActivationGroupID, ActivationInstantiator paramActivationInstantiator, long paramLong)
      throws ActivationException, UnknownGroupException, RemoteException
    {
      Activation.this.checkShutdown();
      Activation.this.getGroupEntry(paramActivationGroupID).activeGroup(paramActivationInstantiator, paramLong);
      return monitor;
    }
    
    public void unregisterGroup(ActivationGroupID paramActivationGroupID)
      throws ActivationException, UnknownGroupException, RemoteException
    {
      Activation.this.checkShutdown();
      Activation.this.removeGroupEntry(paramActivationGroupID).unregisterGroup(true);
    }
    
    public ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
      throws ActivationException, UnknownObjectException, RemoteException
    {
      Activation.this.checkShutdown();
      if (!Activation.this.getGroupID(paramActivationID).equals(paramActivationDesc.getGroupID())) {
        throw new ActivationException("ActivationDesc contains wrong group");
      }
      return Activation.this.getGroupEntry(paramActivationID).setActivationDesc(paramActivationID, paramActivationDesc, true);
    }
    
    public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
      throws ActivationException, UnknownGroupException, RemoteException
    {
      Activation.this.checkShutdown();
      Activation.this.checkArgs(paramActivationGroupDesc, null);
      return Activation.this.getGroupEntry(paramActivationGroupID).setActivationGroupDesc(paramActivationGroupID, paramActivationGroupDesc, true);
    }
    
    public ActivationDesc getActivationDesc(ActivationID paramActivationID)
      throws ActivationException, UnknownObjectException, RemoteException
    {
      Activation.this.checkShutdown();
      return Activation.this.getGroupEntry(paramActivationID).getActivationDesc(paramActivationID);
    }
    
    public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID paramActivationGroupID)
      throws ActivationException, UnknownGroupException, RemoteException
    {
      Activation.this.checkShutdown();
      return getGroupEntrydesc;
    }
    
    /* Error */
    public void shutdown()
      throws AccessException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 182	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
      //   4: invokestatic 195	sun/rmi/server/Activation:access$1100	(Lsun/rmi/server/Activation;)Ljava/lang/Object;
      //   7: astore_1
      //   8: aload_1
      //   9: ifnull +17 -> 26
      //   12: aload_1
      //   13: dup
      //   14: astore_2
      //   15: monitorenter
      //   16: aload_2
      //   17: monitorexit
      //   18: goto +8 -> 26
      //   21: astore_3
      //   22: aload_2
      //   23: monitorexit
      //   24: aload_3
      //   25: athrow
      //   26: aload_0
      //   27: getfield 182	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
      //   30: dup
      //   31: astore_2
      //   32: monitorenter
      //   33: aload_0
      //   34: getfield 182	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
      //   37: invokestatic 193	sun/rmi/server/Activation:access$1200	(Lsun/rmi/server/Activation;)Z
      //   40: ifne +26 -> 66
      //   43: aload_0
      //   44: getfield 182	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
      //   47: iconst_1
      //   48: invokestatic 194	sun/rmi/server/Activation:access$1202	(Lsun/rmi/server/Activation;Z)Z
      //   51: pop
      //   52: new 98	sun/rmi/server/Activation$Shutdown
      //   55: dup
      //   56: aload_0
      //   57: getfield 182	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
      //   60: invokespecial 217	sun/rmi/server/Activation$Shutdown:<init>	(Lsun/rmi/server/Activation;)V
      //   63: invokevirtual 216	sun/rmi/server/Activation$Shutdown:start	()V
      //   66: aload_2
      //   67: monitorexit
      //   68: goto +10 -> 78
      //   71: astore 4
      //   73: aload_2
      //   74: monitorexit
      //   75: aload 4
      //   77: athrow
      //   78: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	79	0	this	ActivationSystemImpl
      //   7	6	1	localObject1	Object
      //   21	4	3	localObject2	Object
      //   71	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   16	18	21	finally
      //   21	24	21	finally
      //   33	68	71	finally
      //   71	75	71	finally
    }
  }
  
  class ActivatorImpl
    extends RemoteServer
    implements Activator
  {
    private static final long serialVersionUID = -3654244726254566136L;
    
    ActivatorImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory)
      throws RemoteException
    {
      LiveRef localLiveRef = new LiveRef(new ObjID(1), paramInt, null, paramRMIServerSocketFactory);
      UnicastServerRef localUnicastServerRef = new UnicastServerRef(localLiveRef);
      ref = localUnicastServerRef;
      localUnicastServerRef.exportObject(this, null, false);
    }
    
    public MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
      throws ActivationException, UnknownObjectException, RemoteException
    {
      Activation.this.checkShutdown();
      return Activation.this.getGroupEntry(paramActivationID).activate(paramActivationID, paramBoolean);
    }
  }
  
  public static class DefaultExecPolicy
  {
    public DefaultExecPolicy() {}
    
    public void checkExecCommand(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
      throws SecurityException
    {
      PermissionCollection localPermissionCollection = getExecPermissions();
      Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
      String str1;
      Object localObject3;
      if (localProperties != null)
      {
        localObject1 = localProperties.propertyNames();
        while (((Enumeration)localObject1).hasMoreElements())
        {
          localObject2 = (String)((Enumeration)localObject1).nextElement();
          str1 = localProperties.getProperty((String)localObject2);
          localObject3 = "-D" + (String)localObject2 + "=" + str1;
          try
          {
            checkPermission(localPermissionCollection, new ExecOptionPermission((String)localObject3));
          }
          catch (AccessControlException localAccessControlException)
          {
            if (str1.equals("")) {
              checkPermission(localPermissionCollection, new ExecOptionPermission("-D" + (String)localObject2));
            } else {
              throw localAccessControlException;
            }
          }
        }
      }
      Object localObject1 = paramActivationGroupDesc.getClassName();
      if (((localObject1 != null) && (!((String)localObject1).equals(ActivationGroupImpl.class.getName()))) || (paramActivationGroupDesc.getLocation() != null) || (paramActivationGroupDesc.getData() != null)) {
        throw new AccessControlException("access denied (custom group implementation not allowed)");
      }
      Object localObject2 = paramActivationGroupDesc.getCommandEnvironment();
      if (localObject2 != null)
      {
        str1 = ((ActivationGroupDesc.CommandEnvironment)localObject2).getCommandPath();
        if (str1 != null) {
          checkPermission(localPermissionCollection, new ExecPermission(str1));
        }
        localObject3 = ((ActivationGroupDesc.CommandEnvironment)localObject2).getCommandOptions();
        if (localObject3 != null) {
          for (String str2 : localObject3) {
            checkPermission(localPermissionCollection, new ExecOptionPermission(str2));
          }
        }
      }
    }
    
    static void checkConfiguration()
    {
      Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Policy run()
        {
          return Policy.getPolicy();
        }
      });
      if (!(localPolicy instanceof PolicyFile)) {
        return;
      }
      PermissionCollection localPermissionCollection = getExecPermissions();
      Enumeration localEnumeration = localPermissionCollection.elements();
      while (localEnumeration.hasMoreElements())
      {
        Permission localPermission = (Permission)localEnumeration.nextElement();
        if (((localPermission instanceof AllPermission)) || ((localPermission instanceof ExecPermission)) || ((localPermission instanceof ExecOptionPermission))) {
          return;
        }
      }
      System.err.println(Activation.getTextResource("rmid.exec.perms.inadequate"));
    }
    
    private static PermissionCollection getExecPermissions()
    {
      PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
      {
        public PermissionCollection run()
        {
          CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
          Policy localPolicy = Policy.getPolicy();
          if (localPolicy != null) {
            return localPolicy.getPermissions(localCodeSource);
          }
          return new Permissions();
        }
      });
      return localPermissionCollection;
    }
    
    private static void checkPermission(PermissionCollection paramPermissionCollection, Permission paramPermission)
      throws AccessControlException
    {
      if (!paramPermissionCollection.implies(paramPermission)) {
        throw new AccessControlException("access denied " + paramPermission.toString());
      }
    }
  }
  
  private static class DelayedAcceptServerSocket
    extends ServerSocket
  {
    private final ServerSocket serverSocket;
    
    DelayedAcceptServerSocket(ServerSocket paramServerSocket)
      throws IOException
    {
      serverSocket = paramServerSocket;
    }
    
    public void bind(SocketAddress paramSocketAddress)
      throws IOException
    {
      serverSocket.bind(paramSocketAddress);
    }
    
    public void bind(SocketAddress paramSocketAddress, int paramInt)
      throws IOException
    {
      serverSocket.bind(paramSocketAddress, paramInt);
    }
    
    public InetAddress getInetAddress()
    {
      (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InetAddress run()
        {
          return serverSocket.getInetAddress();
        }
      });
    }
    
    public int getLocalPort()
    {
      return serverSocket.getLocalPort();
    }
    
    public SocketAddress getLocalSocketAddress()
    {
      (SocketAddress)AccessController.doPrivileged(new PrivilegedAction()
      {
        public SocketAddress run()
        {
          return serverSocket.getLocalSocketAddress();
        }
      });
    }
    
    public Socket accept()
      throws IOException
    {
      synchronized (Activation.initLock)
      {
        try
        {
          while (!Activation.initDone) {
            Activation.initLock.wait();
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new AssertionError(localInterruptedException);
        }
      }
      return serverSocket.accept();
    }
    
    public void close()
      throws IOException
    {
      serverSocket.close();
    }
    
    public ServerSocketChannel getChannel()
    {
      return serverSocket.getChannel();
    }
    
    public boolean isBound()
    {
      return serverSocket.isBound();
    }
    
    public boolean isClosed()
    {
      return serverSocket.isClosed();
    }
    
    public void setSoTimeout(int paramInt)
      throws SocketException
    {
      serverSocket.setSoTimeout(paramInt);
    }
    
    public int getSoTimeout()
      throws IOException
    {
      return serverSocket.getSoTimeout();
    }
    
    public void setReuseAddress(boolean paramBoolean)
      throws SocketException
    {
      serverSocket.setReuseAddress(paramBoolean);
    }
    
    public boolean getReuseAddress()
      throws SocketException
    {
      return serverSocket.getReuseAddress();
    }
    
    public String toString()
    {
      return serverSocket.toString();
    }
    
    public void setReceiveBufferSize(int paramInt)
      throws SocketException
    {
      serverSocket.setReceiveBufferSize(paramInt);
    }
    
    public int getReceiveBufferSize()
      throws SocketException
    {
      return serverSocket.getReceiveBufferSize();
    }
  }
  
  private class GroupEntry
    implements Serializable
  {
    private static final long serialVersionUID = 7222464070032993304L;
    private static final int MAX_TRIES = 2;
    private static final int NORMAL = 0;
    private static final int CREATING = 1;
    private static final int TERMINATE = 2;
    private static final int TERMINATING = 3;
    ActivationGroupDesc desc = null;
    ActivationGroupID groupID = null;
    long incarnation = 0L;
    Map<ActivationID, Activation.ObjectEntry> objects = new HashMap();
    Set<ActivationID> restartSet = new HashSet();
    transient ActivationInstantiator group = null;
    transient int status = 0;
    transient long waitTime = 0L;
    transient String groupName = null;
    transient Process child = null;
    transient boolean removed = false;
    transient Watchdog watchdog = null;
    
    GroupEntry(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
    {
      groupID = paramActivationGroupID;
      desc = paramActivationGroupDesc;
    }
    
    void restartServices()
    {
      Iterator localIterator = null;
      synchronized (this)
      {
        if (restartSet.isEmpty()) {
          return;
        }
        localIterator = new HashSet(restartSet).iterator();
      }
      while (localIterator.hasNext())
      {
        ??? = (ActivationID)localIterator.next();
        try
        {
          activate((ActivationID)???, true);
        }
        catch (Exception localException)
        {
          if (shuttingDown) {
            return;
          }
          System.err.println(Activation.getTextResource("rmid.restart.service.warning"));
          localException.printStackTrace();
        }
      }
    }
    
    synchronized void activeGroup(ActivationInstantiator paramActivationInstantiator, long paramLong)
      throws ActivationException, UnknownGroupException
    {
      if (incarnation != paramLong) {
        throw new ActivationException("invalid incarnation");
      }
      if (group != null)
      {
        if (group.equals(paramActivationInstantiator)) {
          return;
        }
        throw new ActivationException("group already active");
      }
      if ((child != null) && (status != 1)) {
        throw new ActivationException("group not being created");
      }
      group = paramActivationInstantiator;
      status = 0;
      notifyAll();
    }
    
    private void checkRemoved()
      throws UnknownGroupException
    {
      if (removed) {
        throw new UnknownGroupException("group removed");
      }
    }
    
    private Activation.ObjectEntry getObjectEntry(ActivationID paramActivationID)
      throws UnknownObjectException
    {
      if (removed) {
        throw new UnknownObjectException("object's group removed");
      }
      Activation.ObjectEntry localObjectEntry = (Activation.ObjectEntry)objects.get(paramActivationID);
      if (localObjectEntry == null) {
        throw new UnknownObjectException("object unknown");
      }
      return localObjectEntry;
    }
    
    synchronized void registerObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
      throws UnknownGroupException, ActivationException
    {
      checkRemoved();
      objects.put(paramActivationID, new Activation.ObjectEntry(paramActivationDesc));
      if (paramActivationDesc.getRestartMode() == true) {
        restartSet.add(paramActivationID);
      }
      idTable.put(paramActivationID, groupID);
      if (paramBoolean) {
        Activation.this.addLogRecord(new Activation.LogRegisterObject(paramActivationID, paramActivationDesc));
      }
    }
    
    synchronized void unregisterObject(ActivationID paramActivationID, boolean paramBoolean)
      throws UnknownGroupException, ActivationException
    {
      Activation.ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
      removed = true;
      objects.remove(paramActivationID);
      if (desc.getRestartMode() == true) {
        restartSet.remove(paramActivationID);
      }
      idTable.remove(paramActivationID);
      if (paramBoolean) {
        Activation.this.addLogRecord(new Activation.LogUnregisterObject(paramActivationID));
      }
    }
    
    synchronized void unregisterGroup(boolean paramBoolean)
      throws UnknownGroupException, ActivationException
    {
      checkRemoved();
      removed = true;
      Iterator localIterator = objects.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ActivationID localActivationID = (ActivationID)localEntry.getKey();
        idTable.remove(localActivationID);
        Activation.ObjectEntry localObjectEntry = (Activation.ObjectEntry)localEntry.getValue();
        removed = true;
      }
      objects.clear();
      restartSet.clear();
      reset();
      childGone();
      if (paramBoolean) {
        Activation.this.addLogRecord(new Activation.LogUnregisterGroup(groupID));
      }
    }
    
    synchronized ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
      throws UnknownObjectException, UnknownGroupException, ActivationException
    {
      Activation.ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
      ActivationDesc localActivationDesc = desc;
      desc = paramActivationDesc;
      if (paramActivationDesc.getRestartMode() == true) {
        restartSet.add(paramActivationID);
      } else {
        restartSet.remove(paramActivationID);
      }
      if (paramBoolean) {
        Activation.this.addLogRecord(new Activation.LogUpdateDesc(paramActivationID, paramActivationDesc));
      }
      return localActivationDesc;
    }
    
    synchronized ActivationDesc getActivationDesc(ActivationID paramActivationID)
      throws UnknownObjectException, UnknownGroupException
    {
      return getObjectEntrydesc;
    }
    
    synchronized ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, boolean paramBoolean)
      throws UnknownGroupException, ActivationException
    {
      checkRemoved();
      ActivationGroupDesc localActivationGroupDesc = desc;
      desc = paramActivationGroupDesc;
      if (paramBoolean) {
        Activation.this.addLogRecord(new Activation.LogUpdateGroupDesc(paramActivationGroupID, paramActivationGroupDesc));
      }
      return localActivationGroupDesc;
    }
    
    synchronized void inactiveGroup(long paramLong, boolean paramBoolean)
      throws UnknownGroupException
    {
      checkRemoved();
      if (incarnation != paramLong) {
        throw new UnknownGroupException("invalid incarnation");
      }
      reset();
      if (paramBoolean)
      {
        terminate();
      }
      else if ((child != null) && (status == 0))
      {
        status = 2;
        watchdog.noRestart();
      }
    }
    
    synchronized void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
      throws UnknownObjectException
    {
      getObjectEntrystub = paramMarshalledObject;
    }
    
    synchronized void inactiveObject(ActivationID paramActivationID)
      throws UnknownObjectException
    {
      getObjectEntry(paramActivationID).reset();
    }
    
    private synchronized void reset()
    {
      group = null;
      Iterator localIterator = objects.values().iterator();
      while (localIterator.hasNext())
      {
        Activation.ObjectEntry localObjectEntry = (Activation.ObjectEntry)localIterator.next();
        localObjectEntry.reset();
      }
    }
    
    private void childGone()
    {
      if (child != null)
      {
        child = null;
        watchdog.dispose();
        watchdog = null;
        status = 0;
        notifyAll();
      }
    }
    
    private void terminate()
    {
      if ((child != null) && (status != 3))
      {
        child.destroy();
        status = 3;
        waitTime = (System.currentTimeMillis() + Activation.groupTimeout);
        notifyAll();
      }
    }
    
    private void await()
    {
      for (;;)
      {
        switch (status)
        {
        case 0: 
          return;
        case 2: 
          terminate();
        case 3: 
          try
          {
            child.exitValue();
          }
          catch (IllegalThreadStateException localIllegalThreadStateException)
          {
            long l = System.currentTimeMillis();
            if (waitTime > l)
            {
              try
              {
                wait(waitTime - l);
              }
              catch (InterruptedException localInterruptedException2) {}
              continue;
            }
          }
          childGone();
          return;
        case 1: 
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException1) {}
        }
      }
    }
    
    void shutdownFast()
    {
      Process localProcess = child;
      if (localProcess != null) {
        localProcess.destroy();
      }
    }
    
    synchronized void shutdown()
    {
      reset();
      terminate();
      await();
    }
    
    MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
      throws ActivationException
    {
      Object localObject1 = null;
      for (int i = 2; i > 0; i--)
      {
        Activation.ObjectEntry localObjectEntry;
        ActivationInstantiator localActivationInstantiator;
        long l;
        synchronized (this)
        {
          localObjectEntry = getObjectEntry(paramActivationID);
          if ((!paramBoolean) && (stub != null)) {
            return stub;
          }
          localActivationInstantiator = getInstantiator(groupID);
          l = incarnation;
        }
        int j = 0;
        boolean bool = false;
        try
        {
          return localObjectEntry.activate(paramActivationID, paramBoolean, localActivationInstantiator);
        }
        catch (NoSuchObjectException localNoSuchObjectException)
        {
          j = 1;
          localObject1 = localNoSuchObjectException;
        }
        catch (ConnectException localConnectException)
        {
          j = 1;
          bool = true;
          localObject1 = localConnectException;
        }
        catch (ConnectIOException localConnectIOException)
        {
          j = 1;
          bool = true;
          localObject1 = localConnectIOException;
        }
        catch (InactiveGroupException localInactiveGroupException)
        {
          j = 1;
          localObject1 = localInactiveGroupException;
        }
        catch (RemoteException localRemoteException)
        {
          if (localObject1 == null) {
            localObject1 = localRemoteException;
          }
        }
        if (j != 0) {
          try
          {
            System.err.println(MessageFormat.format(Activation.getTextResource("rmid.group.inactive"), new Object[] { ((Exception)localObject1).toString() }));
            ((Exception)localObject1).printStackTrace();
            Activation.this.getGroupEntry(groupID).inactiveGroup(l, bool);
          }
          catch (UnknownGroupException localUnknownGroupException) {}
        }
      }
      throw new ActivationException("object activation failed after 2 tries", (Throwable)localObject1);
    }
    
    private ActivationInstantiator getInstantiator(ActivationGroupID paramActivationGroupID)
      throws ActivationException
    {
      assert (Thread.holdsLock(this));
      await();
      if (group != null) {
        return group;
      }
      checkRemoved();
      int i = 0;
      try
      {
        groupName = Activation.this.Pstartgroup();
        i = 1;
        String[] arrayOfString = Activation.this.activationArgs(desc);
        Activation.this.checkArgs(desc, arrayOfString);
        Object localObject1;
        if (Activation.debugExec)
        {
          localObject1 = new StringBuffer(arrayOfString[0]);
          for (int j = 1; j < arrayOfString.length; j++)
          {
            ((StringBuffer)localObject1).append(' ');
            ((StringBuffer)localObject1).append(arrayOfString[j]);
          }
          System.err.println(MessageFormat.format(Activation.getTextResource("rmid.exec.command"), new Object[] { ((StringBuffer)localObject1).toString() }));
        }
        try
        {
          child = Runtime.getRuntime().exec(arrayOfString);
          status = 1;
          incarnation += 1L;
          watchdog = new Watchdog();
          watchdog.start();
          Activation.this.addLogRecord(new Activation.LogGroupIncarnation(paramActivationGroupID, incarnation));
          PipeWriter.plugTogetherPair(child.getInputStream(), System.out, child.getErrorStream(), System.err);
          localObject1 = new MarshalOutputStream(child.getOutputStream());
          Object localObject2 = null;
          try
          {
            ((MarshalOutputStream)localObject1).writeObject(paramActivationGroupID);
            ((MarshalOutputStream)localObject1).writeObject(desc);
            ((MarshalOutputStream)localObject1).writeLong(incarnation);
            ((MarshalOutputStream)localObject1).flush();
          }
          catch (Throwable localThrowable2)
          {
            localObject2 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject1 != null) {
              if (localObject2 != null) {
                try
                {
                  ((MarshalOutputStream)localObject1).close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject2).addSuppressed(localThrowable3);
                }
              } else {
                ((MarshalOutputStream)localObject1).close();
              }
            }
          }
        }
        catch (IOException localIOException)
        {
          terminate();
          throw new ActivationException("unable to create activation group", localIOException);
        }
        try
        {
          long l1 = System.currentTimeMillis();
          long l2 = l1 + Activation.execTimeout;
          do
          {
            wait(l2 - l1);
            if (group != null)
            {
              ActivationInstantiator localActivationInstantiator = group;
              return localActivationInstantiator;
            }
            l1 = System.currentTimeMillis();
            if (status != 1) {
              break;
            }
          } while (l1 < l2);
        }
        catch (InterruptedException localInterruptedException) {}
        terminate();
        throw new ActivationException(removed ? "activation group unregistered" : "timeout creating child process");
      }
      finally
      {
        if (i != 0) {
          Activation.this.Vstartgroup();
        }
      }
    }
    
    private class Watchdog
      extends Thread
    {
      private final Process groupProcess = child;
      private final long groupIncarnation = incarnation;
      private boolean canInterrupt = true;
      private boolean shouldQuit = false;
      private boolean shouldRestart = true;
      
      Watchdog()
      {
        super();
        setDaemon(true);
      }
      
      public void run()
      {
        if (shouldQuit) {
          return;
        }
        try
        {
          groupProcess.waitFor();
        }
        catch (InterruptedException localInterruptedException)
        {
          return;
        }
        int i = 0;
        synchronized (Activation.GroupEntry.this)
        {
          if (shouldQuit) {
            return;
          }
          canInterrupt = false;
          interrupted();
          if (groupIncarnation == incarnation)
          {
            i = (shouldRestart) && (!shuttingDown) ? 1 : 0;
            Activation.GroupEntry.this.reset();
            Activation.GroupEntry.this.childGone();
          }
        }
        if (i != 0) {
          restartServices();
        }
      }
      
      void dispose()
      {
        shouldQuit = true;
        if (canInterrupt) {
          interrupt();
        }
      }
      
      void noRestart()
      {
        shouldRestart = false;
      }
    }
  }
  
  private static class LogGroupIncarnation
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = 4146872747377631897L;
    private ActivationGroupID id;
    private long inc;
    
    LogGroupIncarnation(ActivationGroupID paramActivationGroupID, long paramLong)
    {
      super();
      id = paramActivationGroupID;
      inc = paramLong;
    }
    
    Object apply(Object paramObject)
    {
      try
      {
        Activation.GroupEntry localGroupEntry = ((Activation)paramObject).getGroupEntry(id);
        incarnation = inc;
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogGroupIncarnation" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static abstract class LogRecord
    implements Serializable
  {
    private static final long serialVersionUID = 8395140512322687529L;
    
    private LogRecord() {}
    
    abstract Object apply(Object paramObject)
      throws Exception;
  }
  
  private static class LogRegisterGroup
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = -1966827458515403625L;
    private ActivationGroupID id;
    private ActivationGroupDesc desc;
    
    LogRegisterGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
    {
      super();
      id = paramActivationGroupID;
      desc = paramActivationGroupDesc;
    }
    
    Object apply(Object paramObject)
    {
      Activation tmp19_16 = ((Activation)paramObject);
      tmp19_16.getClass();
      groupTable.put(id, new Activation.GroupEntry(tmp19_16, id, desc));
      return paramObject;
    }
  }
  
  private static class LogRegisterObject
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = -6280336276146085143L;
    private ActivationID id;
    private ActivationDesc desc;
    
    LogRegisterObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
    {
      super();
      id = paramActivationID;
      desc = paramActivationDesc;
    }
    
    Object apply(Object paramObject)
    {
      try
      {
        ((Activation)paramObject).getGroupEntry(desc.getGroupID()).registerObject(id, desc, false);
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogRegisterObject" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static class LogUnregisterGroup
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = -3356306586522147344L;
    private ActivationGroupID id;
    
    LogUnregisterGroup(ActivationGroupID paramActivationGroupID)
    {
      super();
      id = paramActivationGroupID;
    }
    
    Object apply(Object paramObject)
    {
      Activation.GroupEntry localGroupEntry = (Activation.GroupEntry)groupTable.remove(id);
      try
      {
        localGroupEntry.unregisterGroup(false);
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterGroup" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static class LogUnregisterObject
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = 6269824097396935501L;
    private ActivationID id;
    
    LogUnregisterObject(ActivationID paramActivationID)
    {
      super();
      id = paramActivationID;
    }
    
    Object apply(Object paramObject)
    {
      try
      {
        ((Activation)paramObject).getGroupEntry(id).unregisterObject(id, false);
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterObject" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static class LogUpdateDesc
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = 545511539051179885L;
    private ActivationID id;
    private ActivationDesc desc;
    
    LogUpdateDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
    {
      super();
      id = paramActivationID;
      desc = paramActivationDesc;
    }
    
    Object apply(Object paramObject)
    {
      try
      {
        ((Activation)paramObject).getGroupEntry(id).setActivationDesc(id, desc, false);
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateDesc" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static class LogUpdateGroupDesc
    extends Activation.LogRecord
  {
    private static final long serialVersionUID = -1271300989218424337L;
    private ActivationGroupID id;
    private ActivationGroupDesc desc;
    
    LogUpdateGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
    {
      super();
      id = paramActivationGroupID;
      desc = paramActivationGroupDesc;
    }
    
    Object apply(Object paramObject)
    {
      try
      {
        ((Activation)paramObject).getGroupEntry(id).setActivationGroupDesc(id, desc, false);
      }
      catch (Exception localException)
      {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateGroupDesc" }));
        localException.printStackTrace();
      }
      return paramObject;
    }
  }
  
  private static class ObjectEntry
    implements Serializable
  {
    private static final long serialVersionUID = -5500114225321357856L;
    ActivationDesc desc;
    volatile transient MarshalledObject<? extends Remote> stub = null;
    volatile transient boolean removed = false;
    
    ObjectEntry(ActivationDesc paramActivationDesc)
    {
      desc = paramActivationDesc;
    }
    
    synchronized MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean, ActivationInstantiator paramActivationInstantiator)
      throws RemoteException, ActivationException
    {
      MarshalledObject localMarshalledObject = stub;
      if (removed) {
        throw new UnknownObjectException("object removed");
      }
      if ((!paramBoolean) && (localMarshalledObject != null)) {
        return localMarshalledObject;
      }
      localMarshalledObject = paramActivationInstantiator.newInstance(paramActivationID, desc);
      stub = localMarshalledObject;
      return localMarshalledObject;
    }
    
    void reset()
    {
      stub = null;
    }
  }
  
  static class SameHostOnlyServerRef
    extends UnicastServerRef
  {
    private static final long serialVersionUID = 1234L;
    private String accessKind;
    
    SameHostOnlyServerRef(LiveRef paramLiveRef, String paramString)
    {
      super();
      accessKind = paramString;
    }
    
    protected void unmarshalCustomCallData(ObjectInput paramObjectInput)
      throws IOException, ClassNotFoundException
    {
      RegistryImpl.checkAccess(accessKind);
      super.unmarshalCustomCallData(paramObjectInput);
    }
  }
  
  private class Shutdown
    extends Thread
  {
    Shutdown()
    {
      super();
    }
    
    public void run()
    {
      try
      {
        Activation.unexport(activator);
        Activation.unexport(system);
        Iterator localIterator = groupTable.values().iterator();
        while (localIterator.hasNext())
        {
          Activation.GroupEntry localGroupEntry = (Activation.GroupEntry)localIterator.next();
          localGroupEntry.shutdown();
        }
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        Activation.unexport(monitor);
        try
        {
          synchronized (log)
          {
            log.close();
          }
        }
        catch (IOException localIOException) {}
      }
      finally
      {
        System.err.println(Activation.getTextResource("rmid.daemon.shutdown"));
        System.exit(0);
      }
    }
  }
  
  private class ShutdownHook
    extends Thread
  {
    ShutdownHook()
    {
      super();
    }
    
    public void run()
    {
      synchronized (Activation.this)
      {
        shuttingDown = true;
      }
      ??? = groupTable.values().iterator();
      while (((Iterator)???).hasNext())
      {
        Activation.GroupEntry localGroupEntry = (Activation.GroupEntry)((Iterator)???).next();
        localGroupEntry.shutdownFast();
      }
    }
  }
  
  private static class SystemRegistryImpl
    extends RegistryImpl
  {
    private static final String NAME = ActivationSystem.class.getName();
    private static final long serialVersionUID = 4877330021609408794L;
    private final ActivationSystem systemStub;
    
    SystemRegistryImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, ActivationSystem paramActivationSystem)
      throws RemoteException
    {
      super(paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      systemStub = paramActivationSystem;
    }
    
    public Remote lookup(String paramString)
      throws RemoteException, NotBoundException
    {
      if (paramString.equals(NAME)) {
        return systemStub;
      }
      return super.lookup(paramString);
    }
    
    public String[] list()
      throws RemoteException
    {
      String[] arrayOfString1 = super.list();
      int i = arrayOfString1.length;
      String[] arrayOfString2 = new String[i + 1];
      if (i > 0) {
        System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
      }
      arrayOfString2[i] = NAME;
      return arrayOfString2;
    }
    
    public void bind(String paramString, Remote paramRemote)
      throws RemoteException, AlreadyBoundException, AccessException
    {
      if (paramString.equals(NAME)) {
        throw new AccessException("binding ActivationSystem is disallowed");
      }
      RegistryImpl.checkAccess("ActivationSystem.bind");
      super.bind(paramString, paramRemote);
    }
    
    public void unbind(String paramString)
      throws RemoteException, NotBoundException, AccessException
    {
      if (paramString.equals(NAME)) {
        throw new AccessException("unbinding ActivationSystem is disallowed");
      }
      RegistryImpl.checkAccess("ActivationSystem.unbind");
      super.unbind(paramString);
    }
    
    public void rebind(String paramString, Remote paramRemote)
      throws RemoteException, AccessException
    {
      if (paramString.equals(NAME)) {
        throw new AccessException("binding ActivationSystem is disallowed");
      }
      RegistryImpl.checkAccess("ActivationSystem.rebind");
      super.rebind(paramString, paramRemote);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */