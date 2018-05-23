package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Copy
  extends Instruction
{
  private UseAttributeSets _useSets;
  
  Copy() {}
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("use-attribute-sets");
    if (str.length() > 0)
    {
      if (!Util.isValidQNames(str))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, localErrorMsg);
      }
      _useSets = new UseAttributeSets(str, paramParser);
    }
    parseChildren(paramParser);
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Copy");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_useSets != null) {
      _useSets.typeCheck(paramSymbolTable);
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable2("name", Util.getJCRefType("Ljava/lang/String;"), null);
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable2("length", Util.getJCRefType("I"), null);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    localInstructionList.append(paramMethodGenerator.loadHandler());
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "shallowCopy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)Ljava/lang/String;");
    localInstructionList.append(new INVOKEINTERFACE(i, 3));
    localInstructionList.append(DUP);
    localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
    BranchHandle localBranchHandle1 = localInstructionList.append(new IFNULL(null));
    localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex()));
    int j = localConstantPoolGen.addMethodref("java.lang.String", "length", "()I");
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(DUP);
    localLocalVariableGen2.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen2.getIndex())));
    BranchHandle localBranchHandle2 = localInstructionList.append(new IFEQ(null));
    if (_useSets != null)
    {
      localObject1 = getParent();
      if (((localObject1 instanceof LiteralElement)) || ((localObject1 instanceof LiteralElement)))
      {
        _useSets.translate(paramClassGenerator, paramMethodGenerator);
      }
      else
      {
        localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex()));
        localObject2 = localInstructionList.append(new IFEQ(null));
        _useSets.translate(paramClassGenerator, paramMethodGenerator);
        ((BranchHandle)localObject2).setTarget(localInstructionList.append(NOP));
      }
    }
    localBranchHandle2.setTarget(localInstructionList.append(NOP));
    translateContents(paramClassGenerator, paramMethodGenerator);
    localLocalVariableGen2.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex())));
    Object localObject1 = localInstructionList.append(new IFEQ(null));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
    localInstructionList.append(paramMethodGenerator.endElement());
    Object localObject2 = localInstructionList.append(NOP);
    localBranchHandle1.setTarget((InstructionHandle)localObject2);
    ((BranchHandle)localObject1).setTarget((InstructionHandle)localObject2);
    paramMethodGenerator.removeLocalVariable(localLocalVariableGen1);
    paramMethodGenerator.removeLocalVariable(localLocalVariableGen2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Copy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */