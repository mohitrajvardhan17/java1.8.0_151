package javax.xml.crypto;

import java.security.Key;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public abstract class KeySelector
{
  protected KeySelector() {}
  
  public abstract KeySelectorResult select(KeyInfo paramKeyInfo, Purpose paramPurpose, AlgorithmMethod paramAlgorithmMethod, XMLCryptoContext paramXMLCryptoContext)
    throws KeySelectorException;
  
  public static KeySelector singletonKeySelector(Key paramKey)
  {
    return new SingletonKeySelector(paramKey);
  }
  
  public static class Purpose
  {
    private final String name;
    public static final Purpose SIGN = new Purpose("sign");
    public static final Purpose VERIFY = new Purpose("verify");
    public static final Purpose ENCRYPT = new Purpose("encrypt");
    public static final Purpose DECRYPT = new Purpose("decrypt");
    
    private Purpose(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return name;
    }
  }
  
  private static class SingletonKeySelector
    extends KeySelector
  {
    private final Key key;
    
    SingletonKeySelector(Key paramKey)
    {
      if (paramKey == null) {
        throw new NullPointerException();
      }
      key = paramKey;
    }
    
    public KeySelectorResult select(KeyInfo paramKeyInfo, KeySelector.Purpose paramPurpose, AlgorithmMethod paramAlgorithmMethod, XMLCryptoContext paramXMLCryptoContext)
      throws KeySelectorException
    {
      new KeySelectorResult()
      {
        public Key getKey()
        {
          return key;
        }
      };
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\KeySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */