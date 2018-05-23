package sun.security.tools.policytool;

class SSLPerm
  extends Perm
{
  public SSLPerm()
  {
    super("SSLPermission", "javax.net.ssl.SSLPermission", new String[] { "setHostnameVerifier", "getSSLSessionContext" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\SSLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */