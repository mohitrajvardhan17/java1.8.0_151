package sun.net.ftp.impl;

import sun.net.ftp.FtpClientProvider;

public class DefaultFtpClientProvider
  extends FtpClientProvider
{
  public DefaultFtpClientProvider() {}
  
  public sun.net.ftp.FtpClient createFtpClient()
  {
    return FtpClient.create();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\impl\DefaultFtpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */