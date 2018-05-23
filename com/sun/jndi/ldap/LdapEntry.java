package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

final class LdapEntry
{
  String DN;
  Attributes attributes;
  Vector<Control> respCtls = null;
  
  LdapEntry(String paramString, Attributes paramAttributes)
  {
    DN = paramString;
    attributes = paramAttributes;
  }
  
  LdapEntry(String paramString, Attributes paramAttributes, Vector<Control> paramVector)
  {
    DN = paramString;
    attributes = paramAttributes;
    respCtls = paramVector;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */