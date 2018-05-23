package javax.management.remote;

import javax.security.auth.Subject;

public abstract interface JMXAuthenticator
{
  public abstract Subject authenticate(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */