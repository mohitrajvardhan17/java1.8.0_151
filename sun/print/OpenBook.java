package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

class OpenBook
  implements Pageable
{
  private PageFormat mFormat;
  private Printable mPainter;
  
  OpenBook(PageFormat paramPageFormat, Printable paramPrintable)
  {
    mFormat = paramPageFormat;
    mPainter = paramPrintable;
  }
  
  public int getNumberOfPages()
  {
    return -1;
  }
  
  public PageFormat getPageFormat(int paramInt)
  {
    return mFormat;
  }
  
  public Printable getPrintable(int paramInt)
    throws IndexOutOfBoundsException
  {
    return mPainter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\OpenBook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */