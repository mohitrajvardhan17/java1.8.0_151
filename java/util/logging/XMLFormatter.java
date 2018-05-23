package java.util.logging;

import java.nio.charset.Charset;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class XMLFormatter
  extends Formatter
{
  private LogManager manager = LogManager.getLogManager();
  
  public XMLFormatter() {}
  
  private void a2(StringBuilder paramStringBuilder, int paramInt)
  {
    if (paramInt < 10) {
      paramStringBuilder.append('0');
    }
    paramStringBuilder.append(paramInt);
  }
  
  private void appendISO8601(StringBuilder paramStringBuilder, long paramLong)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.setTimeInMillis(paramLong);
    paramStringBuilder.append(localGregorianCalendar.get(1));
    paramStringBuilder.append('-');
    a2(paramStringBuilder, localGregorianCalendar.get(2) + 1);
    paramStringBuilder.append('-');
    a2(paramStringBuilder, localGregorianCalendar.get(5));
    paramStringBuilder.append('T');
    a2(paramStringBuilder, localGregorianCalendar.get(11));
    paramStringBuilder.append(':');
    a2(paramStringBuilder, localGregorianCalendar.get(12));
    paramStringBuilder.append(':');
    a2(paramStringBuilder, localGregorianCalendar.get(13));
  }
  
  private void escape(StringBuilder paramStringBuilder, String paramString)
  {
    if (paramString == null) {
      paramString = "<null>";
    }
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (c == '<') {
        paramStringBuilder.append("&lt;");
      } else if (c == '>') {
        paramStringBuilder.append("&gt;");
      } else if (c == '&') {
        paramStringBuilder.append("&amp;");
      } else {
        paramStringBuilder.append(c);
      }
    }
  }
  
  public String format(LogRecord paramLogRecord)
  {
    StringBuilder localStringBuilder = new StringBuilder(500);
    localStringBuilder.append("<record>\n");
    localStringBuilder.append("  <date>");
    appendISO8601(localStringBuilder, paramLogRecord.getMillis());
    localStringBuilder.append("</date>\n");
    localStringBuilder.append("  <millis>");
    localStringBuilder.append(paramLogRecord.getMillis());
    localStringBuilder.append("</millis>\n");
    localStringBuilder.append("  <sequence>");
    localStringBuilder.append(paramLogRecord.getSequenceNumber());
    localStringBuilder.append("</sequence>\n");
    String str = paramLogRecord.getLoggerName();
    if (str != null)
    {
      localStringBuilder.append("  <logger>");
      escape(localStringBuilder, str);
      localStringBuilder.append("</logger>\n");
    }
    localStringBuilder.append("  <level>");
    escape(localStringBuilder, paramLogRecord.getLevel().toString());
    localStringBuilder.append("</level>\n");
    if (paramLogRecord.getSourceClassName() != null)
    {
      localStringBuilder.append("  <class>");
      escape(localStringBuilder, paramLogRecord.getSourceClassName());
      localStringBuilder.append("</class>\n");
    }
    if (paramLogRecord.getSourceMethodName() != null)
    {
      localStringBuilder.append("  <method>");
      escape(localStringBuilder, paramLogRecord.getSourceMethodName());
      localStringBuilder.append("</method>\n");
    }
    localStringBuilder.append("  <thread>");
    localStringBuilder.append(paramLogRecord.getThreadID());
    localStringBuilder.append("</thread>\n");
    if (paramLogRecord.getMessage() != null)
    {
      localObject = formatMessage(paramLogRecord);
      localStringBuilder.append("  <message>");
      escape(localStringBuilder, (String)localObject);
      localStringBuilder.append("</message>");
      localStringBuilder.append("\n");
    }
    Object localObject = paramLogRecord.getResourceBundle();
    try
    {
      if ((localObject != null) && (((ResourceBundle)localObject).getString(paramLogRecord.getMessage()) != null))
      {
        localStringBuilder.append("  <key>");
        escape(localStringBuilder, paramLogRecord.getMessage());
        localStringBuilder.append("</key>\n");
        localStringBuilder.append("  <catalog>");
        escape(localStringBuilder, paramLogRecord.getResourceBundleName());
        localStringBuilder.append("</catalog>\n");
      }
    }
    catch (Exception localException1) {}
    Object[] arrayOfObject = paramLogRecord.getParameters();
    if ((arrayOfObject != null) && (arrayOfObject.length != 0) && (paramLogRecord.getMessage().indexOf("{") == -1)) {
      for (int i = 0; i < arrayOfObject.length; i++)
      {
        localStringBuilder.append("  <param>");
        try
        {
          escape(localStringBuilder, arrayOfObject[i].toString());
        }
        catch (Exception localException2)
        {
          localStringBuilder.append("???");
        }
        localStringBuilder.append("</param>\n");
      }
    }
    if (paramLogRecord.getThrown() != null)
    {
      Throwable localThrowable = paramLogRecord.getThrown();
      localStringBuilder.append("  <exception>\n");
      localStringBuilder.append("    <message>");
      escape(localStringBuilder, localThrowable.toString());
      localStringBuilder.append("</message>\n");
      StackTraceElement[] arrayOfStackTraceElement = localThrowable.getStackTrace();
      for (int j = 0; j < arrayOfStackTraceElement.length; j++)
      {
        StackTraceElement localStackTraceElement = arrayOfStackTraceElement[j];
        localStringBuilder.append("    <frame>\n");
        localStringBuilder.append("      <class>");
        escape(localStringBuilder, localStackTraceElement.getClassName());
        localStringBuilder.append("</class>\n");
        localStringBuilder.append("      <method>");
        escape(localStringBuilder, localStackTraceElement.getMethodName());
        localStringBuilder.append("</method>\n");
        if (localStackTraceElement.getLineNumber() >= 0)
        {
          localStringBuilder.append("      <line>");
          localStringBuilder.append(localStackTraceElement.getLineNumber());
          localStringBuilder.append("</line>\n");
        }
        localStringBuilder.append("    </frame>\n");
      }
      localStringBuilder.append("  </exception>\n");
    }
    localStringBuilder.append("</record>\n");
    return localStringBuilder.toString();
  }
  
  public String getHead(Handler paramHandler)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<?xml version=\"1.0\"");
    String str;
    if (paramHandler != null) {
      str = paramHandler.getEncoding();
    } else {
      str = null;
    }
    if (str == null) {
      str = Charset.defaultCharset().name();
    }
    try
    {
      Charset localCharset = Charset.forName(str);
      str = localCharset.name();
    }
    catch (Exception localException) {}
    localStringBuilder.append(" encoding=\"");
    localStringBuilder.append(str);
    localStringBuilder.append("\"");
    localStringBuilder.append(" standalone=\"no\"?>\n");
    localStringBuilder.append("<!DOCTYPE log SYSTEM \"logger.dtd\">\n");
    localStringBuilder.append("<log>\n");
    return localStringBuilder.toString();
  }
  
  public String getTail(Handler paramHandler)
  {
    return "</log>\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\XMLFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */