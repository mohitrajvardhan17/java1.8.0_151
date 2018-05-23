package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.Map;

final class FileOwnerAttributeViewImpl
  implements FileOwnerAttributeView, DynamicFileAttributeView
{
  private static final String OWNER_NAME = "owner";
  private final FileAttributeView view;
  private final boolean isPosixView;
  
  FileOwnerAttributeViewImpl(PosixFileAttributeView paramPosixFileAttributeView)
  {
    view = paramPosixFileAttributeView;
    isPosixView = true;
  }
  
  FileOwnerAttributeViewImpl(AclFileAttributeView paramAclFileAttributeView)
  {
    view = paramAclFileAttributeView;
    isPosixView = false;
  }
  
  public String name()
  {
    return "owner";
  }
  
  public void setAttribute(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equals("owner")) {
      setOwner((UserPrincipal)paramObject);
    } else {
      throw new IllegalArgumentException("'" + name() + ":" + paramString + "' not recognized");
    }
  }
  
  public Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    for (String str : paramArrayOfString) {
      if ((str.equals("*")) || (str.equals("owner"))) {
        localHashMap.put("owner", getOwner());
      } else {
        throw new IllegalArgumentException("'" + name() + ":" + str + "' not recognized");
      }
    }
    return localHashMap;
  }
  
  public UserPrincipal getOwner()
    throws IOException
  {
    if (isPosixView) {
      return ((PosixFileAttributeView)view).readAttributes().owner();
    }
    return ((AclFileAttributeView)view).getOwner();
  }
  
  public void setOwner(UserPrincipal paramUserPrincipal)
    throws IOException
  {
    if (isPosixView) {
      ((PosixFileAttributeView)view).setOwner(paramUserPrincipal);
    } else {
      ((AclFileAttributeView)view).setOwner(paramUserPrincipal);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\FileOwnerAttributeViewImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */