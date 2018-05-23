package sun.security.tools.policytool;

class URLPerm
  extends Perm
{
  public URLPerm()
  {
    super("URLPermission", "java.net.URLPermission", new String[] { "<" + PolicyTool.getMessage("url") + ">" }, new String[] { "<" + PolicyTool.getMessage("method.list") + ">:<" + PolicyTool.getMessage("request.headers.list") + ">" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\URLPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */