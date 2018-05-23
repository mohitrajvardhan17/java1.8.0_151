package com.sun.org.apache.xalan.internal.xslt;

import com.sun.org.apache.xalan.internal.Version;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ListResourceBundle;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Process
{
  public Process() {}
  
  protected static void printArgOptions(ResourceBundle paramResourceBundle)
  {
    System.out.println(paramResourceBundle.getString("xslProc_option"));
    System.out.println("\n\t\t\t" + paramResourceBundle.getString("xslProc_common_options") + "\n");
    System.out.println(paramResourceBundle.getString("optionXSLTC"));
    System.out.println(paramResourceBundle.getString("optionIN"));
    System.out.println(paramResourceBundle.getString("optionXSL"));
    System.out.println(paramResourceBundle.getString("optionOUT"));
    System.out.println(paramResourceBundle.getString("optionV"));
    System.out.println(paramResourceBundle.getString("optionEDUMP"));
    System.out.println(paramResourceBundle.getString("optionXML"));
    System.out.println(paramResourceBundle.getString("optionTEXT"));
    System.out.println(paramResourceBundle.getString("optionHTML"));
    System.out.println(paramResourceBundle.getString("optionPARAM"));
    System.out.println(paramResourceBundle.getString("optionMEDIA"));
    System.out.println(paramResourceBundle.getString("optionFLAVOR"));
    System.out.println(paramResourceBundle.getString("optionDIAG"));
    System.out.println(paramResourceBundle.getString("optionURIRESOLVER"));
    System.out.println(paramResourceBundle.getString("optionENTITYRESOLVER"));
    waitForReturnKey(paramResourceBundle);
    System.out.println(paramResourceBundle.getString("optionCONTENTHANDLER"));
    System.out.println(paramResourceBundle.getString("optionSECUREPROCESSING"));
    System.out.println("\n\t\t\t" + paramResourceBundle.getString("xslProc_xsltc_options") + "\n");
    System.out.println(paramResourceBundle.getString("optionXO"));
    waitForReturnKey(paramResourceBundle);
    System.out.println(paramResourceBundle.getString("optionXD"));
    System.out.println(paramResourceBundle.getString("optionXJ"));
    System.out.println(paramResourceBundle.getString("optionXP"));
    System.out.println(paramResourceBundle.getString("optionXN"));
    System.out.println(paramResourceBundle.getString("optionXX"));
    System.out.println(paramResourceBundle.getString("optionXT"));
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    String str1 = null;
    int m = 0;
    PrintWriter localPrintWriter1 = new PrintWriter(System.err, true);
    PrintWriter localPrintWriter2 = localPrintWriter1;
    ListResourceBundle localListResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
    String str2 = "s2s";
    if (paramArrayOfString.length < 1)
    {
      printArgOptions(localListResourceBundle);
    }
    else
    {
      int n = 1;
      for (int i1 = 0; i1 < paramArrayOfString.length; i1++) {
        if ("-XSLTC".equalsIgnoreCase(paramArrayOfString[i1])) {
          n = 1;
        }
      }
      if (n != 0)
      {
        String str3 = "javax.xml.transform.TransformerFactory";
        String str4 = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
        localObject1 = System.getProperties();
        ((Properties)localObject1).put(str3, str4);
        System.setProperties((Properties)localObject1);
      }
      TransformerFactory localTransformerFactory;
      try
      {
        localTransformerFactory = TransformerFactory.newInstance();
        localTransformerFactory.setErrorListener(new DefaultErrorHandler());
      }
      catch (TransformerFactoryConfigurationError localTransformerFactoryConfigurationError)
      {
        localTransformerFactoryConfigurationError.printStackTrace(localPrintWriter2);
        str1 = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
        localPrintWriter1.println(str1);
        localTransformerFactory = null;
        doExit(str1);
      }
      int i2 = 0;
      int i3 = 0;
      Object localObject1 = null;
      String str5 = null;
      String str6 = null;
      String str7 = null;
      Object localObject2 = null;
      String str8 = null;
      String str9 = null;
      Vector localVector = new Vector();
      int i4 = 0;
      URIResolver localURIResolver = null;
      EntityResolver localEntityResolver = null;
      ContentHandler localContentHandler = null;
      int i5 = -1;
      Object localObject3;
      for (int i6 = 0; i6 < paramArrayOfString.length; i6++) {
        if (!"-XSLTC".equalsIgnoreCase(paramArrayOfString[i6])) {
          if ("-INDENT".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            int i7;
            if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
              i7 = Integer.parseInt(paramArrayOfString[(++i6)]);
            } else {
              i7 = 0;
            }
          }
          else if ("-IN".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
              localObject1 = paramArrayOfString[(++i6)];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-IN" }));
            }
          }
          else if ("-MEDIA".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if (i6 + 1 < paramArrayOfString.length) {
              str9 = paramArrayOfString[(++i6)];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-MEDIA" }));
            }
          }
          else if ("-OUT".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
              str5 = paramArrayOfString[(++i6)];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-OUT" }));
            }
          }
          else if ("-XSL".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
              str7 = paramArrayOfString[(++i6)];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XSL" }));
            }
          }
          else if ("-FLAVOR".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if (i6 + 1 < paramArrayOfString.length) {
              str2 = paramArrayOfString[(++i6)];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-FLAVOR" }));
            }
          }
          else if ("-PARAM".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if (i6 + 2 < paramArrayOfString.length)
            {
              String str10 = paramArrayOfString[(++i6)];
              localVector.addElement(str10);
              localObject3 = paramArrayOfString[(++i6)];
              localVector.addElement(localObject3);
            }
            else
            {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-PARAM" }));
            }
          }
          else if (!"-E".equalsIgnoreCase(paramArrayOfString[i6]))
          {
            if ("-V".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              localPrintWriter1.println(localListResourceBundle.getString("version") + Version.getVersion() + ", " + localListResourceBundle.getString("version2"));
            }
            else if ("-Q".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              j = 1;
            }
            else if ("-DIAG".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              k = 1;
            }
            else if ("-XML".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              str8 = "xml";
            }
            else if ("-TEXT".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              str8 = "text";
            }
            else if ("-HTML".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              str8 = "html";
            }
            else if ("-EDUMP".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              i = 1;
              if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                str6 = paramArrayOfString[(++i6)];
              }
            }
            else if ("-URIRESOLVER".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (i6 + 1 < paramArrayOfString.length)
              {
                try
                {
                  localURIResolver = (URIResolver)ObjectFactory.newInstance(paramArrayOfString[(++i6)], true);
                  localTransformerFactory.setURIResolver(localURIResolver);
                }
                catch (ConfigurationError localConfigurationError1)
                {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-URIResolver" });
                  System.err.println(str1);
                  doExit(str1);
                }
              }
              else
              {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-URIResolver" });
                System.err.println(str1);
                doExit(str1);
              }
            }
            else if ("-ENTITYRESOLVER".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (i6 + 1 < paramArrayOfString.length)
              {
                try
                {
                  localEntityResolver = (EntityResolver)ObjectFactory.newInstance(paramArrayOfString[(++i6)], true);
                }
                catch (ConfigurationError localConfigurationError2)
                {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-EntityResolver" });
                  System.err.println(str1);
                  doExit(str1);
                }
              }
              else
              {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-EntityResolver" });
                System.err.println(str1);
                doExit(str1);
              }
            }
            else if ("-CONTENTHANDLER".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (i6 + 1 < paramArrayOfString.length)
              {
                try
                {
                  localContentHandler = (ContentHandler)ObjectFactory.newInstance(paramArrayOfString[(++i6)], true);
                }
                catch (ConfigurationError localConfigurationError3)
                {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-ContentHandler" });
                  System.err.println(str1);
                  doExit(str1);
                }
              }
              else
              {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-ContentHandler" });
                System.err.println(str1);
                doExit(str1);
              }
            }
            else if ("-XO".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0)
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-'))
                {
                  localTransformerFactory.setAttribute("generate-translet", "true");
                  localTransformerFactory.setAttribute("translet-name", paramArrayOfString[(++i6)]);
                }
                else
                {
                  localTransformerFactory.setAttribute("generate-translet", "true");
                }
              }
              else
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  i6++;
                }
                printInvalidXalanOption("-XO");
              }
            }
            else if ("-XD".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0)
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  localTransformerFactory.setAttribute("destination-directory", paramArrayOfString[(++i6)]);
                } else {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XD" }));
                }
              }
              else
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  i6++;
                }
                printInvalidXalanOption("-XD");
              }
            }
            else if ("-XJ".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0)
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-'))
                {
                  localTransformerFactory.setAttribute("generate-translet", "true");
                  localTransformerFactory.setAttribute("jar-name", paramArrayOfString[(++i6)]);
                }
                else
                {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XJ" }));
                }
              }
              else
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  i6++;
                }
                printInvalidXalanOption("-XJ");
              }
            }
            else if ("-XP".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0)
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  localTransformerFactory.setAttribute("package-name", paramArrayOfString[(++i6)]);
                } else {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XP" }));
                }
              }
              else
              {
                if ((i6 + 1 < paramArrayOfString.length) && (paramArrayOfString[(i6 + 1)].charAt(0) != '-')) {
                  i6++;
                }
                printInvalidXalanOption("-XP");
              }
            }
            else if ("-XN".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0) {
                localTransformerFactory.setAttribute("enable-inlining", "true");
              } else {
                printInvalidXalanOption("-XN");
              }
            }
            else if ("-XX".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0) {
                localTransformerFactory.setAttribute("debug", "true");
              } else {
                printInvalidXalanOption("-XX");
              }
            }
            else if ("-XT".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              if (n != 0) {
                localTransformerFactory.setAttribute("auto-translet", "true");
              } else {
                printInvalidXalanOption("-XT");
              }
            }
            else if ("-SECURE".equalsIgnoreCase(paramArrayOfString[i6]))
            {
              m = 1;
              try
              {
                localTransformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
              }
              catch (TransformerConfigurationException localTransformerConfigurationException) {}
            }
            else
            {
              System.err.println(XSLMessages.createMessage("ER_INVALID_OPTION", new Object[] { paramArrayOfString[i6] }));
            }
          }
        }
      }
      if ((localObject1 == null) && (str7 == null))
      {
        str1 = localListResourceBundle.getString("xslProc_no_input");
        System.err.println(str1);
        doExit(str1);
      }
      try
      {
        long l1 = System.currentTimeMillis();
        if (null != str6) {
          localPrintWriter2 = new PrintWriter(new FileWriter(str6));
        }
        localObject3 = null;
        Object localObject4;
        if (null != str7) {
          if (str2.equals("d2d"))
          {
            DocumentBuilderFactory localDocumentBuilderFactory1 = DocumentBuilderFactory.newInstance();
            localDocumentBuilderFactory1.setNamespaceAware(true);
            if (m != 0) {
              try
              {
                localDocumentBuilderFactory1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
              }
              catch (ParserConfigurationException localParserConfigurationException1) {}
            }
            localObject4 = localDocumentBuilderFactory1.newDocumentBuilder();
            localObject5 = ((DocumentBuilder)localObject4).parse(new InputSource(str7));
            localObject3 = localTransformerFactory.newTemplates(new DOMSource((Node)localObject5, str7));
          }
          else
          {
            localObject3 = localTransformerFactory.newTemplates(new StreamSource(str7));
          }
        }
        if (null != str5)
        {
          localObject4 = new StreamResult(new FileOutputStream(str5));
          ((StreamResult)localObject4).setSystemId(str5);
        }
        else
        {
          localObject4 = new StreamResult(System.out);
        }
        Object localObject5 = (SAXTransformerFactory)localTransformerFactory;
        Object localObject6;
        if (null == localObject3)
        {
          localObject6 = ((SAXTransformerFactory)localObject5).getAssociatedStylesheet(new StreamSource((String)localObject1), str9, null, null);
          if (null != localObject6)
          {
            localObject3 = localTransformerFactory.newTemplates((Source)localObject6);
          }
          else
          {
            if (null != str9) {
              throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_IN_MEDIA", new Object[] { localObject1, str9 }));
            }
            throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_PI", new Object[] { localObject1 }));
          }
        }
        Object localObject10;
        if (null != localObject3)
        {
          localObject6 = str2.equals("th") ? null : ((Templates)localObject3).newTransformer();
          ((Transformer)localObject6).setErrorListener(new DefaultErrorHandler());
          if (null != str8) {
            ((Transformer)localObject6).setOutputProperty("method", str8);
          }
          int i8 = localVector.size();
          for (int i9 = 0; i9 < i8; i9 += 2) {
            ((Transformer)localObject6).setParameter((String)localVector.elementAt(i9), (String)localVector.elementAt(i9 + 1));
          }
          if (localURIResolver != null) {
            ((Transformer)localObject6).setURIResolver(localURIResolver);
          }
          Object localObject7;
          if (null != localObject1)
          {
            Object localObject8;
            Object localObject9;
            if (str2.equals("d2d"))
            {
              DocumentBuilderFactory localDocumentBuilderFactory2 = DocumentBuilderFactory.newInstance();
              localDocumentBuilderFactory2.setCoalescing(true);
              localDocumentBuilderFactory2.setNamespaceAware(true);
              if (m != 0) {
                try
                {
                  localDocumentBuilderFactory2.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                }
                catch (ParserConfigurationException localParserConfigurationException2) {}
              }
              localObject8 = localDocumentBuilderFactory2.newDocumentBuilder();
              if (localEntityResolver != null) {
                ((DocumentBuilder)localObject8).setEntityResolver(localEntityResolver);
              }
              localObject9 = ((DocumentBuilder)localObject8).parse(new InputSource((String)localObject1));
              Document localDocument = ((DocumentBuilder)localObject8).newDocument();
              DocumentFragment localDocumentFragment = localDocument.createDocumentFragment();
              ((Transformer)localObject6).transform(new DOMSource((Node)localObject9, (String)localObject1), new DOMResult(localDocumentFragment));
              Transformer localTransformer = ((SAXTransformerFactory)localObject5).newTransformer();
              localTransformer.setErrorListener(new DefaultErrorHandler());
              Properties localProperties = ((Templates)localObject3).getOutputProperties();
              localTransformer.setOutputProperties(localProperties);
              if (localContentHandler != null)
              {
                SAXResult localSAXResult2 = new SAXResult(localContentHandler);
                localTransformer.transform(new DOMSource(localDocumentFragment), localSAXResult2);
              }
              else
              {
                localTransformer.transform(new DOMSource(localDocumentFragment), (Result)localObject4);
              }
            }
            else if (str2.equals("th"))
            {
              for (int i10 = 0; i10 < 1; i10++)
              {
                localObject8 = null;
                try
                {
                  localObject9 = SAXParserFactory.newInstance();
                  ((SAXParserFactory)localObject9).setNamespaceAware(true);
                  if (m != 0) {
                    try
                    {
                      ((SAXParserFactory)localObject9).setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                    }
                    catch (SAXException localSAXException2) {}
                  }
                  SAXParser localSAXParser = ((SAXParserFactory)localObject9).newSAXParser();
                  localObject8 = localSAXParser.getXMLReader();
                }
                catch (ParserConfigurationException localParserConfigurationException4)
                {
                  throw new SAXException(localParserConfigurationException4);
                }
                catch (FactoryConfigurationError localFactoryConfigurationError2)
                {
                  throw new SAXException(localFactoryConfigurationError2.toString());
                }
                catch (NoSuchMethodError localNoSuchMethodError2) {}catch (AbstractMethodError localAbstractMethodError2) {}
                if (null == localObject8) {
                  localObject8 = XMLReaderFactory.createXMLReader();
                }
                TransformerHandler localTransformerHandler = ((SAXTransformerFactory)localObject5).newTransformerHandler((Templates)localObject3);
                ((XMLReader)localObject8).setContentHandler(localTransformerHandler);
                ((XMLReader)localObject8).setDTDHandler(localTransformerHandler);
                if ((localTransformerHandler instanceof ErrorHandler)) {
                  ((XMLReader)localObject8).setErrorHandler((ErrorHandler)localTransformerHandler);
                }
                try
                {
                  ((XMLReader)localObject8).setProperty("http://xml.org/sax/properties/lexical-handler", localTransformerHandler);
                }
                catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
                try
                {
                  ((XMLReader)localObject8).setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                }
                catch (SAXException localSAXException3) {}
                localTransformerHandler.setResult((Result)localObject4);
                ((XMLReader)localObject8).parse(new InputSource((String)localObject1));
              }
            }
            else if (localEntityResolver != null)
            {
              localObject7 = null;
              try
              {
                localObject8 = SAXParserFactory.newInstance();
                ((SAXParserFactory)localObject8).setNamespaceAware(true);
                if (m != 0) {
                  try
                  {
                    ((SAXParserFactory)localObject8).setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                  }
                  catch (SAXException localSAXException1) {}
                }
                localObject10 = ((SAXParserFactory)localObject8).newSAXParser();
                localObject7 = ((SAXParser)localObject10).getXMLReader();
              }
              catch (ParserConfigurationException localParserConfigurationException3)
              {
                throw new SAXException(localParserConfigurationException3);
              }
              catch (FactoryConfigurationError localFactoryConfigurationError1)
              {
                throw new SAXException(localFactoryConfigurationError1.toString());
              }
              catch (NoSuchMethodError localNoSuchMethodError1) {}catch (AbstractMethodError localAbstractMethodError1) {}
              if (null == localObject7) {
                localObject7 = XMLReaderFactory.createXMLReader();
              }
              ((XMLReader)localObject7).setEntityResolver(localEntityResolver);
              if (localContentHandler != null)
              {
                SAXResult localSAXResult1 = new SAXResult(localContentHandler);
                ((Transformer)localObject6).transform(new SAXSource((XMLReader)localObject7, new InputSource((String)localObject1)), localSAXResult1);
              }
              else
              {
                ((Transformer)localObject6).transform(new SAXSource((XMLReader)localObject7, new InputSource((String)localObject1)), (Result)localObject4);
              }
            }
            else if (localContentHandler != null)
            {
              localObject7 = new SAXResult(localContentHandler);
              ((Transformer)localObject6).transform(new StreamSource((String)localObject1), (Result)localObject7);
            }
            else
            {
              ((Transformer)localObject6).transform(new StreamSource((String)localObject1), (Result)localObject4);
            }
          }
          else
          {
            localObject7 = new StringReader("<?xml version=\"1.0\"?> <doc/>");
            ((Transformer)localObject6).transform(new StreamSource((Reader)localObject7), (Result)localObject4);
          }
        }
        else
        {
          str1 = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
          localPrintWriter1.println(str1);
          doExit(str1);
        }
        if ((null != str5) && (localObject4 != null))
        {
          localObject6 = ((StreamResult)localObject4).getOutputStream();
          Writer localWriter = ((StreamResult)localObject4).getWriter();
          try
          {
            if (localObject6 != null) {
              ((OutputStream)localObject6).close();
            }
            if (localWriter != null) {
              localWriter.close();
            }
          }
          catch (IOException localIOException) {}
        }
        long l2 = System.currentTimeMillis();
        long l3 = l2 - l1;
        if (k != 0)
        {
          localObject10 = new Object[] { localObject1, str7, new Long(l3) };
          str1 = XSLMessages.createMessage("diagTiming", (Object[])localObject10);
          localPrintWriter1.println('\n');
          localPrintWriter1.println(str1);
        }
      }
      catch (Throwable localThrowable)
      {
        Exception localException;
        while ((localThrowable instanceof WrappedRuntimeException)) {
          localException = ((WrappedRuntimeException)localThrowable).getException();
        }
        if (((localException instanceof NullPointerException)) || ((localException instanceof ClassCastException))) {
          i = 1;
        }
        localPrintWriter1.println();
        if (i != 0)
        {
          localException.printStackTrace(localPrintWriter2);
        }
        else
        {
          DefaultErrorHandler.printLocation(localPrintWriter1, localException);
          localPrintWriter1.println(XSLMessages.createMessage("ER_XSLT_ERROR", null) + " (" + localException.getClass().getName() + "): " + localException.getMessage());
        }
        if (null != str6) {
          localPrintWriter2.close();
        }
        doExit(localException.getMessage());
      }
      if (null != str6) {
        localPrintWriter2.close();
      }
      if (null == localPrintWriter1) {}
    }
  }
  
  static void doExit(String paramString)
  {
    throw new RuntimeException(paramString);
  }
  
  private static void waitForReturnKey(ResourceBundle paramResourceBundle)
  {
    System.out.println(paramResourceBundle.getString("xslProc_return_to_continue"));
    try
    {
      while (System.in.read() != 10) {}
    }
    catch (IOException localIOException) {}
  }
  
  private static void printInvalidXSLTCOption(String paramString)
  {
    System.err.println(XSLMessages.createMessage("xslProc_invalid_xsltc_option", new Object[] { paramString }));
  }
  
  private static void printInvalidXalanOption(String paramString)
  {
    System.err.println(XSLMessages.createMessage("xslProc_invalid_xalan_option", new Object[] { paramString }));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xslt\Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */