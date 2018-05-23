package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunPageSelection
  implements PrintRequestAttribute
{
  public static final SunPageSelection ALL = new SunPageSelection(0);
  public static final SunPageSelection RANGE = new SunPageSelection(1);
  public static final SunPageSelection SELECTION = new SunPageSelection(2);
  private int pages;
  
  public SunPageSelection(int paramInt)
  {
    pages = paramInt;
  }
  
  public final Class getCategory()
  {
    return SunPageSelection.class;
  }
  
  public final String getName()
  {
    return "sun-page-selection";
  }
  
  public String toString()
  {
    return "page-selection: " + pages;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\SunPageSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */