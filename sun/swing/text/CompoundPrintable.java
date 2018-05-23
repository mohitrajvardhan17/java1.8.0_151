package sun.swing.text;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CompoundPrintable
  implements CountingPrintable
{
  private final Queue<CountingPrintable> printables;
  private int offset = 0;
  
  public CompoundPrintable(List<CountingPrintable> paramList)
  {
    printables = new LinkedList(paramList);
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
    throws PrinterException
  {
    int i = 1;
    while (printables.peek() != null)
    {
      i = ((CountingPrintable)printables.peek()).print(paramGraphics, paramPageFormat, paramInt - offset);
      if (i == 0) {
        break;
      }
      offset += ((CountingPrintable)printables.poll()).getNumberOfPages();
    }
    return i;
  }
  
  public int getNumberOfPages()
  {
    return offset;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\text\CompoundPrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */