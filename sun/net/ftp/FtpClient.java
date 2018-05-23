package sun.net.ftp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class FtpClient
  implements Closeable
{
  private static final int FTP_PORT = 21;
  
  public static final int defaultPort()
  {
    return 21;
  }
  
  protected FtpClient() {}
  
  public static FtpClient create()
  {
    FtpClientProvider localFtpClientProvider = FtpClientProvider.provider();
    return localFtpClientProvider.createFtpClient();
  }
  
  public static FtpClient create(InetSocketAddress paramInetSocketAddress)
    throws FtpProtocolException, IOException
  {
    FtpClient localFtpClient = create();
    if (paramInetSocketAddress != null) {
      localFtpClient.connect(paramInetSocketAddress);
    }
    return localFtpClient;
  }
  
  public static FtpClient create(String paramString)
    throws FtpProtocolException, IOException
  {
    return create(new InetSocketAddress(paramString, 21));
  }
  
  public abstract FtpClient enablePassiveMode(boolean paramBoolean);
  
  public abstract boolean isPassiveModeEnabled();
  
  public abstract FtpClient setConnectTimeout(int paramInt);
  
  public abstract int getConnectTimeout();
  
  public abstract FtpClient setReadTimeout(int paramInt);
  
  public abstract int getReadTimeout();
  
  public abstract FtpClient setProxy(Proxy paramProxy);
  
  public abstract Proxy getProxy();
  
  public abstract boolean isConnected();
  
  public abstract FtpClient connect(SocketAddress paramSocketAddress)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient connect(SocketAddress paramSocketAddress, int paramInt)
    throws FtpProtocolException, IOException;
  
  public abstract SocketAddress getServerAddress();
  
  public abstract FtpClient login(String paramString, char[] paramArrayOfChar)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient login(String paramString1, char[] paramArrayOfChar, String paramString2)
    throws FtpProtocolException, IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract boolean isLoggedIn();
  
  public abstract FtpClient changeDirectory(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient changeToParentDirectory()
    throws FtpProtocolException, IOException;
  
  public abstract String getWorkingDirectory()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient setRestartOffset(long paramLong);
  
  public abstract FtpClient getFile(String paramString, OutputStream paramOutputStream)
    throws FtpProtocolException, IOException;
  
  public abstract InputStream getFileStream(String paramString)
    throws FtpProtocolException, IOException;
  
  public OutputStream putFileStream(String paramString)
    throws FtpProtocolException, IOException
  {
    return putFileStream(paramString, false);
  }
  
  public abstract OutputStream putFileStream(String paramString, boolean paramBoolean)
    throws FtpProtocolException, IOException;
  
  public FtpClient putFile(String paramString, InputStream paramInputStream)
    throws FtpProtocolException, IOException
  {
    return putFile(paramString, paramInputStream, false);
  }
  
  public abstract FtpClient putFile(String paramString, InputStream paramInputStream, boolean paramBoolean)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient appendFile(String paramString, InputStream paramInputStream)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient rename(String paramString1, String paramString2)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient deleteFile(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient makeDirectory(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient removeDirectory(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient noop()
    throws FtpProtocolException, IOException;
  
  public abstract String getStatus(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract List<String> getFeatures()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient abort()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient completePending()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient reInit()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient setType(TransferType paramTransferType)
    throws FtpProtocolException, IOException;
  
  public FtpClient setBinaryType()
    throws FtpProtocolException, IOException
  {
    setType(TransferType.BINARY);
    return this;
  }
  
  public FtpClient setAsciiType()
    throws FtpProtocolException, IOException
  {
    setType(TransferType.ASCII);
    return this;
  }
  
  public abstract InputStream list(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract InputStream nameList(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract long getSize(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract Date getLastModified(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient setDirParser(FtpDirParser paramFtpDirParser);
  
  public abstract Iterator<FtpDirEntry> listFiles(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient useKerberos()
    throws FtpProtocolException, IOException;
  
  public abstract String getWelcomeMsg();
  
  public abstract FtpReplyCode getLastReplyCode();
  
  public abstract String getLastResponseString();
  
  public abstract long getLastTransferSize();
  
  public abstract String getLastFileName();
  
  public abstract FtpClient startSecureSession()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient endSecureSession()
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient allocate(long paramLong)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient structureMount(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract String getSystem()
    throws FtpProtocolException, IOException;
  
  public abstract String getHelp(String paramString)
    throws FtpProtocolException, IOException;
  
  public abstract FtpClient siteCmd(String paramString)
    throws FtpProtocolException, IOException;
  
  public static enum TransferType
  {
    ASCII,  BINARY,  EBCDIC;
    
    private TransferType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\FtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */