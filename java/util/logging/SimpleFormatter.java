package java.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import sun.util.logging.LoggingSupport;

public class SimpleFormatter
  extends Formatter
{
  private static final String format = ;
  private final Date dat = new Date();
  
  public SimpleFormatter() {}
  
  public synchronized String format(LogRecord paramLogRecord)
  {
    dat.setTime(paramLogRecord.getMillis());
    String str1;
    if (paramLogRecord.getSourceClassName() != null)
    {
      str1 = paramLogRecord.getSourceClassName();
      if (paramLogRecord.getSourceMethodName() != null) {
        str1 = str1 + " " + paramLogRecord.getSourceMethodName();
      }
    }
    else
    {
      str1 = paramLogRecord.getLoggerName();
    }
    String str2 = formatMessage(paramLogRecord);
    String str3 = "";
    if (paramLogRecord.getThrown() != null)
    {
      StringWriter localStringWriter = new StringWriter();
      PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
      localPrintWriter.println();
      paramLogRecord.getThrown().printStackTrace(localPrintWriter);
      localPrintWriter.close();
      str3 = localStringWriter.toString();
    }
    return String.format(format, new Object[] { dat, str1, paramLogRecord.getLoggerName(), paramLogRecord.getLevel().getLocalizedLevelName(), str2, str3 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\SimpleFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */