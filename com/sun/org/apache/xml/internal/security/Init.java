package com.sun.org.apache.xml.internal.security;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper.Algorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Init
{
  public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
  private static Logger log = Logger.getLogger(Init.class.getName());
  private static boolean alreadyInitialized = false;
  
  public Init() {}
  
  public static final synchronized boolean isInitialized()
  {
    return alreadyInitialized;
  }
  
  public static synchronized void init()
  {
    if (alreadyInitialized) {
      return;
    }
    InputStream localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InputStream run()
      {
        String str = System.getProperty("com.sun.org.apache.xml.internal.security.resource.config");
        if (str == null) {
          return null;
        }
        return getClass().getResourceAsStream(str);
      }
    });
    if (localInputStream == null) {
      dynamicInit();
    } else {
      fileInit(localInputStream);
    }
    alreadyInitialized = true;
  }
  
  private static void dynamicInit()
  {
    I18n.init("en", "US");
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Registering default algorithms");
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws XMLSecurityException
        {
          ElementProxy.registerDefaultPrefixes();
          Transform.registerDefaultAlgorithms();
          SignatureAlgorithm.registerDefaultAlgorithms();
          JCEMapper.registerDefaultAlgorithms();
          Canonicalizer.registerDefaultAlgorithms();
          ResourceResolver.registerDefaultResolvers();
          KeyResolver.registerDefaultResolvers();
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      XMLSecurityException localXMLSecurityException = (XMLSecurityException)localPrivilegedActionException.getException();
      log.log(Level.SEVERE, localXMLSecurityException.getMessage(), localXMLSecurityException);
      localXMLSecurityException.printStackTrace();
    }
  }
  
  private static void fileInit(InputStream paramInputStream)
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setValidating(false);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.parse(paramInputStream);
      for (Node localNode1 = localDocument.getFirstChild(); (localNode1 != null) && (!"Configuration".equals(localNode1.getLocalName())); localNode1 = localNode1.getNextSibling()) {}
      if (localNode1 == null)
      {
        log.log(Level.SEVERE, "Error in reading configuration file - Configuration element not found");
        return;
      }
      for (Node localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling()) {
        if (1 == localNode2.getNodeType())
        {
          String str1 = localNode2.getLocalName();
          Object localObject1;
          Object localObject2;
          Object localObject3;
          if (str1.equals("ResourceBundles"))
          {
            localObject1 = (Element)localNode2;
            Attr localAttr = ((Element)localObject1).getAttributeNode("defaultLanguageCode");
            localObject2 = ((Element)localObject1).getAttributeNode("defaultCountryCode");
            localObject3 = localAttr == null ? null : localAttr.getNodeValue();
            String str4 = localObject2 == null ? null : ((Attr)localObject2).getNodeValue();
            I18n.init((String)localObject3, str4);
          }
          int i;
          Object[] arrayOfObject;
          if (str1.equals("CanonicalizationMethods"))
          {
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod");
            for (i = 0; i < localObject1.length; i++)
            {
              localObject2 = localObject1[i].getAttributeNS(null, "URI");
              localObject3 = localObject1[i].getAttributeNS(null, "JAVACLASS");
              try
              {
                Canonicalizer.register((String)localObject2, (String)localObject3);
                if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Canonicalizer.register(" + (String)localObject2 + ", " + (String)localObject3 + ")");
                }
              }
              catch (ClassNotFoundException localClassNotFoundException1)
              {
                arrayOfObject = new Object[] { localObject2, localObject3 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              }
            }
          }
          if (str1.equals("TransformAlgorithms"))
          {
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm");
            for (i = 0; i < localObject1.length; i++)
            {
              localObject2 = localObject1[i].getAttributeNS(null, "URI");
              localObject3 = localObject1[i].getAttributeNS(null, "JAVACLASS");
              try
              {
                Transform.register((String)localObject2, (String)localObject3);
                if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Transform.register(" + (String)localObject2 + ", " + (String)localObject3 + ")");
                }
              }
              catch (ClassNotFoundException localClassNotFoundException2)
              {
                arrayOfObject = new Object[] { localObject2, localObject3 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              }
              catch (NoClassDefFoundError localNoClassDefFoundError)
              {
                log.log(Level.WARNING, "Not able to found dependencies for algorithm, I'll keep working.");
              }
            }
          }
          if ("JCEAlgorithmMappings".equals(str1))
          {
            localObject1 = ((Element)localNode2).getElementsByTagName("Algorithms").item(0);
            if (localObject1 != null)
            {
              Element[] arrayOfElement = XMLUtils.selectNodes(((Node)localObject1).getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm");
              for (int m = 0; m < arrayOfElement.length; m++)
              {
                localObject3 = arrayOfElement[m];
                String str5 = ((Element)localObject3).getAttribute("URI");
                JCEMapper.register(str5, new JCEMapper.Algorithm((Element)localObject3));
              }
            }
          }
          int j;
          String str2;
          if (str1.equals("SignatureAlgorithms"))
          {
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm");
            for (j = 0; j < localObject1.length; j++)
            {
              str2 = localObject1[j].getAttributeNS(null, "URI");
              localObject3 = localObject1[j].getAttributeNS(null, "JAVACLASS");
              try
              {
                SignatureAlgorithm.register(str2, (String)localObject3);
                if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "SignatureAlgorithm.register(" + str2 + ", " + (String)localObject3 + ")");
                }
              }
              catch (ClassNotFoundException localClassNotFoundException3)
              {
                arrayOfObject = new Object[] { str2, localObject3 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              }
            }
          }
          if (str1.equals("ResourceResolvers"))
          {
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
            for (j = 0; j < localObject1.length; j++)
            {
              str2 = localObject1[j].getAttributeNS(null, "JAVACLASS");
              localObject3 = localObject1[j].getAttributeNS(null, "DESCRIPTION");
              if ((localObject3 != null) && (((String)localObject3).length() > 0))
              {
                if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Register Resolver: " + str2 + ": " + (String)localObject3);
                }
              }
              else if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Register Resolver: " + str2 + ": For unknown purposes");
              }
              try
              {
                ResourceResolver.register(str2);
              }
              catch (Throwable localThrowable)
              {
                log.log(Level.WARNING, "Cannot register:" + str2 + " perhaps some needed jars are not installed", localThrowable);
              }
            }
          }
          if (str1.equals("KeyResolver"))
          {
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
            ArrayList localArrayList = new ArrayList(localObject1.length);
            for (int n = 0; n < localObject1.length; n++)
            {
              localObject3 = localObject1[n].getAttributeNS(null, "JAVACLASS");
              String str6 = localObject1[n].getAttributeNS(null, "DESCRIPTION");
              if ((str6 != null) && (str6.length() > 0))
              {
                if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Register Resolver: " + (String)localObject3 + ": " + str6);
                }
              }
              else if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Register Resolver: " + (String)localObject3 + ": For unknown purposes");
              }
              localArrayList.add(localObject3);
            }
            KeyResolver.registerClassNames(localArrayList);
          }
          if (str1.equals("PrefixMappings"))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "Now I try to bind prefixes:");
            }
            localObject1 = XMLUtils.selectNodes(localNode2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping");
            for (int k = 0; k < localObject1.length; k++)
            {
              String str3 = localObject1[k].getAttributeNS(null, "namespace");
              localObject3 = localObject1[k].getAttributeNS(null, "prefix");
              if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Now I try to bind " + (String)localObject3 + " to " + str3);
              }
              ElementProxy.setDefaultPrefix(str3, (String)localObject3);
            }
          }
        }
      }
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "Bad: ", localException);
      localException.printStackTrace();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\Init.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */