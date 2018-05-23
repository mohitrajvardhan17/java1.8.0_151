package java.beans.beancontext;

import java.awt.Component;
import java.awt.Container;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class BeanContextSupport
  extends BeanContextChildSupport
  implements BeanContext, Serializable, PropertyChangeListener, VetoableChangeListener
{
  static final long serialVersionUID = -4879613978649577204L;
  protected transient HashMap children;
  private int serializable = 0;
  protected transient ArrayList bcmListeners;
  protected Locale locale;
  protected boolean okToUseGui;
  protected boolean designTime;
  private transient PropertyChangeListener childPCL;
  private transient VetoableChangeListener childVCL;
  private transient boolean serializing;
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramBeanContext);
    locale = (paramLocale != null ? paramLocale : Locale.getDefault());
    designTime = paramBoolean1;
    okToUseGui = paramBoolean2;
    initialize();
  }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean)
  {
    this(paramBeanContext, paramLocale, paramBoolean, true);
  }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale)
  {
    this(paramBeanContext, paramLocale, false, true);
  }
  
  public BeanContextSupport(BeanContext paramBeanContext)
  {
    this(paramBeanContext, null, false, true);
  }
  
  public BeanContextSupport()
  {
    this(null, null, false, true);
  }
  
  public BeanContext getBeanContextPeer()
  {
    return (BeanContext)getBeanContextChildPeer();
  }
  
  public Object instantiateChild(String paramString)
    throws IOException, ClassNotFoundException
  {
    BeanContext localBeanContext = getBeanContextPeer();
    return Beans.instantiate(localBeanContext.getClass().getClassLoader(), paramString, localBeanContext);
  }
  
  /* Error */
  public int size()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 478	java/util/HashMap:size	()I
    //   14: aload_1
    //   15: monitorexit
    //   16: ireturn
    //   17: astore_2
    //   18: aload_1
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	BeanContextSupport
    //   5	14	1	Ljava/lang/Object;	Object
    //   17	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	20	17	finally
  }
  
  /* Error */
  public boolean isEmpty()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 479	java/util/HashMap:isEmpty	()Z
    //   14: aload_1
    //   15: monitorexit
    //   16: ireturn
    //   17: astore_2
    //   18: aload_1
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	BeanContextSupport
    //   5	14	1	Ljava/lang/Object;	Object
    //   17	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	20	17	finally
  }
  
  /* Error */
  public boolean contains(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: aload_1
    //   12: invokevirtual 481	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   15: aload_2
    //   16: monitorexit
    //   17: ireturn
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	23	0	this	BeanContextSupport
    //   0	23	1	paramObject	Object
    //   5	15	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	18	finally
    //   18	21	18	finally
  }
  
  /* Error */
  public boolean containsKey(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: aload_1
    //   12: invokevirtual 481	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   15: aload_2
    //   16: monitorexit
    //   17: ireturn
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	23	0	this	BeanContextSupport
    //   0	23	1	paramObject	Object
    //   5	15	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	18	finally
    //   18	21	18	finally
  }
  
  /* Error */
  public Iterator iterator()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 222	java/beans/beancontext/BeanContextSupport$BCSIterator
    //   10: dup
    //   11: aload_0
    //   12: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   15: invokevirtual 484	java/util/HashMap:keySet	()Ljava/util/Set;
    //   18: invokeinterface 509 1 0
    //   23: invokespecial 450	java/beans/beancontext/BeanContextSupport$BCSIterator:<init>	(Ljava/util/Iterator;)V
    //   26: aload_1
    //   27: monitorexit
    //   28: areturn
    //   29: astore_2
    //   30: aload_1
    //   31: monitorexit
    //   32: aload_2
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	BeanContextSupport
    //   5	26	1	Ljava/lang/Object;	Object
    //   29	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	28	29	finally
    //   29	32	29	finally
  }
  
  /* Error */
  public Object[] toArray()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 484	java/util/HashMap:keySet	()Ljava/util/Set;
    //   14: invokeinterface 508 1 0
    //   19: aload_1
    //   20: monitorexit
    //   21: areturn
    //   22: astore_2
    //   23: aload_1
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	BeanContextSupport
    //   5	19	1	Ljava/lang/Object;	Object
    //   22	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	21	22	finally
    //   22	25	22	finally
  }
  
  /* Error */
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 484	java/util/HashMap:keySet	()Ljava/util/Set;
    //   14: aload_1
    //   15: invokeinterface 510 2 0
    //   20: aload_2
    //   21: monitorexit
    //   22: areturn
    //   23: astore_3
    //   24: aload_2
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	BeanContextSupport
    //   0	28	1	paramArrayOfObject	Object[]
    //   5	20	2	Ljava/lang/Object;	Object
    //   23	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	22	23	finally
    //   23	26	23	finally
  }
  
  protected BCSChild createBCSChild(Object paramObject1, Object paramObject2)
  {
    return new BCSChild(paramObject1, paramObject2);
  }
  
  public boolean add(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException();
    }
    if (children.containsKey(paramObject)) {
      return false;
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (children.containsKey(paramObject)) {
        return false;
      }
      if (!validatePendingAdd(paramObject)) {
        throw new IllegalStateException();
      }
      BeanContextChild localBeanContextChild1 = getChildBeanContextChild(paramObject);
      BeanContextChild localBeanContextChild2 = null;
      synchronized (paramObject)
      {
        if ((paramObject instanceof BeanContextProxy))
        {
          localBeanContextChild2 = ((BeanContextProxy)paramObject).getBeanContextProxy();
          if (localBeanContextChild2 == null) {
            throw new NullPointerException("BeanContextPeer.getBeanContextProxy()");
          }
        }
        BCSChild localBCSChild1 = createBCSChild(paramObject, localBeanContextChild2);
        BCSChild localBCSChild2 = null;
        synchronized (children)
        {
          children.put(paramObject, localBCSChild1);
          if (localBeanContextChild2 != null) {
            children.put(localBeanContextChild2, localBCSChild2 = createBCSChild(localBeanContextChild2, paramObject));
          }
        }
        if (localBeanContextChild1 != null) {
          synchronized (localBeanContextChild1)
          {
            try
            {
              localBeanContextChild1.setBeanContext(getBeanContextPeer());
            }
            catch (PropertyVetoException localPropertyVetoException)
            {
              synchronized (children)
              {
                children.remove(paramObject);
                if (localBeanContextChild2 != null) {
                  children.remove(localBeanContextChild2);
                }
              }
              throw new IllegalStateException();
            }
            localBeanContextChild1.addPropertyChangeListener("beanContext", childPCL);
            localBeanContextChild1.addVetoableChangeListener("beanContext", childVCL);
          }
        }
        ??? = getChildVisibility(paramObject);
        if (??? != null) {
          if (okToUseGui) {
            ((Visibility)???).okToUseGui();
          } else {
            ((Visibility)???).dontUseGui();
          }
        }
        if (getChildSerializable(paramObject) != null) {
          serializable += 1;
        }
        childJustAddedHook(paramObject, localBCSChild1);
        if (localBeanContextChild2 != null)
        {
          ??? = getChildVisibility(localBeanContextChild2);
          if (??? != null) {
            if (okToUseGui) {
              ((Visibility)???).okToUseGui();
            } else {
              ((Visibility)???).dontUseGui();
            }
          }
          if (getChildSerializable(localBeanContextChild2) != null) {
            serializable += 1;
          }
          childJustAddedHook(localBeanContextChild2, localBCSChild2);
        }
      }
      fireChildrenAdded(new BeanContextMembershipEvent(getBeanContextPeer(), new Object[] { paramObject, localBeanContextChild2 == null ? new Object[] { paramObject } : localBeanContextChild2 }));
    }
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    return remove(paramObject, true);
  }
  
  protected boolean remove(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException();
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (!containsKey(paramObject)) {
        return false;
      }
      if (!validatePendingRemove(paramObject)) {
        throw new IllegalStateException();
      }
      BCSChild localBCSChild1 = (BCSChild)children.get(paramObject);
      BCSChild localBCSChild2 = null;
      Object localObject1 = null;
      synchronized (paramObject)
      {
        if (paramBoolean)
        {
          BeanContextChild localBeanContextChild = getChildBeanContextChild(paramObject);
          if (localBeanContextChild != null) {
            synchronized (localBeanContextChild)
            {
              localBeanContextChild.removePropertyChangeListener("beanContext", childPCL);
              localBeanContextChild.removeVetoableChangeListener("beanContext", childVCL);
              try
              {
                localBeanContextChild.setBeanContext(null);
              }
              catch (PropertyVetoException localPropertyVetoException)
              {
                localBeanContextChild.addPropertyChangeListener("beanContext", childPCL);
                localBeanContextChild.addVetoableChangeListener("beanContext", childVCL);
                throw new IllegalStateException();
              }
            }
          }
        }
        synchronized (children)
        {
          children.remove(paramObject);
          if (localBCSChild1.isProxyPeer())
          {
            localBCSChild2 = (BCSChild)children.get(localObject1 = localBCSChild1.getProxyPeer());
            children.remove(localObject1);
          }
        }
        if (getChildSerializable(paramObject) != null) {
          serializable -= 1;
        }
        childJustRemovedHook(paramObject, localBCSChild1);
        if (localObject1 != null)
        {
          if (getChildSerializable(localObject1) != null) {
            serializable -= 1;
          }
          childJustRemovedHook(localObject1, localBCSChild2);
        }
      }
      fireChildrenRemoved(new BeanContextMembershipEvent(getBeanContextPeer(), new Object[] { paramObject, localObject1 == null ? new Object[] { paramObject } : localObject1 }));
    }
    return true;
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    synchronized (children)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        if (!contains(localIterator.next())) {
          return false;
        }
      }
      return true;
    }
  }
  
  public boolean addAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public void addBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener)
  {
    if (paramBeanContextMembershipListener == null) {
      throw new NullPointerException("listener");
    }
    synchronized (bcmListeners)
    {
      if (bcmListeners.contains(paramBeanContextMembershipListener)) {
        return;
      }
      bcmListeners.add(paramBeanContextMembershipListener);
    }
  }
  
  public void removeBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener)
  {
    if (paramBeanContextMembershipListener == null) {
      throw new NullPointerException("listener");
    }
    synchronized (bcmListeners)
    {
      if (!bcmListeners.contains(paramBeanContextMembershipListener)) {
        return;
      }
      bcmListeners.remove(paramBeanContextMembershipListener);
    }
  }
  
  public InputStream getResourceAsStream(String paramString, BeanContextChild paramBeanContextChild)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    if (paramBeanContextChild == null) {
      throw new NullPointerException("bcc");
    }
    if (containsKey(paramBeanContextChild))
    {
      ClassLoader localClassLoader = paramBeanContextChild.getClass().getClassLoader();
      return localClassLoader != null ? localClassLoader.getResourceAsStream(paramString) : ClassLoader.getSystemResourceAsStream(paramString);
    }
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public URL getResource(String paramString, BeanContextChild paramBeanContextChild)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    if (paramBeanContextChild == null) {
      throw new NullPointerException("bcc");
    }
    if (containsKey(paramBeanContextChild))
    {
      ClassLoader localClassLoader = paramBeanContextChild.getClass().getClassLoader();
      return localClassLoader != null ? localClassLoader.getResource(paramString) : ClassLoader.getSystemResource(paramString);
    }
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public synchronized void setDesignTime(boolean paramBoolean)
  {
    if (designTime != paramBoolean)
    {
      designTime = paramBoolean;
      firePropertyChange("designMode", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
    }
  }
  
  public synchronized boolean isDesignTime()
  {
    return designTime;
  }
  
  public synchronized void setLocale(Locale paramLocale)
    throws PropertyVetoException
  {
    if ((locale != null) && (!locale.equals(paramLocale)) && (paramLocale != null))
    {
      Locale localLocale = locale;
      fireVetoableChange("locale", localLocale, paramLocale);
      locale = paramLocale;
      firePropertyChange("locale", localLocale, paramLocale);
    }
  }
  
  public synchronized Locale getLocale()
  {
    return locale;
  }
  
  public synchronized boolean needsGui()
  {
    BeanContext localBeanContext = getBeanContextPeer();
    if (localBeanContext != this)
    {
      if ((localBeanContext instanceof Visibility)) {
        return localBeanContext.needsGui();
      }
      if (((localBeanContext instanceof Container)) || ((localBeanContext instanceof Component))) {
        return true;
      }
    }
    synchronized (children)
    {
      Iterator localIterator = children.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        try
        {
          return ((Visibility)localObject1).needsGui();
        }
        catch (ClassCastException localClassCastException)
        {
          if (((localObject1 instanceof Container)) || ((localObject1 instanceof Component))) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public synchronized void dontUseGui()
  {
    if (okToUseGui)
    {
      okToUseGui = false;
      synchronized (children)
      {
        Iterator localIterator = children.keySet().iterator();
        while (localIterator.hasNext())
        {
          Visibility localVisibility = getChildVisibility(localIterator.next());
          if (localVisibility != null) {
            localVisibility.dontUseGui();
          }
        }
      }
    }
  }
  
  public synchronized void okToUseGui()
  {
    if (!okToUseGui)
    {
      okToUseGui = true;
      synchronized (children)
      {
        Iterator localIterator = children.keySet().iterator();
        while (localIterator.hasNext())
        {
          Visibility localVisibility = getChildVisibility(localIterator.next());
          if (localVisibility != null) {
            localVisibility.okToUseGui();
          }
        }
      }
    }
  }
  
  public boolean avoidingGui()
  {
    return (!okToUseGui) && (needsGui());
  }
  
  public boolean isSerializing()
  {
    return serializing;
  }
  
  /* Error */
  protected Iterator bcsChildren()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 482	java/util/HashMap:values	()Ljava/util/Collection;
    //   14: invokeinterface 503 1 0
    //   19: aload_1
    //   20: monitorexit
    //   21: areturn
    //   22: astore_2
    //   23: aload_1
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	BeanContextSupport
    //   5	19	1	Ljava/lang/Object;	Object
    //   22	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	21	22	finally
    //   22	25	22	finally
  }
  
  protected void bcsPreSerializationHook(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {}
  
  protected void bcsPreDeserializationHook(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {}
  
  protected void childDeserializedHook(Object paramObject, BCSChild paramBCSChild)
  {
    synchronized (children)
    {
      children.put(paramObject, paramBCSChild);
    }
  }
  
  protected final void serialize(ObjectOutputStream paramObjectOutputStream, Collection paramCollection)
    throws IOException
  {
    int i = 0;
    Object[] arrayOfObject = paramCollection.toArray();
    for (int j = 0; j < arrayOfObject.length; j++) {
      if ((arrayOfObject[j] instanceof Serializable)) {
        i++;
      } else {
        arrayOfObject[j] = null;
      }
    }
    paramObjectOutputStream.writeInt(i);
    for (j = 0; i > 0; j++)
    {
      Object localObject = arrayOfObject[j];
      if (localObject != null)
      {
        paramObjectOutputStream.writeObject(localObject);
        i--;
      }
    }
  }
  
  protected final void deserialize(ObjectInputStream paramObjectInputStream, Collection paramCollection)
    throws IOException, ClassNotFoundException
  {
    int i = 0;
    i = paramObjectInputStream.readInt();
    while (i-- > 0) {
      paramCollection.add(paramObjectInputStream.readObject());
    }
  }
  
  public final void writeChildren(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (serializable <= 0) {
      return;
    }
    boolean bool = serializing;
    serializing = true;
    int i = 0;
    synchronized (children)
    {
      Iterator localIterator = children.entrySet().iterator();
      while ((localIterator.hasNext()) && (i < serializable))
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if ((localEntry.getKey() instanceof Serializable))
        {
          try
          {
            paramObjectOutputStream.writeObject(localEntry.getKey());
            paramObjectOutputStream.writeObject(localEntry.getValue());
          }
          catch (IOException localIOException)
          {
            serializing = bool;
            throw localIOException;
          }
          i++;
        }
      }
    }
    serializing = bool;
    if (i != serializable) {
      throw new IOException("wrote different number of children than expected");
    }
  }
  
  /* Error */
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 402	java/beans/beancontext/BeanContextSupport:serializing	Z
    //   5: getstatic 398	java/beans/beancontext/BeanContext:globalHierarchyLock	Ljava/lang/Object;
    //   8: dup
    //   9: astore_2
    //   10: monitorenter
    //   11: aload_1
    //   12: invokevirtual 455	java/io/ObjectOutputStream:defaultWriteObject	()V
    //   15: aload_0
    //   16: aload_1
    //   17: invokevirtual 424	java/beans/beancontext/BeanContextSupport:bcsPreSerializationHook	(Ljava/io/ObjectOutputStream;)V
    //   20: aload_0
    //   21: getfield 399	java/beans/beancontext/BeanContextSupport:serializable	I
    //   24: ifle +19 -> 43
    //   27: aload_0
    //   28: aload_0
    //   29: invokevirtual 418	java/beans/beancontext/BeanContextSupport:getBeanContextPeer	()Ljava/beans/beancontext/BeanContext;
    //   32: invokevirtual 470	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   35: ifeq +8 -> 43
    //   38: aload_0
    //   39: aload_1
    //   40: invokevirtual 425	java/beans/beancontext/BeanContextSupport:writeChildren	(Ljava/io/ObjectOutputStream;)V
    //   43: aload_0
    //   44: aload_1
    //   45: aload_0
    //   46: getfield 405	java/beans/beancontext/BeanContextSupport:bcmListeners	Ljava/util/ArrayList;
    //   49: invokevirtual 438	java/beans/beancontext/BeanContextSupport:serialize	(Ljava/io/ObjectOutputStream;Ljava/util/Collection;)V
    //   52: aload_0
    //   53: iconst_0
    //   54: putfield 402	java/beans/beancontext/BeanContextSupport:serializing	Z
    //   57: goto +11 -> 68
    //   60: astore_3
    //   61: aload_0
    //   62: iconst_0
    //   63: putfield 402	java/beans/beancontext/BeanContextSupport:serializing	Z
    //   66: aload_3
    //   67: athrow
    //   68: aload_2
    //   69: monitorexit
    //   70: goto +10 -> 80
    //   73: astore 4
    //   75: aload_2
    //   76: monitorexit
    //   77: aload 4
    //   79: athrow
    //   80: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	BeanContextSupport
    //   0	81	1	paramObjectOutputStream	ObjectOutputStream
    //   9	67	2	Ljava/lang/Object;	Object
    //   60	7	3	localObject1	Object
    //   73	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   11	52	60	finally
    //   11	70	73	finally
    //   73	77	73	finally
  }
  
  public final void readChildren(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = serializable;
    while (i-- > 0)
    {
      Object localObject1 = null;
      BCSChild localBCSChild = null;
      try
      {
        localObject1 = paramObjectInputStream.readObject();
        localBCSChild = (BCSChild)paramObjectInputStream.readObject();
      }
      catch (IOException localIOException)
      {
        continue;
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      continue;
      synchronized (localObject1)
      {
        BeanContextChild localBeanContextChild = null;
        try
        {
          localBeanContextChild = (BeanContextChild)localObject1;
        }
        catch (ClassCastException localClassCastException) {}
        if (localBeanContextChild != null)
        {
          try
          {
            localBeanContextChild.setBeanContext(getBeanContextPeer());
            localBeanContextChild.addPropertyChangeListener("beanContext", childPCL);
            localBeanContextChild.addVetoableChangeListener("beanContext", childVCL);
          }
          catch (PropertyVetoException localPropertyVetoException) {}
          continue;
        }
        childDeserializedHook(localObject1, localBCSChild);
      }
    }
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    synchronized (BeanContext.globalHierarchyLock)
    {
      paramObjectInputStream.defaultReadObject();
      initialize();
      bcsPreDeserializationHook(paramObjectInputStream);
      if ((serializable > 0) && (equals(getBeanContextPeer()))) {
        readChildren(paramObjectInputStream);
      }
      deserialize(paramObjectInputStream, bcmListeners = new ArrayList(1));
    }
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object localObject1 = paramPropertyChangeEvent.getSource();
    synchronized (children)
    {
      if (("beanContext".equals(str)) && (containsKey(localObject1)) && (!getBeanContextPeer().equals(paramPropertyChangeEvent.getNewValue())))
      {
        if (!validatePendingRemove(localObject1)) {
          throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", paramPropertyChangeEvent);
        }
        ((BCSChild)children.get(localObject1)).setRemovePending(true);
      }
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object localObject1 = paramPropertyChangeEvent.getSource();
    synchronized (children)
    {
      if (("beanContext".equals(str)) && (containsKey(localObject1)) && (((BCSChild)children.get(localObject1)).isRemovePending()))
      {
        BeanContext localBeanContext = getBeanContextPeer();
        if ((localBeanContext.equals(paramPropertyChangeEvent.getOldValue())) && (!localBeanContext.equals(paramPropertyChangeEvent.getNewValue()))) {
          remove(localObject1, false);
        } else {
          ((BCSChild)children.get(localObject1)).setRemovePending(false);
        }
      }
    }
  }
  
  protected boolean validatePendingAdd(Object paramObject)
  {
    return true;
  }
  
  protected boolean validatePendingRemove(Object paramObject)
  {
    return true;
  }
  
  protected void childJustAddedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected void childJustRemovedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected static final Visibility getChildVisibility(Object paramObject)
  {
    try
    {
      return (Visibility)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final Serializable getChildSerializable(Object paramObject)
  {
    try
    {
      return (Serializable)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final PropertyChangeListener getChildPropertyChangeListener(Object paramObject)
  {
    try
    {
      return (PropertyChangeListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final VetoableChangeListener getChildVetoableChangeListener(Object paramObject)
  {
    try
    {
      return (VetoableChangeListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(Object paramObject)
  {
    try
    {
      return (BeanContextMembershipListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final BeanContextChild getChildBeanContextChild(Object paramObject)
  {
    try
    {
      BeanContextChild localBeanContextChild = (BeanContextChild)paramObject;
      if (((paramObject instanceof BeanContextChild)) && ((paramObject instanceof BeanContextProxy))) {
        throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy");
      }
      return localBeanContextChild;
    }
    catch (ClassCastException localClassCastException1)
    {
      try
      {
        return ((BeanContextProxy)paramObject).getBeanContextProxy();
      }
      catch (ClassCastException localClassCastException2) {}
    }
    return null;
  }
  
  protected final void fireChildrenAdded(BeanContextMembershipEvent paramBeanContextMembershipEvent)
  {
    Object[] arrayOfObject;
    synchronized (bcmListeners)
    {
      arrayOfObject = bcmListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextMembershipListener)arrayOfObject[i]).childrenAdded(paramBeanContextMembershipEvent);
    }
  }
  
  protected final void fireChildrenRemoved(BeanContextMembershipEvent paramBeanContextMembershipEvent)
  {
    Object[] arrayOfObject;
    synchronized (bcmListeners)
    {
      arrayOfObject = bcmListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextMembershipListener)arrayOfObject[i]).childrenRemoved(paramBeanContextMembershipEvent);
    }
  }
  
  protected synchronized void initialize()
  {
    children = new HashMap(serializable + 1);
    bcmListeners = new ArrayList(1);
    childPCL = new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        BeanContextSupport.this.propertyChange(paramAnonymousPropertyChangeEvent);
      }
    };
    childVCL = new VetoableChangeListener()
    {
      public void vetoableChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
        throws PropertyVetoException
      {
        BeanContextSupport.this.vetoableChange(paramAnonymousPropertyChangeEvent);
      }
    };
  }
  
  /* Error */
  protected final Object[] copyChildren()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 406	java/beans/beancontext/BeanContextSupport:children	Ljava/util/HashMap;
    //   11: invokevirtual 484	java/util/HashMap:keySet	()Ljava/util/Set;
    //   14: invokeinterface 508 1 0
    //   19: aload_1
    //   20: monitorexit
    //   21: areturn
    //   22: astore_2
    //   23: aload_1
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	BeanContextSupport
    //   5	19	1	Ljava/lang/Object;	Object
    //   22	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	21	22	finally
    //   22	25	22	finally
  }
  
  protected static final boolean classEquals(Class paramClass1, Class paramClass2)
  {
    return (paramClass1.equals(paramClass2)) || (paramClass1.getName().equals(paramClass2.getName()));
  }
  
  protected class BCSChild
    implements Serializable
  {
    private static final long serialVersionUID = -5815286101609939109L;
    private Object child;
    private Object proxyPeer;
    private transient boolean removePending;
    
    BCSChild(Object paramObject1, Object paramObject2)
    {
      child = paramObject1;
      proxyPeer = paramObject2;
    }
    
    Object getChild()
    {
      return child;
    }
    
    void setRemovePending(boolean paramBoolean)
    {
      removePending = paramBoolean;
    }
    
    boolean isRemovePending()
    {
      return removePending;
    }
    
    boolean isProxyPeer()
    {
      return proxyPeer != null;
    }
    
    Object getProxyPeer()
    {
      return proxyPeer;
    }
  }
  
  protected static final class BCSIterator
    implements Iterator
  {
    private Iterator src;
    
    BCSIterator(Iterator paramIterator)
    {
      src = paramIterator;
    }
    
    public boolean hasNext()
    {
      return src.hasNext();
    }
    
    public Object next()
    {
      return src.next();
    }
    
    public void remove() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */