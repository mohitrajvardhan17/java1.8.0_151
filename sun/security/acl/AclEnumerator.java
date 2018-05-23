package sun.security.acl;

import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

final class AclEnumerator
  implements Enumeration<AclEntry>
{
  Acl acl;
  Enumeration<AclEntry> u1;
  Enumeration<AclEntry> u2;
  Enumeration<AclEntry> g1;
  Enumeration<AclEntry> g2;
  
  AclEnumerator(Acl paramAcl, Hashtable<?, AclEntry> paramHashtable1, Hashtable<?, AclEntry> paramHashtable2, Hashtable<?, AclEntry> paramHashtable3, Hashtable<?, AclEntry> paramHashtable4)
  {
    acl = paramAcl;
    u1 = paramHashtable1.elements();
    u2 = paramHashtable3.elements();
    g1 = paramHashtable2.elements();
    g2 = paramHashtable4.elements();
  }
  
  public boolean hasMoreElements()
  {
    return (u1.hasMoreElements()) || (u2.hasMoreElements()) || (g1.hasMoreElements()) || (g2.hasMoreElements());
  }
  
  public AclEntry nextElement()
  {
    synchronized (acl)
    {
      if (u1.hasMoreElements()) {
        return (AclEntry)u1.nextElement();
      }
      if (u2.hasMoreElements()) {
        return (AclEntry)u2.nextElement();
      }
      if (g1.hasMoreElements()) {
        return (AclEntry)g1.nextElement();
      }
      if (g2.hasMoreElements()) {
        return (AclEntry)g2.nextElement();
      }
    }
    throw new NoSuchElementException("Acl Enumerator");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\AclEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */