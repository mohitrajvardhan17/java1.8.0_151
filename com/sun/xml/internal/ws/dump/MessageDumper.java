package com.sun.xml.internal.ws.dump;

import java.util.logging.Level;
import java.util.logging.Logger;

final class MessageDumper
{
  private final String tubeName;
  private final Logger logger;
  private Level loggingLevel;
  
  public MessageDumper(String paramString, Logger paramLogger, Level paramLevel)
  {
    tubeName = paramString;
    logger = paramLogger;
    loggingLevel = paramLevel;
  }
  
  final boolean isLoggable()
  {
    return logger.isLoggable(loggingLevel);
  }
  
  final void setLoggingLevel(Level paramLevel)
  {
    loggingLevel = paramLevel;
  }
  
  final String createLogMessage(MessageType paramMessageType, ProcessingState paramProcessingState, int paramInt, String paramString1, String paramString2)
  {
    return String.format("%s %s in Tube [ %s ] Instance [ %d ] Engine [ %s ] Thread [ %s ]:%n%s", new Object[] { paramMessageType, paramProcessingState, tubeName, Integer.valueOf(paramInt), paramString1, Thread.currentThread().getName(), paramString2 });
  }
  
  final String dump(MessageType paramMessageType, ProcessingState paramProcessingState, String paramString1, int paramInt, String paramString2)
  {
    String str = createLogMessage(paramMessageType, paramProcessingState, paramInt, paramString2, paramString1);
    logger.log(loggingLevel, str);
    return str;
  }
  
  static enum MessageType
  {
    Request("Request message"),  Response("Response message"),  Exception("Response exception");
    
    private final String name;
    
    private MessageType(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return name;
    }
  }
  
  static enum ProcessingState
  {
    Received("received"),  Processed("processed");
    
    private final String name;
    
    private ProcessingState(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */