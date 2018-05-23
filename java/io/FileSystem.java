package java.io;

abstract class FileSystem
{
  public static final int BA_EXISTS = 1;
  public static final int BA_REGULAR = 2;
  public static final int BA_DIRECTORY = 4;
  public static final int BA_HIDDEN = 8;
  public static final int ACCESS_READ = 4;
  public static final int ACCESS_WRITE = 2;
  public static final int ACCESS_EXECUTE = 1;
  public static final int SPACE_TOTAL = 0;
  public static final int SPACE_FREE = 1;
  public static final int SPACE_USABLE = 2;
  static boolean useCanonCaches = getBooleanProperty("sun.io.useCanonCaches", useCanonCaches);
  static boolean useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache", useCanonPrefixCache);
  
  FileSystem() {}
  
  public abstract char getSeparator();
  
  public abstract char getPathSeparator();
  
  public abstract String normalize(String paramString);
  
  public abstract int prefixLength(String paramString);
  
  public abstract String resolve(String paramString1, String paramString2);
  
  public abstract String getDefaultParent();
  
  public abstract String fromURIPath(String paramString);
  
  public abstract boolean isAbsolute(File paramFile);
  
  public abstract String resolve(File paramFile);
  
  public abstract String canonicalize(String paramString)
    throws IOException;
  
  public abstract int getBooleanAttributes(File paramFile);
  
  public abstract boolean checkAccess(File paramFile, int paramInt);
  
  public abstract boolean setPermission(File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract long getLastModifiedTime(File paramFile);
  
  public abstract long getLength(File paramFile);
  
  public abstract boolean createFileExclusively(String paramString)
    throws IOException;
  
  public abstract boolean delete(File paramFile);
  
  public abstract String[] list(File paramFile);
  
  public abstract boolean createDirectory(File paramFile);
  
  public abstract boolean rename(File paramFile1, File paramFile2);
  
  public abstract boolean setLastModifiedTime(File paramFile, long paramLong);
  
  public abstract boolean setReadOnly(File paramFile);
  
  public abstract File[] listRoots();
  
  public abstract long getSpace(File paramFile, int paramInt);
  
  public abstract int compare(File paramFile1, File paramFile2);
  
  public abstract int hashCode(File paramFile);
  
  private static boolean getBooleanProperty(String paramString, boolean paramBoolean)
  {
    String str = System.getProperty(paramString);
    if (str == null) {
      return paramBoolean;
    }
    return str.equalsIgnoreCase("true");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */