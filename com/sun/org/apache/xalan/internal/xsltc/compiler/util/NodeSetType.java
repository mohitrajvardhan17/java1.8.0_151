package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

public final class NodeSetType
  extends Type
{
  protected NodeSetType() {}
  
  public String toString()
  {
    return "node-set";
  }
  
  public boolean identicalTo(Type paramType)
  {
    return this == paramType;
  }
  
  public String toSignature()
  {
    return "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return new com.sun.org.apache.bcel.internal.generic.ObjectType("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator");
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    if (paramType == Type.String)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    }
    else if (paramType == Type.Boolean)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    }
    else if (paramType == Type.Real)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (RealType)paramType);
    }
    else if (paramType == Type.Node)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (NodeType)paramType);
    }
    else if (paramType == Type.Reference)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ReferenceType)paramType);
    }
    else if (paramType == Type.Object)
    {
      translateTo(paramClassGenerator, paramMethodGenerator, (ObjectType)paramType);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    int i;
    if (paramClass.getName().equals("org.w3c.dom.NodeList"))
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "nodeList2Iterator", "(Lorg/w3c/dom/NodeList;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(new INVOKESTATIC(i));
    }
    else if (paramClass.getName().equals("org.w3c.dom.Node"))
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "node2Iterator", "(Lorg/w3c/dom/Node;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(new INVOKESTATIC(i));
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getName());
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    FlowList localFlowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, paramBooleanType);
    localInstructionList.append(ICONST_1);
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    localFlowList.backPatch(localInstructionList.append(ICONST_0));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    getFirstNode(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(DUP);
    BranchHandle localBranchHandle1 = localInstructionList.append(new IFLT(null));
    Type.Node.translateTo(paramClassGenerator, paramMethodGenerator, paramStringType);
    BranchHandle localBranchHandle2 = localInstructionList.append(new GOTO(null));
    localBranchHandle1.setTarget(localInstructionList.append(POP));
    localInstructionList.append(new PUSH(paramClassGenerator.getConstantPool(), ""));
    localBranchHandle2.setTarget(localInstructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, RealType paramRealType)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
    Type.String.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, NodeType paramNodeType)
  {
    getFirstNode(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ObjectType paramObjectType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    getFirstNode(paramClassGenerator, paramMethodGenerator);
    return new FlowList(localInstructionList.append(new IFLT(null)));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, ReferenceType paramReferenceType)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    String str = paramClass.getName();
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(SWAP);
    int i;
    if (str.equals("org.w3c.dom.Node"))
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else if ((str.equals("org.w3c.dom.NodeList")) || (str.equals("java.lang.Object")))
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
    }
    else if (str.equals("java.lang.String"))
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
      int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
      localInstructionList.append(new INVOKEINTERFACE(j, 2));
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), str);
      paramClassGenerator.getParser().reportError(2, localErrorMsg);
    }
  }
  
  private void getFirstNode(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new INVOKEINTERFACE(localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I"), 1));
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateTo(paramClassGenerator, paramMethodGenerator, Type.Reference);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    paramMethodGenerator.getInstructionList().append(NOP);
  }
  
  public String getClassName()
  {
    return "com.sun.org.apache.xml.internal.dtm.DTMAxisIterator";
  }
  
  public Instruction LOAD(int paramInt)
  {
    return new ALOAD(paramInt);
  }
  
  public Instruction STORE(int paramInt)
  {
    return new ASTORE(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\NodeSetType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */