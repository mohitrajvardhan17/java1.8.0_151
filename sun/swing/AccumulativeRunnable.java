package sun.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

public abstract class AccumulativeRunnable<T>
  implements Runnable
{
  private List<T> arguments = null;
  
  public AccumulativeRunnable() {}
  
  protected abstract void run(List<T> paramList);
  
  public final void run()
  {
    run(flush());
  }
  
  @SafeVarargs
  public final synchronized void add(T... paramVarArgs)
  {
    int i = 1;
    if (arguments == null)
    {
      i = 0;
      arguments = new ArrayList();
    }
    Collections.addAll(arguments, paramVarArgs);
    if (i == 0) {
      submit();
    }
  }
  
  protected void submit()
  {
    SwingUtilities.invokeLater(this);
  }
  
  private final synchronized List<T> flush()
  {
    List localList = arguments;
    arguments = null;
    return localList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\AccumulativeRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */