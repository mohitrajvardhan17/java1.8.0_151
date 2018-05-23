package java.security;

@Deprecated
public abstract class Signer
  extends Identity
{
  private static final long serialVersionUID = -1763464102261361480L;
  private PrivateKey privateKey;
  
  protected Signer() {}
  
  public Signer(String paramString)
  {
    super(paramString);
  }
  
  public Signer(String paramString, IdentityScope paramIdentityScope)
    throws KeyManagementException
  {
    super(paramString, paramIdentityScope);
  }
  
  public PrivateKey getPrivateKey()
  {
    check("getSignerPrivateKey");
    return privateKey;
  }
  
  public final void setKeyPair(KeyPair paramKeyPair)
    throws InvalidParameterException, KeyException
  {
    check("setSignerKeyPair");
    final PublicKey localPublicKey = paramKeyPair.getPublic();
    PrivateKey localPrivateKey = paramKeyPair.getPrivate();
    if ((localPublicKey == null) || (localPrivateKey == null)) {
      throw new InvalidParameterException();
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws KeyManagementException
        {
          setPublicKey(localPublicKey);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((KeyManagementException)localPrivilegedActionException.getException());
    }
    privateKey = localPrivateKey;
  }
  
  String printKeys()
  {
    String str = "";
    PublicKey localPublicKey = getPublicKey();
    if ((localPublicKey != null) && (privateKey != null)) {
      str = "\tpublic and private keys initialized";
    } else {
      str = "\tno keys";
    }
    return str;
  }
  
  public String toString()
  {
    return "[Signer]" + super.toString();
  }
  
  private static void check(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSecurityAccess(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Signer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */