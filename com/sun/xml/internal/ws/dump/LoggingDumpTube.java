package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingDumpTube
  extends AbstractFilterTubeImpl
{
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
  private MessageDumper messageDumper;
  private final Level loggingLevel;
  private final Position position;
  private final int tubeId;
  
  public LoggingDumpTube(Level paramLevel, Position paramPosition, Tube paramTube)
  {
    super(paramTube);
    position = paramPosition;
    loggingLevel = paramLevel;
    tubeId = ID_GENERATOR.incrementAndGet();
  }
  
  public void setLoggedTubeName(String paramString)
  {
    assert (messageDumper == null);
    messageDumper = new MessageDumper(paramString, Logger.getLogger(paramString), loggingLevel);
  }
  
  private LoggingDumpTube(LoggingDumpTube paramLoggingDumpTube, TubeCloner paramTubeCloner)
  {
    super(paramLoggingDumpTube, paramTubeCloner);
    messageDumper = messageDumper;
    loggingLevel = loggingLevel;
    position = position;
    tubeId = ID_GENERATOR.incrementAndGet();
  }
  
  public LoggingDumpTube copy(TubeCloner paramTubeCloner)
  {
    return new LoggingDumpTube(this, paramTubeCloner);
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    if (messageDumper.isLoggable())
    {
      Packet localPacket = paramPacket != null ? paramPacket.copy(true) : null;
      messageDumper.dump(MessageDumper.MessageType.Request, position.requestState, Converter.toString(localPacket), tubeId, currentowner.id);
    }
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    if (messageDumper.isLoggable())
    {
      Packet localPacket = paramPacket != null ? paramPacket.copy(true) : null;
      messageDumper.dump(MessageDumper.MessageType.Response, position.responseState, Converter.toString(localPacket), tubeId, currentowner.id);
    }
    return super.processResponse(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable)
  {
    if (messageDumper.isLoggable()) {
      messageDumper.dump(MessageDumper.MessageType.Exception, position.responseState, Converter.toString(paramThrowable), tubeId, currentowner.id);
    }
    return super.processException(paramThrowable);
  }
  
  public void preDestroy()
  {
    super.preDestroy();
  }
  
  public static enum Position
  {
    Before(MessageDumper.ProcessingState.Received, MessageDumper.ProcessingState.Processed),  After(MessageDumper.ProcessingState.Processed, MessageDumper.ProcessingState.Received);
    
    private final MessageDumper.ProcessingState requestState;
    private final MessageDumper.ProcessingState responseState;
    
    private Position(MessageDumper.ProcessingState paramProcessingState1, MessageDumper.ProcessingState paramProcessingState2)
    {
      requestState = paramProcessingState1;
      responseState = paramProcessingState2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\dump\LoggingDumpTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */