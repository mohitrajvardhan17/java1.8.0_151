package com.sun.jmx.snmp;

import com.sun.jmx.mbeanserver.Util;
import java.util.Vector;

public class SnmpOidDatabaseSupport
  implements SnmpOidDatabase
{
  private Vector<SnmpOidTable> tables = new Vector();
  
  public SnmpOidDatabaseSupport() {}
  
  public SnmpOidDatabaseSupport(SnmpOidTable paramSnmpOidTable)
  {
    tables.addElement(paramSnmpOidTable);
  }
  
  public void add(SnmpOidTable paramSnmpOidTable)
  {
    if (!tables.contains(paramSnmpOidTable)) {
      tables.addElement(paramSnmpOidTable);
    }
  }
  
  public void remove(SnmpOidTable paramSnmpOidTable)
    throws SnmpStatusException
  {
    if (!tables.contains(paramSnmpOidTable)) {
      throw new SnmpStatusException("The specified SnmpOidTable does not exist in this SnmpOidDatabase");
    }
    tables.removeElement(paramSnmpOidTable);
  }
  
  public SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException
  {
    int i = 0;
    while (i < tables.size()) {
      try
      {
        return ((SnmpOidTable)tables.elementAt(i)).resolveVarName(paramString);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        if (i == tables.size() - 1) {
          throw new SnmpStatusException(localSnmpStatusException.getMessage());
        }
        i++;
      }
    }
    return null;
  }
  
  public SnmpOidRecord resolveVarOid(String paramString)
    throws SnmpStatusException
  {
    int i = 0;
    while (i < tables.size()) {
      try
      {
        return ((SnmpOidTable)tables.elementAt(i)).resolveVarOid(paramString);
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        if (i == tables.size() - 1) {
          throw new SnmpStatusException(localSnmpStatusException.getMessage());
        }
        i++;
      }
    }
    return null;
  }
  
  public Vector<?> getAllEntries()
  {
    Vector localVector1 = new Vector();
    for (int i = 0; i < tables.size(); i++)
    {
      Vector localVector2 = (Vector)Util.cast(((SnmpOidTable)tables.elementAt(i)).getAllEntries());
      if (localVector2 != null) {
        for (int j = 0; j < localVector2.size(); j++) {
          localVector1.addElement(localVector2.elementAt(j));
        }
      }
    }
    return localVector1;
  }
  
  public void removeAll()
  {
    tables.removeAllElements();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOidDatabaseSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */