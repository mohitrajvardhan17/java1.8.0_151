package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Attribute
  extends Instruction
{
  private QName _name;
  
  Attribute() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Attribute " + _name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    _name = paramParser.getQName(getAttribute("name"));
    parseChildren(paramParser);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */