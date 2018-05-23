package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.ThreadControllerWrapper;
import java.io.IOException;
import java.io.PrintStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

final class IncrementalSAXSource_Filter
  implements IncrementalSAXSource, ContentHandler, DTDHandler, LexicalHandler, ErrorHandler, Runnable
{
  boolean DEBUG = false;
  private CoroutineManager fCoroutineManager = null;
  private int fControllerCoroutineID = -1;
  private int fSourceCoroutineID = -1;
  private ContentHandler clientContentHandler = null;
  private LexicalHandler clientLexicalHandler = null;
  private DTDHandler clientDTDHandler = null;
  private ErrorHandler clientErrorHandler = null;
  private int eventcounter;
  private int frequency = 5;
  private boolean fNoMoreEvents = false;
  private XMLReader fXMLReader = null;
  private InputSource fXMLReaderInputSource = null;
  
  public IncrementalSAXSource_Filter()
  {
    init(new CoroutineManager(), -1, -1);
  }
  
  public IncrementalSAXSource_Filter(CoroutineManager paramCoroutineManager, int paramInt)
  {
    init(paramCoroutineManager, paramInt, -1);
  }
  
  public static IncrementalSAXSource createIncrementalSAXSource(CoroutineManager paramCoroutineManager, int paramInt)
  {
    return new IncrementalSAXSource_Filter(paramCoroutineManager, paramInt);
  }
  
  public void init(CoroutineManager paramCoroutineManager, int paramInt1, int paramInt2)
  {
    if (paramCoroutineManager == null) {
      paramCoroutineManager = new CoroutineManager();
    }
    fCoroutineManager = paramCoroutineManager;
    fControllerCoroutineID = paramCoroutineManager.co_joinCoroutineSet(paramInt1);
    fSourceCoroutineID = paramCoroutineManager.co_joinCoroutineSet(paramInt2);
    if ((fControllerCoroutineID == -1) || (fSourceCoroutineID == -1)) {
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_COJOINROUTINESET_FAILED", null));
    }
    fNoMoreEvents = false;
    eventcounter = frequency;
  }
  
  public void setXMLReader(XMLReader paramXMLReader)
  {
    fXMLReader = paramXMLReader;
    paramXMLReader.setContentHandler(this);
    paramXMLReader.setDTDHandler(this);
    paramXMLReader.setErrorHandler(this);
    try
    {
      paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    clientContentHandler = paramContentHandler;
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    clientDTDHandler = paramDTDHandler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    clientLexicalHandler = paramLexicalHandler;
  }
  
  public void setErrHandler(ErrorHandler paramErrorHandler)
  {
    clientErrorHandler = paramErrorHandler;
  }
  
  public void setReturnFrequency(int paramInt)
  {
    if (paramInt < 1) {
      paramInt = 1;
    }
    frequency = (eventcounter = paramInt);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    if (clientContentHandler != null) {
      clientContentHandler.endDocument();
    }
    eventcounter = 0;
    co_yield(false);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.endElement(paramString1, paramString2, paramString3);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.endPrefixMapping(paramString);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (--eventcounter <= 0) {
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.setDocumentLocator(paramLocator);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.skippedEntity(paramString);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    co_entry_pause();
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.startDocument();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
    if (clientContentHandler != null) {
      clientContentHandler.startPrefixMapping(paramString1, paramString2);
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.comment(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.endCDATA();
    }
  }
  
  public void endDTD()
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.endDTD();
    }
  }
  
  public void endEntity(String paramString)
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.endEntity(paramString);
    }
  }
  
  public void startCDATA()
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.startCDATA();
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.startDTD(paramString1, paramString2, paramString3);
    }
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    if (null != clientLexicalHandler) {
      clientLexicalHandler.startEntity(paramString);
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (null != clientDTDHandler) {
      clientDTDHandler.notationDecl(paramString1, paramString2, paramString3);
    }
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    if (null != clientDTDHandler) {
      clientDTDHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (null != clientErrorHandler) {
      clientErrorHandler.error(paramSAXParseException);
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (null != clientErrorHandler) {
      clientErrorHandler.error(paramSAXParseException);
    }
    eventcounter = 0;
    co_yield(false);
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (null != clientErrorHandler) {
      clientErrorHandler.error(paramSAXParseException);
    }
  }
  
  public int getSourceCoroutineID()
  {
    return fSourceCoroutineID;
  }
  
  public int getControllerCoroutineID()
  {
    return fControllerCoroutineID;
  }
  
  public CoroutineManager getCoroutineManager()
  {
    return fCoroutineManager;
  }
  
  protected void count_and_yield(boolean paramBoolean)
    throws SAXException
  {
    if (!paramBoolean) {
      eventcounter = 0;
    }
    if (--eventcounter <= 0)
    {
      co_yield(true);
      eventcounter = frequency;
    }
  }
  
  private void co_entry_pause()
    throws SAXException
  {
    if (fCoroutineManager == null) {
      init(null, -1, -1);
    }
    try
    {
      Object localObject = fCoroutineManager.co_entry_pause(fSourceCoroutineID);
      if (localObject == Boolean.FALSE) {
        co_yield(false);
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      if (DEBUG) {
        localNoSuchMethodException.printStackTrace();
      }
      throw new SAXException(localNoSuchMethodException);
    }
  }
  
  private void co_yield(boolean paramBoolean)
    throws SAXException
  {
    if (fNoMoreEvents) {
      return;
    }
    try
    {
      Object localObject = Boolean.FALSE;
      if (paramBoolean) {
        localObject = fCoroutineManager.co_resume(Boolean.TRUE, fSourceCoroutineID, fControllerCoroutineID);
      }
      if (localObject == Boolean.FALSE)
      {
        fNoMoreEvents = true;
        if (fXMLReader != null) {
          throw new StopException();
        }
        fCoroutineManager.co_exit_to(Boolean.FALSE, fSourceCoroutineID, fControllerCoroutineID);
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      fNoMoreEvents = true;
      fCoroutineManager.co_exit(fSourceCoroutineID);
      throw new SAXException(localNoSuchMethodException);
    }
  }
  
  public void startParse(InputSource paramInputSource)
    throws SAXException
  {
    if (fNoMoreEvents) {
      throw new SAXException(XMLMessages.createXMLMessage("ER_INCRSAXSRCFILTER_NOT_RESTARTABLE", null));
    }
    if (fXMLReader == null) {
      throw new SAXException(XMLMessages.createXMLMessage("ER_XMLRDR_NOT_BEFORE_STARTPARSE", null));
    }
    fXMLReaderInputSource = paramInputSource;
    ThreadControllerWrapper.runThread(this, -1);
  }
  
  public void run()
  {
    if (fXMLReader == null) {
      return;
    }
    if (DEBUG) {
      System.out.println("IncrementalSAXSource_Filter parse thread launched");
    }
    Object localObject = Boolean.FALSE;
    try
    {
      fXMLReader.parse(fXMLReaderInputSource);
    }
    catch (IOException localIOException)
    {
      localObject = localIOException;
    }
    catch (StopException localStopException)
    {
      if (DEBUG) {
        System.out.println("Active IncrementalSAXSource_Filter normal stop exception");
      }
    }
    catch (SAXException localSAXException)
    {
      Exception localException = localSAXException.getException();
      if ((localException instanceof StopException))
      {
        if (DEBUG) {
          System.out.println("Active IncrementalSAXSource_Filter normal stop exception");
        }
      }
      else
      {
        if (DEBUG)
        {
          System.out.println("Active IncrementalSAXSource_Filter UNEXPECTED SAX exception: " + localException);
          localException.printStackTrace();
        }
        localObject = localSAXException;
      }
    }
    fXMLReader = null;
    try
    {
      fNoMoreEvents = true;
      fCoroutineManager.co_exit_to(localObject, fSourceCoroutineID, fControllerCoroutineID);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localNoSuchMethodException.printStackTrace(System.err);
      fCoroutineManager.co_exit(fSourceCoroutineID);
    }
  }
  
  public Object deliverMoreNodes(boolean paramBoolean)
  {
    if (fNoMoreEvents) {
      return Boolean.FALSE;
    }
    try
    {
      Object localObject = fCoroutineManager.co_resume(paramBoolean ? Boolean.TRUE : Boolean.FALSE, fControllerCoroutineID, fSourceCoroutineID);
      if (localObject == Boolean.FALSE) {
        fCoroutineManager.co_exit(fControllerCoroutineID);
      }
      return localObject;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return localNoSuchMethodException;
    }
  }
  
  class StopException
    extends RuntimeException
  {
    static final long serialVersionUID = -1129245796185754956L;
    
    StopException() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\IncrementalSAXSource_Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */