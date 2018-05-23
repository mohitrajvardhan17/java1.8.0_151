package org.ietf.jgss;

public class GSSException
  extends Exception
{
  private static final long serialVersionUID = -2706218945227726672L;
  public static final int BAD_BINDINGS = 1;
  public static final int BAD_MECH = 2;
  public static final int BAD_NAME = 3;
  public static final int BAD_NAMETYPE = 4;
  public static final int BAD_STATUS = 5;
  public static final int BAD_MIC = 6;
  public static final int CONTEXT_EXPIRED = 7;
  public static final int CREDENTIALS_EXPIRED = 8;
  public static final int DEFECTIVE_CREDENTIAL = 9;
  public static final int DEFECTIVE_TOKEN = 10;
  public static final int FAILURE = 11;
  public static final int NO_CONTEXT = 12;
  public static final int NO_CRED = 13;
  public static final int BAD_QOP = 14;
  public static final int UNAUTHORIZED = 15;
  public static final int UNAVAILABLE = 16;
  public static final int DUPLICATE_ELEMENT = 17;
  public static final int NAME_NOT_MN = 18;
  public static final int DUPLICATE_TOKEN = 19;
  public static final int OLD_TOKEN = 20;
  public static final int UNSEQ_TOKEN = 21;
  public static final int GAP_TOKEN = 22;
  private static String[] messages = { "Channel binding mismatch", "Unsupported mechanism requested", "Invalid name provided", "Name of unsupported type provided", "Invalid input status selector", "Token had invalid integrity check", "Specified security context expired", "Expired credentials detected", "Defective credential detected", "Defective token detected", "Failure unspecified at GSS-API level", "Security context init/accept not yet called or context deleted", "No valid credentials provided", "Unsupported QOP value", "Operation unauthorized", "Operation unavailable", "Duplicate credential element requested", "Name contains multi-mechanism elements", "The token was a duplicate of an earlier token", "The token's validity period has expired", "A later token has already been processed", "An expected per-message token was not received" };
  private int major;
  private int minor = 0;
  private String minorMessage = null;
  private String majorString = null;
  
  public GSSException(int paramInt)
  {
    if (validateMajor(paramInt)) {
      major = paramInt;
    } else {
      major = 11;
    }
  }
  
  GSSException(int paramInt, String paramString)
  {
    if (validateMajor(paramInt)) {
      major = paramInt;
    } else {
      major = 11;
    }
    majorString = paramString;
  }
  
  public GSSException(int paramInt1, int paramInt2, String paramString)
  {
    if (validateMajor(paramInt1)) {
      major = paramInt1;
    } else {
      major = 11;
    }
    minor = paramInt2;
    minorMessage = paramString;
  }
  
  public int getMajor()
  {
    return major;
  }
  
  public int getMinor()
  {
    return minor;
  }
  
  public String getMajorString()
  {
    if (majorString != null) {
      return majorString;
    }
    return messages[(major - 1)];
  }
  
  public String getMinorString()
  {
    return minorMessage;
  }
  
  public void setMinor(int paramInt, String paramString)
  {
    minor = paramInt;
    minorMessage = paramString;
  }
  
  public String toString()
  {
    return "GSSException: " + getMessage();
  }
  
  public String getMessage()
  {
    if (minor == 0) {
      return getMajorString();
    }
    return getMajorString() + " (Mechanism level: " + getMinorString() + ")";
  }
  
  private boolean validateMajor(int paramInt)
  {
    return (paramInt > 0) && (paramInt <= messages.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\ietf\jgss\GSSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */