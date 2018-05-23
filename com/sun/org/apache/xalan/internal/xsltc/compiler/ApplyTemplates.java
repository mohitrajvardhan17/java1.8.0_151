package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

final class ApplyTemplates
  extends Instruction
{
  private Expression _select;
  private Type _type = null;
  private QName _modeName;
  private String _functionName;
  
  ApplyTemplates() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("ApplyTemplates");
    indent(paramInt + 4);
    Util.println("select " + _select.toString());
    if (_modeName != null)
    {
      indent(paramInt + 4);
      Util.println("mode " + _modeName);
    }
  }
  
  public boolean hasWithParams()
  {
    return hasContents();
  }
  
  public void parseContents(Parser paramParser)
  {
    String str1 = getAttribute("select");
    String str2 = getAttribute("mode");
    if (str1.length() > 0) {
      _select = paramParser.parseExpression(this, "select", null);
    }
    if (str2.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str2))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("INVALID_QNAME_ERR", str2, this);
        paramParser.reportError(3, localErrorMsg);
      }
      _modeName = paramParser.getQNameIgnoreDefaultNs(str2);
    }
    _functionName = paramParser.getTopLevelStylesheet().getMode(_modeName).functionName();
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_select != null)
    {
      _type = _select.typeCheck(paramSymbolTable);
      if (((_type instanceof NodeType)) || ((_type instanceof ReferenceType)))
      {
        _select = new CastExpr(_select, Type.NodeSet);
        _type = Type.NodeSet;
      }
      if (((_type instanceof NodeSetType)) || ((_type instanceof ResultTreeType)))
      {
        typeCheckContents(paramSymbolTable);
        return Type.Void;
      }
      throw new TypeCheckError(this);
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    int i = 0;
    Stylesheet localStylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int j = paramMethodGenerator.getLocalIndex("current");
    Vector localVector = new Vector();
    Iterator localIterator1 = getContents().iterator();
    while (localIterator1.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator1.next();
      if ((localObject instanceof Sort)) {
        localVector.addElement((Sort)localObject);
      }
    }
    if ((localStylesheet.hasLocalParams()) || (hasContents()))
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
      localInstructionList.append(new INVOKEVIRTUAL(k));
      translateContents(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if ((_type != null) && ((_type instanceof ResultTreeType)))
    {
      if (localVector.size() > 0)
      {
        ErrorMsg localErrorMsg = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
        getParser().reportError(4, localErrorMsg);
      }
      _select.translate(paramClassGenerator, paramMethodGenerator);
      _type.translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      if (localVector.size() > 0)
      {
        Sort.translateSortIterator(paramClassGenerator, paramMethodGenerator, _select, localVector);
        int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(paramMethodGenerator.loadCurrentNode());
        localInstructionList.append(new INVOKEINTERFACE(m, 2));
        i = 1;
      }
      else if (_select == null)
      {
        Mode.compileGetChildren(paramClassGenerator, paramMethodGenerator, j);
      }
      else
      {
        _select.translate(paramClassGenerator, paramMethodGenerator);
      }
    }
    if ((_select != null) && (i == 0)) {
      _select.startIterator(paramClassGenerator, paramMethodGenerator);
    }
    String str = paramClassGenerator.getStylesheet().getClassName();
    localInstructionList.append(paramMethodGenerator.loadHandler());
    Object localObject = paramClassGenerator.getApplyTemplatesSig();
    int n = localConstantPoolGen.addMethodref(str, _functionName, (String)localObject);
    localInstructionList.append(new INVOKEVIRTUAL(n));
    Iterator localIterator2 = getContents().iterator();
    while (localIterator2.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator2.next();
      if ((localSyntaxTreeNode instanceof WithParam)) {
        ((WithParam)localSyntaxTreeNode).releaseResultTree(paramClassGenerator, paramMethodGenerator);
      }
    }
    if ((localStylesheet.hasLocalParams()) || (hasContents()))
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      int i1 = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      localInstructionList.append(new INVOKEVIRTUAL(i1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ApplyTemplates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */