package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelImpl;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl.Entry;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.util.ArrayList;

public class XSGrammarPool
  extends XMLGrammarPoolImpl
{
  public XSGrammarPool() {}
  
  public XSModel toXSModel()
  {
    return toXSModel((short)1);
  }
  
  public XSModel toXSModel(short paramShort)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < fGrammars.length; i++) {
      for (localObject = fGrammars[i]; localObject != null; localObject = next) {
        if (desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema")) {
          localArrayList.add(grammar);
        }
      }
    }
    i = localArrayList.size();
    if (i == 0) {
      return toXSModel(new SchemaGrammar[0], paramShort);
    }
    Object localObject = (SchemaGrammar[])localArrayList.toArray(new SchemaGrammar[i]);
    return toXSModel((SchemaGrammar[])localObject, paramShort);
  }
  
  protected XSModel toXSModel(SchemaGrammar[] paramArrayOfSchemaGrammar, short paramShort)
  {
    return new XSModelImpl(paramArrayOfSchemaGrammar, paramShort);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XSGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */