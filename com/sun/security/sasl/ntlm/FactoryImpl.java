package com.sun.security.sasl.ntlm;

import com.sun.security.sasl.util.PolicyUtils;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public final class FactoryImpl
  implements SaslClientFactory, SaslServerFactory
{
  private static final String[] myMechs = { "NTLM" };
  private static final int[] mechPolicies = { 17 };
  
  public FactoryImpl() {}
  
  public SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i].equals("NTLM")) && (PolicyUtils.checkPolicy(mechPolicies[0], paramMap)))
      {
        if (paramCallbackHandler == null) {
          throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
        }
        return new NTLMClient(paramArrayOfString[i], paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
      }
    }
    return null;
  }
  
  public SaslServer createSaslServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    if ((paramString1.equals("NTLM")) && (PolicyUtils.checkPolicy(mechPolicies[0], paramMap)))
    {
      if (paramMap != null)
      {
        String str = (String)paramMap.get("javax.security.sasl.qop");
        if ((str != null) && (!str.equals("auth"))) {
          throw new SaslException("NTLM only support auth");
        }
      }
      if (paramCallbackHandler == null) {
        throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
      }
      return new NTLMServer(paramString1, paramString2, paramString3, paramMap, paramCallbackHandler);
    }
    return null;
  }
  
  public String[] getMechanismNames(Map<String, ?> paramMap)
  {
    return PolicyUtils.filterMechs(myMechs, mechPolicies, paramMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\ntlm\FactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */