package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;

public final class LdapResult
{
  int msgId;
  public int status;
  String matchedDN;
  String errorMessage;
  Vector<Vector<String>> referrals = null;
  LdapReferralException refEx = null;
  Vector<LdapEntry> entries = null;
  Vector<Control> resControls = null;
  public byte[] serverCreds = null;
  String extensionId = null;
  byte[] extensionValue = null;
  
  public LdapResult() {}
  
  boolean compareToSearchResult(String paramString)
  {
    boolean bool = false;
    switch (status)
    {
    case 6: 
      status = 0;
      entries = new Vector(1, 1);
      BasicAttributes localBasicAttributes = new BasicAttributes(true);
      LdapEntry localLdapEntry = new LdapEntry(paramString, localBasicAttributes);
      entries.addElement(localLdapEntry);
      bool = true;
      break;
    case 5: 
      status = 0;
      entries = new Vector(0);
      bool = true;
      break;
    default: 
      bool = false;
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */