package sun.security.timestamp;

import java.io.IOException;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.util.BitArray;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class TSResponse
{
  public static final int GRANTED = 0;
  public static final int GRANTED_WITH_MODS = 1;
  public static final int REJECTION = 2;
  public static final int WAITING = 3;
  public static final int REVOCATION_WARNING = 4;
  public static final int REVOCATION_NOTIFICATION = 5;
  public static final int BAD_ALG = 0;
  public static final int BAD_REQUEST = 2;
  public static final int BAD_DATA_FORMAT = 5;
  public static final int TIME_NOT_AVAILABLE = 14;
  public static final int UNACCEPTED_POLICY = 15;
  public static final int UNACCEPTED_EXTENSION = 16;
  public static final int ADD_INFO_NOT_AVAILABLE = 17;
  public static final int SYSTEM_FAILURE = 25;
  private static final Debug debug = Debug.getInstance("ts");
  private int status;
  private String[] statusString = null;
  private boolean[] failureInfo = null;
  private byte[] encodedTsToken = null;
  private PKCS7 tsToken = null;
  private TimestampToken tstInfo;
  
  TSResponse(byte[] paramArrayOfByte)
    throws IOException
  {
    parse(paramArrayOfByte);
  }
  
  public int getStatusCode()
  {
    return status;
  }
  
  public String[] getStatusMessages()
  {
    return statusString;
  }
  
  public boolean[] getFailureInfo()
  {
    return failureInfo;
  }
  
  public String getStatusCodeAsText()
  {
    switch (status)
    {
    case 0: 
      return "the timestamp request was granted.";
    case 1: 
      return "the timestamp request was granted with some modifications.";
    case 2: 
      return "the timestamp request was rejected.";
    case 3: 
      return "the timestamp request has not yet been processed.";
    case 4: 
      return "warning: a certificate revocation is imminent.";
    case 5: 
      return "notification: a certificate revocation has occurred.";
    }
    return "unknown status code " + status + ".";
  }
  
  private boolean isSet(int paramInt)
  {
    return failureInfo[paramInt];
  }
  
  public String getFailureCodeAsText()
  {
    if (failureInfo == null) {
      return "";
    }
    try
    {
      if (isSet(0)) {
        return "Unrecognized or unsupported algorithm identifier.";
      }
      if (isSet(2)) {
        return "The requested transaction is not permitted or supported.";
      }
      if (isSet(5)) {
        return "The data submitted has the wrong format.";
      }
      if (isSet(14)) {
        return "The TSA's time source is not available.";
      }
      if (isSet(15)) {
        return "The requested TSA policy is not supported by the TSA.";
      }
      if (isSet(16)) {
        return "The requested extension is not supported by the TSA.";
      }
      if (isSet(17)) {
        return "The additional information requested could not be understood or is not available.";
      }
      if (isSet(25)) {
        return "The request cannot be handled due to system failure.";
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return "unknown failure code";
  }
  
  public PKCS7 getToken()
  {
    return tsToken;
  }
  
  public TimestampToken getTimestampToken()
  {
    return tstInfo;
  }
  
  public byte[] getEncodedToken()
  {
    return encodedTsToken;
  }
  
  private void parse(byte[] paramArrayOfByte)
    throws IOException
  {
    DerValue localDerValue1 = new DerValue(paramArrayOfByte);
    if (tag != 48) {
      throw new IOException("Bad encoding for timestamp response");
    }
    DerValue localDerValue2 = data.getDerValue();
    status = data.getInteger();
    if (debug != null) {
      debug.println("timestamp response: status=" + status);
    }
    if (data.available() > 0)
    {
      int i = (byte)data.peekByte();
      if (i == 48)
      {
        DerValue[] arrayOfDerValue = data.getSequence(1);
        statusString = new String[arrayOfDerValue.length];
        for (int j = 0; j < arrayOfDerValue.length; j++)
        {
          statusString[j] = arrayOfDerValue[j].getUTF8String();
          if (debug != null) {
            debug.println("timestamp response: statusString=" + statusString[j]);
          }
        }
      }
    }
    if (data.available() > 0) {
      failureInfo = data.getUnalignedBitString().toBooleanArray();
    }
    if (data.available() > 0)
    {
      DerValue localDerValue3 = data.getDerValue();
      encodedTsToken = localDerValue3.toByteArray();
      tsToken = new PKCS7(encodedTsToken);
      tstInfo = new TimestampToken(tsToken.getContentInfo().getData());
    }
    if ((status == 0) || (status == 1))
    {
      if (tsToken == null) {
        throw new TimestampException("Bad encoding for timestamp response: expected a timeStampToken element to be present");
      }
    }
    else if (tsToken != null) {
      throw new TimestampException("Bad encoding for timestamp response: expected no timeStampToken element to be present");
    }
  }
  
  static final class TimestampException
    extends IOException
  {
    private static final long serialVersionUID = -1631631794891940953L;
    
    TimestampException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\timestamp\TSResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */