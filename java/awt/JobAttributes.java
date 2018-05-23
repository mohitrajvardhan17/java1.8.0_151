package java.awt;

public final class JobAttributes
  implements Cloneable
{
  private int copies;
  private DefaultSelectionType defaultSelection;
  private DestinationType destination;
  private DialogType dialog;
  private String fileName;
  private int fromPage;
  private int maxPage;
  private int minPage;
  private MultipleDocumentHandlingType multipleDocumentHandling;
  private int[][] pageRanges;
  private int prFirst;
  private int prLast;
  private String printer;
  private SidesType sides;
  private int toPage;
  
  public JobAttributes()
  {
    setCopiesToDefault();
    setDefaultSelection(DefaultSelectionType.ALL);
    setDestination(DestinationType.PRINTER);
    setDialog(DialogType.NATIVE);
    setMaxPage(Integer.MAX_VALUE);
    setMinPage(1);
    setMultipleDocumentHandlingToDefault();
    setSidesToDefault();
  }
  
  public JobAttributes(JobAttributes paramJobAttributes)
  {
    set(paramJobAttributes);
  }
  
  public JobAttributes(int paramInt1, DefaultSelectionType paramDefaultSelectionType, DestinationType paramDestinationType, DialogType paramDialogType, String paramString1, int paramInt2, int paramInt3, MultipleDocumentHandlingType paramMultipleDocumentHandlingType, int[][] paramArrayOfInt, String paramString2, SidesType paramSidesType)
  {
    setCopies(paramInt1);
    setDefaultSelection(paramDefaultSelectionType);
    setDestination(paramDestinationType);
    setDialog(paramDialogType);
    setFileName(paramString1);
    setMaxPage(paramInt2);
    setMinPage(paramInt3);
    setMultipleDocumentHandling(paramMultipleDocumentHandlingType);
    setPageRanges(paramArrayOfInt);
    setPrinter(paramString2);
    setSides(paramSidesType);
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public void set(JobAttributes paramJobAttributes)
  {
    copies = copies;
    defaultSelection = defaultSelection;
    destination = destination;
    dialog = dialog;
    fileName = fileName;
    fromPage = fromPage;
    maxPage = maxPage;
    minPage = minPage;
    multipleDocumentHandling = multipleDocumentHandling;
    pageRanges = pageRanges;
    prFirst = prFirst;
    prLast = prLast;
    printer = printer;
    sides = sides;
    toPage = toPage;
  }
  
  public int getCopies()
  {
    return copies;
  }
  
  public void setCopies(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Invalid value for attribute copies");
    }
    copies = paramInt;
  }
  
  public void setCopiesToDefault()
  {
    setCopies(1);
  }
  
  public DefaultSelectionType getDefaultSelection()
  {
    return defaultSelection;
  }
  
  public void setDefaultSelection(DefaultSelectionType paramDefaultSelectionType)
  {
    if (paramDefaultSelectionType == null) {
      throw new IllegalArgumentException("Invalid value for attribute defaultSelection");
    }
    defaultSelection = paramDefaultSelectionType;
  }
  
  public DestinationType getDestination()
  {
    return destination;
  }
  
  public void setDestination(DestinationType paramDestinationType)
  {
    if (paramDestinationType == null) {
      throw new IllegalArgumentException("Invalid value for attribute destination");
    }
    destination = paramDestinationType;
  }
  
  public DialogType getDialog()
  {
    return dialog;
  }
  
  public void setDialog(DialogType paramDialogType)
  {
    if (paramDialogType == null) {
      throw new IllegalArgumentException("Invalid value for attribute dialog");
    }
    dialog = paramDialogType;
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public void setFileName(String paramString)
  {
    fileName = paramString;
  }
  
  public int getFromPage()
  {
    if (fromPage != 0) {
      return fromPage;
    }
    if (toPage != 0) {
      return getMinPage();
    }
    if (pageRanges != null) {
      return prFirst;
    }
    return getMinPage();
  }
  
  public void setFromPage(int paramInt)
  {
    if ((paramInt <= 0) || ((toPage != 0) && (paramInt > toPage)) || (paramInt < minPage) || (paramInt > maxPage)) {
      throw new IllegalArgumentException("Invalid value for attribute fromPage");
    }
    fromPage = paramInt;
  }
  
  public int getMaxPage()
  {
    return maxPage;
  }
  
  public void setMaxPage(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt < minPage)) {
      throw new IllegalArgumentException("Invalid value for attribute maxPage");
    }
    maxPage = paramInt;
  }
  
  public int getMinPage()
  {
    return minPage;
  }
  
  public void setMinPage(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > maxPage)) {
      throw new IllegalArgumentException("Invalid value for attribute minPage");
    }
    minPage = paramInt;
  }
  
  public MultipleDocumentHandlingType getMultipleDocumentHandling()
  {
    return multipleDocumentHandling;
  }
  
  public void setMultipleDocumentHandling(MultipleDocumentHandlingType paramMultipleDocumentHandlingType)
  {
    if (paramMultipleDocumentHandlingType == null) {
      throw new IllegalArgumentException("Invalid value for attribute multipleDocumentHandling");
    }
    multipleDocumentHandling = paramMultipleDocumentHandlingType;
  }
  
  public void setMultipleDocumentHandlingToDefault()
  {
    setMultipleDocumentHandling(MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
  }
  
  public int[][] getPageRanges()
  {
    int j;
    if (pageRanges != null)
    {
      int[][] arrayOfInt = new int[pageRanges.length][2];
      for (j = 0; j < pageRanges.length; j++)
      {
        arrayOfInt[j][0] = pageRanges[j][0];
        arrayOfInt[j][1] = pageRanges[j][1];
      }
      return arrayOfInt;
    }
    if ((fromPage != 0) || (toPage != 0))
    {
      i = getFromPage();
      j = getToPage();
      return new int[][] { { i, j } };
    }
    int i = getMinPage();
    return new int[][] { { i, i } };
  }
  
  public void setPageRanges(int[][] paramArrayOfInt)
  {
    String str = "Invalid value for attribute pageRanges";
    int i = 0;
    int j = 0;
    if (paramArrayOfInt == null) {
      throw new IllegalArgumentException(str);
    }
    for (int k = 0; k < paramArrayOfInt.length; k++)
    {
      if ((paramArrayOfInt[k] == null) || (paramArrayOfInt[k].length != 2) || (paramArrayOfInt[k][0] <= j) || (paramArrayOfInt[k][1] < paramArrayOfInt[k][0])) {
        throw new IllegalArgumentException(str);
      }
      j = paramArrayOfInt[k][1];
      if (i == 0) {
        i = paramArrayOfInt[k][0];
      }
    }
    if ((i < minPage) || (j > maxPage)) {
      throw new IllegalArgumentException(str);
    }
    int[][] arrayOfInt = new int[paramArrayOfInt.length][2];
    for (int m = 0; m < paramArrayOfInt.length; m++)
    {
      arrayOfInt[m][0] = paramArrayOfInt[m][0];
      arrayOfInt[m][1] = paramArrayOfInt[m][1];
    }
    pageRanges = arrayOfInt;
    prFirst = i;
    prLast = j;
  }
  
  public String getPrinter()
  {
    return printer;
  }
  
  public void setPrinter(String paramString)
  {
    printer = paramString;
  }
  
  public SidesType getSides()
  {
    return sides;
  }
  
  public void setSides(SidesType paramSidesType)
  {
    if (paramSidesType == null) {
      throw new IllegalArgumentException("Invalid value for attribute sides");
    }
    sides = paramSidesType;
  }
  
  public void setSidesToDefault()
  {
    setSides(SidesType.ONE_SIDED);
  }
  
  public int getToPage()
  {
    if (toPage != 0) {
      return toPage;
    }
    if (fromPage != 0) {
      return fromPage;
    }
    if (pageRanges != null) {
      return prLast;
    }
    return getMinPage();
  }
  
  public void setToPage(int paramInt)
  {
    if ((paramInt <= 0) || ((fromPage != 0) && (paramInt < fromPage)) || (paramInt < minPage) || (paramInt > maxPage)) {
      throw new IllegalArgumentException("Invalid value for attribute toPage");
    }
    toPage = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof JobAttributes)) {
      return false;
    }
    JobAttributes localJobAttributes = (JobAttributes)paramObject;
    if (fileName == null)
    {
      if (fileName != null) {
        return false;
      }
    }
    else if (!fileName.equals(fileName)) {
      return false;
    }
    if (pageRanges == null)
    {
      if (pageRanges != null) {
        return false;
      }
    }
    else
    {
      if ((pageRanges == null) || (pageRanges.length != pageRanges.length)) {
        return false;
      }
      for (int i = 0; i < pageRanges.length; i++) {
        if ((pageRanges[i][0] != pageRanges[i][0]) || (pageRanges[i][1] != pageRanges[i][1])) {
          return false;
        }
      }
    }
    if (printer == null)
    {
      if (printer != null) {
        return false;
      }
    }
    else if (!printer.equals(printer)) {
      return false;
    }
    return (copies == copies) && (defaultSelection == defaultSelection) && (destination == destination) && (dialog == dialog) && (fromPage == fromPage) && (maxPage == maxPage) && (minPage == minPage) && (multipleDocumentHandling == multipleDocumentHandling) && (prFirst == prFirst) && (prLast == prLast) && (sides == sides) && (toPage == toPage);
  }
  
  public int hashCode()
  {
    int i = (copies + fromPage + maxPage + minPage + prFirst + prLast + toPage) * 31 << 21;
    if (pageRanges != null)
    {
      int j = 0;
      for (int k = 0; k < pageRanges.length; k++) {
        j += pageRanges[k][0] + pageRanges[k][1];
      }
      i ^= j * 31 << 11;
    }
    if (fileName != null) {
      i ^= fileName.hashCode();
    }
    if (printer != null) {
      i ^= printer.hashCode();
    }
    return defaultSelection.hashCode() << 6 ^ destination.hashCode() << 5 ^ dialog.hashCode() << 3 ^ multipleDocumentHandling.hashCode() << 2 ^ sides.hashCode() ^ i;
  }
  
  public String toString()
  {
    int[][] arrayOfInt = getPageRanges();
    String str = "[";
    int i = 1;
    for (int j = 0; j < arrayOfInt.length; j++)
    {
      if (i != 0) {
        i = 0;
      } else {
        str = str + ",";
      }
      str = str + arrayOfInt[j][0] + ":" + arrayOfInt[j][1];
    }
    str = str + "]";
    return "copies=" + getCopies() + ",defaultSelection=" + getDefaultSelection() + ",destination=" + getDestination() + ",dialog=" + getDialog() + ",fileName=" + getFileName() + ",fromPage=" + getFromPage() + ",maxPage=" + getMaxPage() + ",minPage=" + getMinPage() + ",multiple-document-handling=" + getMultipleDocumentHandling() + ",page-ranges=" + str + ",printer=" + getPrinter() + ",sides=" + getSides() + ",toPage=" + getToPage();
  }
  
  public static final class DefaultSelectionType
    extends AttributeValue
  {
    private static final int I_ALL = 0;
    private static final int I_RANGE = 1;
    private static final int I_SELECTION = 2;
    private static final String[] NAMES = { "all", "range", "selection" };
    public static final DefaultSelectionType ALL = new DefaultSelectionType(0);
    public static final DefaultSelectionType RANGE = new DefaultSelectionType(1);
    public static final DefaultSelectionType SELECTION = new DefaultSelectionType(2);
    
    private DefaultSelectionType(int paramInt)
    {
      super(NAMES);
    }
  }
  
  public static final class DestinationType
    extends AttributeValue
  {
    private static final int I_FILE = 0;
    private static final int I_PRINTER = 1;
    private static final String[] NAMES = { "file", "printer" };
    public static final DestinationType FILE = new DestinationType(0);
    public static final DestinationType PRINTER = new DestinationType(1);
    
    private DestinationType(int paramInt)
    {
      super(NAMES);
    }
  }
  
  public static final class DialogType
    extends AttributeValue
  {
    private static final int I_COMMON = 0;
    private static final int I_NATIVE = 1;
    private static final int I_NONE = 2;
    private static final String[] NAMES = { "common", "native", "none" };
    public static final DialogType COMMON = new DialogType(0);
    public static final DialogType NATIVE = new DialogType(1);
    public static final DialogType NONE = new DialogType(2);
    
    private DialogType(int paramInt)
    {
      super(NAMES);
    }
  }
  
  public static final class MultipleDocumentHandlingType
    extends AttributeValue
  {
    private static final int I_SEPARATE_DOCUMENTS_COLLATED_COPIES = 0;
    private static final int I_SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = 1;
    private static final String[] NAMES = { "separate-documents-collated-copies", "separate-documents-uncollated-copies" };
    public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandlingType(0);
    public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandlingType(1);
    
    private MultipleDocumentHandlingType(int paramInt)
    {
      super(NAMES);
    }
  }
  
  public static final class SidesType
    extends AttributeValue
  {
    private static final int I_ONE_SIDED = 0;
    private static final int I_TWO_SIDED_LONG_EDGE = 1;
    private static final int I_TWO_SIDED_SHORT_EDGE = 2;
    private static final String[] NAMES = { "one-sided", "two-sided-long-edge", "two-sided-short-edge" };
    public static final SidesType ONE_SIDED = new SidesType(0);
    public static final SidesType TWO_SIDED_LONG_EDGE = new SidesType(1);
    public static final SidesType TWO_SIDED_SHORT_EDGE = new SidesType(2);
    
    private SidesType(int paramInt)
    {
      super(NAMES);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\JobAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */