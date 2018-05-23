package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

final class ParserSAX
  extends Parser
  implements XMLReader, Locator
{
  public static final String FEATURE_NS = "http://xml.org/sax/features/namespaces";
  public static final String FEATURE_PREF = "http://xml.org/sax/features/namespace-prefixes";
  private boolean mFNamespaces = true;
  private boolean mFPrefixes = false;
  private DefaultHandler mHand = new DefaultHandler();
  private ContentHandler mHandCont = mHand;
  private DTDHandler mHandDtd = mHand;
  private ErrorHandler mHandErr = mHand;
  private EntityResolver mHandEnt = mHand;
  
  public ParserSAX() {}
  
  public ContentHandler getContentHandler()
  {
    return mHandCont != mHand ? mHandCont : null;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    if (paramContentHandler == null) {
      throw new NullPointerException();
    }
    mHandCont = paramContentHandler;
  }
  
  public DTDHandler getDTDHandler()
  {
    return mHandDtd != mHand ? mHandDtd : null;
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    if (paramDTDHandler == null) {
      throw new NullPointerException();
    }
    mHandDtd = paramDTDHandler;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return mHandErr != mHand ? mHandErr : null;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    if (paramErrorHandler == null) {
      throw new NullPointerException();
    }
    mHandErr = paramErrorHandler;
  }
  
  public EntityResolver getEntityResolver()
  {
    return mHandEnt != mHand ? mHandEnt : null;
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    if (paramEntityResolver == null) {
      throw new NullPointerException();
    }
    mHandEnt = paramEntityResolver;
  }
  
  public String getPublicId()
  {
    return mInp != null ? mInp.pubid : null;
  }
  
  public String getSystemId()
  {
    return mInp != null ? mInp.sysid : null;
  }
  
  public int getLineNumber()
  {
    return -1;
  }
  
  public int getColumnNumber()
  {
    return -1;
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    parse(new InputSource(paramString));
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    if (paramInputSource == null) {
      throw new IllegalArgumentException("");
    }
    mInp = new Input(512);
    mPh = -1;
    try
    {
      setinp(paramInputSource);
    }
    catch (SAXException localSAXException)
    {
      throw localSAXException;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      panic(localException.toString());
    }
    parse();
  }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if ((paramInputStream == null) || (paramDefaultHandler == null)) {
      throw new IllegalArgumentException("");
    }
    parse(new InputSource(paramInputStream), paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if ((paramInputSource == null) || (paramDefaultHandler == null)) {
      throw new IllegalArgumentException("");
    }
    mHandCont = paramDefaultHandler;
    mHandDtd = paramDefaultHandler;
    mHandErr = paramDefaultHandler;
    mHandEnt = paramDefaultHandler;
    mInp = new Input(512);
    mPh = -1;
    try
    {
      setinp(paramInputSource);
    }
    catch (SAXException|IOException|RuntimeException localSAXException)
    {
      throw localSAXException;
    }
    catch (Exception localException)
    {
      panic(localException.toString());
    }
    parse();
  }
  
  private void parse()
    throws SAXException, IOException
  {
    init();
    try
    {
      mHandCont.setDocumentLocator(this);
      mHandCont.startDocument();
      if (mPh != 1) {
        mPh = 1;
      }
      int i = 0;
      do
      {
        wsskip();
        switch (i = step())
        {
        case 1: 
        case 2: 
          mPh = 4;
          break;
        case 6: 
        case 8: 
          break;
        case 9: 
          if (mPh >= 3) {
            panic("");
          }
          mPh = 3;
          break;
        case 3: 
        case 4: 
        case 5: 
        case 7: 
        default: 
          panic("");
        }
      } while (mPh < 4);
      do
      {
        switch (i)
        {
        case 1: 
        case 2: 
          if (mIsNSAware == true) {
            mHandCont.startElement(mElm.value, mElm.name, "", mAttrs);
          } else {
            mHandCont.startElement("", "", mElm.name, mAttrs);
          }
          if (i == 2) {
            i = step();
          }
          break;
        case 3: 
          if (mIsNSAware == true) {
            mHandCont.endElement(mElm.value, mElm.name, "");
          } else {
            mHandCont.endElement("", "", mElm.name);
          }
          while (mPref.list == mElm)
          {
            mHandCont.endPrefixMapping(mPref.name);
            mPref = del(mPref);
          }
          mElm = del(mElm);
          if (mElm == null) {
            mPh = 5;
          } else {
            i = step();
          }
          break;
        case 4: 
        case 5: 
        case 6: 
        case 7: 
        case 8: 
        case 10: 
          i = step();
          break;
        }
        panic("");
      } while (mPh == 4);
      do
      {
        if (wsskip() == 65535) {
          break;
        }
        switch (step())
        {
        case 6: 
        case 8: 
          break;
        default: 
          panic("");
        }
      } while (mPh == 5);
      mPh = 6;
    }
    catch (SAXException localSAXException)
    {
      throw localSAXException;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      panic(localException.toString());
    }
    finally
    {
      mHandCont.endDocument();
      cleanup();
    }
  }
  
  protected void docType(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    mHandDtd.notationDecl(paramString1, paramString2, paramString3);
  }
  
  protected void comm(char[] paramArrayOfChar, int paramInt) {}
  
  protected void pi(String paramString1, String paramString2)
    throws SAXException
  {
    mHandCont.processingInstruction(paramString1, paramString2);
  }
  
  protected void newPrefix()
    throws SAXException
  {
    mHandCont.startPrefixMapping(mPref.name, mPref.value);
  }
  
  protected void skippedEnt(String paramString)
    throws SAXException
  {
    mHandCont.skippedEntity(paramString);
  }
  
  protected InputSource resolveEnt(String paramString1, String paramString2, String paramString3)
    throws SAXException, IOException
  {
    return mHandEnt.resolveEntity(paramString2, paramString3);
  }
  
  protected void notDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    mHandDtd.notationDecl(paramString1, paramString2, paramString3);
  }
  
  protected void unparsedEntDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    mHandDtd.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
  }
  
  protected void panic(String paramString)
    throws SAXException
  {
    SAXParseException localSAXParseException = new SAXParseException(paramString, this);
    mHandErr.fatalError(localSAXParseException);
    throw localSAXParseException;
  }
  
  protected void bflash()
    throws SAXException
  {
    if (mBuffIdx >= 0)
    {
      mHandCont.characters(mBuff, 0, mBuffIdx + 1);
      mBuffIdx = -1;
    }
  }
  
  protected void bflash_ws()
    throws SAXException
  {
    if (mBuffIdx >= 0)
    {
      mHandCont.characters(mBuff, 0, mBuffIdx + 1);
      mBuffIdx = -1;
    }
  }
  
  public boolean getFeature(String paramString)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object getProperty(String paramString)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\ParserSAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */