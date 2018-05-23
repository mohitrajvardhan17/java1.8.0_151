package java.util.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StreamHandler
  extends Handler
{
  private OutputStream output;
  private boolean doneHeader;
  private volatile Writer writer;
  
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.INFO));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
    try
    {
      setEncoding(localLogManager.getStringProperty(str + ".encoding", null));
    }
    catch (Exception localException1)
    {
      try
      {
        setEncoding(null);
      }
      catch (Exception localException2) {}
    }
  }
  
  public StreamHandler()
  {
    sealed = false;
    configure();
    sealed = true;
  }
  
  public StreamHandler(OutputStream paramOutputStream, Formatter paramFormatter)
  {
    sealed = false;
    configure();
    setFormatter(paramFormatter);
    setOutputStream(paramOutputStream);
    sealed = true;
  }
  
  protected synchronized void setOutputStream(OutputStream paramOutputStream)
    throws SecurityException
  {
    if (paramOutputStream == null) {
      throw new NullPointerException();
    }
    flushAndClose();
    output = paramOutputStream;
    doneHeader = false;
    String str = getEncoding();
    if (str == null) {
      writer = new OutputStreamWriter(output);
    } else {
      try
      {
        writer = new OutputStreamWriter(output, str);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new Error("Unexpected exception " + localUnsupportedEncodingException);
      }
    }
  }
  
  public synchronized void setEncoding(String paramString)
    throws SecurityException, UnsupportedEncodingException
  {
    super.setEncoding(paramString);
    if (output == null) {
      return;
    }
    flush();
    if (paramString == null) {
      writer = new OutputStreamWriter(output);
    } else {
      writer = new OutputStreamWriter(output, paramString);
    }
  }
  
  public synchronized void publish(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord)) {
      return;
    }
    String str;
    try
    {
      str = getFormatter().format(paramLogRecord);
    }
    catch (Exception localException1)
    {
      reportError(null, localException1, 5);
      return;
    }
    try
    {
      if (!doneHeader)
      {
        writer.write(getFormatter().getHead(this));
        doneHeader = true;
      }
      writer.write(str);
    }
    catch (Exception localException2)
    {
      reportError(null, localException2, 1);
    }
  }
  
  public boolean isLoggable(LogRecord paramLogRecord)
  {
    if ((writer == null) || (paramLogRecord == null)) {
      return false;
    }
    return super.isLoggable(paramLogRecord);
  }
  
  public synchronized void flush()
  {
    if (writer != null) {
      try
      {
        writer.flush();
      }
      catch (Exception localException)
      {
        reportError(null, localException, 2);
      }
    }
  }
  
  private synchronized void flushAndClose()
    throws SecurityException
  {
    checkPermission();
    if (writer != null)
    {
      try
      {
        if (!doneHeader)
        {
          writer.write(getFormatter().getHead(this));
          doneHeader = true;
        }
        writer.write(getFormatter().getTail(this));
        writer.flush();
        writer.close();
      }
      catch (Exception localException)
      {
        reportError(null, localException, 3);
      }
      writer = null;
      output = null;
    }
  }
  
  public synchronized void close()
    throws SecurityException
  {
    flushAndClose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\StreamHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */