package com.sun.jmx.snmp;

import java.util.Enumeration;
import java.util.Vector;

public class SnmpVarBindList
  extends Vector<SnmpVarBind>
{
  private static final long serialVersionUID = -7203997794636430321L;
  public String identity = "VarBindList ";
  Timestamp timestamp;
  
  public SnmpVarBindList()
  {
    super(5, 5);
  }
  
  public SnmpVarBindList(int paramInt)
  {
    super(paramInt);
  }
  
  public SnmpVarBindList(String paramString)
  {
    super(5, 5);
    identity = paramString;
  }
  
  public SnmpVarBindList(SnmpVarBindList paramSnmpVarBindList)
  {
    super(paramSnmpVarBindList.size(), 5);
    paramSnmpVarBindList.copyInto(elementData);
    elementCount = paramSnmpVarBindList.size();
  }
  
  public SnmpVarBindList(Vector<SnmpVarBind> paramVector)
  {
    super(paramVector.size(), 5);
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      addElement(localSnmpVarBind.clone());
    }
  }
  
  public SnmpVarBindList(String paramString, Vector<SnmpVarBind> paramVector)
  {
    this(paramVector);
  }
  
  public Timestamp getTimestamp()
  {
    return timestamp;
  }
  
  public void setTimestamp(Timestamp paramTimestamp)
  {
    timestamp = paramTimestamp;
  }
  
  public final synchronized SnmpVarBind getVarBindAt(int paramInt)
  {
    return (SnmpVarBind)elementAt(paramInt);
  }
  
  public synchronized int getVarBindCount()
  {
    return size();
  }
  
  public synchronized Enumeration<SnmpVarBind> getVarBindList()
  {
    return elements();
  }
  
  public final synchronized void setVarBindList(Vector<SnmpVarBind> paramVector)
  {
    setVarBindList(paramVector, false);
  }
  
  public final synchronized void setVarBindList(Vector<SnmpVarBind> paramVector, boolean paramBoolean)
  {
    synchronized (paramVector)
    {
      int i = paramVector.size();
      setSize(i);
      paramVector.copyInto(elementData);
      if (paramBoolean) {
        for (int j = 0; j < i; j++)
        {
          SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
          elementData[j] = localSnmpVarBind.clone();
        }
      }
    }
  }
  
  public synchronized void addVarBindList(SnmpVarBindList paramSnmpVarBindList)
  {
    ensureCapacity(paramSnmpVarBindList.size() + size());
    for (int i = 0; i < paramSnmpVarBindList.size(); i++) {
      addElement(paramSnmpVarBindList.getVarBindAt(i));
    }
  }
  
  public synchronized boolean removeVarBindList(SnmpVarBindList paramSnmpVarBindList)
  {
    boolean bool = true;
    for (int i = 0; i < paramSnmpVarBindList.size(); i++) {
      bool = removeElement(paramSnmpVarBindList.getVarBindAt(i));
    }
    return bool;
  }
  
  public final synchronized void replaceVarBind(SnmpVarBind paramSnmpVarBind, int paramInt)
  {
    setElementAt(paramSnmpVarBind, paramInt);
  }
  
  public final synchronized void addVarBind(String[] paramArrayOfString, String paramString)
    throws SnmpStatusException
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      SnmpVarBind localSnmpVarBind = new SnmpVarBind(paramArrayOfString[i]);
      localSnmpVarBind.addInstance(paramString);
      addElement(localSnmpVarBind);
    }
  }
  
  public synchronized boolean removeVarBind(String[] paramArrayOfString, String paramString)
    throws SnmpStatusException
  {
    boolean bool = true;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      SnmpVarBind localSnmpVarBind = new SnmpVarBind(paramArrayOfString[i]);
      localSnmpVarBind.addInstance(paramString);
      int j = indexOfOid(localSnmpVarBind);
      try
      {
        removeElementAt(j);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public synchronized void addVarBind(String[] paramArrayOfString)
    throws SnmpStatusException
  {
    addVarBind(paramArrayOfString, null);
  }
  
  public synchronized boolean removeVarBind(String[] paramArrayOfString)
    throws SnmpStatusException
  {
    return removeVarBind(paramArrayOfString, null);
  }
  
  public synchronized void addVarBind(String paramString)
    throws SnmpStatusException
  {
    SnmpVarBind localSnmpVarBind = new SnmpVarBind(paramString);
    addVarBind(localSnmpVarBind);
  }
  
  public synchronized boolean removeVarBind(String paramString)
    throws SnmpStatusException
  {
    SnmpVarBind localSnmpVarBind = new SnmpVarBind(paramString);
    int i = indexOfOid(localSnmpVarBind);
    try
    {
      removeElementAt(i);
      return true;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return false;
  }
  
  public synchronized void addVarBind(SnmpVarBind paramSnmpVarBind)
  {
    addElement(paramSnmpVarBind);
  }
  
  public synchronized boolean removeVarBind(SnmpVarBind paramSnmpVarBind)
  {
    return removeElement(paramSnmpVarBind);
  }
  
  public synchronized void addInstance(String paramString)
    throws SnmpStatusException
  {
    int i = size();
    for (int j = 0; j < i; j++) {
      ((SnmpVarBind)elementData[j]).addInstance(paramString);
    }
  }
  
  public final synchronized void concat(Vector<SnmpVarBind> paramVector)
  {
    ensureCapacity(size() + paramVector.size());
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements()) {
      addElement(localEnumeration.nextElement());
    }
  }
  
  public synchronized boolean checkForValidValues()
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
      if (!localSnmpVarBind.isValidValue()) {
        return false;
      }
    }
    return true;
  }
  
  public synchronized boolean checkForUnspecifiedValue()
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
      if (localSnmpVarBind.isUnspecifiedValue()) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized SnmpVarBindList splitAt(int paramInt)
  {
    SnmpVarBindList localSnmpVarBindList = null;
    if (paramInt > elementCount) {
      return localSnmpVarBindList;
    }
    localSnmpVarBindList = new SnmpVarBindList();
    int i = size();
    for (int j = paramInt; j < i; j++) {
      localSnmpVarBindList.addElement((SnmpVarBind)elementData[j]);
    }
    elementCount = paramInt;
    trimToSize();
    return localSnmpVarBindList;
  }
  
  public synchronized int indexOfOid(SnmpVarBind paramSnmpVarBind, int paramInt1, int paramInt2)
  {
    SnmpOid localSnmpOid = paramSnmpVarBind.getOid();
    for (int i = paramInt1; i < paramInt2; i++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[i];
      if (localSnmpOid.equals(localSnmpVarBind.getOid())) {
        return i;
      }
    }
    return -1;
  }
  
  public synchronized int indexOfOid(SnmpVarBind paramSnmpVarBind)
  {
    return indexOfOid(paramSnmpVarBind, 0, size());
  }
  
  public synchronized int indexOfOid(SnmpOid paramSnmpOid)
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
      if (paramSnmpOid.equals(localSnmpVarBind.getOid())) {
        return j;
      }
    }
    return -1;
  }
  
  public synchronized SnmpVarBindList cloneWithValue()
  {
    SnmpVarBindList localSnmpVarBindList = new SnmpVarBindList();
    localSnmpVarBindList.setTimestamp(getTimestamp());
    localSnmpVarBindList.ensureCapacity(size());
    for (int i = 0; i < size(); i++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[i];
      localSnmpVarBindList.addElement(localSnmpVarBind.clone());
    }
    return localSnmpVarBindList;
  }
  
  public synchronized SnmpVarBindList cloneWithoutValue()
  {
    SnmpVarBindList localSnmpVarBindList = new SnmpVarBindList();
    int i = size();
    localSnmpVarBindList.ensureCapacity(i);
    for (int j = 0; j < i; j++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
      localSnmpVarBindList.addElement((SnmpVarBind)localSnmpVarBind.cloneWithoutValue());
    }
    return localSnmpVarBindList;
  }
  
  public synchronized SnmpVarBindList clone()
  {
    return cloneWithValue();
  }
  
  public synchronized Vector<SnmpVarBind> toVector(boolean paramBoolean)
  {
    int i = elementCount;
    if (!paramBoolean) {
      return new Vector(this);
    }
    Vector localVector = new Vector(i, 5);
    for (int j = 0; j < i; j++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[j];
      localVector.addElement(localSnmpVarBind.clone());
    }
    return localVector;
  }
  
  public String oidListToString()
  {
    StringBuilder localStringBuilder = new StringBuilder(300);
    for (int i = 0; i < elementCount; i++)
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)elementData[i];
      localStringBuilder.append(localSnmpVarBind.getOid().toString()).append("\n");
    }
    return localStringBuilder.toString();
  }
  
  public synchronized String varBindListToString()
  {
    StringBuilder localStringBuilder = new StringBuilder(300);
    for (int i = 0; i < elementCount; i++) {
      localStringBuilder.append(elementData[i].toString()).append("\n");
    }
    return localStringBuilder.toString();
  }
  
  protected void finalize()
  {
    removeAllElements();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpVarBindList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */