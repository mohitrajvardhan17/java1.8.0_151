package java.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

final class ProcessImpl
  extends Process
{
  private static final JavaIOFileDescriptorAccess fdAccess = ;
  private static final int VERIFICATION_CMD_BAT = 0;
  private static final int VERIFICATION_WIN32 = 1;
  private static final int VERIFICATION_LEGACY = 2;
  private static final char[][] ESCAPE_VERIFICATION = { { ' ', '\t', '<', '>', '&', '|', '^' }, { ' ', '\t', '<', '>' }, { ' ', '\t' } };
  private long handle = 0L;
  private OutputStream stdin_stream;
  private InputStream stdout_stream;
  private InputStream stderr_stream;
  private static final int STILL_ACTIVE = getStillActive();
  
  private static FileOutputStream newFileOutputStream(File paramFile, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
    {
      String str = paramFile.getPath();
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkWrite(str);
      }
      long l = openForAtomicAppend(str);
      FileDescriptor localFileDescriptor = new FileDescriptor();
      fdAccess.setHandle(localFileDescriptor, l);
      (FileOutputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public FileOutputStream run()
        {
          return new FileOutputStream(val$fd);
        }
      });
    }
    return new FileOutputStream(paramFile);
  }
  
  static Process start(String[] paramArrayOfString, Map<String, String> paramMap, String paramString, ProcessBuilder.Redirect[] paramArrayOfRedirect, boolean paramBoolean)
    throws IOException
  {
    String str = ProcessEnvironment.toEnvironmentBlock(paramMap);
    FileInputStream localFileInputStream = null;
    FileOutputStream localFileOutputStream1 = null;
    FileOutputStream localFileOutputStream2 = null;
    try
    {
      long[] arrayOfLong;
      if (paramArrayOfRedirect == null)
      {
        arrayOfLong = new long[] { -1L, -1L, -1L };
      }
      else
      {
        arrayOfLong = new long[3];
        if (paramArrayOfRedirect[0] == ProcessBuilder.Redirect.PIPE)
        {
          arrayOfLong[0] = -1L;
        }
        else if (paramArrayOfRedirect[0] == ProcessBuilder.Redirect.INHERIT)
        {
          arrayOfLong[0] = fdAccess.getHandle(FileDescriptor.in);
        }
        else
        {
          localFileInputStream = new FileInputStream(paramArrayOfRedirect[0].file());
          arrayOfLong[0] = fdAccess.getHandle(localFileInputStream.getFD());
        }
        if (paramArrayOfRedirect[1] == ProcessBuilder.Redirect.PIPE)
        {
          arrayOfLong[1] = -1L;
        }
        else if (paramArrayOfRedirect[1] == ProcessBuilder.Redirect.INHERIT)
        {
          arrayOfLong[1] = fdAccess.getHandle(FileDescriptor.out);
        }
        else
        {
          localFileOutputStream1 = newFileOutputStream(paramArrayOfRedirect[1].file(), paramArrayOfRedirect[1].append());
          arrayOfLong[1] = fdAccess.getHandle(localFileOutputStream1.getFD());
        }
        if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.PIPE)
        {
          arrayOfLong[2] = -1L;
        }
        else if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.INHERIT)
        {
          arrayOfLong[2] = fdAccess.getHandle(FileDescriptor.err);
        }
        else
        {
          localFileOutputStream2 = newFileOutputStream(paramArrayOfRedirect[2].file(), paramArrayOfRedirect[2].append());
          arrayOfLong[2] = fdAccess.getHandle(localFileOutputStream2.getFD());
        }
      }
      ProcessImpl localProcessImpl = new ProcessImpl(paramArrayOfString, str, paramString, arrayOfLong, paramBoolean);
      return localProcessImpl;
    }
    finally
    {
      try
      {
        if (localFileInputStream != null) {
          localFileInputStream.close();
        }
      }
      finally
      {
        try
        {
          if (localFileOutputStream1 != null) {
            localFileOutputStream1.close();
          }
        }
        finally
        {
          if (localFileOutputStream2 != null) {
            localFileOutputStream2.close();
          }
        }
      }
    }
  }
  
  private static String[] getTokensFromCommand(String paramString)
  {
    ArrayList localArrayList = new ArrayList(8);
    Matcher localMatcher = LazyPattern.PATTERN.matcher(paramString);
    while (localMatcher.find()) {
      localArrayList.add(localMatcher.group());
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private static String createCommandLine(int paramInt, String paramString, String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder(80);
    localStringBuilder.append(paramString);
    for (int i = 1; i < paramArrayOfString.length; i++)
    {
      localStringBuilder.append(' ');
      String str = paramArrayOfString[i];
      if (needsEscaping(paramInt, str))
      {
        localStringBuilder.append('"').append(str);
        if ((paramInt != 0) && (str.endsWith("\\"))) {
          localStringBuilder.append('\\');
        }
        localStringBuilder.append('"');
      }
      else
      {
        localStringBuilder.append(str);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static boolean isQuoted(boolean paramBoolean, String paramString1, String paramString2)
  {
    int i = paramString1.length() - 1;
    if ((i >= 1) && (paramString1.charAt(0) == '"') && (paramString1.charAt(i) == '"'))
    {
      if ((paramBoolean) && (paramString1.indexOf('"', 1) != i)) {
        throw new IllegalArgumentException(paramString2);
      }
      return true;
    }
    if ((paramBoolean) && (paramString1.indexOf('"') >= 0)) {
      throw new IllegalArgumentException(paramString2);
    }
    return false;
  }
  
  private static boolean needsEscaping(int paramInt, String paramString)
  {
    boolean bool = isQuoted(paramInt == 0, paramString, "Argument has embedded quote, use the explicit CMD.EXE call.");
    if (!bool)
    {
      char[] arrayOfChar = ESCAPE_VERIFICATION[paramInt];
      for (int i = 0; i < arrayOfChar.length; i++) {
        if (paramString.indexOf(arrayOfChar[i]) >= 0) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static String getExecutablePath(String paramString)
    throws IOException
  {
    boolean bool = isQuoted(true, paramString, "Executable name has embedded quote, split the arguments");
    File localFile = new File(bool ? paramString.substring(1, paramString.length() - 1) : paramString);
    return localFile.getPath();
  }
  
  private boolean isShellFile(String paramString)
  {
    String str = paramString.toUpperCase();
    return (str.endsWith(".CMD")) || (str.endsWith(".BAT"));
  }
  
  private String quoteString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString.length() + 2);
    return '"' + paramString + '"';
  }
  
  private ProcessImpl(String[] paramArrayOfString, String paramString1, String paramString2, final long[] paramArrayOfLong, boolean paramBoolean)
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    int i = 0;
    String str2;
    if (localSecurityManager == null)
    {
      i = 1;
      str2 = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
      if (str2 != null) {
        i = !"false".equalsIgnoreCase(str2) ? 1 : 0;
      }
    }
    String str1;
    if (i != 0)
    {
      str2 = new File(paramArrayOfString[0]).getPath();
      if (needsEscaping(2, str2)) {
        str2 = quoteString(str2);
      }
      str1 = createCommandLine(2, str2, paramArrayOfString);
    }
    else
    {
      try
      {
        str2 = getExecutablePath(paramArrayOfString[0]);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        for (String str3 : paramArrayOfString) {
          localStringBuilder.append(str3).append(' ');
        }
        paramArrayOfString = getTokensFromCommand(localStringBuilder.toString());
        str2 = getExecutablePath(paramArrayOfString[0]);
        if (localSecurityManager != null) {
          localSecurityManager.checkExec(str2);
        }
      }
      str1 = createCommandLine(isShellFile(str2) ? 0 : 1, quoteString(str2), paramArrayOfString);
    }
    handle = create(str1, paramString1, paramString2, paramArrayOfLong, paramBoolean);
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        FileDescriptor localFileDescriptor;
        if (paramArrayOfLong[0] == -1L)
        {
          stdin_stream = ProcessBuilder.NullOutputStream.INSTANCE;
        }
        else
        {
          localFileDescriptor = new FileDescriptor();
          ProcessImpl.fdAccess.setHandle(localFileDescriptor, paramArrayOfLong[0]);
          stdin_stream = new BufferedOutputStream(new FileOutputStream(localFileDescriptor));
        }
        if (paramArrayOfLong[1] == -1L)
        {
          stdout_stream = ProcessBuilder.NullInputStream.INSTANCE;
        }
        else
        {
          localFileDescriptor = new FileDescriptor();
          ProcessImpl.fdAccess.setHandle(localFileDescriptor, paramArrayOfLong[1]);
          stdout_stream = new BufferedInputStream(new FileInputStream(localFileDescriptor));
        }
        if (paramArrayOfLong[2] == -1L)
        {
          stderr_stream = ProcessBuilder.NullInputStream.INSTANCE;
        }
        else
        {
          localFileDescriptor = new FileDescriptor();
          ProcessImpl.fdAccess.setHandle(localFileDescriptor, paramArrayOfLong[2]);
          stderr_stream = new FileInputStream(localFileDescriptor);
        }
        return null;
      }
    });
  }
  
  public OutputStream getOutputStream()
  {
    return stdin_stream;
  }
  
  public InputStream getInputStream()
  {
    return stdout_stream;
  }
  
  public InputStream getErrorStream()
  {
    return stderr_stream;
  }
  
  protected void finalize()
  {
    closeHandle(handle);
  }
  
  private static native int getStillActive();
  
  public int exitValue()
  {
    int i = getExitCodeProcess(handle);
    if (i == STILL_ACTIVE) {
      throw new IllegalThreadStateException("process has not exited");
    }
    return i;
  }
  
  private static native int getExitCodeProcess(long paramLong);
  
  public int waitFor()
    throws InterruptedException
  {
    waitForInterruptibly(handle);
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return exitValue();
  }
  
  private static native void waitForInterruptibly(long paramLong);
  
  public boolean waitFor(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (getExitCodeProcess(handle) != STILL_ACTIVE) {
      return true;
    }
    if (paramLong <= 0L) {
      return false;
    }
    long l1 = paramTimeUnit.toNanos(paramLong);
    long l2 = System.nanoTime() + l1;
    do
    {
      long l3 = TimeUnit.NANOSECONDS.toMillis(l1 + 999999L);
      waitForTimeoutInterruptibly(handle, l3);
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      if (getExitCodeProcess(handle) != STILL_ACTIVE) {
        return true;
      }
      l1 = l2 - System.nanoTime();
    } while (l1 > 0L);
    return getExitCodeProcess(handle) != STILL_ACTIVE;
  }
  
  private static native void waitForTimeoutInterruptibly(long paramLong1, long paramLong2);
  
  public void destroy()
  {
    terminateProcess(handle);
  }
  
  public Process destroyForcibly()
  {
    destroy();
    return this;
  }
  
  private static native void terminateProcess(long paramLong);
  
  public boolean isAlive()
  {
    return isProcessAlive(handle);
  }
  
  private static native boolean isProcessAlive(long paramLong);
  
  private static synchronized native long create(String paramString1, String paramString2, String paramString3, long[] paramArrayOfLong, boolean paramBoolean)
    throws IOException;
  
  private static native long openForAtomicAppend(String paramString)
    throws IOException;
  
  private static native boolean closeHandle(long paramLong);
  
  private static class LazyPattern
  {
    private static final Pattern PATTERN = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
    
    private LazyPattern() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ProcessImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */