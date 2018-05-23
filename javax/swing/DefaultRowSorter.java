package javax.swing;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class DefaultRowSorter<M, I>
  extends RowSorter<M>
{
  private boolean sortsOnUpdates;
  private Row[] viewToModel;
  private int[] modelToView;
  private Comparator[] comparators;
  private boolean[] isSortable;
  private RowSorter.SortKey[] cachedSortKeys;
  private Comparator[] sortComparators;
  private RowFilter<? super M, ? super I> filter;
  private DefaultRowSorter<M, I>.FilterEntry filterEntry;
  private List<RowSorter.SortKey> sortKeys = Collections.emptyList();
  private boolean[] useToString;
  private boolean sorted;
  private int maxSortKeys = 3;
  private ModelWrapper<M, I> modelWrapper;
  private int modelRowCount;
  
  public DefaultRowSorter() {}
  
  protected final void setModelWrapper(ModelWrapper<M, I> paramModelWrapper)
  {
    if (paramModelWrapper == null) {
      throw new IllegalArgumentException("modelWrapper most be non-null");
    }
    ModelWrapper localModelWrapper = modelWrapper;
    modelWrapper = paramModelWrapper;
    if (localModelWrapper != null) {
      modelStructureChanged();
    } else {
      modelRowCount = getModelWrapper().getRowCount();
    }
  }
  
  protected final ModelWrapper<M, I> getModelWrapper()
  {
    return modelWrapper;
  }
  
  public final M getModel()
  {
    return (M)getModelWrapper().getModel();
  }
  
  public void setSortable(int paramInt, boolean paramBoolean)
  {
    checkColumn(paramInt);
    if (isSortable == null)
    {
      isSortable = new boolean[getModelWrapper().getColumnCount()];
      for (int i = isSortable.length - 1; i >= 0; i--) {
        isSortable[i] = true;
      }
    }
    isSortable[paramInt] = paramBoolean;
  }
  
  public boolean isSortable(int paramInt)
  {
    checkColumn(paramInt);
    return isSortable == null ? 1 : isSortable[paramInt];
  }
  
  public void setSortKeys(List<? extends RowSorter.SortKey> paramList)
  {
    List localList = sortKeys;
    if ((paramList != null) && (paramList.size() > 0))
    {
      int i = getModelWrapper().getColumnCount();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        RowSorter.SortKey localSortKey = (RowSorter.SortKey)localIterator.next();
        if ((localSortKey == null) || (localSortKey.getColumn() < 0) || (localSortKey.getColumn() >= i)) {
          throw new IllegalArgumentException("Invalid SortKey");
        }
      }
      sortKeys = Collections.unmodifiableList(new ArrayList(paramList));
    }
    else
    {
      sortKeys = Collections.emptyList();
    }
    if (!sortKeys.equals(localList))
    {
      fireSortOrderChanged();
      if (viewToModel == null) {
        sort();
      } else {
        sortExistingData();
      }
    }
  }
  
  public List<? extends RowSorter.SortKey> getSortKeys()
  {
    return sortKeys;
  }
  
  public void setMaxSortKeys(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Invalid max");
    }
    maxSortKeys = paramInt;
  }
  
  public int getMaxSortKeys()
  {
    return maxSortKeys;
  }
  
  public void setSortsOnUpdates(boolean paramBoolean)
  {
    sortsOnUpdates = paramBoolean;
  }
  
  public boolean getSortsOnUpdates()
  {
    return sortsOnUpdates;
  }
  
  public void setRowFilter(RowFilter<? super M, ? super I> paramRowFilter)
  {
    filter = paramRowFilter;
    sort();
  }
  
  public RowFilter<? super M, ? super I> getRowFilter()
  {
    return filter;
  }
  
  public void toggleSortOrder(int paramInt)
  {
    checkColumn(paramInt);
    if (isSortable(paramInt))
    {
      Object localObject = new ArrayList(getSortKeys());
      for (int i = ((List)localObject).size() - 1; (i >= 0) && (((RowSorter.SortKey)((List)localObject).get(i)).getColumn() != paramInt); i--) {}
      if (i == -1)
      {
        RowSorter.SortKey localSortKey = new RowSorter.SortKey(paramInt, SortOrder.ASCENDING);
        ((List)localObject).add(0, localSortKey);
      }
      else if (i == 0)
      {
        ((List)localObject).set(0, toggle((RowSorter.SortKey)((List)localObject).get(0)));
      }
      else
      {
        ((List)localObject).remove(i);
        ((List)localObject).add(0, new RowSorter.SortKey(paramInt, SortOrder.ASCENDING));
      }
      if (((List)localObject).size() > getMaxSortKeys()) {
        localObject = ((List)localObject).subList(0, getMaxSortKeys());
      }
      setSortKeys((List)localObject);
    }
  }
  
  private RowSorter.SortKey toggle(RowSorter.SortKey paramSortKey)
  {
    if (paramSortKey.getSortOrder() == SortOrder.ASCENDING) {
      return new RowSorter.SortKey(paramSortKey.getColumn(), SortOrder.DESCENDING);
    }
    return new RowSorter.SortKey(paramSortKey.getColumn(), SortOrder.ASCENDING);
  }
  
  public int convertRowIndexToView(int paramInt)
  {
    if (modelToView == null)
    {
      if ((paramInt < 0) || (paramInt >= getModelWrapper().getRowCount())) {
        throw new IndexOutOfBoundsException("Invalid index");
      }
      return paramInt;
    }
    return modelToView[paramInt];
  }
  
  public int convertRowIndexToModel(int paramInt)
  {
    if (viewToModel == null)
    {
      if ((paramInt < 0) || (paramInt >= getModelWrapper().getRowCount())) {
        throw new IndexOutOfBoundsException("Invalid index");
      }
      return paramInt;
    }
    return viewToModel[paramInt].modelIndex;
  }
  
  private boolean isUnsorted()
  {
    List localList = getSortKeys();
    int i = localList.size();
    return (i == 0) || (((RowSorter.SortKey)localList.get(0)).getSortOrder() == SortOrder.UNSORTED);
  }
  
  private void sortExistingData()
  {
    int[] arrayOfInt = getViewToModelAsInts(viewToModel);
    updateUseToString();
    cacheSortKeys(getSortKeys());
    if (isUnsorted())
    {
      if (getRowFilter() == null)
      {
        viewToModel = null;
        modelToView = null;
      }
      else
      {
        int i = 0;
        for (int j = 0; j < modelToView.length; j++) {
          if (modelToView[j] != -1)
          {
            viewToModel[i].modelIndex = j;
            modelToView[j] = (i++);
          }
        }
      }
    }
    else
    {
      Arrays.sort(viewToModel);
      setModelToViewFromViewToModel(false);
    }
    fireRowSorterChanged(arrayOfInt);
  }
  
  public void sort()
  {
    sorted = true;
    int[] arrayOfInt = getViewToModelAsInts(viewToModel);
    updateUseToString();
    if (isUnsorted())
    {
      cachedSortKeys = new RowSorter.SortKey[0];
      if (getRowFilter() == null)
      {
        if (viewToModel != null)
        {
          viewToModel = null;
          modelToView = null;
        }
      }
      else {
        initializeFilteredMapping();
      }
    }
    else
    {
      cacheSortKeys(getSortKeys());
      if (getRowFilter() != null)
      {
        initializeFilteredMapping();
      }
      else
      {
        createModelToView(getModelWrapper().getRowCount());
        createViewToModel(getModelWrapper().getRowCount());
      }
      Arrays.sort(viewToModel);
      setModelToViewFromViewToModel(false);
    }
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void updateUseToString()
  {
    int i = getModelWrapper().getColumnCount();
    if ((useToString == null) || (useToString.length != i)) {
      useToString = new boolean[i];
    }
    i--;
    while (i >= 0)
    {
      useToString[i] = useToString(i);
      i--;
    }
  }
  
  private void initializeFilteredMapping()
  {
    int i = getModelWrapper().getRowCount();
    int m = 0;
    createModelToView(i);
    for (int j = 0; j < i; j++) {
      if (include(j))
      {
        modelToView[j] = (j - m);
      }
      else
      {
        modelToView[j] = -1;
        m++;
      }
    }
    createViewToModel(i - m);
    j = 0;
    int k = 0;
    while (j < i)
    {
      if (modelToView[j] != -1) {
        viewToModel[(k++)].modelIndex = j;
      }
      j++;
    }
  }
  
  private void createModelToView(int paramInt)
  {
    if ((modelToView == null) || (modelToView.length != paramInt)) {
      modelToView = new int[paramInt];
    }
  }
  
  private void createViewToModel(int paramInt)
  {
    int i = 0;
    if (viewToModel != null)
    {
      i = Math.min(paramInt, viewToModel.length);
      if (viewToModel.length != paramInt)
      {
        Row[] arrayOfRow = viewToModel;
        viewToModel = new Row[paramInt];
        System.arraycopy(arrayOfRow, 0, viewToModel, 0, i);
      }
    }
    else
    {
      viewToModel = new Row[paramInt];
    }
    for (int j = 0; j < i; j++) {
      viewToModel[j].modelIndex = j;
    }
    for (j = i; j < paramInt; j++) {
      viewToModel[j] = new Row(this, j);
    }
  }
  
  private void cacheSortKeys(List<? extends RowSorter.SortKey> paramList)
  {
    int i = paramList.size();
    sortComparators = new Comparator[i];
    for (int j = 0; j < i; j++) {
      sortComparators[j] = getComparator0(((RowSorter.SortKey)paramList.get(j)).getColumn());
    }
    cachedSortKeys = ((RowSorter.SortKey[])paramList.toArray(new RowSorter.SortKey[i]));
  }
  
  protected boolean useToString(int paramInt)
  {
    return getComparator(paramInt) == null;
  }
  
  private void setModelToViewFromViewToModel(boolean paramBoolean)
  {
    if (paramBoolean) {
      for (i = modelToView.length - 1; i >= 0; i--) {
        modelToView[i] = -1;
      }
    }
    for (int i = viewToModel.length - 1; i >= 0; i--) {
      modelToView[viewToModel[i].modelIndex] = i;
    }
  }
  
  private int[] getViewToModelAsInts(Row[] paramArrayOfRow)
  {
    if (paramArrayOfRow != null)
    {
      int[] arrayOfInt = new int[paramArrayOfRow.length];
      for (int i = paramArrayOfRow.length - 1; i >= 0; i--) {
        arrayOfInt[i] = modelIndex;
      }
      return arrayOfInt;
    }
    return new int[0];
  }
  
  public void setComparator(int paramInt, Comparator<?> paramComparator)
  {
    checkColumn(paramInt);
    if (comparators == null) {
      comparators = new Comparator[getModelWrapper().getColumnCount()];
    }
    comparators[paramInt] = paramComparator;
  }
  
  public Comparator<?> getComparator(int paramInt)
  {
    checkColumn(paramInt);
    if (comparators != null) {
      return comparators[paramInt];
    }
    return null;
  }
  
  private Comparator getComparator0(int paramInt)
  {
    Comparator localComparator = getComparator(paramInt);
    if (localComparator != null) {
      return localComparator;
    }
    return Collator.getInstance();
  }
  
  private RowFilter.Entry<M, I> getFilterEntry(int paramInt)
  {
    if (filterEntry == null) {
      filterEntry = new FilterEntry(null);
    }
    filterEntry.modelIndex = paramInt;
    return filterEntry;
  }
  
  public int getViewRowCount()
  {
    if (viewToModel != null) {
      return viewToModel.length;
    }
    return getModelWrapper().getRowCount();
  }
  
  public int getModelRowCount()
  {
    return getModelWrapper().getRowCount();
  }
  
  private void allChanged()
  {
    modelToView = null;
    viewToModel = null;
    comparators = null;
    isSortable = null;
    if (isUnsorted()) {
      sort();
    } else {
      setSortKeys(null);
    }
  }
  
  public void modelStructureChanged()
  {
    allChanged();
    modelRowCount = getModelWrapper().getRowCount();
  }
  
  public void allRowsChanged()
  {
    modelRowCount = getModelWrapper().getRowCount();
    sort();
  }
  
  public void rowsInserted(int paramInt1, int paramInt2)
  {
    checkAgainstModel(paramInt1, paramInt2);
    int i = getModelWrapper().getRowCount();
    if (paramInt2 >= i) {
      throw new IndexOutOfBoundsException("Invalid range");
    }
    modelRowCount = i;
    if (shouldOptimizeChange(paramInt1, paramInt2)) {
      rowsInserted0(paramInt1, paramInt2);
    }
  }
  
  public void rowsDeleted(int paramInt1, int paramInt2)
  {
    checkAgainstModel(paramInt1, paramInt2);
    if ((paramInt1 >= modelRowCount) || (paramInt2 >= modelRowCount)) {
      throw new IndexOutOfBoundsException("Invalid range");
    }
    modelRowCount = getModelWrapper().getRowCount();
    if (shouldOptimizeChange(paramInt1, paramInt2)) {
      rowsDeleted0(paramInt1, paramInt2);
    }
  }
  
  public void rowsUpdated(int paramInt1, int paramInt2)
  {
    checkAgainstModel(paramInt1, paramInt2);
    if ((paramInt1 >= modelRowCount) || (paramInt2 >= modelRowCount)) {
      throw new IndexOutOfBoundsException("Invalid range");
    }
    if (getSortsOnUpdates())
    {
      if (shouldOptimizeChange(paramInt1, paramInt2)) {
        rowsUpdated0(paramInt1, paramInt2);
      }
    }
    else {
      sorted = false;
    }
  }
  
  public void rowsUpdated(int paramInt1, int paramInt2, int paramInt3)
  {
    checkColumn(paramInt3);
    rowsUpdated(paramInt1, paramInt2);
  }
  
  private void checkAgainstModel(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > paramInt2) || (paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > modelRowCount)) {
      throw new IndexOutOfBoundsException("Invalid range");
    }
  }
  
  private boolean include(int paramInt)
  {
    RowFilter localRowFilter = getRowFilter();
    if (localRowFilter != null) {
      return localRowFilter.include(getFilterEntry(paramInt));
    }
    return true;
  }
  
  private int compare(int paramInt1, int paramInt2)
  {
    for (int k = 0; k < cachedSortKeys.length; k++)
    {
      int i = cachedSortKeys[k].getColumn();
      SortOrder localSortOrder = cachedSortKeys[k].getSortOrder();
      int j;
      if (localSortOrder == SortOrder.UNSORTED)
      {
        j = paramInt1 - paramInt2;
      }
      else
      {
        Object localObject1;
        Object localObject2;
        if (useToString[i] != 0)
        {
          localObject1 = getModelWrapper().getStringValueAt(paramInt1, i);
          localObject2 = getModelWrapper().getStringValueAt(paramInt2, i);
        }
        else
        {
          localObject1 = getModelWrapper().getValueAt(paramInt1, i);
          localObject2 = getModelWrapper().getValueAt(paramInt2, i);
        }
        if (localObject1 == null)
        {
          if (localObject2 == null) {
            j = 0;
          } else {
            j = -1;
          }
        }
        else if (localObject2 == null) {
          j = 1;
        } else {
          j = sortComparators[k].compare(localObject1, localObject2);
        }
        if (localSortOrder == SortOrder.DESCENDING) {
          j *= -1;
        }
      }
      if (j != 0) {
        return j;
      }
    }
    return paramInt1 - paramInt2;
  }
  
  private boolean isTransformed()
  {
    return viewToModel != null;
  }
  
  private void insertInOrder(List<Row> paramList, Row[] paramArrayOfRow)
  {
    int i = 0;
    int k = paramList.size();
    for (int m = 0; m < k; m++)
    {
      int j = Arrays.binarySearch(paramArrayOfRow, paramList.get(m));
      if (j < 0) {
        j = -1 - j;
      }
      System.arraycopy(paramArrayOfRow, i, viewToModel, i + m, j - i);
      viewToModel[(j + m)] = ((Row)paramList.get(m));
      i = j;
    }
    System.arraycopy(paramArrayOfRow, i, viewToModel, i + k, paramArrayOfRow.length - i);
  }
  
  private boolean shouldOptimizeChange(int paramInt1, int paramInt2)
  {
    if (!isTransformed()) {
      return false;
    }
    if ((!sorted) || (paramInt2 - paramInt1 > viewToModel.length / 10))
    {
      sort();
      return false;
    }
    return true;
  }
  
  private void rowsInserted0(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getViewToModelAsInts(viewToModel);
    int j = paramInt2 - paramInt1 + 1;
    ArrayList localArrayList = new ArrayList(j);
    for (int i = paramInt1; i <= paramInt2; i++) {
      if (include(i)) {
        localArrayList.add(new Row(this, i));
      }
    }
    for (i = modelToView.length - 1; i >= paramInt1; i--)
    {
      int k = modelToView[i];
      if (k != -1) {
        viewToModel[k].modelIndex += j;
      }
    }
    if (localArrayList.size() > 0)
    {
      Collections.sort(localArrayList);
      Row[] arrayOfRow = viewToModel;
      viewToModel = new Row[viewToModel.length + localArrayList.size()];
      insertInOrder(localArrayList, arrayOfRow);
    }
    createModelToView(getModelWrapper().getRowCount());
    setModelToViewFromViewToModel(true);
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void rowsDeleted0(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getViewToModelAsInts(viewToModel);
    int i = 0;
    int k;
    for (int j = paramInt1; j <= paramInt2; j++)
    {
      k = modelToView[j];
      if (k != -1)
      {
        i++;
        viewToModel[k] = null;
      }
    }
    int m = paramInt2 - paramInt1 + 1;
    for (j = modelToView.length - 1; j > paramInt2; j--)
    {
      k = modelToView[j];
      if (k != -1) {
        viewToModel[k].modelIndex -= m;
      }
    }
    if (i > 0)
    {
      Row[] arrayOfRow = new Row[viewToModel.length - i];
      int n = 0;
      int i1 = 0;
      for (j = 0; j < viewToModel.length; j++) {
        if (viewToModel[j] == null)
        {
          System.arraycopy(viewToModel, i1, arrayOfRow, n, j - i1);
          n += j - i1;
          i1 = j + 1;
        }
      }
      System.arraycopy(viewToModel, i1, arrayOfRow, n, viewToModel.length - i1);
      viewToModel = arrayOfRow;
    }
    createModelToView(getModelWrapper().getRowCount());
    setModelToViewFromViewToModel(true);
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void rowsUpdated0(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getViewToModelAsInts(viewToModel);
    int k = paramInt2 - paramInt1 + 1;
    Object localObject;
    int j;
    int i;
    int m;
    if (getRowFilter() == null)
    {
      localObject = new Row[k];
      j = 0;
      i = paramInt1;
      while (i <= paramInt2)
      {
        localObject[j] = viewToModel[modelToView[i]];
        i++;
        j++;
      }
      Arrays.sort((Object[])localObject);
      Row[] arrayOfRow1 = new Row[viewToModel.length - k];
      i = 0;
      j = 0;
      while (i < viewToModel.length)
      {
        m = viewToModel[i].modelIndex;
        if ((m < paramInt1) || (m > paramInt2)) {
          arrayOfRow1[(j++)] = viewToModel[i];
        }
        i++;
      }
      insertInOrder(Arrays.asList((Object[])localObject), arrayOfRow1);
      setModelToViewFromViewToModel(false);
    }
    else
    {
      localObject = new ArrayList(k);
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      for (i = paramInt1; i <= paramInt2; i++) {
        if (modelToView[i] == -1)
        {
          if (include(i))
          {
            ((List)localObject).add(new Row(this, i));
            n++;
          }
        }
        else
        {
          if (!include(i)) {
            i1++;
          } else {
            ((List)localObject).add(viewToModel[modelToView[i]]);
          }
          modelToView[i] = -2;
          i2++;
        }
      }
      Collections.sort((List)localObject);
      Row[] arrayOfRow2 = new Row[viewToModel.length - i2];
      i = 0;
      j = 0;
      while (i < viewToModel.length)
      {
        m = viewToModel[i].modelIndex;
        if (modelToView[m] != -2) {
          arrayOfRow2[(j++)] = viewToModel[i];
        }
        i++;
      }
      if (n != i1) {
        viewToModel = new Row[viewToModel.length + n - i1];
      }
      insertInOrder((List)localObject, arrayOfRow2);
      setModelToViewFromViewToModel(true);
    }
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void checkColumn(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getModelWrapper().getColumnCount())) {
      throw new IndexOutOfBoundsException("column beyond range of TableModel");
    }
  }
  
  private class FilterEntry
    extends RowFilter.Entry<M, I>
  {
    int modelIndex;
    
    private FilterEntry() {}
    
    public M getModel()
    {
      return (M)getModelWrapper().getModel();
    }
    
    public int getValueCount()
    {
      return getModelWrapper().getColumnCount();
    }
    
    public Object getValue(int paramInt)
    {
      return getModelWrapper().getValueAt(modelIndex, paramInt);
    }
    
    public String getStringValue(int paramInt)
    {
      return getModelWrapper().getStringValueAt(modelIndex, paramInt);
    }
    
    public I getIdentifier()
    {
      return (I)getModelWrapper().getIdentifier(modelIndex);
    }
  }
  
  protected static abstract class ModelWrapper<M, I>
  {
    protected ModelWrapper() {}
    
    public abstract M getModel();
    
    public abstract int getColumnCount();
    
    public abstract int getRowCount();
    
    public abstract Object getValueAt(int paramInt1, int paramInt2);
    
    public String getStringValueAt(int paramInt1, int paramInt2)
    {
      Object localObject = getValueAt(paramInt1, paramInt2);
      if (localObject == null) {
        return "";
      }
      String str = localObject.toString();
      if (str == null) {
        return "";
      }
      return str;
    }
    
    public abstract I getIdentifier(int paramInt);
  }
  
  private static class Row
    implements Comparable<Row>
  {
    private DefaultRowSorter sorter;
    int modelIndex;
    
    public Row(DefaultRowSorter paramDefaultRowSorter, int paramInt)
    {
      sorter = paramDefaultRowSorter;
      modelIndex = paramInt;
    }
    
    public int compareTo(Row paramRow)
    {
      return sorter.compare(modelIndex, modelIndex);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultRowSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */