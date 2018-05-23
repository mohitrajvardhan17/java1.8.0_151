package sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class NTLMAuthSequence
{
  private String username;
  private String password;
  private String ntdomain;
  private int state;
  private long crdHandle;
  private long ctxHandle;
  Status status;
  
  NTLMAuthSequence(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    username = paramString1;
    password = paramString2;
    ntdomain = paramString3;
    status = new Status();
    state = 0;
    crdHandle = getCredentialsHandle(paramString1, paramString3, paramString2);
    if (crdHandle == 0L) {
      throw new IOException("could not get credentials handle");
    }
  }
  
  public String getAuthHeader(String paramString)
    throws IOException
  {
    byte[] arrayOfByte1 = null;
    assert (!status.sequenceComplete);
    if (paramString != null) {
      arrayOfByte1 = Base64.getDecoder().decode(paramString);
    }
    byte[] arrayOfByte2 = getNextToken(crdHandle, arrayOfByte1, status);
    if (arrayOfByte2 == null) {
      throw new IOException("Internal authentication error");
    }
    return Base64.getEncoder().encodeToString(arrayOfByte2);
  }
  
  public boolean isComplete()
  {
    return status.sequenceComplete;
  }
  
  private static native void initFirst(Class<Status> paramClass);
  
  private native long getCredentialsHandle(String paramString1, String paramString2, String paramString3);
  
  private native byte[] getNextToken(long paramLong, byte[] paramArrayOfByte, Status paramStatus);
  
  static
  {
    initFirst(Status.class);
  }
  
  class Status
  {
    boolean sequenceComplete;
    
    Status() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */