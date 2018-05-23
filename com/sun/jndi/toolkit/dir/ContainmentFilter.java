package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class ContainmentFilter
  implements AttrFilter
{
  private Attributes matchingAttrs;
  
  public ContainmentFilter(Attributes paramAttributes)
  {
    matchingAttrs = paramAttributes;
  }
  
  public boolean check(Attributes paramAttributes)
    throws NamingException
  {
    return (matchingAttrs == null) || (matchingAttrs.size() == 0) || (contains(paramAttributes, matchingAttrs));
  }
  
  public static boolean contains(Attributes paramAttributes1, Attributes paramAttributes2)
    throws NamingException
  {
    if (paramAttributes2 == null) {
      return true;
    }
    NamingEnumeration localNamingEnumeration1 = paramAttributes2.getAll();
    while (localNamingEnumeration1.hasMore())
    {
      if (paramAttributes1 == null) {
        return false;
      }
      Attribute localAttribute1 = (Attribute)localNamingEnumeration1.next();
      Attribute localAttribute2 = paramAttributes1.get(localAttribute1.getID());
      if (localAttribute2 == null) {
        return false;
      }
      if (localAttribute1.size() > 0)
      {
        NamingEnumeration localNamingEnumeration2 = localAttribute1.getAll();
        while (localNamingEnumeration2.hasMore()) {
          if (!localAttribute2.contains(localNamingEnumeration2.next())) {
            return false;
          }
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\ContainmentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */