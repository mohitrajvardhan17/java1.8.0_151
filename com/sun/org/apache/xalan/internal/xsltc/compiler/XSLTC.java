package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class XSLTC
{
  private Parser _parser;
  private XMLReader _reader = null;
  private SourceLoader _loader = null;
  private Stylesheet _stylesheet;
  private int _modeSerial = 1;
  private int _stylesheetSerial = 1;
  private int _stepPatternSerial = 1;
  private int _helperClassSerial = 0;
  private int _attributeSetSerial = 0;
  private int[] _numberFieldIndexes;
  private int _nextGType;
  private Vector _namesIndex;
  private Map<String, Integer> _elements;
  private Map<String, Integer> _attributes;
  private int _nextNSType;
  private Vector _namespaceIndex;
  private Map<String, Integer> _namespaces;
  private Map<String, Integer> _namespacePrefixes;
  private Vector m_characterData;
  public static final int FILE_OUTPUT = 0;
  public static final int JAR_OUTPUT = 1;
  public static final int BYTEARRAY_OUTPUT = 2;
  public static final int CLASSLOADER_OUTPUT = 3;
  public static final int BYTEARRAY_AND_FILE_OUTPUT = 4;
  public static final int BYTEARRAY_AND_JAR_OUTPUT = 5;
  private boolean _debug = false;
  private String _jarFileName = null;
  private String _className = null;
  private String _packageName = null;
  private File _destDir = null;
  private int _outputType = 0;
  private Vector _classes;
  private Vector _bcelClasses;
  private boolean _callsNodeset = false;
  private boolean _multiDocument = false;
  private boolean _hasIdCall = false;
  private boolean _templateInlining = false;
  private boolean _isSecureProcessing = false;
  private boolean _useServicesMechanism = true;
  private String _accessExternalStylesheet = "all";
  private String _accessExternalDTD = "all";
  private XMLSecurityManager _xmlSecurityManager;
  private final FeatureManager _featureManager;
  private ClassLoader _extensionClassLoader;
  private final Map<String, Class> _externalExtensionFunctions;
  
  public XSLTC(boolean paramBoolean, FeatureManager paramFeatureManager)
  {
    _parser = new Parser(this, paramBoolean);
    _featureManager = paramFeatureManager;
    _extensionClassLoader = null;
    _externalExtensionFunctions = new HashMap();
  }
  
  public void setSecureProcessing(boolean paramBoolean)
  {
    _isSecureProcessing = paramBoolean;
  }
  
  public boolean isSecureProcessing()
  {
    return _isSecureProcessing;
  }
  
  public boolean useServicesMechnism()
  {
    return _useServicesMechanism;
  }
  
  public void setServicesMechnism(boolean paramBoolean)
  {
    _useServicesMechanism = paramBoolean;
  }
  
  public boolean getFeature(FeatureManager.Feature paramFeature)
  {
    return _featureManager.isFeatureEnabled(paramFeature);
  }
  
  public Object getProperty(String paramString)
  {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet")) {
      return _accessExternalStylesheet;
    }
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
      return _accessExternalDTD;
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      return _xmlSecurityManager;
    }
    if (paramString.equals("jdk.xml.transform.extensionClassLoader")) {
      return _extensionClassLoader;
    }
    return null;
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalStylesheet"))
    {
      _accessExternalStylesheet = ((String)paramObject);
    }
    else if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD"))
    {
      _accessExternalDTD = ((String)paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      _xmlSecurityManager = ((XMLSecurityManager)paramObject);
    }
    else if (paramString.equals("jdk.xml.transform.extensionClassLoader"))
    {
      _extensionClassLoader = ((ClassLoader)paramObject);
      _externalExtensionFunctions.clear();
    }
  }
  
  public Parser getParser()
  {
    return _parser;
  }
  
  public void setOutputType(int paramInt)
  {
    _outputType = paramInt;
  }
  
  public Properties getOutputProperties()
  {
    return _parser.getOutputProperties();
  }
  
  public void init()
  {
    reset();
    _reader = null;
    _classes = new Vector();
    _bcelClasses = new Vector();
  }
  
  private void setExternalExtensionFunctions(String paramString, Class paramClass)
  {
    if ((_isSecureProcessing) && (paramClass != null) && (!_externalExtensionFunctions.containsKey(paramString))) {
      _externalExtensionFunctions.put(paramString, paramClass);
    }
  }
  
  Class loadExternalFunction(String paramString)
    throws ClassNotFoundException
  {
    Class localClass = null;
    if (_externalExtensionFunctions.containsKey(paramString))
    {
      localClass = (Class)_externalExtensionFunctions.get(paramString);
    }
    else if (_extensionClassLoader != null)
    {
      localClass = Class.forName(paramString, true, _extensionClassLoader);
      setExternalExtensionFunctions(paramString, localClass);
    }
    if (localClass == null) {
      throw new ClassNotFoundException(paramString);
    }
    return localClass;
  }
  
  public Map<String, Class> getExternalExtensionFunctions()
  {
    return Collections.unmodifiableMap(_externalExtensionFunctions);
  }
  
  private void reset()
  {
    _nextGType = 14;
    _elements = new HashMap();
    _attributes = new HashMap();
    _namespaces = new HashMap();
    _namespaces.put("", new Integer(_nextNSType));
    _namesIndex = new Vector(128);
    _namespaceIndex = new Vector(32);
    _namespacePrefixes = new HashMap();
    _stylesheet = null;
    _parser.init();
    _modeSerial = 1;
    _stylesheetSerial = 1;
    _stepPatternSerial = 1;
    _helperClassSerial = 0;
    _attributeSetSerial = 0;
    _multiDocument = false;
    _hasIdCall = false;
    _numberFieldIndexes = new int[] { -1, -1, -1 };
    _externalExtensionFunctions.clear();
  }
  
  public void setSourceLoader(SourceLoader paramSourceLoader)
  {
    _loader = paramSourceLoader;
  }
  
  public void setTemplateInlining(boolean paramBoolean)
  {
    _templateInlining = paramBoolean;
  }
  
  public boolean getTemplateInlining()
  {
    return _templateInlining;
  }
  
  public void setPIParameters(String paramString1, String paramString2, String paramString3)
  {
    _parser.setPIParameters(paramString1, paramString2, paramString3);
  }
  
  public boolean compile(URL paramURL)
  {
    try
    {
      InputStream localInputStream = paramURL.openStream();
      InputSource localInputSource = new InputSource(localInputStream);
      localInputSource.setSystemId(paramURL.toString());
      return compile(localInputSource, _className);
    }
    catch (IOException localIOException)
    {
      _parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", localIOException));
    }
    return false;
  }
  
  public boolean compile(URL paramURL, String paramString)
  {
    try
    {
      InputStream localInputStream = paramURL.openStream();
      InputSource localInputSource = new InputSource(localInputStream);
      localInputSource.setSystemId(paramURL.toString());
      return compile(localInputSource, paramString);
    }
    catch (IOException localIOException)
    {
      _parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", localIOException));
    }
    return false;
  }
  
  public boolean compile(InputStream paramInputStream, String paramString)
  {
    InputSource localInputSource = new InputSource(paramInputStream);
    localInputSource.setSystemId(paramString);
    return compile(localInputSource, paramString);
  }
  
  public boolean compile(InputSource paramInputSource, String paramString)
  {
    try
    {
      reset();
      String str = null;
      if (paramInputSource != null) {
        str = paramInputSource.getSystemId();
      }
      if (_className == null)
      {
        if (paramString != null) {
          setClassName(paramString);
        } else if ((str != null) && (!str.equals(""))) {
          setClassName(Util.baseName(str));
        }
        if ((_className == null) || (_className.length() == 0)) {
          setClassName("GregorSamsa");
        }
      }
      SyntaxTreeNode localSyntaxTreeNode = null;
      if (_reader == null) {
        localSyntaxTreeNode = _parser.parse(paramInputSource);
      } else {
        localSyntaxTreeNode = _parser.parse(_reader, paramInputSource);
      }
      if ((!_parser.errorsFound()) && (localSyntaxTreeNode != null))
      {
        _stylesheet = _parser.makeStylesheet(localSyntaxTreeNode);
        _stylesheet.setSourceLoader(_loader);
        _stylesheet.setSystemId(str);
        _stylesheet.setParentStylesheet(null);
        _stylesheet.setTemplateInlining(_templateInlining);
        _parser.setCurrentStylesheet(_stylesheet);
        _parser.createAST(_stylesheet);
      }
      if ((!_parser.errorsFound()) && (_stylesheet != null))
      {
        _stylesheet.setCallsNodeset(_callsNodeset);
        _stylesheet.setMultiDocument(_multiDocument);
        _stylesheet.setHasIdCall(_hasIdCall);
        synchronized (getClass())
        {
          _stylesheet.translate();
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      _parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", localException));
    }
    catch (Error localError)
    {
      if (_debug) {
        localError.printStackTrace();
      }
      _parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", localError));
    }
    finally
    {
      _reader = null;
    }
    return !_parser.errorsFound();
  }
  
  public boolean compile(Vector paramVector)
  {
    int i = paramVector.size();
    if (i == 0) {
      return true;
    }
    if (i == 1)
    {
      localObject1 = paramVector.firstElement();
      if ((localObject1 instanceof URL)) {
        return compile((URL)localObject1);
      }
      return false;
    }
    Object localObject1 = paramVector.elements();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      _className = null;
      Object localObject2 = ((Enumeration)localObject1).nextElement();
      if (((localObject2 instanceof URL)) && (!compile((URL)localObject2))) {
        return false;
      }
    }
    return true;
  }
  
  public byte[][] getBytecodes()
  {
    int i = _classes.size();
    byte[][] arrayOfByte = new byte[i][1];
    for (int j = 0; j < i; j++) {
      arrayOfByte[j] = ((byte[])(byte[])_classes.elementAt(j));
    }
    return arrayOfByte;
  }
  
  public byte[][] compile(String paramString, InputSource paramInputSource, int paramInt)
  {
    _outputType = paramInt;
    if (compile(paramInputSource, paramString)) {
      return getBytecodes();
    }
    return (byte[][])null;
  }
  
  public byte[][] compile(String paramString, InputSource paramInputSource)
  {
    return compile(paramString, paramInputSource, 2);
  }
  
  public void setXMLReader(XMLReader paramXMLReader)
  {
    _reader = paramXMLReader;
  }
  
  public XMLReader getXMLReader()
  {
    return _reader;
  }
  
  public Vector getErrors()
  {
    return _parser.getErrors();
  }
  
  public Vector getWarnings()
  {
    return _parser.getWarnings();
  }
  
  public void printErrors()
  {
    _parser.printErrors();
  }
  
  public void printWarnings()
  {
    _parser.printWarnings();
  }
  
  protected void setMultiDocument(boolean paramBoolean)
  {
    _multiDocument = paramBoolean;
  }
  
  public boolean isMultiDocument()
  {
    return _multiDocument;
  }
  
  protected void setCallsNodeset(boolean paramBoolean)
  {
    if (paramBoolean) {
      setMultiDocument(paramBoolean);
    }
    _callsNodeset = paramBoolean;
  }
  
  public boolean callsNodeset()
  {
    return _callsNodeset;
  }
  
  protected void setHasIdCall(boolean paramBoolean)
  {
    _hasIdCall = paramBoolean;
  }
  
  public boolean hasIdCall()
  {
    return _hasIdCall;
  }
  
  public void setClassName(String paramString)
  {
    String str1 = Util.baseName(paramString);
    String str2 = Util.noExtName(str1);
    String str3 = Util.toJavaName(str2);
    if (_packageName == null) {
      _className = str3;
    } else {
      _className = (_packageName + '.' + str3);
    }
  }
  
  public String getClassName()
  {
    return _className;
  }
  
  private String classFileName(String paramString)
  {
    return paramString.replace('.', File.separatorChar) + ".class";
  }
  
  private File getOutputFile(String paramString)
  {
    if (_destDir != null) {
      return new File(_destDir, classFileName(paramString));
    }
    return new File(classFileName(paramString));
  }
  
  public boolean setDestDirectory(String paramString)
  {
    File localFile = new File(paramString);
    if ((SecuritySupport.getFileExists(localFile)) || (localFile.mkdirs()))
    {
      _destDir = localFile;
      return true;
    }
    _destDir = null;
    return false;
  }
  
  public void setPackageName(String paramString)
  {
    _packageName = paramString;
    if (_className != null) {
      setClassName(_className);
    }
  }
  
  public void setJarFileName(String paramString)
  {
    String str = ".jar";
    if (paramString.endsWith(".jar")) {
      _jarFileName = paramString;
    } else {
      _jarFileName = (paramString + ".jar");
    }
    _outputType = 1;
  }
  
  public String getJarFileName()
  {
    return _jarFileName;
  }
  
  public void setStylesheet(Stylesheet paramStylesheet)
  {
    if (_stylesheet == null) {
      _stylesheet = paramStylesheet;
    }
  }
  
  public Stylesheet getStylesheet()
  {
    return _stylesheet;
  }
  
  public int registerAttribute(QName paramQName)
  {
    Integer localInteger = (Integer)_attributes.get(paramQName.toString());
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(_nextGType++);
      _attributes.put(paramQName.toString(), localInteger);
      String str1 = paramQName.getNamespace();
      String str2 = "@" + paramQName.getLocalPart();
      if ((str1 != null) && (!str1.equals(""))) {
        _namesIndex.addElement(str1 + ":" + str2);
      } else {
        _namesIndex.addElement(str2);
      }
      if (paramQName.getLocalPart().equals("*")) {
        registerNamespace(paramQName.getNamespace());
      }
    }
    return localInteger.intValue();
  }
  
  public int registerElement(QName paramQName)
  {
    Integer localInteger = (Integer)_elements.get(paramQName.toString());
    if (localInteger == null)
    {
      _elements.put(paramQName.toString(), localInteger = Integer.valueOf(_nextGType++));
      _namesIndex.addElement(paramQName.toString());
    }
    if (paramQName.getLocalPart().equals("*")) {
      registerNamespace(paramQName.getNamespace());
    }
    return localInteger.intValue();
  }
  
  public int registerNamespacePrefix(QName paramQName)
  {
    Integer localInteger = (Integer)_namespacePrefixes.get(paramQName.toString());
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(_nextGType++);
      _namespacePrefixes.put(paramQName.toString(), localInteger);
      String str = paramQName.getNamespace();
      if ((str != null) && (!str.equals(""))) {
        _namesIndex.addElement("?");
      } else {
        _namesIndex.addElement("?" + paramQName.getLocalPart());
      }
    }
    return localInteger.intValue();
  }
  
  public int registerNamespace(String paramString)
  {
    Integer localInteger = (Integer)_namespaces.get(paramString);
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(_nextNSType++);
      _namespaces.put(paramString, localInteger);
      _namespaceIndex.addElement(paramString);
    }
    return localInteger.intValue();
  }
  
  public int nextModeSerial()
  {
    return _modeSerial++;
  }
  
  public int nextStylesheetSerial()
  {
    return _stylesheetSerial++;
  }
  
  public int nextStepPatternSerial()
  {
    return _stepPatternSerial++;
  }
  
  public int[] getNumberFieldIndexes()
  {
    return _numberFieldIndexes;
  }
  
  public int nextHelperClassSerial()
  {
    return _helperClassSerial++;
  }
  
  public int nextAttributeSetSerial()
  {
    return _attributeSetSerial++;
  }
  
  public Vector getNamesIndex()
  {
    return _namesIndex;
  }
  
  public Vector getNamespaceIndex()
  {
    return _namespaceIndex;
  }
  
  public String getHelperClassName()
  {
    return getClassName() + '$' + _helperClassSerial++;
  }
  
  public void dumpClass(JavaClass paramJavaClass)
  {
    Object localObject;
    if ((_outputType == 0) || (_outputType == 4))
    {
      localObject = getOutputFile(paramJavaClass.getClassName());
      String str = ((File)localObject).getParent();
      if (str != null)
      {
        File localFile = new File(str);
        if (!SecuritySupport.getFileExists(localFile)) {
          localFile.mkdirs();
        }
      }
    }
    try
    {
      switch (_outputType)
      {
      case 0: 
        paramJavaClass.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(paramJavaClass.getClassName()))));
        break;
      case 1: 
        _bcelClasses.addElement(paramJavaClass);
        break;
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        localObject = new ByteArrayOutputStream(2048);
        paramJavaClass.dump((OutputStream)localObject);
        _classes.addElement(((ByteArrayOutputStream)localObject).toByteArray());
        if (_outputType == 4) {
          paramJavaClass.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(paramJavaClass.getClassName()))));
        } else if (_outputType == 5) {
          _bcelClasses.addElement(paramJavaClass);
        }
        break;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private String entryName(File paramFile)
    throws IOException
  {
    return paramFile.getName().replace(File.separatorChar, '/');
  }
  
  public void outputToJar()
    throws IOException
  {
    Manifest localManifest = new Manifest();
    Attributes localAttributes = localManifest.getMainAttributes();
    localAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.2");
    Map localMap = localManifest.getEntries();
    Enumeration localEnumeration = _bcelClasses.elements();
    String str1 = new Date().toString();
    Attributes.Name localName = new Attributes.Name("Date");
    Object localObject3;
    while (localEnumeration.hasMoreElements())
    {
      localObject1 = (JavaClass)localEnumeration.nextElement();
      localObject2 = ((JavaClass)localObject1).getClassName().replace('.', '/');
      localObject3 = new Attributes();
      ((Attributes)localObject3).put(localName, str1);
      localMap.put((String)localObject2 + ".class", localObject3);
    }
    Object localObject1 = new File(_destDir, _jarFileName);
    Object localObject2 = new JarOutputStream(new FileOutputStream((File)localObject1), localManifest);
    localEnumeration = _bcelClasses.elements();
    while (localEnumeration.hasMoreElements())
    {
      localObject3 = (JavaClass)localEnumeration.nextElement();
      String str2 = ((JavaClass)localObject3).getClassName().replace('.', '/');
      ((JarOutputStream)localObject2).putNextEntry(new JarEntry(str2 + ".class"));
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
      ((JavaClass)localObject3).dump(localByteArrayOutputStream);
      localByteArrayOutputStream.writeTo((OutputStream)localObject2);
    }
    ((JarOutputStream)localObject2).close();
  }
  
  public void setDebug(boolean paramBoolean)
  {
    _debug = paramBoolean;
  }
  
  public boolean debug()
  {
    return _debug;
  }
  
  public String getCharacterData(int paramInt)
  {
    return ((StringBuffer)m_characterData.elementAt(paramInt)).toString();
  }
  
  public int getCharacterDataCount()
  {
    return m_characterData != null ? m_characterData.size() : 0;
  }
  
  public int addCharacterData(String paramString)
  {
    StringBuffer localStringBuffer;
    if (m_characterData == null)
    {
      m_characterData = new Vector();
      localStringBuffer = new StringBuffer();
      m_characterData.addElement(localStringBuffer);
    }
    else
    {
      localStringBuffer = (StringBuffer)m_characterData.elementAt(m_characterData.size() - 1);
    }
    if (paramString.length() + localStringBuffer.length() > 21845)
    {
      localStringBuffer = new StringBuffer();
      m_characterData.addElement(localStringBuffer);
    }
    int i = localStringBuffer.length();
    localStringBuffer.append(paramString);
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XSLTC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */