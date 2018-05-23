package com.sun.jndi.dns;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class DnsNameParser
  implements NameParser
{
  DnsNameParser() {}
  
  public Name parse(String paramString)
    throws NamingException
  {
    return new DnsName(paramString);
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof DnsNameParser;
  }
  
  public int hashCode()
  {
    return DnsNameParser.class.hashCode() + 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */