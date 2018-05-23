package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class Key
  extends TopLevelElement
{
  private QName _name;
  private Pattern _match;
  private Expression _use;
  private Type _useType;
  
  Key() {}
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (!XML11Char.isXML11ValidQName(str))
    {
      ErrorMsg localErrorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
      paramParser.reportError(3, localErrorMsg);
    }
    _name = paramParser.getQNameIgnoreDefaultNs(str);
    getSymbolTable().addKey(_name, this);
    _match = paramParser.parsePattern(this, "match", null);
    _use = paramParser.parseExpression(this, "use", null);
    if (_name == null)
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
      return;
    }
    if (_match.isDummy())
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "match");
      return;
    }
    if (_use.isDummy())
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "use");
      return;
    }
  }
  
  public String getName()
  {
    return _name.toString();
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _match.typeCheck(paramSymbolTable);
    _useType = _use.typeCheck(paramSymbolTable);
    if ((!(_useType instanceof StringType)) && (!(_useType instanceof NodeSetType))) {
      _use = new CastExpr(_use, Type.String);
    }
    return Type.Void;
  }
  
  public void traverseNodeSet(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
    int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable("parentNode", Util.getJCRefType("I"), null, null);
    localLocalVariableGen.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen.getIndex())));
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    _use.translate(paramClassGenerator, paramMethodGenerator);
    _use.startIterator(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramMethodGenerator.storeIterator());
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    InstructionHandle localInstructionHandle = localInstructionList.append(NOP);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, _name.toString()));
    localLocalVariableGen.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen.getIndex())));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    localInstructionList.append(new INVOKEVIRTUAL(paramInt));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, getName()));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new INVOKEVIRTUAL(k));
    localBranchHandle.setTarget(localInstructionList.append(paramMethodGenerator.loadIterator()));
    localInstructionList.append(paramMethodGenerator.nextNode());
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localInstructionList.append(new IFGE(localInstructionHandle));
    localInstructionList.append(paramMethodGenerator.storeIterator());
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = paramMethodGenerator.getLocalIndex("current");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "buildKeyIndex", "(Ljava/lang/String;ILjava/lang/String;)V");
    int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "setKeyIndexDom", "(Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
    int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    int n = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new PUSH(localConstantPoolGen, 4));
    localInstructionList.append(new INVOKEINTERFACE(n, 2));
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.setStartNode());
    localInstructionList.append(paramMethodGenerator.storeIterator());
    BranchHandle localBranchHandle1 = localInstructionList.append(new GOTO(null));
    InstructionHandle localInstructionHandle1 = localInstructionList.append(NOP);
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    _match.translate(paramClassGenerator, paramMethodGenerator);
    _match.synthesize(paramClassGenerator, paramMethodGenerator);
    BranchHandle localBranchHandle2 = localInstructionList.append(new IFEQ(null));
    if ((_useType instanceof NodeSetType))
    {
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      traverseNodeSet(paramClassGenerator, paramMethodGenerator, j);
    }
    else
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _name.toString()));
      localInstructionList.append(DUP_X1);
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      _use.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(j));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new INVOKEVIRTUAL(k));
    }
    InstructionHandle localInstructionHandle2 = localInstructionList.append(NOP);
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(paramMethodGenerator.nextNode());
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localInstructionList.append(new IFGT(localInstructionHandle1));
    localInstructionList.append(paramMethodGenerator.storeIterator());
    localInstructionList.append(paramMethodGenerator.storeCurrentNode());
    localBranchHandle1.setTarget(localInstructionHandle2);
    localBranchHandle2.setTarget(localInstructionHandle2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */