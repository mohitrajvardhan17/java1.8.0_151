package javax.net.ssl;

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SNIHostName
  extends SNIServerName
{
  private final String hostname;
  
  public SNIHostName(String paramString)
  {
    super(0, (paramString = IDN.toASCII((String)Objects.requireNonNull(paramString, "Server name value of host_name cannot be null"), 2)).getBytes(StandardCharsets.US_ASCII));
    hostname = paramString;
    checkHostName();
  }
  
  public SNIHostName(byte[] paramArrayOfByte)
  {
    super(0, paramArrayOfByte);
    try
    {
      CharsetDecoder localCharsetDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
      hostname = IDN.toASCII(localCharsetDecoder.decode(ByteBuffer.wrap(paramArrayOfByte)).toString());
    }
    catch (RuntimeException|CharacterCodingException localRuntimeException)
    {
      throw new IllegalArgumentException("The encoded server name value is invalid", localRuntimeException);
    }
    checkHostName();
  }
  
  public String getAsciiName()
  {
    return hostname;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SNIHostName)) {
      return hostname.equalsIgnoreCase(hostname);
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + hostname.toUpperCase(Locale.ENGLISH).hashCode();
    return i;
  }
  
  public String toString()
  {
    return "type=host_name (0), value=" + hostname;
  }
  
  public static SNIMatcher createSNIMatcher(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("The regular expression cannot be null");
    }
    return new SNIHostNameMatcher(paramString);
  }
  
  private void checkHostName()
  {
    if (hostname.isEmpty()) {
      throw new IllegalArgumentException("Server name value of host_name cannot be empty");
    }
    if (hostname.endsWith(".")) {
      throw new IllegalArgumentException("Server name value of host_name cannot have the trailing dot");
    }
  }
  
  private static final class SNIHostNameMatcher
    extends SNIMatcher
  {
    private final Pattern pattern;
    
    SNIHostNameMatcher(String paramString)
    {
      super();
      pattern = Pattern.compile(paramString, 2);
    }
    
    public boolean matches(SNIServerName paramSNIServerName)
    {
      if (paramSNIServerName == null) {
        throw new NullPointerException("The SNIServerName argument cannot be null");
      }
      SNIHostName localSNIHostName;
      if (!(paramSNIServerName instanceof SNIHostName))
      {
        if (paramSNIServerName.getType() != 0) {
          throw new IllegalArgumentException("The server name type is not host_name");
        }
        try
        {
          localSNIHostName = new SNIHostName(paramSNIServerName.getEncoded());
        }
        catch (NullPointerException|IllegalArgumentException localNullPointerException)
        {
          return false;
        }
      }
      else
      {
        localSNIHostName = (SNIHostName)paramSNIServerName;
      }
      String str = localSNIHostName.getAsciiName();
      if (pattern.matcher(str).matches()) {
        return true;
      }
      return pattern.matcher(IDN.toUnicode(str)).matches();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SNIHostName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */