package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.Entity.ExternalEntity;
import com.sun.xml.internal.stream.Entity.InternalEntity;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class XMLEntityManager
  implements XMLComponent, XMLEntityResolver
{
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
  public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected boolean fStrictURI;
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String STAX_ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/stax-entity-resolver";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
  private static final Boolean[] FEATURE_DEFAULTS = { null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null, new Integer(8192), null, null };
  private static final String XMLEntity = "[xml]".intern();
  private static final String DTDEntity = "[dtd]".intern();
  private static final boolean DEBUG_BUFFER = false;
  protected boolean fWarnDuplicateEntityDef;
  private static final boolean DEBUG_ENTITIES = false;
  private static final boolean DEBUG_ENCODINGS = false;
  private static final boolean DEBUG_RESOLVER = false;
  protected boolean fValidation;
  protected boolean fExternalGeneralEntities;
  protected boolean fExternalParameterEntities;
  protected boolean fAllowJavaEncodings = true;
  protected boolean fLoadExternalDTD = true;
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityResolver fEntityResolver;
  protected StaxEntityResolverWrapper fStaxEntityResolver;
  protected PropertyManager fPropertyManager;
  boolean fSupportDTD = true;
  boolean fReplaceEntityReferences = true;
  boolean fSupportExternalEntities = true;
  protected String fAccessExternalDTD = "all";
  protected ValidationManager fValidationManager;
  protected int fBufferSize = 8192;
  protected XMLSecurityManager fSecurityManager = null;
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  protected int entityExpansionIndex;
  protected boolean fStandalone;
  protected boolean fInExternalSubset = false;
  protected XMLEntityHandler fEntityHandler;
  protected XMLEntityScanner fEntityScanner;
  protected XMLEntityScanner fXML10EntityScanner;
  protected XMLEntityScanner fXML11EntityScanner;
  protected int fEntityExpansionCount = 0;
  protected Map<String, Entity> fEntities = new HashMap();
  protected Stack<Entity> fEntityStack = new Stack();
  protected Entity.ScannedEntity fCurrentEntity = null;
  boolean fISCreatedByResolver = false;
  protected XMLEntityStorage fEntityStorage;
  protected final Object[] defaultEncoding = { "UTF-8", null };
  private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  private final Augmentations fEntityAugs = new AugmentationsImpl();
  private CharacterBufferPool fBufferPool = new CharacterBufferPool(fBufferSize, 1024);
  private static String gUserDir;
  private static com.sun.org.apache.xerces.internal.util.URI gUserDirURI;
  private static boolean[] gNeedEscaping = new boolean[''];
  private static char[] gAfterEscaping1 = new char[''];
  private static char[] gAfterEscaping2 = new char[''];
  private static char[] gHexChs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public XMLEntityManager()
  {
    fSecurityManager = new XMLSecurityManager(true);
    fEntityStorage = new XMLEntityStorage(this);
    setScannerVersion((short)1);
  }
  
  public XMLEntityManager(PropertyManager paramPropertyManager)
  {
    fPropertyManager = paramPropertyManager;
    fEntityStorage = new XMLEntityStorage(this);
    fEntityScanner = new XMLEntityScanner(paramPropertyManager, this);
    reset(paramPropertyManager);
  }
  
  public void addInternalEntity(String paramString1, String paramString2)
  {
    if (!fEntities.containsKey(paramString1))
    {
      Entity.InternalEntity localInternalEntity = new Entity.InternalEntity(paramString1, paramString2, fInExternalSubset);
      fEntities.put(paramString1, localInternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public void addExternalEntity(String paramString1, String paramString2, String paramString3, String paramString4)
    throws IOException
  {
    if (!fEntities.containsKey(paramString1))
    {
      if (paramString4 == null)
      {
        int i = fEntityStack.size();
        if ((i == 0) && (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null)) {
          paramString4 = fCurrentEntity.entityLocation.getExpandedSystemId();
        }
        for (int j = i - 1; j >= 0; j--)
        {
          Entity.ScannedEntity localScannedEntity = (Entity.ScannedEntity)fEntityStack.elementAt(j);
          if ((entityLocation != null) && (entityLocation.getExpandedSystemId() != null))
          {
            paramString4 = entityLocation.getExpandedSystemId();
            break;
          }
        }
      }
      Entity.ExternalEntity localExternalEntity = new Entity.ExternalEntity(paramString1, new XMLEntityDescriptionImpl(paramString1, paramString2, paramString3, paramString4, expandSystemId(paramString3, paramString4, false)), null, fInExternalSubset);
      fEntities.put(paramString1, localExternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public void addUnparsedEntity(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (!fEntities.containsKey(paramString1))
    {
      Entity.ExternalEntity localExternalEntity = new Entity.ExternalEntity(paramString1, new XMLEntityDescriptionImpl(paramString1, paramString2, paramString3, paramString4, null), paramString5, fInExternalSubset);
      fEntities.put(paramString1, localExternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public XMLEntityStorage getEntityStore()
  {
    return fEntityStorage;
  }
  
  public XMLEntityScanner getEntityScanner()
  {
    if (fEntityScanner == null)
    {
      if (fXML10EntityScanner == null) {
        fXML10EntityScanner = new XMLEntityScanner();
      }
      fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
      fEntityScanner = fXML10EntityScanner;
    }
    return fEntityScanner;
  }
  
  public void setScannerVersion(short paramShort)
  {
    if (paramShort == 1)
    {
      if (fXML10EntityScanner == null) {
        fXML10EntityScanner = new XMLEntityScanner();
      }
      fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
      fEntityScanner = fXML10EntityScanner;
      fEntityScanner.setCurrentEntity(fCurrentEntity);
    }
    else
    {
      if (fXML11EntityScanner == null) {
        fXML11EntityScanner = new XML11EntityScanner();
      }
      fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
      fEntityScanner = fXML11EntityScanner;
      fEntityScanner.setCurrentEntity(fCurrentEntity);
    }
  }
  
  public String setupCurrentEntity(boolean paramBoolean1, String paramString, XMLInputSource paramXMLInputSource, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException, XNIException
  {
    String str1 = paramXMLInputSource.getPublicId();
    Object localObject1 = paramXMLInputSource.getSystemId();
    Object localObject2 = paramXMLInputSource.getBaseSystemId();
    String str2 = paramXMLInputSource.getEncoding();
    boolean bool1 = str2 != null;
    Boolean localBoolean = null;
    Object localObject3 = null;
    Reader localReader = paramXMLInputSource.getCharacterStream();
    Object localObject4 = expandSystemId((String)localObject1, (String)localObject2, fStrictURI);
    if (localObject2 == null) {
      localObject2 = localObject4;
    }
    if (localReader == null)
    {
      localObject3 = paramXMLInputSource.getByteStream();
      Object localObject5;
      if (localObject3 == null)
      {
        localObject5 = new URL((String)localObject4);
        URLConnection localURLConnection = ((URL)localObject5).openConnection();
        if (!(localURLConnection instanceof HttpURLConnection))
        {
          localObject3 = localURLConnection.getInputStream();
        }
        else
        {
          boolean bool2 = true;
          Object localObject7;
          if ((paramXMLInputSource instanceof HTTPInputSource))
          {
            localObject7 = (HttpURLConnection)localURLConnection;
            HTTPInputSource localHTTPInputSource = (HTTPInputSource)paramXMLInputSource;
            Iterator localIterator = localHTTPInputSource.getHTTPRequestProperties();
            while (localIterator.hasNext())
            {
              Map.Entry localEntry = (Map.Entry)localIterator.next();
              ((HttpURLConnection)localObject7).setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
            }
            bool2 = localHTTPInputSource.getFollowHTTPRedirects();
            if (!bool2) {
              setInstanceFollowRedirects((HttpURLConnection)localObject7, bool2);
            }
          }
          localObject3 = localURLConnection.getInputStream();
          if (bool2)
          {
            localObject7 = localURLConnection.getURL().toString();
            if (!((String)localObject7).equals(localObject4))
            {
              localObject1 = localObject7;
              localObject4 = localObject7;
            }
          }
        }
      }
      localObject3 = new RewindableInputStream((InputStream)localObject3);
      int i;
      Object localObject6;
      int j;
      int k;
      int m;
      if (str2 == null)
      {
        localObject5 = new byte[4];
        for (i = 0; i < 4; i++) {
          localObject5[i] = ((byte)((InputStream)localObject3).read());
        }
        if (i == 4)
        {
          localObject6 = getEncodingName((byte[])localObject5, i);
          str2 = (String)localObject6[0];
          localBoolean = (Boolean)localObject6[1];
          ((InputStream)localObject3).reset();
          if ((i > 2) && (str2.equals("UTF-8")))
          {
            j = localObject5[0] & 0xFF;
            k = localObject5[1] & 0xFF;
            m = localObject5[2] & 0xFF;
            if ((j == 239) && (k == 187) && (m == 191)) {
              ((InputStream)localObject3).skip(3L);
            }
          }
          localReader = createReader((InputStream)localObject3, str2, localBoolean);
        }
        else
        {
          localReader = createReader((InputStream)localObject3, str2, localBoolean);
        }
      }
      else
      {
        str2 = str2.toUpperCase(Locale.ENGLISH);
        if (str2.equals("UTF-8"))
        {
          localObject5 = new int[3];
          for (i = 0; i < 3; i++)
          {
            localObject5[i] = ((InputStream)localObject3).read();
            if (localObject5[i] == -1) {
              break;
            }
          }
          if (i == 3)
          {
            if ((localObject5[0] != 239) || (localObject5[1] != 187) || (localObject5[2] != 191)) {
              ((InputStream)localObject3).reset();
            }
          }
          else {
            ((InputStream)localObject3).reset();
          }
        }
        else if (str2.equals("UTF-16"))
        {
          localObject5 = new int[4];
          for (i = 0; i < 4; i++)
          {
            localObject5[i] = ((InputStream)localObject3).read();
            if (localObject5[i] == -1) {
              break;
            }
          }
          ((InputStream)localObject3).reset();
          localObject6 = "UTF-16";
          if (i >= 2)
          {
            j = localObject5[0];
            k = localObject5[1];
            if ((j == 254) && (k == 255))
            {
              localObject6 = "UTF-16BE";
              localBoolean = Boolean.TRUE;
            }
            else if ((j == 255) && (k == 254))
            {
              localObject6 = "UTF-16LE";
              localBoolean = Boolean.FALSE;
            }
            else if (i == 4)
            {
              m = localObject5[2];
              int n = localObject5[3];
              if ((j == 0) && (k == 60) && (m == 0) && (n == 63))
              {
                localObject6 = "UTF-16BE";
                localBoolean = Boolean.TRUE;
              }
              if ((j == 60) && (k == 0) && (m == 63) && (n == 0))
              {
                localObject6 = "UTF-16LE";
                localBoolean = Boolean.FALSE;
              }
            }
          }
          localReader = createReader((InputStream)localObject3, (String)localObject6, localBoolean);
        }
        else if (str2.equals("ISO-10646-UCS-4"))
        {
          localObject5 = new int[4];
          for (i = 0; i < 4; i++)
          {
            localObject5[i] = ((InputStream)localObject3).read();
            if (localObject5[i] == -1) {
              break;
            }
          }
          ((InputStream)localObject3).reset();
          if (i == 4) {
            if ((localObject5[0] == 0) && (localObject5[1] == 0) && (localObject5[2] == 0) && (localObject5[3] == 60)) {
              localBoolean = Boolean.TRUE;
            } else if ((localObject5[0] == 60) && (localObject5[1] == 0) && (localObject5[2] == 0) && (localObject5[3] == 0)) {
              localBoolean = Boolean.FALSE;
            }
          }
        }
        else if (str2.equals("ISO-10646-UCS-2"))
        {
          localObject5 = new int[4];
          for (i = 0; i < 4; i++)
          {
            localObject5[i] = ((InputStream)localObject3).read();
            if (localObject5[i] == -1) {
              break;
            }
          }
          ((InputStream)localObject3).reset();
          if (i == 4) {
            if ((localObject5[0] == 0) && (localObject5[1] == 60) && (localObject5[2] == 0) && (localObject5[3] == 63)) {
              localBoolean = Boolean.TRUE;
            } else if ((localObject5[0] == 60) && (localObject5[1] == 0) && (localObject5[2] == 63) && (localObject5[3] == 0)) {
              localBoolean = Boolean.FALSE;
            }
          }
        }
        localReader = createReader((InputStream)localObject3, str2, localBoolean);
      }
    }
    if (fCurrentEntity != null) {
      fEntityStack.push(fCurrentEntity);
    }
    fCurrentEntity = new Entity.ScannedEntity(paramBoolean1, paramString, new XMLResourceIdentifierImpl(str1, (String)localObject1, (String)localObject2, (String)localObject4), (InputStream)localObject3, localReader, str2, paramBoolean2, bool1, paramBoolean3);
    fCurrentEntity.setEncodingExternallySpecified(bool1);
    fEntityScanner.setCurrentEntity(fCurrentEntity);
    fResourceIdentifier.setValues(str1, (String)localObject1, (String)localObject2, (String)localObject4);
    if (fLimitAnalyzer != null) {
      fLimitAnalyzer.startEntity(paramString);
    }
    return str2;
  }
  
  public boolean isExternalEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isExternal();
  }
  
  public boolean isEntityDeclInExternalSubset(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isEntityDeclInExternalSubset();
  }
  
  public void setStandalone(boolean paramBoolean)
  {
    fStandalone = paramBoolean;
  }
  
  public boolean isStandalone()
  {
    return fStandalone;
  }
  
  public boolean isDeclaredEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    return localEntity != null;
  }
  
  public boolean isUnparsedEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isUnparsed();
  }
  
  public XMLResourceIdentifier getCurrentResourceIdentifier()
  {
    return fResourceIdentifier;
  }
  
  public void setEntityHandler(XMLEntityHandler paramXMLEntityHandler)
  {
    fEntityHandler = paramXMLEntityHandler;
  }
  
  public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws IOException
  {
    if (paramXMLResourceIdentifier == null) {
      return null;
    }
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    String str3 = paramXMLResourceIdentifier.getBaseSystemId();
    String str4 = paramXMLResourceIdentifier.getExpandedSystemId();
    int i = str4 == null ? 1 : 0;
    if ((str3 == null) && (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null))
    {
      str3 = fCurrentEntity.entityLocation.getExpandedSystemId();
      if (str3 != null) {
        i = 1;
      }
    }
    if (i != 0) {
      str4 = expandSystemId(str2, str3, false);
    }
    StaxXMLInputSource localStaxXMLInputSource = null;
    XMLInputSource localXMLInputSource = null;
    XMLResourceIdentifierImpl localXMLResourceIdentifierImpl = null;
    if ((paramXMLResourceIdentifier instanceof XMLResourceIdentifierImpl))
    {
      localXMLResourceIdentifierImpl = (XMLResourceIdentifierImpl)paramXMLResourceIdentifier;
    }
    else
    {
      fResourceIdentifier.clear();
      localXMLResourceIdentifierImpl = fResourceIdentifier;
    }
    localXMLResourceIdentifierImpl.setValues(str1, str2, str3, str4);
    fISCreatedByResolver = false;
    if (fStaxEntityResolver != null)
    {
      localStaxXMLInputSource = fStaxEntityResolver.resolveEntity(localXMLResourceIdentifierImpl);
      if (localStaxXMLInputSource != null) {
        fISCreatedByResolver = true;
      }
    }
    if (fEntityResolver != null)
    {
      localXMLInputSource = fEntityResolver.resolveEntity(localXMLResourceIdentifierImpl);
      if (localXMLInputSource != null) {
        fISCreatedByResolver = true;
      }
    }
    if (localXMLInputSource != null) {
      localStaxXMLInputSource = new StaxXMLInputSource(localXMLInputSource, fISCreatedByResolver);
    }
    if (localStaxXMLInputSource == null) {
      localStaxXMLInputSource = new StaxXMLInputSource(new XMLInputSource(str1, str2, str3));
    } else if (!localStaxXMLInputSource.hasXMLStreamOrXMLEventReader()) {}
    return localStaxXMLInputSource;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws IOException, XNIException
  {
    if (paramXMLResourceIdentifier == null) {
      return null;
    }
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    String str3 = paramXMLResourceIdentifier.getBaseSystemId();
    String str4 = paramXMLResourceIdentifier.getExpandedSystemId();
    int i = str4 == null ? 1 : 0;
    if ((str3 == null) && (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null))
    {
      str3 = fCurrentEntity.entityLocation.getExpandedSystemId();
      if (str3 != null) {
        i = 1;
      }
    }
    if (i != 0) {
      str4 = expandSystemId(str2, str3, false);
    }
    XMLInputSource localXMLInputSource = null;
    if (fEntityResolver != null)
    {
      paramXMLResourceIdentifier.setBaseSystemId(str3);
      paramXMLResourceIdentifier.setExpandedSystemId(str4);
      localXMLInputSource = fEntityResolver.resolveEntity(paramXMLResourceIdentifier);
    }
    if (localXMLInputSource == null) {
      localXMLInputSource = new XMLInputSource(str1, str2, str3);
    }
    return localXMLInputSource;
  }
  
  public void startEntity(boolean paramBoolean1, String paramString, boolean paramBoolean2)
    throws IOException, XNIException
  {
    Entity localEntity = fEntityStorage.getEntity(paramString);
    if (localEntity == null)
    {
      if (fEntityHandler != null)
      {
        String str1 = null;
        fResourceIdentifier.clear();
        fEntityAugs.removeAllItems();
        fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        fEntityHandler.startEntity(paramString, fResourceIdentifier, str1, fEntityAugs);
        fEntityAugs.removeAllItems();
        fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        fEntityHandler.endEntity(paramString, fEntityAugs);
      }
      return;
    }
    boolean bool1 = localEntity.isExternal();
    Entity.ExternalEntity localExternalEntity = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    Object localObject2;
    if (bool1)
    {
      localExternalEntity = (Entity.ExternalEntity)localEntity;
      str2 = entityLocation != null ? entityLocation.getLiteralSystemId() : null;
      str3 = entityLocation != null ? entityLocation.getBaseSystemId() : null;
      str4 = expandSystemId(str2, str3);
      boolean bool2 = localEntity.isUnparsed();
      j = paramString.startsWith("%");
      int k = j == 0 ? 1 : 0;
      if ((bool2) || ((k != 0) && (!fExternalGeneralEntities)) || ((j != 0) && (!fExternalParameterEntities)) || (!fSupportDTD) || (!fSupportExternalEntities))
      {
        if (fEntityHandler != null)
        {
          fResourceIdentifier.clear();
          localObject2 = null;
          fResourceIdentifier.setValues(entityLocation != null ? entityLocation.getPublicId() : null, str2, str3, str4);
          fEntityAugs.removeAllItems();
          fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          fEntityHandler.startEntity(paramString, fResourceIdentifier, (String)localObject2, fEntityAugs);
          fEntityAugs.removeAllItems();
          fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          fEntityHandler.endEntity(paramString, fEntityAugs);
        }
        return;
      }
    }
    int i = fEntityStack.size();
    Object localObject3;
    for (int j = i; j >= 0; j--)
    {
      localObject1 = j == i ? fCurrentEntity : (Entity)fEntityStack.elementAt(j);
      if (name == paramString)
      {
        localObject2 = paramString;
        for (int m = j + 1; m < i; m++)
        {
          localObject1 = (Entity)fEntityStack.elementAt(m);
          localObject2 = (String)localObject2 + " -> " + name;
        }
        localObject2 = (String)localObject2 + " -> " + fCurrentEntity.name;
        localObject2 = (String)localObject2 + " -> " + paramString;
        fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { paramString, localObject2 }, (short)2);
        if (fEntityHandler != null)
        {
          fResourceIdentifier.clear();
          localObject3 = null;
          if (bool1) {
            fResourceIdentifier.setValues(entityLocation != null ? entityLocation.getPublicId() : null, str2, str3, str4);
          }
          fEntityAugs.removeAllItems();
          fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          fEntityHandler.startEntity(paramString, fResourceIdentifier, (String)localObject3, fEntityAugs);
          fEntityAugs.removeAllItems();
          fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          fEntityHandler.endEntity(paramString, fEntityAugs);
        }
        return;
      }
    }
    StaxXMLInputSource localStaxXMLInputSource = null;
    Object localObject1 = null;
    if (bool1)
    {
      localStaxXMLInputSource = resolveEntityAsPerStax(entityLocation);
      localObject1 = localStaxXMLInputSource.getXMLInputSource();
      if ((!fISCreatedByResolver) && (fLoadExternalDTD))
      {
        localObject2 = SecuritySupport.checkAccess(str4, fAccessExternalDTD, "all");
        if (localObject2 != null) {
          fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "AccessExternalEntity", new Object[] { SecuritySupport.sanitizePath(str4), localObject2 }, (short)2);
        }
      }
    }
    else
    {
      localObject2 = (Entity.InternalEntity)localEntity;
      localObject3 = new StringReader(text);
      localObject1 = new XMLInputSource(null, null, null, (Reader)localObject3, null);
    }
    startEntity(paramBoolean1, paramString, (XMLInputSource)localObject1, paramBoolean2, bool1);
  }
  
  public void startDocumentEntity(XMLInputSource paramXMLInputSource)
    throws IOException, XNIException
  {
    startEntity(false, XMLEntity, paramXMLInputSource, false, true);
  }
  
  public void startDTDEntity(XMLInputSource paramXMLInputSource)
    throws IOException, XNIException
  {
    startEntity(false, DTDEntity, paramXMLInputSource, false, true);
  }
  
  public void startExternalSubset()
  {
    fInExternalSubset = true;
  }
  
  public void endExternalSubset()
  {
    fInExternalSubset = false;
  }
  
  public void startEntity(boolean paramBoolean1, String paramString, XMLInputSource paramXMLInputSource, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException, XNIException
  {
    String str = setupCurrentEntity(paramBoolean1, paramString, paramXMLInputSource, paramBoolean2, paramBoolean3);
    fEntityExpansionCount += 1;
    if (fLimitAnalyzer != null) {
      fLimitAnalyzer.addValue(entityExpansionIndex, paramString, 1);
    }
    if ((fSecurityManager != null) && (fSecurityManager.isOverLimit(entityExpansionIndex, fLimitAnalyzer)))
    {
      fSecurityManager.debugPrint(fLimitAnalyzer);
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimit", new Object[] { fSecurityManager.getLimitValueByIndex(entityExpansionIndex) }, (short)2);
      fEntityExpansionCount = 0;
    }
    if (fEntityHandler != null) {
      fEntityHandler.startEntity(paramString, fResourceIdentifier, str, null);
    }
  }
  
  public Entity.ScannedEntity getCurrentEntity()
  {
    return fCurrentEntity;
  }
  
  public Entity.ScannedEntity getTopLevelEntity()
  {
    return (Entity.ScannedEntity)(fEntityStack.empty() ? null : (Entity)fEntityStack.elementAt(0));
  }
  
  public void closeReaders() {}
  
  public void endEntity()
    throws IOException, XNIException
  {
    Entity.ScannedEntity localScannedEntity = fEntityStack.size() > 0 ? (Entity.ScannedEntity)fEntityStack.pop() : null;
    if (fCurrentEntity != null) {
      try
      {
        if (fLimitAnalyzer != null)
        {
          fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, fCurrentEntity.name);
          if (fCurrentEntity.name.equals("[xml]")) {
            fSecurityManager.debugPrint(fLimitAnalyzer);
          }
        }
        fCurrentEntity.close();
      }
      catch (IOException localIOException)
      {
        throw new XNIException(localIOException);
      }
    }
    if (fEntityHandler != null) {
      if (localScannedEntity == null)
      {
        fEntityAugs.removeAllItems();
        fEntityAugs.putItem("LAST_ENTITY", Boolean.TRUE);
        fEntityHandler.endEntity(fCurrentEntity.name, fEntityAugs);
        fEntityAugs.removeAllItems();
      }
      else
      {
        fEntityHandler.endEntity(fCurrentEntity.name, null);
      }
    }
    int i = fCurrentEntity.name == XMLEntity ? 1 : 0;
    fCurrentEntity = localScannedEntity;
    fEntityScanner.setCurrentEntity(fCurrentEntity);
    if (((fCurrentEntity == null ? 1 : 0) & (i == 0 ? 1 : 0)) != 0) {
      throw new EOFException();
    }
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    fSymbolTable = ((SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    try
    {
      fStaxEntityResolver = ((StaxEntityResolverWrapper)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver"));
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      fStaxEntityResolver = null;
    }
    fSupportDTD = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    fReplaceEntityReferences = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences")).booleanValue();
    fSupportExternalEntities = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities")).booleanValue();
    fLoadExternalDTD = (!((Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue());
    XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramPropertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    fAccessExternalDTD = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    fSecurityManager = ((XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager"));
    fLimitAnalyzer = new XMLLimitAnalyzer();
    fEntityStorage.reset(paramPropertyManager);
    fEntityScanner.reset(paramPropertyManager);
    fEntities.clear();
    fEntityStack.removeAllElements();
    fCurrentEntity = null;
    fValidation = false;
    fExternalGeneralEntities = true;
    fExternalParameterEntities = true;
    fAllowJavaEncodings = true;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    boolean bool = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!bool)
    {
      reset();
      if (fEntityScanner != null) {
        fEntityScanner.reset(paramXMLComponentManager);
      }
      if (fEntityStorage != null) {
        fEntityStorage.reset(paramXMLComponentManager);
      }
      return;
    }
    fValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    fExternalGeneralEntities = paramXMLComponentManager.getFeature("http://xml.org/sax/features/external-general-entities", true);
    fExternalParameterEntities = paramXMLComponentManager.getFeature("http://xml.org/sax/features/external-parameter-entities", true);
    fAllowJavaEncodings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    fWarnDuplicateEntityDef = paramXMLComponentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    fStrictURI = paramXMLComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    fLoadExternalDTD = paramXMLComponentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fEntityResolver = ((XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null));
    fStaxEntityResolver = ((StaxEntityResolverWrapper)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver", null));
    fValidationManager = ((ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null));
    fSecurityManager = ((XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
    entityExpansionIndex = fSecurityManager.getIndex("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit");
    fSupportDTD = true;
    fReplaceEntityReferences = true;
    fSupportExternalEntities = true;
    XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    if (localXMLSecurityPropertyManager == null) {
      localXMLSecurityPropertyManager = new XMLSecurityPropertyManager();
    }
    fAccessExternalDTD = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    reset();
    fEntityScanner.reset(paramXMLComponentManager);
    fEntityStorage.reset(paramXMLComponentManager);
  }
  
  public void reset()
  {
    fLimitAnalyzer = new XMLLimitAnalyzer();
    fStandalone = false;
    fEntities.clear();
    fEntityStack.removeAllElements();
    fEntityExpansionCount = 0;
    fCurrentEntity = null;
    if (fXML10EntityScanner != null) {
      fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
    }
    if (fXML11EntityScanner != null) {
      fXML11EntityScanner.reset(fSymbolTable, this, fErrorReporter);
    }
    fEntityHandler = null;
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "allow-java-encodings".length()) && (paramString.endsWith("allow-java-encodings"))) {
        fAllowJavaEncodings = paramBoolean;
      }
      if ((i == "nonvalidating/load-external-dtd".length()) && (paramString.endsWith("nonvalidating/load-external-dtd")))
      {
        fLoadExternalDTD = paramBoolean;
        return;
      }
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/symbol-table".length()) && (paramString.endsWith("internal/symbol-table")))
      {
        fSymbolTable = ((SymbolTable)paramObject);
        return;
      }
      if ((i == "internal/error-reporter".length()) && (paramString.endsWith("internal/error-reporter")))
      {
        fErrorReporter = ((XMLErrorReporter)paramObject);
        return;
      }
      if ((i == "internal/entity-resolver".length()) && (paramString.endsWith("internal/entity-resolver")))
      {
        fEntityResolver = ((XMLEntityResolver)paramObject);
        return;
      }
      if ((i == "input-buffer-size".length()) && (paramString.endsWith("input-buffer-size")))
      {
        Integer localInteger = (Integer)paramObject;
        if ((localInteger != null) && (localInteger.intValue() > 64))
        {
          fBufferSize = localInteger.intValue();
          fEntityScanner.setBufferSize(fBufferSize);
          fBufferPool.setExternalBufferSize(fBufferSize);
        }
      }
      if ((i == "security-manager".length()) && (paramString.endsWith("security-manager"))) {
        fSecurityManager = ((XMLSecurityManager)paramObject);
      }
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      fAccessExternalDTD = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    }
  }
  
  public void setLimitAnalyzer(XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    fLimitAnalyzer = paramXMLLimitAnalyzer;
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public Boolean getFeatureDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(paramString)) {
        return FEATURE_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(paramString)) {
        return PROPERTY_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public static String expandSystemId(String paramString)
  {
    return expandSystemId(paramString, null);
  }
  
  private static synchronized com.sun.org.apache.xerces.internal.util.URI getUserDir()
    throws URI.MalformedURIException
  {
    String str = "";
    try
    {
      str = SecuritySupport.getSystemProperty("user.dir");
    }
    catch (SecurityException localSecurityException) {}
    if (str.length() == 0) {
      return new com.sun.org.apache.xerces.internal.util.URI("file", "", "", null, null);
    }
    if ((gUserDirURI != null) && (str.equals(gUserDir))) {
      return gUserDirURI;
    }
    gUserDir = str;
    char c = File.separatorChar;
    str = str.replace(c, '/');
    int i = str.length();
    StringBuffer localStringBuffer = new StringBuffer(i * 3);
    int j;
    if ((i >= 2) && (str.charAt(1) == ':'))
    {
      j = Character.toUpperCase(str.charAt(0));
      if ((j >= 65) && (j <= 90)) {
        localStringBuffer.append('/');
      }
    }
    for (int k = 0; k < i; k++)
    {
      j = str.charAt(k);
      if (j >= 128) {
        break;
      }
      if (gNeedEscaping[j] != 0)
      {
        localStringBuffer.append('%');
        localStringBuffer.append(gAfterEscaping1[j]);
        localStringBuffer.append(gAfterEscaping2[j]);
      }
      else
      {
        localStringBuffer.append((char)j);
      }
    }
    if (k < i)
    {
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = str.substring(k).getBytes("UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        return new com.sun.org.apache.xerces.internal.util.URI("file", "", str, null, null);
      }
      i = arrayOfByte.length;
      for (k = 0; k < i; k++)
      {
        int m = arrayOfByte[k];
        if (m < 0)
        {
          j = m + 256;
          localStringBuffer.append('%');
          localStringBuffer.append(gHexChs[(j >> 4)]);
          localStringBuffer.append(gHexChs[(j & 0xF)]);
        }
        else if (gNeedEscaping[m] != 0)
        {
          localStringBuffer.append('%');
          localStringBuffer.append(gAfterEscaping1[m]);
          localStringBuffer.append(gAfterEscaping2[m]);
        }
        else
        {
          localStringBuffer.append((char)m);
        }
      }
    }
    if (!str.endsWith("/")) {
      localStringBuffer.append('/');
    }
    gUserDirURI = new com.sun.org.apache.xerces.internal.util.URI("file", "", localStringBuffer.toString(), null, null);
    return gUserDirURI;
  }
  
  public static void absolutizeAgainstUserDir(com.sun.org.apache.xerces.internal.util.URI paramURI)
    throws URI.MalformedURIException
  {
    paramURI.absolutize(getUserDir());
  }
  
  public static String expandSystemId(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      return paramString1;
    }
    try
    {
      com.sun.org.apache.xerces.internal.util.URI localURI1 = new com.sun.org.apache.xerces.internal.util.URI(paramString1);
      if (localURI1 != null) {
        return paramString1;
      }
    }
    catch (URI.MalformedURIException localMalformedURIException1) {}
    String str1 = fixURI(paramString1);
    com.sun.org.apache.xerces.internal.util.URI localURI2 = null;
    com.sun.org.apache.xerces.internal.util.URI localURI3 = null;
    try
    {
      if ((paramString2 == null) || (paramString2.length() == 0) || (paramString2.equals(paramString1)))
      {
        String str2 = getUserDir().toString();
        localURI2 = new com.sun.org.apache.xerces.internal.util.URI("file", "", str2, null, null);
      }
      else
      {
        try
        {
          localURI2 = new com.sun.org.apache.xerces.internal.util.URI(fixURI(paramString2));
        }
        catch (URI.MalformedURIException localMalformedURIException2)
        {
          if (paramString2.indexOf(':') != -1)
          {
            localURI2 = new com.sun.org.apache.xerces.internal.util.URI("file", "", fixURI(paramString2), null, null);
          }
          else
          {
            String str3 = getUserDir().toString();
            str3 = str3 + fixURI(paramString2);
            localURI2 = new com.sun.org.apache.xerces.internal.util.URI("file", "", str3, null, null);
          }
        }
      }
      localURI3 = new com.sun.org.apache.xerces.internal.util.URI(localURI2, str1);
    }
    catch (Exception localException) {}
    if (localURI3 == null) {
      return paramString1;
    }
    return localURI3.toString();
  }
  
  public static String expandSystemId(String paramString1, String paramString2, boolean paramBoolean)
    throws URI.MalformedURIException
  {
    if (paramString1 == null) {
      return null;
    }
    Object localObject;
    if (paramBoolean) {
      try
      {
        new com.sun.org.apache.xerces.internal.util.URI(paramString1);
        return paramString1;
      }
      catch (URI.MalformedURIException localMalformedURIException1)
      {
        com.sun.org.apache.xerces.internal.util.URI localURI1 = null;
        if ((paramString2 == null) || (paramString2.length() == 0)) {
          localURI1 = new com.sun.org.apache.xerces.internal.util.URI("file", "", getUserDir().toString(), null, null);
        } else {
          try
          {
            localURI1 = new com.sun.org.apache.xerces.internal.util.URI(paramString2);
          }
          catch (URI.MalformedURIException localMalformedURIException3)
          {
            localObject = getUserDir().toString();
            localObject = (String)localObject + paramString2;
            localURI1 = new com.sun.org.apache.xerces.internal.util.URI("file", "", (String)localObject, null, null);
          }
        }
        com.sun.org.apache.xerces.internal.util.URI localURI2 = new com.sun.org.apache.xerces.internal.util.URI(localURI1, paramString1);
        return localURI2.toString();
      }
    }
    try
    {
      return expandSystemIdStrictOff(paramString1, paramString2);
    }
    catch (URI.MalformedURIException localMalformedURIException2)
    {
      try
      {
        return expandSystemIdStrictOff1(paramString1, paramString2);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        if (paramString1.length() == 0) {
          return paramString1;
        }
        String str = fixURI(paramString1);
        com.sun.org.apache.xerces.internal.util.URI localURI3 = null;
        localObject = null;
        try
        {
          if ((paramString2 == null) || (paramString2.length() == 0) || (paramString2.equals(paramString1))) {
            localURI3 = getUserDir();
          } else {
            try
            {
              localURI3 = new com.sun.org.apache.xerces.internal.util.URI(fixURI(paramString2).trim());
            }
            catch (URI.MalformedURIException localMalformedURIException4)
            {
              if (paramString2.indexOf(':') != -1) {
                localURI3 = new com.sun.org.apache.xerces.internal.util.URI("file", "", fixURI(paramString2).trim(), null, null);
              } else {
                localURI3 = new com.sun.org.apache.xerces.internal.util.URI(getUserDir(), fixURI(paramString2));
              }
            }
          }
          localObject = new com.sun.org.apache.xerces.internal.util.URI(localURI3, str.trim());
        }
        catch (Exception localException) {}
        if (localObject == null) {
          return paramString1;
        }
      }
    }
    return ((com.sun.org.apache.xerces.internal.util.URI)localObject).toString();
  }
  
  private static String expandSystemIdStrictOn(String paramString1, String paramString2)
    throws URI.MalformedURIException
  {
    com.sun.org.apache.xerces.internal.util.URI localURI1 = new com.sun.org.apache.xerces.internal.util.URI(paramString1, true);
    if (localURI1.isAbsoluteURI()) {
      return paramString1;
    }
    com.sun.org.apache.xerces.internal.util.URI localURI2 = null;
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      localURI2 = getUserDir();
    }
    else
    {
      localURI2 = new com.sun.org.apache.xerces.internal.util.URI(paramString2, true);
      if (!localURI2.isAbsoluteURI()) {
        localURI2.absolutize(getUserDir());
      }
    }
    localURI1.absolutize(localURI2);
    return localURI1.toString();
  }
  
  public static void setInstanceFollowRedirects(HttpURLConnection paramHttpURLConnection, boolean paramBoolean)
  {
    try
    {
      Method localMethod = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", new Class[] { Boolean.TYPE });
      localMethod.invoke(paramHttpURLConnection, new Object[] { paramBoolean ? Boolean.TRUE : Boolean.FALSE });
    }
    catch (Exception localException) {}
  }
  
  private static String expandSystemIdStrictOff(String paramString1, String paramString2)
    throws URI.MalformedURIException
  {
    com.sun.org.apache.xerces.internal.util.URI localURI1 = new com.sun.org.apache.xerces.internal.util.URI(paramString1, true);
    if (localURI1.isAbsoluteURI())
    {
      if (localURI1.getScheme().length() > 1) {
        return paramString1;
      }
      throw new URI.MalformedURIException();
    }
    com.sun.org.apache.xerces.internal.util.URI localURI2 = null;
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      localURI2 = getUserDir();
    }
    else
    {
      localURI2 = new com.sun.org.apache.xerces.internal.util.URI(paramString2, true);
      if (!localURI2.isAbsoluteURI()) {
        localURI2.absolutize(getUserDir());
      }
    }
    localURI1.absolutize(localURI2);
    return localURI1.toString();
  }
  
  private static String expandSystemIdStrictOff1(String paramString1, String paramString2)
    throws URISyntaxException, URI.MalformedURIException
  {
    java.net.URI localURI = new java.net.URI(paramString1);
    if (localURI.isAbsolute())
    {
      if (localURI.getScheme().length() > 1) {
        return paramString1;
      }
      throw new URISyntaxException(paramString1, "the scheme's length is only one character");
    }
    com.sun.org.apache.xerces.internal.util.URI localURI1 = null;
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      localURI1 = getUserDir();
    }
    else
    {
      localURI1 = new com.sun.org.apache.xerces.internal.util.URI(paramString2, true);
      if (!localURI1.isAbsoluteURI()) {
        localURI1.absolutize(getUserDir());
      }
    }
    localURI = new java.net.URI(localURI1.toString()).resolve(localURI);
    return localURI.toString();
  }
  
  protected Object[] getEncodingName(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < 2) {
      return defaultEncoding;
    }
    int i = paramArrayOfByte[0] & 0xFF;
    int j = paramArrayOfByte[1] & 0xFF;
    if ((i == 254) && (j == 255)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 255) && (j == 254)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if (paramInt < 3) {
      return defaultEncoding;
    }
    int k = paramArrayOfByte[2] & 0xFF;
    if ((i == 239) && (j == 187) && (k == 191)) {
      return defaultEncoding;
    }
    if (paramInt < 4) {
      return defaultEncoding;
    }
    int m = paramArrayOfByte[3] & 0xFF;
    if ((i == 0) && (j == 0) && (k == 0) && (m == 60)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
    }
    if ((i == 0) && (j == 0) && (k == 60) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 63)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 63) && (m == 0)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if ((i == 76) && (j == 111) && (k == 167) && (m == 148)) {
      return new Object[] { "CP037", null };
    }
    return defaultEncoding;
  }
  
  protected Reader createReader(InputStream paramInputStream, String paramString, Boolean paramBoolean)
    throws IOException
  {
    if (paramString == null) {
      paramString = "UTF-8";
    }
    String str1 = paramString.toUpperCase(Locale.ENGLISH);
    if (str1.equals("UTF-8")) {
      return new UTF8Reader(paramInputStream, fBufferSize, fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), fErrorReporter.getLocale());
    }
    if (str1.equals("US-ASCII")) {
      return new ASCIIReader(paramInputStream, fBufferSize, fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), fErrorReporter.getLocale());
    }
    if (str1.equals("ISO-10646-UCS-4"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)8);
        }
        return new UCSReader(paramInputStream, (short)4);
      }
      fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    if (str1.equals("ISO-10646-UCS-2"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)2);
        }
        return new UCSReader(paramInputStream, (short)1);
      }
      fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    boolean bool1 = XMLChar.isValidIANAEncoding(paramString);
    boolean bool2 = XMLChar.isValidJavaEncoding(paramString);
    if ((!bool1) || ((fAllowJavaEncodings) && (!bool2)))
    {
      fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
      paramString = "ISO-8859-1";
    }
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null) {
      if (fAllowJavaEncodings)
      {
        str2 = paramString;
      }
      else
      {
        fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
        str2 = "ISO8859_1";
      }
    }
    return new BufferedReader(new InputStreamReader(paramInputStream, str2));
  }
  
  public String getPublicId()
  {
    return (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getPublicId() : null;
  }
  
  public String getExpandedSystemId()
  {
    if (fCurrentEntity != null)
    {
      if ((fCurrentEntity.entityLocation != null) && (fCurrentEntity.entityLocation.getExpandedSystemId() != null)) {
        return fCurrentEntity.entityLocation.getExpandedSystemId();
      }
      int i = fEntityStack.size();
      for (int j = i - 1; j >= 0; j--)
      {
        Entity.ScannedEntity localScannedEntity = (Entity.ScannedEntity)fEntityStack.elementAt(j);
        if ((entityLocation != null) && (entityLocation.getExpandedSystemId() != null)) {
          return entityLocation.getExpandedSystemId();
        }
      }
    }
    return null;
  }
  
  public String getLiteralSystemId()
  {
    if (fCurrentEntity != null)
    {
      if ((fCurrentEntity.entityLocation != null) && (fCurrentEntity.entityLocation.getLiteralSystemId() != null)) {
        return fCurrentEntity.entityLocation.getLiteralSystemId();
      }
      int i = fEntityStack.size();
      for (int j = i - 1; j >= 0; j--)
      {
        Entity.ScannedEntity localScannedEntity = (Entity.ScannedEntity)fEntityStack.elementAt(j);
        if ((entityLocation != null) && (entityLocation.getLiteralSystemId() != null)) {
          return entityLocation.getLiteralSystemId();
        }
      }
    }
    return null;
  }
  
  public int getLineNumber()
  {
    if (fCurrentEntity != null)
    {
      if (fCurrentEntity.isExternal()) {
        return fCurrentEntity.lineNumber;
      }
      int i = fEntityStack.size();
      for (int j = i - 1; j > 0; j--)
      {
        Entity.ScannedEntity localScannedEntity = (Entity.ScannedEntity)fEntityStack.elementAt(j);
        if (localScannedEntity.isExternal()) {
          return lineNumber;
        }
      }
    }
    return -1;
  }
  
  public int getColumnNumber()
  {
    if (fCurrentEntity != null)
    {
      if (fCurrentEntity.isExternal()) {
        return fCurrentEntity.columnNumber;
      }
      int i = fEntityStack.size();
      for (int j = i - 1; j > 0; j--)
      {
        Entity.ScannedEntity localScannedEntity = (Entity.ScannedEntity)fEntityStack.elementAt(j);
        if (localScannedEntity.isExternal()) {
          return columnNumber;
        }
      }
    }
    return -1;
  }
  
  protected static String fixURI(String paramString)
  {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.length() >= 2)
    {
      i = paramString.charAt(1);
      if (i == 58)
      {
        int j = Character.toUpperCase(paramString.charAt(0));
        if ((j >= 65) && (j <= 90)) {
          paramString = "/" + paramString;
        }
      }
      else if ((i == 47) && (paramString.charAt(0) == '/'))
      {
        paramString = "file:" + paramString;
      }
    }
    int i = paramString.indexOf(' ');
    if (i >= 0)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString.length());
      for (int k = 0; k < i; k++) {
        localStringBuilder.append(paramString.charAt(k));
      }
      localStringBuilder.append("%20");
      for (k = i + 1; k < paramString.length(); k++) {
        if (paramString.charAt(k) == ' ') {
          localStringBuilder.append("%20");
        } else {
          localStringBuilder.append(paramString.charAt(k));
        }
      }
      paramString = localStringBuilder.toString();
    }
    return paramString;
  }
  
  final void print() {}
  
  public void test()
  {
    fEntityStorage.addExternalEntity("entityUsecase1", null, "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt", "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
    fEntityStorage.addInternalEntity("entityUsecase2", "<Test>value</Test>");
    fEntityStorage.addInternalEntity("entityUsecase3", "value3");
    fEntityStorage.addInternalEntity("text", "Hello World.");
    fEntityStorage.addInternalEntity("empty-element", "<foo/>");
    fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
    fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
    fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
    fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
    fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
    fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
    fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
    fEntityStorage.addInternalEntity("ch", "&#x00A9;");
    fEntityStorage.addInternalEntity("ch1", "&#84;");
    fEntityStorage.addInternalEntity("% ch2", "param");
  }
  
  static
  {
    for (int i = 0; i <= 31; i++)
    {
      gNeedEscaping[i] = true;
      gAfterEscaping1[i] = gHexChs[(i >> 4)];
      gAfterEscaping2[i] = gHexChs[(i & 0xF)];
    }
    gNeedEscaping[127] = true;
    gAfterEscaping1[127] = '7';
    gAfterEscaping2[127] = 'F';
    for (int k : new char[] { ' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' })
    {
      gNeedEscaping[k] = true;
      gAfterEscaping1[k] = gHexChs[(k >> 4)];
      gAfterEscaping2[k] = gHexChs[(k & 0xF)];
    }
  }
  
  private static class CharacterBuffer
  {
    private char[] ch;
    private boolean isExternal;
    
    public CharacterBuffer(boolean paramBoolean, int paramInt)
    {
      isExternal = paramBoolean;
      ch = new char[paramInt];
    }
  }
  
  private static class CharacterBufferPool
  {
    private static final int DEFAULT_POOL_SIZE = 3;
    private XMLEntityManager.CharacterBuffer[] fInternalBufferPool;
    private XMLEntityManager.CharacterBuffer[] fExternalBufferPool;
    private int fExternalBufferSize;
    private int fInternalBufferSize;
    private int poolSize;
    private int fInternalTop;
    private int fExternalTop;
    
    public CharacterBufferPool(int paramInt1, int paramInt2)
    {
      this(3, paramInt1, paramInt2);
    }
    
    public CharacterBufferPool(int paramInt1, int paramInt2, int paramInt3)
    {
      fExternalBufferSize = paramInt2;
      fInternalBufferSize = paramInt3;
      poolSize = paramInt1;
      init();
    }
    
    private void init()
    {
      fInternalBufferPool = new XMLEntityManager.CharacterBuffer[poolSize];
      fExternalBufferPool = new XMLEntityManager.CharacterBuffer[poolSize];
      fInternalTop = -1;
      fExternalTop = -1;
    }
    
    public XMLEntityManager.CharacterBuffer getBuffer(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        if (fExternalTop > -1) {
          return fExternalBufferPool[(fExternalTop--)];
        }
        return new XMLEntityManager.CharacterBuffer(true, fExternalBufferSize);
      }
      if (fInternalTop > -1) {
        return fInternalBufferPool[(fInternalTop--)];
      }
      return new XMLEntityManager.CharacterBuffer(false, fInternalBufferSize);
    }
    
    public void returnToPool(XMLEntityManager.CharacterBuffer paramCharacterBuffer)
    {
      if (isExternal)
      {
        if (fExternalTop < fExternalBufferPool.length - 1) {
          fExternalBufferPool[(++fExternalTop)] = paramCharacterBuffer;
        }
      }
      else if (fInternalTop < fInternalBufferPool.length - 1) {
        fInternalBufferPool[(++fInternalTop)] = paramCharacterBuffer;
      }
    }
    
    public void setExternalBufferSize(int paramInt)
    {
      fExternalBufferSize = paramInt;
      fExternalBufferPool = new XMLEntityManager.CharacterBuffer[poolSize];
      fExternalTop = -1;
    }
  }
  
  protected final class RewindableInputStream
    extends InputStream
  {
    private InputStream fInputStream;
    private byte[] fData = new byte[64];
    private int fStartOffset;
    private int fEndOffset;
    private int fOffset;
    private int fLength;
    private int fMark;
    
    public RewindableInputStream(InputStream paramInputStream)
    {
      fInputStream = paramInputStream;
      fStartOffset = 0;
      fEndOffset = -1;
      fOffset = 0;
      fLength = 0;
      fMark = 0;
    }
    
    public void setStartOffset(int paramInt)
    {
      fStartOffset = paramInt;
    }
    
    public void rewind()
    {
      fOffset = fStartOffset;
    }
    
    public int read()
      throws IOException
    {
      int i = 0;
      if (fOffset < fLength) {
        return fData[(fOffset++)] & 0xFF;
      }
      if (fOffset == fEndOffset) {
        return -1;
      }
      if (fOffset == fData.length)
      {
        byte[] arrayOfByte = new byte[fOffset << 1];
        System.arraycopy(fData, 0, arrayOfByte, 0, fOffset);
        fData = arrayOfByte;
      }
      i = fInputStream.read();
      if (i == -1)
      {
        fEndOffset = fOffset;
        return -1;
      }
      fData[(fLength++)] = ((byte)i);
      fOffset += 1;
      return i & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = fLength - fOffset;
      if (i == 0)
      {
        if (fOffset == fEndOffset) {
          return -1;
        }
        if ((fCurrentEntity.mayReadChunks) || (!fCurrentEntity.xmlDeclChunkRead))
        {
          if (!fCurrentEntity.xmlDeclChunkRead)
          {
            fCurrentEntity.xmlDeclChunkRead = true;
            paramInt2 = 28;
          }
          return fInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
        }
        int j = read();
        if (j == -1)
        {
          fEndOffset = fOffset;
          return -1;
        }
        paramArrayOfByte[paramInt1] = ((byte)j);
        return 1;
      }
      if (paramInt2 < i)
      {
        if (paramInt2 <= 0) {
          return 0;
        }
      }
      else {
        paramInt2 = i;
      }
      if (paramArrayOfByte != null) {
        System.arraycopy(fData, fOffset, paramArrayOfByte, paramInt1, paramInt2);
      }
      fOffset += paramInt2;
      return paramInt2;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong <= 0L) {
        return 0L;
      }
      int i = fLength - fOffset;
      if (i == 0)
      {
        if (fOffset == fEndOffset) {
          return 0L;
        }
        return fInputStream.skip(paramLong);
      }
      if (paramLong <= i)
      {
        fOffset = ((int)(fOffset + paramLong));
        return paramLong;
      }
      fOffset += i;
      if (fOffset == fEndOffset) {
        return i;
      }
      paramLong -= i;
      return fInputStream.skip(paramLong) + i;
    }
    
    public int available()
      throws IOException
    {
      int i = fLength - fOffset;
      if (i == 0)
      {
        if (fOffset == fEndOffset) {
          return -1;
        }
        return fCurrentEntity.mayReadChunks ? fInputStream.available() : 0;
      }
      return i;
    }
    
    public void mark(int paramInt)
    {
      fMark = fOffset;
    }
    
    public void reset()
    {
      fOffset = fMark;
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public void close()
      throws IOException
    {
      if (fInputStream != null)
      {
        fInputStream.close();
        fInputStream = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */