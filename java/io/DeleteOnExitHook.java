package java.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

class DeleteOnExitHook
{
  private static LinkedHashSet<String> files = new LinkedHashSet();
  
  private DeleteOnExitHook() {}
  
  static synchronized void add(String paramString)
  {
    if (files == null) {
      throw new IllegalStateException("Shutdown in progress");
    }
    files.add(paramString);
  }
  
  static void runHooks()
  {
    LinkedHashSet localLinkedHashSet;
    synchronized (DeleteOnExitHook.class)
    {
      localLinkedHashSet = files;
      files = null;
    }
    ??? = new ArrayList(localLinkedHashSet);
    Collections.reverse((List)???);
    Iterator localIterator = ((ArrayList)???).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      new File(str).delete();
    }
  }
  
  static
  {
    SharedSecrets.getJavaLangAccess().registerShutdownHook(2, true, new Runnable()
    {
      public void run() {}
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\DeleteOnExitHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */