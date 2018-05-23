package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class ConcatCall
  extends FunctionCall
{
  public ConcatCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    for (int i = 0; i < argumentCount(); i++)
    {
      Expression localExpression = argument(i);
      if (!localExpression.typeCheck(paramSymbolTable).identicalTo(Type.String)) {
        setArgument(i, new CastExpr(localExpression, Type.String));
      }
    }
    return _type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = argumentCount();
    switch (i)
    {
    case 0: 
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
      break;
    case 1: 
      argument().translate(paramClassGenerator, paramMethodGenerator);
      break;
    default: 
      int j = localConstantPoolGen.addMethodref("java.lang.StringBuffer", "<init>", "()V");
      INVOKEVIRTUAL localINVOKEVIRTUAL = new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
      int k = localConstantPoolGen.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.lang.StringBuffer")));
      localInstructionList.append(DUP);
      localInstructionList.append(new INVOKESPECIAL(j));
      for (int m = 0; m < i; m++)
      {
        argument(m).translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(localINVOKEVIRTUAL);
      }
      localInstructionList.append(new INVOKEVIRTUAL(k));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ConcatCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */