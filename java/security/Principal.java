package java.security;

import java.util.Set;
import javax.security.auth.Subject;

public abstract interface Principal
{
  public abstract boolean equals(Object paramObject);
  
  public abstract String toString();
  
  public abstract int hashCode();
  
  public abstract String getName();
  
  public boolean implies(Subject paramSubject)
  {
    if (paramSubject == null) {
      return false;
    }
    return paramSubject.getPrincipals().contains(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Principal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */