package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpOidTableSupport
  implements SnmpOidTable
{
  private Hashtable<String, SnmpOidRecord> oidStore = new Hashtable();
  private String myName;
  
  public SnmpOidTableSupport(String paramString)
  {
    myName = paramString;
  }
  
  public SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException
  {
    SnmpOidRecord localSnmpOidRecord = (SnmpOidRecord)oidStore.get(paramString);
    if (localSnmpOidRecord != null) {
      return localSnmpOidRecord;
    }
    throw new SnmpStatusException("Variable name <" + paramString + "> not found in Oid repository");
  }
  
  public SnmpOidRecord resolveVarOid(String paramString)
    throws SnmpStatusException
  {
    int i = paramString.indexOf('.');
    if (i < 0) {
      throw new SnmpStatusException("Variable oid <" + paramString + "> not found in Oid repository");
    }
    if (i == 0) {
      paramString = paramString.substring(1, paramString.length());
    }
    Enumeration localEnumeration = oidStore.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpOidRecord localSnmpOidRecord = (SnmpOidRecord)localEnumeration.nextElement();
      if (localSnmpOidRecord.getOid().equals(paramString)) {
        return localSnmpOidRecord;
      }
    }
    throw new SnmpStatusException("Variable oid <" + paramString + "> not found in Oid repository");
  }
  
  public Vector<SnmpOidRecord> getAllEntries()
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = oidStore.elements();
    while (localEnumeration.hasMoreElements()) {
      localVector.addElement(localEnumeration.nextElement());
    }
    return localVector;
  }
  
  public synchronized void loadMib(SnmpOidRecord[] paramArrayOfSnmpOidRecord)
  {
    try
    {
      for (int i = 0;; i++)
      {
        SnmpOidRecord localSnmpOidRecord = paramArrayOfSnmpOidRecord[i];
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpOidTableSupport.class.getName(), "loadMib", "Load " + localSnmpOidRecord.getName());
        }
        oidStore.put(localSnmpOidRecord.getName(), localSnmpOidRecord);
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SnmpOidTableSupport)) {
      return false;
    }
    SnmpOidTableSupport localSnmpOidTableSupport = (SnmpOidTableSupport)paramObject;
    return myName.equals(localSnmpOidTableSupport.getName());
  }
  
  public int hashCode()
  {
    return Objects.hashCode(myName);
  }
  
  public String getName()
  {
    return myName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOidTableSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */