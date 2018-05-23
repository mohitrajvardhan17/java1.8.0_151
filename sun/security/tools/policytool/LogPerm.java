package sun.security.tools.policytool;

class LogPerm
  extends Perm
{
  public LogPerm()
  {
    super("LoggingPermission", "java.util.logging.LoggingPermission", new String[] { "control" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\LogPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */