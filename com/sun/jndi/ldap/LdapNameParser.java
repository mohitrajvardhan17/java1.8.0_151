package com.sun.jndi.ldap;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;

class LdapNameParser
  implements NameParser
{
  public LdapNameParser() {}
  
  public Name parse(String paramString)
    throws NamingException
  {
    return new LdapName(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */