package java.awt.print;

import java.util.Vector;

public class Book
  implements Pageable
{
  private Vector mPages = new Vector();
  
  public Book() {}
  
  public int getNumberOfPages()
  {
    return mPages.size();
  }
  
  public PageFormat getPageFormat(int paramInt)
    throws IndexOutOfBoundsException
  {
    return getPage(paramInt).getPageFormat();
  }
  
  public Printable getPrintable(int paramInt)
    throws IndexOutOfBoundsException
  {
    return getPage(paramInt).getPrintable();
  }
  
  public void setPage(int paramInt, Printable paramPrintable, PageFormat paramPageFormat)
    throws IndexOutOfBoundsException
  {
    if (paramPrintable == null) {
      throw new NullPointerException("painter is null");
    }
    if (paramPageFormat == null) {
      throw new NullPointerException("page is null");
    }
    mPages.setElementAt(new BookPage(paramPrintable, paramPageFormat), paramInt);
  }
  
  public void append(Printable paramPrintable, PageFormat paramPageFormat)
  {
    mPages.addElement(new BookPage(paramPrintable, paramPageFormat));
  }
  
  public void append(Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
  {
    BookPage localBookPage = new BookPage(paramPrintable, paramPageFormat);
    int i = mPages.size();
    int j = i + paramInt;
    mPages.setSize(j);
    for (int k = i; k < j; k++) {
      mPages.setElementAt(localBookPage, k);
    }
  }
  
  private BookPage getPage(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    return (BookPage)mPages.elementAt(paramInt);
  }
  
  private class BookPage
  {
    private PageFormat mFormat;
    private Printable mPainter;
    
    BookPage(Printable paramPrintable, PageFormat paramPageFormat)
    {
      if ((paramPrintable == null) || (paramPageFormat == null)) {
        throw new NullPointerException();
      }
      mFormat = paramPageFormat;
      mPainter = paramPrintable;
    }
    
    Printable getPrintable()
    {
      return mPainter;
    }
    
    PageFormat getPageFormat()
    {
      return mFormat;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\Book.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */