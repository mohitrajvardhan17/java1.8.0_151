package sun.security.jgss;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public class GSSExceptionImpl
  extends GSSException
{
  private static final long serialVersionUID = 4251197939069005575L;
  private String majorMessage;
  
  GSSExceptionImpl(int paramInt, Oid paramOid)
  {
    super(paramInt);
    majorMessage = (super.getMajorString() + ": " + paramOid);
  }
  
  public GSSExceptionImpl(int paramInt, String paramString)
  {
    super(paramInt);
    majorMessage = paramString;
  }
  
  public GSSExceptionImpl(int paramInt, Exception paramException)
  {
    super(paramInt);
    initCause(paramException);
  }
  
  public GSSExceptionImpl(int paramInt, String paramString, Exception paramException)
  {
    this(paramInt, paramString);
    initCause(paramException);
  }
  
  public String getMessage()
  {
    if (majorMessage != null) {
      return majorMessage;
    }
    return super.getMessage();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */