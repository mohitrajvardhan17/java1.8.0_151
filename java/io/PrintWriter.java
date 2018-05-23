package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import sun.security.action.GetPropertyAction;

public class PrintWriter
  extends Writer
{
  protected Writer out;
  private final boolean autoFlush;
  private boolean trouble = false;
  private Formatter formatter;
  private PrintStream psOut = null;
  private final String lineSeparator;
  
  private static Charset toCharset(String paramString)
    throws UnsupportedEncodingException
  {
    Objects.requireNonNull(paramString, "charsetName");
    try
    {
      return Charset.forName(paramString);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString);
    }
  }
  
  public PrintWriter(Writer paramWriter)
  {
    this(paramWriter, false);
  }
  
  public PrintWriter(Writer paramWriter, boolean paramBoolean)
  {
    super(paramWriter);
    out = paramWriter;
    autoFlush = paramBoolean;
    lineSeparator = ((String)AccessController.doPrivileged(new GetPropertyAction("line.separator")));
  }
  
  public PrintWriter(OutputStream paramOutputStream)
  {
    this(paramOutputStream, false);
  }
  
  public PrintWriter(OutputStream paramOutputStream, boolean paramBoolean)
  {
    this(new BufferedWriter(new OutputStreamWriter(paramOutputStream)), paramBoolean);
    if ((paramOutputStream instanceof PrintStream)) {
      psOut = ((PrintStream)paramOutputStream);
    }
  }
  
  public PrintWriter(String paramString)
    throws FileNotFoundException
  {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramString))), false);
  }
  
  private PrintWriter(Charset paramCharset, File paramFile)
    throws FileNotFoundException
  {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile), paramCharset)), false);
  }
  
  public PrintWriter(String paramString1, String paramString2)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(toCharset(paramString2), new File(paramString1));
  }
  
  public PrintWriter(File paramFile)
    throws FileNotFoundException
  {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile))), false);
  }
  
  public PrintWriter(File paramFile, String paramString)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(toCharset(paramString), paramFile);
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (out == null) {
      throw new IOException("Stream closed");
    }
  }
  
  public void flush()
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        out.flush();
      }
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public void close()
  {
    try
    {
      synchronized (lock)
      {
        if (out == null) {
          return;
        }
        out.close();
        out = null;
      }
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public boolean checkError()
  {
    if (out != null) {
      flush();
    }
    if ((out instanceof PrintWriter))
    {
      PrintWriter localPrintWriter = (PrintWriter)out;
      return localPrintWriter.checkError();
    }
    if (psOut != null) {
      return psOut.checkError();
    }
    return trouble;
  }
  
  protected void setError()
  {
    trouble = true;
  }
  
  protected void clearError()
  {
    trouble = false;
  }
  
  public void write(int paramInt)
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        out.write(paramInt);
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        out.write(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public void write(char[] paramArrayOfChar)
  {
    write(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        out.write(paramString, paramInt1, paramInt2);
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public void write(String paramString)
  {
    write(paramString, 0, paramString.length());
  }
  
  private void newLine()
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        out.write(lineSeparator);
        if (autoFlush) {
          out.flush();
        }
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
  }
  
  public void print(boolean paramBoolean)
  {
    write(paramBoolean ? "true" : "false");
  }
  
  public void print(char paramChar)
  {
    write(paramChar);
  }
  
  public void print(int paramInt)
  {
    write(String.valueOf(paramInt));
  }
  
  public void print(long paramLong)
  {
    write(String.valueOf(paramLong));
  }
  
  public void print(float paramFloat)
  {
    write(String.valueOf(paramFloat));
  }
  
  public void print(double paramDouble)
  {
    write(String.valueOf(paramDouble));
  }
  
  public void print(char[] paramArrayOfChar)
  {
    write(paramArrayOfChar);
  }
  
  public void print(String paramString)
  {
    if (paramString == null) {
      paramString = "null";
    }
    write(paramString);
  }
  
  public void print(Object paramObject)
  {
    write(String.valueOf(paramObject));
  }
  
  public void println()
  {
    newLine();
  }
  
  public void println(boolean paramBoolean)
  {
    synchronized (lock)
    {
      print(paramBoolean);
      println();
    }
  }
  
  public void println(char paramChar)
  {
    synchronized (lock)
    {
      print(paramChar);
      println();
    }
  }
  
  public void println(int paramInt)
  {
    synchronized (lock)
    {
      print(paramInt);
      println();
    }
  }
  
  public void println(long paramLong)
  {
    synchronized (lock)
    {
      print(paramLong);
      println();
    }
  }
  
  public void println(float paramFloat)
  {
    synchronized (lock)
    {
      print(paramFloat);
      println();
    }
  }
  
  public void println(double paramDouble)
  {
    synchronized (lock)
    {
      print(paramDouble);
      println();
    }
  }
  
  public void println(char[] paramArrayOfChar)
  {
    synchronized (lock)
    {
      print(paramArrayOfChar);
      println();
    }
  }
  
  public void println(String paramString)
  {
    synchronized (lock)
    {
      print(paramString);
      println();
    }
  }
  
  public void println(Object paramObject)
  {
    String str = String.valueOf(paramObject);
    synchronized (lock)
    {
      print(str);
      println();
    }
  }
  
  public PrintWriter printf(String paramString, Object... paramVarArgs)
  {
    return format(paramString, paramVarArgs);
  }
  
  public PrintWriter printf(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    return format(paramLocale, paramString, paramVarArgs);
  }
  
  public PrintWriter format(String paramString, Object... paramVarArgs)
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != Locale.getDefault())) {
          formatter = new Formatter(this);
        }
        formatter.format(Locale.getDefault(), paramString, paramVarArgs);
        if (autoFlush) {
          out.flush();
        }
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
    return this;
  }
  
  public PrintWriter format(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    try
    {
      synchronized (lock)
      {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != paramLocale)) {
          formatter = new Formatter(this, paramLocale);
        }
        formatter.format(paramLocale, paramString, paramVarArgs);
        if (autoFlush) {
          out.flush();
        }
      }
    }
    catch (InterruptedIOException localInterruptedIOException)
    {
      Thread.currentThread().interrupt();
    }
    catch (IOException localIOException)
    {
      trouble = true;
    }
    return this;
  }
  
  public PrintWriter append(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      write("null");
    } else {
      write(paramCharSequence.toString());
    }
    return this;
  }
  
  public PrintWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    CharSequence localCharSequence = paramCharSequence == null ? "null" : paramCharSequence;
    write(localCharSequence.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public PrintWriter append(char paramChar)
  {
    write(paramChar);
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PrintWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */