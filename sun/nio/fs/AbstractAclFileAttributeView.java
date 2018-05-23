package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractAclFileAttributeView
  implements AclFileAttributeView, DynamicFileAttributeView
{
  private static final String OWNER_NAME = "owner";
  private static final String ACL_NAME = "acl";
  
  AbstractAclFileAttributeView() {}
  
  public final String name()
  {
    return "acl";
  }
  
  public final void setAttribute(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equals("owner"))
    {
      setOwner((UserPrincipal)paramObject);
      return;
    }
    if (paramString.equals("acl"))
    {
      setAcl((List)paramObject);
      return;
    }
    throw new IllegalArgumentException("'" + name() + ":" + paramString + "' not recognized");
  }
  
  public final Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException
  {
    int i = 0;
    int j = 0;
    for (String str : paramArrayOfString) {
      if (str.equals("*"))
      {
        j = 1;
        i = 1;
      }
      else if (str.equals("acl"))
      {
        i = 1;
      }
      else if (str.equals("owner"))
      {
        j = 1;
      }
      else
      {
        throw new IllegalArgumentException("'" + name() + ":" + str + "' not recognized");
      }
    }
    ??? = new HashMap(2);
    if (i != 0) {
      ((Map)???).put("acl", getAcl());
    }
    if (j != 0) {
      ((Map)???).put("owner", getOwner());
    }
    return Collections.unmodifiableMap((Map)???);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractAclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */