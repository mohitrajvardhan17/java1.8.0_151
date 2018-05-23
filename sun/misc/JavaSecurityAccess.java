package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public abstract interface JavaSecurityAccess
{
  public abstract <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2);
  
  public abstract <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaSecurityAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */