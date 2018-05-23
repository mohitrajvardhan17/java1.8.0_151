package sun.nio.fs;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;

class WindowsException
  extends Exception
{
  static final long serialVersionUID = 2765039493083748820L;
  private int lastError;
  private String msg;
  
  WindowsException(int paramInt)
  {
    lastError = paramInt;
    msg = null;
  }
  
  WindowsException(String paramString)
  {
    lastError = 0;
    msg = paramString;
  }
  
  int lastError()
  {
    return lastError;
  }
  
  String errorString()
  {
    if (msg == null)
    {
      msg = WindowsNativeDispatcher.FormatMessage(lastError);
      if (msg == null) {
        msg = ("Unknown error: 0x" + Integer.toHexString(lastError));
      }
    }
    return msg;
  }
  
  public String getMessage()
  {
    return errorString();
  }
  
  private IOException translateToIOException(String paramString1, String paramString2)
  {
    if (lastError() == 0) {
      return new IOException(errorString());
    }
    if ((lastError() == 2) || (lastError() == 3)) {
      return new NoSuchFileException(paramString1, paramString2, null);
    }
    if ((lastError() == 80) || (lastError() == 183)) {
      return new FileAlreadyExistsException(paramString1, paramString2, null);
    }
    if (lastError() == 5) {
      return new AccessDeniedException(paramString1, paramString2, null);
    }
    return new FileSystemException(paramString1, paramString2, errorString());
  }
  
  void rethrowAsIOException(String paramString)
    throws IOException
  {
    IOException localIOException = translateToIOException(paramString, null);
    throw localIOException;
  }
  
  void rethrowAsIOException(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2)
    throws IOException
  {
    String str1 = paramWindowsPath1 == null ? null : paramWindowsPath1.getPathForExceptionMessage();
    String str2 = paramWindowsPath2 == null ? null : paramWindowsPath2.getPathForExceptionMessage();
    IOException localIOException = translateToIOException(str1, str2);
    throw localIOException;
  }
  
  void rethrowAsIOException(WindowsPath paramWindowsPath)
    throws IOException
  {
    rethrowAsIOException(paramWindowsPath, null);
  }
  
  IOException asIOException(WindowsPath paramWindowsPath)
  {
    return translateToIOException(paramWindowsPath.getPathForExceptionMessage(), null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */