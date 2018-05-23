package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Formatter;
import java.util.Locale;

public class PrintStream
  extends FilterOutputStream
  implements Appendable, Closeable
{
  private final boolean autoFlush;
  private boolean trouble = false;
  private Formatter formatter;
  private BufferedWriter textOut;
  private OutputStreamWriter charOut;
  private boolean closing = false;
  
  private static <T> T requireNonNull(T paramT, String paramString)
  {
    if (paramT == null) {
      throw new NullPointerException(paramString);
    }
    return paramT;
  }
  
  private static Charset toCharset(String paramString)
    throws UnsupportedEncodingException
  {
    requireNonNull(paramString, "charsetName");
    try
    {
      return Charset.forName(paramString);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString);
    }
  }
  
  private PrintStream(boolean paramBoolean, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    autoFlush = paramBoolean;
    charOut = new OutputStreamWriter(this);
    textOut = new BufferedWriter(charOut);
  }
  
  private PrintStream(boolean paramBoolean, OutputStream paramOutputStream, Charset paramCharset)
  {
    super(paramOutputStream);
    autoFlush = paramBoolean;
    charOut = new OutputStreamWriter(this, paramCharset);
    textOut = new BufferedWriter(charOut);
  }
  
  private PrintStream(boolean paramBoolean, Charset paramCharset, OutputStream paramOutputStream)
    throws UnsupportedEncodingException
  {
    this(paramBoolean, paramOutputStream, paramCharset);
  }
  
  public PrintStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, false);
  }
  
  public PrintStream(OutputStream paramOutputStream, boolean paramBoolean)
  {
    this(paramBoolean, (OutputStream)requireNonNull(paramOutputStream, "Null output stream"));
  }
  
  public PrintStream(OutputStream paramOutputStream, boolean paramBoolean, String paramString)
    throws UnsupportedEncodingException
  {
    this(paramBoolean, (OutputStream)requireNonNull(paramOutputStream, "Null output stream"), toCharset(paramString));
  }
  
  public PrintStream(String paramString)
    throws FileNotFoundException
  {
    this(false, new FileOutputStream(paramString));
  }
  
  public PrintStream(String paramString1, String paramString2)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(false, toCharset(paramString2), new FileOutputStream(paramString1));
  }
  
  public PrintStream(File paramFile)
    throws FileNotFoundException
  {
    this(false, new FileOutputStream(paramFile));
  }
  
  public PrintStream(File paramFile, String paramString)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(false, toCharset(paramString), new FileOutputStream(paramFile));
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
    synchronized (this)
    {
      try
      {
        ensureOpen();
        out.flush();
      }
      catch (IOException localIOException)
      {
        trouble = true;
      }
    }
  }
  
  public void close()
  {
    synchronized (this)
    {
      if (!closing)
      {
        closing = true;
        try
        {
          textOut.close();
          out.close();
        }
        catch (IOException localIOException)
        {
          trouble = true;
        }
        textOut = null;
        charOut = null;
        out = null;
      }
    }
  }
  
  public boolean checkError()
  {
    if (out != null) {
      flush();
    }
    if ((out instanceof PrintStream))
    {
      PrintStream localPrintStream = (PrintStream)out;
      return localPrintStream.checkError();
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
      synchronized (this)
      {
        ensureOpen();
        out.write(paramInt);
        if ((paramInt == 10) && (autoFlush)) {
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
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        out.write(paramArrayOfByte, paramInt1, paramInt2);
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
  
  private void write(char[] paramArrayOfChar)
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        textOut.write(paramArrayOfChar);
        textOut.flushBuffer();
        charOut.flushBuffer();
        if (autoFlush) {
          for (int i = 0; i < paramArrayOfChar.length; i++) {
            if (paramArrayOfChar[i] == '\n') {
              out.flush();
            }
          }
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
  
  private void write(String paramString)
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        textOut.write(paramString);
        textOut.flushBuffer();
        charOut.flushBuffer();
        if ((autoFlush) && (paramString.indexOf('\n') >= 0)) {
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
  
  private void newLine()
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        textOut.newLine();
        textOut.flushBuffer();
        charOut.flushBuffer();
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
    write(String.valueOf(paramChar));
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
    synchronized (this)
    {
      print(paramBoolean);
      newLine();
    }
  }
  
  public void println(char paramChar)
  {
    synchronized (this)
    {
      print(paramChar);
      newLine();
    }
  }
  
  public void println(int paramInt)
  {
    synchronized (this)
    {
      print(paramInt);
      newLine();
    }
  }
  
  public void println(long paramLong)
  {
    synchronized (this)
    {
      print(paramLong);
      newLine();
    }
  }
  
  public void println(float paramFloat)
  {
    synchronized (this)
    {
      print(paramFloat);
      newLine();
    }
  }
  
  public void println(double paramDouble)
  {
    synchronized (this)
    {
      print(paramDouble);
      newLine();
    }
  }
  
  public void println(char[] paramArrayOfChar)
  {
    synchronized (this)
    {
      print(paramArrayOfChar);
      newLine();
    }
  }
  
  public void println(String paramString)
  {
    synchronized (this)
    {
      print(paramString);
      newLine();
    }
  }
  
  public void println(Object paramObject)
  {
    String str = String.valueOf(paramObject);
    synchronized (this)
    {
      print(str);
      newLine();
    }
  }
  
  public PrintStream printf(String paramString, Object... paramVarArgs)
  {
    return format(paramString, paramVarArgs);
  }
  
  public PrintStream printf(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    return format(paramLocale, paramString, paramVarArgs);
  }
  
  public PrintStream format(String paramString, Object... paramVarArgs)
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != Locale.getDefault())) {
          formatter = new Formatter(this);
        }
        formatter.format(Locale.getDefault(), paramString, paramVarArgs);
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
  
  public PrintStream format(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    try
    {
      synchronized (this)
      {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != paramLocale)) {
          formatter = new Formatter(this, paramLocale);
        }
        formatter.format(paramLocale, paramString, paramVarArgs);
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
  
  public PrintStream append(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      print("null");
    } else {
      print(paramCharSequence.toString());
    }
    return this;
  }
  
  public PrintStream append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    CharSequence localCharSequence = paramCharSequence == null ? "null" : paramCharSequence;
    write(localCharSequence.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public PrintStream append(char paramChar)
  {
    print(paramChar);
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PrintStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */