package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.List;
import java.util.Vector;

public final class Template
  extends TopLevelElement
{
  private QName _name;
  private QName _mode;
  private Pattern _pattern;
  private double _priority;
  private int _position;
  private boolean _disabled = false;
  private boolean _compiled = false;
  private boolean _simplified = false;
  private boolean _isSimpleNamedTemplate = false;
  private Vector<Param> _parameters = new Vector();
  private Stylesheet _stylesheet = null;
  
  public Template() {}
  
  public boolean hasParams()
  {
    return _parameters.size() > 0;
  }
  
  public boolean isSimplified()
  {
    return _simplified;
  }
  
  public void setSimplified()
  {
    _simplified = true;
  }
  
  public boolean isSimpleNamedTemplate()
  {
    return _isSimpleNamedTemplate;
  }
  
  public void addParameter(Param paramParam)
  {
    _parameters.addElement(paramParam);
  }
  
  public Vector<Param> getParameters()
  {
    return _parameters;
  }
  
  public void disable()
  {
    _disabled = true;
  }
  
  public boolean disabled()
  {
    return _disabled;
  }
  
  public double getPriority()
  {
    return _priority;
  }
  
  public int getPosition()
  {
    return _position;
  }
  
  public boolean isNamed()
  {
    return _name != null;
  }
  
  public Pattern getPattern()
  {
    return _pattern;
  }
  
  public QName getName()
  {
    return _name;
  }
  
  public void setName(QName paramQName)
  {
    if (_name == null) {
      _name = paramQName;
    }
  }
  
  public QName getModeName()
  {
    return _mode;
  }
  
  public int compareTo(Object paramObject)
  {
    Template localTemplate = (Template)paramObject;
    if (_priority > _priority) {
      return 1;
    }
    if (_priority < _priority) {
      return -1;
    }
    if (_position > _position) {
      return 1;
    }
    if (_position < _position) {
      return -1;
    }
    return 0;
  }
  
  public void display(int paramInt)
  {
    Util.println('\n');
    indent(paramInt);
    if (_name != null)
    {
      indent(paramInt);
      Util.println("name = " + _name);
    }
    else if (_pattern != null)
    {
      indent(paramInt);
      Util.println("match = " + _pattern.toString());
    }
    if (_mode != null)
    {
      indent(paramInt);
      Util.println("mode = " + _mode);
    }
    displayContents(paramInt + 4);
  }
  
  private boolean resolveNamedTemplates(Template paramTemplate, Parser paramParser)
  {
    if (paramTemplate == null) {
      return true;
    }
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    int i = getImportPrecedence();
    int j = paramTemplate.getImportPrecedence();
    if (i > j)
    {
      paramTemplate.disable();
      return true;
    }
    if (i < j)
    {
      localSymbolTable.addTemplate(paramTemplate);
      disable();
      return true;
    }
    return false;
  }
  
  public Stylesheet getStylesheet()
  {
    return _stylesheet;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str1 = getAttribute("name");
    String str2 = getAttribute("mode");
    String str3 = getAttribute("match");
    String str4 = getAttribute("priority");
    _stylesheet = super.getStylesheet();
    Object localObject;
    if (str1.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str1))
      {
        localObject = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
        paramParser.reportError(3, (ErrorMsg)localObject);
      }
      _name = paramParser.getQNameIgnoreDefaultNs(str1);
    }
    if (str2.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str2))
      {
        localObject = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
        paramParser.reportError(3, (ErrorMsg)localObject);
      }
      _mode = paramParser.getQNameIgnoreDefaultNs(str2);
    }
    if (str3.length() > 0) {
      _pattern = paramParser.parsePattern(this, "match", null);
    }
    if (str4.length() > 0) {
      _priority = Double.parseDouble(str4);
    } else if (_pattern != null) {
      _priority = _pattern.getPriority();
    } else {
      _priority = NaN.0D;
    }
    _position = paramParser.getTemplateIndex();
    if (_name != null)
    {
      localObject = paramParser.getSymbolTable().addTemplate(this);
      if (!resolveNamedTemplates((Template)localObject, paramParser))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("TEMPLATE_REDEF_ERR", _name, this);
        paramParser.reportError(3, localErrorMsg);
      }
      if ((_pattern == null) && (_mode == null)) {
        _isSimpleNamedTemplate = true;
      }
    }
    if ((_parent instanceof Stylesheet)) {
      ((Stylesheet)_parent).addTemplate(this);
    }
    paramParser.setTemplate(this);
    parseChildren(paramParser);
    paramParser.setTemplate(null);
  }
  
  public void parseSimplified(Stylesheet paramStylesheet, Parser paramParser)
  {
    _stylesheet = paramStylesheet;
    setParent(paramStylesheet);
    _name = null;
    _mode = null;
    _priority = NaN.0D;
    _pattern = paramParser.parsePattern(this, "/");
    List localList = _stylesheet.getContents();
    SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localList.get(0);
    if ((localSyntaxTreeNode instanceof LiteralElement))
    {
      addElement(localSyntaxTreeNode);
      localSyntaxTreeNode.setParent(this);
      localList.set(0, this);
      paramParser.setTemplate(this);
      localSyntaxTreeNode.parseContents(paramParser);
      paramParser.setTemplate(null);
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_pattern != null) {
      _pattern.typeCheck(paramSymbolTable);
    }
    return typeCheckContents(paramSymbolTable);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_disabled) {
      return;
    }
    String str1 = paramClassGenerator.getClassName();
    if ((_compiled) && (isNamed()))
    {
      String str2 = Util.escape(_name.toString());
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadIterator());
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref(str1, str2, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V")));
      return;
    }
    if (_compiled) {
      return;
    }
    _compiled = true;
    if ((_isSimpleNamedTemplate) && ((paramMethodGenerator instanceof NamedMethodGenerator)))
    {
      int i = _parameters.size();
      NamedMethodGenerator localNamedMethodGenerator = (NamedMethodGenerator)paramMethodGenerator;
      for (int j = 0; j < i; j++)
      {
        Param localParam = (Param)_parameters.elementAt(j);
        localParam.setLoadInstruction(localNamedMethodGenerator.loadParameter(j));
        localParam.setStoreInstruction(localNamedMethodGenerator.storeParameter(j));
      }
    }
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.setPositions(true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Template.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */