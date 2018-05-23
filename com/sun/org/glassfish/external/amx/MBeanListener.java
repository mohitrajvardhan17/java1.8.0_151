package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

@Taxonomy(stability=Stability.UNCOMMITTED)
public class MBeanListener<T extends Callback>
  implements NotificationListener
{
  private final String mJMXDomain;
  private final String mType;
  private final String mName;
  private final ObjectName mObjectName;
  private final MBeanServerConnection mMBeanServer;
  private final T mCallback;
  
  private static void debug(Object paramObject)
  {
    System.out.println("" + paramObject);
  }
  
  public String toString()
  {
    return "MBeanListener: ObjectName=" + mObjectName + ", type=" + mType + ", name=" + mName;
  }
  
  public String getType()
  {
    return mType;
  }
  
  public String getName()
  {
    return mName;
  }
  
  public MBeanServerConnection getMBeanServer()
  {
    return mMBeanServer;
  }
  
  public T getCallback()
  {
    return mCallback;
  }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, T paramT)
  {
    mMBeanServer = paramMBeanServerConnection;
    mObjectName = paramObjectName;
    mJMXDomain = null;
    mType = null;
    mName = null;
    mCallback = paramT;
  }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, String paramString1, String paramString2, T paramT)
  {
    this(paramMBeanServerConnection, paramString1, paramString2, null, paramT);
  }
  
  public MBeanListener(MBeanServerConnection paramMBeanServerConnection, String paramString1, String paramString2, String paramString3, T paramT)
  {
    mMBeanServer = paramMBeanServerConnection;
    mJMXDomain = paramString1;
    mType = paramString2;
    mName = paramString3;
    mObjectName = null;
    mCallback = paramT;
  }
  
  private boolean isRegistered(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName)
  {
    try
    {
      return paramMBeanServerConnection.isRegistered(paramObjectName);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }
  
  public void startListening()
  {
    try
    {
      mMBeanServer.addNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this, null, this);
    }
    catch (Exception localException1)
    {
      throw new RuntimeException("Can't add NotificationListener", localException1);
    }
    if (mObjectName != null)
    {
      if (isRegistered(mMBeanServer, mObjectName)) {
        mCallback.mbeanRegistered(mObjectName, this);
      }
    }
    else
    {
      String str = "type=" + mType;
      if (mName != null) {
        str = str + "," + "name" + mName;
      }
      ObjectName localObjectName1 = AMXUtil.newObjectName(mJMXDomain + ":" + str);
      try
      {
        Set localSet = mMBeanServer.queryNames(localObjectName1, null);
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          ObjectName localObjectName2 = (ObjectName)localIterator.next();
          mCallback.mbeanRegistered(localObjectName2, this);
        }
      }
      catch (Exception localException2)
      {
        throw new RuntimeException(localException2);
      }
    }
  }
  
  public void stopListening()
  {
    try
    {
      mMBeanServer.removeNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this);
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Can't remove NotificationListener " + this, localException);
    }
  }
  
  public void handleNotification(Notification paramNotification, Object paramObject)
  {
    if ((paramNotification instanceof MBeanServerNotification))
    {
      MBeanServerNotification localMBeanServerNotification = (MBeanServerNotification)paramNotification;
      ObjectName localObjectName = localMBeanServerNotification.getMBeanName();
      int i = 0;
      String str;
      if ((mObjectName != null) && (mObjectName.equals(localObjectName)))
      {
        i = 1;
      }
      else if ((localObjectName.getDomain().equals(mJMXDomain)) && (mType != null) && (mType.equals(localObjectName.getKeyProperty("type"))))
      {
        str = localObjectName.getKeyProperty("name");
        if ((mName != null) && (mName.equals(str))) {
          i = 1;
        }
      }
      if (i != 0)
      {
        str = localMBeanServerNotification.getType();
        if ("JMX.mbean.registered".equals(str)) {
          mCallback.mbeanRegistered(localObjectName, this);
        } else if ("JMX.mbean.unregistered".equals(str)) {
          mCallback.mbeanUnregistered(localObjectName, this);
        }
      }
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void mbeanRegistered(ObjectName paramObjectName, MBeanListener paramMBeanListener);
    
    public abstract void mbeanUnregistered(ObjectName paramObjectName, MBeanListener paramMBeanListener);
  }
  
  public static class CallbackImpl
    implements MBeanListener.Callback
  {
    private volatile ObjectName mRegistered = null;
    private volatile ObjectName mUnregistered = null;
    private final boolean mStopAtFirst;
    protected final CountDownLatch mLatch = new CountDownLatch(1);
    
    public CallbackImpl()
    {
      this(true);
    }
    
    public CallbackImpl(boolean paramBoolean)
    {
      mStopAtFirst = paramBoolean;
    }
    
    public ObjectName getRegistered()
    {
      return mRegistered;
    }
    
    public ObjectName getUnregistered()
    {
      return mUnregistered;
    }
    
    public void await()
    {
      try
      {
        mLatch.await();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
    }
    
    public void mbeanRegistered(ObjectName paramObjectName, MBeanListener paramMBeanListener)
    {
      mRegistered = paramObjectName;
      if (mStopAtFirst) {
        paramMBeanListener.stopListening();
      }
    }
    
    public void mbeanUnregistered(ObjectName paramObjectName, MBeanListener paramMBeanListener)
    {
      mUnregistered = paramObjectName;
      if (mStopAtFirst) {
        paramMBeanListener.stopListening();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\amx\MBeanListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */