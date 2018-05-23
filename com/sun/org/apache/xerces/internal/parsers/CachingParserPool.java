package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.ShadowedSymbolTable;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class CachingParserPool
{
  public static final boolean DEFAULT_SHADOW_SYMBOL_TABLE = false;
  public static final boolean DEFAULT_SHADOW_GRAMMAR_POOL = false;
  protected SymbolTable fSynchronizedSymbolTable;
  protected XMLGrammarPool fSynchronizedGrammarPool;
  protected boolean fShadowSymbolTable = false;
  protected boolean fShadowGrammarPool = false;
  
  public CachingParserPool()
  {
    this(new SymbolTable(), new XMLGrammarPoolImpl());
  }
  
  public CachingParserPool(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    fSynchronizedSymbolTable = new SynchronizedSymbolTable(paramSymbolTable);
    fSynchronizedGrammarPool = new SynchronizedGrammarPool(paramXMLGrammarPool);
  }
  
  public SymbolTable getSymbolTable()
  {
    return fSynchronizedSymbolTable;
  }
  
  public XMLGrammarPool getXMLGrammarPool()
  {
    return fSynchronizedGrammarPool;
  }
  
  public void setShadowSymbolTable(boolean paramBoolean)
  {
    fShadowSymbolTable = paramBoolean;
  }
  
  public DOMParser createDOMParser()
  {
    SymbolTable localSymbolTable = fShadowSymbolTable ? new ShadowedSymbolTable(fSynchronizedSymbolTable) : fSynchronizedSymbolTable;
    XMLGrammarPool localXMLGrammarPool = fShadowGrammarPool ? new ShadowedGrammarPool(fSynchronizedGrammarPool) : fSynchronizedGrammarPool;
    return new DOMParser(localSymbolTable, localXMLGrammarPool);
  }
  
  public SAXParser createSAXParser()
  {
    SymbolTable localSymbolTable = fShadowSymbolTable ? new ShadowedSymbolTable(fSynchronizedSymbolTable) : fSynchronizedSymbolTable;
    XMLGrammarPool localXMLGrammarPool = fShadowGrammarPool ? new ShadowedGrammarPool(fSynchronizedGrammarPool) : fSynchronizedGrammarPool;
    return new SAXParser(localSymbolTable, localXMLGrammarPool);
  }
  
  public static final class ShadowedGrammarPool
    extends XMLGrammarPoolImpl
  {
    private XMLGrammarPool fGrammarPool;
    
    public ShadowedGrammarPool(XMLGrammarPool paramXMLGrammarPool)
    {
      fGrammarPool = paramXMLGrammarPool;
    }
    
    public Grammar[] retrieveInitialGrammarSet(String paramString)
    {
      Grammar[] arrayOfGrammar = super.retrieveInitialGrammarSet(paramString);
      if (arrayOfGrammar != null) {
        return arrayOfGrammar;
      }
      return fGrammarPool.retrieveInitialGrammarSet(paramString);
    }
    
    public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      Grammar localGrammar = super.retrieveGrammar(paramXMLGrammarDescription);
      if (localGrammar != null) {
        return localGrammar;
      }
      return fGrammarPool.retrieveGrammar(paramXMLGrammarDescription);
    }
    
    public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar)
    {
      super.cacheGrammars(paramString, paramArrayOfGrammar);
      fGrammarPool.cacheGrammars(paramString, paramArrayOfGrammar);
    }
    
    public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      if (super.containsGrammar(paramXMLGrammarDescription)) {
        return super.getGrammar(paramXMLGrammarDescription);
      }
      return null;
    }
    
    public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      return super.containsGrammar(paramXMLGrammarDescription);
    }
  }
  
  public static final class SynchronizedGrammarPool
    implements XMLGrammarPool
  {
    private XMLGrammarPool fGrammarPool;
    
    public SynchronizedGrammarPool(XMLGrammarPool paramXMLGrammarPool)
    {
      fGrammarPool = paramXMLGrammarPool;
    }
    
    /* Error */
    public Grammar[] retrieveInitialGrammarSet(String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 42	com/sun/org/apache/xerces/internal/parsers/CachingParserPool$SynchronizedGrammarPool:fGrammarPool	Lcom/sun/org/apache/xerces/internal/xni/grammars/XMLGrammarPool;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 42	com/sun/org/apache/xerces/internal/parsers/CachingParserPool$SynchronizedGrammarPool:fGrammarPool	Lcom/sun/org/apache/xerces/internal/xni/grammars/XMLGrammarPool;
      //   11: aload_1
      //   12: invokeinterface 48 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedGrammarPool
      //   0	25	1	paramString	String
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    /* Error */
    public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 42	com/sun/org/apache/xerces/internal/parsers/CachingParserPool$SynchronizedGrammarPool:fGrammarPool	Lcom/sun/org/apache/xerces/internal/xni/grammars/XMLGrammarPool;
      //   4: dup
      //   5: astore_2
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 42	com/sun/org/apache/xerces/internal/parsers/CachingParserPool$SynchronizedGrammarPool:fGrammarPool	Lcom/sun/org/apache/xerces/internal/xni/grammars/XMLGrammarPool;
      //   11: aload_1
      //   12: invokeinterface 47 2 0
      //   17: aload_2
      //   18: monitorexit
      //   19: areturn
      //   20: astore_3
      //   21: aload_2
      //   22: monitorexit
      //   23: aload_3
      //   24: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	25	0	this	SynchronizedGrammarPool
      //   0	25	1	paramXMLGrammarDescription	XMLGrammarDescription
      //   5	17	2	Ljava/lang/Object;	Object
      //   20	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	19	20	finally
      //   20	23	20	finally
    }
    
    public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar)
    {
      synchronized (fGrammarPool)
      {
        fGrammarPool.cacheGrammars(paramString, paramArrayOfGrammar);
      }
    }
    
    public void lockPool()
    {
      synchronized (fGrammarPool)
      {
        fGrammarPool.lockPool();
      }
    }
    
    public void clear()
    {
      synchronized (fGrammarPool)
      {
        fGrammarPool.clear();
      }
    }
    
    public void unlockPool()
    {
      synchronized (fGrammarPool)
      {
        fGrammarPool.unlockPool();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\CachingParserPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */