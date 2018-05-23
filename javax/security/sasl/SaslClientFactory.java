package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

public abstract interface SaslClientFactory
{
  public abstract SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException;
  
  public abstract String[] getMechanismNames(Map<String, ?> paramMap);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\SaslClientFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */