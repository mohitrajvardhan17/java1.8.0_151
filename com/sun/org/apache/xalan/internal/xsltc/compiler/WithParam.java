package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class WithParam
  extends Instruction
{
  private QName _name;
  protected String _escapedName;
  private Expression _select;
  private LocalVariableGen _domAdapter;
  private boolean _doParameterOptimization = false;
  
  WithParam() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("with-param " + _name);
    if (_select != null)
    {
      indent(paramInt + 4);
      Util.println("select " + _select.toString());
    }
    displayContents(paramInt + 4);
  }
  
  public String getEscapedName()
  {
    return _escapedName;
  }
  
  public QName getName()
  {
    return _name;
  }
  
  public void setName(QName paramQName)
  {
    _name = paramQName;
    _escapedName = Util.escape(paramQName.getStringRep());
  }
  
  public void setDoParameterOptimization(boolean paramBoolean)
  {
    _doParameterOptimization = paramBoolean;
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
    Object localObject = getAttribute("select");
    if (((String)localObject).length() > 0) {
      _select = paramParser.parseExpression(this, "select", null);
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_select != null)
    {
      Type localType = _select.typeCheck(paramSymbolTable);
      if (!(localType instanceof ReferenceType)) {
        _select = new CastExpr(_select, Type.Reference);
      }
    }
    else
    {
      typeCheckContents(paramSymbolTable);
    }
    return Type.Void;
  }
  
  public void translateValue(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_select != null)
    {
      _select.translate(paramClassGenerator, paramMethodGenerator);
      _select.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      Object localObject;
      if (hasContents())
      {
        localObject = paramMethodGenerator.getInstructionList();
        compileResultTree(paramClassGenerator, paramMethodGenerator);
        _domAdapter = paramMethodGenerator.addLocalVariable2("@" + _escapedName, Type.ResultTree.toJCType(), ((InstructionList)localObject).getEnd());
        ((InstructionList)localObject).append(DUP);
        ((InstructionList)localObject).append(new ASTORE(_domAdapter.getIndex()));
      }
      else
      {
        localObject = paramClassGenerator.getConstantPool();
        InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
        localInstructionList.append(new PUSH((ConstantPoolGen)localObject, ""));
      }
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_doParameterOptimization)
    {
      translateValue(paramClassGenerator, paramMethodGenerator);
      return;
    }
    String str = Util.escape(getEscapedName());
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, str));
    translateValue(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(new PUSH(localConstantPoolGen, false));
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
    localInstructionList.append(POP);
  }
  
  public void releaseResultTree(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_domAdapter != null)
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      if ((paramClassGenerator.getStylesheet().callsNodeset()) && (paramClassGenerator.getDOMClass().equals("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")))
      {
        i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "removeDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)V");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM")));
        localInstructionList.append(new ALOAD(_domAdapter.getIndex()));
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
        localInstructionList.append(new INVOKEVIRTUAL(i));
      }
      int i = localConstantPoolGen.addInterfaceMethodref("com/sun/org/apache/xalan/internal/xsltc/DOM", "release", "()V");
      localInstructionList.append(new ALOAD(_domAdapter.getIndex()));
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
      _domAdapter = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\WithParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */