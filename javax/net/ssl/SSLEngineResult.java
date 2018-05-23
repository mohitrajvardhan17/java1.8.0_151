package javax.net.ssl;

public class SSLEngineResult
{
  private final Status status;
  private final HandshakeStatus handshakeStatus;
  private final int bytesConsumed;
  private final int bytesProduced;
  
  public SSLEngineResult(Status paramStatus, HandshakeStatus paramHandshakeStatus, int paramInt1, int paramInt2)
  {
    if ((paramStatus == null) || (paramHandshakeStatus == null) || (paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Invalid Parameter(s)");
    }
    status = paramStatus;
    handshakeStatus = paramHandshakeStatus;
    bytesConsumed = paramInt1;
    bytesProduced = paramInt2;
  }
  
  public final Status getStatus()
  {
    return status;
  }
  
  public final HandshakeStatus getHandshakeStatus()
  {
    return handshakeStatus;
  }
  
  public final int bytesConsumed()
  {
    return bytesConsumed;
  }
  
  public final int bytesProduced()
  {
    return bytesProduced;
  }
  
  public String toString()
  {
    return "Status = " + status + " HandshakeStatus = " + handshakeStatus + "\nbytesConsumed = " + bytesConsumed + " bytesProduced = " + bytesProduced;
  }
  
  public static enum HandshakeStatus
  {
    NOT_HANDSHAKING,  FINISHED,  NEED_TASK,  NEED_WRAP,  NEED_UNWRAP;
    
    private HandshakeStatus() {}
  }
  
  public static enum Status
  {
    BUFFER_UNDERFLOW,  BUFFER_OVERFLOW,  OK,  CLOSED;
    
    private Status() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLEngineResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */