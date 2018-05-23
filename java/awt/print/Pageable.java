package java.awt.print;

public abstract interface Pageable
{
  public static final int UNKNOWN_NUMBER_OF_PAGES = -1;
  
  public abstract int getNumberOfPages();
  
  public abstract PageFormat getPageFormat(int paramInt)
    throws IndexOutOfBoundsException;
  
  public abstract Printable getPrintable(int paramInt)
    throws IndexOutOfBoundsException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\Pageable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */