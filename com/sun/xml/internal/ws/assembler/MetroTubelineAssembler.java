package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyDecorator;
import com.sun.xml.internal.ws.dump.LoggingDumpTube;
import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

public class MetroTubelineAssembler
  implements TubelineAssembler
{
  private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";
  public static final MetroConfigNameImpl JAXWS_TUBES_CONFIG_NAMES = new MetroConfigNameImpl("jaxws-tubes-default.xml", "jaxws-tubes.xml");
  private static final Logger LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
  private final BindingID bindingId;
  private final TubelineAssemblyController tubelineAssemblyController;
  
  public MetroTubelineAssembler(BindingID paramBindingID, MetroConfigName paramMetroConfigName)
  {
    bindingId = paramBindingID;
    tubelineAssemblyController = new TubelineAssemblyController(paramMetroConfigName);
  }
  
  TubelineAssemblyController getTubelineAssemblyController()
  {
    return tubelineAssemblyController;
  }
  
  @NotNull
  public Tube createClient(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.finer("Assembling client-side tubeline for WS endpoint: " + paramClientTubeAssemblerContext.getAddress().getURI().toString());
    }
    DefaultClientTubelineAssemblyContext localDefaultClientTubelineAssemblyContext = createClientContext(paramClientTubeAssemblerContext);
    Collection localCollection = tubelineAssemblyController.getTubeCreators(localDefaultClientTubelineAssemblyContext);
    Object localObject = localCollection.iterator();
    while (((Iterator)localObject).hasNext())
    {
      TubeCreator localTubeCreator1 = (TubeCreator)((Iterator)localObject).next();
      localTubeCreator1.updateContext(localDefaultClientTubelineAssemblyContext);
    }
    localObject = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, localDefaultClientTubelineAssemblyContext.getContainer()));
    int i = 1;
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      TubeCreator localTubeCreator2 = (TubeCreator)localIterator.next();
      MessageDumpingInfo localMessageDumpingInfo = setupMessageDumping(localTubeCreator2.getMessageDumpPropertyBase(), Side.Client);
      Tube localTube = localDefaultClientTubelineAssemblyContext.getTubelineHead();
      LoggingDumpTube localLoggingDumpTube1 = null;
      if (dumpAfter)
      {
        localLoggingDumpTube1 = new LoggingDumpTube(logLevel, LoggingDumpTube.Position.After, localDefaultClientTubelineAssemblyContext.getTubelineHead());
        localDefaultClientTubelineAssemblyContext.setTubelineHead(localLoggingDumpTube1);
      }
      if (!localDefaultClientTubelineAssemblyContext.setTubelineHead(((TubelineAssemblyDecorator)localObject).decorateClient(localTubeCreator2.createTube(localDefaultClientTubelineAssemblyContext), localDefaultClientTubelineAssemblyContext)))
      {
        if (localLoggingDumpTube1 != null) {
          localDefaultClientTubelineAssemblyContext.setTubelineHead(localTube);
        }
      }
      else
      {
        String str = localDefaultClientTubelineAssemblyContext.getTubelineHead().getClass().getName();
        if (localLoggingDumpTube1 != null) {
          localLoggingDumpTube1.setLoggedTubeName(str);
        }
        if (dumpBefore)
        {
          LoggingDumpTube localLoggingDumpTube2 = new LoggingDumpTube(logLevel, LoggingDumpTube.Position.Before, localDefaultClientTubelineAssemblyContext.getTubelineHead());
          localLoggingDumpTube2.setLoggedTubeName(str);
          localDefaultClientTubelineAssemblyContext.setTubelineHead(localLoggingDumpTube2);
        }
      }
      if (i != 0)
      {
        localDefaultClientTubelineAssemblyContext.setTubelineHead(((TubelineAssemblyDecorator)localObject).decorateClientTail(localDefaultClientTubelineAssemblyContext.getTubelineHead(), localDefaultClientTubelineAssemblyContext));
        i = 0;
      }
    }
    return ((TubelineAssemblyDecorator)localObject).decorateClientHead(localDefaultClientTubelineAssemblyContext.getTubelineHead(), localDefaultClientTubelineAssemblyContext);
  }
  
  @NotNull
  public Tube createServer(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext)
  {
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.finer("Assembling endpoint tubeline for WS endpoint: " + paramServerTubeAssemblerContext.getEndpoint().getServiceName() + "::" + paramServerTubeAssemblerContext.getEndpoint().getPortName());
    }
    DefaultServerTubelineAssemblyContext localDefaultServerTubelineAssemblyContext = createServerContext(paramServerTubeAssemblerContext);
    Collection localCollection = tubelineAssemblyController.getTubeCreators(localDefaultServerTubelineAssemblyContext);
    Object localObject = localCollection.iterator();
    while (((Iterator)localObject).hasNext())
    {
      TubeCreator localTubeCreator1 = (TubeCreator)((Iterator)localObject).next();
      localTubeCreator1.updateContext(localDefaultServerTubelineAssemblyContext);
    }
    localObject = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, localDefaultServerTubelineAssemblyContext.getEndpoint().getContainer()));
    int i = 1;
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      TubeCreator localTubeCreator2 = (TubeCreator)localIterator.next();
      MessageDumpingInfo localMessageDumpingInfo = setupMessageDumping(localTubeCreator2.getMessageDumpPropertyBase(), Side.Endpoint);
      Tube localTube = localDefaultServerTubelineAssemblyContext.getTubelineHead();
      LoggingDumpTube localLoggingDumpTube1 = null;
      if (dumpAfter)
      {
        localLoggingDumpTube1 = new LoggingDumpTube(logLevel, LoggingDumpTube.Position.After, localDefaultServerTubelineAssemblyContext.getTubelineHead());
        localDefaultServerTubelineAssemblyContext.setTubelineHead(localLoggingDumpTube1);
      }
      if (!localDefaultServerTubelineAssemblyContext.setTubelineHead(((TubelineAssemblyDecorator)localObject).decorateServer(localTubeCreator2.createTube(localDefaultServerTubelineAssemblyContext), localDefaultServerTubelineAssemblyContext)))
      {
        if (localLoggingDumpTube1 != null) {
          localDefaultServerTubelineAssemblyContext.setTubelineHead(localTube);
        }
      }
      else
      {
        String str = localDefaultServerTubelineAssemblyContext.getTubelineHead().getClass().getName();
        if (localLoggingDumpTube1 != null) {
          localLoggingDumpTube1.setLoggedTubeName(str);
        }
        if (dumpBefore)
        {
          LoggingDumpTube localLoggingDumpTube2 = new LoggingDumpTube(logLevel, LoggingDumpTube.Position.Before, localDefaultServerTubelineAssemblyContext.getTubelineHead());
          localLoggingDumpTube2.setLoggedTubeName(str);
          localDefaultServerTubelineAssemblyContext.setTubelineHead(localLoggingDumpTube2);
        }
      }
      if (i != 0)
      {
        localDefaultServerTubelineAssemblyContext.setTubelineHead(((TubelineAssemblyDecorator)localObject).decorateServerTail(localDefaultServerTubelineAssemblyContext.getTubelineHead(), localDefaultServerTubelineAssemblyContext));
        i = 0;
      }
    }
    return ((TubelineAssemblyDecorator)localObject).decorateServerHead(localDefaultServerTubelineAssemblyContext.getTubelineHead(), localDefaultServerTubelineAssemblyContext);
  }
  
  private MessageDumpingInfo setupMessageDumping(String paramString, Side paramSide)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    Object localObject = Level.INFO;
    Boolean localBoolean = getBooleanValue("com.sun.metro.soap.dump");
    if (localBoolean != null)
    {
      bool1 = localBoolean.booleanValue();
      bool2 = localBoolean.booleanValue();
    }
    localBoolean = getBooleanValue("com.sun.metro.soap.dump.before");
    bool1 = localBoolean != null ? localBoolean.booleanValue() : bool1;
    localBoolean = getBooleanValue("com.sun.metro.soap.dump.after");
    bool2 = localBoolean != null ? localBoolean.booleanValue() : bool2;
    Level localLevel = getLevelValue("com.sun.metro.soap.dump.level");
    if (localLevel != null) {
      localObject = localLevel;
    }
    localBoolean = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString());
    if (localBoolean != null)
    {
      bool1 = localBoolean.booleanValue();
      bool2 = localBoolean.booleanValue();
    }
    localBoolean = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString() + ".before");
    bool1 = localBoolean != null ? localBoolean.booleanValue() : bool1;
    localBoolean = getBooleanValue("com.sun.metro.soap.dump." + paramSide.toString() + ".after");
    bool2 = localBoolean != null ? localBoolean.booleanValue() : bool2;
    localLevel = getLevelValue("com.sun.metro.soap.dump." + paramSide.toString() + ".level");
    if (localLevel != null) {
      localObject = localLevel;
    }
    localBoolean = getBooleanValue(paramString);
    if (localBoolean != null)
    {
      bool1 = localBoolean.booleanValue();
      bool2 = localBoolean.booleanValue();
    }
    localBoolean = getBooleanValue(paramString + ".before");
    bool1 = localBoolean != null ? localBoolean.booleanValue() : bool1;
    localBoolean = getBooleanValue(paramString + ".after");
    bool2 = localBoolean != null ? localBoolean.booleanValue() : bool2;
    localLevel = getLevelValue(paramString + ".level");
    if (localLevel != null) {
      localObject = localLevel;
    }
    paramString = paramString + "." + paramSide.toString();
    localBoolean = getBooleanValue(paramString);
    if (localBoolean != null)
    {
      bool1 = localBoolean.booleanValue();
      bool2 = localBoolean.booleanValue();
    }
    localBoolean = getBooleanValue(paramString + ".before");
    bool1 = localBoolean != null ? localBoolean.booleanValue() : bool1;
    localBoolean = getBooleanValue(paramString + ".after");
    bool2 = localBoolean != null ? localBoolean.booleanValue() : bool2;
    localLevel = getLevelValue(paramString + ".level");
    if (localLevel != null) {
      localObject = localLevel;
    }
    return new MessageDumpingInfo(bool1, bool2, (Level)localObject);
  }
  
  private Boolean getBooleanValue(String paramString)
  {
    Boolean localBoolean = null;
    String str = System.getProperty(paramString);
    if (str != null)
    {
      localBoolean = Boolean.valueOf(str);
      LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(paramString, localBoolean));
    }
    return localBoolean;
  }
  
  private Level getLevelValue(String paramString)
  {
    Level localLevel = null;
    String str = System.getProperty(paramString);
    if (str != null)
    {
      LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(paramString, str));
      try
      {
        localLevel = Level.parse(str);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        LOGGER.warning(TubelineassemblyMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(paramString, str), localIllegalArgumentException);
      }
    }
    return localLevel;
  }
  
  protected DefaultServerTubelineAssemblyContext createServerContext(ServerTubeAssemblerContext paramServerTubeAssemblerContext)
  {
    return new DefaultServerTubelineAssemblyContext(paramServerTubeAssemblerContext);
  }
  
  protected DefaultClientTubelineAssemblyContext createClientContext(ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    return new DefaultClientTubelineAssemblyContext(paramClientTubeAssemblerContext);
  }
  
  private static class MessageDumpingInfo
  {
    final boolean dumpBefore;
    final boolean dumpAfter;
    final Level logLevel;
    
    MessageDumpingInfo(boolean paramBoolean1, boolean paramBoolean2, Level paramLevel)
    {
      dumpBefore = paramBoolean1;
      dumpAfter = paramBoolean2;
      logLevel = paramLevel;
    }
  }
  
  private static enum Side
  {
    Client("client"),  Endpoint("endpoint");
    
    private final String name;
    
    private Side(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\MetroTubelineAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */