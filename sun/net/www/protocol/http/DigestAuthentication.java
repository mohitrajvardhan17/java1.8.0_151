package sun.net.www.protocol.http;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import sun.net.NetProperties;
import sun.net.www.HeaderParser;

class DigestAuthentication
  extends AuthenticationInfo
{
  private static final long serialVersionUID = 100L;
  private String authMethod;
  private static final String compatPropName = "http.auth.digest.quoteParameters";
  private static final boolean delimCompatFlag;
  Parameters params;
  private static final char[] charArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  private static final String[] zeroPad = { "00000000", "0000000", "000000", "00000", "0000", "000", "00", "0" };
  
  public DigestAuthentication(boolean paramBoolean, URL paramURL, String paramString1, String paramString2, PasswordAuthentication paramPasswordAuthentication, Parameters paramParameters)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.DIGEST, paramURL, paramString1);
    authMethod = paramString2;
    pw = paramPasswordAuthentication;
    params = paramParameters;
  }
  
  public DigestAuthentication(boolean paramBoolean, String paramString1, int paramInt, String paramString2, String paramString3, PasswordAuthentication paramPasswordAuthentication, Parameters paramParameters)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.DIGEST, paramString1, paramInt, paramString2);
    authMethod = paramString3;
    pw = paramPasswordAuthentication;
    params = paramParameters;
  }
  
  public boolean supportsPreemptiveAuthorization()
  {
    return true;
  }
  
  public String getHeaderValue(URL paramURL, String paramString)
  {
    return getHeaderValueImpl(paramURL.getFile(), paramString);
  }
  
  String getHeaderValue(String paramString1, String paramString2)
  {
    return getHeaderValueImpl(paramString1, paramString2);
  }
  
  public boolean isAuthorizationStale(String paramString)
  {
    HeaderParser localHeaderParser = new HeaderParser(paramString);
    String str1 = localHeaderParser.findValue("stale");
    if ((str1 == null) || (!str1.equals("true"))) {
      return false;
    }
    String str2 = localHeaderParser.findValue("nonce");
    if ((str2 == null) || ("".equals(str2))) {
      return false;
    }
    params.setNonce(str2);
    return true;
  }
  
  public boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
  {
    params.setNonce(paramHeaderParser.findValue("nonce"));
    params.setOpaque(paramHeaderParser.findValue("opaque"));
    params.setQop(paramHeaderParser.findValue("qop"));
    String str1 = "";
    String str2;
    if ((type == 'p') && (paramHttpURLConnection.tunnelState() == HttpURLConnection.TunnelState.SETUP))
    {
      str1 = HttpURLConnection.connectRequestURI(paramHttpURLConnection.getURL());
      str2 = HttpURLConnection.HTTP_CONNECT;
    }
    else
    {
      try
      {
        str1 = paramHttpURLConnection.getRequestURI();
      }
      catch (IOException localIOException) {}
      str2 = paramHttpURLConnection.getMethod();
    }
    if ((params.nonce == null) || (authMethod == null) || (pw == null) || (realm == null)) {
      return false;
    }
    if (authMethod.length() >= 1) {
      authMethod = (Character.toUpperCase(authMethod.charAt(0)) + authMethod.substring(1).toLowerCase());
    }
    String str3 = paramHeaderParser.findValue("algorithm");
    if ((str3 == null) || ("".equals(str3))) {
      str3 = "MD5";
    }
    params.setAlgorithm(str3);
    if (params.authQop()) {
      params.setNewCnonce();
    }
    String str4 = getHeaderValueImpl(str1, str2);
    if (str4 != null)
    {
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str4);
      return true;
    }
    return false;
  }
  
  private String getHeaderValueImpl(String paramString1, String paramString2)
  {
    char[] arrayOfChar = pw.getPassword();
    boolean bool = params.authQop();
    String str2 = params.getOpaque();
    String str3 = params.getCnonce();
    String str4 = params.getNonce();
    String str5 = params.getAlgorithm();
    params.incrementNC();
    int i = params.getNCCount();
    String str6 = null;
    if (i != -1)
    {
      str6 = Integer.toHexString(i).toLowerCase();
      int j = str6.length();
      if (j < 8) {
        str6 = zeroPad[j] + str6;
      }
    }
    String str1;
    try
    {
      str1 = computeDigest(true, pw.getUserName(), arrayOfChar, realm, paramString2, paramString1, str4, str3, str6);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return null;
    }
    String str7 = "\"";
    if (bool) {
      str7 = "\", nc=" + str6;
    }
    String str8;
    String str9;
    if (delimCompatFlag)
    {
      str8 = ", algorithm=\"" + str5 + "\"";
      str9 = ", qop=\"auth\"";
    }
    else
    {
      str8 = ", algorithm=" + str5;
      str9 = ", qop=auth";
    }
    String str10 = authMethod + " username=\"" + pw.getUserName() + "\", realm=\"" + realm + "\", nonce=\"" + str4 + str7 + ", uri=\"" + paramString1 + "\", response=\"" + str1 + "\"" + str8;
    if (str2 != null) {
      str10 = str10 + ", opaque=\"" + str2 + "\"";
    }
    if (str3 != null) {
      str10 = str10 + ", cnonce=\"" + str3 + "\"";
    }
    if (bool) {
      str10 = str10 + str9;
    }
    return str10;
  }
  
  public void checkResponse(String paramString1, String paramString2, URL paramURL)
    throws IOException
  {
    checkResponse(paramString1, paramString2, paramURL.getFile());
  }
  
  public void checkResponse(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    char[] arrayOfChar = pw.getPassword();
    String str1 = pw.getUserName();
    boolean bool = params.authQop();
    String str2 = params.getOpaque();
    String str3 = params.cnonce;
    String str4 = params.getNonce();
    String str5 = params.getAlgorithm();
    int i = params.getNCCount();
    String str6 = null;
    if (paramString1 == null) {
      throw new ProtocolException("No authentication information in response");
    }
    if (i != -1)
    {
      str6 = Integer.toHexString(i).toUpperCase();
      int j = str6.length();
      if (j < 8) {
        str6 = zeroPad[j] + str6;
      }
    }
    try
    {
      String str7 = computeDigest(false, str1, arrayOfChar, realm, paramString2, paramString3, str4, str3, str6);
      HeaderParser localHeaderParser = new HeaderParser(paramString1);
      String str8 = localHeaderParser.findValue("rspauth");
      if (str8 == null) {
        throw new ProtocolException("No digest in response");
      }
      if (!str8.equals(str7)) {
        throw new ProtocolException("Response digest invalid");
      }
      String str9 = localHeaderParser.findValue("nextnonce");
      if ((str9 != null) && (!"".equals(str9))) {
        params.setNonce(str9);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new ProtocolException("Unsupported algorithm in response");
    }
  }
  
  private String computeDigest(boolean paramBoolean, String paramString1, char[] paramArrayOfChar, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
    throws NoSuchAlgorithmException
  {
    String str3 = params.getAlgorithm();
    boolean bool = str3.equalsIgnoreCase("MD5-sess");
    MessageDigest localMessageDigest = MessageDigest.getInstance(bool ? "MD5" : str3);
    String str2;
    String str4;
    String str1;
    if (bool)
    {
      if ((str2 = params.getCachedHA1()) == null)
      {
        str4 = paramString1 + ":" + paramString2 + ":";
        str5 = encode(str4, paramArrayOfChar, localMessageDigest);
        str1 = str5 + ":" + paramString5 + ":" + paramString6;
        str2 = encode(str1, null, localMessageDigest);
        params.setCachedHA1(str2);
      }
    }
    else
    {
      str1 = paramString1 + ":" + paramString2 + ":";
      str2 = encode(str1, paramArrayOfChar, localMessageDigest);
    }
    if (paramBoolean) {
      str4 = paramString3 + ":" + paramString4;
    } else {
      str4 = ":" + paramString4;
    }
    String str5 = encode(str4, null, localMessageDigest);
    String str6;
    if (params.authQop()) {
      str6 = str2 + ":" + paramString5 + ":" + paramString7 + ":" + paramString6 + ":auth:" + str5;
    } else {
      str6 = str2 + ":" + paramString5 + ":" + str5;
    }
    String str7 = encode(str6, null, localMessageDigest);
    return str7;
  }
  
  private String encode(String paramString, char[] paramArrayOfChar, MessageDigest paramMessageDigest)
  {
    try
    {
      paramMessageDigest.update(paramString.getBytes("ISO-8859-1"));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    if (paramArrayOfChar != null)
    {
      arrayOfByte = new byte[paramArrayOfChar.length];
      for (int i = 0; i < paramArrayOfChar.length; i++) {
        arrayOfByte[i] = ((byte)paramArrayOfChar[i]);
      }
      paramMessageDigest.update(arrayOfByte);
      Arrays.fill(arrayOfByte, (byte)0);
    }
    byte[] arrayOfByte = paramMessageDigest.digest();
    StringBuffer localStringBuffer = new StringBuffer(arrayOfByte.length * 2);
    for (int j = 0; j < arrayOfByte.length; j++)
    {
      int k = arrayOfByte[j] >>> 4 & 0xF;
      localStringBuffer.append(charArray[k]);
      k = arrayOfByte[j] & 0xF;
      localStringBuffer.append(charArray[k]);
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return NetProperties.getBoolean("http.auth.digest.quoteParameters");
      }
    });
    delimCompatFlag = localBoolean == null ? false : localBoolean.booleanValue();
  }
  
  static class Parameters
    implements Serializable
  {
    private static final long serialVersionUID = -3584543755194526252L;
    private boolean serverQop = false;
    private String opaque = null;
    private String cnonce;
    private String nonce = null;
    private String algorithm = null;
    private int NCcount = 0;
    private String cachedHA1 = null;
    private boolean redoCachedHA1 = true;
    private static final int cnonceRepeat = 5;
    private static final int cnoncelen = 40;
    private static Random random = new Random();
    int cnonce_count = 0;
    
    Parameters()
    {
      setNewCnonce();
    }
    
    boolean authQop()
    {
      return serverQop;
    }
    
    synchronized void incrementNC()
    {
      NCcount += 1;
    }
    
    synchronized int getNCCount()
    {
      return NCcount;
    }
    
    synchronized String getCnonce()
    {
      if (cnonce_count >= 5) {
        setNewCnonce();
      }
      cnonce_count += 1;
      return cnonce;
    }
    
    synchronized void setNewCnonce()
    {
      byte[] arrayOfByte = new byte[20];
      char[] arrayOfChar = new char[40];
      random.nextBytes(arrayOfByte);
      for (int i = 0; i < 20; i++)
      {
        int j = arrayOfByte[i] + 128;
        arrayOfChar[(i * 2)] = ((char)(65 + j / 16));
        arrayOfChar[(i * 2 + 1)] = ((char)(65 + j % 16));
      }
      cnonce = new String(arrayOfChar, 0, 40);
      cnonce_count = 0;
      redoCachedHA1 = true;
    }
    
    synchronized void setQop(String paramString)
    {
      if (paramString != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
        while (localStringTokenizer.hasMoreTokens()) {
          if (localStringTokenizer.nextToken().equalsIgnoreCase("auth"))
          {
            serverQop = true;
            return;
          }
        }
      }
      serverQop = false;
    }
    
    synchronized String getOpaque()
    {
      return opaque;
    }
    
    synchronized void setOpaque(String paramString)
    {
      opaque = paramString;
    }
    
    synchronized String getNonce()
    {
      return nonce;
    }
    
    synchronized void setNonce(String paramString)
    {
      if (!paramString.equals(nonce))
      {
        nonce = paramString;
        NCcount = 0;
        redoCachedHA1 = true;
      }
    }
    
    synchronized String getCachedHA1()
    {
      if (redoCachedHA1) {
        return null;
      }
      return cachedHA1;
    }
    
    synchronized void setCachedHA1(String paramString)
    {
      cachedHA1 = paramString;
      redoCachedHA1 = false;
    }
    
    synchronized String getAlgorithm()
    {
      return algorithm;
    }
    
    synchronized void setAlgorithm(String paramString)
    {
      algorithm = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\DigestAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */