package sun.net.ftp;

public class FtpProtocolException
  extends Exception
{
  private static final long serialVersionUID = 5978077070276545054L;
  private final FtpReplyCode code;
  
  public FtpProtocolException(String paramString)
  {
    super(paramString);
    code = FtpReplyCode.UNKNOWN_ERROR;
  }
  
  public FtpProtocolException(String paramString, FtpReplyCode paramFtpReplyCode)
  {
    super(paramString);
    code = paramFtpReplyCode;
  }
  
  public FtpReplyCode getReplyCode()
  {
    return code;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\FtpProtocolException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */