package com.sun.jndi.ldap;

import javax.naming.NamingEnumeration;

abstract interface ReferralEnumeration<T>
  extends NamingEnumeration<T>
{
  public abstract void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\ReferralEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */