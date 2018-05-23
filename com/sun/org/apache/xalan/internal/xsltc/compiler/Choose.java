package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

final class Choose
  extends Instruction
{
  Choose() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Choose");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Vector localVector1 = new Vector();
    Otherwise localOtherwise = null;
    Iterator localIterator = elements();
    ErrorMsg localErrorMsg = null;
    int i = getLineNumber();
    while (localIterator.hasNext())
    {
      localObject1 = (SyntaxTreeNode)localIterator.next();
      if ((localObject1 instanceof When))
      {
        localVector1.addElement(localObject1);
      }
      else if ((localObject1 instanceof Otherwise))
      {
        if (localOtherwise == null)
        {
          localOtherwise = (Otherwise)localObject1;
        }
        else
        {
          localErrorMsg = new ErrorMsg("MULTIPLE_OTHERWISE_ERR", this);
          getParser().reportError(3, localErrorMsg);
        }
      }
      else if ((localObject1 instanceof Text))
      {
        ((Text)localObject1).ignore();
      }
      else
      {
        localErrorMsg = new ErrorMsg("WHEN_ELEMENT_ERR", this);
        getParser().reportError(3, localErrorMsg);
      }
    }
    if (localVector1.size() == 0)
    {
      localErrorMsg = new ErrorMsg("MISSING_WHEN_ERR", this);
      getParser().reportError(3, localErrorMsg);
      return;
    }
    Object localObject1 = paramMethodGenerator.getInstructionList();
    BranchHandle localBranchHandle = null;
    Vector localVector2 = new Vector();
    InstructionHandle localInstructionHandle1 = null;
    Enumeration localEnumeration = localVector1.elements();
    Object localObject3;
    while (localEnumeration.hasMoreElements())
    {
      localObject2 = (When)localEnumeration.nextElement();
      localObject3 = ((When)localObject2).getTest();
      InstructionHandle localInstructionHandle2 = ((InstructionList)localObject1).getEnd();
      if (localBranchHandle != null) {
        localBranchHandle.setTarget(((InstructionList)localObject1).append(NOP));
      }
      ((Expression)localObject3).translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      if ((localObject3 instanceof FunctionCall))
      {
        FunctionCall localFunctionCall = (FunctionCall)localObject3;
        try
        {
          Type localType = localFunctionCall.typeCheck(getParser().getSymbolTable());
          if (localType != Type.Boolean) {
            _falseList.add(((InstructionList)localObject1).append(new IFEQ(null)));
          }
        }
        catch (TypeCheckError localTypeCheckError) {}
      }
      localInstructionHandle2 = ((InstructionList)localObject1).getEnd();
      if (!((When)localObject2).ignore()) {
        ((When)localObject2).translateContents(paramClassGenerator, paramMethodGenerator);
      }
      localVector2.addElement(((InstructionList)localObject1).append(new GOTO(null)));
      if ((localEnumeration.hasMoreElements()) || (localOtherwise != null))
      {
        localBranchHandle = ((InstructionList)localObject1).append(new GOTO(null));
        ((Expression)localObject3).backPatchFalseList(localBranchHandle);
      }
      else
      {
        ((Expression)localObject3).backPatchFalseList(localInstructionHandle1 = ((InstructionList)localObject1).append(NOP));
      }
      ((Expression)localObject3).backPatchTrueList(localInstructionHandle2.getNext());
    }
    if (localOtherwise != null)
    {
      localBranchHandle.setTarget(((InstructionList)localObject1).append(NOP));
      localOtherwise.translateContents(paramClassGenerator, paramMethodGenerator);
      localInstructionHandle1 = ((InstructionList)localObject1).append(NOP);
    }
    Object localObject2 = localVector2.elements();
    while (((Enumeration)localObject2).hasMoreElements())
    {
      localObject3 = (BranchHandle)((Enumeration)localObject2).nextElement();
      ((BranchHandle)localObject3).setTarget(localInstructionHandle1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Choose.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */