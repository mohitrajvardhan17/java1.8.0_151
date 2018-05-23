package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import java.lang.ref.WeakReference;

final class WeakReferenceXMLSchema
  extends AbstractXMLSchema
{
  private WeakReference fGrammarPool = new WeakReference(null);
  
  public WeakReferenceXMLSchema() {}
  
  public synchronized XMLGrammarPool getGrammarPool()
  {
    Object localObject = (XMLGrammarPool)fGrammarPool.get();
    if (localObject == null)
    {
      localObject = new SoftReferenceGrammarPool();
      fGrammarPool = new WeakReference(localObject);
    }
    return (XMLGrammarPool)localObject;
  }
  
  public boolean isFullyComposed()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\WeakReferenceXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */