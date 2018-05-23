package javax.print.attribute.standard;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;

public final class PrinterStateReasons
  extends HashMap<PrinterStateReason, Severity>
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = -3731791085163619457L;
  
  public PrinterStateReasons() {}
  
  public PrinterStateReasons(int paramInt)
  {
    super(paramInt);
  }
  
  public PrinterStateReasons(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public PrinterStateReasons(Map<PrinterStateReason, Severity> paramMap)
  {
    this();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put((PrinterStateReason)localEntry.getKey(), (Severity)localEntry.getValue());
    }
  }
  
  public Severity put(PrinterStateReason paramPrinterStateReason, Severity paramSeverity)
  {
    if (paramPrinterStateReason == null) {
      throw new NullPointerException("reason is null");
    }
    if (paramSeverity == null) {
      throw new NullPointerException("severity is null");
    }
    return (Severity)super.put(paramPrinterStateReason, paramSeverity);
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterStateReasons.class;
  }
  
  public final String getName()
  {
    return "printer-state-reasons";
  }
  
  public Set<PrinterStateReason> printerStateReasonSet(Severity paramSeverity)
  {
    if (paramSeverity == null) {
      throw new NullPointerException("severity is null");
    }
    return new PrinterStateReasonSet(paramSeverity, entrySet());
  }
  
  private class PrinterStateReasonSet
    extends AbstractSet<PrinterStateReason>
  {
    private Severity mySeverity;
    private Set myEntrySet;
    
    public PrinterStateReasonSet(Severity paramSeverity, Set paramSet)
    {
      mySeverity = paramSeverity;
      myEntrySet = paramSet;
    }
    
    public int size()
    {
      int i = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        localIterator.next();
        i++;
      }
      return i;
    }
    
    public Iterator iterator()
    {
      return new PrinterStateReasons.PrinterStateReasonSetIterator(PrinterStateReasons.this, mySeverity, myEntrySet.iterator());
    }
  }
  
  private class PrinterStateReasonSetIterator
    implements Iterator
  {
    private Severity mySeverity;
    private Iterator myIterator;
    private Map.Entry myEntry;
    
    public PrinterStateReasonSetIterator(Severity paramSeverity, Iterator paramIterator)
    {
      mySeverity = paramSeverity;
      myIterator = paramIterator;
      goToNext();
    }
    
    private void goToNext()
    {
      myEntry = null;
      while ((myEntry == null) && (myIterator.hasNext()))
      {
        myEntry = ((Map.Entry)myIterator.next());
        if ((Severity)myEntry.getValue() != mySeverity) {
          myEntry = null;
        }
      }
    }
    
    public boolean hasNext()
    {
      return myEntry != null;
    }
    
    public Object next()
    {
      if (myEntry == null) {
        throw new NoSuchElementException();
      }
      Object localObject = myEntry.getKey();
      goToNext();
      return localObject;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PrinterStateReasons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */