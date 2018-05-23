package sun.security.tools.policytool;

class InqSecContextPerm
  extends Perm
{
  public InqSecContextPerm()
  {
    super("InquireSecContextPermission", "com.sun.security.jgss.InquireSecContextPermission", new String[] { "KRB5_GET_SESSION_KEY", "KRB5_GET_TKT_FLAGS", "KRB5_GET_AUTHZ_DATA", "KRB5_GET_AUTHTIME" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\InqSecContextPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */