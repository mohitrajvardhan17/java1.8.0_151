package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpIndex
  implements Serializable
{
  private static final long serialVersionUID = 8712159739982192146L;
  private Vector<SnmpOid> oids = new Vector();
  private int size = 0;
  
  public SnmpIndex(SnmpOid[] paramArrayOfSnmpOid)
  {
    size = paramArrayOfSnmpOid.length;
    for (int i = 0; i < size; i++) {
      oids.addElement(paramArrayOfSnmpOid[i]);
    }
  }
  
  public SnmpIndex(SnmpOid paramSnmpOid)
  {
    oids.addElement(paramSnmpOid);
    size = 1;
  }
  
  public int getNbComponents()
  {
    return size;
  }
  
  public Vector<SnmpOid> getComponents()
  {
    return oids;
  }
  
  public boolean equals(SnmpIndex paramSnmpIndex)
  {
    if (size != paramSnmpIndex.getNbComponents()) {
      return false;
    }
    Vector localVector = paramSnmpIndex.getComponents();
    for (int i = 0; i < size; i++)
    {
      SnmpOid localSnmpOid1 = (SnmpOid)oids.elementAt(i);
      SnmpOid localSnmpOid2 = (SnmpOid)localVector.elementAt(i);
      if (!localSnmpOid1.equals(localSnmpOid2)) {
        return false;
      }
    }
    return true;
  }
  
  public int compareTo(SnmpIndex paramSnmpIndex)
  {
    int i = paramSnmpIndex.getNbComponents();
    Vector localVector = paramSnmpIndex.getComponents();
    for (int k = 0; k < size; k++)
    {
      if (k > i) {
        return 1;
      }
      SnmpOid localSnmpOid1 = (SnmpOid)oids.elementAt(k);
      SnmpOid localSnmpOid2 = (SnmpOid)localVector.elementAt(k);
      int j = localSnmpOid1.compareTo(localSnmpOid2);
      if (j != 0) {
        return j;
      }
    }
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Enumeration localEnumeration = oids.elements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpOid localSnmpOid = (SnmpOid)localEnumeration.nextElement();
      localStringBuilder.append("//").append(localSnmpOid.toString());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */