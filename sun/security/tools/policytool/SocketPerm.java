package sun.security.tools.policytool;

class SocketPerm
  extends Perm
{
  public SocketPerm()
  {
    super("SocketPermission", "java.net.SocketPermission", new String[0], new String[] { "accept", "connect", "listen", "resolve" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\SocketPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */