package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import java.util.Iterator;
import java.util.Vector;

public final class FlowList
{
  private Vector _elements;
  
  public FlowList()
  {
    _elements = null;
  }
  
  public FlowList(InstructionHandle paramInstructionHandle)
  {
    _elements = new Vector();
    _elements.addElement(paramInstructionHandle);
  }
  
  public FlowList(FlowList paramFlowList)
  {
    _elements = _elements;
  }
  
  public FlowList add(InstructionHandle paramInstructionHandle)
  {
    if (_elements == null) {
      _elements = new Vector();
    }
    _elements.addElement(paramInstructionHandle);
    return this;
  }
  
  public FlowList append(FlowList paramFlowList)
  {
    if (_elements == null)
    {
      _elements = _elements;
    }
    else
    {
      Vector localVector = _elements;
      if (localVector != null)
      {
        int i = localVector.size();
        for (int j = 0; j < i; j++) {
          _elements.addElement(localVector.elementAt(j));
        }
      }
    }
    return this;
  }
  
  public void backPatch(InstructionHandle paramInstructionHandle)
  {
    if (_elements != null)
    {
      int i = _elements.size();
      for (int j = 0; j < i; j++)
      {
        BranchHandle localBranchHandle = (BranchHandle)_elements.elementAt(j);
        localBranchHandle.setTarget(paramInstructionHandle);
      }
      _elements.clear();
    }
  }
  
  public FlowList copyAndRedirect(InstructionList paramInstructionList1, InstructionList paramInstructionList2)
  {
    FlowList localFlowList = new FlowList();
    if (_elements == null) {
      return localFlowList;
    }
    int i = _elements.size();
    Iterator localIterator1 = paramInstructionList1.iterator();
    Iterator localIterator2 = paramInstructionList2.iterator();
    while (localIterator1.hasNext())
    {
      InstructionHandle localInstructionHandle1 = (InstructionHandle)localIterator1.next();
      InstructionHandle localInstructionHandle2 = (InstructionHandle)localIterator2.next();
      for (int j = 0; j < i; j++) {
        if (_elements.elementAt(j) == localInstructionHandle1) {
          localFlowList.add(localInstructionHandle2);
        }
      }
    }
    return localFlowList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FlowList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */