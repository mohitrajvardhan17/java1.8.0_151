package sun.security.tools.policytool;

class SQLPerm
  extends Perm
{
  public SQLPerm()
  {
    super("SQLPermission", "java.sql.SQLPermission", new String[] { "setLog", "callAbort", "setSyncFactory", "setNetworkTimeout" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\SQLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */