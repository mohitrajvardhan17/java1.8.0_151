package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;

public class TypeCheckError
  extends Exception
{
  static final long serialVersionUID = 3246224233917854640L;
  ErrorMsg _error = null;
  SyntaxTreeNode _node = null;
  
  public TypeCheckError(SyntaxTreeNode paramSyntaxTreeNode)
  {
    _node = paramSyntaxTreeNode;
  }
  
  public TypeCheckError(ErrorMsg paramErrorMsg)
  {
    _error = paramErrorMsg;
  }
  
  public TypeCheckError(String paramString, Object paramObject)
  {
    _error = new ErrorMsg(paramString, paramObject);
  }
  
  public TypeCheckError(String paramString, Object paramObject1, Object paramObject2)
  {
    _error = new ErrorMsg(paramString, paramObject1, paramObject2);
  }
  
  public ErrorMsg getErrorMsg()
  {
    return _error;
  }
  
  public String getMessage()
  {
    return toString();
  }
  
  public String toString()
  {
    if (_error == null) {
      if (_node != null) {
        _error = new ErrorMsg("TYPE_CHECK_ERR", _node.toString());
      } else {
        _error = new ErrorMsg("TYPE_CHECK_UNK_LOC_ERR");
      }
    }
    return _error.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\TypeCheckError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */