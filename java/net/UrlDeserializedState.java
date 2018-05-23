package java.net;

final class UrlDeserializedState
{
  private final String protocol;
  private final String host;
  private final int port;
  private final String authority;
  private final String file;
  private final String ref;
  private final int hashCode;
  
  public UrlDeserializedState(String paramString1, String paramString2, int paramInt1, String paramString3, String paramString4, String paramString5, int paramInt2)
  {
    protocol = paramString1;
    host = paramString2;
    port = paramInt1;
    authority = paramString3;
    file = paramString4;
    ref = paramString5;
    hashCode = paramInt2;
  }
  
  String getProtocol()
  {
    return protocol;
  }
  
  String getHost()
  {
    return host;
  }
  
  String getAuthority()
  {
    return authority;
  }
  
  int getPort()
  {
    return port;
  }
  
  String getFile()
  {
    return file;
  }
  
  String getRef()
  {
    return ref;
  }
  
  int getHashCode()
  {
    return hashCode;
  }
  
  String reconstituteUrlString()
  {
    int i = protocol.length() + 1;
    if ((authority != null) && (authority.length() > 0)) {
      i += 2 + authority.length();
    }
    if (file != null) {
      i += file.length();
    }
    if (ref != null) {
      i += 1 + ref.length();
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    localStringBuilder.append(protocol);
    localStringBuilder.append(":");
    if ((authority != null) && (authority.length() > 0))
    {
      localStringBuilder.append("//");
      localStringBuilder.append(authority);
    }
    if (file != null) {
      localStringBuilder.append(file);
    }
    if (ref != null)
    {
      localStringBuilder.append("#");
      localStringBuilder.append(ref);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\UrlDeserializedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */