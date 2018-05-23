package sun.awt.shell;

import java.util.Comparator;
import javax.swing.SortOrder;

public class ShellFolderColumnInfo
{
  private String title;
  private Integer width;
  private boolean visible;
  private Integer alignment;
  private SortOrder sortOrder;
  private Comparator comparator;
  private boolean compareByColumn;
  
  public ShellFolderColumnInfo(String paramString, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean1, SortOrder paramSortOrder, Comparator paramComparator, boolean paramBoolean2)
  {
    title = paramString;
    width = paramInteger1;
    alignment = paramInteger2;
    visible = paramBoolean1;
    sortOrder = paramSortOrder;
    comparator = paramComparator;
    compareByColumn = paramBoolean2;
  }
  
  public ShellFolderColumnInfo(String paramString, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean, SortOrder paramSortOrder, Comparator paramComparator)
  {
    this(paramString, paramInteger1, paramInteger2, paramBoolean, paramSortOrder, paramComparator, false);
  }
  
  public ShellFolderColumnInfo(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramBoolean, null, null);
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public void setTitle(String paramString)
  {
    title = paramString;
  }
  
  public Integer getWidth()
  {
    return width;
  }
  
  public void setWidth(Integer paramInteger)
  {
    width = paramInteger;
  }
  
  public Integer getAlignment()
  {
    return alignment;
  }
  
  public void setAlignment(Integer paramInteger)
  {
    alignment = paramInteger;
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    visible = paramBoolean;
  }
  
  public SortOrder getSortOrder()
  {
    return sortOrder;
  }
  
  public void setSortOrder(SortOrder paramSortOrder)
  {
    sortOrder = paramSortOrder;
  }
  
  public Comparator getComparator()
  {
    return comparator;
  }
  
  public void setComparator(Comparator paramComparator)
  {
    comparator = paramComparator;
  }
  
  public boolean isCompareByColumn()
  {
    return compareByColumn;
  }
  
  public void setCompareByColumn(boolean paramBoolean)
  {
    compareByColumn = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\shell\ShellFolderColumnInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */