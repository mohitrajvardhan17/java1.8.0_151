package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class ArgumentList
{
  private final Expression _arg;
  private final ArgumentList _rest;
  
  public ArgumentList(Expression paramExpression, ArgumentList paramArgumentList)
  {
    _arg = paramExpression;
    _rest = paramArgumentList;
  }
  
  public String toString()
  {
    return _arg.toString() + ", " + _rest.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ArgumentList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */