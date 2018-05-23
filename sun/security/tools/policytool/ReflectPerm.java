package sun.security.tools.policytool;

class ReflectPerm
  extends Perm
{
  public ReflectPerm()
  {
    super("ReflectPermission", "java.lang.reflect.ReflectPermission", new String[] { "suppressAccessChecks" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ReflectPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */