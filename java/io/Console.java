package java.io;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import sun.misc.JavaIOAccess;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Console
  implements Flushable
{
  private Object readLock = new Object();
  private Object writeLock = new Object();
  private Reader reader;
  private Writer out;
  private PrintWriter pw;
  private Formatter formatter;
  private Charset cs;
  private char[] rcb;
  private static boolean echoOff;
  private static Console cons;
  
  public PrintWriter writer()
  {
    return pw;
  }
  
  public Reader reader()
  {
    return reader;
  }
  
  public Console format(String paramString, Object... paramVarArgs)
  {
    formatter.format(paramString, paramVarArgs).flush();
    return this;
  }
  
  public Console printf(String paramString, Object... paramVarArgs)
  {
    return format(paramString, paramVarArgs);
  }
  
  public String readLine(String paramString, Object... paramVarArgs)
  {
    String str = null;
    synchronized (writeLock)
    {
      synchronized (readLock)
      {
        if (paramString.length() != 0) {
          pw.format(paramString, paramVarArgs);
        }
        try
        {
          char[] arrayOfChar = readline(false);
          if (arrayOfChar != null) {
            str = new String(arrayOfChar);
          }
        }
        catch (IOException localIOException)
        {
          throw new IOError(localIOException);
        }
      }
    }
    return str;
  }
  
  public String readLine()
  {
    return readLine("", new Object[0]);
  }
  
  public char[] readPassword(String paramString, Object... paramVarArgs)
  {
    char[] arrayOfChar = null;
    synchronized (writeLock)
    {
      synchronized (readLock)
      {
        try
        {
          echoOff = echo(false);
        }
        catch (IOException localIOException1)
        {
          throw new IOError(localIOException1);
        }
        IOError localIOError = null;
        try
        {
          if (paramString.length() != 0) {
            pw.format(paramString, paramVarArgs);
          }
          arrayOfChar = readline(true);
        }
        catch (IOException localIOException3)
        {
          localIOError = new IOError(localIOException3);
        }
        finally
        {
          try
          {
            echoOff = echo(true);
          }
          catch (IOException localIOException5)
          {
            if (localIOError == null) {
              localIOError = new IOError(localIOException5);
            } else {
              localIOError.addSuppressed(localIOException5);
            }
          }
          if (localIOError != null) {
            throw localIOError;
          }
        }
        pw.println();
      }
    }
    return arrayOfChar;
  }
  
  public char[] readPassword()
  {
    return readPassword("", new Object[0]);
  }
  
  public void flush()
  {
    pw.flush();
  }
  
  private static native String encoding();
  
  private static native boolean echo(boolean paramBoolean)
    throws IOException;
  
  private char[] readline(boolean paramBoolean)
    throws IOException
  {
    int i = reader.read(rcb, 0, rcb.length);
    if (i < 0) {
      return null;
    }
    if (rcb[(i - 1)] == '\r')
    {
      i--;
    }
    else if (rcb[(i - 1)] == '\n')
    {
      i--;
      if ((i > 0) && (rcb[(i - 1)] == '\r')) {
        i--;
      }
    }
    char[] arrayOfChar = new char[i];
    if (i > 0)
    {
      System.arraycopy(rcb, 0, arrayOfChar, 0, i);
      if (paramBoolean) {
        Arrays.fill(rcb, 0, i, ' ');
      }
    }
    return arrayOfChar;
  }
  
  private char[] grow()
  {
    assert (Thread.holdsLock(readLock));
    char[] arrayOfChar = new char[rcb.length * 2];
    System.arraycopy(rcb, 0, arrayOfChar, 0, rcb.length);
    rcb = arrayOfChar;
    return rcb;
  }
  
  private static native boolean istty();
  
  private Console()
  {
    String str = encoding();
    if (str != null) {
      try
      {
        cs = Charset.forName(str);
      }
      catch (Exception localException) {}
    }
    if (cs == null) {
      cs = Charset.defaultCharset();
    }
    out = StreamEncoder.forOutputStreamWriter(new FileOutputStream(FileDescriptor.out), writeLock, cs);
    pw = new PrintWriter(out, true)
    {
      public void close() {}
    };
    formatter = new Formatter(out);
    reader = new LineReader(StreamDecoder.forInputStreamReader(new FileInputStream(FileDescriptor.in), readLock, cs));
    rcb = new char['Ѐ'];
  }
  
  static
  {
    try
    {
      SharedSecrets.getJavaLangAccess().registerShutdownHook(0, false, new Runnable()
      {
        public void run()
        {
          try
          {
            if (Console.echoOff) {
              Console.echo(true);
            }
          }
          catch (IOException localIOException) {}
        }
      });
    }
    catch (IllegalStateException localIllegalStateException) {}
    SharedSecrets.setJavaIOAccess(new JavaIOAccess()
    {
      public Console console()
      {
        if (Console.access$500())
        {
          if (Console.cons == null) {
            Console.access$602(new Console(null));
          }
          return Console.cons;
        }
        return null;
      }
      
      public Charset charset()
      {
        return conscs;
      }
    });
  }
  
  class LineReader
    extends Reader
  {
    private Reader in;
    private char[] cb;
    private int nChars;
    private int nextChar;
    boolean leftoverLF;
    
    LineReader(Reader paramReader)
    {
      in = paramReader;
      cb = new char['Ѐ'];
      nextChar = (nChars = 0);
      leftoverLF = false;
    }
    
    public void close() {}
    
    public boolean ready()
      throws IOException
    {
      return in.ready();
    }
    
    public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (j < 0) || (j > paramArrayOfChar.length)) {
        throw new IndexOutOfBoundsException();
      }
      synchronized (readLock)
      {
        int k = 0;
        int m = 0;
        do
        {
          if (nextChar >= nChars)
          {
            int n = 0;
            do
            {
              n = in.read(cb, 0, cb.length);
            } while (n == 0);
            if (n > 0)
            {
              nChars = n;
              nextChar = 0;
              if ((n < cb.length) && (cb[(n - 1)] != '\n') && (cb[(n - 1)] != '\r')) {
                k = 1;
              }
            }
            else
            {
              if (i - paramInt1 == 0) {
                return -1;
              }
              return i - paramInt1;
            }
          }
          if ((leftoverLF) && (paramArrayOfChar == rcb) && (cb[nextChar] == '\n')) {
            nextChar += 1;
          }
          leftoverLF = false;
          while (nextChar < nChars)
          {
            m = paramArrayOfChar[(i++)] = cb[nextChar];
            cb[(nextChar++)] = '\000';
            if (m == 10) {
              return i - paramInt1;
            }
            if (m == 13)
            {
              if (i == j) {
                if (paramArrayOfChar == rcb)
                {
                  paramArrayOfChar = Console.this.grow();
                  j = paramArrayOfChar.length;
                }
                else
                {
                  leftoverLF = true;
                  return i - paramInt1;
                }
              }
              if ((nextChar == nChars) && (in.ready()))
              {
                nChars = in.read(cb, 0, cb.length);
                nextChar = 0;
              }
              if ((nextChar < nChars) && (cb[nextChar] == '\n'))
              {
                paramArrayOfChar[(i++)] = '\n';
                nextChar += 1;
              }
              return i - paramInt1;
            }
            if (i == j) {
              if (paramArrayOfChar == rcb)
              {
                paramArrayOfChar = Console.this.grow();
                j = paramArrayOfChar.length;
              }
              else
              {
                return i - paramInt1;
              }
            }
          }
        } while (k == 0);
        return i - paramInt1;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\Console.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */