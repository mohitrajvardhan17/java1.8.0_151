package javax.swing.filechooser;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public final class FileNameExtensionFilter
  extends FileFilter
{
  private final String description;
  private final String[] extensions;
  private final String[] lowerCaseExtensions;
  
  public FileNameExtensionFilter(String paramString, String... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw new IllegalArgumentException("Extensions must be non-null and not empty");
    }
    description = paramString;
    extensions = new String[paramVarArgs.length];
    lowerCaseExtensions = new String[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      if ((paramVarArgs[i] == null) || (paramVarArgs[i].length() == 0)) {
        throw new IllegalArgumentException("Each extension must be non-null and not empty");
      }
      extensions[i] = paramVarArgs[i];
      lowerCaseExtensions[i] = paramVarArgs[i].toLowerCase(Locale.ENGLISH);
    }
  }
  
  public boolean accept(File paramFile)
  {
    if (paramFile != null)
    {
      if (paramFile.isDirectory()) {
        return true;
      }
      String str1 = paramFile.getName();
      int i = str1.lastIndexOf('.');
      if ((i > 0) && (i < str1.length() - 1))
      {
        String str2 = str1.substring(i + 1).toLowerCase(Locale.ENGLISH);
        for (String str3 : lowerCaseExtensions) {
          if (str2.equals(str3)) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String[] getExtensions()
  {
    String[] arrayOfString = new String[extensions.length];
    System.arraycopy(extensions, 0, arrayOfString, 0, extensions.length);
    return arrayOfString;
  }
  
  public String toString()
  {
    return super.toString() + "[description=" + getDescription() + " extensions=" + Arrays.asList(getExtensions()) + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\filechooser\FileNameExtensionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */