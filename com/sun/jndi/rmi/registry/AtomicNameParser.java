package com.sun.jndi.rmi.registry;

import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

class AtomicNameParser
  implements NameParser
{
  private static final Properties syntax = new Properties();
  
  AtomicNameParser() {}
  
  public Name parse(String paramString)
    throws NamingException
  {
    return new CompoundName(paramString, syntax);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\rmi\registry\AtomicNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */