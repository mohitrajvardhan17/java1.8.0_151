package sun.security.tools.policytool;

class SerialPerm
  extends Perm
{
  public SerialPerm()
  {
    super("SerializablePermission", "java.io.SerializablePermission", new String[] { "enableSubclassImplementation", "enableSubstitution" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\SerialPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */