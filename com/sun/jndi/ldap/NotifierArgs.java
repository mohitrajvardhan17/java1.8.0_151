package com.sun.jndi.ldap;

import javax.naming.directory.SearchControls;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;

final class NotifierArgs
{
  static final int ADDED_MASK = 1;
  static final int REMOVED_MASK = 2;
  static final int CHANGED_MASK = 4;
  static final int RENAMED_MASK = 8;
  String name;
  String filter;
  SearchControls controls;
  int mask;
  private int sum = -1;
  
  NotifierArgs(String paramString, int paramInt, NamingListener paramNamingListener)
  {
    this(paramString, "(objectclass=*)", null, paramNamingListener);
    if (paramInt != 1)
    {
      controls = new SearchControls();
      controls.setSearchScope(paramInt);
    }
  }
  
  NotifierArgs(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener)
  {
    name = paramString1;
    filter = paramString2;
    controls = paramSearchControls;
    if ((paramNamingListener instanceof NamespaceChangeListener)) {
      mask |= 0xB;
    }
    if ((paramNamingListener instanceof ObjectChangeListener)) {
      mask |= 0x4;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof NotifierArgs))
    {
      NotifierArgs localNotifierArgs = (NotifierArgs)paramObject;
      return (mask == mask) && (name.equals(name)) && (filter.equals(filter)) && (checkControls(controls));
    }
    return false;
  }
  
  private boolean checkControls(SearchControls paramSearchControls)
  {
    if ((controls == null) || (paramSearchControls == null)) {
      return paramSearchControls == controls;
    }
    return (controls.getSearchScope() == paramSearchControls.getSearchScope()) && (controls.getTimeLimit() == paramSearchControls.getTimeLimit()) && (controls.getDerefLinkFlag() == paramSearchControls.getDerefLinkFlag()) && (controls.getReturningObjFlag() == paramSearchControls.getReturningObjFlag()) && (controls.getCountLimit() == paramSearchControls.getCountLimit()) && (checkStringArrays(controls.getReturningAttributes(), paramSearchControls.getReturningAttributes()));
  }
  
  private static boolean checkStringArrays(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if ((paramArrayOfString1 == null) || (paramArrayOfString2 == null)) {
      return paramArrayOfString1 == paramArrayOfString2;
    }
    if (paramArrayOfString1.length != paramArrayOfString2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfString1.length; i++) {
      if (!paramArrayOfString1[i].equals(paramArrayOfString2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    if (sum == -1) {
      sum = (mask + name.hashCode() + filter.hashCode() + controlsCode());
    }
    return sum;
  }
  
  private int controlsCode()
  {
    if (controls == null) {
      return 0;
    }
    int i = controls.getTimeLimit() + (int)controls.getCountLimit() + (controls.getDerefLinkFlag() ? 1 : 0) + (controls.getReturningObjFlag() ? 1 : 0);
    String[] arrayOfString = controls.getReturningAttributes();
    if (arrayOfString != null) {
      for (int j = 0; j < arrayOfString.length; j++) {
        i += arrayOfString[j].hashCode();
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\NotifierArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */