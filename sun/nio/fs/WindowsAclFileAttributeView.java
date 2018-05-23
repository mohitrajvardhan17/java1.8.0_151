package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

class WindowsAclFileAttributeView
  extends AbstractAclFileAttributeView
{
  private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
  private final WindowsPath file;
  private final boolean followLinks;
  
  WindowsAclFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean)
  {
    file = paramWindowsPath;
    followLinks = paramBoolean;
  }
  
  private void checkAccess(WindowsPath paramWindowsPath, boolean paramBoolean1, boolean paramBoolean2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (paramBoolean1) {
        localSecurityManager.checkRead(paramWindowsPath.getPathForPermissionCheck());
      }
      if (paramBoolean2) {
        localSecurityManager.checkWrite(paramWindowsPath.getPathForPermissionCheck());
      }
      localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
    }
  }
  
  static NativeBuffer getFileSecurity(String paramString, int paramInt)
    throws IOException
  {
    int i = 0;
    try
    {
      i = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, 0L, 0);
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(paramString);
    }
    assert (i > 0);
    NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
    try
    {
      for (;;)
      {
        int j = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, localNativeBuffer.address(), i);
        if (j <= i) {
          return localNativeBuffer;
        }
        localNativeBuffer.release();
        localNativeBuffer = NativeBuffers.getNativeBuffer(j);
        i = j;
      }
      return null;
    }
    catch (WindowsException localWindowsException2)
    {
      localNativeBuffer.release();
      localWindowsException2.rethrowAsIOException(paramString);
    }
  }
  
  public UserPrincipal getOwner()
    throws IOException
  {
    checkAccess(file, true, false);
    String str = WindowsLinkSupport.getFinalPath(file, followLinks);
    NativeBuffer localNativeBuffer = getFileSecurity(str, 1);
    try
    {
      long l = WindowsNativeDispatcher.GetSecurityDescriptorOwner(localNativeBuffer.address());
      if (l == 0L) {
        throw new IOException("no owner");
      }
      UserPrincipal localUserPrincipal2 = WindowsUserPrincipals.fromSid(l);
      return localUserPrincipal2;
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(file);
      UserPrincipal localUserPrincipal1 = null;
      return localUserPrincipal1;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  public List<AclEntry> getAcl()
    throws IOException
  {
    checkAccess(file, true, false);
    String str = WindowsLinkSupport.getFinalPath(file, followLinks);
    NativeBuffer localNativeBuffer = getFileSecurity(str, 4);
    try
    {
      List localList = WindowsSecurityDescriptor.getAcl(localNativeBuffer.address());
      return localList;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  public void setOwner(UserPrincipal paramUserPrincipal)
    throws IOException
  {
    if (paramUserPrincipal == null) {
      throw new NullPointerException("'owner' is null");
    }
    if (!(paramUserPrincipal instanceof WindowsUserPrincipals.User)) {
      throw new ProviderMismatchException();
    }
    WindowsUserPrincipals.User localUser = (WindowsUserPrincipals.User)paramUserPrincipal;
    checkAccess(file, false, true);
    String str = WindowsLinkSupport.getFinalPath(file, followLinks);
    long l = 0L;
    try
    {
      l = WindowsNativeDispatcher.ConvertStringSidToSid(localUser.sidString());
    }
    catch (WindowsException localWindowsException1)
    {
      throw new IOException("Failed to get SID for " + localUser.getName() + ": " + localWindowsException1.errorString());
    }
    try
    {
      NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(20);
      try
      {
        WindowsNativeDispatcher.InitializeSecurityDescriptor(localNativeBuffer.address());
        WindowsNativeDispatcher.SetSecurityDescriptorOwner(localNativeBuffer.address(), l);
        WindowsSecurity.Privilege localPrivilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
        try
        {
          WindowsNativeDispatcher.SetFileSecurity(str, 1, localNativeBuffer.address());
        }
        finally
        {
          localPrivilege.drop();
        }
      }
      catch (WindowsException localWindowsException2)
      {
        localWindowsException2.rethrowAsIOException(file);
      }
      finally
      {
        localNativeBuffer.release();
      }
    }
    finally
    {
      WindowsNativeDispatcher.LocalFree(l);
    }
  }
  
  public void setAcl(List<AclEntry> paramList)
    throws IOException
  {
    checkAccess(file, false, true);
    String str = WindowsLinkSupport.getFinalPath(file, followLinks);
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.create(paramList);
    try
    {
      WindowsNativeDispatcher.SetFileSecurity(str, 4, localWindowsSecurityDescriptor.address());
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(file);
    }
    finally
    {
      localWindowsSecurityDescriptor.release();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsAclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */