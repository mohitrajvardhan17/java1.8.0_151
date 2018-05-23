package sun.net.www.protocol.http;

import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.security.action.GetPropertyAction;

public class AuthenticationHeader
{
  MessageHeader rsp;
  HeaderParser preferred;
  String preferred_r;
  private final HttpCallerInfo hci;
  boolean dontUseNegotiate = false;
  static String authPref = null;
  String hdrname;
  HashMap<String, SchemeMapValue> schemes;
  
  public String toString()
  {
    return "AuthenticationHeader: prefer " + preferred_r;
  }
  
  public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean)
  {
    this(paramString, paramMessageHeader, paramHttpCallerInfo, paramBoolean, Collections.emptySet());
  }
  
  public AuthenticationHeader(String paramString, MessageHeader paramMessageHeader, HttpCallerInfo paramHttpCallerInfo, boolean paramBoolean, Set<String> paramSet)
  {
    hci = paramHttpCallerInfo;
    dontUseNegotiate = paramBoolean;
    rsp = paramMessageHeader;
    hdrname = paramString;
    schemes = new HashMap();
    parse(paramSet);
  }
  
  public HttpCallerInfo getHttpCallerInfo()
  {
    return hci;
  }
  
  private void parse(Set<String> paramSet)
  {
    Iterator localIterator1 = rsp.multiValueIterator(hdrname);
    Object localObject2;
    while (localIterator1.hasNext())
    {
      localObject1 = (String)localIterator1.next();
      localObject2 = new HeaderParser((String)localObject1);
      Iterator localIterator2 = ((HeaderParser)localObject2).keys();
      int i = 0;
      int j = -1;
      HeaderParser localHeaderParser;
      String str;
      while (localIterator2.hasNext())
      {
        localIterator2.next();
        if (((HeaderParser)localObject2).findValue(i) == null)
        {
          if (j != -1)
          {
            localHeaderParser = ((HeaderParser)localObject2).subsequence(j, i);
            str = localHeaderParser.findKey(0);
            if (!paramSet.contains(str)) {
              schemes.put(str, new SchemeMapValue(localHeaderParser, (String)localObject1));
            }
          }
          j = i;
        }
        i++;
      }
      if (i > j)
      {
        localHeaderParser = ((HeaderParser)localObject2).subsequence(j, i);
        str = localHeaderParser.findKey(0);
        if (!paramSet.contains(str)) {
          schemes.put(str, new SchemeMapValue(localHeaderParser, (String)localObject1));
        }
      }
    }
    Object localObject1 = null;
    if ((authPref == null) || ((localObject1 = (SchemeMapValue)schemes.get(authPref)) == null))
    {
      if ((localObject1 == null) && (!dontUseNegotiate))
      {
        localObject2 = (SchemeMapValue)schemes.get("negotiate");
        if (localObject2 != null)
        {
          if ((hci == null) || (!NegotiateAuthentication.isSupported(new HttpCallerInfo(hci, "Negotiate")))) {
            localObject2 = null;
          }
          localObject1 = localObject2;
        }
      }
      if ((localObject1 == null) && (!dontUseNegotiate))
      {
        localObject2 = (SchemeMapValue)schemes.get("kerberos");
        if (localObject2 != null)
        {
          if ((hci == null) || (!NegotiateAuthentication.isSupported(new HttpCallerInfo(hci, "Kerberos")))) {
            localObject2 = null;
          }
          localObject1 = localObject2;
        }
      }
      if ((localObject1 == null) && ((localObject1 = (SchemeMapValue)schemes.get("digest")) == null) && ((!NTLMAuthenticationProxy.supported) || ((localObject1 = (SchemeMapValue)schemes.get("ntlm")) == null))) {
        localObject1 = (SchemeMapValue)schemes.get("basic");
      }
    }
    else if ((dontUseNegotiate) && (authPref.equals("negotiate")))
    {
      localObject1 = null;
    }
    if (localObject1 != null)
    {
      preferred = parser;
      preferred_r = raw;
    }
  }
  
  public HeaderParser headerParser()
  {
    return preferred;
  }
  
  public String scheme()
  {
    if (preferred != null) {
      return preferred.findKey(0);
    }
    return null;
  }
  
  public String raw()
  {
    return preferred_r;
  }
  
  public boolean isPresent()
  {
    return preferred != null;
  }
  
  static
  {
    authPref = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.preference"));
    if (authPref != null)
    {
      authPref = authPref.toLowerCase();
      if ((authPref.equals("spnego")) || (authPref.equals("kerberos"))) {
        authPref = "negotiate";
      }
    }
  }
  
  static class SchemeMapValue
  {
    String raw;
    HeaderParser parser;
    
    SchemeMapValue(HeaderParser paramHeaderParser, String paramString)
    {
      raw = paramString;
      parser = paramHeaderParser;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\AuthenticationHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */