package javax.naming;

import java.util.Hashtable;

public abstract class ReferralException
  extends NamingException
{
  private static final long serialVersionUID = -2881363844695698876L;
  
  protected ReferralException(String paramString)
  {
    super(paramString);
  }
  
  protected ReferralException() {}
  
  public abstract Object getReferralInfo();
  
  public abstract Context getReferralContext()
    throws NamingException;
  
  public abstract Context getReferralContext(Hashtable<?, ?> paramHashtable)
    throws NamingException;
  
  public abstract boolean skipReferral();
  
  public abstract void retryReferral();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ReferralException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */