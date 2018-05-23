package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunMinMaxPage
  implements PrintRequestAttribute
{
  private int page_max;
  private int page_min;
  
  public SunMinMaxPage(int paramInt1, int paramInt2)
  {
    page_min = paramInt1;
    page_max = paramInt2;
  }
  
  public final Class getCategory()
  {
    return SunMinMaxPage.class;
  }
  
  public final int getMin()
  {
    return page_min;
  }
  
  public final int getMax()
  {
    return page_max;
  }
  
  public final String getName()
  {
    return "sun-page-minmax";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\SunMinMaxPage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */