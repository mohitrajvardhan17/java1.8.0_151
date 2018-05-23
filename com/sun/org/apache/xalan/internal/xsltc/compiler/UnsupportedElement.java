package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.List;
import java.util.Vector;

final class UnsupportedElement
  extends SyntaxTreeNode
{
  private Vector _fallbacks = null;
  private ErrorMsg _message = null;
  private boolean _isExtension = false;
  
  public UnsupportedElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    super(paramString1, paramString2, paramString3);
    _isExtension = paramBoolean;
  }
  
  public void setErrorMessage(ErrorMsg paramErrorMsg)
  {
    _message = paramErrorMsg;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Unsupported element = " + _qname.getNamespace() + ":" + _qname.getLocalPart());
    displayContents(paramInt + 4);
  }
  
  private void processFallbacks(Parser paramParser)
  {
    List localList = getContents();
    if (localList != null)
    {
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localList.get(j);
        if ((localSyntaxTreeNode instanceof Fallback))
        {
          Fallback localFallback = (Fallback)localSyntaxTreeNode;
          localFallback.activate();
          localFallback.parseContents(paramParser);
          if (_fallbacks == null) {
            _fallbacks = new Vector();
          }
          _fallbacks.addElement(localSyntaxTreeNode);
        }
      }
    }
  }
  
  public void parseContents(Parser paramParser)
  {
    processFallbacks(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_fallbacks != null)
    {
      int i = _fallbacks.size();
      for (int j = 0; j < i; j++)
      {
        Fallback localFallback = (Fallback)_fallbacks.elementAt(j);
        localFallback.typeCheck(paramSymbolTable);
      }
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_fallbacks != null)
    {
      int i = _fallbacks.size();
      for (int j = 0; j < i; j++)
      {
        Fallback localFallback = (Fallback)_fallbacks.elementAt(j);
        localFallback.translate(paramClassGenerator, paramMethodGenerator);
      }
    }
    else
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      int k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unsupported_ElementF", "(Ljava/lang/String;Z)V");
      localInstructionList.append(new PUSH(localConstantPoolGen, getQName().toString()));
      localInstructionList.append(new PUSH(localConstantPoolGen, _isExtension));
      localInstructionList.append(new INVOKESTATIC(k));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnsupportedElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */