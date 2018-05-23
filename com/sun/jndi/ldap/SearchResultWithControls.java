package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class SearchResultWithControls
  extends SearchResult
  implements HasControls
{
  private Control[] controls;
  private static final long serialVersionUID = 8476983938747908202L;
  
  public SearchResultWithControls(String paramString, Object paramObject, Attributes paramAttributes, boolean paramBoolean, Control[] paramArrayOfControl)
  {
    super(paramString, paramObject, paramAttributes, paramBoolean);
    controls = paramArrayOfControl;
  }
  
  public Control[] getControls()
    throws NamingException
  {
    return controls;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\SearchResultWithControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */