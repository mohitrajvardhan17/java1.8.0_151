package sun.nio.fs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Locale;

public abstract class AbstractFileTypeDetector
  extends FileTypeDetector
{
  private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
  
  protected AbstractFileTypeDetector() {}
  
  public final String probeContentType(Path paramPath)
    throws IOException
  {
    if (paramPath == null) {
      throw new NullPointerException("'file' is null");
    }
    String str = implProbeContentType(paramPath);
    return str == null ? null : parse(str);
  }
  
  protected abstract String implProbeContentType(Path paramPath)
    throws IOException;
  
  private static String parse(String paramString)
  {
    int i = paramString.indexOf('/');
    int j = paramString.indexOf(';');
    if (i < 0) {
      return null;
    }
    String str1 = paramString.substring(0, i).trim().toLowerCase(Locale.ENGLISH);
    if (!isValidToken(str1)) {
      return null;
    }
    String str2 = j < 0 ? paramString.substring(i + 1) : paramString.substring(i + 1, j);
    str2 = str2.trim().toLowerCase(Locale.ENGLISH);
    if (!isValidToken(str2)) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder(str1.length() + str2.length() + 1);
    localStringBuilder.append(str1);
    localStringBuilder.append('/');
    localStringBuilder.append(str2);
    return localStringBuilder.toString();
  }
  
  private static boolean isTokenChar(char paramChar)
  {
    return (paramChar > ' ') && (paramChar < '') && ("()<>@,;:/[]?=\\\"".indexOf(paramChar) < 0);
  }
  
  private static boolean isValidToken(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (!isTokenChar(paramString.charAt(j))) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractFileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */