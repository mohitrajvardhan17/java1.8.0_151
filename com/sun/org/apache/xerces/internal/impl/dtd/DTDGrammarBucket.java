package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.HashMap;
import java.util.Map;

public class DTDGrammarBucket
{
  protected Map<XMLDTDDescription, DTDGrammar> fGrammars = new HashMap();
  protected DTDGrammar fActiveGrammar;
  protected boolean fIsStandalone;
  
  public DTDGrammarBucket() {}
  
  public void putGrammar(DTDGrammar paramDTDGrammar)
  {
    XMLDTDDescription localXMLDTDDescription = (XMLDTDDescription)paramDTDGrammar.getGrammarDescription();
    fGrammars.put(localXMLDTDDescription, paramDTDGrammar);
  }
  
  public DTDGrammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return (DTDGrammar)fGrammars.get((XMLDTDDescription)paramXMLGrammarDescription);
  }
  
  public void clear()
  {
    fGrammars.clear();
    fActiveGrammar = null;
    fIsStandalone = false;
  }
  
  void setStandalone(boolean paramBoolean)
  {
    fIsStandalone = paramBoolean;
  }
  
  boolean getStandalone()
  {
    return fIsStandalone;
  }
  
  void setActiveGrammar(DTDGrammar paramDTDGrammar)
  {
    fActiveGrammar = paramDTDGrammar;
  }
  
  DTDGrammar getActiveGrammar()
  {
    return fActiveGrammar;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\DTDGrammarBucket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */