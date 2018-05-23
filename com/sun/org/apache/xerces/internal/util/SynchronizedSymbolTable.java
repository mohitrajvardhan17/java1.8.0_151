package com.sun.org.apache.xerces.internal.util;

public final class SynchronizedSymbolTable
  extends SymbolTable
{
  protected SymbolTable fSymbolTable;
  
  public SynchronizedSymbolTable(SymbolTable paramSymbolTable)
  {
    fSymbolTable = paramSymbolTable;
  }
  
  public SynchronizedSymbolTable()
  {
    fSymbolTable = new SymbolTable();
  }
  
  public SynchronizedSymbolTable(int paramInt)
  {
    fSymbolTable = new SymbolTable(paramInt);
  }
  
  /* Error */
  public String addSymbol(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   11: aload_1
    //   12: invokevirtual 40	com/sun/org/apache/xerces/internal/util/SymbolTable:addSymbol	(Ljava/lang/String;)Ljava/lang/String;
    //   15: aload_2
    //   16: monitorexit
    //   17: areturn
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	23	0	this	SynchronizedSymbolTable
    //   0	23	1	paramString	String
    //   5	15	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	18	finally
    //   18	21	18	finally
  }
  
  /* Error */
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   4: dup
    //   5: astore 4
    //   7: monitorenter
    //   8: aload_0
    //   9: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   12: aload_1
    //   13: iload_2
    //   14: iload_3
    //   15: invokevirtual 39	com/sun/org/apache/xerces/internal/util/SymbolTable:addSymbol	([CII)Ljava/lang/String;
    //   18: aload 4
    //   20: monitorexit
    //   21: areturn
    //   22: astore 5
    //   24: aload 4
    //   26: monitorexit
    //   27: aload 5
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	SynchronizedSymbolTable
    //   0	30	1	paramArrayOfChar	char[]
    //   0	30	2	paramInt1	int
    //   0	30	3	paramInt2	int
    //   5	20	4	Ljava/lang/Object;	Object
    //   22	6	5	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   8	21	22	finally
    //   22	27	22	finally
  }
  
  /* Error */
  public boolean containsSymbol(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   11: aload_1
    //   12: invokevirtual 38	com/sun/org/apache/xerces/internal/util/SymbolTable:containsSymbol	(Ljava/lang/String;)Z
    //   15: aload_2
    //   16: monitorexit
    //   17: ireturn
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	23	0	this	SynchronizedSymbolTable
    //   0	23	1	paramString	String
    //   5	15	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	18	finally
    //   18	21	18	finally
  }
  
  /* Error */
  public boolean containsSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   4: dup
    //   5: astore 4
    //   7: monitorenter
    //   8: aload_0
    //   9: getfield 34	com/sun/org/apache/xerces/internal/util/SynchronizedSymbolTable:fSymbolTable	Lcom/sun/org/apache/xerces/internal/util/SymbolTable;
    //   12: aload_1
    //   13: iload_2
    //   14: iload_3
    //   15: invokevirtual 37	com/sun/org/apache/xerces/internal/util/SymbolTable:containsSymbol	([CII)Z
    //   18: aload 4
    //   20: monitorexit
    //   21: ireturn
    //   22: astore 5
    //   24: aload 4
    //   26: monitorexit
    //   27: aload 5
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	SynchronizedSymbolTable
    //   0	30	1	paramArrayOfChar	char[]
    //   0	30	2	paramInt1	int
    //   0	30	3	paramInt2	int
    //   5	20	4	Ljava/lang/Object;	Object
    //   22	6	5	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   8	21	22	finally
    //   22	27	22	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SynchronizedSymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */