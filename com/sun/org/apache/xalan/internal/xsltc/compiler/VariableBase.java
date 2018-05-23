package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.PrintStream;
import java.util.Vector;

class VariableBase
  extends TopLevelElement
{
  protected QName _name;
  protected String _escapedName;
  protected com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type _type;
  protected boolean _isLocal;
  protected LocalVariableGen _local;
  protected Instruction _loadInstruction;
  protected Instruction _storeInstruction;
  protected Expression _select;
  protected String select;
  protected Vector<VariableRefBase> _refs = new Vector(2);
  protected boolean _ignore = false;
  
  VariableBase() {}
  
  public void disable()
  {
    _ignore = true;
  }
  
  public void addReference(VariableRefBase paramVariableRefBase)
  {
    _refs.addElement(paramVariableRefBase);
  }
  
  public void copyReferences(VariableBase paramVariableBase)
  {
    int i = _refs.size();
    for (int j = 0; j < i; j++) {
      paramVariableBase.addReference((VariableRefBase)_refs.get(j));
    }
  }
  
  public void mapRegister(MethodGenerator paramMethodGenerator)
  {
    if (_local == null)
    {
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      String str = getEscapedName();
      com.sun.org.apache.bcel.internal.generic.Type localType = _type.toJCType();
      _local = paramMethodGenerator.addLocalVariable2(str, localType, localInstructionList.getEnd());
    }
  }
  
  public void unmapRegister(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_local != null)
    {
      if ((_type instanceof ResultTreeType))
      {
        ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
        InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
        if ((paramClassGenerator.getStylesheet().callsNodeset()) && (paramClassGenerator.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")))
        {
          i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
          localInstructionList.append(paramMethodGenerator.loadDOM());
          localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
          localInstructionList.append(loadInstruction());
          localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
          localInstructionList.append(new INVOKEVIRTUAL(i));
        }
        int i = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
        localInstructionList.append(loadInstruction());
        localInstructionList.append(new INVOKEINTERFACE(i, 1));
      }
      _local.setEnd(paramMethodGenerator.getInstructionList().getEnd());
      paramMethodGenerator.removeLocalVariable(_local);
      _refs = null;
      _local = null;
    }
  }
  
  public Instruction loadInstruction()
  {
    if (_loadInstruction == null) {
      _loadInstruction = _type.LOAD(_local.getIndex());
    }
    return _loadInstruction;
  }
  
  public Instruction storeInstruction()
  {
    if (_storeInstruction == null) {
      _storeInstruction = _type.STORE(_local.getIndex());
    }
    return _storeInstruction;
  }
  
  public Expression getExpression()
  {
    return _select;
  }
  
  public String toString()
  {
    return "variable(" + _name + ")";
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    System.out.println("Variable " + _name);
    if (_select != null)
    {
      indent(paramInt + 4);
      System.out.println("select " + _select.toString());
    }
    displayContents(paramInt + 4);
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type getType()
  {
    return _type;
  }
  
  public QName getName()
  {
    return _name;
  }
  
  public String getEscapedName()
  {
    return _escapedName;
  }
  
  public void setName(QName paramQName)
  {
    _name = paramQName;
    _escapedName = Util.escape(paramQName.getStringRep());
  }
  
  public boolean isLocal()
  {
    return _isLocal;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (str.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str))
      {
        localObject = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, (ErrorMsg)localObject);
      }
      setName(paramParser.getQNameIgnoreDefaultNs(str));
    }
    else
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    }
    Object localObject = paramParser.lookupVariable(_name);
    if ((localObject != null) && (((VariableBase)localObject).getParent() == getParent())) {
      reportError(this, paramParser, "VARIABLE_REDEF_ERR", str);
    }
    select = getAttribute("select");
    if (select.length() > 0)
    {
      _select = getParser().parseExpression(this, "select", null);
      if (_select.isDummy())
      {
        reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
        return;
      }
    }
    parseChildren(paramParser);
  }
  
  public void translateValue(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen;
    InstructionList localInstructionList;
    if (_select != null)
    {
      _select.translate(paramClassGenerator, paramMethodGenerator);
      if ((_select.getType() instanceof NodeSetType))
      {
        localConstantPoolGen = paramClassGenerator.getConstantPool();
        localInstructionList = paramMethodGenerator.getInstructionList();
        int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CachedNodeListIterator")));
        localInstructionList.append(DUP_X1);
        localInstructionList.append(SWAP);
        localInstructionList.append(new INVOKESPECIAL(i));
      }
      _select.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    else if (hasContents())
    {
      compileResultTree(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      localConstantPoolGen = paramClassGenerator.getConstantPool();
      localInstructionList = paramMethodGenerator.getInstructionList();
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\VariableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */