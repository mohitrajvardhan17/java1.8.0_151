package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class Comment
  extends Instruction
{
  Comment() {}
  
  public void parseContents(Parser paramParser)
  {
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    typeCheckContents(paramSymbolTable);
    return Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Text localText = null;
    if (elementCount() == 1)
    {
      Object localObject = elementAt(0);
      if ((localObject instanceof Text)) {
        localText = (Text)localObject;
      }
    }
    int i;
    if (localText != null)
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      if (localText.canLoadAsArrayOffsetLength())
      {
        localText.loadAsArrayOffsetLength(paramClassGenerator, paramMethodGenerator);
        i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "([CII)V");
        localInstructionList.append(new INVOKEINTERFACE(i, 4));
      }
      else
      {
        localInstructionList.append(new PUSH(localConstantPoolGen, localText.getText()));
        i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
        localInstructionList.append(new INVOKEINTERFACE(i, 2));
      }
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(DUP);
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
      localInstructionList.append(DUP);
      localInstructionList.append(paramMethodGenerator.storeHandler());
      translateContents(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "comment", "(Ljava/lang/String;)V");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      localInstructionList.append(paramMethodGenerator.storeHandler());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Comment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */