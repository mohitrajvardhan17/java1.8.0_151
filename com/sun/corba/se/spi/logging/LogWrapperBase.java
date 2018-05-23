package com.sun.corba.se.spi.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class LogWrapperBase
{
  protected Logger logger;
  protected String loggerName;
  
  protected LogWrapperBase(Logger paramLogger)
  {
    logger = paramLogger;
    loggerName = paramLogger.getName();
  }
  
  protected void doLog(Level paramLevel, String paramString, Object[] paramArrayOfObject, Class paramClass, Throwable paramThrowable)
  {
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
    if (paramArrayOfObject != null) {
      localLogRecord.setParameters(paramArrayOfObject);
    }
    inferCaller(paramClass, localLogRecord);
    localLogRecord.setThrown(paramThrowable);
    localLogRecord.setLoggerName(loggerName);
    localLogRecord.setResourceBundle(logger.getResourceBundle());
    logger.log(localLogRecord);
  }
  
  private void inferCaller(Class paramClass, LogRecord paramLogRecord)
  {
    StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
    StackTraceElement localStackTraceElement = null;
    String str1 = paramClass.getName();
    String str2 = LogWrapperBase.class.getName();
    for (int i = 0; i < arrayOfStackTraceElement.length; i++)
    {
      localStackTraceElement = arrayOfStackTraceElement[i];
      String str3 = localStackTraceElement.getClassName();
      if ((!str3.equals(str1)) && (!str3.equals(str2))) {
        break;
      }
    }
    if (i < arrayOfStackTraceElement.length)
    {
      paramLogRecord.setSourceClassName(localStackTraceElement.getClassName());
      paramLogRecord.setSourceMethodName(localStackTraceElement.getMethodName());
    }
  }
  
  protected void doLog(Level paramLevel, String paramString, Class paramClass, Throwable paramThrowable)
  {
    doLog(paramLevel, paramString, null, paramClass, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\logging\LogWrapperBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */