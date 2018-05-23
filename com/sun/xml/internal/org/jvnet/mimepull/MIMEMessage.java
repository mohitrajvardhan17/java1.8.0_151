package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEMessage
{
  private static final Logger LOGGER = Logger.getLogger(MIMEMessage.class.getName());
  MIMEConfig config;
  private final InputStream in;
  private final List<MIMEPart> partsList;
  private final Map<String, MIMEPart> partsMap;
  private final Iterator<MIMEEvent> it;
  private boolean parsed;
  private MIMEPart currentPart;
  private int currentIndex;
  
  public MIMEMessage(InputStream paramInputStream, String paramString)
  {
    this(paramInputStream, paramString, new MIMEConfig());
  }
  
  public MIMEMessage(InputStream paramInputStream, String paramString, MIMEConfig paramMIMEConfig)
  {
    in = paramInputStream;
    config = paramMIMEConfig;
    MIMEParser localMIMEParser = new MIMEParser(paramInputStream, paramString, paramMIMEConfig);
    it = localMIMEParser.iterator();
    partsList = new ArrayList();
    partsMap = new HashMap();
    if (paramMIMEConfig.isParseEagerly()) {
      parseAll();
    }
  }
  
  public List<MIMEPart> getAttachments()
  {
    if (!parsed) {
      parseAll();
    }
    return partsList;
  }
  
  public MIMEPart getPart(int paramInt)
  {
    LOGGER.log(Level.FINE, "index={0}", Integer.valueOf(paramInt));
    MIMEPart localMIMEPart = paramInt < partsList.size() ? (MIMEPart)partsList.get(paramInt) : null;
    if ((parsed) && (localMIMEPart == null)) {
      throw new MIMEParsingException("There is no " + paramInt + " attachment part ");
    }
    if (localMIMEPart == null)
    {
      localMIMEPart = new MIMEPart(this);
      partsList.add(paramInt, localMIMEPart);
    }
    LOGGER.log(Level.FINE, "Got attachment at index={0} attachment={1}", new Object[] { Integer.valueOf(paramInt), localMIMEPart });
    return localMIMEPart;
  }
  
  public MIMEPart getPart(String paramString)
  {
    LOGGER.log(Level.FINE, "Content-ID={0}", paramString);
    MIMEPart localMIMEPart = getDecodedCidPart(paramString);
    if ((parsed) && (localMIMEPart == null)) {
      throw new MIMEParsingException("There is no attachment part with Content-ID = " + paramString);
    }
    if (localMIMEPart == null)
    {
      localMIMEPart = new MIMEPart(this, paramString);
      partsMap.put(paramString, localMIMEPart);
    }
    LOGGER.log(Level.FINE, "Got attachment for Content-ID={0} attachment={1}", new Object[] { paramString, localMIMEPart });
    return localMIMEPart;
  }
  
  private MIMEPart getDecodedCidPart(String paramString)
  {
    MIMEPart localMIMEPart = (MIMEPart)partsMap.get(paramString);
    if ((localMIMEPart == null) && (paramString.indexOf('%') != -1)) {
      try
      {
        String str = URLDecoder.decode(paramString, "utf-8");
        localMIMEPart = (MIMEPart)partsMap.get(str);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    }
    return localMIMEPart;
  }
  
  public final void parseAll()
  {
    while (makeProgress()) {}
  }
  
  public synchronized boolean makeProgress()
  {
    if (!it.hasNext()) {
      return false;
    }
    MIMEEvent localMIMEEvent = (MIMEEvent)it.next();
    switch (localMIMEEvent.getEventType())
    {
    case START_MESSAGE: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_MESSAGE);
      break;
    case START_PART: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_PART);
      break;
    case HEADERS: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.HEADERS);
      MIMEEvent.Headers localHeaders = (MIMEEvent.Headers)localMIMEEvent;
      InternetHeaders localInternetHeaders = localHeaders.getHeaders();
      List localList = localInternetHeaders.getHeader("content-id");
      String str = currentIndex + "";
      if ((str.length() > 2) && (str.charAt(0) == '<')) {
        str = str.substring(1, str.length() - 1);
      }
      MIMEPart localMIMEPart1 = currentIndex < partsList.size() ? (MIMEPart)partsList.get(currentIndex) : null;
      MIMEPart localMIMEPart2 = getDecodedCidPart(str);
      if ((localMIMEPart1 == null) && (localMIMEPart2 == null))
      {
        currentPart = getPart(str);
        partsList.add(currentIndex, currentPart);
      }
      else if (localMIMEPart1 == null)
      {
        currentPart = localMIMEPart2;
        partsList.add(currentIndex, localMIMEPart2);
      }
      else if (localMIMEPart2 == null)
      {
        currentPart = localMIMEPart1;
        currentPart.setContentId(str);
        partsMap.put(str, currentPart);
      }
      else if (localMIMEPart1 != localMIMEPart2)
      {
        throw new MIMEParsingException("Created two different attachments using Content-ID and index");
      }
      currentPart.setHeaders(localInternetHeaders);
      break;
    case CONTENT: 
      LOGGER.log(Level.FINER, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.CONTENT);
      MIMEEvent.Content localContent = (MIMEEvent.Content)localMIMEEvent;
      ByteBuffer localByteBuffer = localContent.getData();
      currentPart.addBody(localByteBuffer);
      break;
    case END_PART: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_PART);
      currentPart.doneParsing();
      currentIndex += 1;
      break;
    case END_MESSAGE: 
      LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_MESSAGE);
      parsed = true;
      try
      {
        in.close();
      }
      catch (IOException localIOException)
      {
        throw new MIMEParsingException(localIOException);
      }
    default: 
      throw new MIMEParsingException("Unknown Parser state = " + localMIMEEvent.getEventType());
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */