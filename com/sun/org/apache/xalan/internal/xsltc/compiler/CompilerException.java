package com.sun.org.apache.xalan.internal.xsltc.compiler;

public final class CompilerException
  extends Exception
{
  static final long serialVersionUID = 1732939618562742663L;
  private String _msg;
  
  public CompilerException() {}
  
  public CompilerException(Exception paramException)
  {
    super(paramException.toString());
    _msg = paramException.toString();
  }
  
  public CompilerException(String paramString)
  {
    super(paramString);
    _msg = paramString;
  }
  
  public String getMessage()
  {
    int i = _msg.indexOf(':');
    if (i > -1) {
      return _msg.substring(i);
    }
    return _msg;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CompilerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */