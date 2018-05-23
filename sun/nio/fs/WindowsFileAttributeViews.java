package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Set;

class WindowsFileAttributeViews
{
  WindowsFileAttributeViews() {}
  
  static Basic createBasicView(WindowsPath paramWindowsPath, boolean paramBoolean)
  {
    return new Basic(paramWindowsPath, paramBoolean);
  }
  
  static Dos createDosView(WindowsPath paramWindowsPath, boolean paramBoolean)
  {
    return new Dos(paramWindowsPath, paramBoolean);
  }
  
  private static class Basic
    extends AbstractBasicFileAttributeView
  {
    final WindowsPath file;
    final boolean followLinks;
    
    Basic(WindowsPath paramWindowsPath, boolean paramBoolean)
    {
      file = paramWindowsPath;
      followLinks = paramBoolean;
    }
    
    public WindowsFileAttributes readAttributes()
      throws IOException
    {
      file.checkRead();
      try
      {
        return WindowsFileAttributes.get(file, followLinks);
      }
      catch (WindowsException localWindowsException)
      {
        localWindowsException.rethrowAsIOException(file);
      }
      return null;
    }
    
    private long adjustForFatEpoch(long paramLong)
    {
      if ((paramLong != -1L) && (paramLong < 119600064000000000L)) {
        return 119600064000000000L;
      }
      return paramLong;
    }
    
    void setFileTimes(long paramLong1, long paramLong2, long paramLong3)
      throws IOException
    {
      long l = -1L;
      try
      {
        int i = 33554432;
        if ((!followLinks) && (file.getFileSystem().supportsLinks())) {
          i |= 0x200000;
        }
        l = WindowsNativeDispatcher.CreateFile(file.getPathForWin32Calls(), 256, 7, 3, i);
      }
      catch (WindowsException localWindowsException1)
      {
        localWindowsException1.rethrowAsIOException(file);
      }
      try
      {
        WindowsNativeDispatcher.SetFileTime(l, paramLong1, paramLong2, paramLong3);
      }
      catch (WindowsException localWindowsException2)
      {
        Object localObject1;
        if ((followLinks) && (localWindowsException2.lastError() == 87)) {
          try
          {
            if (WindowsFileStore.create(file).type().equals("FAT"))
            {
              WindowsNativeDispatcher.SetFileTime(l, adjustForFatEpoch(paramLong1), adjustForFatEpoch(paramLong2), adjustForFatEpoch(paramLong3));
              localObject1 = null;
            }
          }
          catch (SecurityException localSecurityException) {}catch (WindowsException localWindowsException3) {}catch (IOException localIOException) {}
        }
        if (localObject1 != null) {
          ((WindowsException)localObject1).rethrowAsIOException(file);
        }
      }
      finally
      {
        WindowsNativeDispatcher.CloseHandle(l);
      }
    }
    
    public void setTimes(FileTime paramFileTime1, FileTime paramFileTime2, FileTime paramFileTime3)
      throws IOException
    {
      if ((paramFileTime1 == null) && (paramFileTime2 == null) && (paramFileTime3 == null)) {
        return;
      }
      file.checkWrite();
      long l1 = paramFileTime3 == null ? -1L : WindowsFileAttributes.toWindowsTime(paramFileTime3);
      long l2 = paramFileTime2 == null ? -1L : WindowsFileAttributes.toWindowsTime(paramFileTime2);
      long l3 = paramFileTime1 == null ? -1L : WindowsFileAttributes.toWindowsTime(paramFileTime1);
      setFileTimes(l1, l2, l3);
    }
  }
  
  static class Dos
    extends WindowsFileAttributeViews.Basic
    implements DosFileAttributeView
  {
    private static final String READONLY_NAME = "readonly";
    private static final String ARCHIVE_NAME = "archive";
    private static final String SYSTEM_NAME = "system";
    private static final String HIDDEN_NAME = "hidden";
    private static final String ATTRIBUTES_NAME = "attributes";
    static final Set<String> dosAttributeNames = Util.newSet(basicAttributeNames, new String[] { "readonly", "archive", "system", "hidden", "attributes" });
    
    Dos(WindowsPath paramWindowsPath, boolean paramBoolean)
    {
      super(paramBoolean);
    }
    
    public String name()
    {
      return "dos";
    }
    
    public void setAttribute(String paramString, Object paramObject)
      throws IOException
    {
      if (paramString.equals("readonly"))
      {
        setReadOnly(((Boolean)paramObject).booleanValue());
        return;
      }
      if (paramString.equals("archive"))
      {
        setArchive(((Boolean)paramObject).booleanValue());
        return;
      }
      if (paramString.equals("system"))
      {
        setSystem(((Boolean)paramObject).booleanValue());
        return;
      }
      if (paramString.equals("hidden"))
      {
        setHidden(((Boolean)paramObject).booleanValue());
        return;
      }
      super.setAttribute(paramString, paramObject);
    }
    
    public Map<String, Object> readAttributes(String[] paramArrayOfString)
      throws IOException
    {
      AbstractBasicFileAttributeView.AttributesBuilder localAttributesBuilder = AbstractBasicFileAttributeView.AttributesBuilder.create(dosAttributeNames, paramArrayOfString);
      WindowsFileAttributes localWindowsFileAttributes = readAttributes();
      addRequestedBasicAttributes(localWindowsFileAttributes, localAttributesBuilder);
      if (localAttributesBuilder.match("readonly")) {
        localAttributesBuilder.add("readonly", Boolean.valueOf(localWindowsFileAttributes.isReadOnly()));
      }
      if (localAttributesBuilder.match("archive")) {
        localAttributesBuilder.add("archive", Boolean.valueOf(localWindowsFileAttributes.isArchive()));
      }
      if (localAttributesBuilder.match("system")) {
        localAttributesBuilder.add("system", Boolean.valueOf(localWindowsFileAttributes.isSystem()));
      }
      if (localAttributesBuilder.match("hidden")) {
        localAttributesBuilder.add("hidden", Boolean.valueOf(localWindowsFileAttributes.isHidden()));
      }
      if (localAttributesBuilder.match("attributes")) {
        localAttributesBuilder.add("attributes", Integer.valueOf(localWindowsFileAttributes.attributes()));
      }
      return localAttributesBuilder.unmodifiableMap();
    }
    
    private void updateAttributes(int paramInt, boolean paramBoolean)
      throws IOException
    {
      file.checkWrite();
      String str = WindowsLinkSupport.getFinalPath(file, followLinks);
      try
      {
        int i = WindowsNativeDispatcher.GetFileAttributes(str);
        int j = i;
        if (paramBoolean) {
          j |= paramInt;
        } else {
          j &= (paramInt ^ 0xFFFFFFFF);
        }
        if (j != i) {
          WindowsNativeDispatcher.SetFileAttributes(str, j);
        }
      }
      catch (WindowsException localWindowsException)
      {
        localWindowsException.rethrowAsIOException(file);
      }
    }
    
    public void setReadOnly(boolean paramBoolean)
      throws IOException
    {
      updateAttributes(1, paramBoolean);
    }
    
    public void setHidden(boolean paramBoolean)
      throws IOException
    {
      updateAttributes(2, paramBoolean);
    }
    
    public void setArchive(boolean paramBoolean)
      throws IOException
    {
      updateAttributes(32, paramBoolean);
    }
    
    public void setSystem(boolean paramBoolean)
      throws IOException
    {
      updateAttributes(4, paramBoolean);
    }
    
    void setAttributes(WindowsFileAttributes paramWindowsFileAttributes)
      throws IOException
    {
      int i = 0;
      if (paramWindowsFileAttributes.isReadOnly()) {
        i |= 0x1;
      }
      if (paramWindowsFileAttributes.isHidden()) {
        i |= 0x2;
      }
      if (paramWindowsFileAttributes.isArchive()) {
        i |= 0x20;
      }
      if (paramWindowsFileAttributes.isSystem()) {
        i |= 0x4;
      }
      updateAttributes(i, true);
      setFileTimes(WindowsFileAttributes.toWindowsTime(paramWindowsFileAttributes.creationTime()), WindowsFileAttributes.toWindowsTime(paramWindowsFileAttributes.lastModifiedTime()), WindowsFileAttributes.toWindowsTime(paramWindowsFileAttributes.lastAccessTime()));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsFileAttributeViews.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */