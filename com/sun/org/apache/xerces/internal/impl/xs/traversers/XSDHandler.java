package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar.BuiltinSchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar.Schema4Annotations;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader.LocationArray;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDDescription;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
import com.sun.org.apache.xerces.internal.impl.xs.XSGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOM;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaParsingConfig;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSInputSource;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTerm;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XSDHandler
{
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
  protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  protected static final boolean DEBUG_NODE_POOL = false;
  static final int ATTRIBUTE_TYPE = 1;
  static final int ATTRIBUTEGROUP_TYPE = 2;
  static final int ELEMENT_TYPE = 3;
  static final int GROUP_TYPE = 4;
  static final int IDENTITYCONSTRAINT_TYPE = 5;
  static final int NOTATION_TYPE = 6;
  static final int TYPEDECL_TYPE = 7;
  public static final String REDEF_IDENTIFIER = "_fn3dktizrknc9pi";
  protected XSDeclarationPool fDeclPool = null;
  protected XMLSecurityManager fSecurityManager = null;
  private String fAccessExternalSchema;
  private String fAccessExternalDTD;
  private boolean registryEmpty = true;
  private Map<String, Element> fUnparsedAttributeRegistry = new HashMap();
  private Map<String, Element> fUnparsedAttributeGroupRegistry = new HashMap();
  private Map<String, Element> fUnparsedElementRegistry = new HashMap();
  private Map<String, Element> fUnparsedGroupRegistry = new HashMap();
  private Map<String, Element> fUnparsedIdentityConstraintRegistry = new HashMap();
  private Map<String, Element> fUnparsedNotationRegistry = new HashMap();
  private Map<String, Element> fUnparsedTypeRegistry = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedAttributeRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedAttributeGroupRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedElementRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedGroupRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedIdentityConstraintRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedNotationRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo> fUnparsedTypeRegistrySub = new HashMap();
  private Map<String, XSDocumentInfo>[] fUnparsedRegistriesExt = { null, null, null, null, null, null, null, null };
  private Map<XSDocumentInfo, Vector<XSDocumentInfo>> fDependencyMap = new HashMap();
  private Map<String, Vector> fImportMap = new HashMap();
  private Vector<String> fAllTNSs = new Vector();
  private Map<String, XMLSchemaLoader.LocationArray> fLocationPairs = null;
  Map<Node, String> fHiddenNodes = null;
  private Map<XSDKey, Element> fTraversed = new HashMap();
  private Map<Element, String> fDoc2SystemId = new HashMap();
  private XSDocumentInfo fRoot = null;
  private Map fDoc2XSDocumentMap = new HashMap();
  private Map fRedefine2XSDMap = null;
  private Map fRedefine2NSSupport = null;
  private Map fRedefinedRestrictedAttributeGroupRegistry = new HashMap();
  private Map fRedefinedRestrictedGroupRegistry = new HashMap();
  private boolean fLastSchemaWasDuplicate;
  private boolean fValidateAnnotations = false;
  private boolean fHonourAllSchemaLocations = false;
  boolean fNamespaceGrowth = false;
  boolean fTolerateDuplicates = false;
  private XMLErrorReporter fErrorReporter;
  private XMLErrorHandler fErrorHandler;
  private Locale fLocale;
  private XMLEntityResolver fEntityManager;
  private XSAttributeChecker fAttributeChecker;
  private SymbolTable fSymbolTable;
  private XSGrammarBucket fGrammarBucket;
  private XSDDescription fSchemaGrammarDescription;
  private XMLGrammarPool fGrammarPool;
  private XMLSecurityPropertyManager fSecurityPropertyMgr = null;
  XSDAttributeGroupTraverser fAttributeGroupTraverser;
  XSDAttributeTraverser fAttributeTraverser;
  XSDComplexTypeTraverser fComplexTypeTraverser;
  XSDElementTraverser fElementTraverser;
  XSDGroupTraverser fGroupTraverser;
  XSDKeyrefTraverser fKeyrefTraverser;
  XSDNotationTraverser fNotationTraverser;
  XSDSimpleTypeTraverser fSimpleTypeTraverser;
  XSDUniqueOrKeyTraverser fUniqueOrKeyTraverser;
  XSDWildcardTraverser fWildCardTraverser;
  SchemaDVFactory fDVFactory;
  SchemaDOMParser fSchemaParser = new SchemaDOMParser(new SchemaParsingConfig());
  SchemaContentHandler fXSContentHandler;
  StAXSchemaParser fStAXSchemaParser;
  XML11Configuration fAnnotationValidator;
  XSAnnotationGrammarPool fGrammarBucketAdapter;
  private static final int INIT_STACK_SIZE = 30;
  private static final int INC_STACK_SIZE = 10;
  private int fLocalElemStackPos = 0;
  private XSParticleDecl[] fParticle = new XSParticleDecl[30];
  private Element[] fLocalElementDecl = new Element[30];
  private XSDocumentInfo[] fLocalElementDecl_schema = new XSDocumentInfo[30];
  private int[] fAllContext = new int[30];
  private XSObject[] fParent = new XSObject[30];
  private String[][] fLocalElemNamespaceContext = new String[30][1];
  private static final int INIT_KEYREF_STACK = 2;
  private static final int INC_KEYREF_STACK_AMOUNT = 2;
  private int fKeyrefStackPos = 0;
  private Element[] fKeyrefs = new Element[2];
  private XSDocumentInfo[] fKeyrefsMapXSDocumentInfo = new XSDocumentInfo[2];
  private XSElementDecl[] fKeyrefElems = new XSElementDecl[2];
  private String[][] fKeyrefNamespaceContext = new String[2][1];
  SymbolHash fGlobalAttrDecls = new SymbolHash(12);
  SymbolHash fGlobalAttrGrpDecls = new SymbolHash(5);
  SymbolHash fGlobalElemDecls = new SymbolHash(25);
  SymbolHash fGlobalGroupDecls = new SymbolHash(5);
  SymbolHash fGlobalNotationDecls = new SymbolHash(1);
  SymbolHash fGlobalIDConstraintDecls = new SymbolHash(3);
  SymbolHash fGlobalTypeDecls = new SymbolHash(25);
  private static final String[][] NS_ERROR_CODES = { { "src-include.2.1", "src-include.2.1" }, { "src-redefine.3.1", "src-redefine.3.1" }, { "src-import.3.1", "src-import.3.2" }, null, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" } };
  private static final String[] ELE_ERROR_CODES = { "src-include.1", "src-redefine.2", "src-import.2", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4" };
  private Vector fReportedTNS = null;
  private static final String[] COMP_TYPE = { null, "attribute declaration", "attribute group", "element declaration", "group", "identity constraint", "notation", "type definition" };
  private static final String[] CIRCULAR_CODES = { "Internal-Error", "Internal-Error", "src-attribute_group.3", "e-props-correct.6", "mg-props-correct.2", "Internal-Error", "Internal-Error", "st-props-correct.2" };
  private SimpleLocator xl = new SimpleLocator();
  
  private String null2EmptyString(String paramString)
  {
    return paramString == null ? XMLSymbols.EMPTY_STRING : paramString;
  }
  
  private String emptyString2Null(String paramString)
  {
    return paramString == XMLSymbols.EMPTY_STRING ? null : paramString;
  }
  
  private String doc2SystemId(Element paramElement)
  {
    String str = null;
    if ((paramElement.getOwnerDocument() instanceof SchemaDOM)) {
      str = ((SchemaDOM)paramElement.getOwnerDocument()).getDocumentURI();
    }
    return str != null ? str : (String)fDoc2SystemId.get(paramElement);
  }
  
  public XSDHandler() {}
  
  public XSDHandler(XSGrammarBucket paramXSGrammarBucket)
  {
    this();
    fGrammarBucket = paramXSGrammarBucket;
    fSchemaGrammarDescription = new XSDDescription();
  }
  
  public SchemaGrammar parseSchema(XMLInputSource paramXMLInputSource, XSDDescription paramXSDDescription, Map<String, XMLSchemaLoader.LocationArray> paramMap)
    throws IOException
  {
    fLocationPairs = paramMap;
    fSchemaParser.resetNodePool();
    SchemaGrammar localSchemaGrammar1 = null;
    String str1 = null;
    short s = paramXSDDescription.getContextType();
    if (s != 3)
    {
      if ((fHonourAllSchemaLocations) && (s == 2) && (isExistingGrammar(paramXSDDescription, fNamespaceGrowth))) {
        localSchemaGrammar1 = fGrammarBucket.getGrammar(paramXSDDescription.getTargetNamespace());
      } else {
        localSchemaGrammar1 = findGrammar(paramXSDDescription, fNamespaceGrowth);
      }
      if (localSchemaGrammar1 != null)
      {
        if (!fNamespaceGrowth) {
          return localSchemaGrammar1;
        }
        try
        {
          if (localSchemaGrammar1.getDocumentLocations().contains(XMLEntityManager.expandSystemId(paramXMLInputSource.getSystemId(), paramXMLInputSource.getBaseSystemId(), false))) {
            return localSchemaGrammar1;
          }
        }
        catch (URI.MalformedURIException localMalformedURIException) {}
      }
      str1 = paramXSDDescription.getTargetNamespace();
      if (str1 != null) {
        str1 = fSymbolTable.addSymbol(str1);
      }
    }
    prepareForParse();
    Element localElement1 = null;
    if ((paramXMLInputSource instanceof DOMInputSource)) {
      localElement1 = getSchemaDocument(str1, (DOMInputSource)paramXMLInputSource, s == 3, s, null);
    } else if ((paramXMLInputSource instanceof SAXInputSource)) {
      localElement1 = getSchemaDocument(str1, (SAXInputSource)paramXMLInputSource, s == 3, s, null);
    } else if ((paramXMLInputSource instanceof StAXInputSource)) {
      localElement1 = getSchemaDocument(str1, (StAXInputSource)paramXMLInputSource, s == 3, s, null);
    } else if ((paramXMLInputSource instanceof XSInputSource)) {
      localElement1 = getSchemaDocument((XSInputSource)paramXMLInputSource, paramXSDDescription);
    } else {
      localElement1 = getSchemaDocument(str1, paramXMLInputSource, s == 3, s, null);
    }
    if (localElement1 == null)
    {
      if ((paramXMLInputSource instanceof XSInputSource)) {
        return fGrammarBucket.getGrammar(paramXSDDescription.getTargetNamespace());
      }
      return localSchemaGrammar1;
    }
    Object localObject;
    if (s == 3)
    {
      localElement2 = localElement1;
      str1 = DOMUtil.getAttrValue(localElement2, SchemaSymbols.ATT_TARGETNAMESPACE);
      if ((str1 != null) && (str1.length() > 0))
      {
        str1 = fSymbolTable.addSymbol(str1);
        paramXSDDescription.setTargetNamespace(str1);
      }
      else
      {
        str1 = null;
      }
      localSchemaGrammar1 = findGrammar(paramXSDDescription, fNamespaceGrowth);
      String str2 = XMLEntityManager.expandSystemId(paramXMLInputSource.getSystemId(), paramXMLInputSource.getBaseSystemId(), false);
      if ((localSchemaGrammar1 != null) && ((!fNamespaceGrowth) || ((str2 != null) && (localSchemaGrammar1.getDocumentLocations().contains(str2))))) {
        return localSchemaGrammar1;
      }
      localObject = new XSDKey(str2, s, str1);
      fTraversed.put(localObject, localElement1);
      if (str2 != null) {
        fDoc2SystemId.put(localElement1, str2);
      }
    }
    prepareForTraverse();
    fRoot = constructTrees(localElement1, paramXMLInputSource.getSystemId(), paramXSDDescription, localSchemaGrammar1 != null);
    if (fRoot == null) {
      return null;
    }
    buildGlobalNameRegistries();
    Element localElement2 = fValidateAnnotations ? new ArrayList() : null;
    traverseSchemas(localElement2);
    traverseLocalElements();
    resolveKeyRefs();
    for (int i = fAllTNSs.size() - 1; i >= 0; i--)
    {
      localObject = (String)fAllTNSs.elementAt(i);
      Vector localVector = (Vector)fImportMap.get(localObject);
      SchemaGrammar localSchemaGrammar2 = fGrammarBucket.getGrammar(emptyString2Null((String)localObject));
      if (localSchemaGrammar2 != null)
      {
        int j = 0;
        for (int k = 0; k < localVector.size(); k++)
        {
          SchemaGrammar localSchemaGrammar3 = fGrammarBucket.getGrammar((String)localVector.elementAt(k));
          if (localSchemaGrammar3 != null) {
            localVector.setElementAt(localSchemaGrammar3, j++);
          }
        }
        localVector.setSize(j);
        localSchemaGrammar2.setImportedGrammars(localVector);
      }
    }
    if ((fValidateAnnotations) && (localElement2.size() > 0)) {
      validateAnnotations(localElement2);
    }
    return fGrammarBucket.getGrammar(fRoot.fTargetNamespace);
  }
  
  private void validateAnnotations(ArrayList paramArrayList)
  {
    if (fAnnotationValidator == null) {
      createAnnotationValidator();
    }
    int i = paramArrayList.size();
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null);
    fGrammarBucketAdapter.refreshGrammars(fGrammarBucket);
    for (int j = 0; j < i; j += 2)
    {
      localXMLInputSource.setSystemId((String)paramArrayList.get(j));
      for (XSAnnotationInfo localXSAnnotationInfo = (XSAnnotationInfo)paramArrayList.get(j + 1); localXSAnnotationInfo != null; localXSAnnotationInfo = next)
      {
        localXMLInputSource.setCharacterStream(new StringReader(fAnnotation));
        try
        {
          fAnnotationValidator.parse(localXMLInputSource);
        }
        catch (IOException localIOException) {}
      }
    }
  }
  
  private void createAnnotationValidator()
  {
    fAnnotationValidator = new XML11Configuration();
    fGrammarBucketAdapter = new XSAnnotationGrammarPool(null);
    fAnnotationValidator.setFeature("http://xml.org/sax/features/validation", true);
    fAnnotationValidator.setFeature("http://apache.org/xml/features/validation/schema", true);
    fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/grammar-pool", fGrammarBucketAdapter);
    fAnnotationValidator.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager != null ? fSecurityManager : new XMLSecurityManager(true));
    fAnnotationValidator.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
    fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", fErrorHandler != null ? fErrorHandler : new DefaultErrorHandler());
    fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", fLocale);
  }
  
  SchemaGrammar getGrammar(String paramString)
  {
    return fGrammarBucket.getGrammar(paramString);
  }
  
  protected SchemaGrammar findGrammar(XSDDescription paramXSDDescription, boolean paramBoolean)
  {
    SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(paramXSDDescription.getTargetNamespace());
    if ((localSchemaGrammar == null) && (fGrammarPool != null))
    {
      localSchemaGrammar = (SchemaGrammar)fGrammarPool.retrieveGrammar(paramXSDDescription);
      if ((localSchemaGrammar != null) && (!fGrammarBucket.putGrammar(localSchemaGrammar, true, paramBoolean)))
      {
        reportSchemaWarning("GrammarConflict", null, null);
        localSchemaGrammar = null;
      }
    }
    return localSchemaGrammar;
  }
  
  protected XSDocumentInfo constructTrees(Element paramElement, String paramString, XSDDescription paramXSDDescription, boolean paramBoolean)
  {
    if (paramElement == null) {
      return null;
    }
    String str1 = paramXSDDescription.getTargetNamespace();
    int i = paramXSDDescription.getContextType();
    XSDocumentInfo localXSDocumentInfo = null;
    try
    {
      localXSDocumentInfo = new XSDocumentInfo(paramElement, fAttributeChecker, fSymbolTable);
    }
    catch (XMLSchemaException localXMLSchemaException)
    {
      reportSchemaError(ELE_ERROR_CODES[i], new Object[] { paramString }, paramElement);
      return null;
    }
    if ((fTargetNamespace != null) && (fTargetNamespace.length() == 0))
    {
      reportSchemaWarning("EmptyTargetNamespace", new Object[] { paramString }, paramElement);
      fTargetNamespace = null;
    }
    int j;
    if (str1 != null)
    {
      j = 0;
      if ((i == 0) || (i == 1))
      {
        if (fTargetNamespace == null)
        {
          fTargetNamespace = str1;
          fIsChameleonSchema = true;
        }
        else if (str1 != fTargetNamespace)
        {
          reportSchemaError(NS_ERROR_CODES[i][j], new Object[] { str1, fTargetNamespace }, paramElement);
          return null;
        }
      }
      else if ((i != 3) && (str1 != fTargetNamespace))
      {
        reportSchemaError(NS_ERROR_CODES[i][j], new Object[] { str1, fTargetNamespace }, paramElement);
        return null;
      }
    }
    else if (fTargetNamespace != null)
    {
      if (i == 3)
      {
        paramXSDDescription.setTargetNamespace(fTargetNamespace);
        str1 = fTargetNamespace;
      }
      else
      {
        j = 1;
        reportSchemaError(NS_ERROR_CODES[i][j], new Object[] { str1, fTargetNamespace }, paramElement);
        return null;
      }
    }
    localXSDocumentInfo.addAllowedNS(fTargetNamespace);
    Object localObject1 = null;
    if (paramBoolean)
    {
      localObject2 = fGrammarBucket.getGrammar(fTargetNamespace);
      if (((SchemaGrammar)localObject2).isImmutable())
      {
        localObject1 = new SchemaGrammar((SchemaGrammar)localObject2);
        fGrammarBucket.putGrammar((SchemaGrammar)localObject1);
        updateImportListWith((SchemaGrammar)localObject1);
      }
      else
      {
        localObject1 = localObject2;
      }
      updateImportListFor((SchemaGrammar)localObject1);
    }
    else if ((i == 0) || (i == 1))
    {
      localObject1 = fGrammarBucket.getGrammar(fTargetNamespace);
    }
    else if ((fHonourAllSchemaLocations) && (i == 2))
    {
      localObject1 = findGrammar(paramXSDDescription, false);
      if (localObject1 == null)
      {
        localObject1 = new SchemaGrammar(fTargetNamespace, paramXSDDescription.makeClone(), fSymbolTable);
        fGrammarBucket.putGrammar((SchemaGrammar)localObject1);
      }
    }
    else
    {
      localObject1 = new SchemaGrammar(fTargetNamespace, paramXSDDescription.makeClone(), fSymbolTable);
      fGrammarBucket.putGrammar((SchemaGrammar)localObject1);
    }
    ((SchemaGrammar)localObject1).addDocument(null, (String)fDoc2SystemId.get(fSchemaElement));
    fDoc2XSDocumentMap.put(paramElement, localXSDocumentInfo);
    Object localObject2 = new Vector();
    Element localElement1 = paramElement;
    Element localElement2 = null;
    for (Element localElement3 = DOMUtil.getFirstChildElement(localElement1); localElement3 != null; localElement3 = DOMUtil.getNextSiblingElement(localElement3))
    {
      String str2 = null;
      String str3 = null;
      String str4 = DOMUtil.getLocalName(localElement3);
      short s = -1;
      boolean bool1 = false;
      if (!str4.equals(SchemaSymbols.ELT_ANNOTATION))
      {
        Element localElement4;
        String str5;
        Object localObject4;
        Object localObject5;
        if (str4.equals(SchemaSymbols.ELT_IMPORT))
        {
          s = 2;
          localObject3 = fAttributeChecker.checkAttributes(localElement3, true, localXSDocumentInfo);
          str3 = (String)localObject3[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
          str2 = (String)localObject3[XSAttributeChecker.ATTIDX_NAMESPACE];
          if (str2 != null) {
            str2 = fSymbolTable.addSymbol(str2);
          }
          localElement4 = DOMUtil.getFirstChildElement(localElement3);
          if (localElement4 != null)
          {
            str5 = DOMUtil.getLocalName(localElement4);
            if (str5.equals(SchemaSymbols.ELT_ANNOTATION)) {
              ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseAnnotationDecl(localElement4, (Object[])localObject3, true, localXSDocumentInfo));
            } else {
              reportSchemaError("s4s-elt-must-match.1", new Object[] { str4, "annotation?", str5 }, localElement3);
            }
            if (DOMUtil.getNextSiblingElement(localElement4) != null) {
              reportSchemaError("s4s-elt-must-match.1", new Object[] { str4, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(localElement4)) }, localElement3);
            }
          }
          else
          {
            str5 = DOMUtil.getSyntheticAnnotation(localElement3);
            if (str5 != null) {
              ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseSyntheticAnnotation(localElement3, str5, (Object[])localObject3, true, localXSDocumentInfo));
            }
          }
          fAttributeChecker.returnAttrArray((Object[])localObject3, localXSDocumentInfo);
          if (str2 == fTargetNamespace)
          {
            reportSchemaError(str2 != null ? "src-import.1.1" : "src-import.1.2", new Object[] { str2 }, localElement3);
            continue;
          }
          if (localXSDocumentInfo.isAllowedNS(str2))
          {
            if ((!fHonourAllSchemaLocations) && (!fNamespaceGrowth)) {
              continue;
            }
          }
          else {
            localXSDocumentInfo.addAllowedNS(str2);
          }
          str5 = null2EmptyString(fTargetNamespace);
          localObject4 = (Vector)fImportMap.get(str5);
          if (localObject4 == null)
          {
            fAllTNSs.addElement(str5);
            localObject4 = new Vector();
            fImportMap.put(str5, localObject4);
            ((Vector)localObject4).addElement(str2);
          }
          else if (!((Vector)localObject4).contains(str2))
          {
            ((Vector)localObject4).addElement(str2);
          }
          fSchemaGrammarDescription.reset();
          fSchemaGrammarDescription.setContextType((short)2);
          fSchemaGrammarDescription.setBaseSystemId(doc2SystemId(paramElement));
          fSchemaGrammarDescription.setLiteralSystemId(str3);
          fSchemaGrammarDescription.setLocationHints(new String[] { str3 });
          fSchemaGrammarDescription.setTargetNamespace(str2);
          localObject5 = findGrammar(fSchemaGrammarDescription, fNamespaceGrowth);
          if (localObject5 != null)
          {
            if (fNamespaceGrowth) {
              try
              {
                if (((SchemaGrammar)localObject5).getDocumentLocations().contains(XMLEntityManager.expandSystemId(str3, fSchemaGrammarDescription.getBaseSystemId(), false))) {
                  continue;
                }
                bool1 = true;
              }
              catch (URI.MalformedURIException localMalformedURIException2) {}
            }
            if ((!fHonourAllSchemaLocations) || (isExistingGrammar(fSchemaGrammarDescription, false))) {
              continue;
            }
          }
          localElement2 = resolveSchema(fSchemaGrammarDescription, false, localElement3, localObject5 == null);
        }
        else
        {
          if ((!str4.equals(SchemaSymbols.ELT_INCLUDE)) && (!str4.equals(SchemaSymbols.ELT_REDEFINE))) {
            break;
          }
          localObject3 = fAttributeChecker.checkAttributes(localElement3, true, localXSDocumentInfo);
          str3 = (String)localObject3[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
          if (str4.equals(SchemaSymbols.ELT_REDEFINE))
          {
            if (fRedefine2NSSupport == null) {
              fRedefine2NSSupport = new HashMap();
            }
            fRedefine2NSSupport.put(localElement3, new SchemaNamespaceSupport(fNamespaceSupport));
          }
          if (str4.equals(SchemaSymbols.ELT_INCLUDE))
          {
            localElement4 = DOMUtil.getFirstChildElement(localElement3);
            if (localElement4 != null)
            {
              str5 = DOMUtil.getLocalName(localElement4);
              if (str5.equals(SchemaSymbols.ELT_ANNOTATION)) {
                ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseAnnotationDecl(localElement4, (Object[])localObject3, true, localXSDocumentInfo));
              } else {
                reportSchemaError("s4s-elt-must-match.1", new Object[] { str4, "annotation?", str5 }, localElement3);
              }
              if (DOMUtil.getNextSiblingElement(localElement4) != null) {
                reportSchemaError("s4s-elt-must-match.1", new Object[] { str4, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(localElement4)) }, localElement3);
              }
            }
            else
            {
              str5 = DOMUtil.getSyntheticAnnotation(localElement3);
              if (str5 != null) {
                ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseSyntheticAnnotation(localElement3, str5, (Object[])localObject3, true, localXSDocumentInfo));
              }
            }
          }
          else
          {
            for (localElement4 = DOMUtil.getFirstChildElement(localElement3); localElement4 != null; localElement4 = DOMUtil.getNextSiblingElement(localElement4))
            {
              str5 = DOMUtil.getLocalName(localElement4);
              if (str5.equals(SchemaSymbols.ELT_ANNOTATION))
              {
                ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseAnnotationDecl(localElement4, (Object[])localObject3, true, localXSDocumentInfo));
                DOMUtil.setHidden(localElement4, fHiddenNodes);
              }
              else
              {
                localObject4 = DOMUtil.getSyntheticAnnotation(localElement3);
                if (localObject4 != null) {
                  ((SchemaGrammar)localObject1).addAnnotation(fElementTraverser.traverseSyntheticAnnotation(localElement3, (String)localObject4, (Object[])localObject3, true, localXSDocumentInfo));
                }
              }
            }
          }
          fAttributeChecker.returnAttrArray((Object[])localObject3, localXSDocumentInfo);
          if (str3 == null) {
            reportSchemaError("s4s-att-must-appear", new Object[] { "<include> or <redefine>", "schemaLocation" }, localElement3);
          }
          boolean bool2 = false;
          s = 0;
          if (str4.equals(SchemaSymbols.ELT_REDEFINE))
          {
            bool2 = nonAnnotationContent(localElement3);
            s = 1;
          }
          fSchemaGrammarDescription.reset();
          fSchemaGrammarDescription.setContextType(s);
          fSchemaGrammarDescription.setBaseSystemId(doc2SystemId(paramElement));
          fSchemaGrammarDescription.setLocationHints(new String[] { str3 });
          fSchemaGrammarDescription.setTargetNamespace(str1);
          boolean bool3 = false;
          localObject4 = resolveSchemaSource(fSchemaGrammarDescription, bool2, localElement3, true);
          if ((fNamespaceGrowth) && (s == 0)) {
            try
            {
              localObject5 = XMLEntityManager.expandSystemId(((XMLInputSource)localObject4).getSystemId(), ((XMLInputSource)localObject4).getBaseSystemId(), false);
              bool3 = ((SchemaGrammar)localObject1).getDocumentLocations().contains((String)localObject5);
            }
            catch (URI.MalformedURIException localMalformedURIException1) {}
          }
          if (!bool3)
          {
            localElement2 = resolveSchema((XMLInputSource)localObject4, fSchemaGrammarDescription, bool2, localElement3);
            str2 = fTargetNamespace;
          }
          else
          {
            fLastSchemaWasDuplicate = true;
          }
        }
        Object localObject3 = null;
        if (fLastSchemaWasDuplicate) {
          localObject3 = localElement2 == null ? null : (XSDocumentInfo)fDoc2XSDocumentMap.get(localElement2);
        } else {
          localObject3 = constructTrees(localElement2, str3, fSchemaGrammarDescription, bool1);
        }
        if ((str4.equals(SchemaSymbols.ELT_REDEFINE)) && (localObject3 != null))
        {
          if (fRedefine2XSDMap == null) {
            fRedefine2XSDMap = new HashMap();
          }
          fRedefine2XSDMap.put(localElement3, localObject3);
        }
        if (localElement2 != null)
        {
          if (localObject3 != null) {
            ((Vector)localObject2).addElement(localObject3);
          }
          localElement2 = null;
        }
      }
    }
    fDependencyMap.put(localXSDocumentInfo, localObject2);
    return localXSDocumentInfo;
  }
  
  private boolean isExistingGrammar(XSDDescription paramXSDDescription, boolean paramBoolean)
  {
    SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(paramXSDDescription.getTargetNamespace());
    if (localSchemaGrammar == null) {
      return findGrammar(paramXSDDescription, paramBoolean) != null;
    }
    if (localSchemaGrammar.isImmutable()) {
      return true;
    }
    try
    {
      return localSchemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(paramXSDDescription.getLiteralSystemId(), paramXSDDescription.getBaseSystemId(), false));
    }
    catch (URI.MalformedURIException localMalformedURIException) {}
    return false;
  }
  
  private void updateImportListFor(SchemaGrammar paramSchemaGrammar)
  {
    Vector localVector = paramSchemaGrammar.getImportedGrammars();
    if (localVector != null) {
      for (int i = 0; i < localVector.size(); i++)
      {
        SchemaGrammar localSchemaGrammar1 = (SchemaGrammar)localVector.elementAt(i);
        SchemaGrammar localSchemaGrammar2 = fGrammarBucket.getGrammar(localSchemaGrammar1.getTargetNamespace());
        if ((localSchemaGrammar2 != null) && (localSchemaGrammar1 != localSchemaGrammar2)) {
          localVector.set(i, localSchemaGrammar2);
        }
      }
    }
  }
  
  private void updateImportListWith(SchemaGrammar paramSchemaGrammar)
  {
    SchemaGrammar[] arrayOfSchemaGrammar = fGrammarBucket.getGrammars();
    for (int i = 0; i < arrayOfSchemaGrammar.length; i++)
    {
      SchemaGrammar localSchemaGrammar1 = arrayOfSchemaGrammar[i];
      if (localSchemaGrammar1 != paramSchemaGrammar)
      {
        Vector localVector = localSchemaGrammar1.getImportedGrammars();
        if (localVector != null) {
          for (int j = 0; j < localVector.size(); j++)
          {
            SchemaGrammar localSchemaGrammar2 = (SchemaGrammar)localVector.elementAt(j);
            if (null2EmptyString(localSchemaGrammar2.getTargetNamespace()).equals(null2EmptyString(paramSchemaGrammar.getTargetNamespace())))
            {
              if (localSchemaGrammar2 == paramSchemaGrammar) {
                break;
              }
              localVector.set(j, paramSchemaGrammar);
              break;
            }
          }
        }
      }
    }
  }
  
  protected void buildGlobalNameRegistries()
  {
    registryEmpty = false;
    Stack localStack = new Stack();
    localStack.push(fRoot);
    while (!localStack.empty())
    {
      XSDocumentInfo localXSDocumentInfo = (XSDocumentInfo)localStack.pop();
      Element localElement1 = fSchemaElement;
      if (!DOMUtil.isHidden(localElement1, fHiddenNodes))
      {
        Element localElement2 = localElement1;
        int i = 1;
        for (Object localObject1 = DOMUtil.getFirstChildElement(localElement2); localObject1 != null; localObject1 = DOMUtil.getNextSiblingElement((Node)localObject1)) {
          if (!DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_ANNOTATION)) {
            if ((DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_INCLUDE)) || (DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_IMPORT)))
            {
              if (i == 0) {
                reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName((Node)localObject1) }, (Element)localObject1);
              }
              DOMUtil.setHidden((Node)localObject1, fHiddenNodes);
            }
            else
            {
              Object localObject2;
              String str1;
              String str2;
              if (DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_REDEFINE))
              {
                if (i == 0) {
                  reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName((Node)localObject1) }, (Element)localObject1);
                }
                for (localObject2 = DOMUtil.getFirstChildElement((Node)localObject1); localObject2 != null; localObject2 = DOMUtil.getNextSiblingElement((Node)localObject2))
                {
                  str1 = DOMUtil.getAttrValue((Element)localObject2, SchemaSymbols.ATT_NAME);
                  if (str1.length() != 0)
                  {
                    str2 = fTargetNamespace + "," + str1;
                    String str3 = DOMUtil.getLocalName((Node)localObject2);
                    String str4;
                    if (str3.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP))
                    {
                      checkForDuplicateNames(str2, 2, fUnparsedAttributeGroupRegistry, fUnparsedAttributeGroupRegistrySub, (Element)localObject2, localXSDocumentInfo);
                      str4 = DOMUtil.getAttrValue((Element)localObject2, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                      renameRedefiningComponents(localXSDocumentInfo, (Element)localObject2, SchemaSymbols.ELT_ATTRIBUTEGROUP, str1, str4);
                    }
                    else if ((str3.equals(SchemaSymbols.ELT_COMPLEXTYPE)) || (str3.equals(SchemaSymbols.ELT_SIMPLETYPE)))
                    {
                      checkForDuplicateNames(str2, 7, fUnparsedTypeRegistry, fUnparsedTypeRegistrySub, (Element)localObject2, localXSDocumentInfo);
                      str4 = DOMUtil.getAttrValue((Element)localObject2, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                      if (str3.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                        renameRedefiningComponents(localXSDocumentInfo, (Element)localObject2, SchemaSymbols.ELT_COMPLEXTYPE, str1, str4);
                      } else {
                        renameRedefiningComponents(localXSDocumentInfo, (Element)localObject2, SchemaSymbols.ELT_SIMPLETYPE, str1, str4);
                      }
                    }
                    else if (str3.equals(SchemaSymbols.ELT_GROUP))
                    {
                      checkForDuplicateNames(str2, 4, fUnparsedGroupRegistry, fUnparsedGroupRegistrySub, (Element)localObject2, localXSDocumentInfo);
                      str4 = DOMUtil.getAttrValue((Element)localObject2, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
                      renameRedefiningComponents(localXSDocumentInfo, (Element)localObject2, SchemaSymbols.ELT_GROUP, str1, str4);
                    }
                  }
                }
              }
              else
              {
                i = 0;
                localObject2 = DOMUtil.getAttrValue((Element)localObject1, SchemaSymbols.ATT_NAME);
                if (((String)localObject2).length() != 0)
                {
                  str1 = fTargetNamespace + "," + (String)localObject2;
                  str2 = DOMUtil.getLocalName((Node)localObject1);
                  if (str2.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                    checkForDuplicateNames(str1, 1, fUnparsedAttributeRegistry, fUnparsedAttributeRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  } else if (str2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    checkForDuplicateNames(str1, 2, fUnparsedAttributeGroupRegistry, fUnparsedAttributeGroupRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  } else if ((str2.equals(SchemaSymbols.ELT_COMPLEXTYPE)) || (str2.equals(SchemaSymbols.ELT_SIMPLETYPE))) {
                    checkForDuplicateNames(str1, 7, fUnparsedTypeRegistry, fUnparsedTypeRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  } else if (str2.equals(SchemaSymbols.ELT_ELEMENT)) {
                    checkForDuplicateNames(str1, 3, fUnparsedElementRegistry, fUnparsedElementRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  } else if (str2.equals(SchemaSymbols.ELT_GROUP)) {
                    checkForDuplicateNames(str1, 4, fUnparsedGroupRegistry, fUnparsedGroupRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  } else if (str2.equals(SchemaSymbols.ELT_NOTATION)) {
                    checkForDuplicateNames(str1, 6, fUnparsedNotationRegistry, fUnparsedNotationRegistrySub, (Element)localObject1, localXSDocumentInfo);
                  }
                }
              }
            }
          }
        }
        DOMUtil.setHidden(localElement1, fHiddenNodes);
        localObject1 = (Vector)fDependencyMap.get(localXSDocumentInfo);
        for (int j = 0; j < ((Vector)localObject1).size(); j++) {
          localStack.push(((Vector)localObject1).elementAt(j));
        }
      }
    }
  }
  
  protected void traverseSchemas(ArrayList paramArrayList)
  {
    setSchemasVisible(fRoot);
    Stack localStack = new Stack();
    localStack.push(fRoot);
    while (!localStack.empty())
    {
      XSDocumentInfo localXSDocumentInfo = (XSDocumentInfo)localStack.pop();
      Element localElement1 = fSchemaElement;
      SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(fTargetNamespace);
      if (!DOMUtil.isHidden(localElement1, fHiddenNodes))
      {
        Element localElement2 = localElement1;
        int i = 0;
        for (Object localObject = DOMUtil.getFirstVisibleChildElement(localElement2, fHiddenNodes); localObject != null; localObject = DOMUtil.getNextVisibleSiblingElement((Node)localObject, fHiddenNodes))
        {
          DOMUtil.setHidden((Node)localObject, fHiddenNodes);
          String str1 = DOMUtil.getLocalName((Node)localObject);
          if (DOMUtil.getLocalName((Node)localObject).equals(SchemaSymbols.ELT_REDEFINE))
          {
            localXSDocumentInfo.backupNSSupport(fRedefine2NSSupport != null ? (SchemaNamespaceSupport)fRedefine2NSSupport.get(localObject) : null);
            for (Element localElement3 = DOMUtil.getFirstVisibleChildElement((Node)localObject, fHiddenNodes); localElement3 != null; localElement3 = DOMUtil.getNextVisibleSiblingElement(localElement3, fHiddenNodes))
            {
              String str2 = DOMUtil.getLocalName(localElement3);
              DOMUtil.setHidden(localElement3, fHiddenNodes);
              if (str2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                fAttributeGroupTraverser.traverseGlobal(localElement3, localXSDocumentInfo, localSchemaGrammar);
              } else if (str2.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                fComplexTypeTraverser.traverseGlobal(localElement3, localXSDocumentInfo, localSchemaGrammar);
              } else if (str2.equals(SchemaSymbols.ELT_GROUP)) {
                fGroupTraverser.traverseGlobal(localElement3, localXSDocumentInfo, localSchemaGrammar);
              } else if (str2.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                fSimpleTypeTraverser.traverseGlobal(localElement3, localXSDocumentInfo, localSchemaGrammar);
              } else {
                reportSchemaError("s4s-elt-must-match.1", new Object[] { DOMUtil.getLocalName((Node)localObject), "(annotation | (simpleType | complexType | group | attributeGroup))*", str2 }, localElement3);
              }
            }
            localXSDocumentInfo.restoreNSSupport();
          }
          else if (str1.equals(SchemaSymbols.ELT_ATTRIBUTE))
          {
            fAttributeTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP))
          {
            fAttributeGroupTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_COMPLEXTYPE))
          {
            fComplexTypeTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_ELEMENT))
          {
            fElementTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_GROUP))
          {
            fGroupTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_NOTATION))
          {
            fNotationTraverser.traverse((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_SIMPLETYPE))
          {
            fSimpleTypeTraverser.traverseGlobal((Element)localObject, localXSDocumentInfo, localSchemaGrammar);
          }
          else if (str1.equals(SchemaSymbols.ELT_ANNOTATION))
          {
            localSchemaGrammar.addAnnotation(fElementTraverser.traverseAnnotationDecl((Element)localObject, localXSDocumentInfo.getSchemaAttrs(), true, localXSDocumentInfo));
            i = 1;
          }
          else
          {
            reportSchemaError("s4s-elt-invalid-content.1", new Object[] { SchemaSymbols.ELT_SCHEMA, DOMUtil.getLocalName((Node)localObject) }, (Element)localObject);
          }
        }
        if (i == 0)
        {
          localObject = DOMUtil.getSyntheticAnnotation(localElement2);
          if (localObject != null) {
            localSchemaGrammar.addAnnotation(fElementTraverser.traverseSyntheticAnnotation(localElement2, (String)localObject, localXSDocumentInfo.getSchemaAttrs(), true, localXSDocumentInfo));
          }
        }
        if (paramArrayList != null)
        {
          localObject = localXSDocumentInfo.getAnnotations();
          if (localObject != null)
          {
            paramArrayList.add(doc2SystemId(localElement1));
            paramArrayList.add(localObject);
          }
        }
        localXSDocumentInfo.returnSchemaAttrs();
        DOMUtil.setHidden(localElement1, fHiddenNodes);
        localObject = (Vector)fDependencyMap.get(localXSDocumentInfo);
        for (int j = 0; j < ((Vector)localObject).size(); j++) {
          localStack.push(((Vector)localObject).elementAt(j));
        }
      }
    }
  }
  
  private final boolean needReportTNSError(String paramString)
  {
    if (fReportedTNS == null) {
      fReportedTNS = new Vector();
    } else if (fReportedTNS.contains(paramString)) {
      return false;
    }
    fReportedTNS.addElement(paramString);
    return true;
  }
  
  void addGlobalAttributeDecl(XSAttributeDecl paramXSAttributeDecl)
  {
    String str1 = paramXSAttributeDecl.getNamespace();
    String str2 = str1 + "," + paramXSAttributeDecl.getName();
    if (fGlobalAttrDecls.get(str2) == null) {
      fGlobalAttrDecls.put(str2, paramXSAttributeDecl);
    }
  }
  
  void addGlobalAttributeGroupDecl(XSAttributeGroupDecl paramXSAttributeGroupDecl)
  {
    String str1 = paramXSAttributeGroupDecl.getNamespace();
    String str2 = str1 + "," + paramXSAttributeGroupDecl.getName();
    if (fGlobalAttrGrpDecls.get(str2) == null) {
      fGlobalAttrGrpDecls.put(str2, paramXSAttributeGroupDecl);
    }
  }
  
  void addGlobalElementDecl(XSElementDecl paramXSElementDecl)
  {
    String str1 = paramXSElementDecl.getNamespace();
    String str2 = str1 + "," + paramXSElementDecl.getName();
    if (fGlobalElemDecls.get(str2) == null) {
      fGlobalElemDecls.put(str2, paramXSElementDecl);
    }
  }
  
  void addGlobalGroupDecl(XSGroupDecl paramXSGroupDecl)
  {
    String str1 = paramXSGroupDecl.getNamespace();
    String str2 = str1 + "," + paramXSGroupDecl.getName();
    if (fGlobalGroupDecls.get(str2) == null) {
      fGlobalGroupDecls.put(str2, paramXSGroupDecl);
    }
  }
  
  void addGlobalNotationDecl(XSNotationDecl paramXSNotationDecl)
  {
    String str1 = paramXSNotationDecl.getNamespace();
    String str2 = str1 + "," + paramXSNotationDecl.getName();
    if (fGlobalNotationDecls.get(str2) == null) {
      fGlobalNotationDecls.put(str2, paramXSNotationDecl);
    }
  }
  
  void addGlobalTypeDecl(XSTypeDefinition paramXSTypeDefinition)
  {
    String str1 = paramXSTypeDefinition.getNamespace();
    String str2 = str1 + "," + paramXSTypeDefinition.getName();
    if (fGlobalTypeDecls.get(str2) == null) {
      fGlobalTypeDecls.put(str2, paramXSTypeDefinition);
    }
  }
  
  void addIDConstraintDecl(IdentityConstraint paramIdentityConstraint)
  {
    String str1 = paramIdentityConstraint.getNamespace();
    String str2 = str1 + "," + paramIdentityConstraint.getIdentityConstraintName();
    if (fGlobalIDConstraintDecls.get(str2) == null) {
      fGlobalIDConstraintDecls.put(str2, paramIdentityConstraint);
    }
  }
  
  private XSAttributeDecl getGlobalAttributeDecl(String paramString)
  {
    return (XSAttributeDecl)fGlobalAttrDecls.get(paramString);
  }
  
  private XSAttributeGroupDecl getGlobalAttributeGroupDecl(String paramString)
  {
    return (XSAttributeGroupDecl)fGlobalAttrGrpDecls.get(paramString);
  }
  
  private XSElementDecl getGlobalElementDecl(String paramString)
  {
    return (XSElementDecl)fGlobalElemDecls.get(paramString);
  }
  
  private XSGroupDecl getGlobalGroupDecl(String paramString)
  {
    return (XSGroupDecl)fGlobalGroupDecls.get(paramString);
  }
  
  private XSNotationDecl getGlobalNotationDecl(String paramString)
  {
    return (XSNotationDecl)fGlobalNotationDecls.get(paramString);
  }
  
  private XSTypeDefinition getGlobalTypeDecl(String paramString)
  {
    return (XSTypeDefinition)fGlobalTypeDecls.get(paramString);
  }
  
  private IdentityConstraint getIDConstraintDecl(String paramString)
  {
    return (IdentityConstraint)fGlobalIDConstraintDecls.get(paramString);
  }
  
  protected Object getGlobalDecl(XSDocumentInfo paramXSDocumentInfo, int paramInt, QName paramQName, Element paramElement)
  {
    if ((uri != null) && (uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) && (paramInt == 7))
    {
      localObject1 = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(localpart);
      if (localObject1 != null) {
        return localObject1;
      }
    }
    if ((!paramXSDocumentInfo.isAllowedNS(uri)) && (paramXSDocumentInfo.needReportTNSError(uri)))
    {
      localObject1 = uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
      reportSchemaError((String)localObject1, new Object[] { fDoc2SystemId.get(fSchemaElement), uri, rawname }, paramElement);
    }
    Object localObject1 = fGrammarBucket.getGrammar(uri);
    if (localObject1 == null)
    {
      if (needReportTNSError(uri)) {
        reportSchemaError("src-resolve", new Object[] { rawname, COMP_TYPE[paramInt] }, paramElement);
      }
      return null;
    }
    Object localObject2 = getGlobalDeclFromGrammar((SchemaGrammar)localObject1, paramInt, localpart);
    String str1 = uri + "," + localpart;
    if (!fTolerateDuplicates)
    {
      if (localObject2 != null) {
        return localObject2;
      }
    }
    else
    {
      localObject3 = getGlobalDecl(str1, paramInt);
      if (localObject3 != null) {
        return localObject3;
      }
    }
    Object localObject3 = null;
    Element localElement = null;
    XSDocumentInfo localXSDocumentInfo = null;
    switch (paramInt)
    {
    case 1: 
      localElement = getElementFromMap(fUnparsedAttributeRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedAttributeRegistrySub, str1);
      break;
    case 2: 
      localElement = getElementFromMap(fUnparsedAttributeGroupRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedAttributeGroupRegistrySub, str1);
      break;
    case 3: 
      localElement = getElementFromMap(fUnparsedElementRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedElementRegistrySub, str1);
      break;
    case 4: 
      localElement = getElementFromMap(fUnparsedGroupRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedGroupRegistrySub, str1);
      break;
    case 5: 
      localElement = getElementFromMap(fUnparsedIdentityConstraintRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedIdentityConstraintRegistrySub, str1);
      break;
    case 6: 
      localElement = getElementFromMap(fUnparsedNotationRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedNotationRegistrySub, str1);
      break;
    case 7: 
      localElement = getElementFromMap(fUnparsedTypeRegistry, str1);
      localXSDocumentInfo = getDocInfoFromMap(fUnparsedTypeRegistrySub, str1);
      break;
    default: 
      reportSchemaError("Internal-Error", new Object[] { "XSDHandler asked to locate component of type " + paramInt + "; it does not recognize this type!" }, paramElement);
    }
    if (localElement == null)
    {
      if (localObject2 == null) {
        reportSchemaError("src-resolve", new Object[] { rawname, COMP_TYPE[paramInt] }, paramElement);
      }
      return localObject2;
    }
    localObject3 = findXSDocumentForDecl(paramXSDocumentInfo, localElement, localXSDocumentInfo);
    String str2;
    if (localObject3 == null)
    {
      if (localObject2 == null)
      {
        str2 = uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
        reportSchemaError(str2, new Object[] { fDoc2SystemId.get(fSchemaElement), uri, rawname }, paramElement);
      }
      return localObject2;
    }
    if (DOMUtil.isHidden(localElement, fHiddenNodes))
    {
      if (localObject2 == null)
      {
        str2 = CIRCULAR_CODES[paramInt];
        if ((paramInt == 7) && (SchemaSymbols.ELT_COMPLEXTYPE.equals(DOMUtil.getLocalName(localElement)))) {
          str2 = "ct-props-correct.3";
        }
        reportSchemaError(str2, new Object[] { prefix + ":" + localpart }, paramElement);
      }
      return localObject2;
    }
    return traverseGlobalDecl(paramInt, localElement, (XSDocumentInfo)localObject3, (SchemaGrammar)localObject1);
  }
  
  protected Object getGlobalDecl(String paramString, int paramInt)
  {
    Object localObject = null;
    switch (paramInt)
    {
    case 1: 
      localObject = getGlobalAttributeDecl(paramString);
      break;
    case 2: 
      localObject = getGlobalAttributeGroupDecl(paramString);
      break;
    case 3: 
      localObject = getGlobalElementDecl(paramString);
      break;
    case 4: 
      localObject = getGlobalGroupDecl(paramString);
      break;
    case 5: 
      localObject = getIDConstraintDecl(paramString);
      break;
    case 6: 
      localObject = getGlobalNotationDecl(paramString);
      break;
    case 7: 
      localObject = getGlobalTypeDecl(paramString);
    }
    return localObject;
  }
  
  protected Object getGlobalDeclFromGrammar(SchemaGrammar paramSchemaGrammar, int paramInt, String paramString)
  {
    Object localObject = null;
    switch (paramInt)
    {
    case 1: 
      localObject = paramSchemaGrammar.getGlobalAttributeDecl(paramString);
      break;
    case 2: 
      localObject = paramSchemaGrammar.getGlobalAttributeGroupDecl(paramString);
      break;
    case 3: 
      localObject = paramSchemaGrammar.getGlobalElementDecl(paramString);
      break;
    case 4: 
      localObject = paramSchemaGrammar.getGlobalGroupDecl(paramString);
      break;
    case 5: 
      localObject = paramSchemaGrammar.getIDConstraintDecl(paramString);
      break;
    case 6: 
      localObject = paramSchemaGrammar.getGlobalNotationDecl(paramString);
      break;
    case 7: 
      localObject = paramSchemaGrammar.getGlobalTypeDecl(paramString);
    }
    return localObject;
  }
  
  protected Object getGlobalDeclFromGrammar(SchemaGrammar paramSchemaGrammar, int paramInt, String paramString1, String paramString2)
  {
    Object localObject = null;
    switch (paramInt)
    {
    case 1: 
      localObject = paramSchemaGrammar.getGlobalAttributeDecl(paramString1, paramString2);
      break;
    case 2: 
      localObject = paramSchemaGrammar.getGlobalAttributeGroupDecl(paramString1, paramString2);
      break;
    case 3: 
      localObject = paramSchemaGrammar.getGlobalElementDecl(paramString1, paramString2);
      break;
    case 4: 
      localObject = paramSchemaGrammar.getGlobalGroupDecl(paramString1, paramString2);
      break;
    case 5: 
      localObject = paramSchemaGrammar.getIDConstraintDecl(paramString1, paramString2);
      break;
    case 6: 
      localObject = paramSchemaGrammar.getGlobalNotationDecl(paramString1, paramString2);
      break;
    case 7: 
      localObject = paramSchemaGrammar.getGlobalTypeDecl(paramString1, paramString2);
    }
    return localObject;
  }
  
  protected Object traverseGlobalDecl(int paramInt, Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object localObject = null;
    DOMUtil.setHidden(paramElement, fHiddenNodes);
    SchemaNamespaceSupport localSchemaNamespaceSupport = null;
    Element localElement = DOMUtil.getParent(paramElement);
    if (DOMUtil.getLocalName(localElement).equals(SchemaSymbols.ELT_REDEFINE)) {
      localSchemaNamespaceSupport = fRedefine2NSSupport != null ? (SchemaNamespaceSupport)fRedefine2NSSupport.get(localElement) : null;
    }
    paramXSDocumentInfo.backupNSSupport(localSchemaNamespaceSupport);
    switch (paramInt)
    {
    case 7: 
      if (DOMUtil.getLocalName(paramElement).equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
        localObject = fComplexTypeTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      } else {
        localObject = fSimpleTypeTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      }
      break;
    case 1: 
      localObject = fAttributeTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      break;
    case 3: 
      localObject = fElementTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      break;
    case 2: 
      localObject = fAttributeGroupTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      break;
    case 4: 
      localObject = fGroupTraverser.traverseGlobal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      break;
    case 6: 
      localObject = fNotationTraverser.traverse(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
      break;
    }
    paramXSDocumentInfo.restoreNSSupport();
    return localObject;
  }
  
  public String schemaDocument2SystemId(XSDocumentInfo paramXSDocumentInfo)
  {
    return (String)fDoc2SystemId.get(fSchemaElement);
  }
  
  Object getGrpOrAttrGrpRedefinedByRestriction(int paramInt, QName paramQName, XSDocumentInfo paramXSDocumentInfo, Element paramElement)
  {
    String str1 = "," + localpart;
    String str2 = null;
    switch (paramInt)
    {
    case 2: 
      str2 = (String)fRedefinedRestrictedAttributeGroupRegistry.get(str1);
      break;
    case 4: 
      str2 = (String)fRedefinedRestrictedGroupRegistry.get(str1);
      break;
    default: 
      return null;
    }
    if (str2 == null) {
      return null;
    }
    int i = str2.indexOf(",");
    QName localQName = new QName(XMLSymbols.EMPTY_STRING, str2.substring(i + 1), str2.substring(i), i == 0 ? null : str2.substring(0, i));
    Object localObject = getGlobalDecl(paramXSDocumentInfo, paramInt, localQName, paramElement);
    if (localObject == null)
    {
      switch (paramInt)
      {
      case 2: 
        reportSchemaError("src-redefine.7.2.1", new Object[] { localpart }, paramElement);
        break;
      case 4: 
        reportSchemaError("src-redefine.6.2.1", new Object[] { localpart }, paramElement);
      }
      return null;
    }
    return localObject;
  }
  
  protected void resolveKeyRefs()
  {
    for (int i = 0; i < fKeyrefStackPos; i++)
    {
      XSDocumentInfo localXSDocumentInfo = fKeyrefsMapXSDocumentInfo[i];
      fNamespaceSupport.makeGlobal();
      fNamespaceSupport.setEffectiveContext(fKeyrefNamespaceContext[i]);
      SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(fTargetNamespace);
      DOMUtil.setHidden(fKeyrefs[i], fHiddenNodes);
      fKeyrefTraverser.traverse(fKeyrefs[i], fKeyrefElems[i], localXSDocumentInfo, localSchemaGrammar);
    }
  }
  
  protected Map getIDRegistry()
  {
    return fUnparsedIdentityConstraintRegistry;
  }
  
  protected Map getIDRegistry_sub()
  {
    return fUnparsedIdentityConstraintRegistrySub;
  }
  
  protected void storeKeyRef(Element paramElement, XSDocumentInfo paramXSDocumentInfo, XSElementDecl paramXSElementDecl)
  {
    String str = DOMUtil.getAttrValue(paramElement, SchemaSymbols.ATT_NAME);
    Object localObject;
    if (str.length() != 0)
    {
      localObject = fTargetNamespace + "," + str;
      checkForDuplicateNames((String)localObject, 5, fUnparsedIdentityConstraintRegistry, fUnparsedIdentityConstraintRegistrySub, paramElement, paramXSDocumentInfo);
    }
    if (fKeyrefStackPos == fKeyrefs.length)
    {
      localObject = new Element[fKeyrefStackPos + 2];
      System.arraycopy(fKeyrefs, 0, localObject, 0, fKeyrefStackPos);
      fKeyrefs = ((Element[])localObject);
      XSElementDecl[] arrayOfXSElementDecl = new XSElementDecl[fKeyrefStackPos + 2];
      System.arraycopy(fKeyrefElems, 0, arrayOfXSElementDecl, 0, fKeyrefStackPos);
      fKeyrefElems = arrayOfXSElementDecl;
      String[][] arrayOfString = new String[fKeyrefStackPos + 2][];
      System.arraycopy(fKeyrefNamespaceContext, 0, arrayOfString, 0, fKeyrefStackPos);
      fKeyrefNamespaceContext = arrayOfString;
      XSDocumentInfo[] arrayOfXSDocumentInfo = new XSDocumentInfo[fKeyrefStackPos + 2];
      System.arraycopy(fKeyrefsMapXSDocumentInfo, 0, arrayOfXSDocumentInfo, 0, fKeyrefStackPos);
      fKeyrefsMapXSDocumentInfo = arrayOfXSDocumentInfo;
    }
    fKeyrefs[fKeyrefStackPos] = paramElement;
    fKeyrefElems[fKeyrefStackPos] = paramXSElementDecl;
    fKeyrefNamespaceContext[fKeyrefStackPos] = fNamespaceSupport.getEffectiveLocalContext();
    fKeyrefsMapXSDocumentInfo[(fKeyrefStackPos++)] = paramXSDocumentInfo;
  }
  
  private Element resolveSchema(XSDDescription paramXSDDescription, boolean paramBoolean1, Element paramElement, boolean paramBoolean2)
  {
    XMLInputSource localXMLInputSource = null;
    try
    {
      Map localMap = paramBoolean2 ? fLocationPairs : Collections.emptyMap();
      localXMLInputSource = XMLSchemaLoader.resolveDocument(paramXSDDescription, localMap, fEntityManager);
    }
    catch (IOException localIOException)
    {
      if (paramBoolean1) {
        reportSchemaError("schema_reference.4", new Object[] { paramXSDDescription.getLocationHints()[0] }, paramElement);
      } else {
        reportSchemaWarning("schema_reference.4", new Object[] { paramXSDDescription.getLocationHints()[0] }, paramElement);
      }
    }
    if ((localXMLInputSource instanceof DOMInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (DOMInputSource)localXMLInputSource, paramBoolean1, paramXSDDescription.getContextType(), paramElement);
    }
    if ((localXMLInputSource instanceof SAXInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (SAXInputSource)localXMLInputSource, paramBoolean1, paramXSDDescription.getContextType(), paramElement);
    }
    if ((localXMLInputSource instanceof StAXInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (StAXInputSource)localXMLInputSource, paramBoolean1, paramXSDDescription.getContextType(), paramElement);
    }
    if ((localXMLInputSource instanceof XSInputSource)) {
      return getSchemaDocument((XSInputSource)localXMLInputSource, paramXSDDescription);
    }
    return getSchemaDocument(paramXSDDescription.getTargetNamespace(), localXMLInputSource, paramBoolean1, paramXSDDescription.getContextType(), paramElement);
  }
  
  private Element resolveSchema(XMLInputSource paramXMLInputSource, XSDDescription paramXSDDescription, boolean paramBoolean, Element paramElement)
  {
    if ((paramXMLInputSource instanceof DOMInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (DOMInputSource)paramXMLInputSource, paramBoolean, paramXSDDescription.getContextType(), paramElement);
    }
    if ((paramXMLInputSource instanceof SAXInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (SAXInputSource)paramXMLInputSource, paramBoolean, paramXSDDescription.getContextType(), paramElement);
    }
    if ((paramXMLInputSource instanceof StAXInputSource)) {
      return getSchemaDocument(paramXSDDescription.getTargetNamespace(), (StAXInputSource)paramXMLInputSource, paramBoolean, paramXSDDescription.getContextType(), paramElement);
    }
    if ((paramXMLInputSource instanceof XSInputSource)) {
      return getSchemaDocument((XSInputSource)paramXMLInputSource, paramXSDDescription);
    }
    return getSchemaDocument(paramXSDDescription.getTargetNamespace(), paramXMLInputSource, paramBoolean, paramXSDDescription.getContextType(), paramElement);
  }
  
  private XMLInputSource resolveSchemaSource(XSDDescription paramXSDDescription, boolean paramBoolean1, Element paramElement, boolean paramBoolean2)
  {
    XMLInputSource localXMLInputSource = null;
    try
    {
      Map localMap = paramBoolean2 ? fLocationPairs : Collections.emptyMap();
      localXMLInputSource = XMLSchemaLoader.resolveDocument(paramXSDDescription, localMap, fEntityManager);
    }
    catch (IOException localIOException)
    {
      if (paramBoolean1) {
        reportSchemaError("schema_reference.4", new Object[] { paramXSDDescription.getLocationHints()[0] }, paramElement);
      } else {
        reportSchemaWarning("schema_reference.4", new Object[] { paramXSDDescription.getLocationHints()[0] }, paramElement);
      }
    }
    return localXMLInputSource;
  }
  
  private Element getSchemaDocument(String paramString, XMLInputSource paramXMLInputSource, boolean paramBoolean, short paramShort, Element paramElement)
  {
    boolean bool = true;
    Object localObject1 = null;
    Element localElement = null;
    try
    {
      if ((paramXMLInputSource != null) && ((paramXMLInputSource.getSystemId() != null) || (paramXMLInputSource.getByteStream() != null) || (paramXMLInputSource.getCharacterStream() != null)))
      {
        XSDKey localXSDKey = null;
        String str = null;
        if (paramShort != 3)
        {
          str = XMLEntityManager.expandSystemId(paramXMLInputSource.getSystemId(), paramXMLInputSource.getBaseSystemId(), false);
          localXSDKey = new XSDKey(str, paramShort, paramString);
          if ((localElement = (Element)fTraversed.get(localXSDKey)) != null)
          {
            fLastSchemaWasDuplicate = true;
            return localElement;
          }
          if ((paramShort == 2) || (paramShort == 0) || (paramShort == 1))
          {
            localObject2 = SecuritySupport.checkAccess(str, fAccessExternalSchema, "all");
            if (localObject2 != null) {
              reportSchemaFatalError("schema_reference.access", new Object[] { SecuritySupport.sanitizePath(str), localObject2 }, paramElement);
            }
          }
        }
        fSchemaParser.parse(paramXMLInputSource);
        Object localObject2 = fSchemaParser.getDocument();
        localElement = localObject2 != null ? DOMUtil.getRoot((Document)localObject2) : null;
        return getSchemaDocument0(localXSDKey, str, localElement);
      }
      bool = false;
    }
    catch (IOException localIOException)
    {
      localObject1 = localIOException;
    }
    return getSchemaDocument1(paramBoolean, bool, paramXMLInputSource, paramElement, (IOException)localObject1);
  }
  
  private Element getSchemaDocument(String paramString, SAXInputSource paramSAXInputSource, boolean paramBoolean, short paramShort, Element paramElement)
  {
    Object localObject1 = paramSAXInputSource.getXMLReader();
    InputSource localInputSource = paramSAXInputSource.getInputSource();
    boolean bool1 = true;
    Object localObject2 = null;
    Element localElement = null;
    try
    {
      if ((localInputSource != null) && ((localInputSource.getSystemId() != null) || (localInputSource.getByteStream() != null) || (localInputSource.getCharacterStream() != null)))
      {
        XSDKey localXSDKey = null;
        String str = null;
        if (paramShort != 3)
        {
          str = XMLEntityManager.expandSystemId(localInputSource.getSystemId(), paramSAXInputSource.getBaseSystemId(), false);
          localXSDKey = new XSDKey(str, paramShort, paramString);
          if ((localElement = (Element)fTraversed.get(localXSDKey)) != null)
          {
            fLastSchemaWasDuplicate = true;
            return localElement;
          }
        }
        boolean bool2 = false;
        if (localObject1 != null)
        {
          try
          {
            bool2 = ((XMLReader)localObject1).getFeature("http://xml.org/sax/features/namespace-prefixes");
          }
          catch (SAXException localSAXException2) {}
        }
        else
        {
          try
          {
            localObject1 = XMLReaderFactory.createXMLReader();
          }
          catch (SAXException localSAXException3)
          {
            localObject1 = new SAXParser();
          }
          try
          {
            ((XMLReader)localObject1).setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            bool2 = true;
            if (((localObject1 instanceof SAXParser)) && (fSecurityManager != null)) {
              ((XMLReader)localObject1).setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
            }
          }
          catch (SAXException localSAXException4) {}
          try
          {
            ((XMLReader)localObject1).setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", fAccessExternalDTD);
          }
          catch (SAXNotRecognizedException localSAXNotRecognizedException)
          {
            XMLSecurityManager.printWarning(localObject1.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", localSAXNotRecognizedException);
          }
        }
        boolean bool3 = false;
        try
        {
          bool3 = ((XMLReader)localObject1).getFeature("http://xml.org/sax/features/string-interning");
        }
        catch (SAXException localSAXException5) {}
        if (fXSContentHandler == null) {
          fXSContentHandler = new SchemaContentHandler();
        }
        fXSContentHandler.reset(fSchemaParser, fSymbolTable, bool2, bool3);
        ((XMLReader)localObject1).setContentHandler(fXSContentHandler);
        ((XMLReader)localObject1).setErrorHandler(fErrorReporter.getSAXErrorHandler());
        ((XMLReader)localObject1).parse(localInputSource);
        try
        {
          ((XMLReader)localObject1).setContentHandler(null);
          ((XMLReader)localObject1).setErrorHandler(null);
        }
        catch (Exception localException) {}
        Document localDocument = fXSContentHandler.getDocument();
        localElement = localDocument != null ? DOMUtil.getRoot(localDocument) : null;
        return getSchemaDocument0(localXSDKey, str, localElement);
      }
      bool1 = false;
    }
    catch (SAXParseException localSAXParseException)
    {
      throw SAX2XNIUtil.createXMLParseException0(localSAXParseException);
    }
    catch (SAXException localSAXException1)
    {
      throw SAX2XNIUtil.createXNIException0(localSAXException1);
    }
    catch (IOException localIOException)
    {
      localObject2 = localIOException;
    }
    return getSchemaDocument1(paramBoolean, bool1, paramSAXInputSource, paramElement, (IOException)localObject2);
  }
  
  private Element getSchemaDocument(String paramString, DOMInputSource paramDOMInputSource, boolean paramBoolean, short paramShort, Element paramElement)
  {
    boolean bool = true;
    Object localObject1 = null;
    Object localObject2 = null;
    Element localElement = null;
    Node localNode1 = paramDOMInputSource.getNode();
    int i = -1;
    if (localNode1 != null)
    {
      i = localNode1.getNodeType();
      if (i == 9) {
        localElement = DOMUtil.getRoot((Document)localNode1);
      } else if (i == 1) {
        localElement = (Element)localNode1;
      }
    }
    try
    {
      if (localElement != null)
      {
        XSDKey localXSDKey = null;
        String str = null;
        if (paramShort != 3)
        {
          str = XMLEntityManager.expandSystemId(paramDOMInputSource.getSystemId(), paramDOMInputSource.getBaseSystemId(), false);
          int j = i == 9 ? 1 : 0;
          if (j == 0)
          {
            Node localNode2 = localElement.getParentNode();
            if (localNode2 != null) {
              j = localNode2.getNodeType() == 9 ? 1 : 0;
            }
          }
          if (j != 0)
          {
            localXSDKey = new XSDKey(str, paramShort, paramString);
            if ((localObject2 = (Element)fTraversed.get(localXSDKey)) != null)
            {
              fLastSchemaWasDuplicate = true;
              return (Element)localObject2;
            }
          }
        }
        localObject2 = localElement;
        return getSchemaDocument0(localXSDKey, str, (Element)localObject2);
      }
      bool = false;
    }
    catch (IOException localIOException)
    {
      localObject1 = localIOException;
    }
    return getSchemaDocument1(paramBoolean, bool, paramDOMInputSource, paramElement, (IOException)localObject1);
  }
  
  private Element getSchemaDocument(String paramString, StAXInputSource paramStAXInputSource, boolean paramBoolean, short paramShort, Element paramElement)
  {
    Object localObject1 = null;
    Element localElement = null;
    try
    {
      boolean bool1 = paramStAXInputSource.shouldConsumeRemainingContent();
      localObject2 = paramStAXInputSource.getXMLStreamReader();
      XMLEventReader localXMLEventReader = paramStAXInputSource.getXMLEventReader();
      XSDKey localXSDKey = null;
      String str = null;
      if (paramShort != 3)
      {
        str = XMLEntityManager.expandSystemId(paramStAXInputSource.getSystemId(), paramStAXInputSource.getBaseSystemId(), false);
        boolean bool2 = bool1;
        if (!bool2) {
          if (localObject2 != null) {
            bool2 = ((XMLStreamReader)localObject2).getEventType() == 7;
          } else {
            bool2 = localXMLEventReader.peek().isStartDocument();
          }
        }
        if (bool2)
        {
          localXSDKey = new XSDKey(str, paramShort, paramString);
          if ((localElement = (Element)fTraversed.get(localXSDKey)) != null)
          {
            fLastSchemaWasDuplicate = true;
            return localElement;
          }
        }
      }
      if (fStAXSchemaParser == null) {
        fStAXSchemaParser = new StAXSchemaParser();
      }
      fStAXSchemaParser.reset(fSchemaParser, fSymbolTable);
      if (localObject2 != null)
      {
        fStAXSchemaParser.parse((XMLStreamReader)localObject2);
        if (bool1) {
          while (((XMLStreamReader)localObject2).hasNext()) {
            ((XMLStreamReader)localObject2).next();
          }
        }
      }
      else
      {
        fStAXSchemaParser.parse(localXMLEventReader);
        if (bool1) {
          while (localXMLEventReader.hasNext()) {
            localXMLEventReader.nextEvent();
          }
        }
      }
      Document localDocument = fStAXSchemaParser.getDocument();
      localElement = localDocument != null ? DOMUtil.getRoot(localDocument) : null;
      return getSchemaDocument0(localXSDKey, str, localElement);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      Object localObject2 = new StAXLocationWrapper();
      ((StAXLocationWrapper)localObject2).setLocation(localXMLStreamException.getLocation());
      throw new XMLParseException((XMLLocator)localObject2, localXMLStreamException.getMessage(), localXMLStreamException);
    }
    catch (IOException localIOException)
    {
      localObject1 = localIOException;
    }
    return getSchemaDocument1(paramBoolean, true, paramStAXInputSource, paramElement, (IOException)localObject1);
  }
  
  private Element getSchemaDocument0(XSDKey paramXSDKey, String paramString, Element paramElement)
  {
    if (paramXSDKey != null) {
      fTraversed.put(paramXSDKey, paramElement);
    }
    if (paramString != null) {
      fDoc2SystemId.put(paramElement, paramString);
    }
    fLastSchemaWasDuplicate = false;
    return paramElement;
  }
  
  private Element getSchemaDocument1(boolean paramBoolean1, boolean paramBoolean2, XMLInputSource paramXMLInputSource, Element paramElement, IOException paramIOException)
  {
    if (paramBoolean1)
    {
      if (paramBoolean2) {
        reportSchemaError("schema_reference.4", new Object[] { paramXMLInputSource.getSystemId() }, paramElement, paramIOException);
      } else {
        reportSchemaError("schema_reference.4", new Object[] { paramXMLInputSource == null ? "" : paramXMLInputSource.getSystemId() }, paramElement, paramIOException);
      }
    }
    else if (paramBoolean2) {
      reportSchemaWarning("schema_reference.4", new Object[] { paramXMLInputSource.getSystemId() }, paramElement, paramIOException);
    }
    fLastSchemaWasDuplicate = false;
    return null;
  }
  
  private Element getSchemaDocument(XSInputSource paramXSInputSource, XSDDescription paramXSDDescription)
  {
    SchemaGrammar[] arrayOfSchemaGrammar = paramXSInputSource.getGrammars();
    int i = paramXSDDescription.getContextType();
    Object localObject;
    if ((arrayOfSchemaGrammar != null) && (arrayOfSchemaGrammar.length > 0))
    {
      localObject = expandGrammars(arrayOfSchemaGrammar);
      if ((fNamespaceGrowth) || (!existingGrammars((Vector)localObject)))
      {
        addGrammars((Vector)localObject);
        if (i == 3) {
          paramXSDDescription.setTargetNamespace(arrayOfSchemaGrammar[0].getTargetNamespace());
        }
      }
    }
    else
    {
      localObject = paramXSInputSource.getComponents();
      if ((localObject != null) && (localObject.length > 0))
      {
        HashMap localHashMap = new HashMap();
        Vector localVector = expandComponents((XSObject[])localObject, localHashMap);
        if ((fNamespaceGrowth) || (canAddComponents(localVector)))
        {
          addGlobalComponents(localVector, localHashMap);
          if (i == 3) {
            paramXSDDescription.setTargetNamespace(localObject[0].getNamespace());
          }
        }
      }
    }
    return null;
  }
  
  private Vector expandGrammars(SchemaGrammar[] paramArrayOfSchemaGrammar)
  {
    Vector localVector1 = new Vector();
    for (int i = 0; i < paramArrayOfSchemaGrammar.length; i++) {
      if (!localVector1.contains(paramArrayOfSchemaGrammar[i])) {
        localVector1.add(paramArrayOfSchemaGrammar[i]);
      }
    }
    for (int j = 0; j < localVector1.size(); j++)
    {
      SchemaGrammar localSchemaGrammar1 = (SchemaGrammar)localVector1.elementAt(j);
      Vector localVector2 = localSchemaGrammar1.getImportedGrammars();
      if (localVector2 != null) {
        for (int k = localVector2.size() - 1; k >= 0; k--)
        {
          SchemaGrammar localSchemaGrammar2 = (SchemaGrammar)localVector2.elementAt(k);
          if (!localVector1.contains(localSchemaGrammar2)) {
            localVector1.addElement(localSchemaGrammar2);
          }
        }
      }
    }
    return localVector1;
  }
  
  private boolean existingGrammars(Vector paramVector)
  {
    int i = paramVector.size();
    XSDDescription localXSDDescription = new XSDDescription();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar1 = (SchemaGrammar)paramVector.elementAt(j);
      localXSDDescription.setNamespace(localSchemaGrammar1.getTargetNamespace());
      SchemaGrammar localSchemaGrammar2 = findGrammar(localXSDDescription, false);
      if (localSchemaGrammar2 != null) {
        return true;
      }
    }
    return false;
  }
  
  private boolean canAddComponents(Vector paramVector)
  {
    int i = paramVector.size();
    XSDDescription localXSDDescription = new XSDDescription();
    for (int j = 0; j < i; j++)
    {
      XSObject localXSObject = (XSObject)paramVector.elementAt(j);
      if (!canAddComponent(localXSObject, localXSDDescription)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean canAddComponent(XSObject paramXSObject, XSDDescription paramXSDDescription)
  {
    paramXSDDescription.setNamespace(paramXSObject.getNamespace());
    SchemaGrammar localSchemaGrammar = findGrammar(paramXSDDescription, false);
    if (localSchemaGrammar == null) {
      return true;
    }
    if (localSchemaGrammar.isImmutable()) {
      return false;
    }
    int i = paramXSObject.getType();
    String str = paramXSObject.getName();
    switch (i)
    {
    case 3: 
      if (localSchemaGrammar.getGlobalTypeDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 1: 
      if (localSchemaGrammar.getGlobalAttributeDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 5: 
      if (localSchemaGrammar.getGlobalAttributeDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 2: 
      if (localSchemaGrammar.getGlobalElementDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 6: 
      if (localSchemaGrammar.getGlobalGroupDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 11: 
      if (localSchemaGrammar.getGlobalNotationDecl(str) == paramXSObject) {
        return true;
      }
      break;
    case 4: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    default: 
      return true;
    }
    return false;
  }
  
  private void addGrammars(Vector paramVector)
  {
    int i = paramVector.size();
    XSDDescription localXSDDescription = new XSDDescription();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar1 = (SchemaGrammar)paramVector.elementAt(j);
      localXSDDescription.setNamespace(localSchemaGrammar1.getTargetNamespace());
      SchemaGrammar localSchemaGrammar2 = findGrammar(localXSDDescription, fNamespaceGrowth);
      if (localSchemaGrammar1 != localSchemaGrammar2) {
        addGrammarComponents(localSchemaGrammar1, localSchemaGrammar2);
      }
    }
  }
  
  private void addGrammarComponents(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    if (paramSchemaGrammar2 == null)
    {
      createGrammarFrom(paramSchemaGrammar1);
      return;
    }
    SchemaGrammar localSchemaGrammar = paramSchemaGrammar2;
    if (localSchemaGrammar.isImmutable()) {
      localSchemaGrammar = createGrammarFrom(paramSchemaGrammar2);
    }
    addNewGrammarLocations(paramSchemaGrammar1, localSchemaGrammar);
    addNewImportedGrammars(paramSchemaGrammar1, localSchemaGrammar);
    addNewGrammarComponents(paramSchemaGrammar1, localSchemaGrammar);
  }
  
  private SchemaGrammar createGrammarFrom(SchemaGrammar paramSchemaGrammar)
  {
    SchemaGrammar localSchemaGrammar = new SchemaGrammar(paramSchemaGrammar);
    fGrammarBucket.putGrammar(localSchemaGrammar);
    updateImportListWith(localSchemaGrammar);
    updateImportListFor(localSchemaGrammar);
    return localSchemaGrammar;
  }
  
  private void addNewGrammarLocations(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    StringList localStringList1 = paramSchemaGrammar1.getDocumentLocations();
    int i = localStringList1.size();
    StringList localStringList2 = paramSchemaGrammar2.getDocumentLocations();
    for (int j = 0; j < i; j++)
    {
      String str = localStringList1.item(j);
      if (!localStringList2.contains(str)) {
        paramSchemaGrammar2.addDocument(null, str);
      }
    }
  }
  
  private void addNewImportedGrammars(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    Vector localVector1 = paramSchemaGrammar1.getImportedGrammars();
    if (localVector1 != null)
    {
      Vector localVector2 = paramSchemaGrammar2.getImportedGrammars();
      if (localVector2 == null)
      {
        localVector2 = (Vector)localVector1.clone();
        paramSchemaGrammar2.setImportedGrammars(localVector2);
      }
      else
      {
        updateImportList(localVector1, localVector2);
      }
    }
  }
  
  private void updateImportList(Vector paramVector1, Vector paramVector2)
  {
    int i = paramVector1.size();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar = (SchemaGrammar)paramVector1.elementAt(j);
      if (!containedImportedGrammar(paramVector2, localSchemaGrammar)) {
        paramVector2.add(localSchemaGrammar);
      }
    }
  }
  
  private void addNewGrammarComponents(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    paramSchemaGrammar2.resetComponents();
    addGlobalElementDecls(paramSchemaGrammar1, paramSchemaGrammar2);
    addGlobalAttributeDecls(paramSchemaGrammar1, paramSchemaGrammar2);
    addGlobalAttributeGroupDecls(paramSchemaGrammar1, paramSchemaGrammar2);
    addGlobalGroupDecls(paramSchemaGrammar1, paramSchemaGrammar2);
    addGlobalTypeDecls(paramSchemaGrammar1, paramSchemaGrammar2);
    addGlobalNotationDecls(paramSchemaGrammar1, paramSchemaGrammar2);
  }
  
  private void addGlobalElementDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)2);
    int i = localXSNamedMap.getLength();
    XSElementDecl localXSElementDecl1;
    XSElementDecl localXSElementDecl2;
    for (int j = 0; j < i; j++)
    {
      localXSElementDecl1 = (XSElementDecl)localXSNamedMap.item(j);
      localXSElementDecl2 = paramSchemaGrammar2.getGlobalElementDecl(localXSElementDecl1.getName());
      if (localXSElementDecl2 == null) {
        paramSchemaGrammar2.addGlobalElementDecl(localXSElementDecl1);
      } else if (localXSElementDecl2 == localXSElementDecl1) {}
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)2);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSElementDecl1 = (XSElementDecl)localObjectList.item(k + 1);
      localXSElementDecl2 = paramSchemaGrammar2.getGlobalElementDecl(str3, str2);
      if (localXSElementDecl2 == null) {
        paramSchemaGrammar2.addGlobalElementDecl(localXSElementDecl1, str2);
      } else if (localXSElementDecl2 == localXSElementDecl1) {}
    }
  }
  
  private void addGlobalAttributeDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)1);
    int i = localXSNamedMap.getLength();
    XSAttributeDecl localXSAttributeDecl1;
    XSAttributeDecl localXSAttributeDecl2;
    for (int j = 0; j < i; j++)
    {
      localXSAttributeDecl1 = (XSAttributeDecl)localXSNamedMap.item(j);
      localXSAttributeDecl2 = paramSchemaGrammar2.getGlobalAttributeDecl(localXSAttributeDecl1.getName());
      if (localXSAttributeDecl2 == null) {
        paramSchemaGrammar2.addGlobalAttributeDecl(localXSAttributeDecl1);
      } else if ((localXSAttributeDecl2 != localXSAttributeDecl1) && (!fTolerateDuplicates)) {
        reportSharingError(localXSAttributeDecl1.getNamespace(), localXSAttributeDecl1.getName());
      }
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)1);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSAttributeDecl1 = (XSAttributeDecl)localObjectList.item(k + 1);
      localXSAttributeDecl2 = paramSchemaGrammar2.getGlobalAttributeDecl(str3, str2);
      if (localXSAttributeDecl2 == null) {
        paramSchemaGrammar2.addGlobalAttributeDecl(localXSAttributeDecl1, str2);
      } else if (localXSAttributeDecl2 == localXSAttributeDecl1) {}
    }
  }
  
  private void addGlobalAttributeGroupDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)5);
    int i = localXSNamedMap.getLength();
    XSAttributeGroupDecl localXSAttributeGroupDecl1;
    XSAttributeGroupDecl localXSAttributeGroupDecl2;
    for (int j = 0; j < i; j++)
    {
      localXSAttributeGroupDecl1 = (XSAttributeGroupDecl)localXSNamedMap.item(j);
      localXSAttributeGroupDecl2 = paramSchemaGrammar2.getGlobalAttributeGroupDecl(localXSAttributeGroupDecl1.getName());
      if (localXSAttributeGroupDecl2 == null) {
        paramSchemaGrammar2.addGlobalAttributeGroupDecl(localXSAttributeGroupDecl1);
      } else if ((localXSAttributeGroupDecl2 != localXSAttributeGroupDecl1) && (!fTolerateDuplicates)) {
        reportSharingError(localXSAttributeGroupDecl1.getNamespace(), localXSAttributeGroupDecl1.getName());
      }
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)5);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSAttributeGroupDecl1 = (XSAttributeGroupDecl)localObjectList.item(k + 1);
      localXSAttributeGroupDecl2 = paramSchemaGrammar2.getGlobalAttributeGroupDecl(str3, str2);
      if (localXSAttributeGroupDecl2 == null) {
        paramSchemaGrammar2.addGlobalAttributeGroupDecl(localXSAttributeGroupDecl1, str2);
      } else if (localXSAttributeGroupDecl2 == localXSAttributeGroupDecl1) {}
    }
  }
  
  private void addGlobalNotationDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)11);
    int i = localXSNamedMap.getLength();
    XSNotationDecl localXSNotationDecl1;
    XSNotationDecl localXSNotationDecl2;
    for (int j = 0; j < i; j++)
    {
      localXSNotationDecl1 = (XSNotationDecl)localXSNamedMap.item(j);
      localXSNotationDecl2 = paramSchemaGrammar2.getGlobalNotationDecl(localXSNotationDecl1.getName());
      if (localXSNotationDecl2 == null) {
        paramSchemaGrammar2.addGlobalNotationDecl(localXSNotationDecl1);
      } else if ((localXSNotationDecl2 != localXSNotationDecl1) && (!fTolerateDuplicates)) {
        reportSharingError(localXSNotationDecl1.getNamespace(), localXSNotationDecl1.getName());
      }
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)11);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSNotationDecl1 = (XSNotationDecl)localObjectList.item(k + 1);
      localXSNotationDecl2 = paramSchemaGrammar2.getGlobalNotationDecl(str3, str2);
      if (localXSNotationDecl2 == null) {
        paramSchemaGrammar2.addGlobalNotationDecl(localXSNotationDecl1, str2);
      } else if (localXSNotationDecl2 == localXSNotationDecl1) {}
    }
  }
  
  private void addGlobalGroupDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)6);
    int i = localXSNamedMap.getLength();
    XSGroupDecl localXSGroupDecl1;
    XSGroupDecl localXSGroupDecl2;
    for (int j = 0; j < i; j++)
    {
      localXSGroupDecl1 = (XSGroupDecl)localXSNamedMap.item(j);
      localXSGroupDecl2 = paramSchemaGrammar2.getGlobalGroupDecl(localXSGroupDecl1.getName());
      if (localXSGroupDecl2 == null) {
        paramSchemaGrammar2.addGlobalGroupDecl(localXSGroupDecl1);
      } else if ((localXSGroupDecl1 != localXSGroupDecl2) && (!fTolerateDuplicates)) {
        reportSharingError(localXSGroupDecl1.getNamespace(), localXSGroupDecl1.getName());
      }
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)6);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSGroupDecl1 = (XSGroupDecl)localObjectList.item(k + 1);
      localXSGroupDecl2 = paramSchemaGrammar2.getGlobalGroupDecl(str3, str2);
      if (localXSGroupDecl2 == null) {
        paramSchemaGrammar2.addGlobalGroupDecl(localXSGroupDecl1, str2);
      } else if (localXSGroupDecl2 == localXSGroupDecl1) {}
    }
  }
  
  private void addGlobalTypeDecls(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
  {
    XSNamedMap localXSNamedMap = paramSchemaGrammar1.getComponents((short)3);
    int i = localXSNamedMap.getLength();
    XSTypeDefinition localXSTypeDefinition1;
    XSTypeDefinition localXSTypeDefinition2;
    for (int j = 0; j < i; j++)
    {
      localXSTypeDefinition1 = (XSTypeDefinition)localXSNamedMap.item(j);
      localXSTypeDefinition2 = paramSchemaGrammar2.getGlobalTypeDecl(localXSTypeDefinition1.getName());
      if (localXSTypeDefinition2 == null) {
        paramSchemaGrammar2.addGlobalTypeDecl(localXSTypeDefinition1);
      } else if ((localXSTypeDefinition2 != localXSTypeDefinition1) && (!fTolerateDuplicates)) {
        reportSharingError(localXSTypeDefinition1.getNamespace(), localXSTypeDefinition1.getName());
      }
    }
    ObjectList localObjectList = paramSchemaGrammar1.getComponentsExt((short)3);
    i = localObjectList.getLength();
    for (int k = 0; k < i; k += 2)
    {
      String str1 = (String)localObjectList.item(k);
      int m = str1.indexOf(',');
      String str2 = str1.substring(0, m);
      String str3 = str1.substring(m + 1, str1.length());
      localXSTypeDefinition1 = (XSTypeDefinition)localObjectList.item(k + 1);
      localXSTypeDefinition2 = paramSchemaGrammar2.getGlobalTypeDecl(str3, str2);
      if (localXSTypeDefinition2 == null) {
        paramSchemaGrammar2.addGlobalTypeDecl(localXSTypeDefinition1, str2);
      } else if (localXSTypeDefinition2 == localXSTypeDefinition1) {}
    }
  }
  
  private Vector expandComponents(XSObject[] paramArrayOfXSObject, Map<String, Vector> paramMap)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < paramArrayOfXSObject.length; i++) {
      if (!localVector.contains(paramArrayOfXSObject[i])) {
        localVector.add(paramArrayOfXSObject[i]);
      }
    }
    for (i = 0; i < localVector.size(); i++)
    {
      XSObject localXSObject = (XSObject)localVector.elementAt(i);
      expandRelatedComponents(localXSObject, localVector, paramMap);
    }
    return localVector;
  }
  
  private void expandRelatedComponents(XSObject paramXSObject, Vector paramVector, Map<String, Vector> paramMap)
  {
    int i = paramXSObject.getType();
    switch (i)
    {
    case 3: 
      expandRelatedTypeComponents((XSTypeDefinition)paramXSObject, paramVector, paramXSObject.getNamespace(), paramMap);
      break;
    case 1: 
      expandRelatedAttributeComponents((XSAttributeDeclaration)paramXSObject, paramVector, paramXSObject.getNamespace(), paramMap);
      break;
    case 5: 
      expandRelatedAttributeGroupComponents((XSAttributeGroupDefinition)paramXSObject, paramVector, paramXSObject.getNamespace(), paramMap);
    case 2: 
      expandRelatedElementComponents((XSElementDeclaration)paramXSObject, paramVector, paramXSObject.getNamespace(), paramMap);
      break;
    case 6: 
      expandRelatedModelGroupDefinitionComponents((XSModelGroupDefinition)paramXSObject, paramVector, paramXSObject.getNamespace(), paramMap);
    }
  }
  
  private void expandRelatedAttributeComponents(XSAttributeDeclaration paramXSAttributeDeclaration, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    addRelatedType(paramXSAttributeDeclaration.getTypeDefinition(), paramVector, paramString, paramMap);
  }
  
  private void expandRelatedElementComponents(XSElementDeclaration paramXSElementDeclaration, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    addRelatedType(paramXSElementDeclaration.getTypeDefinition(), paramVector, paramString, paramMap);
    XSElementDeclaration localXSElementDeclaration = paramXSElementDeclaration.getSubstitutionGroupAffiliation();
    if (localXSElementDeclaration != null) {
      addRelatedElement(localXSElementDeclaration, paramVector, paramString, paramMap);
    }
  }
  
  private void expandRelatedTypeComponents(XSTypeDefinition paramXSTypeDefinition, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    if ((paramXSTypeDefinition instanceof XSComplexTypeDecl)) {
      expandRelatedComplexTypeComponents((XSComplexTypeDecl)paramXSTypeDefinition, paramVector, paramString, paramMap);
    } else if ((paramXSTypeDefinition instanceof XSSimpleTypeDecl)) {
      expandRelatedSimpleTypeComponents((XSSimpleTypeDefinition)paramXSTypeDefinition, paramVector, paramString, paramMap);
    }
  }
  
  private void expandRelatedModelGroupDefinitionComponents(XSModelGroupDefinition paramXSModelGroupDefinition, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    expandRelatedModelGroupComponents(paramXSModelGroupDefinition.getModelGroup(), paramVector, paramString, paramMap);
  }
  
  private void expandRelatedAttributeGroupComponents(XSAttributeGroupDefinition paramXSAttributeGroupDefinition, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    expandRelatedAttributeUsesComponents(paramXSAttributeGroupDefinition.getAttributeUses(), paramVector, paramString, paramMap);
  }
  
  private void expandRelatedComplexTypeComponents(XSComplexTypeDecl paramXSComplexTypeDecl, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    addRelatedType(paramXSComplexTypeDecl.getBaseType(), paramVector, paramString, paramMap);
    expandRelatedAttributeUsesComponents(paramXSComplexTypeDecl.getAttributeUses(), paramVector, paramString, paramMap);
    XSParticle localXSParticle = paramXSComplexTypeDecl.getParticle();
    if (localXSParticle != null) {
      expandRelatedParticleComponents(localXSParticle, paramVector, paramString, paramMap);
    }
  }
  
  private void expandRelatedSimpleTypeComponents(XSSimpleTypeDefinition paramXSSimpleTypeDefinition, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    XSTypeDefinition localXSTypeDefinition = paramXSSimpleTypeDefinition.getBaseType();
    if (localXSTypeDefinition != null) {
      addRelatedType(localXSTypeDefinition, paramVector, paramString, paramMap);
    }
    XSSimpleTypeDefinition localXSSimpleTypeDefinition1 = paramXSSimpleTypeDefinition.getItemType();
    if (localXSSimpleTypeDefinition1 != null) {
      addRelatedType(localXSSimpleTypeDefinition1, paramVector, paramString, paramMap);
    }
    XSSimpleTypeDefinition localXSSimpleTypeDefinition2 = paramXSSimpleTypeDefinition.getPrimitiveType();
    if (localXSSimpleTypeDefinition2 != null) {
      addRelatedType(localXSSimpleTypeDefinition2, paramVector, paramString, paramMap);
    }
    XSObjectList localXSObjectList = paramXSSimpleTypeDefinition.getMemberTypes();
    if (localXSObjectList.size() > 0) {
      for (int i = 0; i < localXSObjectList.size(); i++) {
        addRelatedType((XSTypeDefinition)localXSObjectList.item(i), paramVector, paramString, paramMap);
      }
    }
  }
  
  private void expandRelatedAttributeUsesComponents(XSObjectList paramXSObjectList, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    int i = paramXSObjectList == null ? 0 : paramXSObjectList.size();
    for (int j = 0; j < i; j++) {
      expandRelatedAttributeUseComponents((XSAttributeUse)paramXSObjectList.item(j), paramVector, paramString, paramMap);
    }
  }
  
  private void expandRelatedAttributeUseComponents(XSAttributeUse paramXSAttributeUse, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    addRelatedAttribute(paramXSAttributeUse.getAttrDeclaration(), paramVector, paramString, paramMap);
  }
  
  private void expandRelatedParticleComponents(XSParticle paramXSParticle, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    XSTerm localXSTerm = paramXSParticle.getTerm();
    switch (localXSTerm.getType())
    {
    case 2: 
      addRelatedElement((XSElementDeclaration)localXSTerm, paramVector, paramString, paramMap);
      break;
    case 7: 
      expandRelatedModelGroupComponents((XSModelGroup)localXSTerm, paramVector, paramString, paramMap);
      break;
    }
  }
  
  private void expandRelatedModelGroupComponents(XSModelGroup paramXSModelGroup, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    XSObjectList localXSObjectList = paramXSModelGroup.getParticles();
    int i = localXSObjectList == null ? 0 : localXSObjectList.getLength();
    for (int j = 0; j < i; j++) {
      expandRelatedParticleComponents((XSParticle)localXSObjectList.item(j), paramVector, paramString, paramMap);
    }
  }
  
  private void addRelatedType(XSTypeDefinition paramXSTypeDefinition, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    if (!paramXSTypeDefinition.getAnonymous())
    {
      if ((!paramXSTypeDefinition.getNamespace().equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (!paramVector.contains(paramXSTypeDefinition)))
      {
        Vector localVector = findDependentNamespaces(paramString, paramMap);
        addNamespaceDependency(paramString, paramXSTypeDefinition.getNamespace(), localVector);
        paramVector.add(paramXSTypeDefinition);
      }
    }
    else {
      expandRelatedTypeComponents(paramXSTypeDefinition, paramVector, paramString, paramMap);
    }
  }
  
  private void addRelatedElement(XSElementDeclaration paramXSElementDeclaration, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    if (paramXSElementDeclaration.getScope() == 1)
    {
      if (!paramVector.contains(paramXSElementDeclaration))
      {
        Vector localVector = findDependentNamespaces(paramString, paramMap);
        addNamespaceDependency(paramString, paramXSElementDeclaration.getNamespace(), localVector);
        paramVector.add(paramXSElementDeclaration);
      }
    }
    else {
      expandRelatedElementComponents(paramXSElementDeclaration, paramVector, paramString, paramMap);
    }
  }
  
  private void addRelatedAttribute(XSAttributeDeclaration paramXSAttributeDeclaration, Vector paramVector, String paramString, Map<String, Vector> paramMap)
  {
    if (paramXSAttributeDeclaration.getScope() == 1)
    {
      if (!paramVector.contains(paramXSAttributeDeclaration))
      {
        Vector localVector = findDependentNamespaces(paramString, paramMap);
        addNamespaceDependency(paramString, paramXSAttributeDeclaration.getNamespace(), localVector);
        paramVector.add(paramXSAttributeDeclaration);
      }
    }
    else {
      expandRelatedAttributeComponents(paramXSAttributeDeclaration, paramVector, paramString, paramMap);
    }
  }
  
  private void addGlobalComponents(Vector paramVector, Map<String, Vector> paramMap)
  {
    XSDDescription localXSDDescription = new XSDDescription();
    int i = paramVector.size();
    for (int j = 0; j < i; j++) {
      addGlobalComponent((XSObject)paramVector.elementAt(j), localXSDDescription);
    }
    updateImportDependencies(paramMap);
  }
  
  private void addGlobalComponent(XSObject paramXSObject, XSDDescription paramXSDDescription)
  {
    String str1 = paramXSObject.getNamespace();
    paramXSDDescription.setNamespace(str1);
    SchemaGrammar localSchemaGrammar = getSchemaGrammar(paramXSDDescription);
    int i = paramXSObject.getType();
    String str2 = paramXSObject.getName();
    switch (i)
    {
    case 3: 
      if (!((XSTypeDefinition)paramXSObject).getAnonymous())
      {
        if (localSchemaGrammar.getGlobalTypeDecl(str2) == null) {
          localSchemaGrammar.addGlobalTypeDecl((XSTypeDefinition)paramXSObject);
        }
        if (localSchemaGrammar.getGlobalTypeDecl(str2, "") == null) {
          localSchemaGrammar.addGlobalTypeDecl((XSTypeDefinition)paramXSObject, "");
        }
      }
      break;
    case 1: 
      if (((XSAttributeDecl)paramXSObject).getScope() == 1)
      {
        if (localSchemaGrammar.getGlobalAttributeDecl(str2) == null) {
          localSchemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)paramXSObject);
        }
        if (localSchemaGrammar.getGlobalAttributeDecl(str2, "") == null) {
          localSchemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)paramXSObject, "");
        }
      }
      break;
    case 5: 
      if (localSchemaGrammar.getGlobalAttributeDecl(str2) == null) {
        localSchemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)paramXSObject);
      }
      if (localSchemaGrammar.getGlobalAttributeDecl(str2, "") == null) {
        localSchemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)paramXSObject, "");
      }
      break;
    case 2: 
      if (((XSElementDecl)paramXSObject).getScope() == 1)
      {
        localSchemaGrammar.addGlobalElementDeclAll((XSElementDecl)paramXSObject);
        if (localSchemaGrammar.getGlobalElementDecl(str2) == null) {
          localSchemaGrammar.addGlobalElementDecl((XSElementDecl)paramXSObject);
        }
        if (localSchemaGrammar.getGlobalElementDecl(str2, "") == null) {
          localSchemaGrammar.addGlobalElementDecl((XSElementDecl)paramXSObject, "");
        }
      }
      break;
    case 6: 
      if (localSchemaGrammar.getGlobalGroupDecl(str2) == null) {
        localSchemaGrammar.addGlobalGroupDecl((XSGroupDecl)paramXSObject);
      }
      if (localSchemaGrammar.getGlobalGroupDecl(str2, "") == null) {
        localSchemaGrammar.addGlobalGroupDecl((XSGroupDecl)paramXSObject, "");
      }
      break;
    case 11: 
      if (localSchemaGrammar.getGlobalNotationDecl(str2) == null) {
        localSchemaGrammar.addGlobalNotationDecl((XSNotationDecl)paramXSObject);
      }
      if (localSchemaGrammar.getGlobalNotationDecl(str2, "") == null) {
        localSchemaGrammar.addGlobalNotationDecl((XSNotationDecl)paramXSObject, "");
      }
      break;
    }
  }
  
  private void updateImportDependencies(Map<String, Vector> paramMap)
  {
    if (paramMap == null) {
      return;
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      Vector localVector = (Vector)localEntry.getValue();
      if (localVector.size() > 0) {
        expandImportList(str, localVector);
      }
    }
  }
  
  private void expandImportList(String paramString, Vector paramVector)
  {
    SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(paramString);
    if (localSchemaGrammar != null)
    {
      Vector localVector = localSchemaGrammar.getImportedGrammars();
      if (localVector == null)
      {
        localVector = new Vector();
        addImportList(localSchemaGrammar, localVector, paramVector);
        localSchemaGrammar.setImportedGrammars(localVector);
      }
      else
      {
        updateImportList(localSchemaGrammar, localVector, paramVector);
      }
    }
  }
  
  private void addImportList(SchemaGrammar paramSchemaGrammar, Vector paramVector1, Vector paramVector2)
  {
    int i = paramVector2.size();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar((String)paramVector2.elementAt(j));
      if (localSchemaGrammar != null) {
        paramVector1.add(localSchemaGrammar);
      }
    }
  }
  
  private void updateImportList(SchemaGrammar paramSchemaGrammar, Vector paramVector1, Vector paramVector2)
  {
    int i = paramVector2.size();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar((String)paramVector2.elementAt(j));
      if ((localSchemaGrammar != null) && (!containedImportedGrammar(paramVector1, localSchemaGrammar))) {
        paramVector1.add(localSchemaGrammar);
      }
    }
  }
  
  private boolean containedImportedGrammar(Vector paramVector, SchemaGrammar paramSchemaGrammar)
  {
    int i = paramVector.size();
    for (int j = 0; j < i; j++)
    {
      SchemaGrammar localSchemaGrammar = (SchemaGrammar)paramVector.elementAt(j);
      if (null2EmptyString(localSchemaGrammar.getTargetNamespace()).equals(null2EmptyString(paramSchemaGrammar.getTargetNamespace()))) {
        return true;
      }
    }
    return false;
  }
  
  private SchemaGrammar getSchemaGrammar(XSDDescription paramXSDDescription)
  {
    SchemaGrammar localSchemaGrammar = findGrammar(paramXSDDescription, fNamespaceGrowth);
    if (localSchemaGrammar == null)
    {
      localSchemaGrammar = new SchemaGrammar(paramXSDDescription.getNamespace(), paramXSDDescription.makeClone(), fSymbolTable);
      fGrammarBucket.putGrammar(localSchemaGrammar);
    }
    else if (localSchemaGrammar.isImmutable())
    {
      localSchemaGrammar = createGrammarFrom(localSchemaGrammar);
    }
    return localSchemaGrammar;
  }
  
  private Vector findDependentNamespaces(String paramString, Map paramMap)
  {
    String str = null2EmptyString(paramString);
    Vector localVector = (Vector)getFromMap(paramMap, str);
    if (localVector == null)
    {
      localVector = new Vector();
      paramMap.put(str, localVector);
    }
    return localVector;
  }
  
  private void addNamespaceDependency(String paramString1, String paramString2, Vector paramVector)
  {
    String str1 = null2EmptyString(paramString1);
    String str2 = null2EmptyString(paramString2);
    if ((!str1.equals(str2)) && (!paramVector.contains(str2))) {
      paramVector.add(str2);
    }
  }
  
  private void reportSharingError(String paramString1, String paramString2)
  {
    String str = paramString1 + "," + paramString2;
    reportSchemaError("sch-props-correct.2", new Object[] { str }, null);
  }
  
  private void createTraversers()
  {
    fAttributeChecker = new XSAttributeChecker(this);
    fAttributeGroupTraverser = new XSDAttributeGroupTraverser(this, fAttributeChecker);
    fAttributeTraverser = new XSDAttributeTraverser(this, fAttributeChecker);
    fComplexTypeTraverser = new XSDComplexTypeTraverser(this, fAttributeChecker);
    fElementTraverser = new XSDElementTraverser(this, fAttributeChecker);
    fGroupTraverser = new XSDGroupTraverser(this, fAttributeChecker);
    fKeyrefTraverser = new XSDKeyrefTraverser(this, fAttributeChecker);
    fNotationTraverser = new XSDNotationTraverser(this, fAttributeChecker);
    fSimpleTypeTraverser = new XSDSimpleTypeTraverser(this, fAttributeChecker);
    fUniqueOrKeyTraverser = new XSDUniqueOrKeyTraverser(this, fAttributeChecker);
    fWildCardTraverser = new XSDWildcardTraverser(this, fAttributeChecker);
  }
  
  void prepareForParse()
  {
    fTraversed.clear();
    fDoc2SystemId.clear();
    fHiddenNodes.clear();
    fLastSchemaWasDuplicate = false;
  }
  
  void prepareForTraverse()
  {
    if (!registryEmpty)
    {
      fUnparsedAttributeRegistry.clear();
      fUnparsedAttributeGroupRegistry.clear();
      fUnparsedElementRegistry.clear();
      fUnparsedGroupRegistry.clear();
      fUnparsedIdentityConstraintRegistry.clear();
      fUnparsedNotationRegistry.clear();
      fUnparsedTypeRegistry.clear();
      fUnparsedAttributeRegistrySub.clear();
      fUnparsedAttributeGroupRegistrySub.clear();
      fUnparsedElementRegistrySub.clear();
      fUnparsedGroupRegistrySub.clear();
      fUnparsedIdentityConstraintRegistrySub.clear();
      fUnparsedNotationRegistrySub.clear();
      fUnparsedTypeRegistrySub.clear();
    }
    for (int i = 1; i <= 7; i++) {
      if (fUnparsedRegistriesExt[i] != null) {
        fUnparsedRegistriesExt[i].clear();
      }
    }
    fDependencyMap.clear();
    fDoc2XSDocumentMap.clear();
    if (fRedefine2XSDMap != null) {
      fRedefine2XSDMap.clear();
    }
    if (fRedefine2NSSupport != null) {
      fRedefine2NSSupport.clear();
    }
    fAllTNSs.removeAllElements();
    fImportMap.clear();
    fRoot = null;
    for (i = 0; i < fLocalElemStackPos; i++)
    {
      fParticle[i] = null;
      fLocalElementDecl[i] = null;
      fLocalElementDecl_schema[i] = null;
      fLocalElemNamespaceContext[i] = null;
    }
    fLocalElemStackPos = 0;
    for (i = 0; i < fKeyrefStackPos; i++)
    {
      fKeyrefs[i] = null;
      fKeyrefElems[i] = null;
      fKeyrefNamespaceContext[i] = null;
      fKeyrefsMapXSDocumentInfo[i] = null;
    }
    fKeyrefStackPos = 0;
    if (fAttributeChecker == null) {
      createTraversers();
    }
    Locale localLocale = fErrorReporter.getLocale();
    fAttributeChecker.reset(fSymbolTable);
    fAttributeGroupTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fAttributeTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fComplexTypeTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fElementTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fGroupTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fKeyrefTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fNotationTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fSimpleTypeTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fUniqueOrKeyTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fWildCardTraverser.reset(fSymbolTable, fValidateAnnotations, localLocale);
    fRedefinedRestrictedAttributeGroupRegistry.clear();
    fRedefinedRestrictedGroupRegistry.clear();
    fGlobalAttrDecls.clear();
    fGlobalAttrGrpDecls.clear();
    fGlobalElemDecls.clear();
    fGlobalGroupDecls.clear();
    fGlobalNotationDecls.clear();
    fGlobalIDConstraintDecls.clear();
    fGlobalTypeDecls.clear();
  }
  
  public void setDeclPool(XSDeclarationPool paramXSDeclarationPool)
  {
    fDeclPool = paramXSDeclarationPool;
  }
  
  public void setDVFactory(SchemaDVFactory paramSchemaDVFactory)
  {
    fDVFactory = paramSchemaDVFactory;
  }
  
  public SchemaDVFactory getDVFactory()
  {
    return fDVFactory;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
  {
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fSecurityManager = ((XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
    fEntityManager = ((XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    XMLEntityResolver localXMLEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
    if (localXMLEntityResolver != null) {
      fSchemaParser.setEntityResolver(localXMLEntityResolver);
    }
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fErrorHandler = fErrorReporter.getErrorHandler();
    fLocale = fErrorReporter.getLocale();
    fValidateAnnotations = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validate-annotations", false);
    fHonourAllSchemaLocations = paramXMLComponentManager.getFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
    fNamespaceGrowth = paramXMLComponentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
    fTolerateDuplicates = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
    try
    {
      if (fErrorHandler != fSchemaParser.getProperty("http://apache.org/xml/properties/internal/error-handler"))
      {
        fSchemaParser.setProperty("http://apache.org/xml/properties/internal/error-handler", fErrorHandler != null ? fErrorHandler : new DefaultErrorHandler());
        if (fAnnotationValidator != null) {
          fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", fErrorHandler != null ? fErrorHandler : new DefaultErrorHandler());
        }
      }
      if (fLocale != fSchemaParser.getProperty("http://apache.org/xml/properties/locale"))
      {
        fSchemaParser.setProperty("http://apache.org/xml/properties/locale", fLocale);
        if (fAnnotationValidator != null) {
          fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", fLocale);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException1) {}
    try
    {
      fSchemaParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", fErrorReporter.getFeature("http://apache.org/xml/features/continue-after-fatal-error"));
    }
    catch (XMLConfigurationException localXMLConfigurationException2) {}
    try
    {
      if (paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false)) {
        fSchemaParser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException3) {}
    try
    {
      if (paramXMLComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false)) {
        fSchemaParser.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException4) {}
    try
    {
      fGrammarPool = ((XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool"));
    }
    catch (XMLConfigurationException localXMLConfigurationException5)
    {
      fGrammarPool = null;
    }
    try
    {
      if (paramXMLComponentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false)) {
        fSchemaParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException6) {}
    try
    {
      if (fSecurityManager != null) {
        fSchemaParser.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException7) {}
    fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    fSchemaParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
    fAccessExternalDTD = fSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    fAccessExternalSchema = fSecurityPropertyMgr.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
  }
  
  void traverseLocalElements()
  {
    fElementTraverser.fDeferTraversingLocalElements = false;
    for (int i = 0; i < fLocalElemStackPos; i++)
    {
      Element localElement = fLocalElementDecl[i];
      XSDocumentInfo localXSDocumentInfo = fLocalElementDecl_schema[i];
      SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(fTargetNamespace);
      fElementTraverser.traverseLocal(fParticle[i], localElement, localXSDocumentInfo, localSchemaGrammar, fAllContext[i], fParent[i], fLocalElemNamespaceContext[i]);
      if (fParticle[i].fType == 0)
      {
        XSModelGroupImpl localXSModelGroupImpl = null;
        if ((fParent[i] instanceof XSComplexTypeDecl))
        {
          XSParticle localXSParticle = ((XSComplexTypeDecl)fParent[i]).getParticle();
          if (localXSParticle != null) {
            localXSModelGroupImpl = (XSModelGroupImpl)localXSParticle.getTerm();
          }
        }
        else
        {
          localXSModelGroupImpl = fParent[i]).fModelGroup;
        }
        if (localXSModelGroupImpl != null) {
          removeParticle(localXSModelGroupImpl, fParticle[i]);
        }
      }
    }
  }
  
  private boolean removeParticle(XSModelGroupImpl paramXSModelGroupImpl, XSParticleDecl paramXSParticleDecl)
  {
    for (int i = 0; i < fParticleCount; i++)
    {
      XSParticleDecl localXSParticleDecl = fParticles[i];
      if (localXSParticleDecl == paramXSParticleDecl)
      {
        for (int j = i; j < fParticleCount - 1; j++) {
          fParticles[j] = fParticles[(j + 1)];
        }
        fParticleCount -= 1;
        return true;
      }
      if ((fType == 3) && (removeParticle((XSModelGroupImpl)fValue, paramXSParticleDecl))) {
        return true;
      }
    }
    return false;
  }
  
  void fillInLocalElemInfo(Element paramElement, XSDocumentInfo paramXSDocumentInfo, int paramInt, XSObject paramXSObject, XSParticleDecl paramXSParticleDecl)
  {
    if (fParticle.length == fLocalElemStackPos)
    {
      XSParticleDecl[] arrayOfXSParticleDecl = new XSParticleDecl[fLocalElemStackPos + 10];
      System.arraycopy(fParticle, 0, arrayOfXSParticleDecl, 0, fLocalElemStackPos);
      fParticle = arrayOfXSParticleDecl;
      Element[] arrayOfElement = new Element[fLocalElemStackPos + 10];
      System.arraycopy(fLocalElementDecl, 0, arrayOfElement, 0, fLocalElemStackPos);
      fLocalElementDecl = arrayOfElement;
      XSDocumentInfo[] arrayOfXSDocumentInfo = new XSDocumentInfo[fLocalElemStackPos + 10];
      System.arraycopy(fLocalElementDecl_schema, 0, arrayOfXSDocumentInfo, 0, fLocalElemStackPos);
      fLocalElementDecl_schema = arrayOfXSDocumentInfo;
      int[] arrayOfInt = new int[fLocalElemStackPos + 10];
      System.arraycopy(fAllContext, 0, arrayOfInt, 0, fLocalElemStackPos);
      fAllContext = arrayOfInt;
      XSObject[] arrayOfXSObject = new XSObject[fLocalElemStackPos + 10];
      System.arraycopy(fParent, 0, arrayOfXSObject, 0, fLocalElemStackPos);
      fParent = arrayOfXSObject;
      String[][] arrayOfString = new String[fLocalElemStackPos + 10][];
      System.arraycopy(fLocalElemNamespaceContext, 0, arrayOfString, 0, fLocalElemStackPos);
      fLocalElemNamespaceContext = arrayOfString;
    }
    fParticle[fLocalElemStackPos] = paramXSParticleDecl;
    fLocalElementDecl[fLocalElemStackPos] = paramElement;
    fLocalElementDecl_schema[fLocalElemStackPos] = paramXSDocumentInfo;
    fAllContext[fLocalElemStackPos] = paramInt;
    fParent[fLocalElemStackPos] = paramXSObject;
    fLocalElemNamespaceContext[(fLocalElemStackPos++)] = fNamespaceSupport.getEffectiveLocalContext();
  }
  
  void checkForDuplicateNames(String paramString, int paramInt, Map<String, Element> paramMap, Map<String, XSDocumentInfo> paramMap1, Element paramElement, XSDocumentInfo paramXSDocumentInfo)
  {
    Object localObject = null;
    if ((localObject = paramMap.get(paramString)) == null)
    {
      if ((fNamespaceGrowth) && (!fTolerateDuplicates)) {
        checkForDuplicateNames(paramString, paramInt, paramElement);
      }
      paramMap.put(paramString, paramElement);
      paramMap1.put(paramString, paramXSDocumentInfo);
    }
    else
    {
      Element localElement1 = (Element)localObject;
      XSDocumentInfo localXSDocumentInfo1 = (XSDocumentInfo)paramMap1.get(paramString);
      if (localElement1 == paramElement) {
        return;
      }
      Element localElement2 = null;
      XSDocumentInfo localXSDocumentInfo2 = null;
      int i = 1;
      if (DOMUtil.getLocalName(localElement2 = DOMUtil.getParent(localElement1)).equals(SchemaSymbols.ELT_REDEFINE))
      {
        localXSDocumentInfo2 = fRedefine2XSDMap != null ? (XSDocumentInfo)fRedefine2XSDMap.get(localElement2) : null;
      }
      else if (DOMUtil.getLocalName(DOMUtil.getParent(paramElement)).equals(SchemaSymbols.ELT_REDEFINE))
      {
        localXSDocumentInfo2 = localXSDocumentInfo1;
        i = 0;
      }
      if (localXSDocumentInfo2 != null)
      {
        if (localXSDocumentInfo1 == paramXSDocumentInfo)
        {
          reportSchemaError("sch-props-correct.2", new Object[] { paramString }, paramElement);
          return;
        }
        String str = paramString.substring(paramString.lastIndexOf(',') + 1) + "_fn3dktizrknc9pi";
        if (localXSDocumentInfo2 == paramXSDocumentInfo)
        {
          paramElement.setAttribute(SchemaSymbols.ATT_NAME, str);
          if (fTargetNamespace == null)
          {
            paramMap.put("," + str, paramElement);
            paramMap1.put("," + str, paramXSDocumentInfo);
          }
          else
          {
            paramMap.put(fTargetNamespace + "," + str, paramElement);
            paramMap1.put(fTargetNamespace + "," + str, paramXSDocumentInfo);
          }
          if (fTargetNamespace == null) {
            checkForDuplicateNames("," + str, paramInt, paramMap, paramMap1, paramElement, paramXSDocumentInfo);
          } else {
            checkForDuplicateNames(fTargetNamespace + "," + str, paramInt, paramMap, paramMap1, paramElement, paramXSDocumentInfo);
          }
        }
        else if (i != 0)
        {
          if (fTargetNamespace == null) {
            checkForDuplicateNames("," + str, paramInt, paramMap, paramMap1, paramElement, paramXSDocumentInfo);
          } else {
            checkForDuplicateNames(fTargetNamespace + "," + str, paramInt, paramMap, paramMap1, paramElement, paramXSDocumentInfo);
          }
        }
        else
        {
          reportSchemaError("sch-props-correct.2", new Object[] { paramString }, paramElement);
        }
      }
      else if (!fTolerateDuplicates)
      {
        reportSchemaError("sch-props-correct.2", new Object[] { paramString }, paramElement);
      }
      else if ((fUnparsedRegistriesExt[paramInt] != null) && (fUnparsedRegistriesExt[paramInt].get(paramString) == paramXSDocumentInfo))
      {
        reportSchemaError("sch-props-correct.2", new Object[] { paramString }, paramElement);
      }
    }
    if (fTolerateDuplicates)
    {
      if (fUnparsedRegistriesExt[paramInt] == null) {
        fUnparsedRegistriesExt[paramInt] = new HashMap();
      }
      fUnparsedRegistriesExt[paramInt].put(paramString, paramXSDocumentInfo);
    }
  }
  
  void checkForDuplicateNames(String paramString, int paramInt, Element paramElement)
  {
    int i = paramString.indexOf(',');
    String str = paramString.substring(0, i);
    SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(emptyString2Null(str));
    if (localSchemaGrammar != null)
    {
      Object localObject = getGlobalDeclFromGrammar(localSchemaGrammar, paramInt, paramString.substring(i + 1));
      if (localObject != null) {
        reportSchemaError("sch-props-correct.2", new Object[] { paramString }, paramElement);
      }
    }
  }
  
  private void renameRedefiningComponents(XSDocumentInfo paramXSDocumentInfo, Element paramElement, String paramString1, String paramString2, String paramString3)
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (paramString1.equals(SchemaSymbols.ELT_SIMPLETYPE))
    {
      localObject1 = DOMUtil.getFirstChildElement(paramElement);
      if (localObject1 == null)
      {
        reportSchemaError("src-redefine.5.a.a", null, paramElement);
      }
      else
      {
        localObject2 = DOMUtil.getLocalName((Node)localObject1);
        if (((String)localObject2).equals(SchemaSymbols.ELT_ANNOTATION)) {
          localObject1 = DOMUtil.getNextSiblingElement((Node)localObject1);
        }
        if (localObject1 == null)
        {
          reportSchemaError("src-redefine.5.a.a", null, paramElement);
        }
        else
        {
          localObject2 = DOMUtil.getLocalName((Node)localObject1);
          if (!((String)localObject2).equals(SchemaSymbols.ELT_RESTRICTION))
          {
            reportSchemaError("src-redefine.5.a.b", new Object[] { localObject2 }, paramElement);
          }
          else
          {
            localObject3 = fAttributeChecker.checkAttributes((Element)localObject1, false, paramXSDocumentInfo);
            localObject4 = (QName)localObject3[XSAttributeChecker.ATTIDX_BASE];
            if ((localObject4 == null) || (uri != fTargetNamespace) || (!localpart.equals(paramString2))) {
              reportSchemaError("src-redefine.5.a.c", new Object[] { localObject2, (fTargetNamespace == null ? "" : fTargetNamespace) + "," + paramString2 }, paramElement);
            } else if ((prefix != null) && (prefix.length() > 0)) {
              ((Element)localObject1).setAttribute(SchemaSymbols.ATT_BASE, prefix + ":" + paramString3);
            } else {
              ((Element)localObject1).setAttribute(SchemaSymbols.ATT_BASE, paramString3);
            }
            fAttributeChecker.returnAttrArray((Object[])localObject3, paramXSDocumentInfo);
          }
        }
      }
    }
    else if (paramString1.equals(SchemaSymbols.ELT_COMPLEXTYPE))
    {
      localObject1 = DOMUtil.getFirstChildElement(paramElement);
      if (localObject1 == null)
      {
        reportSchemaError("src-redefine.5.b.a", null, paramElement);
      }
      else
      {
        if (DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_ANNOTATION)) {
          localObject1 = DOMUtil.getNextSiblingElement((Node)localObject1);
        }
        if (localObject1 == null)
        {
          reportSchemaError("src-redefine.5.b.a", null, paramElement);
        }
        else
        {
          localObject2 = DOMUtil.getFirstChildElement((Node)localObject1);
          if (localObject2 == null)
          {
            reportSchemaError("src-redefine.5.b.b", null, (Element)localObject1);
          }
          else
          {
            localObject3 = DOMUtil.getLocalName((Node)localObject2);
            if (((String)localObject3).equals(SchemaSymbols.ELT_ANNOTATION)) {
              localObject2 = DOMUtil.getNextSiblingElement((Node)localObject2);
            }
            if (localObject2 == null)
            {
              reportSchemaError("src-redefine.5.b.b", null, (Element)localObject1);
            }
            else
            {
              localObject3 = DOMUtil.getLocalName((Node)localObject2);
              if ((!((String)localObject3).equals(SchemaSymbols.ELT_RESTRICTION)) && (!((String)localObject3).equals(SchemaSymbols.ELT_EXTENSION)))
              {
                reportSchemaError("src-redefine.5.b.c", new Object[] { localObject3 }, (Element)localObject2);
              }
              else
              {
                localObject4 = fAttributeChecker.checkAttributes((Element)localObject2, false, paramXSDocumentInfo);
                QName localQName = (QName)localObject4[XSAttributeChecker.ATTIDX_BASE];
                if ((localQName == null) || (uri != fTargetNamespace) || (!localpart.equals(paramString2))) {
                  reportSchemaError("src-redefine.5.b.d", new Object[] { localObject3, (fTargetNamespace == null ? "" : fTargetNamespace) + "," + paramString2 }, (Element)localObject2);
                } else if ((prefix != null) && (prefix.length() > 0)) {
                  ((Element)localObject2).setAttribute(SchemaSymbols.ATT_BASE, prefix + ":" + paramString3);
                } else {
                  ((Element)localObject2).setAttribute(SchemaSymbols.ATT_BASE, paramString3);
                }
              }
            }
          }
        }
      }
    }
    else
    {
      int i;
      if (paramString1.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP))
      {
        localObject1 = fTargetNamespace + "," + paramString2;
        i = changeRedefineGroup((String)localObject1, paramString1, paramString3, paramElement, paramXSDocumentInfo);
        if (i > 1) {
          reportSchemaError("src-redefine.7.1", new Object[] { new Integer(i) }, paramElement);
        } else if (i != 1) {
          if (fTargetNamespace == null) {
            fRedefinedRestrictedAttributeGroupRegistry.put(localObject1, "," + paramString3);
          } else {
            fRedefinedRestrictedAttributeGroupRegistry.put(localObject1, fTargetNamespace + "," + paramString3);
          }
        }
      }
      else if (paramString1.equals(SchemaSymbols.ELT_GROUP))
      {
        localObject1 = fTargetNamespace + "," + paramString2;
        i = changeRedefineGroup((String)localObject1, paramString1, paramString3, paramElement, paramXSDocumentInfo);
        if (i > 1) {
          reportSchemaError("src-redefine.6.1.1", new Object[] { new Integer(i) }, paramElement);
        } else if (i != 1) {
          if (fTargetNamespace == null) {
            fRedefinedRestrictedGroupRegistry.put(localObject1, "," + paramString3);
          } else {
            fRedefinedRestrictedGroupRegistry.put(localObject1, fTargetNamespace + "," + paramString3);
          }
        }
      }
      else
      {
        reportSchemaError("Internal-Error", new Object[] { "could not handle this particular <redefine>; please submit your schemas and instance document in a bug report!" }, paramElement);
      }
    }
  }
  
  private String findQName(String paramString, XSDocumentInfo paramXSDocumentInfo)
  {
    SchemaNamespaceSupport localSchemaNamespaceSupport = fNamespaceSupport;
    int i = paramString.indexOf(':');
    String str1 = XMLSymbols.EMPTY_STRING;
    if (i > 0) {
      str1 = paramString.substring(0, i);
    }
    String str2 = localSchemaNamespaceSupport.getURI(fSymbolTable.addSymbol(str1));
    String str3 = i == 0 ? paramString : paramString.substring(i + 1);
    if ((str1 == XMLSymbols.EMPTY_STRING) && (str2 == null) && (fIsChameleonSchema)) {
      str2 = fTargetNamespace;
    }
    if (str2 == null) {
      return "," + str3;
    }
    return str2 + "," + str3;
  }
  
  private int changeRedefineGroup(String paramString1, String paramString2, String paramString3, Element paramElement, XSDocumentInfo paramXSDocumentInfo)
  {
    int i = 0;
    for (Element localElement = DOMUtil.getFirstChildElement(paramElement); localElement != null; localElement = DOMUtil.getNextSiblingElement(localElement))
    {
      String str1 = DOMUtil.getLocalName(localElement);
      if (!str1.equals(paramString2))
      {
        i += changeRedefineGroup(paramString1, paramString2, paramString3, localElement, paramXSDocumentInfo);
      }
      else
      {
        String str2 = localElement.getAttribute(SchemaSymbols.ATT_REF);
        if (str2.length() != 0)
        {
          String str3 = findQName(str2, paramXSDocumentInfo);
          if (paramString1.equals(str3))
          {
            String str4 = XMLSymbols.EMPTY_STRING;
            int j = str2.indexOf(":");
            if (j > 0)
            {
              str4 = str2.substring(0, j);
              localElement.setAttribute(SchemaSymbols.ATT_REF, str4 + ":" + paramString3);
            }
            else
            {
              localElement.setAttribute(SchemaSymbols.ATT_REF, paramString3);
            }
            i++;
            if (paramString2.equals(SchemaSymbols.ELT_GROUP))
            {
              String str5 = localElement.getAttribute(SchemaSymbols.ATT_MINOCCURS);
              String str6 = localElement.getAttribute(SchemaSymbols.ATT_MAXOCCURS);
              if (((str6.length() != 0) && (!str6.equals("1"))) || ((str5.length() != 0) && (!str5.equals("1")))) {
                reportSchemaError("src-redefine.6.1.2", new Object[] { str2 }, localElement);
              }
            }
          }
        }
      }
    }
    return i;
  }
  
  private XSDocumentInfo findXSDocumentForDecl(XSDocumentInfo paramXSDocumentInfo1, Element paramElement, XSDocumentInfo paramXSDocumentInfo2)
  {
    XSDocumentInfo localXSDocumentInfo1 = paramXSDocumentInfo2;
    if (localXSDocumentInfo1 == null) {
      return null;
    }
    XSDocumentInfo localXSDocumentInfo2 = (XSDocumentInfo)localXSDocumentInfo1;
    return localXSDocumentInfo2;
  }
  
  private boolean nonAnnotationContent(Element paramElement)
  {
    for (Element localElement = DOMUtil.getFirstChildElement(paramElement); localElement != null; localElement = DOMUtil.getNextSiblingElement(localElement)) {
      if (!DOMUtil.getLocalName(localElement).equals(SchemaSymbols.ELT_ANNOTATION)) {
        return true;
      }
    }
    return false;
  }
  
  private void setSchemasVisible(XSDocumentInfo paramXSDocumentInfo)
  {
    if (DOMUtil.isHidden(fSchemaElement, fHiddenNodes))
    {
      DOMUtil.setVisible(fSchemaElement, fHiddenNodes);
      Vector localVector = (Vector)fDependencyMap.get(paramXSDocumentInfo);
      for (int i = 0; i < localVector.size(); i++) {
        setSchemasVisible((XSDocumentInfo)localVector.elementAt(i));
      }
    }
  }
  
  public SimpleLocator element2Locator(Element paramElement)
  {
    if (!(paramElement instanceof ElementImpl)) {
      return null;
    }
    SimpleLocator localSimpleLocator = new SimpleLocator();
    return element2Locator(paramElement, localSimpleLocator) ? localSimpleLocator : null;
  }
  
  public boolean element2Locator(Element paramElement, SimpleLocator paramSimpleLocator)
  {
    if (paramSimpleLocator == null) {
      return false;
    }
    if ((paramElement instanceof ElementImpl))
    {
      ElementImpl localElementImpl = (ElementImpl)paramElement;
      Document localDocument = localElementImpl.getOwnerDocument();
      String str = (String)fDoc2SystemId.get(DOMUtil.getRoot(localDocument));
      int i = localElementImpl.getLineNumber();
      int j = localElementImpl.getColumnNumber();
      paramSimpleLocator.setValues(str, str, i, j, localElementImpl.getCharacterOffset());
      return true;
    }
    return false;
  }
  
  private Element getElementFromMap(Map<String, Element> paramMap, String paramString)
  {
    if (paramMap == null) {
      return null;
    }
    return (Element)paramMap.get(paramString);
  }
  
  private XSDocumentInfo getDocInfoFromMap(Map<String, XSDocumentInfo> paramMap, String paramString)
  {
    if (paramMap == null) {
      return null;
    }
    return (XSDocumentInfo)paramMap.get(paramString);
  }
  
  private Object getFromMap(Map paramMap, String paramString)
  {
    if (paramMap == null) {
      return null;
    }
    return paramMap.get(paramString);
  }
  
  void reportSchemaFatalError(String paramString, Object[] paramArrayOfObject, Element paramElement)
  {
    reportSchemaErr(paramString, paramArrayOfObject, paramElement, (short)2, null);
  }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject, Element paramElement)
  {
    reportSchemaErr(paramString, paramArrayOfObject, paramElement, (short)1, null);
  }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject, Element paramElement, Exception paramException)
  {
    reportSchemaErr(paramString, paramArrayOfObject, paramElement, (short)1, paramException);
  }
  
  void reportSchemaWarning(String paramString, Object[] paramArrayOfObject, Element paramElement)
  {
    reportSchemaErr(paramString, paramArrayOfObject, paramElement, (short)0, null);
  }
  
  void reportSchemaWarning(String paramString, Object[] paramArrayOfObject, Element paramElement, Exception paramException)
  {
    reportSchemaErr(paramString, paramArrayOfObject, paramElement, (short)0, paramException);
  }
  
  void reportSchemaErr(String paramString, Object[] paramArrayOfObject, Element paramElement, short paramShort, Exception paramException)
  {
    if (element2Locator(paramElement, xl)) {
      fErrorReporter.reportError(xl, "http://www.w3.org/TR/xml-schema-1", paramString, paramArrayOfObject, paramShort, paramException);
    } else {
      fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", paramString, paramArrayOfObject, paramShort, paramException);
    }
  }
  
  public void setGenerateSyntheticAnnotations(boolean paramBoolean)
  {
    fSchemaParser.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", paramBoolean);
  }
  
  private static final class SAX2XNIUtil
    extends ErrorHandlerWrapper
  {
    private SAX2XNIUtil() {}
    
    public static XMLParseException createXMLParseException0(SAXParseException paramSAXParseException)
    {
      return createXMLParseException(paramSAXParseException);
    }
    
    public static XNIException createXNIException0(SAXException paramSAXException)
    {
      return createXNIException(paramSAXException);
    }
  }
  
  private static class XSAnnotationGrammarPool
    implements XMLGrammarPool
  {
    private XSGrammarBucket fGrammarBucket;
    private Grammar[] fInitialGrammarSet;
    
    private XSAnnotationGrammarPool() {}
    
    public Grammar[] retrieveInitialGrammarSet(String paramString)
    {
      if (paramString == "http://www.w3.org/2001/XMLSchema")
      {
        if (fInitialGrammarSet == null) {
          if (fGrammarBucket == null)
          {
            fInitialGrammarSet = new Grammar[] { SchemaGrammar.Schema4Annotations.INSTANCE };
          }
          else
          {
            SchemaGrammar[] arrayOfSchemaGrammar = fGrammarBucket.getGrammars();
            for (int i = 0; i < arrayOfSchemaGrammar.length; i++) {
              if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(arrayOfSchemaGrammar[i].getTargetNamespace()))
              {
                fInitialGrammarSet = arrayOfSchemaGrammar;
                return fInitialGrammarSet;
              }
            }
            Grammar[] arrayOfGrammar = new Grammar[arrayOfSchemaGrammar.length + 1];
            System.arraycopy(arrayOfSchemaGrammar, 0, arrayOfGrammar, 0, arrayOfSchemaGrammar.length);
            arrayOfGrammar[(arrayOfGrammar.length - 1)] = SchemaGrammar.Schema4Annotations.INSTANCE;
            fInitialGrammarSet = arrayOfGrammar;
          }
        }
        return fInitialGrammarSet;
      }
      return new Grammar[0];
    }
    
    public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
    
    public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      if (paramXMLGrammarDescription.getGrammarType() == "http://www.w3.org/2001/XMLSchema")
      {
        String str = ((XMLSchemaDescription)paramXMLGrammarDescription).getTargetNamespace();
        if (fGrammarBucket != null)
        {
          SchemaGrammar localSchemaGrammar = fGrammarBucket.getGrammar(str);
          if (localSchemaGrammar != null) {
            return localSchemaGrammar;
          }
        }
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(str)) {
          return SchemaGrammar.Schema4Annotations.INSTANCE;
        }
      }
      return null;
    }
    
    public void refreshGrammars(XSGrammarBucket paramXSGrammarBucket)
    {
      fGrammarBucket = paramXSGrammarBucket;
      fInitialGrammarSet = null;
    }
    
    public void lockPool() {}
    
    public void unlockPool() {}
    
    public void clear() {}
  }
  
  private static class XSDKey
  {
    String systemId;
    short referType;
    String referNS;
    
    XSDKey(String paramString1, short paramShort, String paramString2)
    {
      systemId = paramString1;
      referType = paramShort;
      referNS = paramString2;
    }
    
    public int hashCode()
    {
      return referNS == null ? 0 : referNS.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof XSDKey)) {
        return false;
      }
      XSDKey localXSDKey = (XSDKey)paramObject;
      if (referNS != referNS) {
        return false;
      }
      return (systemId != null) && (systemId.equals(systemId));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */