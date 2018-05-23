package javax.swing.text;

import java.util.Vector;

public class PlainDocument
  extends AbstractDocument
{
  public static final String tabSizeAttribute = "tabSize";
  public static final String lineLimitAttribute = "lineLimit";
  private AbstractDocument.AbstractElement defaultRoot;
  private Vector<Element> added = new Vector();
  private Vector<Element> removed = new Vector();
  private transient Segment s;
  
  public PlainDocument()
  {
    this(new GapContent());
  }
  
  public PlainDocument(AbstractDocument.Content paramContent)
  {
    super(paramContent);
    putProperty("tabSize", Integer.valueOf(8));
    defaultRoot = createDefaultRoot();
  }
  
  public void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    Object localObject = getProperty("filterNewlines");
    if (((localObject instanceof Boolean)) && (localObject.equals(Boolean.TRUE)) && (paramString != null) && (paramString.indexOf('\n') >= 0))
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString);
      int i = localStringBuilder.length();
      for (int j = 0; j < i; j++) {
        if (localStringBuilder.charAt(j) == '\n') {
          localStringBuilder.setCharAt(j, ' ');
        }
      }
      paramString = localStringBuilder.toString();
    }
    super.insertString(paramInt, paramString, paramAttributeSet);
  }
  
  public Element getDefaultRootElement()
  {
    return defaultRoot;
  }
  
  protected AbstractDocument.AbstractElement createDefaultRoot()
  {
    AbstractDocument.BranchElement localBranchElement = (AbstractDocument.BranchElement)createBranchElement(null, null);
    Element localElement = createLeafElement(localBranchElement, null, 0, 1);
    Element[] arrayOfElement = new Element[1];
    arrayOfElement[0] = localElement;
    localBranchElement.replace(0, 0, arrayOfElement);
    return localBranchElement;
  }
  
  public Element getParagraphElement(int paramInt)
  {
    Element localElement = getDefaultRootElement();
    return localElement.getElement(localElement.getElementIndex(paramInt));
  }
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet)
  {
    removed.removeAllElements();
    added.removeAllElements();
    AbstractDocument.BranchElement localBranchElement = (AbstractDocument.BranchElement)getDefaultRootElement();
    int i = paramDefaultDocumentEvent.getOffset();
    int j = paramDefaultDocumentEvent.getLength();
    if (i > 0)
    {
      i--;
      j++;
    }
    int k = localBranchElement.getElementIndex(i);
    Element localElement = localBranchElement.getElement(k);
    int m = localElement.getStartOffset();
    int n = localElement.getEndOffset();
    int i1 = m;
    try
    {
      if (s == null) {
        s = new Segment();
      }
      getContent().getChars(i, j, s);
      int i2 = 0;
      for (int i3 = 0; i3 < j; i3++)
      {
        int i4 = s.array[(s.offset + i3)];
        if (i4 == 10)
        {
          int i5 = i + i3 + 1;
          added.addElement(createLeafElement(localBranchElement, null, i1, i5));
          i1 = i5;
          i2 = 1;
        }
      }
      if (i2 != 0)
      {
        removed.addElement(localElement);
        if ((i + j == n) && (i1 != n) && (k + 1 < localBranchElement.getElementCount()))
        {
          localObject = localBranchElement.getElement(k + 1);
          removed.addElement(localObject);
          n = ((Element)localObject).getEndOffset();
        }
        if (i1 < n) {
          added.addElement(createLeafElement(localBranchElement, null, i1, n));
        }
        Object localObject = new Element[added.size()];
        added.copyInto((Object[])localObject);
        Element[] arrayOfElement = new Element[removed.size()];
        removed.copyInto(arrayOfElement);
        AbstractDocument.ElementEdit localElementEdit = new AbstractDocument.ElementEdit(localBranchElement, k, arrayOfElement, (Element[])localObject);
        paramDefaultDocumentEvent.addEdit(localElementEdit);
        localBranchElement.replace(k, arrayOfElement.length, (Element[])localObject);
      }
      if (Utilities.isComposedTextAttributeDefined(paramAttributeSet)) {
        insertComposedTextUpdate(paramDefaultDocumentEvent, paramAttributeSet);
      }
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new Error("Internal error: " + localBadLocationException.toString());
    }
    super.insertUpdate(paramDefaultDocumentEvent, paramAttributeSet);
  }
  
  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent)
  {
    removed.removeAllElements();
    AbstractDocument.BranchElement localBranchElement = (AbstractDocument.BranchElement)getDefaultRootElement();
    int i = paramDefaultDocumentEvent.getOffset();
    int j = paramDefaultDocumentEvent.getLength();
    int k = localBranchElement.getElementIndex(i);
    int m = localBranchElement.getElementIndex(i + j);
    Element[] arrayOfElement1;
    Element[] arrayOfElement2;
    AbstractDocument.ElementEdit localElementEdit;
    if (k != m)
    {
      for (int n = k; n <= m; n++) {
        removed.addElement(localBranchElement.getElement(n));
      }
      n = localBranchElement.getElement(k).getStartOffset();
      int i1 = localBranchElement.getElement(m).getEndOffset();
      arrayOfElement1 = new Element[1];
      arrayOfElement1[0] = createLeafElement(localBranchElement, null, n, i1);
      arrayOfElement2 = new Element[removed.size()];
      removed.copyInto(arrayOfElement2);
      localElementEdit = new AbstractDocument.ElementEdit(localBranchElement, k, arrayOfElement2, arrayOfElement1);
      paramDefaultDocumentEvent.addEdit(localElementEdit);
      localBranchElement.replace(k, arrayOfElement2.length, arrayOfElement1);
    }
    else
    {
      Element localElement1 = localBranchElement.getElement(k);
      if (!localElement1.isLeaf())
      {
        Element localElement2 = localElement1.getElement(localElement1.getElementIndex(i));
        if (Utilities.isComposedTextElement(localElement2))
        {
          arrayOfElement1 = new Element[1];
          arrayOfElement1[0] = createLeafElement(localBranchElement, null, localElement1.getStartOffset(), localElement1.getEndOffset());
          arrayOfElement2 = new Element[1];
          arrayOfElement2[0] = localElement1;
          localElementEdit = new AbstractDocument.ElementEdit(localBranchElement, k, arrayOfElement2, arrayOfElement1);
          paramDefaultDocumentEvent.addEdit(localElementEdit);
          localBranchElement.replace(k, 1, arrayOfElement1);
        }
      }
    }
    super.removeUpdate(paramDefaultDocumentEvent);
  }
  
  private void insertComposedTextUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet)
  {
    added.removeAllElements();
    AbstractDocument.BranchElement localBranchElement = (AbstractDocument.BranchElement)getDefaultRootElement();
    int i = paramDefaultDocumentEvent.getOffset();
    int j = paramDefaultDocumentEvent.getLength();
    int k = localBranchElement.getElementIndex(i);
    Element localElement = localBranchElement.getElement(k);
    int m = localElement.getStartOffset();
    int n = localElement.getEndOffset();
    AbstractDocument.BranchElement[] arrayOfBranchElement = new AbstractDocument.BranchElement[1];
    arrayOfBranchElement[0] = ((AbstractDocument.BranchElement)createBranchElement(localBranchElement, null));
    Element[] arrayOfElement1 = new Element[1];
    arrayOfElement1[0] = localElement;
    if (m != i) {
      added.addElement(createLeafElement(arrayOfBranchElement[0], null, m, i));
    }
    added.addElement(createLeafElement(arrayOfBranchElement[0], paramAttributeSet, i, i + j));
    if (n != i + j) {
      added.addElement(createLeafElement(arrayOfBranchElement[0], null, i + j, n));
    }
    Element[] arrayOfElement2 = new Element[added.size()];
    added.copyInto(arrayOfElement2);
    AbstractDocument.ElementEdit localElementEdit = new AbstractDocument.ElementEdit(localBranchElement, k, arrayOfElement1, arrayOfBranchElement);
    paramDefaultDocumentEvent.addEdit(localElementEdit);
    arrayOfBranchElement[0].replace(0, 0, arrayOfElement2);
    localBranchElement.replace(k, 1, arrayOfBranchElement);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\PlainDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */