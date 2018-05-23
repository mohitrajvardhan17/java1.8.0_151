package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MonitoredObjectImpl
  implements MonitoredObject
{
  private final String name;
  private final String description;
  private Map children = new HashMap();
  private Map monitoredAttributes = new HashMap();
  private MonitoredObject parent = null;
  
  MonitoredObjectImpl(String paramString1, String paramString2)
  {
    name = paramString1;
    description = paramString2;
  }
  
  /* Error */
  public MonitoredObject getChild(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_2
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 86	com/sun/corba/se/impl/monitoring/MonitoredObjectImpl:children	Ljava/util/Map;
    //   8: aload_1
    //   9: invokeinterface 99 2 0
    //   14: checkcast 49	com/sun/corba/se/spi/monitoring/MonitoredObject
    //   17: aload_2
    //   18: monitorexit
    //   19: areturn
    //   20: astore_3
    //   21: aload_2
    //   22: monitorexit
    //   23: aload_3
    //   24: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	MonitoredObjectImpl
    //   0	25	1	paramString	String
    //   2	20	2	Ljava/lang/Object;	Object
    //   20	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	19	20	finally
    //   20	23	20	finally
  }
  
  /* Error */
  public Collection getChildren()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 86	com/sun/corba/se/impl/monitoring/MonitoredObjectImpl:children	Ljava/util/Map;
    //   8: invokeinterface 98 1 0
    //   13: aload_1
    //   14: monitorexit
    //   15: areturn
    //   16: astore_2
    //   17: aload_1
    //   18: monitorexit
    //   19: aload_2
    //   20: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	21	0	this	MonitoredObjectImpl
    //   2	16	1	Ljava/lang/Object;	Object
    //   16	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	15	16	finally
    //   16	19	16	finally
  }
  
  public void addChild(MonitoredObject paramMonitoredObject)
  {
    if (paramMonitoredObject != null) {
      synchronized (this)
      {
        children.put(paramMonitoredObject.getName(), paramMonitoredObject);
        paramMonitoredObject.setParent(this);
      }
    }
  }
  
  public void removeChild(String paramString)
  {
    if (paramString != null) {
      synchronized (this)
      {
        children.remove(paramString);
      }
    }
  }
  
  public synchronized MonitoredObject getParent()
  {
    return parent;
  }
  
  public synchronized void setParent(MonitoredObject paramMonitoredObject)
  {
    parent = paramMonitoredObject;
  }
  
  /* Error */
  public MonitoredAttribute getAttribute(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_2
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 87	com/sun/corba/se/impl/monitoring/MonitoredObjectImpl:monitoredAttributes	Ljava/util/Map;
    //   8: aload_1
    //   9: invokeinterface 99 2 0
    //   14: checkcast 48	com/sun/corba/se/spi/monitoring/MonitoredAttribute
    //   17: aload_2
    //   18: monitorexit
    //   19: areturn
    //   20: astore_3
    //   21: aload_2
    //   22: monitorexit
    //   23: aload_3
    //   24: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	MonitoredObjectImpl
    //   0	25	1	paramString	String
    //   2	20	2	Ljava/lang/Object;	Object
    //   20	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	19	20	finally
    //   20	23	20	finally
  }
  
  /* Error */
  public Collection getAttributes()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 87	com/sun/corba/se/impl/monitoring/MonitoredObjectImpl:monitoredAttributes	Ljava/util/Map;
    //   8: invokeinterface 98 1 0
    //   13: aload_1
    //   14: monitorexit
    //   15: areturn
    //   16: astore_2
    //   17: aload_1
    //   18: monitorexit
    //   19: aload_2
    //   20: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	21	0	this	MonitoredObjectImpl
    //   2	16	1	Ljava/lang/Object;	Object
    //   16	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	15	16	finally
    //   16	19	16	finally
  }
  
  public void addAttribute(MonitoredAttribute paramMonitoredAttribute)
  {
    if (paramMonitoredAttribute != null) {
      synchronized (this)
      {
        monitoredAttributes.put(paramMonitoredAttribute.getName(), paramMonitoredAttribute);
      }
    }
  }
  
  public void removeAttribute(String paramString)
  {
    if (paramString != null) {
      synchronized (this)
      {
        monitoredAttributes.remove(paramString);
      }
    }
  }
  
  public void clearState()
  {
    synchronized (this)
    {
      Iterator localIterator = monitoredAttributes.values().iterator();
      while (localIterator.hasNext()) {
        ((MonitoredAttribute)localIterator.next()).clearState();
      }
      localIterator = children.values().iterator();
      while (localIterator.hasNext()) {
        ((MonitoredObject)localIterator.next()).clearState();
      }
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoredObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */