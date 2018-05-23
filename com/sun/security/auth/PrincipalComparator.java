package com.sun.security.auth;

import javax.security.auth.Subject;
import jdk.Exported;

@Exported
public abstract interface PrincipalComparator
{
  public abstract boolean implies(Subject paramSubject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\PrincipalComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */