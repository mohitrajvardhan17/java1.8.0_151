package com.sun.jndi.toolkit.dir;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract interface AttrFilter
{
  public abstract boolean check(Attributes paramAttributes)
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\AttrFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */