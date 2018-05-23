package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.RuntimeOperationsException;

public class Repository
{
  private final Map<String, Map<String, NamedObject>> domainTb;
  private volatile int nbElements = 0;
  private final String domain;
  private final ReentrantReadWriteLock lock;
  
  private void addAllMatching(Map<String, NamedObject> paramMap, Set<NamedObject> paramSet, ObjectNamePattern paramObjectNamePattern)
  {
    synchronized (paramMap)
    {
      Iterator localIterator = paramMap.values().iterator();
      while (localIterator.hasNext())
      {
        NamedObject localNamedObject = (NamedObject)localIterator.next();
        ObjectName localObjectName = localNamedObject.getName();
        if (paramObjectNamePattern.matchKeys(localObjectName)) {
          paramSet.add(localNamedObject);
        }
      }
    }
  }
  
  private void addNewDomMoi(DynamicMBean paramDynamicMBean, String paramString, ObjectName paramObjectName, RegistrationContext paramRegistrationContext)
  {
    HashMap localHashMap = new HashMap();
    String str = paramObjectName.getCanonicalKeyPropertyListString();
    addMoiToTb(paramDynamicMBean, paramObjectName, str, localHashMap, paramRegistrationContext);
    domainTb.put(paramString, localHashMap);
    nbElements += 1;
  }
  
  private void registering(RegistrationContext paramRegistrationContext)
  {
    if (paramRegistrationContext == null) {
      return;
    }
    try
    {
      paramRegistrationContext.registering();
    }
    catch (RuntimeOperationsException localRuntimeOperationsException)
    {
      throw localRuntimeOperationsException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new RuntimeOperationsException(localRuntimeException);
    }
  }
  
  private void unregistering(RegistrationContext paramRegistrationContext, ObjectName paramObjectName)
  {
    if (paramRegistrationContext == null) {
      return;
    }
    try
    {
      paramRegistrationContext.unregistered();
    }
    catch (Exception localException)
    {
      JmxProperties.MBEANSERVER_LOGGER.log(Level.FINE, "Unexpected exception while unregistering " + paramObjectName, localException);
    }
  }
  
  private void addMoiToTb(DynamicMBean paramDynamicMBean, ObjectName paramObjectName, String paramString, Map<String, NamedObject> paramMap, RegistrationContext paramRegistrationContext)
  {
    registering(paramRegistrationContext);
    paramMap.put(paramString, new NamedObject(paramObjectName, paramDynamicMBean));
  }
  
  private NamedObject retrieveNamedObject(ObjectName paramObjectName)
  {
    if (paramObjectName.isPattern()) {
      return null;
    }
    String str = paramObjectName.getDomain().intern();
    if (str.length() == 0) {
      str = domain;
    }
    Map localMap = (Map)domainTb.get(str);
    if (localMap == null) {
      return null;
    }
    return (NamedObject)localMap.get(paramObjectName.getCanonicalKeyPropertyListString());
  }
  
  public Repository(String paramString)
  {
    this(paramString, true);
  }
  
  public Repository(String paramString, boolean paramBoolean)
  {
    lock = new ReentrantReadWriteLock(paramBoolean);
    domainTb = new HashMap(5);
    if ((paramString != null) && (paramString.length() != 0)) {
      domain = paramString.intern();
    } else {
      domain = "DefaultDomain";
    }
    domainTb.put(domain, new HashMap());
  }
  
  public String[] getDomains()
  {
    lock.readLock().lock();
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList(domainTb.size());
      Iterator localIterator = domainTb.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Map localMap = (Map)localEntry.getValue();
        if ((localMap != null) && (localMap.size() != 0)) {
          localArrayList.add(localEntry.getKey());
        }
      }
    }
    finally
    {
      lock.readLock().unlock();
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public void addMBean(DynamicMBean paramDynamicMBean, ObjectName paramObjectName, RegistrationContext paramRegistrationContext)
    throws InstanceAlreadyExistsException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "addMBean", "name = " + paramObjectName);
    }
    String str1 = paramObjectName.getDomain().intern();
    int i = 0;
    if (str1.length() == 0) {
      paramObjectName = Util.newObjectName(domain + paramObjectName.toString());
    }
    if (str1 == domain)
    {
      i = 1;
      str1 = domain;
    }
    else
    {
      i = 0;
    }
    if (paramObjectName.isPattern()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Repository: cannot add mbean for pattern name " + paramObjectName.toString()));
    }
    lock.writeLock().lock();
    try
    {
      if ((i == 0) && (str1.equals("JMImplementation")) && (domainTb.containsKey("JMImplementation"))) {
        throw new RuntimeOperationsException(new IllegalArgumentException("Repository: domain name cannot be JMImplementation"));
      }
      Map localMap = (Map)domainTb.get(str1);
      if (localMap == null)
      {
        addNewDomMoi(paramDynamicMBean, str1, paramObjectName, paramRegistrationContext);
        return;
      }
      String str2 = paramObjectName.getCanonicalKeyPropertyListString();
      NamedObject localNamedObject = (NamedObject)localMap.get(str2);
      if (localNamedObject != null) {
        throw new InstanceAlreadyExistsException(paramObjectName.toString());
      }
      nbElements += 1;
      addMoiToTb(paramDynamicMBean, paramObjectName, str2, localMap, paramRegistrationContext);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
  
  public boolean contains(ObjectName paramObjectName)
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "contains", " name = " + paramObjectName);
    }
    lock.readLock().lock();
    try
    {
      boolean bool = retrieveNamedObject(paramObjectName) != null;
      return bool;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
  
  public DynamicMBean retrieve(ObjectName paramObjectName)
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "retrieve", "name = " + paramObjectName);
    }
    lock.readLock().lock();
    try
    {
      NamedObject localNamedObject = retrieveNamedObject(paramObjectName);
      if (localNamedObject == null)
      {
        localDynamicMBean = null;
        return localDynamicMBean;
      }
      DynamicMBean localDynamicMBean = localNamedObject.getObject();
      return localDynamicMBean;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
  
  public Set<NamedObject> query(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    HashSet localHashSet = new HashSet();
    ObjectName localObjectName;
    if ((paramObjectName == null) || (paramObjectName.getCanonicalName().length() == 0) || (paramObjectName.equals(ObjectName.WILDCARD))) {
      localObjectName = ObjectName.WILDCARD;
    } else {
      localObjectName = paramObjectName;
    }
    lock.readLock().lock();
    try
    {
      Object localObject2;
      if (!localObjectName.isPattern())
      {
        localObject1 = retrieveNamedObject(localObjectName);
        if (localObject1 != null) {
          localHashSet.add(localObject1);
        }
        localObject2 = localHashSet;
        return (Set<NamedObject>)localObject2;
      }
      if (localObjectName == ObjectName.WILDCARD)
      {
        localObject1 = domainTb.values().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map)((Iterator)localObject1).next();
          localHashSet.addAll(((Map)localObject2).values());
        }
        localObject1 = localHashSet;
        return (Set<NamedObject>)localObject1;
      }
      Object localObject1 = localObjectName.getCanonicalKeyPropertyListString();
      int i = ((String)localObject1).length() == 0 ? 1 : 0;
      ObjectNamePattern localObjectNamePattern = i != 0 ? null : new ObjectNamePattern(localObjectName);
      if (localObjectName.getDomain().length() == 0)
      {
        localObject3 = (Map)domainTb.get(domain);
        if (i != 0) {
          localHashSet.addAll(((Map)localObject3).values());
        } else {
          addAllMatching((Map)localObject3, localHashSet, localObjectNamePattern);
        }
        localObject4 = localHashSet;
        return (Set<NamedObject>)localObject4;
      }
      if (!localObjectName.isDomainPattern())
      {
        localObject3 = (Map)domainTb.get(localObjectName.getDomain());
        if (localObject3 == null)
        {
          localObject4 = Collections.emptySet();
          return (Set<NamedObject>)localObject4;
        }
        if (i != 0) {
          localHashSet.addAll(((Map)localObject3).values());
        } else {
          addAllMatching((Map)localObject3, localHashSet, localObjectNamePattern);
        }
        localObject4 = localHashSet;
        return (Set<NamedObject>)localObject4;
      }
      Object localObject3 = localObjectName.getDomain();
      Object localObject4 = domainTb.keySet().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        String str = (String)((Iterator)localObject4).next();
        if (Util.wildmatch(str, (String)localObject3))
        {
          Map localMap = (Map)domainTb.get(str);
          if (i != 0) {
            localHashSet.addAll(localMap.values());
          } else {
            addAllMatching(localMap, localHashSet, localObjectNamePattern);
          }
        }
      }
      localObject4 = localHashSet;
      return (Set<NamedObject>)localObject4;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
  
  public void remove(ObjectName paramObjectName, RegistrationContext paramRegistrationContext)
    throws InstanceNotFoundException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "remove", "name = " + paramObjectName);
    }
    String str = paramObjectName.getDomain().intern();
    if (str.length() == 0) {
      str = domain;
    }
    lock.writeLock().lock();
    try
    {
      Map localMap = (Map)domainTb.get(str);
      if (localMap == null) {
        throw new InstanceNotFoundException(paramObjectName.toString());
      }
      if (localMap.remove(paramObjectName.getCanonicalKeyPropertyListString()) == null) {
        throw new InstanceNotFoundException(paramObjectName.toString());
      }
      nbElements -= 1;
      if (localMap.isEmpty())
      {
        domainTb.remove(str);
        if (str == domain) {
          domainTb.put(domain, new HashMap());
        }
      }
      unregistering(paramRegistrationContext, paramObjectName);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
  
  public Integer getCount()
  {
    return Integer.valueOf(nbElements);
  }
  
  public String getDefaultDomain()
  {
    return domain;
  }
  
  private static final class ObjectNamePattern
  {
    private final String[] keys;
    private final String[] values;
    private final String properties;
    private final boolean isPropertyListPattern;
    private final boolean isPropertyValuePattern;
    public final ObjectName pattern;
    
    public ObjectNamePattern(ObjectName paramObjectName)
    {
      this(paramObjectName.isPropertyListPattern(), paramObjectName.isPropertyValuePattern(), paramObjectName.getCanonicalKeyPropertyListString(), paramObjectName.getKeyPropertyList(), paramObjectName);
    }
    
    ObjectNamePattern(boolean paramBoolean1, boolean paramBoolean2, String paramString, Map<String, String> paramMap, ObjectName paramObjectName)
    {
      isPropertyListPattern = paramBoolean1;
      isPropertyValuePattern = paramBoolean2;
      properties = paramString;
      int i = paramMap.size();
      keys = new String[i];
      values = new String[i];
      int j = 0;
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        keys[j] = ((String)localEntry.getKey());
        values[j] = ((String)localEntry.getValue());
        j++;
      }
      pattern = paramObjectName;
    }
    
    public boolean matchKeys(ObjectName paramObjectName)
    {
      if ((isPropertyValuePattern) && (!isPropertyListPattern) && (paramObjectName.getKeyPropertyList().size() != keys.length)) {
        return false;
      }
      if ((isPropertyValuePattern) || (isPropertyListPattern))
      {
        for (int i = keys.length - 1; i >= 0; i--)
        {
          str2 = paramObjectName.getKeyProperty(keys[i]);
          if (str2 == null) {
            return false;
          }
          if ((isPropertyValuePattern) && (pattern.isPropertyValuePattern(keys[i])))
          {
            if (!Util.wildmatch(str2, values[i])) {
              return false;
            }
          }
          else if (!str2.equals(values[i])) {
            return false;
          }
        }
        return true;
      }
      String str1 = paramObjectName.getCanonicalKeyPropertyListString();
      String str2 = properties;
      return str1.equals(str2);
    }
  }
  
  public static abstract interface RegistrationContext
  {
    public abstract void registering();
    
    public abstract void unregistered();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\Repository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */