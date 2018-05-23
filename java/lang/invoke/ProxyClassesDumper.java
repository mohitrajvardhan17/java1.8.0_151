package java.lang.invoke;

import java.io.FilePermission;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.util.logging.PlatformLogger;

final class ProxyClassesDumper
{
  private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private static final char[] BAD_CHARS = { '\\', ':', '*', '?', '"', '<', '>', '|' };
  private static final String[] REPLACEMENT = { "%5C", "%3A", "%2A", "%3F", "%22", "%3C", "%3E", "%7C" };
  private final Path dumpDir;
  
  public static ProxyClassesDumper getInstance(String paramString)
  {
    if (null == paramString) {
      return null;
    }
    try
    {
      paramString = paramString.trim();
      Path localPath = Paths.get(paramString.length() == 0 ? "." : paramString, new String[0]);
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          ProxyClassesDumper.validateDumpDir(val$dir);
          return null;
        }
      }, null, new Permission[] { new FilePermission("<<ALL FILES>>", "read, write") });
      return new ProxyClassesDumper(localPath);
    }
    catch (InvalidPathException localInvalidPathException)
    {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Path " + paramString + " is not valid - dumping disabled", localInvalidPathException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning(localIllegalArgumentException.getMessage() + " - dumping disabled");
    }
    return null;
  }
  
  private ProxyClassesDumper(Path paramPath)
  {
    dumpDir = ((Path)Objects.requireNonNull(paramPath));
  }
  
  private static void validateDumpDir(Path paramPath)
  {
    if (!Files.exists(paramPath, new LinkOption[0])) {
      throw new IllegalArgumentException("Directory " + paramPath + " does not exist");
    }
    if (!Files.isDirectory(paramPath, new LinkOption[0])) {
      throw new IllegalArgumentException("Path " + paramPath + " is not a directory");
    }
    if (!Files.isWritable(paramPath)) {
      throw new IllegalArgumentException("Directory " + paramPath + " is not writable");
    }
  }
  
  public static String encodeForFilename(String paramString)
  {
    int i = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(i);
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      if (c <= '\037')
      {
        localStringBuilder.append('%');
        localStringBuilder.append(HEX[(c >> '\004' & 0xF)]);
        localStringBuilder.append(HEX[(c & 0xF)]);
      }
      else
      {
        for (int k = 0; k < BAD_CHARS.length; k++) {
          if (c == BAD_CHARS[k])
          {
            localStringBuilder.append(REPLACEMENT[k]);
            break;
          }
        }
        if (k >= BAD_CHARS.length) {
          localStringBuilder.append(c);
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  public void dumpClass(String paramString, byte[] paramArrayOfByte)
  {
    Path localPath1;
    try
    {
      localPath1 = dumpDir.resolve(encodeForFilename(paramString) + ".class");
    }
    catch (InvalidPathException localInvalidPathException)
    {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Invalid path for class " + paramString);
      return;
    }
    try
    {
      Path localPath2 = localPath1.getParent();
      Files.createDirectories(localPath2, new FileAttribute[0]);
      Files.write(localPath1, paramArrayOfByte, new OpenOption[0]);
    }
    catch (Exception localException)
    {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Exception writing to path at " + localPath1.toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\ProxyClassesDumper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */