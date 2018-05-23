package sun.security.krb5;

import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;

public class KrbException
  extends Exception
{
  private static final long serialVersionUID = -4993302876451928596L;
  private int returnCode;
  private KRBError error;
  
  public KrbException(String paramString)
  {
    super(paramString);
  }
  
  public KrbException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public KrbException(int paramInt)
  {
    returnCode = paramInt;
  }
  
  public KrbException(int paramInt, String paramString)
  {
    this(paramString);
    returnCode = paramInt;
  }
  
  public KrbException(KRBError paramKRBError)
  {
    returnCode = paramKRBError.getErrorCode();
    error = paramKRBError;
  }
  
  public KrbException(KRBError paramKRBError, String paramString)
  {
    this(paramString);
    returnCode = paramKRBError.getErrorCode();
    error = paramKRBError;
  }
  
  public KRBError getError()
  {
    return error;
  }
  
  public int returnCode()
  {
    return returnCode;
  }
  
  public String returnCodeSymbol()
  {
    return returnCodeSymbol(returnCode);
  }
  
  public static String returnCodeSymbol(int paramInt)
  {
    return "not yet implemented";
  }
  
  public String returnCodeMessage()
  {
    return Krb5.getErrorMessage(returnCode);
  }
  
  public static String errorMessage(int paramInt)
  {
    return Krb5.getErrorMessage(paramInt);
  }
  
  public String krbErrorMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer("krb_error " + returnCode);
    String str = getMessage();
    if (str != null)
    {
      localStringBuffer.append(" ");
      localStringBuffer.append(str);
    }
    return localStringBuffer.toString();
  }
  
  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = returnCode();
    if (i != 0)
    {
      localStringBuffer.append(returnCodeMessage());
      localStringBuffer.append(" (").append(returnCode()).append(')');
    }
    String str = super.getMessage();
    if ((str != null) && (str.length() != 0))
    {
      if (i != 0) {
        localStringBuffer.append(" - ");
      }
      localStringBuffer.append(str);
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    return "KrbException: " + getMessage();
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + returnCode;
    if (error != null) {
      i = 37 * i + error.hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KrbException)) {
      return false;
    }
    KrbException localKrbException = (KrbException)paramObject;
    if (returnCode != returnCode) {
      return false;
    }
    return error == null ? false : error == null ? true : error.equals(error);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */