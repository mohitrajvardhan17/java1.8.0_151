package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class IncrementalSAXSource_Xerces
  implements IncrementalSAXSource
{
  Method fParseSomeSetup = null;
  Method fParseSome = null;
  Object fPullParserConfig = null;
  Method fConfigSetInput = null;
  Method fConfigParse = null;
  Method fSetInputSource = null;
  Constructor fConfigInputSourceCtor = null;
  Method fConfigSetByteStream = null;
  Method fConfigSetCharStream = null;
  Method fConfigSetEncoding = null;
  Method fReset = null;
  SAXParser fIncrementalParser;
  private boolean fParseInProgress = false;
  private static final Object[] noparms = new Object[0];
  private static final Object[] parmsfalse = { Boolean.FALSE };
  
  public IncrementalSAXSource_Xerces()
    throws NoSuchMethodException
  {
    try
    {
      Class localClass1 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration", true);
      localObject = new Class[] { localClass1 };
      Constructor localConstructor = SAXParser.class.getConstructor((Class[])localObject);
      Class localClass2 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.parsers.StandardParserConfiguration", true);
      fPullParserConfig = localClass2.newInstance();
      Object[] arrayOfObject = { fPullParserConfig };
      fIncrementalParser = ((SAXParser)localConstructor.newInstance(arrayOfObject));
      Class localClass3 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource", true);
      Class[] arrayOfClass1 = { localClass3 };
      fConfigSetInput = localClass2.getMethod("setInputSource", arrayOfClass1);
      Class[] arrayOfClass2 = { String.class, String.class, String.class };
      fConfigInputSourceCtor = localClass3.getConstructor(arrayOfClass2);
      Class[] arrayOfClass3 = { InputStream.class };
      fConfigSetByteStream = localClass3.getMethod("setByteStream", arrayOfClass3);
      Class[] arrayOfClass4 = { Reader.class };
      fConfigSetCharStream = localClass3.getMethod("setCharacterStream", arrayOfClass4);
      Class[] arrayOfClass5 = { String.class };
      fConfigSetEncoding = localClass3.getMethod("setEncoding", arrayOfClass5);
      Class[] arrayOfClass6 = { Boolean.TYPE };
      fConfigParse = localClass2.getMethod("parse", arrayOfClass6);
      Class[] arrayOfClass7 = new Class[0];
      fReset = fIncrementalParser.getClass().getMethod("reset", arrayOfClass7);
    }
    catch (Exception localException)
    {
      Object localObject = new IncrementalSAXSource_Xerces(new SAXParser());
      fParseSomeSetup = fParseSomeSetup;
      fParseSome = fParseSome;
      fIncrementalParser = fIncrementalParser;
    }
  }
  
  public IncrementalSAXSource_Xerces(SAXParser paramSAXParser)
    throws NoSuchMethodException
  {
    fIncrementalParser = paramSAXParser;
    Class localClass = paramSAXParser.getClass();
    Class[] arrayOfClass = { InputSource.class };
    fParseSomeSetup = localClass.getMethod("parseSomeSetup", arrayOfClass);
    arrayOfClass = new Class[0];
    fParseSome = localClass.getMethod("parseSome", arrayOfClass);
  }
  
  public static IncrementalSAXSource createIncrementalSAXSource()
  {
    try
    {
      return new IncrementalSAXSource_Xerces();
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      IncrementalSAXSource_Filter localIncrementalSAXSource_Filter = new IncrementalSAXSource_Filter();
      localIncrementalSAXSource_Filter.setXMLReader(new SAXParser());
      return localIncrementalSAXSource_Filter;
    }
  }
  
  public static IncrementalSAXSource createIncrementalSAXSource(SAXParser paramSAXParser)
  {
    try
    {
      return new IncrementalSAXSource_Xerces(paramSAXParser);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      IncrementalSAXSource_Filter localIncrementalSAXSource_Filter = new IncrementalSAXSource_Filter();
      localIncrementalSAXSource_Filter.setXMLReader(paramSAXParser);
      return localIncrementalSAXSource_Filter;
    }
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    fIncrementalParser.setContentHandler(paramContentHandler);
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    try
    {
      fIncrementalParser.setProperty("http://xml.org/sax/properties/lexical-handler", paramLexicalHandler);
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    fIncrementalParser.setDTDHandler(paramDTDHandler);
  }
  
  public void startParse(InputSource paramInputSource)
    throws SAXException
  {
    if (fIncrementalParser == null) {
      throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_NEEDS_SAXPARSER", null));
    }
    if (fParseInProgress) {
      throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_WHILE_PARSING", null));
    }
    boolean bool = false;
    try
    {
      bool = parseSomeSetup(paramInputSource);
    }
    catch (Exception localException)
    {
      throw new SAXException(localException);
    }
    if (!bool) {
      throw new SAXException(XMLMessages.createXMLMessage("ER_COULD_NOT_INIT_PARSER", null));
    }
  }
  
  public Object deliverMoreNodes(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      fParseInProgress = false;
      return Boolean.FALSE;
    }
    Object localObject;
    try
    {
      boolean bool = parseSome();
      localObject = bool ? Boolean.TRUE : Boolean.FALSE;
    }
    catch (SAXException localSAXException)
    {
      localObject = localSAXException;
    }
    catch (IOException localIOException)
    {
      localObject = localIOException;
    }
    catch (Exception localException)
    {
      localObject = new SAXException(localException);
    }
    return localObject;
  }
  
  private boolean parseSomeSetup(InputSource paramInputSource)
    throws SAXException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    if (fConfigSetInput != null)
    {
      arrayOfObject1 = new Object[] { paramInputSource.getPublicId(), paramInputSource.getSystemId(), null };
      localObject = fConfigInputSourceCtor.newInstance(arrayOfObject1);
      Object[] arrayOfObject2 = { paramInputSource.getByteStream() };
      fConfigSetByteStream.invoke(localObject, arrayOfObject2);
      arrayOfObject2[0] = paramInputSource.getCharacterStream();
      fConfigSetCharStream.invoke(localObject, arrayOfObject2);
      arrayOfObject2[0] = paramInputSource.getEncoding();
      fConfigSetEncoding.invoke(localObject, arrayOfObject2);
      Object[] arrayOfObject3 = new Object[0];
      fReset.invoke(fIncrementalParser, arrayOfObject3);
      arrayOfObject2[0] = localObject;
      fConfigSetInput.invoke(fPullParserConfig, arrayOfObject2);
      return parseSome();
    }
    Object[] arrayOfObject1 = { paramInputSource };
    Object localObject = fParseSomeSetup.invoke(fIncrementalParser, arrayOfObject1);
    return ((Boolean)localObject).booleanValue();
  }
  
  private boolean parseSome()
    throws SAXException, IOException, IllegalAccessException, InvocationTargetException
  {
    if (fConfigSetInput != null)
    {
      localObject = (Boolean)fConfigParse.invoke(fPullParserConfig, parmsfalse);
      return ((Boolean)localObject).booleanValue();
    }
    Object localObject = fParseSome.invoke(fIncrementalParser, noparms);
    return ((Boolean)localObject).booleanValue();
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    System.out.println("Starting...");
    CoroutineManager localCoroutineManager = new CoroutineManager();
    int i = localCoroutineManager.co_joinCoroutineSet(-1);
    if (i == -1)
    {
      System.out.println("ERROR: Couldn't allocate coroutine number.\n");
      return;
    }
    IncrementalSAXSource localIncrementalSAXSource = createIncrementalSAXSource();
    XMLSerializer localXMLSerializer = new XMLSerializer(System.out, null);
    localIncrementalSAXSource.setContentHandler(localXMLSerializer);
    localIncrementalSAXSource.setLexicalHandler(localXMLSerializer);
    for (int j = 0; j < paramArrayOfString.length; j++) {
      try
      {
        InputSource localInputSource = new InputSource(paramArrayOfString[j]);
        Object localObject = null;
        boolean bool = true;
        localIncrementalSAXSource.startParse(localInputSource);
        for (localObject = localIncrementalSAXSource.deliverMoreNodes(bool); localObject == Boolean.TRUE; localObject = localIncrementalSAXSource.deliverMoreNodes(bool))
        {
          System.out.println("\nSome parsing successful, trying more.\n");
          if ((j + 1 < paramArrayOfString.length) && ("!".equals(paramArrayOfString[(j + 1)])))
          {
            j++;
            bool = false;
          }
        }
        if (((localObject instanceof Boolean)) && ((Boolean)localObject == Boolean.FALSE)) {
          System.out.println("\nParser ended (EOF or on request).\n");
        } else if (localObject == null) {
          System.out.println("\nUNEXPECTED: Parser says shut down prematurely.\n");
        } else if ((localObject instanceof Exception)) {
          throw new WrappedRuntimeException((Exception)localObject);
        }
      }
      catch (SAXException localSAXException)
      {
        localSAXException.printStackTrace();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\IncrementalSAXSource_Xerces.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */