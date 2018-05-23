package com.sun.xml.internal.ws.dump;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class MessageDumpingFeature
  extends WebServiceFeature
{
  public static final String ID = "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature";
  private static final Level DEFAULT_MSG_LOG_LEVEL = Level.FINE;
  private final Queue<String> messageQueue = paramBoolean ? new ConcurrentLinkedQueue() : null;
  private final AtomicBoolean messageLoggingStatus = new AtomicBoolean(true);
  private final String messageLoggingRoot;
  private final Level messageLoggingLevel;
  
  public MessageDumpingFeature()
  {
    this(null, null, true);
  }
  
  public MessageDumpingFeature(String paramString, Level paramLevel, boolean paramBoolean)
  {
    messageLoggingRoot = ((paramString != null) && (paramString.length() > 0) ? paramString : "com.sun.xml.internal.ws.messagedump");
    messageLoggingLevel = (paramLevel != null ? paramLevel : DEFAULT_MSG_LOG_LEVEL);
    enabled = true;
  }
  
  public MessageDumpingFeature(boolean paramBoolean)
  {
    this();
    enabled = paramBoolean;
  }
  
  @FeatureConstructor({"enabled", "messageLoggingRoot", "messageLoggingLevel", "storeMessages"})
  public MessageDumpingFeature(boolean paramBoolean1, String paramString1, String paramString2, boolean paramBoolean2)
  {
    this(paramString1, Level.parse(paramString2), paramBoolean2);
    enabled = paramBoolean1;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature";
  }
  
  public String nextMessage()
  {
    return messageQueue != null ? (String)messageQueue.poll() : null;
  }
  
  public void enableMessageLogging()
  {
    messageLoggingStatus.set(true);
  }
  
  public void disableMessageLogging()
  {
    messageLoggingStatus.set(false);
  }
  
  @ManagedAttribute
  public boolean getMessageLoggingStatus()
  {
    return messageLoggingStatus.get();
  }
  
  @ManagedAttribute
  public String getMessageLoggingRoot()
  {
    return messageLoggingRoot;
  }
  
  @ManagedAttribute
  public Level getMessageLoggingLevel()
  {
    return messageLoggingLevel;
  }
  
  boolean offerMessage(String paramString)
  {
    return messageQueue != null ? messageQueue.offer(paramString) : false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumpingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */