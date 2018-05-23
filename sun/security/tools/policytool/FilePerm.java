package sun.security.tools.policytool;

class FilePerm
  extends Perm
{
  public FilePerm()
  {
    super("FilePermission", "java.io.FilePermission", new String[] { "<<ALL FILES>>" }, new String[] { "read", "write", "delete", "execute" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\FilePerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */