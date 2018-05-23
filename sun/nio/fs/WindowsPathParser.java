package sun.nio.fs;

import java.nio.file.InvalidPathException;

class WindowsPathParser
{
  private static final String reservedChars = "<>:\"|?*";
  
  private WindowsPathParser() {}
  
  static Result parse(String paramString)
  {
    return parse(paramString, true);
  }
  
  static Result parseNormalizedPath(String paramString)
  {
    return parse(paramString, false);
  }
  
  private static Result parse(String paramString, boolean paramBoolean)
  {
    String str1 = "";
    WindowsPathType localWindowsPathType = null;
    int i = paramString.length();
    int j = 0;
    if (i > 1)
    {
      char c1 = paramString.charAt(0);
      char c2 = paramString.charAt(1);
      int k = 0;
      int m = 2;
      if ((isSlash(c1)) && (isSlash(c2)))
      {
        localWindowsPathType = WindowsPathType.UNC;
        j = nextNonSlash(paramString, m, i);
        m = nextSlash(paramString, j, i);
        if (j == m) {
          throw new InvalidPathException(paramString, "UNC path is missing hostname");
        }
        String str2 = paramString.substring(j, m);
        j = nextNonSlash(paramString, m, i);
        m = nextSlash(paramString, j, i);
        if (j == m) {
          throw new InvalidPathException(paramString, "UNC path is missing sharename");
        }
        str1 = "\\\\" + str2 + "\\" + paramString.substring(j, m) + "\\";
        j = m;
      }
      else if ((isLetter(c1)) && (c2 == ':'))
      {
        int n;
        if ((i > 2) && (isSlash(n = paramString.charAt(2))))
        {
          if (n == 92) {
            str1 = paramString.substring(0, 3);
          } else {
            str1 = paramString.substring(0, 2) + '\\';
          }
          j = 3;
          localWindowsPathType = WindowsPathType.ABSOLUTE;
        }
        else
        {
          str1 = paramString.substring(0, 2);
          j = 2;
          localWindowsPathType = WindowsPathType.DRIVE_RELATIVE;
        }
      }
    }
    if (j == 0) {
      if ((i > 0) && (isSlash(paramString.charAt(0))))
      {
        localWindowsPathType = WindowsPathType.DIRECTORY_RELATIVE;
        str1 = "\\";
      }
      else
      {
        localWindowsPathType = WindowsPathType.RELATIVE;
      }
    }
    if (paramBoolean)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString.length());
      localStringBuilder.append(str1);
      return new Result(localWindowsPathType, str1, normalize(localStringBuilder, paramString, j));
    }
    return new Result(localWindowsPathType, str1, paramString);
  }
  
  private static String normalize(StringBuilder paramStringBuilder, String paramString, int paramInt)
  {
    int i = paramString.length();
    paramInt = nextNonSlash(paramString, paramInt, i);
    int j = paramInt;
    char c1 = '\000';
    while (paramInt < i)
    {
      char c2 = paramString.charAt(paramInt);
      if (isSlash(c2))
      {
        if (c1 == ' ') {
          throw new InvalidPathException(paramString, "Trailing char <" + c1 + ">", paramInt - 1);
        }
        paramStringBuilder.append(paramString, j, paramInt);
        paramInt = nextNonSlash(paramString, paramInt, i);
        if (paramInt != i) {
          paramStringBuilder.append('\\');
        }
        j = paramInt;
      }
      else
      {
        if (isInvalidPathChar(c2)) {
          throw new InvalidPathException(paramString, "Illegal char <" + c2 + ">", paramInt);
        }
        c1 = c2;
        paramInt++;
      }
    }
    if (j != paramInt)
    {
      if (c1 == ' ') {
        throw new InvalidPathException(paramString, "Trailing char <" + c1 + ">", paramInt - 1);
      }
      paramStringBuilder.append(paramString, j, paramInt);
    }
    return paramStringBuilder.toString();
  }
  
  private static final boolean isSlash(char paramChar)
  {
    return (paramChar == '\\') || (paramChar == '/');
  }
  
  private static final int nextNonSlash(String paramString, int paramInt1, int paramInt2)
  {
    while ((paramInt1 < paramInt2) && (isSlash(paramString.charAt(paramInt1)))) {
      paramInt1++;
    }
    return paramInt1;
  }
  
  private static final int nextSlash(String paramString, int paramInt1, int paramInt2)
  {
    char c;
    while ((paramInt1 < paramInt2) && (!isSlash(c = paramString.charAt(paramInt1))))
    {
      if (isInvalidPathChar(c)) {
        throw new InvalidPathException(paramString, "Illegal character [" + c + "] in path", paramInt1);
      }
      paramInt1++;
    }
    return paramInt1;
  }
  
  private static final boolean isLetter(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
  }
  
  private static final boolean isInvalidPathChar(char paramChar)
  {
    return (paramChar < ' ') || ("<>:\"|?*".indexOf(paramChar) != -1);
  }
  
  static class Result
  {
    private final WindowsPathType type;
    private final String root;
    private final String path;
    
    Result(WindowsPathType paramWindowsPathType, String paramString1, String paramString2)
    {
      type = paramWindowsPathType;
      root = paramString1;
      path = paramString2;
    }
    
    WindowsPathType type()
    {
      return type;
    }
    
    String root()
    {
      return root;
    }
    
    String path()
    {
      return path;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsPathParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */