package java.util.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public abstract class AbstractPreferences
  extends Preferences
{
  private final String name;
  private final String absolutePath;
  final AbstractPreferences parent;
  private final AbstractPreferences root;
  protected boolean newNode = false;
  private Map<String, AbstractPreferences> kidCache = new HashMap();
  private boolean removed = false;
  private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];
  private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];
  protected final Object lock = new Object();
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final AbstractPreferences[] EMPTY_ABSTRACT_PREFS_ARRAY = new AbstractPreferences[0];
  private static final List<EventObject> eventQueue = new LinkedList();
  private static Thread eventDispatchThread = null;
  
  protected AbstractPreferences(AbstractPreferences paramAbstractPreferences, String paramString)
  {
    if (paramAbstractPreferences == null)
    {
      if (!paramString.equals("")) {
        throw new IllegalArgumentException("Root name '" + paramString + "' must be \"\"");
      }
      absolutePath = "/";
      root = this;
    }
    else
    {
      if (paramString.indexOf('/') != -1) {
        throw new IllegalArgumentException("Name '" + paramString + "' contains '/'");
      }
      if (paramString.equals("")) {
        throw new IllegalArgumentException("Illegal name: empty string");
      }
      root = root;
      absolutePath = (paramAbstractPreferences.absolutePath() + "/" + paramString);
    }
    name = paramString;
    parent = paramAbstractPreferences;
  }
  
  public void put(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    if (paramString1.length() > 80) {
      throw new IllegalArgumentException("Key too long: " + paramString1);
    }
    if (paramString2.length() > 8192) {
      throw new IllegalArgumentException("Value too long: " + paramString2);
    }
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      putSpi(paramString1, paramString2);
      enqueuePreferenceChangeEvent(paramString1, paramString2);
    }
  }
  
  public String get(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("Null key");
    }
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      String str = null;
      try
      {
        str = getSpi(paramString1);
      }
      catch (Exception localException) {}
      return str == null ? paramString2 : str;
    }
  }
  
  public void remove(String paramString)
  {
    Objects.requireNonNull(paramString, "Specified key cannot be null");
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      removeSpi(paramString);
      enqueuePreferenceChangeEvent(paramString, null);
    }
  }
  
  public void clear()
    throws BackingStoreException
  {
    synchronized (lock)
    {
      String[] arrayOfString = keys();
      for (int i = 0; i < arrayOfString.length; i++) {
        remove(arrayOfString[i]);
      }
    }
  }
  
  public void putInt(String paramString, int paramInt)
  {
    put(paramString, Integer.toString(paramInt));
  }
  
  public int getInt(String paramString, int paramInt)
  {
    int i = paramInt;
    try
    {
      String str = get(paramString, null);
      if (str != null) {
        i = Integer.parseInt(str);
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    return i;
  }
  
  public void putLong(String paramString, long paramLong)
  {
    put(paramString, Long.toString(paramLong));
  }
  
  public long getLong(String paramString, long paramLong)
  {
    long l = paramLong;
    try
    {
      String str = get(paramString, null);
      if (str != null) {
        l = Long.parseLong(str);
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    return l;
  }
  
  public void putBoolean(String paramString, boolean paramBoolean)
  {
    put(paramString, String.valueOf(paramBoolean));
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    boolean bool = paramBoolean;
    String str = get(paramString, null);
    if (str != null) {
      if (str.equalsIgnoreCase("true")) {
        bool = true;
      } else if (str.equalsIgnoreCase("false")) {
        bool = false;
      }
    }
    return bool;
  }
  
  public void putFloat(String paramString, float paramFloat)
  {
    put(paramString, Float.toString(paramFloat));
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    float f = paramFloat;
    try
    {
      String str = get(paramString, null);
      if (str != null) {
        f = Float.parseFloat(str);
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    return f;
  }
  
  public void putDouble(String paramString, double paramDouble)
  {
    put(paramString, Double.toString(paramDouble));
  }
  
  public double getDouble(String paramString, double paramDouble)
  {
    double d = paramDouble;
    try
    {
      String str = get(paramString, null);
      if (str != null) {
        d = Double.parseDouble(str);
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    return d;
  }
  
  public void putByteArray(String paramString, byte[] paramArrayOfByte)
  {
    put(paramString, Base64.byteArrayToBase64(paramArrayOfByte));
  }
  
  public byte[] getByteArray(String paramString, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    String str = get(paramString, null);
    try
    {
      if (str != null) {
        arrayOfByte = Base64.base64ToByteArray(str);
      }
    }
    catch (RuntimeException localRuntimeException) {}
    return arrayOfByte;
  }
  
  public String[] keys()
    throws BackingStoreException
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      return keysSpi();
    }
  }
  
  public String[] childrenNames()
    throws BackingStoreException
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      TreeSet localTreeSet = new TreeSet(kidCache.keySet());
      for (String str : childrenNamesSpi()) {
        localTreeSet.add(str);
      }
      return (String[])localTreeSet.toArray(EMPTY_STRING_ARRAY);
    }
  }
  
  protected final AbstractPreferences[] cachedChildren()
  {
    return (AbstractPreferences[])kidCache.values().toArray(EMPTY_ABSTRACT_PREFS_ARRAY);
  }
  
  public Preferences parent()
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      return parent;
    }
  }
  
  public Preferences node(String paramString)
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      if (paramString.equals("")) {
        return this;
      }
      if (paramString.equals("/")) {
        return root;
      }
      if (paramString.charAt(0) != '/') {
        return node(new StringTokenizer(paramString, "/", true));
      }
    }
    return root.node(new StringTokenizer(paramString.substring(1), "/", true));
  }
  
  private Preferences node(StringTokenizer paramStringTokenizer)
  {
    String str = paramStringTokenizer.nextToken();
    if (str.equals("/")) {
      throw new IllegalArgumentException("Consecutive slashes in path");
    }
    synchronized (lock)
    {
      AbstractPreferences localAbstractPreferences = (AbstractPreferences)kidCache.get(str);
      if (localAbstractPreferences == null)
      {
        if (str.length() > 80) {
          throw new IllegalArgumentException("Node name " + str + " too long");
        }
        localAbstractPreferences = childSpi(str);
        if (newNode) {
          enqueueNodeAddedEvent(localAbstractPreferences);
        }
        kidCache.put(str, localAbstractPreferences);
      }
      if (!paramStringTokenizer.hasMoreTokens()) {
        return localAbstractPreferences;
      }
      paramStringTokenizer.nextToken();
      if (!paramStringTokenizer.hasMoreTokens()) {
        throw new IllegalArgumentException("Path ends with slash");
      }
      return localAbstractPreferences.node(paramStringTokenizer);
    }
  }
  
  public boolean nodeExists(String paramString)
    throws BackingStoreException
  {
    synchronized (lock)
    {
      if (paramString.equals("")) {
        return !removed;
      }
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      if (paramString.equals("/")) {
        return true;
      }
      if (paramString.charAt(0) != '/') {
        return nodeExists(new StringTokenizer(paramString, "/", true));
      }
    }
    return root.nodeExists(new StringTokenizer(paramString.substring(1), "/", true));
  }
  
  private boolean nodeExists(StringTokenizer paramStringTokenizer)
    throws BackingStoreException
  {
    String str = paramStringTokenizer.nextToken();
    if (str.equals("/")) {
      throw new IllegalArgumentException("Consecutive slashes in path");
    }
    synchronized (lock)
    {
      AbstractPreferences localAbstractPreferences = (AbstractPreferences)kidCache.get(str);
      if (localAbstractPreferences == null) {
        localAbstractPreferences = getChild(str);
      }
      if (localAbstractPreferences == null) {
        return false;
      }
      if (!paramStringTokenizer.hasMoreTokens()) {
        return true;
      }
      paramStringTokenizer.nextToken();
      if (!paramStringTokenizer.hasMoreTokens()) {
        throw new IllegalArgumentException("Path ends with slash");
      }
      return localAbstractPreferences.nodeExists(paramStringTokenizer);
    }
  }
  
  public void removeNode()
    throws BackingStoreException
  {
    if (this == root) {
      throw new UnsupportedOperationException("Can't remove the root!");
    }
    synchronized (parent.lock)
    {
      removeNode2();
      parent.kidCache.remove(name);
    }
  }
  
  private void removeNode2()
    throws BackingStoreException
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node already removed.");
      }
      String[] arrayOfString = childrenNamesSpi();
      for (int i = 0; i < arrayOfString.length; i++) {
        if (!kidCache.containsKey(arrayOfString[i])) {
          kidCache.put(arrayOfString[i], childSpi(arrayOfString[i]));
        }
      }
      Iterator localIterator = kidCache.values().iterator();
      while (localIterator.hasNext()) {
        try
        {
          ((AbstractPreferences)localIterator.next()).removeNode2();
          localIterator.remove();
        }
        catch (BackingStoreException localBackingStoreException) {}
      }
      removeNodeSpi();
      removed = true;
      parent.enqueueNodeRemovedEvent(this);
    }
  }
  
  public String name()
  {
    return name;
  }
  
  public String absolutePath()
  {
    return absolutePath;
  }
  
  public boolean isUserNode()
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(root == Preferences.userRoot());
      }
    })).booleanValue();
  }
  
  public void addPreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener)
  {
    if (paramPreferenceChangeListener == null) {
      throw new NullPointerException("Change listener is null.");
    }
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      PreferenceChangeListener[] arrayOfPreferenceChangeListener = prefListeners;
      prefListeners = new PreferenceChangeListener[arrayOfPreferenceChangeListener.length + 1];
      System.arraycopy(arrayOfPreferenceChangeListener, 0, prefListeners, 0, arrayOfPreferenceChangeListener.length);
      prefListeners[arrayOfPreferenceChangeListener.length] = paramPreferenceChangeListener;
    }
    startEventDispatchThreadIfNecessary();
  }
  
  public void removePreferenceChangeListener(PreferenceChangeListener paramPreferenceChangeListener)
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      if ((prefListeners == null) || (prefListeners.length == 0)) {
        throw new IllegalArgumentException("Listener not registered.");
      }
      PreferenceChangeListener[] arrayOfPreferenceChangeListener = new PreferenceChangeListener[prefListeners.length - 1];
      int i = 0;
      while ((i < arrayOfPreferenceChangeListener.length) && (prefListeners[i] != paramPreferenceChangeListener)) {
        arrayOfPreferenceChangeListener[i] = prefListeners[(i++)];
      }
      if ((i == arrayOfPreferenceChangeListener.length) && (prefListeners[i] != paramPreferenceChangeListener)) {
        throw new IllegalArgumentException("Listener not registered.");
      }
      while (i < arrayOfPreferenceChangeListener.length) {
        arrayOfPreferenceChangeListener[i] = prefListeners[(++i)];
      }
      prefListeners = arrayOfPreferenceChangeListener;
    }
  }
  
  public void addNodeChangeListener(NodeChangeListener paramNodeChangeListener)
  {
    if (paramNodeChangeListener == null) {
      throw new NullPointerException("Change listener is null.");
    }
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      if (nodeListeners == null)
      {
        nodeListeners = new NodeChangeListener[1];
        nodeListeners[0] = paramNodeChangeListener;
      }
      else
      {
        NodeChangeListener[] arrayOfNodeChangeListener = nodeListeners;
        nodeListeners = new NodeChangeListener[arrayOfNodeChangeListener.length + 1];
        System.arraycopy(arrayOfNodeChangeListener, 0, nodeListeners, 0, arrayOfNodeChangeListener.length);
        nodeListeners[arrayOfNodeChangeListener.length] = paramNodeChangeListener;
      }
    }
    startEventDispatchThreadIfNecessary();
  }
  
  public void removeNodeChangeListener(NodeChangeListener paramNodeChangeListener)
  {
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed.");
      }
      if ((nodeListeners == null) || (nodeListeners.length == 0)) {
        throw new IllegalArgumentException("Listener not registered.");
      }
      for (int i = 0; (i < nodeListeners.length) && (nodeListeners[i] != paramNodeChangeListener); i++) {}
      if (i == nodeListeners.length) {
        throw new IllegalArgumentException("Listener not registered.");
      }
      NodeChangeListener[] arrayOfNodeChangeListener = new NodeChangeListener[nodeListeners.length - 1];
      if (i != 0) {
        System.arraycopy(nodeListeners, 0, arrayOfNodeChangeListener, 0, i);
      }
      if (i != arrayOfNodeChangeListener.length) {
        System.arraycopy(nodeListeners, i + 1, arrayOfNodeChangeListener, i, arrayOfNodeChangeListener.length - i);
      }
      nodeListeners = arrayOfNodeChangeListener;
    }
  }
  
  protected abstract void putSpi(String paramString1, String paramString2);
  
  protected abstract String getSpi(String paramString);
  
  protected abstract void removeSpi(String paramString);
  
  protected abstract void removeNodeSpi()
    throws BackingStoreException;
  
  protected abstract String[] keysSpi()
    throws BackingStoreException;
  
  protected abstract String[] childrenNamesSpi()
    throws BackingStoreException;
  
  protected AbstractPreferences getChild(String paramString)
    throws BackingStoreException
  {
    synchronized (lock)
    {
      String[] arrayOfString = childrenNames();
      for (int i = 0; i < arrayOfString.length; i++) {
        if (arrayOfString[i].equals(paramString)) {
          return childSpi(arrayOfString[i]);
        }
      }
    }
    return null;
  }
  
  protected abstract AbstractPreferences childSpi(String paramString);
  
  public String toString()
  {
    return (isUserNode() ? "User" : "System") + " Preference Node: " + absolutePath();
  }
  
  public void sync()
    throws BackingStoreException
  {
    sync2();
  }
  
  private void sync2()
    throws BackingStoreException
  {
    AbstractPreferences[] arrayOfAbstractPreferences;
    synchronized (lock)
    {
      if (removed) {
        throw new IllegalStateException("Node has been removed");
      }
      syncSpi();
      arrayOfAbstractPreferences = cachedChildren();
    }
    for (int i = 0; i < arrayOfAbstractPreferences.length; i++) {
      arrayOfAbstractPreferences[i].sync2();
    }
  }
  
  protected abstract void syncSpi()
    throws BackingStoreException;
  
  public void flush()
    throws BackingStoreException
  {
    flush2();
  }
  
  private void flush2()
    throws BackingStoreException
  {
    AbstractPreferences[] arrayOfAbstractPreferences;
    synchronized (lock)
    {
      flushSpi();
      if (removed) {
        return;
      }
      arrayOfAbstractPreferences = cachedChildren();
    }
    for (int i = 0; i < arrayOfAbstractPreferences.length; i++) {
      arrayOfAbstractPreferences[i].flush2();
    }
  }
  
  protected abstract void flushSpi()
    throws BackingStoreException;
  
  /* Error */
  protected boolean isRemoved()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 423	java/util/prefs/AbstractPreferences:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 422	java/util/prefs/AbstractPreferences:removed	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	AbstractPreferences
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  private static synchronized void startEventDispatchThreadIfNecessary()
  {
    if (eventDispatchThread == null)
    {
      eventDispatchThread = new EventDispatchThread(null);
      eventDispatchThread.setDaemon(true);
      eventDispatchThread.start();
    }
  }
  
  /* Error */
  PreferenceChangeListener[] prefListeners()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 423	java/util/prefs/AbstractPreferences:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 434	java/util/prefs/AbstractPreferences:prefListeners	[Ljava/util/prefs/PreferenceChangeListener;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	AbstractPreferences
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  NodeChangeListener[] nodeListeners()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 423	java/util/prefs/AbstractPreferences:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 433	java/util/prefs/AbstractPreferences:nodeListeners	[Ljava/util/prefs/NodeChangeListener;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	AbstractPreferences
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  private void enqueuePreferenceChangeEvent(String paramString1, String paramString2)
  {
    if (prefListeners.length != 0) {
      synchronized (eventQueue)
      {
        eventQueue.add(new PreferenceChangeEvent(this, paramString1, paramString2));
        eventQueue.notify();
      }
    }
  }
  
  private void enqueueNodeAddedEvent(Preferences paramPreferences)
  {
    if (nodeListeners.length != 0) {
      synchronized (eventQueue)
      {
        eventQueue.add(new NodeAddedEvent(this, paramPreferences));
        eventQueue.notify();
      }
    }
  }
  
  private void enqueueNodeRemovedEvent(Preferences paramPreferences)
  {
    if (nodeListeners.length != 0) {
      synchronized (eventQueue)
      {
        eventQueue.add(new NodeRemovedEvent(this, paramPreferences));
        eventQueue.notify();
      }
    }
  }
  
  public void exportNode(OutputStream paramOutputStream)
    throws IOException, BackingStoreException
  {
    XmlSupport.export(paramOutputStream, this, false);
  }
  
  public void exportSubtree(OutputStream paramOutputStream)
    throws IOException, BackingStoreException
  {
    XmlSupport.export(paramOutputStream, this, true);
  }
  
  private static class EventDispatchThread
    extends Thread
  {
    private EventDispatchThread() {}
    
    public void run()
    {
      for (;;)
      {
        EventObject localEventObject = null;
        synchronized (AbstractPreferences.eventQueue)
        {
          try
          {
            while (AbstractPreferences.eventQueue.isEmpty()) {
              AbstractPreferences.eventQueue.wait();
            }
            localEventObject = (EventObject)AbstractPreferences.eventQueue.remove(0);
          }
          catch (InterruptedException localInterruptedException)
          {
            return;
          }
        }
        ??? = (AbstractPreferences)localEventObject.getSource();
        Object localObject1;
        Object localObject3;
        int i;
        if ((localEventObject instanceof PreferenceChangeEvent))
        {
          localObject1 = (PreferenceChangeEvent)localEventObject;
          localObject3 = ((AbstractPreferences)???).prefListeners();
          for (i = 0; i < localObject3.length; i++) {
            localObject3[i].preferenceChange((PreferenceChangeEvent)localObject1);
          }
        }
        else
        {
          localObject1 = (NodeChangeEvent)localEventObject;
          localObject3 = ((AbstractPreferences)???).nodeListeners();
          if ((localObject1 instanceof AbstractPreferences.NodeAddedEvent)) {
            for (i = 0; i < localObject3.length; i++) {
              localObject3[i].childAdded((NodeChangeEvent)localObject1);
            }
          } else {
            for (i = 0; i < localObject3.length; i++) {
              localObject3[i].childRemoved((NodeChangeEvent)localObject1);
            }
          }
        }
      }
    }
  }
  
  private class NodeAddedEvent
    extends NodeChangeEvent
  {
    private static final long serialVersionUID = -6743557530157328528L;
    
    NodeAddedEvent(Preferences paramPreferences1, Preferences paramPreferences2)
    {
      super(paramPreferences2);
    }
  }
  
  private class NodeRemovedEvent
    extends NodeChangeEvent
  {
    private static final long serialVersionUID = 8735497392918824837L;
    
    NodeRemovedEvent(Preferences paramPreferences1, Preferences paramPreferences2)
    {
      super(paramPreferences2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\AbstractPreferences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */