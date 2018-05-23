package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class Transform
{
  private SerializationHandler _handler;
  private String _fileName;
  private String _className;
  private String _jarFileSrc;
  private boolean _isJarFileSpecified = false;
  private Vector _params = null;
  private boolean _uri;
  private boolean _debug;
  private int _iterations;
  
  public Transform(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    _fileName = paramString2;
    _className = paramString1;
    _uri = paramBoolean1;
    _debug = paramBoolean2;
    _iterations = paramInt;
  }
  
  public String getFileName()
  {
    return _fileName;
  }
  
  public String getClassName()
  {
    return _className;
  }
  
  public void setParameters(Vector paramVector)
  {
    _params = paramVector;
  }
  
  private void setJarFileInputSrc(boolean paramBoolean, String paramString)
  {
    _isJarFileSpecified = paramBoolean;
    _jarFileSrc = paramString;
  }
  
  private void doTransform()
  {
    try
    {
      Class localClass = ObjectFactory.findProviderClass(_className, true);
      localObject = (AbstractTranslet)localClass.newInstance();
      ((AbstractTranslet)localObject).postInitialization();
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      try
      {
        localSAXParserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
      }
      catch (Exception localException2)
      {
        localSAXParserFactory.setNamespaceAware(true);
      }
      SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
      XMLReader localXMLReader = localSAXParser.getXMLReader();
      XSLTCDTMManager localXSLTCDTMManager = XSLTCDTMManager.createNewDTMManagerInstance();
      DOMWSFilter localDOMWSFilter;
      if ((localObject != null) && ((localObject instanceof StripFilter))) {
        localDOMWSFilter = new DOMWSFilter((AbstractTranslet)localObject);
      } else {
        localDOMWSFilter = null;
      }
      DOMEnhancedForDTM localDOMEnhancedForDTM = (DOMEnhancedForDTM)localXSLTCDTMManager.getDTM(new SAXSource(localXMLReader, new InputSource(_fileName)), false, localDOMWSFilter, true, false, ((AbstractTranslet)localObject).hasIdCall());
      localDOMEnhancedForDTM.setDocumentURI(_fileName);
      ((AbstractTranslet)localObject).prepassDocument(localDOMEnhancedForDTM);
      int i = _params.size();
      for (int j = 0; j < i; j++)
      {
        Parameter localParameter = (Parameter)_params.elementAt(j);
        ((AbstractTranslet)localObject).addParameter(_name, _value);
      }
      TransletOutputHandlerFactory localTransletOutputHandlerFactory = TransletOutputHandlerFactory.newInstance();
      localTransletOutputHandlerFactory.setOutputType(0);
      localTransletOutputHandlerFactory.setEncoding(_encoding);
      localTransletOutputHandlerFactory.setOutputMethod(_method);
      if (_iterations == -1)
      {
        ((AbstractTranslet)localObject).transform(localDOMEnhancedForDTM, localTransletOutputHandlerFactory.getSerializationHandler());
      }
      else if (_iterations > 0)
      {
        long l = System.currentTimeMillis();
        for (int k = 0; k < _iterations; k++) {
          ((AbstractTranslet)localObject).transform(localDOMEnhancedForDTM, localTransletOutputHandlerFactory.getSerializationHandler());
        }
        l = System.currentTimeMillis() - l;
        System.err.println("\n<!--");
        System.err.println("  transform  = " + l / _iterations + " ms");
        System.err.println("  throughput = " + 1000.0D / (l / _iterations) + " tps");
        System.err.println("-->");
      }
    }
    catch (TransletException localTransletException)
    {
      if (_debug) {
        localTransletException.printStackTrace();
      }
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + localTransletException.getMessage());
    }
    catch (RuntimeException localRuntimeException)
    {
      if (_debug) {
        localRuntimeException.printStackTrace();
      }
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + localRuntimeException.getMessage());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      if (_debug) {
        localFileNotFoundException.printStackTrace();
      }
      localObject = new ErrorMsg("FILE_NOT_FOUND_ERR", _fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + ((ErrorMsg)localObject).toString());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      if (_debug) {
        localMalformedURLException.printStackTrace();
      }
      localObject = new ErrorMsg("INVALID_URI_ERR", _fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + ((ErrorMsg)localObject).toString());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (_debug) {
        localClassNotFoundException.printStackTrace();
      }
      localObject = new ErrorMsg("CLASS_NOT_FOUND_ERR", _className);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + ((ErrorMsg)localObject).toString());
    }
    catch (UnknownHostException localUnknownHostException)
    {
      if (_debug) {
        localUnknownHostException.printStackTrace();
      }
      localObject = new ErrorMsg("INVALID_URI_ERR", _fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + ((ErrorMsg)localObject).toString());
    }
    catch (SAXException localSAXException)
    {
      Object localObject = localSAXException.getException();
      if (_debug)
      {
        if (localObject != null) {
          ((Exception)localObject).printStackTrace();
        }
        localSAXException.printStackTrace();
      }
      System.err.print(new ErrorMsg("RUNTIME_ERROR_KEY"));
      if (localObject != null) {
        System.err.println(((Exception)localObject).getMessage());
      } else {
        System.err.println(localSAXException.getMessage());
      }
    }
    catch (Exception localException1)
    {
      if (_debug) {
        localException1.printStackTrace();
      }
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + localException1.getMessage());
    }
  }
  
  public static void printUsage()
  {
    System.err.println(new ErrorMsg("TRANSFORM_USAGE_STR"));
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      if (paramArrayOfString.length > 0)
      {
        int j = -1;
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        String str1 = null;
        for (int i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].charAt(0) == '-'); i++) {
          if (paramArrayOfString[i].equals("-u"))
          {
            bool1 = true;
          }
          else if (paramArrayOfString[i].equals("-x"))
          {
            bool2 = true;
          }
          else if (paramArrayOfString[i].equals("-j"))
          {
            bool3 = true;
            str1 = paramArrayOfString[(++i)];
          }
          else if (paramArrayOfString[i].equals("-n"))
          {
            try
            {
              j = Integer.parseInt(paramArrayOfString[(++i)]);
            }
            catch (NumberFormatException localNumberFormatException) {}
          }
          else
          {
            printUsage();
          }
        }
        if (paramArrayOfString.length - i < 2) {
          printUsage();
        }
        Transform localTransform = new Transform(paramArrayOfString[(i + 1)], paramArrayOfString[i], bool1, bool2, j);
        localTransform.setJarFileInputSrc(bool3, str1);
        Vector localVector = new Vector();
        i += 2;
        while (i < paramArrayOfString.length)
        {
          int k = paramArrayOfString[i].indexOf('=');
          if (k > 0)
          {
            String str2 = paramArrayOfString[i].substring(0, k);
            String str3 = paramArrayOfString[i].substring(k + 1);
            localVector.addElement(new Parameter(str2, str3));
          }
          else
          {
            printUsage();
          }
          i++;
        }
        if (i == paramArrayOfString.length)
        {
          localTransform.setParameters(localVector);
          localTransform.doTransform();
        }
      }
      else
      {
        printUsage();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\cmdline\Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */