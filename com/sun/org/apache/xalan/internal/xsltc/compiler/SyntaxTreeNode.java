package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DUP_X1;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public abstract class SyntaxTreeNode
  implements Constants
{
  private Parser _parser;
  protected SyntaxTreeNode _parent;
  private Stylesheet _stylesheet;
  private Template _template;
  private final List<SyntaxTreeNode> _contents = new ArrayList(2);
  protected QName _qname;
  private int _line;
  protected AttributesImpl _attributes = null;
  private Map<String, String> _prefixMapping = null;
  protected static final SyntaxTreeNode Dummy = new AbsolutePathPattern(null);
  protected static final int IndentIncrement = 4;
  private static final char[] _spaces = "                                                       ".toCharArray();
  
  public SyntaxTreeNode()
  {
    _line = 0;
    _qname = null;
  }
  
  public SyntaxTreeNode(int paramInt)
  {
    _line = paramInt;
    _qname = null;
  }
  
  public SyntaxTreeNode(String paramString1, String paramString2, String paramString3)
  {
    _line = 0;
    setQName(paramString1, paramString2, paramString3);
  }
  
  protected final void setLineNumber(int paramInt)
  {
    _line = paramInt;
  }
  
  public final int getLineNumber()
  {
    if (_line > 0) {
      return _line;
    }
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    return localSyntaxTreeNode != null ? localSyntaxTreeNode.getLineNumber() : 0;
  }
  
  protected void setQName(QName paramQName)
  {
    _qname = paramQName;
  }
  
  protected void setQName(String paramString1, String paramString2, String paramString3)
  {
    _qname = new QName(paramString1, paramString2, paramString3);
  }
  
  protected QName getQName()
  {
    return _qname;
  }
  
  protected void setAttributes(AttributesImpl paramAttributesImpl)
  {
    _attributes = paramAttributesImpl;
  }
  
  protected String getAttribute(String paramString)
  {
    if (_attributes == null) {
      return "";
    }
    String str = _attributes.getValue(paramString);
    return (str == null) || (str.equals("")) ? "" : str;
  }
  
  protected String getAttribute(String paramString1, String paramString2)
  {
    return getAttribute(paramString1 + ':' + paramString2);
  }
  
  protected boolean hasAttribute(String paramString)
  {
    return (_attributes != null) && (_attributes.getValue(paramString) != null);
  }
  
  protected void addAttribute(String paramString1, String paramString2)
  {
    int i = _attributes.getIndex(paramString1);
    if (i != -1) {
      _attributes.setAttribute(i, "", Util.getLocalName(paramString1), paramString1, "CDATA", paramString2);
    } else {
      _attributes.addAttribute("", Util.getLocalName(paramString1), paramString1, "CDATA", paramString2);
    }
  }
  
  protected Attributes getAttributes()
  {
    return _attributes;
  }
  
  protected void setPrefixMapping(Map<String, String> paramMap)
  {
    _prefixMapping = paramMap;
  }
  
  protected Map<String, String> getPrefixMapping()
  {
    return _prefixMapping;
  }
  
  protected void addPrefixMapping(String paramString1, String paramString2)
  {
    if (_prefixMapping == null) {
      _prefixMapping = new HashMap();
    }
    _prefixMapping.put(paramString1, paramString2);
  }
  
  protected String lookupNamespace(String paramString)
  {
    String str = null;
    if (_prefixMapping != null) {
      str = (String)_prefixMapping.get(paramString);
    }
    if ((str == null) && (_parent != null))
    {
      str = _parent.lookupNamespace(paramString);
      if ((paramString == "") && (str == null)) {
        str = "";
      }
    }
    return str;
  }
  
  protected String lookupPrefix(String paramString)
  {
    String str1 = null;
    if ((_prefixMapping != null) && (_prefixMapping.containsValue(paramString)))
    {
      Iterator localIterator = _prefixMapping.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        str1 = (String)localEntry.getKey();
        String str2 = (String)localEntry.getValue();
        if (str2.equals(paramString)) {
          return str1;
        }
      }
    }
    else if (_parent != null)
    {
      str1 = _parent.lookupPrefix(paramString);
      if ((paramString == "") && (str1 == null)) {
        str1 = "";
      }
    }
    return str1;
  }
  
  protected void setParser(Parser paramParser)
  {
    _parser = paramParser;
  }
  
  public final Parser getParser()
  {
    return _parser;
  }
  
  protected void setParent(SyntaxTreeNode paramSyntaxTreeNode)
  {
    if (_parent == null) {
      _parent = paramSyntaxTreeNode;
    }
  }
  
  protected final SyntaxTreeNode getParent()
  {
    return _parent;
  }
  
  protected final boolean isDummy()
  {
    return this == Dummy;
  }
  
  protected int getImportPrecedence()
  {
    Stylesheet localStylesheet = getStylesheet();
    if (localStylesheet == null) {
      return Integer.MIN_VALUE;
    }
    return localStylesheet.getImportPrecedence();
  }
  
  public Stylesheet getStylesheet()
  {
    if (_stylesheet == null)
    {
      for (SyntaxTreeNode localSyntaxTreeNode = this; localSyntaxTreeNode != null; localSyntaxTreeNode = localSyntaxTreeNode.getParent()) {
        if ((localSyntaxTreeNode instanceof Stylesheet)) {
          return (Stylesheet)localSyntaxTreeNode;
        }
      }
      _stylesheet = ((Stylesheet)localSyntaxTreeNode);
    }
    return _stylesheet;
  }
  
  protected Template getTemplate()
  {
    if (_template == null)
    {
      for (SyntaxTreeNode localSyntaxTreeNode = this; (localSyntaxTreeNode != null) && (!(localSyntaxTreeNode instanceof Template)); localSyntaxTreeNode = localSyntaxTreeNode.getParent()) {}
      _template = ((Template)localSyntaxTreeNode);
    }
    return _template;
  }
  
  protected final XSLTC getXSLTC()
  {
    return _parser.getXSLTC();
  }
  
  protected final SymbolTable getSymbolTable()
  {
    return _parser == null ? null : _parser.getSymbolTable();
  }
  
  public void parseContents(Parser paramParser)
  {
    parseChildren(paramParser);
  }
  
  protected final void parseChildren(Parser paramParser)
  {
    ArrayList localArrayList = null;
    Iterator localIterator = _contents.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator.next();
      paramParser.getSymbolTable().setCurrentNode((SyntaxTreeNode)localObject);
      ((SyntaxTreeNode)localObject).parseContents(paramParser);
      QName localQName = updateScope(paramParser, (SyntaxTreeNode)localObject);
      if (localQName != null)
      {
        if (localArrayList == null) {
          localArrayList = new ArrayList(2);
        }
        localArrayList.add(localQName);
      }
    }
    paramParser.getSymbolTable().setCurrentNode(this);
    if (localArrayList != null)
    {
      localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        localObject = (QName)localIterator.next();
        paramParser.removeVariable((QName)localObject);
      }
    }
  }
  
  protected QName updateScope(Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode)
  {
    Object localObject;
    if ((paramSyntaxTreeNode instanceof Variable))
    {
      localObject = (Variable)paramSyntaxTreeNode;
      paramParser.addVariable((Variable)localObject);
      return ((Variable)localObject).getName();
    }
    if ((paramSyntaxTreeNode instanceof Param))
    {
      localObject = (Param)paramSyntaxTreeNode;
      paramParser.addParameter((Param)localObject);
      return ((Param)localObject).getName();
    }
    return null;
  }
  
  public abstract Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError;
  
  protected Type typeCheckContents(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Iterator localIterator = _contents.iterator();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      localSyntaxTreeNode.typeCheck(paramSymbolTable);
    }
    return Type.Void;
  }
  
  public abstract void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator);
  
  protected void translateContents(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    int i = elementCount();
    Iterator localIterator = _contents.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator.next();
      paramMethodGenerator.markChunkStart();
      ((SyntaxTreeNode)localObject).translate(paramClassGenerator, paramMethodGenerator);
      paramMethodGenerator.markChunkEnd();
    }
    for (int j = 0; j < i; j++) {
      if ((_contents.get(j) instanceof VariableBase))
      {
        localObject = (VariableBase)_contents.get(j);
        ((VariableBase)localObject).unmapRegister(paramClassGenerator, paramMethodGenerator);
      }
    }
  }
  
  private boolean isSimpleRTF(SyntaxTreeNode paramSyntaxTreeNode)
  {
    List localList = paramSyntaxTreeNode.getContents();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      if (!isTextElement(localSyntaxTreeNode, false)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean isAdaptiveRTF(SyntaxTreeNode paramSyntaxTreeNode)
  {
    List localList = paramSyntaxTreeNode.getContents();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      if (!isTextElement(localSyntaxTreeNode, true)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean isTextElement(SyntaxTreeNode paramSyntaxTreeNode, boolean paramBoolean)
  {
    if (((paramSyntaxTreeNode instanceof ValueOf)) || ((paramSyntaxTreeNode instanceof Number)) || ((paramSyntaxTreeNode instanceof Text))) {
      return true;
    }
    if ((paramSyntaxTreeNode instanceof If)) {
      return paramBoolean ? isAdaptiveRTF(paramSyntaxTreeNode) : isSimpleRTF(paramSyntaxTreeNode);
    }
    if ((paramSyntaxTreeNode instanceof Choose))
    {
      List localList = paramSyntaxTreeNode.getContents();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
        if ((!(localSyntaxTreeNode instanceof Text)) && (((!(localSyntaxTreeNode instanceof When)) && (!(localSyntaxTreeNode instanceof Otherwise))) || (((!paramBoolean) || (!isAdaptiveRTF(localSyntaxTreeNode))) && ((paramBoolean) || (!isSimpleRTF(localSyntaxTreeNode)))))) {
          return false;
        }
      }
      return true;
    }
    return (paramBoolean) && (((paramSyntaxTreeNode instanceof CallTemplate)) || ((paramSyntaxTreeNode instanceof ApplyTemplates)));
  }
  
  protected void compileResultTree(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Stylesheet localStylesheet = paramClassGenerator.getStylesheet();
    boolean bool1 = isSimpleRTF(this);
    boolean bool2 = false;
    if (!bool1) {
      bool2 = isAdaptiveRTF(this);
    }
    int i = bool2 ? 1 : bool1 ? 0 : 2;
    localInstructionList.append(paramMethodGenerator.loadHandler());
    String str = paramClassGenerator.getDOMClass();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IIZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    localInstructionList.append(new PUSH(localConstantPoolGen, 32));
    localInstructionList.append(new PUSH(localConstantPoolGen, i));
    localInstructionList.append(new PUSH(localConstantPoolGen, localStylesheet.callsNodeset()));
    localInstructionList.append(new INVOKEINTERFACE(j, 4));
    localInstructionList.append(DUP);
    j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    localInstructionList.append(new INVOKEINTERFACE(j, 1));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.storeHandler());
    localInstructionList.append(paramMethodGenerator.startDocument());
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(paramMethodGenerator.endDocument());
    if ((localStylesheet.callsNodeset()) && (!str.equals("com/sun/org/apache/xalan/internal/xsltc/DOM")))
    {
      j = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;[Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
      localInstructionList.append(new DUP_X1());
      localInstructionList.append(SWAP);
      if (!localStylesheet.callsNodeset())
      {
        localInstructionList.append(new ICONST(0));
        localInstructionList.append(new ANEWARRAY(localConstantPoolGen.addClass("java.lang.String")));
        localInstructionList.append(DUP);
        localInstructionList.append(DUP);
        localInstructionList.append(new ICONST(0));
        localInstructionList.append(new NEWARRAY(BasicType.INT));
        localInstructionList.append(SWAP);
        localInstructionList.append(new INVOKESPECIAL(j));
      }
      else
      {
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
        localInstructionList.append(ALOAD_0);
        localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
        localInstructionList.append(new INVOKESPECIAL(j));
        localInstructionList.append(DUP);
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(paramClassGenerator.getDOMClass())));
        localInstructionList.append(SWAP);
        j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "addDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)I");
        localInstructionList.append(new INVOKEVIRTUAL(j));
        localInstructionList.append(POP);
      }
    }
    localInstructionList.append(SWAP);
    localInstructionList.append(paramMethodGenerator.storeHandler());
  }
  
  protected boolean contextDependent()
  {
    return true;
  }
  
  protected boolean dependentContents()
  {
    Iterator localIterator = _contents.iterator();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      if (localSyntaxTreeNode.contextDependent()) {
        return true;
      }
    }
    return false;
  }
  
  protected final void addElement(SyntaxTreeNode paramSyntaxTreeNode)
  {
    _contents.add(paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(this);
  }
  
  protected final void setFirstElement(SyntaxTreeNode paramSyntaxTreeNode)
  {
    _contents.add(0, paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(this);
  }
  
  protected final void removeElement(SyntaxTreeNode paramSyntaxTreeNode)
  {
    _contents.remove(paramSyntaxTreeNode);
    paramSyntaxTreeNode.setParent(null);
  }
  
  protected final List<SyntaxTreeNode> getContents()
  {
    return _contents;
  }
  
  protected final boolean hasContents()
  {
    return elementCount() > 0;
  }
  
  protected final int elementCount()
  {
    return _contents.size();
  }
  
  protected final Iterator<SyntaxTreeNode> elements()
  {
    return _contents.iterator();
  }
  
  protected final Object elementAt(int paramInt)
  {
    return _contents.get(paramInt);
  }
  
  protected final SyntaxTreeNode lastChild()
  {
    if (_contents.isEmpty()) {
      return null;
    }
    return (SyntaxTreeNode)_contents.get(_contents.size() - 1);
  }
  
  public void display(int paramInt)
  {
    displayContents(paramInt);
  }
  
  protected void displayContents(int paramInt)
  {
    Iterator localIterator = _contents.iterator();
    while (localIterator.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
      localSyntaxTreeNode.display(paramInt);
    }
  }
  
  protected final void indent(int paramInt)
  {
    System.out.print(new String(_spaces, 0, paramInt));
  }
  
  protected void reportError(SyntaxTreeNode paramSyntaxTreeNode, Parser paramParser, String paramString1, String paramString2)
  {
    ErrorMsg localErrorMsg = new ErrorMsg(paramString1, paramString2, paramSyntaxTreeNode);
    paramParser.reportError(3, localErrorMsg);
  }
  
  protected void reportWarning(SyntaxTreeNode paramSyntaxTreeNode, Parser paramParser, String paramString1, String paramString2)
  {
    ErrorMsg localErrorMsg = new ErrorMsg(paramString1, paramString2, paramSyntaxTreeNode);
    paramParser.reportError(4, localErrorMsg);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\SyntaxTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */