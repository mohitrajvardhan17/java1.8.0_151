package java.security;

import java.io.Serializable;

public final class KeyPair
  implements Serializable
{
  private static final long serialVersionUID = -7565189502268009837L;
  private PrivateKey privateKey;
  private PublicKey publicKey;
  
  public KeyPair(PublicKey paramPublicKey, PrivateKey paramPrivateKey)
  {
    publicKey = paramPublicKey;
    privateKey = paramPrivateKey;
  }
  
  public PublicKey getPublic()
  {
    return publicKey;
  }
  
  public PrivateKey getPrivate()
  {
    return privateKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */