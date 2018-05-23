package com.sun.jndi.toolkit.dir;

import java.util.Properties;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

final class HierarchicalNameParser
  implements NameParser
{
  static final Properties mySyntax = new Properties();
  
  HierarchicalNameParser() {}
  
  public Name parse(String paramString)
    throws NamingException
  {
    return new HierarchicalName(paramString, mySyntax);
  }
  
  static
  {
    mySyntax.put("jndi.syntax.direction", "left_to_right");
    mySyntax.put("jndi.syntax.separator", "/");
    mySyntax.put("jndi.syntax.ignorecase", "true");
    mySyntax.put("jndi.syntax.escape", "\\");
    mySyntax.put("jndi.syntax.beginquote", "\"");
    mySyntax.put("jndi.syntax.trimblanks", "false");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\HierarchicalNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */