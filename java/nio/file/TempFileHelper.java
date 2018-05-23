package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttribute<*>;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class TempFileHelper
{
  private static final Path tmpdir = Paths.get((String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")), new String[0]);
  private static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
  private static final SecureRandom random = new SecureRandom();
  
  private TempFileHelper() {}
  
  private static Path generatePath(String paramString1, String paramString2, Path paramPath)
  {
    long l = random.nextLong();
    l = l == Long.MIN_VALUE ? 0L : Math.abs(l);
    Path localPath = paramPath.getFileSystem().getPath(paramString1 + Long.toString(l) + paramString2, new String[0]);
    if (localPath.getParent() != null) {
      throw new IllegalArgumentException("Invalid prefix or suffix");
    }
    return paramPath.resolve(localPath);
  }
  
  private static Path create(Path paramPath, String paramString1, String paramString2, boolean paramBoolean, FileAttribute<?>[] paramArrayOfFileAttribute)
    throws IOException
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    if (paramString2 == null) {
      paramString2 = paramBoolean ? "" : ".tmp";
    }
    if (paramPath == null) {
      paramPath = tmpdir;
    }
    Object localObject;
    if ((isPosix) && (paramPath.getFileSystem() == FileSystems.getDefault())) {
      if (paramArrayOfFileAttribute.length == 0)
      {
        paramArrayOfFileAttribute = new FileAttribute[1];
        paramArrayOfFileAttribute[0] = (paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions);
      }
      else
      {
        int i = 0;
        for (int j = 0; j < paramArrayOfFileAttribute.length; j++) {
          if (paramArrayOfFileAttribute[j].name().equals("posix:permissions"))
          {
            i = 1;
            break;
          }
        }
        if (i == 0)
        {
          localObject = new FileAttribute[paramArrayOfFileAttribute.length + 1];
          System.arraycopy(paramArrayOfFileAttribute, 0, localObject, 0, paramArrayOfFileAttribute.length);
          paramArrayOfFileAttribute = (FileAttribute<?>[])localObject;
          paramArrayOfFileAttribute[(paramArrayOfFileAttribute.length - 1)] = (paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions);
        }
      }
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    for (;;)
    {
      try
      {
        localObject = generatePath(paramString1, paramString2, paramPath);
      }
      catch (InvalidPathException localInvalidPathException)
      {
        if (localSecurityManager != null) {
          throw new IllegalArgumentException("Invalid prefix or suffix");
        }
        throw localInvalidPathException;
      }
      try
      {
        if (paramBoolean) {
          return Files.createDirectory((Path)localObject, paramArrayOfFileAttribute);
        }
        return Files.createFile((Path)localObject, paramArrayOfFileAttribute);
      }
      catch (SecurityException localSecurityException)
      {
        if ((paramPath == tmpdir) && (localSecurityManager != null)) {
          throw new SecurityException("Unable to create temporary file or directory");
        }
        throw localSecurityException;
      }
      catch (FileAlreadyExistsException localFileAlreadyExistsException) {}
    }
  }
  
  static Path createTempFile(Path paramPath, String paramString1, String paramString2, FileAttribute<?>[] paramArrayOfFileAttribute)
    throws IOException
  {
    return create(paramPath, paramString1, paramString2, false, paramArrayOfFileAttribute);
  }
  
  static Path createTempDirectory(Path paramPath, String paramString, FileAttribute<?>[] paramArrayOfFileAttribute)
    throws IOException
  {
    return create(paramPath, paramString, null, true, paramArrayOfFileAttribute);
  }
  
  private static class PosixPermissions
  {
    static final FileAttribute<Set<PosixFilePermission>> filePermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
    static final FileAttribute<Set<PosixFilePermission>> dirPermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
    
    private PosixPermissions() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\TempFileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */