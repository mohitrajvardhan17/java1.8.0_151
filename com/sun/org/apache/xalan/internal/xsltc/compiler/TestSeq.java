package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import java.util.Map;
import java.util.Vector;

final class TestSeq
{
  private int _kernelType;
  private Vector _patterns = null;
  private Mode _mode = null;
  private Template _default = null;
  private InstructionList _instructionList;
  private InstructionHandle _start = null;
  
  public TestSeq(Vector paramVector, Mode paramMode)
  {
    this(paramVector, -2, paramMode);
  }
  
  public TestSeq(Vector paramVector, int paramInt, Mode paramMode)
  {
    _patterns = paramVector;
    _kernelType = paramInt;
    _mode = paramMode;
  }
  
  public String toString()
  {
    int i = _patterns.size();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int j = 0; j < i; j++)
    {
      LocationPathPattern localLocationPathPattern = (LocationPathPattern)_patterns.elementAt(j);
      if (j == 0) {
        localStringBuffer.append("Testseq for kernel ").append(_kernelType).append('\n');
      }
      localStringBuffer.append("   pattern ").append(j).append(": ").append(localLocationPathPattern.toString()).append('\n');
    }
    return localStringBuffer.toString();
  }
  
  public InstructionList getInstructionList()
  {
    return _instructionList;
  }
  
  public double getPriority()
  {
    Template localTemplate = _patterns.size() == 0 ? _default : ((Pattern)_patterns.elementAt(0)).getTemplate();
    return localTemplate.getPriority();
  }
  
  public int getPosition()
  {
    Template localTemplate = _patterns.size() == 0 ? _default : ((Pattern)_patterns.elementAt(0)).getTemplate();
    return localTemplate.getPosition();
  }
  
  public void reduce()
  {
    Vector localVector = new Vector();
    int i = _patterns.size();
    for (int j = 0; j < i; j++)
    {
      LocationPathPattern localLocationPathPattern = (LocationPathPattern)_patterns.elementAt(j);
      localLocationPathPattern.reduceKernelPattern();
      if (localLocationPathPattern.isWildcard())
      {
        _default = localLocationPathPattern.getTemplate();
        break;
      }
      localVector.addElement(localLocationPathPattern);
    }
    _patterns = localVector;
  }
  
  public void findTemplates(Map<Template, Object> paramMap)
  {
    if (_default != null) {
      paramMap.put(_default, this);
    }
    for (int i = 0; i < _patterns.size(); i++)
    {
      LocationPathPattern localLocationPathPattern = (LocationPathPattern)_patterns.elementAt(i);
      paramMap.put(localLocationPathPattern.getTemplate(), this);
    }
  }
  
  private InstructionHandle getTemplateHandle(Template paramTemplate)
  {
    return _mode.getTemplateInstructionHandle(paramTemplate);
  }
  
  private LocationPathPattern getPattern(int paramInt)
  {
    return (LocationPathPattern)_patterns.elementAt(paramInt);
  }
  
  public InstructionHandle compile(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle)
  {
    if (_start != null) {
      return _start;
    }
    int i = _patterns.size();
    if (i == 0) {
      return _start = getTemplateHandle(_default);
    }
    InstructionHandle localInstructionHandle1 = _default == null ? paramInstructionHandle : getTemplateHandle(_default);
    for (int j = i - 1; j >= 0; j--)
    {
      LocationPathPattern localLocationPathPattern = getPattern(j);
      Template localTemplate = localLocationPathPattern.getTemplate();
      InstructionList localInstructionList1 = new InstructionList();
      localInstructionList1.append(paramMethodGenerator.loadCurrentNode());
      InstructionList localInstructionList2 = paramMethodGenerator.getInstructionList(localLocationPathPattern);
      if (localInstructionList2 == null)
      {
        localInstructionList2 = localLocationPathPattern.compile(paramClassGenerator, paramMethodGenerator);
        paramMethodGenerator.addInstructionList(localLocationPathPattern, localInstructionList2);
      }
      InstructionList localInstructionList3 = localInstructionList2.copy();
      FlowList localFlowList1 = localLocationPathPattern.getTrueList();
      if (localFlowList1 != null) {
        localFlowList1 = localFlowList1.copyAndRedirect(localInstructionList2, localInstructionList3);
      }
      FlowList localFlowList2 = localLocationPathPattern.getFalseList();
      if (localFlowList2 != null) {
        localFlowList2 = localFlowList2.copyAndRedirect(localInstructionList2, localInstructionList3);
      }
      localInstructionList1.append(localInstructionList3);
      InstructionHandle localInstructionHandle2 = getTemplateHandle(localTemplate);
      BranchHandle localBranchHandle = localInstructionList1.append(new GOTO_W(localInstructionHandle2));
      if (localFlowList1 != null) {
        localFlowList1.backPatch(localBranchHandle);
      }
      if (localFlowList2 != null) {
        localFlowList2.backPatch(localInstructionHandle1);
      }
      localInstructionHandle1 = localInstructionList1.getStart();
      if (_instructionList != null) {
        localInstructionList1.append(_instructionList);
      }
      _instructionList = localInstructionList1;
    }
    return _start = localInstructionHandle1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\TestSeq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */