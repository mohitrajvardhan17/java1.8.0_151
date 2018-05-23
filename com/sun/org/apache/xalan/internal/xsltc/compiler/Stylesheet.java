package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.FieldGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.PUTSTATIC;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.util.InstructionFinder;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public final class Stylesheet
  extends SyntaxTreeNode
{
  private String _version;
  private QName _name;
  private String _systemId;
  private Stylesheet _parentStylesheet;
  private Vector _globals = new Vector();
  private Boolean _hasLocalParams = null;
  private String _className;
  private final Vector _templates = new Vector();
  private Vector _allValidTemplates = null;
  private int _nextModeSerial = 1;
  private final Map<String, Mode> _modes = new HashMap();
  private Mode _defaultMode;
  private final Map<String, String> _extensions = new HashMap();
  public Stylesheet _importedFrom = null;
  public Stylesheet _includedFrom = null;
  private Vector _includedStylesheets = null;
  private int _importPrecedence = 1;
  private int _minimumDescendantPrecedence = -1;
  private Map<String, Key> _keys = new HashMap();
  private SourceLoader _loader = null;
  private boolean _numberFormattingUsed = false;
  private boolean _simplified = false;
  private boolean _multiDocument = false;
  private boolean _callsNodeset = false;
  private boolean _hasIdCall = false;
  private boolean _templateInlining = false;
  private Output _lastOutputElement = null;
  private Properties _outputProperties = null;
  private int _outputMethod = 0;
  public static final int UNKNOWN_OUTPUT = 0;
  public static final int XML_OUTPUT = 1;
  public static final int HTML_OUTPUT = 2;
  public static final int TEXT_OUTPUT = 3;
  
  public Stylesheet() {}
  
  public int getOutputMethod()
  {
    return _outputMethod;
  }
  
  private void checkOutputMethod()
  {
    if (_lastOutputElement != null)
    {
      String str = _lastOutputElement.getOutputMethod();
      if (str != null) {
        if (str.equals("xml")) {
          _outputMethod = 1;
        } else if (str.equals("html")) {
          _outputMethod = 2;
        } else if (str.equals("text")) {
          _outputMethod = 3;
        }
      }
    }
  }
  
  public boolean getTemplateInlining()
  {
    return _templateInlining;
  }
  
  public void setTemplateInlining(boolean paramBoolean)
  {
    _templateInlining = paramBoolean;
  }
  
  public boolean isSimplified()
  {
    return _simplified;
  }
  
  public void setSimplified()
  {
    _simplified = true;
  }
  
  public void setHasIdCall(boolean paramBoolean)
  {
    _hasIdCall = paramBoolean;
  }
  
  public void setOutputProperty(String paramString1, String paramString2)
  {
    if (_outputProperties == null) {
      _outputProperties = new Properties();
    }
    _outputProperties.setProperty(paramString1, paramString2);
  }
  
  public void setOutputProperties(Properties paramProperties)
  {
    _outputProperties = paramProperties;
  }
  
  public Properties getOutputProperties()
  {
    return _outputProperties;
  }
  
  public Output getLastOutputElement()
  {
    return _lastOutputElement;
  }
  
  public void setMultiDocument(boolean paramBoolean)
  {
    _multiDocument = paramBoolean;
  }
  
  public boolean isMultiDocument()
  {
    return _multiDocument;
  }
  
  public void setCallsNodeset(boolean paramBoolean)
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
  
  public void numberFormattingUsed()
  {
    _numberFormattingUsed = true;
    Stylesheet localStylesheet = getParentStylesheet();
    if (null != localStylesheet) {
      localStylesheet.numberFormattingUsed();
    }
  }
  
  public void setImportPrecedence(int paramInt)
  {
    _importPrecedence = paramInt;
    Iterator localIterator = elements();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator.next();
      if ((localObject instanceof Include))
      {
        Stylesheet localStylesheet = ((Include)localObject).getIncludedStylesheet();
        if ((localStylesheet != null) && (_includedFrom == this)) {
          localStylesheet.setImportPrecedence(paramInt);
        }
      }
    }
    if (_importedFrom != null)
    {
      if (_importedFrom.getImportPrecedence() < paramInt)
      {
        localObject = getParser();
        int i = ((Parser)localObject).getNextImportPrecedence();
        _importedFrom.setImportPrecedence(i);
      }
    }
    else if ((_includedFrom != null) && (_includedFrom.getImportPrecedence() != paramInt)) {
      _includedFrom.setImportPrecedence(paramInt);
    }
  }
  
  public int getImportPrecedence()
  {
    return _importPrecedence;
  }
  
  public int getMinimumDescendantPrecedence()
  {
    if (_minimumDescendantPrecedence == -1)
    {
      int i = getImportPrecedence();
      int j = _includedStylesheets != null ? _includedStylesheets.size() : 0;
      for (int k = 0; k < j; k++)
      {
        int m = ((Stylesheet)_includedStylesheets.elementAt(k)).getMinimumDescendantPrecedence();
        if (m < i) {
          i = m;
        }
      }
      _minimumDescendantPrecedence = i;
    }
    return _minimumDescendantPrecedence;
  }
  
  public boolean checkForLoop(String paramString)
  {
    if ((_systemId != null) && (_systemId.equals(paramString))) {
      return true;
    }
    if (_parentStylesheet != null) {
      return _parentStylesheet.checkForLoop(paramString);
    }
    return false;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _name = makeStylesheetName("__stylesheet_");
  }
  
  public void setParentStylesheet(Stylesheet paramStylesheet)
  {
    _parentStylesheet = paramStylesheet;
  }
  
  public Stylesheet getParentStylesheet()
  {
    return _parentStylesheet;
  }
  
  public void setImportingStylesheet(Stylesheet paramStylesheet)
  {
    _importedFrom = paramStylesheet;
    paramStylesheet.addIncludedStylesheet(this);
  }
  
  public void setIncludingStylesheet(Stylesheet paramStylesheet)
  {
    _includedFrom = paramStylesheet;
    paramStylesheet.addIncludedStylesheet(this);
  }
  
  public void addIncludedStylesheet(Stylesheet paramStylesheet)
  {
    if (_includedStylesheets == null) {
      _includedStylesheets = new Vector();
    }
    _includedStylesheets.addElement(paramStylesheet);
  }
  
  public void setSystemId(String paramString)
  {
    if (paramString != null) {
      _systemId = SystemIDResolver.getAbsoluteURI(paramString);
    }
  }
  
  public String getSystemId()
  {
    return _systemId;
  }
  
  public void setSourceLoader(SourceLoader paramSourceLoader)
  {
    _loader = paramSourceLoader;
  }
  
  public SourceLoader getSourceLoader()
  {
    return _loader;
  }
  
  private QName makeStylesheetName(String paramString)
  {
    return getParser().getQName(paramString + getXSLTC().nextStylesheetSerial());
  }
  
  public boolean hasGlobals()
  {
    return _globals.size() > 0;
  }
  
  public boolean hasLocalParams()
  {
    if (_hasLocalParams == null)
    {
      Vector localVector = getAllValidTemplates();
      int i = localVector.size();
      for (int j = 0; j < i; j++)
      {
        Template localTemplate = (Template)localVector.elementAt(j);
        if (localTemplate.hasParams())
        {
          _hasLocalParams = Boolean.TRUE;
          return true;
        }
      }
      _hasLocalParams = Boolean.FALSE;
      return false;
    }
    return _hasLocalParams.booleanValue();
  }
  
  protected void addPrefixMapping(String paramString1, String paramString2)
  {
    if ((paramString1.equals("")) && (paramString2.equals("http://www.w3.org/1999/xhtml"))) {
      return;
    }
    super.addPrefixMapping(paramString1, paramString2);
  }
  
  private void extensionURI(String paramString, SymbolTable paramSymbolTable)
  {
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str1 = localStringTokenizer.nextToken();
        String str2 = lookupNamespace(str1);
        if (str2 != null) {
          _extensions.put(str2, str1);
        }
      }
    }
  }
  
  public boolean isExtension(String paramString)
  {
    return _extensions.get(paramString) != null;
  }
  
  public void declareExtensionPrefixes(Parser paramParser)
  {
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    String str = getAttribute("extension-element-prefixes");
    extensionURI(str, localSymbolTable);
  }
  
  public void parseContents(Parser paramParser)
  {
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    addPrefixMapping("xml", "http://www.w3.org/XML/1998/namespace");
    Stylesheet localStylesheet = localSymbolTable.addStylesheet(_name, this);
    Object localObject;
    if (localStylesheet != null)
    {
      localObject = new ErrorMsg("MULTIPLE_STYLESHEET_ERR", this);
      paramParser.reportError(3, (ErrorMsg)localObject);
    }
    if (_simplified)
    {
      localSymbolTable.excludeURI("http://www.w3.org/1999/XSL/Transform");
      localObject = new Template();
      ((Template)localObject).parseSimplified(this, paramParser);
    }
    else
    {
      parseOwnChildren(paramParser);
    }
  }
  
  public final void parseOwnChildren(Parser paramParser)
  {
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("exclude-result-prefixes");
    String str2 = getAttribute("extension-element-prefixes");
    localSymbolTable.pushExcludedNamespacesContext();
    localSymbolTable.excludeURI("http://www.w3.org/1999/XSL/Transform");
    localSymbolTable.excludeNamespaces(str1);
    localSymbolTable.excludeNamespaces(str2);
    List localList = getContents();
    int i = localList.size();
    SyntaxTreeNode localSyntaxTreeNode;
    for (int j = 0; j < i; j++)
    {
      localSyntaxTreeNode = (SyntaxTreeNode)localList.get(j);
      if (((localSyntaxTreeNode instanceof VariableBase)) || ((localSyntaxTreeNode instanceof NamespaceAlias)))
      {
        paramParser.getSymbolTable().setCurrentNode(localSyntaxTreeNode);
        localSyntaxTreeNode.parseContents(paramParser);
      }
    }
    for (j = 0; j < i; j++)
    {
      localSyntaxTreeNode = (SyntaxTreeNode)localList.get(j);
      if ((!(localSyntaxTreeNode instanceof VariableBase)) && (!(localSyntaxTreeNode instanceof NamespaceAlias)))
      {
        paramParser.getSymbolTable().setCurrentNode(localSyntaxTreeNode);
        localSyntaxTreeNode.parseContents(paramParser);
      }
      if ((!_templateInlining) && ((localSyntaxTreeNode instanceof Template)))
      {
        Template localTemplate = (Template)localSyntaxTreeNode;
        String str3 = "template$dot$" + localTemplate.getPosition();
        localTemplate.setName(paramParser.getQName(str3));
      }
    }
    localSymbolTable.popExcludedNamespacesContext();
  }
  
  public void processModes()
  {
    if (_defaultMode == null) {
      _defaultMode = new Mode(null, this, "");
    }
    _defaultMode.processPatterns(_keys);
    Iterator localIterator = _modes.values().iterator();
    while (localIterator.hasNext())
    {
      Mode localMode = (Mode)localIterator.next();
      localMode.processPatterns(_keys);
    }
  }
  
  private void compileModes(ClassGenerator paramClassGenerator)
  {
    _defaultMode.compileApplyTemplates(paramClassGenerator);
    Iterator localIterator = _modes.values().iterator();
    while (localIterator.hasNext())
    {
      Mode localMode = (Mode)localIterator.next();
      localMode.compileApplyTemplates(paramClassGenerator);
    }
  }
  
  public Mode getMode(QName paramQName)
  {
    if (paramQName == null)
    {
      if (_defaultMode == null) {
        _defaultMode = new Mode(null, this, "");
      }
      return _defaultMode;
    }
    Mode localMode = (Mode)_modes.get(paramQName.getStringRep());
    if (localMode == null)
    {
      String str = Integer.toString(_nextModeSerial++);
      _modes.put(paramQName.getStringRep(), localMode = new Mode(paramQName, this, str));
    }
    return localMode;
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    int i = _globals.size();
    for (int j = 0; j < i; j++)
    {
      VariableBase localVariableBase = (VariableBase)_globals.elementAt(j);
      localVariableBase.typeCheck(paramSymbolTable);
    }
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translate();
  }
  
  private void addDOMField(ClassGenerator paramClassGenerator)
  {
    FieldGen localFieldGen = new FieldGen(1, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), "_dom", paramClassGenerator.getConstantPool());
    paramClassGenerator.addField(localFieldGen.getField());
  }
  
  private void addStaticField(ClassGenerator paramClassGenerator, String paramString1, String paramString2)
  {
    FieldGen localFieldGen = new FieldGen(12, Util.getJCRefType(paramString1), paramString2, paramClassGenerator.getConstantPool());
    paramClassGenerator.addField(localFieldGen.getField());
  }
  
  public void translate()
  {
    _className = getXSLTC().getClassName();
    ClassGenerator localClassGenerator = new ClassGenerator(_className, "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "", 33, null, this);
    addDOMField(localClassGenerator);
    compileTransform(localClassGenerator);
    Iterator localIterator = elements();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      Object localObject;
      if ((localSyntaxTreeNode instanceof Template))
      {
        localObject = (Template)localSyntaxTreeNode;
        getMode(((Template)localObject).getModeName()).addTemplate((Template)localObject);
      }
      else if ((localSyntaxTreeNode instanceof AttributeSet))
      {
        ((AttributeSet)localSyntaxTreeNode).translate(localClassGenerator, null);
      }
      else if ((localSyntaxTreeNode instanceof Output))
      {
        localObject = (Output)localSyntaxTreeNode;
        if (((Output)localObject).enabled()) {
          _lastOutputElement = ((Output)localObject);
        }
      }
    }
    checkOutputMethod();
    processModes();
    compileModes(localClassGenerator);
    compileStaticInitializer(localClassGenerator);
    compileConstructor(localClassGenerator, _lastOutputElement);
    if (!getParser().errorsFound()) {
      getXSLTC().dumpClass(localClassGenerator.getJavaClass());
    }
  }
  
  private void compileStaticInitializer(ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(9, com.sun.org.apache.bcel.internal.generic.Type.VOID, null, null, "<clinit>", _className, localInstructionList, localConstantPoolGen);
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sNamesArray");
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sUrisArray");
    addStaticField(paramClassGenerator, "[I", "_sTypesArray");
    addStaticField(paramClassGenerator, "[Ljava/lang/String;", "_sNamespaceArray");
    int i = getXSLTC().getCharacterDataCount();
    for (int j = 0; j < i; j++) {
      addStaticField(paramClassGenerator, "[C", "_scharData" + j);
    }
    Vector localVector1 = getXSLTC().getNamesIndex();
    int k = localVector1.size();
    String[] arrayOfString1 = new String[k];
    String[] arrayOfString2 = new String[k];
    int[] arrayOfInt = new int[k];
    for (int n = 0; n < k; n++)
    {
      String str1 = (String)localVector1.elementAt(n);
      int m;
      if ((m = str1.lastIndexOf(':')) > -1) {
        arrayOfString2[n] = str1.substring(0, m);
      }
      m += 1;
      if (str1.charAt(m) == '@')
      {
        arrayOfInt[n] = 2;
        m++;
      }
      else if (str1.charAt(m) == '?')
      {
        arrayOfInt[n] = 13;
        m++;
      }
      else
      {
        arrayOfInt[n] = 1;
      }
      if (m == 0) {
        arrayOfString1[n] = str1;
      } else {
        arrayOfString1[n] = str1.substring(m);
      }
    }
    localMethodGenerator.markChunkStart();
    localInstructionList.append(new PUSH(localConstantPoolGen, k));
    localInstructionList.append(new ANEWARRAY(localConstantPoolGen.addClass("java.lang.String")));
    n = localConstantPoolGen.addFieldref(_className, "_sNamesArray", "[Ljava/lang/String;");
    localInstructionList.append(new PUTSTATIC(n));
    localMethodGenerator.markChunkEnd();
    for (int i1 = 0; i1 < k; i1++)
    {
      String str2 = arrayOfString1[i1];
      localMethodGenerator.markChunkStart();
      localInstructionList.append(new GETSTATIC(n));
      localInstructionList.append(new PUSH(localConstantPoolGen, i1));
      localInstructionList.append(new PUSH(localConstantPoolGen, str2));
      localInstructionList.append(AASTORE);
      localMethodGenerator.markChunkEnd();
    }
    localMethodGenerator.markChunkStart();
    localInstructionList.append(new PUSH(localConstantPoolGen, k));
    localInstructionList.append(new ANEWARRAY(localConstantPoolGen.addClass("java.lang.String")));
    i1 = localConstantPoolGen.addFieldref(_className, "_sUrisArray", "[Ljava/lang/String;");
    localInstructionList.append(new PUTSTATIC(i1));
    localMethodGenerator.markChunkEnd();
    for (int i2 = 0; i2 < k; i2++)
    {
      String str3 = arrayOfString2[i2];
      localMethodGenerator.markChunkStart();
      localInstructionList.append(new GETSTATIC(i1));
      localInstructionList.append(new PUSH(localConstantPoolGen, i2));
      localInstructionList.append(new PUSH(localConstantPoolGen, str3));
      localInstructionList.append(AASTORE);
      localMethodGenerator.markChunkEnd();
    }
    localMethodGenerator.markChunkStart();
    localInstructionList.append(new PUSH(localConstantPoolGen, k));
    localInstructionList.append(new NEWARRAY(BasicType.INT));
    i2 = localConstantPoolGen.addFieldref(_className, "_sTypesArray", "[I");
    localInstructionList.append(new PUTSTATIC(i2));
    localMethodGenerator.markChunkEnd();
    for (int i3 = 0; i3 < k; i3++)
    {
      i4 = arrayOfInt[i3];
      localMethodGenerator.markChunkStart();
      localInstructionList.append(new GETSTATIC(i2));
      localInstructionList.append(new PUSH(localConstantPoolGen, i3));
      localInstructionList.append(new PUSH(localConstantPoolGen, i4));
      localInstructionList.append(IASTORE);
    }
    Vector localVector2 = getXSLTC().getNamespaceIndex();
    localMethodGenerator.markChunkStart();
    localInstructionList.append(new PUSH(localConstantPoolGen, localVector2.size()));
    localInstructionList.append(new ANEWARRAY(localConstantPoolGen.addClass("java.lang.String")));
    int i4 = localConstantPoolGen.addFieldref(_className, "_sNamespaceArray", "[Ljava/lang/String;");
    localInstructionList.append(new PUTSTATIC(i4));
    localMethodGenerator.markChunkEnd();
    for (int i5 = 0; i5 < localVector2.size(); i5++)
    {
      String str4 = (String)localVector2.elementAt(i5);
      localMethodGenerator.markChunkStart();
      localInstructionList.append(new GETSTATIC(i4));
      localInstructionList.append(new PUSH(localConstantPoolGen, i5));
      localInstructionList.append(new PUSH(localConstantPoolGen, str4));
      localInstructionList.append(AASTORE);
      localMethodGenerator.markChunkEnd();
    }
    i5 = getXSLTC().getCharacterDataCount();
    int i6 = localConstantPoolGen.addMethodref("java.lang.String", "toCharArray", "()[C");
    for (int i7 = 0; i7 < i5; i7++)
    {
      localMethodGenerator.markChunkStart();
      localInstructionList.append(new PUSH(localConstantPoolGen, getXSLTC().getCharacterData(i7)));
      localInstructionList.append(new INVOKEVIRTUAL(i6));
      localInstructionList.append(new PUTSTATIC(localConstantPoolGen.addFieldref(_className, "_scharData" + i7, "[C")));
      localMethodGenerator.markChunkEnd();
    }
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  private void compileConstructor(ClassGenerator paramClassGenerator, Output paramOutput)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, null, null, "<init>", _className, localInstructionList, localConstantPoolGen);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "<init>", "()V")));
    localMethodGenerator.markChunkStart();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref(_className, "_sNamesArray", "[Ljava/lang/String;")));
    localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref(_className, "_sUrisArray", "[Ljava/lang/String;")));
    localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
    localMethodGenerator.markChunkEnd();
    localMethodGenerator.markChunkStart();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref(_className, "_sTypesArray", "[I")));
    localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
    localMethodGenerator.markChunkEnd();
    localMethodGenerator.markChunkStart();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref(_className, "_sNamespaceArray", "[Ljava/lang/String;")));
    localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
    localMethodGenerator.markChunkEnd();
    localMethodGenerator.markChunkStart();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, 101));
    localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "transletVersion", "I")));
    localMethodGenerator.markChunkEnd();
    if (_hasIdCall)
    {
      localMethodGenerator.markChunkStart();
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new PUSH(localConstantPoolGen, Boolean.TRUE));
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_hasIdCall", "Z")));
      localMethodGenerator.markChunkEnd();
    }
    if (paramOutput != null)
    {
      localMethodGenerator.markChunkStart();
      paramOutput.translate(paramClassGenerator, localMethodGenerator);
      localMethodGenerator.markChunkEnd();
    }
    if (_numberFormattingUsed)
    {
      localMethodGenerator.markChunkStart();
      DecimalFormatting.translateDefaultDFS(paramClassGenerator, localMethodGenerator);
      localMethodGenerator.markChunkEnd();
    }
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  private String compileTopLevel(ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    com.sun.org.apache.bcel.internal.generic.Type[] arrayOfType = { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;") };
    String[] arrayOfString = { "document", "iterator", "handler" };
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, arrayOfType, arrayOfString, "topLevel", _className, localInstructionList, paramClassGenerator.getConstantPool());
    localMethodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    LocalVariableGen localLocalVariableGen = localMethodGenerator.addLocalVariable("current", com.sun.org.apache.bcel.internal.generic.Type.INT, null, null);
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setFilter", "(Lcom/sun/org/apache/xalan/internal/xsltc/StripFilter;)V");
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(localMethodGenerator.loadDOM());
    localInstructionList.append(new INVOKEINTERFACE(j, 1));
    localInstructionList.append(localMethodGenerator.nextNode());
    localLocalVariableGen.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen.getIndex())));
    Vector localVector1 = new Vector(_globals);
    Iterator localIterator = elements();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      if ((localSyntaxTreeNode instanceof Key)) {
        localVector1.add(localSyntaxTreeNode);
      }
    }
    localVector1 = resolveDependencies(localVector1);
    int k = localVector1.size();
    Object localObject;
    for (int m = 0; m < k; m++)
    {
      localObject = (TopLevelElement)localVector1.elementAt(m);
      ((TopLevelElement)localObject).translate(paramClassGenerator, localMethodGenerator);
      if ((localObject instanceof Key))
      {
        Key localKey = (Key)localObject;
        _keys.put(localKey.getName(), localKey);
      }
    }
    Vector localVector2 = new Vector();
    localIterator = elements();
    while (localIterator.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator.next();
      if ((localObject instanceof DecimalFormatting)) {
        ((DecimalFormatting)localObject).translate(paramClassGenerator, localMethodGenerator);
      } else if ((localObject instanceof Whitespace)) {
        localVector2.addAll(((Whitespace)localObject).getRules());
      }
    }
    if (localVector2.size() > 0) {
      Whitespace.translateRules(localVector2, paramClassGenerator);
    }
    if (paramClassGenerator.containsMethod("stripSpace", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;II)Z") != null)
    {
      localInstructionList.append(localMethodGenerator.loadDOM());
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
    return "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
  }
  
  private Vector resolveDependencies(Vector paramVector)
  {
    Vector localVector1 = new Vector();
    while (paramVector.size() > 0)
    {
      int i = 0;
      int j = 0;
      while (j < paramVector.size())
      {
        TopLevelElement localTopLevelElement = (TopLevelElement)paramVector.elementAt(j);
        Vector localVector2 = localTopLevelElement.getDependencies();
        if ((localVector2 == null) || (localVector1.containsAll(localVector2)))
        {
          localVector1.addElement(localTopLevelElement);
          paramVector.remove(j);
          i = 1;
        }
        else
        {
          j++;
        }
      }
      if (i == 0)
      {
        ErrorMsg localErrorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", paramVector.toString(), this);
        getParser().reportError(3, localErrorMsg);
        return localVector1;
      }
    }
    return localVector1;
  }
  
  private String compileBuildKeys(ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    com.sun.org.apache.bcel.internal.generic.Type[] arrayOfType = { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), com.sun.org.apache.bcel.internal.generic.Type.INT };
    String[] arrayOfString = { "document", "iterator", "handler", "current" };
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, arrayOfType, arrayOfString, "buildKeys", _className, localInstructionList, paramClassGenerator.getConstantPool());
    localMethodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    Iterator localIterator = elements();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      if ((localSyntaxTreeNode instanceof Key))
      {
        Key localKey = (Key)localSyntaxTreeNode;
        localKey.translate(paramClassGenerator, localMethodGenerator);
        _keys.put(localKey.getName(), localKey);
      }
    }
    localInstructionList.append(RETURN);
    localMethodGenerator.stripAttributes(true);
    localMethodGenerator.setMaxLocals();
    localMethodGenerator.setMaxStack();
    localMethodGenerator.removeNOPs();
    paramClassGenerator.addMethod(localMethodGenerator.getMethod());
    return "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V";
  }
  
  private void compileTransform(ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    com.sun.org.apache.bcel.internal.generic.Type[] arrayOfType = new com.sun.org.apache.bcel.internal.generic.Type[3];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, arrayOfType, arrayOfString, "transform", _className, localInstructionList, paramClassGenerator.getConstantPool());
    localMethodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "resetPrefixIndex", "()V");
    localInstructionList.append(new INVOKESTATIC(i));
    LocalVariableGen localLocalVariableGen = localMethodGenerator.addLocalVariable("current", com.sun.org.apache.bcel.internal.generic.Type.INT, null, null);
    String str1 = paramClassGenerator.getApplyTemplatesSig();
    int j = localConstantPoolGen.addMethodref(getClassName(), "applyTemplates", str1);
    int k = localConstantPoolGen.addFieldref(getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (isMultiDocument())
    {
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
      localInstructionList.append(DUP);
    }
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(localMethodGenerator.loadDOM());
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "makeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;")));
    if (isMultiDocument())
    {
      m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
      localInstructionList.append(new INVOKESPECIAL(m));
    }
    localInstructionList.append(new PUTFIELD(k));
    int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(localMethodGenerator.loadDOM());
    localInstructionList.append(new INVOKEINTERFACE(m, 1));
    localInstructionList.append(localMethodGenerator.nextNode());
    localLocalVariableGen.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen.getIndex())));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(localMethodGenerator.loadHandler());
    int n = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "transferOutputSettings", "(Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    localInstructionList.append(new INVOKEVIRTUAL(n));
    String str2 = compileBuildKeys(paramClassGenerator);
    int i1 = localConstantPoolGen.addMethodref(getClassName(), "buildKeys", str2);
    Iterator localIterator = elements();
    if ((_globals.size() > 0) || (localIterator.hasNext()))
    {
      String str3 = compileTopLevel(paramClassGenerator);
      int i2 = localConstantPoolGen.addMethodref(getClassName(), "topLevel", str3);
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new GETFIELD(k));
      localInstructionList.append(localMethodGenerator.loadIterator());
      localInstructionList.append(localMethodGenerator.loadHandler());
      localInstructionList.append(new INVOKEVIRTUAL(i2));
    }
    localInstructionList.append(localMethodGenerator.loadHandler());
    localInstructionList.append(localMethodGenerator.startDocument());
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(k));
    localInstructionList.append(localMethodGenerator.loadIterator());
    localInstructionList.append(localMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(localMethodGenerator.loadHandler());
    localInstructionList.append(localMethodGenerator.endDocument());
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  private void peepHoleOptimization(MethodGenerator paramMethodGenerator)
  {
    String str = "`aload'`pop'`instruction'";
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    InstructionFinder localInstructionFinder = new InstructionFinder(localInstructionList);
    Iterator localIterator = localInstructionFinder.search("`aload'`pop'`instruction'");
    while (localIterator.hasNext())
    {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])localIterator.next();
      try
      {
        localInstructionList.delete(arrayOfInstructionHandle[0], arrayOfInstructionHandle[1]);
      }
      catch (TargetLostException localTargetLostException) {}
    }
  }
  
  public int addParam(Param paramParam)
  {
    _globals.addElement(paramParam);
    return _globals.size() - 1;
  }
  
  public int addVariable(Variable paramVariable)
  {
    _globals.addElement(paramVariable);
    return _globals.size() - 1;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Stylesheet");
    displayContents(paramInt + 4);
  }
  
  public String getNamespace(String paramString)
  {
    return lookupNamespace(paramString);
  }
  
  public String getClassName()
  {
    return _className;
  }
  
  public Vector getTemplates()
  {
    return _templates;
  }
  
  public Vector getAllValidTemplates()
  {
    if (_includedStylesheets == null) {
      return _templates;
    }
    if (_allValidTemplates == null)
    {
      Vector localVector = new Vector();
      localVector.addAll(_templates);
      int i = _includedStylesheets.size();
      for (int j = 0; j < i; j++)
      {
        Stylesheet localStylesheet = (Stylesheet)_includedStylesheets.elementAt(j);
        localVector.addAll(localStylesheet.getAllValidTemplates());
      }
      if (_parentStylesheet != null) {
        return localVector;
      }
      _allValidTemplates = localVector;
    }
    return _allValidTemplates;
  }
  
  protected void addTemplate(Template paramTemplate)
  {
    _templates.addElement(paramTemplate);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Stylesheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */