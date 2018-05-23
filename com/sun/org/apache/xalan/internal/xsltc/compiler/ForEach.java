package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

final class ForEach
  extends Instruction
{
  private Expression _select;
  private Type _type;
  
  ForEach() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("ForEach");
    indent(paramInt + 4);
    Util.println("select " + _select.toString());
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    _select = paramParser.parseExpression(this, "select", null);
    parseChildren(paramParser);
    if (_select.isDummy()) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "select");
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _type = _select.typeCheck(paramSymbolTable);
    if (((_type instanceof ReferenceType)) || ((_type instanceof NodeType)))
    {
      _select = new CastExpr(_select, Type.NodeSet);
      typeCheckContents(paramSymbolTable);
      return Type.Void;
    }
    if (((_type instanceof NodeSetType)) || ((_type instanceof ResultTreeType)))
    {
      typeCheckContents(paramSymbolTable);
      return Type.Void;
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    Vector localVector = new Vector();
    Iterator localIterator = elements();
    while (localIterator.hasNext())
    {
      localObject = (SyntaxTreeNode)localIterator.next();
      if ((localObject instanceof Sort)) {
        localVector.addElement(localObject);
      }
    }
    if ((_type != null) && ((_type instanceof ResultTreeType)))
    {
      localInstructionList.append(paramMethodGenerator.loadDOM());
      if (localVector.size() > 0)
      {
        localObject = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
        getParser().reportError(4, (ErrorMsg)localObject);
      }
      _select.translate(paramClassGenerator, paramMethodGenerator);
      _type.translateTo(paramClassGenerator, paramMethodGenerator, Type.NodeSet);
      localInstructionList.append(SWAP);
      localInstructionList.append(paramMethodGenerator.storeDOM());
    }
    else
    {
      if (localVector.size() > 0) {
        Sort.translateSortIterator(paramClassGenerator, paramMethodGenerator, _select, localVector);
      } else {
        _select.translate(paramClassGenerator, paramMethodGenerator);
      }
      if (!(_type instanceof ReferenceType))
      {
        localInstructionList.append(paramMethodGenerator.loadContextNode());
        localInstructionList.append(paramMethodGenerator.setStartNode());
      }
    }
    localInstructionList.append(paramMethodGenerator.storeIterator());
    initializeVariables(paramClassGenerator, paramMethodGenerator);
    Object localObject = localInstructionList.append(new GOTO(null));
    InstructionHandle localInstructionHandle = localInstructionList.append(NOP);
    translateContents(paramClassGenerator, paramMethodGenerator);
    ((BranchHandle)localObject).setTarget(localInstructionList.append(paramMethodGenerator.loadIterator()));
    localInstructionList.append(paramMethodGenerator.nextNode());
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localInstructionList.append(new IFGT(localInstructionHandle));
    if ((_type != null) && ((_type instanceof ResultTreeType))) {
      localInstructionList.append(paramMethodGenerator.storeDOM());
    }
    localInstructionList.append(paramMethodGenerator.storeIterator());
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
  }
  
  public void initializeVariables(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    int i = elementCount();
    for (int j = 0; j < i; j++)
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)getContents().get(j);
      if ((localSyntaxTreeNode instanceof Variable))
      {
        Variable localVariable = (Variable)localSyntaxTreeNode;
        localVariable.initialize(paramClassGenerator, paramMethodGenerator);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ForEach.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */