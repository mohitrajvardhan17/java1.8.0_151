package java.security.cert;

public class LDAPCertStoreParameters
  implements CertStoreParameters
{
  private static final int LDAP_DEFAULT_PORT = 389;
  private int port;
  private String serverName;
  
  public LDAPCertStoreParameters(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    serverName = paramString;
    port = paramInt;
  }
  
  public LDAPCertStoreParameters(String paramString)
  {
    this(paramString, 389);
  }
  
  public LDAPCertStoreParameters()
  {
    this("localhost", 389);
  }
  
  public String getServerName()
  {
    return serverName;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("LDAPCertStoreParameters: [\n");
    localStringBuffer.append("  serverName: " + serverName + "\n");
    localStringBuffer.append("  port: " + port + "\n");
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\LDAPCertStoreParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */