package javax.swing.text.html;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument.AbstractElement;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.AbstractDocument.Content;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.AbstractDocument.ElementEdit;
import javax.swing.text.AbstractDocument.LeafElement;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DefaultStyledDocument.AttributeUndoableEdit;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoableEdit;
import sun.swing.SwingUtilities2;

public class HTMLDocument
  extends DefaultStyledDocument
{
  private boolean frameDocument = false;
  private boolean preservesUnknownTags = true;
  private HashMap<String, ButtonGroup> radioButtonGroupsMap;
  static final String TokenThreshold = "token threshold";
  private static final int MaxThreshold = 10000;
  private static final int StepThreshold = 5;
  public static final String AdditionalComments = "AdditionalComments";
  static final String StyleType = "StyleType";
  URL base;
  boolean hasBaseTag = false;
  private String baseTarget = null;
  private HTMLEditorKit.Parser parser;
  private static AttributeSet contentAttributeSet;
  static String MAP_PROPERTY = "__MAP__";
  private static char[] NEWLINE;
  private boolean insertInBody = false;
  private static final String I18NProperty = "i18n";
  
  public HTMLDocument()
  {
    this(new GapContent(4096), new StyleSheet());
  }
  
  public HTMLDocument(StyleSheet paramStyleSheet)
  {
    this(new GapContent(4096), paramStyleSheet);
  }
  
  public HTMLDocument(AbstractDocument.Content paramContent, StyleSheet paramStyleSheet)
  {
    super(paramContent, paramStyleSheet);
  }
  
  public HTMLEditorKit.ParserCallback getReader(int paramInt)
  {
    Object localObject = getProperty("stream");
    if ((localObject instanceof URL)) {
      setBase((URL)localObject);
    }
    HTMLReader localHTMLReader = new HTMLReader(paramInt);
    return localHTMLReader;
  }
  
  public HTMLEditorKit.ParserCallback getReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag)
  {
    return getReader(paramInt1, paramInt2, paramInt3, paramTag, true);
  }
  
  HTMLEditorKit.ParserCallback getReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag, boolean paramBoolean)
  {
    Object localObject = getProperty("stream");
    if ((localObject instanceof URL)) {
      setBase((URL)localObject);
    }
    HTMLReader localHTMLReader = new HTMLReader(paramInt1, paramInt2, paramInt3, paramTag, paramBoolean, false, true);
    return localHTMLReader;
  }
  
  public URL getBase()
  {
    return base;
  }
  
  public void setBase(URL paramURL)
  {
    base = paramURL;
    getStyleSheet().setBase(paramURL);
  }
  
  protected void insert(int paramInt, DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec)
    throws BadLocationException
  {
    super.insert(paramInt, paramArrayOfElementSpec);
  }
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      paramAttributeSet = contentAttributeSet;
    } else if (paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)) {
      ((MutableAttributeSet)paramAttributeSet).addAttributes(contentAttributeSet);
    }
    if (paramAttributeSet.isDefined("CR")) {
      ((MutableAttributeSet)paramAttributeSet).removeAttribute("CR");
    }
    super.insertUpdate(paramDefaultDocumentEvent, paramAttributeSet);
  }
  
  protected void create(DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec)
  {
    super.create(paramArrayOfElementSpec);
  }
  
  public void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    try
    {
      writeLock();
      int i = Math.min(paramInt1 + paramInt2, getLength());
      Element localElement1 = getParagraphElement(paramInt1);
      paramInt1 = localElement1.getStartOffset();
      localElement1 = getParagraphElement(i);
      paramInt2 = Math.max(0, localElement1.getEndOffset() - paramInt1);
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      AttributeSet localAttributeSet = paramAttributeSet.copyAttributes();
      int j = Integer.MAX_VALUE;
      for (int k = paramInt1; k <= i; k = j)
      {
        Element localElement2 = getParagraphElement(k);
        if (j == localElement2.getEndOffset()) {
          j++;
        } else {
          j = localElement2.getEndOffset();
        }
        MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)localElement2.getAttributes();
        localDefaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(localElement2, localAttributeSet, paramBoolean));
        if (paramBoolean) {
          localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
        }
        localMutableAttributeSet.addAttributes(paramAttributeSet);
      }
      localDefaultDocumentEvent.end();
      fireChangedUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    finally
    {
      writeUnlock();
    }
  }
  
  public StyleSheet getStyleSheet()
  {
    return (StyleSheet)getAttributeContext();
  }
  
  public Iterator getIterator(HTML.Tag paramTag)
  {
    if (paramTag.isBlock()) {
      return null;
    }
    return new LeafIterator(paramTag, this);
  }
  
  protected Element createLeafElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    return new RunElement(paramElement, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected Element createBranchElement(Element paramElement, AttributeSet paramAttributeSet)
  {
    return new BlockElement(paramElement, paramAttributeSet);
  }
  
  protected AbstractDocument.AbstractElement createDefaultRoot()
  {
    writeLock();
    SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
    localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
    BlockElement localBlockElement1 = new BlockElement(null, localSimpleAttributeSet.copyAttributes());
    localSimpleAttributeSet.removeAttributes(localSimpleAttributeSet);
    localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
    BlockElement localBlockElement2 = new BlockElement(localBlockElement1, localSimpleAttributeSet.copyAttributes());
    localSimpleAttributeSet.removeAttributes(localSimpleAttributeSet);
    localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
    getStyleSheet().addCSSAttributeFromHTML(localSimpleAttributeSet, CSS.Attribute.MARGIN_TOP, "0");
    BlockElement localBlockElement3 = new BlockElement(localBlockElement2, localSimpleAttributeSet.copyAttributes());
    localSimpleAttributeSet.removeAttributes(localSimpleAttributeSet);
    localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
    RunElement localRunElement = new RunElement(localBlockElement3, localSimpleAttributeSet, 0, 1);
    Element[] arrayOfElement = new Element[1];
    arrayOfElement[0] = localRunElement;
    localBlockElement3.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = localBlockElement3;
    localBlockElement2.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = localBlockElement2;
    localBlockElement1.replace(0, 0, arrayOfElement);
    writeUnlock();
    return localBlockElement1;
  }
  
  public void setTokenThreshold(int paramInt)
  {
    putProperty("token threshold", new Integer(paramInt));
  }
  
  public int getTokenThreshold()
  {
    Integer localInteger = (Integer)getProperty("token threshold");
    if (localInteger != null) {
      return localInteger.intValue();
    }
    return Integer.MAX_VALUE;
  }
  
  public void setPreservesUnknownTags(boolean paramBoolean)
  {
    preservesUnknownTags = paramBoolean;
  }
  
  public boolean getPreservesUnknownTags()
  {
    return preservesUnknownTags;
  }
  
  public void processHTMLFrameHyperlinkEvent(HTMLFrameHyperlinkEvent paramHTMLFrameHyperlinkEvent)
  {
    String str1 = paramHTMLFrameHyperlinkEvent.getTarget();
    Element localElement1 = paramHTMLFrameHyperlinkEvent.getSourceElement();
    String str2 = paramHTMLFrameHyperlinkEvent.getURL().toString();
    if (str1.equals("_self"))
    {
      updateFrame(localElement1, str2);
    }
    else if (str1.equals("_parent"))
    {
      updateFrameSet(localElement1.getParentElement(), str2);
    }
    else
    {
      Element localElement2 = findFrame(str1);
      if (localElement2 != null) {
        updateFrame(localElement2, str2);
      }
    }
  }
  
  private Element findFrame(String paramString)
  {
    ElementIterator localElementIterator = new ElementIterator(this);
    Element localElement;
    while ((localElement = localElementIterator.next()) != null)
    {
      AttributeSet localAttributeSet = localElement.getAttributes();
      if (matchNameAttribute(localAttributeSet, HTML.Tag.FRAME))
      {
        String str = (String)localAttributeSet.getAttribute(HTML.Attribute.NAME);
        if ((str != null) && (str.equals(paramString))) {
          break;
        }
      }
    }
    return localElement;
  }
  
  static boolean matchNameAttribute(AttributeSet paramAttributeSet, HTML.Tag paramTag)
  {
    Object localObject = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if ((localObject instanceof HTML.Tag))
    {
      HTML.Tag localTag = (HTML.Tag)localObject;
      if (localTag == paramTag) {
        return true;
      }
    }
    return false;
  }
  
  private void updateFrameSet(Element paramElement, String paramString)
  {
    try
    {
      int i = paramElement.getStartOffset();
      int j = Math.min(getLength(), paramElement.getEndOffset());
      String str = "<frame";
      if (paramString != null) {
        str = str + " src=\"" + paramString + "\"";
      }
      str = str + ">";
      installParserIfNecessary();
      setOuterHTML(paramElement, str);
    }
    catch (BadLocationException localBadLocationException) {}catch (IOException localIOException) {}
  }
  
  private void updateFrame(Element paramElement, String paramString)
  {
    try
    {
      writeLock();
      AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramElement.getStartOffset(), 1, DocumentEvent.EventType.CHANGE);
      AttributeSet localAttributeSet = paramElement.getAttributes().copyAttributes();
      MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)paramElement.getAttributes();
      localDefaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(paramElement, localAttributeSet, false));
      localMutableAttributeSet.removeAttribute(HTML.Attribute.SRC);
      localMutableAttributeSet.addAttribute(HTML.Attribute.SRC, paramString);
      localDefaultDocumentEvent.end();
      fireChangedUpdate(localDefaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
    finally
    {
      writeUnlock();
    }
  }
  
  boolean isFrameDocument()
  {
    return frameDocument;
  }
  
  void setFrameDocumentState(boolean paramBoolean)
  {
    frameDocument = paramBoolean;
  }
  
  void addMap(Map paramMap)
  {
    String str = paramMap.getName();
    if (str != null)
    {
      Object localObject = getProperty(MAP_PROPERTY);
      if (localObject == null)
      {
        localObject = new Hashtable(11);
        putProperty(MAP_PROPERTY, localObject);
      }
      if ((localObject instanceof Hashtable)) {
        ((Hashtable)localObject).put("#" + str, paramMap);
      }
    }
  }
  
  void removeMap(Map paramMap)
  {
    String str = paramMap.getName();
    if (str != null)
    {
      Object localObject = getProperty(MAP_PROPERTY);
      if ((localObject instanceof Hashtable)) {
        ((Hashtable)localObject).remove("#" + str);
      }
    }
  }
  
  Map getMap(String paramString)
  {
    if (paramString != null)
    {
      Object localObject = getProperty(MAP_PROPERTY);
      if ((localObject != null) && ((localObject instanceof Hashtable))) {
        return (Map)((Hashtable)localObject).get(paramString);
      }
    }
    return null;
  }
  
  Enumeration getMaps()
  {
    Object localObject = getProperty(MAP_PROPERTY);
    if ((localObject instanceof Hashtable)) {
      return ((Hashtable)localObject).elements();
    }
    return null;
  }
  
  void setDefaultStyleSheetType(String paramString)
  {
    putProperty("StyleType", paramString);
  }
  
  String getDefaultStyleSheetType()
  {
    String str = (String)getProperty("StyleType");
    if (str == null) {
      return "text/css";
    }
    return str;
  }
  
  public void setParser(HTMLEditorKit.Parser paramParser)
  {
    parser = paramParser;
    putProperty("__PARSER__", null);
  }
  
  public HTMLEditorKit.Parser getParser()
  {
    Object localObject = getProperty("__PARSER__");
    if ((localObject instanceof HTMLEditorKit.Parser)) {
      return (HTMLEditorKit.Parser)localObject;
    }
    return parser;
  }
  
  public void setInnerHTML(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if ((paramElement != null) && (paramElement.isLeaf())) {
      throw new IllegalArgumentException("Can not set inner HTML of a leaf");
    }
    if ((paramElement != null) && (paramString != null))
    {
      int i = paramElement.getElementCount();
      int j = paramElement.getStartOffset();
      insertHTML(paramElement, paramElement.getStartOffset(), paramString, true);
      if (paramElement.getElementCount() > i) {
        removeElements(paramElement, paramElement.getElementCount() - i, i);
      }
    }
  }
  
  public void setOuterHTML(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if ((paramElement != null) && (paramElement.getParentElement() != null) && (paramString != null))
    {
      int i = paramElement.getStartOffset();
      int j = paramElement.getEndOffset();
      int k = getLength();
      boolean bool = !paramElement.isLeaf();
      if ((!bool) && ((j > k) || (getText(j - 1, 1).charAt(0) == NEWLINE[0]))) {
        bool = true;
      }
      Element localElement = paramElement.getParentElement();
      int m = localElement.getElementCount();
      insertHTML(localElement, i, paramString, bool);
      int n = getLength();
      if (m != localElement.getElementCount())
      {
        int i1 = localElement.getElementIndex(i + n - k);
        removeElements(localElement, i1, 1);
      }
    }
  }
  
  public void insertAfterStart(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if ((paramElement == null) || (paramString == null)) {
      return;
    }
    if (paramElement.isLeaf()) {
      throw new IllegalArgumentException("Can not insert HTML after start of a leaf");
    }
    insertHTML(paramElement, paramElement.getStartOffset(), paramString, false);
  }
  
  public void insertBeforeEnd(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if ((paramElement != null) && (paramElement.isLeaf())) {
      throw new IllegalArgumentException("Can not set inner HTML before end of leaf");
    }
    if (paramElement != null)
    {
      int i = paramElement.getEndOffset();
      if ((paramElement.getElement(paramElement.getElementIndex(i - 1)).isLeaf()) && (getText(i - 1, 1).charAt(0) == NEWLINE[0])) {
        i--;
      }
      insertHTML(paramElement, i, paramString, false);
    }
  }
  
  public void insertBeforeStart(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if (paramElement != null)
    {
      Element localElement = paramElement.getParentElement();
      if (localElement != null) {
        insertHTML(localElement, paramElement.getStartOffset(), paramString, false);
      }
    }
  }
  
  public void insertAfterEnd(Element paramElement, String paramString)
    throws BadLocationException, IOException
  {
    verifyParser();
    if (paramElement != null)
    {
      Element localElement = paramElement.getParentElement();
      if (localElement != null)
      {
        if (BODYname.equals(localElement.getName())) {
          insertInBody = true;
        }
        int i = paramElement.getEndOffset();
        if (i > getLength() + 1) {
          i--;
        } else if ((paramElement.isLeaf()) && (getText(i - 1, 1).charAt(0) == NEWLINE[0])) {
          i--;
        }
        insertHTML(localElement, i, paramString, false);
        if (insertInBody) {
          insertInBody = false;
        }
      }
    }
  }
  
  public Element getElement(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return getElement(getDefaultRootElement(), HTML.Attribute.ID, paramString, true);
  }
  
  public Element getElement(Element paramElement, Object paramObject1, Object paramObject2)
  {
    return getElement(paramElement, paramObject1, paramObject2, true);
  }
  
  private Element getElement(Element paramElement, Object paramObject1, Object paramObject2, boolean paramBoolean)
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    if ((localAttributeSet != null) && (localAttributeSet.isDefined(paramObject1)) && (paramObject2.equals(localAttributeSet.getAttribute(paramObject1)))) {
      return paramElement;
    }
    Object localObject2;
    if (!paramElement.isLeaf())
    {
      int i = 0;
      int j = paramElement.getElementCount();
      while (i < j)
      {
        localObject2 = getElement(paramElement.getElement(i), paramObject1, paramObject2, paramBoolean);
        if (localObject2 != null) {
          return (Element)localObject2;
        }
        i++;
      }
    }
    else if ((paramBoolean) && (localAttributeSet != null))
    {
      Enumeration localEnumeration = localAttributeSet.getAttributeNames();
      if (localEnumeration != null) {
        while (localEnumeration.hasMoreElements())
        {
          Object localObject1 = localEnumeration.nextElement();
          if (((localObject1 instanceof HTML.Tag)) && ((localAttributeSet.getAttribute(localObject1) instanceof AttributeSet)))
          {
            localObject2 = (AttributeSet)localAttributeSet.getAttribute(localObject1);
            if ((((AttributeSet)localObject2).isDefined(paramObject1)) && (paramObject2.equals(((AttributeSet)localObject2).getAttribute(paramObject1)))) {
              return paramElement;
            }
          }
        }
      }
    }
    return null;
  }
  
  private void verifyParser()
  {
    if (getParser() == null) {
      throw new IllegalStateException("No HTMLEditorKit.Parser");
    }
  }
  
  private void installParserIfNecessary()
  {
    if (getParser() == null) {
      setParser(new HTMLEditorKit().getParser());
    }
  }
  
  private void insertHTML(Element paramElement, int paramInt, String paramString, boolean paramBoolean)
    throws BadLocationException, IOException
  {
    if ((paramElement != null) && (paramString != null))
    {
      HTMLEditorKit.Parser localParser = getParser();
      if (localParser != null)
      {
        int i = Math.max(0, paramInt - 1);
        Element localElement1 = getCharacterElement(i);
        Element localElement2 = paramElement;
        int j = 0;
        int k = 0;
        if (paramElement.getStartOffset() > i)
        {
          while ((localElement2 != null) && (localElement2.getStartOffset() > i))
          {
            localElement2 = localElement2.getParentElement();
            k++;
          }
          if (localElement2 == null) {
            throw new BadLocationException("No common parent", paramInt);
          }
        }
        while ((localElement1 != null) && (localElement1 != localElement2))
        {
          j++;
          localElement1 = localElement1.getParentElement();
        }
        if (localElement1 != null)
        {
          HTMLReader localHTMLReader = new HTMLReader(paramInt, j - 1, k, null, false, true, paramBoolean);
          localParser.parse(new StringReader(paramString), localHTMLReader, true);
          localHTMLReader.flush();
        }
      }
    }
  }
  
  private void removeElements(Element paramElement, int paramInt1, int paramInt2)
    throws BadLocationException
  {
    writeLock();
    try
    {
      int i = paramElement.getElement(paramInt1).getStartOffset();
      int j = paramElement.getElement(paramInt1 + paramInt2 - 1).getEndOffset();
      if (j > getLength()) {
        removeElementsAtEnd(paramElement, paramInt1, paramInt2, i, j);
      } else {
        removeElements(paramElement, paramInt1, paramInt2, i, j);
      }
    }
    finally
    {
      writeUnlock();
    }
  }
  
  private void removeElementsAtEnd(Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    boolean bool = paramElement.getElement(paramInt1 - 1).isLeaf();
    AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt3 - 1, paramInt4 - paramInt3 + 1, DocumentEvent.EventType.REMOVE);
    Element localElement;
    if (bool)
    {
      localElement = getCharacterElement(getLength());
      paramInt1--;
      if (localElement.getParentElement() != paramElement) {
        replace(localDefaultDocumentEvent, paramElement, paramInt1, ++paramInt2, paramInt3, paramInt4, true, true);
      } else {
        replace(localDefaultDocumentEvent, paramElement, paramInt1, paramInt2, paramInt3, paramInt4, true, false);
      }
    }
    else
    {
      for (localElement = paramElement.getElement(paramInt1 - 1); !localElement.isLeaf(); localElement = localElement.getElement(localElement.getElementCount() - 1)) {}
      localElement = localElement.getParentElement();
      replace(localDefaultDocumentEvent, paramElement, paramInt1, paramInt2, paramInt3, paramInt4, false, false);
      replace(localDefaultDocumentEvent, localElement, localElement.getElementCount() - 1, 1, paramInt3, paramInt4, true, true);
    }
    postRemoveUpdate(localDefaultDocumentEvent);
    localDefaultDocumentEvent.end();
    fireRemoveUpdate(localDefaultDocumentEvent);
    fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
  }
  
  private void replace(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    throws BadLocationException
  {
    AttributeSet localAttributeSet = paramElement.getElement(paramInt1).getAttributes();
    Element[] arrayOfElement2 = new Element[paramInt2];
    for (int i = 0; i < paramInt2; i++) {
      arrayOfElement2[i] = paramElement.getElement(i + paramInt1);
    }
    if (paramBoolean1)
    {
      UndoableEdit localUndoableEdit = getContent().remove(paramInt3 - 1, paramInt4 - paramInt3);
      if (localUndoableEdit != null) {
        paramDefaultDocumentEvent.addEdit(localUndoableEdit);
      }
    }
    Element[] arrayOfElement1;
    if (paramBoolean2)
    {
      arrayOfElement1 = new Element[1];
      arrayOfElement1[0] = createLeafElement(paramElement, localAttributeSet, paramInt3 - 1, paramInt3);
    }
    else
    {
      arrayOfElement1 = new Element[0];
    }
    paramDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(paramElement, paramInt1, arrayOfElement2, arrayOfElement1));
    ((AbstractDocument.BranchElement)paramElement).replace(paramInt1, arrayOfElement2.length, arrayOfElement1);
  }
  
  private void removeElements(Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    Element[] arrayOfElement1 = new Element[paramInt2];
    Element[] arrayOfElement2 = new Element[0];
    for (int i = 0; i < paramInt2; i++) {
      arrayOfElement1[i] = paramElement.getElement(i + paramInt1);
    }
    AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt3, paramInt4 - paramInt3, DocumentEvent.EventType.REMOVE);
    ((AbstractDocument.BranchElement)paramElement).replace(paramInt1, arrayOfElement1.length, arrayOfElement2);
    localDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(paramElement, paramInt1, arrayOfElement1, arrayOfElement2));
    UndoableEdit localUndoableEdit = getContent().remove(paramInt3, paramInt4 - paramInt3);
    if (localUndoableEdit != null) {
      localDefaultDocumentEvent.addEdit(localUndoableEdit);
    }
    postRemoveUpdate(localDefaultDocumentEvent);
    localDefaultDocumentEvent.end();
    fireRemoveUpdate(localDefaultDocumentEvent);
    if (localUndoableEdit != null) {
      fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
    }
  }
  
  void obtainLock()
  {
    writeLock();
  }
  
  void releaseLock()
  {
    writeUnlock();
  }
  
  protected void fireChangedUpdate(DocumentEvent paramDocumentEvent)
  {
    super.fireChangedUpdate(paramDocumentEvent);
  }
  
  protected void fireUndoableEditUpdate(UndoableEditEvent paramUndoableEditEvent)
  {
    super.fireUndoableEditUpdate(paramUndoableEditEvent);
  }
  
  boolean hasBaseTag()
  {
    return hasBaseTag;
  }
  
  String getBaseTarget()
  {
    return baseTarget;
  }
  
  static
  {
    contentAttributeSet = new SimpleAttributeSet();
    ((MutableAttributeSet)contentAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
    NEWLINE = new char[1];
    NEWLINE[0] = '\n';
  }
  
  public class BlockElement
    extends AbstractDocument.BranchElement
  {
    public BlockElement(Element paramElement, AttributeSet paramAttributeSet)
    {
      super(paramElement, paramAttributeSet);
    }
    
    public String getName()
    {
      Object localObject = getAttribute(StyleConstants.NameAttribute);
      if (localObject != null) {
        return localObject.toString();
      }
      return super.getName();
    }
    
    public AttributeSet getResolveParent()
    {
      return null;
    }
  }
  
  private static class FixedLengthDocument
    extends PlainDocument
  {
    private int maxLength;
    
    public FixedLengthDocument(int paramInt)
    {
      maxLength = paramInt;
    }
    
    public void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException
    {
      if ((paramString != null) && (paramString.length() + getLength() <= maxLength)) {
        super.insertString(paramInt, paramString, paramAttributeSet);
      }
    }
  }
  
  public class HTMLReader
    extends HTMLEditorKit.ParserCallback
  {
    private boolean receivedEndHTML;
    private int flushCount;
    private boolean insertAfterImplied;
    private boolean wantsTrailingNewline;
    int threshold;
    int offset;
    boolean inParagraph = false;
    boolean impliedP = false;
    boolean inPre = false;
    boolean inTextArea = false;
    TextAreaDocument textAreaDocument = null;
    boolean inTitle = false;
    boolean lastWasNewline = true;
    boolean emptyAnchor;
    boolean midInsert;
    boolean inBody;
    HTML.Tag insertTag;
    boolean insertInsertTag;
    boolean foundInsertTag;
    int insertTagDepthDelta;
    int popDepth;
    int pushDepth;
    Map lastMap;
    boolean inStyle = false;
    String defaultStyle;
    Vector<Object> styles;
    boolean inHead = false;
    boolean isStyleCSS = "text/css".equals(getDefaultStyleSheetType());
    boolean emptyDocument = getLength() == 0;
    AttributeSet styleAttributes;
    Option option;
    protected Vector<DefaultStyledDocument.ElementSpec> parseBuffer = new Vector();
    protected MutableAttributeSet charAttr = new HTMLDocument.TaggedAttributeSet();
    Stack<AttributeSet> charAttrStack = new Stack();
    Hashtable<HTML.Tag, TagAction> tagMap;
    int inBlock = 0;
    private HTML.Tag nextTagAfterPImplied = null;
    
    public HTMLReader(int paramInt)
    {
      this(paramInt, 0, 0, null);
    }
    
    public HTMLReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag)
    {
      this(paramInt1, paramInt2, paramInt3, paramTag, true, false, true);
    }
    
    HTMLReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      offset = paramInt1;
      threshold = getTokenThreshold();
      tagMap = new Hashtable(57);
      TagAction localTagAction = new TagAction();
      BlockAction localBlockAction = new BlockAction();
      ParagraphAction localParagraphAction = new ParagraphAction();
      CharacterAction localCharacterAction = new CharacterAction();
      SpecialAction localSpecialAction = new SpecialAction();
      FormAction localFormAction = new FormAction();
      HiddenAction localHiddenAction = new HiddenAction();
      ConvertAction localConvertAction = new ConvertAction();
      tagMap.put(HTML.Tag.A, new AnchorAction());
      tagMap.put(HTML.Tag.ADDRESS, localCharacterAction);
      tagMap.put(HTML.Tag.APPLET, localHiddenAction);
      tagMap.put(HTML.Tag.AREA, new AreaAction());
      tagMap.put(HTML.Tag.B, localConvertAction);
      tagMap.put(HTML.Tag.BASE, new BaseAction());
      tagMap.put(HTML.Tag.BASEFONT, localCharacterAction);
      tagMap.put(HTML.Tag.BIG, localCharacterAction);
      tagMap.put(HTML.Tag.BLOCKQUOTE, localBlockAction);
      tagMap.put(HTML.Tag.BODY, localBlockAction);
      tagMap.put(HTML.Tag.BR, localSpecialAction);
      tagMap.put(HTML.Tag.CAPTION, localBlockAction);
      tagMap.put(HTML.Tag.CENTER, localBlockAction);
      tagMap.put(HTML.Tag.CITE, localCharacterAction);
      tagMap.put(HTML.Tag.CODE, localCharacterAction);
      tagMap.put(HTML.Tag.DD, localBlockAction);
      tagMap.put(HTML.Tag.DFN, localCharacterAction);
      tagMap.put(HTML.Tag.DIR, localBlockAction);
      tagMap.put(HTML.Tag.DIV, localBlockAction);
      tagMap.put(HTML.Tag.DL, localBlockAction);
      tagMap.put(HTML.Tag.DT, localParagraphAction);
      tagMap.put(HTML.Tag.EM, localCharacterAction);
      tagMap.put(HTML.Tag.FONT, localConvertAction);
      tagMap.put(HTML.Tag.FORM, new FormTagAction(null));
      tagMap.put(HTML.Tag.FRAME, localSpecialAction);
      tagMap.put(HTML.Tag.FRAMESET, localBlockAction);
      tagMap.put(HTML.Tag.H1, localParagraphAction);
      tagMap.put(HTML.Tag.H2, localParagraphAction);
      tagMap.put(HTML.Tag.H3, localParagraphAction);
      tagMap.put(HTML.Tag.H4, localParagraphAction);
      tagMap.put(HTML.Tag.H5, localParagraphAction);
      tagMap.put(HTML.Tag.H6, localParagraphAction);
      tagMap.put(HTML.Tag.HEAD, new HeadAction());
      tagMap.put(HTML.Tag.HR, localSpecialAction);
      tagMap.put(HTML.Tag.HTML, localBlockAction);
      tagMap.put(HTML.Tag.I, localConvertAction);
      tagMap.put(HTML.Tag.IMG, localSpecialAction);
      tagMap.put(HTML.Tag.INPUT, localFormAction);
      tagMap.put(HTML.Tag.ISINDEX, new IsindexAction());
      tagMap.put(HTML.Tag.KBD, localCharacterAction);
      tagMap.put(HTML.Tag.LI, localBlockAction);
      tagMap.put(HTML.Tag.LINK, new LinkAction());
      tagMap.put(HTML.Tag.MAP, new MapAction());
      tagMap.put(HTML.Tag.MENU, localBlockAction);
      tagMap.put(HTML.Tag.META, new MetaAction());
      tagMap.put(HTML.Tag.NOBR, localCharacterAction);
      tagMap.put(HTML.Tag.NOFRAMES, localBlockAction);
      tagMap.put(HTML.Tag.OBJECT, localSpecialAction);
      tagMap.put(HTML.Tag.OL, localBlockAction);
      tagMap.put(HTML.Tag.OPTION, localFormAction);
      tagMap.put(HTML.Tag.P, localParagraphAction);
      tagMap.put(HTML.Tag.PARAM, new ObjectAction());
      tagMap.put(HTML.Tag.PRE, new PreAction());
      tagMap.put(HTML.Tag.SAMP, localCharacterAction);
      tagMap.put(HTML.Tag.SCRIPT, localHiddenAction);
      tagMap.put(HTML.Tag.SELECT, localFormAction);
      tagMap.put(HTML.Tag.SMALL, localCharacterAction);
      tagMap.put(HTML.Tag.SPAN, localCharacterAction);
      tagMap.put(HTML.Tag.STRIKE, localConvertAction);
      tagMap.put(HTML.Tag.S, localCharacterAction);
      tagMap.put(HTML.Tag.STRONG, localCharacterAction);
      tagMap.put(HTML.Tag.STYLE, new StyleAction());
      tagMap.put(HTML.Tag.SUB, localConvertAction);
      tagMap.put(HTML.Tag.SUP, localConvertAction);
      tagMap.put(HTML.Tag.TABLE, localBlockAction);
      tagMap.put(HTML.Tag.TD, localBlockAction);
      tagMap.put(HTML.Tag.TEXTAREA, localFormAction);
      tagMap.put(HTML.Tag.TH, localBlockAction);
      tagMap.put(HTML.Tag.TITLE, new TitleAction());
      tagMap.put(HTML.Tag.TR, localBlockAction);
      tagMap.put(HTML.Tag.TT, localCharacterAction);
      tagMap.put(HTML.Tag.U, localConvertAction);
      tagMap.put(HTML.Tag.UL, localBlockAction);
      tagMap.put(HTML.Tag.VAR, localCharacterAction);
      if (paramTag != null)
      {
        insertTag = paramTag;
        popDepth = paramInt2;
        pushDepth = paramInt3;
        insertInsertTag = paramBoolean1;
        foundInsertTag = false;
      }
      else
      {
        foundInsertTag = true;
      }
      if (paramBoolean2)
      {
        popDepth = paramInt2;
        pushDepth = paramInt3;
        insertAfterImplied = true;
        foundInsertTag = false;
        midInsert = false;
        insertInsertTag = true;
        wantsTrailingNewline = paramBoolean3;
      }
      else
      {
        midInsert = ((!emptyDocument) && (paramTag == null));
        if (midInsert) {
          generateEndsSpecsForMidInsert();
        }
      }
      if ((!emptyDocument) && (!midInsert))
      {
        int i = Math.max(offset - 1, 0);
        Element localElement = getCharacterElement(i);
        for (int j = 0; j <= popDepth; j++) {
          localElement = localElement.getParentElement();
        }
        for (j = 0; j < pushDepth; j++)
        {
          int k = localElement.getElementIndex(offset);
          localElement = localElement.getElement(k);
        }
        AttributeSet localAttributeSet = localElement.getAttributes();
        if (localAttributeSet != null)
        {
          HTML.Tag localTag = (HTML.Tag)localAttributeSet.getAttribute(StyleConstants.NameAttribute);
          if (localTag != null) {
            inParagraph = localTag.isParagraph();
          }
        }
      }
    }
    
    private void generateEndsSpecsForMidInsert()
    {
      int i = heightToElementWithName(HTML.Tag.BODY, Math.max(0, offset - 1));
      int j = 0;
      if ((i == -1) && (offset > 0))
      {
        i = heightToElementWithName(HTML.Tag.BODY, offset);
        if (i != -1)
        {
          i = depthTo(offset - 1) - 1;
          j = 1;
        }
      }
      if (i == -1) {
        throw new RuntimeException("Must insert new content into body element-");
      }
      if (i != -1)
      {
        try
        {
          if ((j == 0) && (offset > 0) && (!getText(offset - 1, 1).equals("\n")))
          {
            SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
            localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            DefaultStyledDocument.ElementSpec localElementSpec2 = new DefaultStyledDocument.ElementSpec(localSimpleAttributeSet, (short)3, HTMLDocument.NEWLINE, 0, 1);
            parseBuffer.addElement(localElementSpec2);
          }
        }
        catch (BadLocationException localBadLocationException) {}
        while (i-- > 0) {
          parseBuffer.addElement(new DefaultStyledDocument.ElementSpec(null, (short)2));
        }
        if (j != 0)
        {
          DefaultStyledDocument.ElementSpec localElementSpec1 = new DefaultStyledDocument.ElementSpec(null, (short)1);
          localElementSpec1.setDirection((short)5);
          parseBuffer.addElement(localElementSpec1);
        }
      }
    }
    
    private int depthTo(int paramInt)
    {
      Element localElement = getDefaultRootElement();
      int i = 0;
      while (!localElement.isLeaf())
      {
        i++;
        localElement = localElement.getElement(localElement.getElementIndex(paramInt));
      }
      return i;
    }
    
    private int heightToElementWithName(Object paramObject, int paramInt)
    {
      Element localElement = getCharacterElement(paramInt).getParentElement();
      int i = 0;
      while ((localElement != null) && (localElement.getAttributes().getAttribute(StyleConstants.NameAttribute) != paramObject))
      {
        i++;
        localElement = localElement.getParentElement();
      }
      return localElement == null ? -1 : i;
    }
    
    private void adjustEndElement()
    {
      int i = getLength();
      if (i == 0) {
        return;
      }
      obtainLock();
      try
      {
        Element[] arrayOfElement1 = getPathTo(i - 1);
        int j = arrayOfElement1.length;
        if ((j > 1) && (arrayOfElement1[1].getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) && (arrayOfElement1[1].getEndOffset() == i))
        {
          String str = getText(i - 1, 1);
          Element[] arrayOfElement2 = new Element[0];
          Element[] arrayOfElement3 = new Element[1];
          int k = arrayOfElement1[0].getElementIndex(i);
          arrayOfElement3[0] = arrayOfElement1[0].getElement(k);
          ((AbstractDocument.BranchElement)arrayOfElement1[0]).replace(k, 1, arrayOfElement2);
          AbstractDocument.ElementEdit localElementEdit = new AbstractDocument.ElementEdit(arrayOfElement1[0], k, arrayOfElement3, arrayOfElement2);
          SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
          localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
          localSimpleAttributeSet.addAttribute("CR", Boolean.TRUE);
          arrayOfElement2 = new Element[1];
          arrayOfElement2[0] = createLeafElement(arrayOfElement1[(j - 1)], localSimpleAttributeSet, i, i + 1);
          k = arrayOfElement1[(j - 1)].getElementCount();
          ((AbstractDocument.BranchElement)arrayOfElement1[(j - 1)]).replace(k, 0, arrayOfElement2);
          AbstractDocument.DefaultDocumentEvent localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(HTMLDocument.this, i, 1, DocumentEvent.EventType.CHANGE);
          localDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(arrayOfElement1[(j - 1)], k, new Element[0], arrayOfElement2));
          localDefaultDocumentEvent.addEdit(localElementEdit);
          localDefaultDocumentEvent.end();
          fireChangedUpdate(localDefaultDocumentEvent);
          fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
          if (str.equals("\n"))
          {
            localDefaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(HTMLDocument.this, i - 1, 1, DocumentEvent.EventType.REMOVE);
            removeUpdate(localDefaultDocumentEvent);
            UndoableEdit localUndoableEdit = getContent().remove(i - 1, 1);
            if (localUndoableEdit != null) {
              localDefaultDocumentEvent.addEdit(localUndoableEdit);
            }
            postRemoveUpdate(localDefaultDocumentEvent);
            localDefaultDocumentEvent.end();
            fireRemoveUpdate(localDefaultDocumentEvent);
            fireUndoableEditUpdate(new UndoableEditEvent(this, localDefaultDocumentEvent));
          }
        }
      }
      catch (BadLocationException localBadLocationException) {}finally
      {
        releaseLock();
      }
    }
    
    private Element[] getPathTo(int paramInt)
    {
      Stack localStack = new Stack();
      for (Element localElement = getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(localElement.getElementIndex(paramInt))) {
        localStack.push(localElement);
      }
      Element[] arrayOfElement = new Element[localStack.size()];
      localStack.copyInto(arrayOfElement);
      return arrayOfElement;
    }
    
    public void flush()
      throws BadLocationException
    {
      if ((emptyDocument) && (!insertAfterImplied))
      {
        if ((getLength() > 0) || (parseBuffer.size() > 0))
        {
          flushBuffer(true);
          adjustEndElement();
        }
      }
      else {
        flushBuffer(true);
      }
    }
    
    public void handleText(char[] paramArrayOfChar, int paramInt)
    {
      if ((receivedEndHTML) || ((midInsert) && (!inBody))) {
        return;
      }
      if (getProperty("i18n").equals(Boolean.FALSE))
      {
        Object localObject = getProperty(TextAttribute.RUN_DIRECTION);
        if ((localObject != null) && (localObject.equals(TextAttribute.RUN_DIRECTION_RTL))) {
          putProperty("i18n", Boolean.TRUE);
        } else if (SwingUtilities2.isComplexLayout(paramArrayOfChar, 0, paramArrayOfChar.length)) {
          putProperty("i18n", Boolean.TRUE);
        }
      }
      if (inTextArea)
      {
        textAreaContent(paramArrayOfChar);
      }
      else if (inPre)
      {
        preContent(paramArrayOfChar);
      }
      else if (inTitle)
      {
        putProperty("title", new String(paramArrayOfChar));
      }
      else if (option != null)
      {
        option.setLabel(new String(paramArrayOfChar));
      }
      else if (inStyle)
      {
        if (styles != null) {
          styles.addElement(new String(paramArrayOfChar));
        }
      }
      else if (inBlock > 0)
      {
        if ((!foundInsertTag) && (insertAfterImplied))
        {
          foundInsertTag(false);
          foundInsertTag = true;
          inParagraph = (impliedP = !insertInBody ? 1 : 0);
        }
        if (paramArrayOfChar.length >= 1) {
          addContent(paramArrayOfChar, 0, paramArrayOfChar.length);
        }
      }
    }
    
    public void handleStartTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt)
    {
      if (receivedEndHTML) {
        return;
      }
      if ((midInsert) && (!inBody))
      {
        if (paramTag == HTML.Tag.BODY)
        {
          inBody = true;
          inBlock += 1;
        }
        return;
      }
      if ((!inBody) && (paramTag == HTML.Tag.BODY)) {
        inBody = true;
      }
      if ((isStyleCSS) && (paramMutableAttributeSet.isDefined(HTML.Attribute.STYLE)))
      {
        localObject = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.STYLE);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.STYLE);
        styleAttributes = getStyleSheet().getDeclaration((String)localObject);
        paramMutableAttributeSet.addAttributes(styleAttributes);
      }
      else
      {
        styleAttributes = null;
      }
      Object localObject = (TagAction)tagMap.get(paramTag);
      if (localObject != null) {
        ((TagAction)localObject).start(paramTag, paramMutableAttributeSet);
      }
    }
    
    public void handleComment(char[] paramArrayOfChar, int paramInt)
    {
      if (receivedEndHTML)
      {
        addExternalComment(new String(paramArrayOfChar));
        return;
      }
      if (inStyle)
      {
        if (styles != null) {
          styles.addElement(new String(paramArrayOfChar));
        }
      }
      else if (getPreservesUnknownTags())
      {
        if ((inBlock == 0) && ((foundInsertTag) || (insertTag != HTML.Tag.COMMENT)))
        {
          addExternalComment(new String(paramArrayOfChar));
          return;
        }
        localObject = new SimpleAttributeSet();
        ((SimpleAttributeSet)localObject).addAttribute(HTML.Attribute.COMMENT, new String(paramArrayOfChar));
        addSpecialElement(HTML.Tag.COMMENT, (MutableAttributeSet)localObject);
      }
      Object localObject = (TagAction)tagMap.get(HTML.Tag.COMMENT);
      if (localObject != null)
      {
        ((TagAction)localObject).start(HTML.Tag.COMMENT, new SimpleAttributeSet());
        ((TagAction)localObject).end(HTML.Tag.COMMENT);
      }
    }
    
    private void addExternalComment(String paramString)
    {
      Object localObject = getProperty("AdditionalComments");
      if ((localObject != null) && (!(localObject instanceof Vector))) {
        return;
      }
      if (localObject == null)
      {
        localObject = new Vector();
        putProperty("AdditionalComments", localObject);
      }
      ((Vector)localObject).addElement(paramString);
    }
    
    public void handleEndTag(HTML.Tag paramTag, int paramInt)
    {
      if ((receivedEndHTML) || ((midInsert) && (!inBody))) {
        return;
      }
      if (paramTag == HTML.Tag.HTML) {
        receivedEndHTML = true;
      }
      if (paramTag == HTML.Tag.BODY)
      {
        inBody = false;
        if (midInsert) {
          inBlock -= 1;
        }
      }
      TagAction localTagAction = (TagAction)tagMap.get(paramTag);
      if (localTagAction != null) {
        localTagAction.end(paramTag);
      }
    }
    
    public void handleSimpleTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt)
    {
      if ((receivedEndHTML) || ((midInsert) && (!inBody))) {
        return;
      }
      if ((isStyleCSS) && (paramMutableAttributeSet.isDefined(HTML.Attribute.STYLE)))
      {
        localObject = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.STYLE);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.STYLE);
        styleAttributes = getStyleSheet().getDeclaration((String)localObject);
        paramMutableAttributeSet.addAttributes(styleAttributes);
      }
      else
      {
        styleAttributes = null;
      }
      Object localObject = (TagAction)tagMap.get(paramTag);
      if (localObject != null)
      {
        ((TagAction)localObject).start(paramTag, paramMutableAttributeSet);
        ((TagAction)localObject).end(paramTag);
      }
      else if (getPreservesUnknownTags())
      {
        addSpecialElement(paramTag, paramMutableAttributeSet);
      }
    }
    
    public void handleEndOfLineString(String paramString)
    {
      if ((emptyDocument) && (paramString != null)) {
        putProperty("__EndOfLine__", paramString);
      }
    }
    
    protected void registerTag(HTML.Tag paramTag, TagAction paramTagAction)
    {
      tagMap.put(paramTag, paramTagAction);
    }
    
    protected void pushCharacterStyle()
    {
      charAttrStack.push(charAttr.copyAttributes());
    }
    
    protected void popCharacterStyle()
    {
      if (!charAttrStack.empty())
      {
        charAttr = ((MutableAttributeSet)charAttrStack.peek());
        charAttrStack.pop();
      }
    }
    
    protected void textAreaContent(char[] paramArrayOfChar)
    {
      try
      {
        textAreaDocument.insertString(textAreaDocument.getLength(), new String(paramArrayOfChar), null);
      }
      catch (BadLocationException localBadLocationException) {}
    }
    
    protected void preContent(char[] paramArrayOfChar)
    {
      int i = 0;
      for (int j = 0; j < paramArrayOfChar.length; j++) {
        if (paramArrayOfChar[j] == '\n')
        {
          addContent(paramArrayOfChar, i, j - i + 1);
          blockClose(HTML.Tag.IMPLIED);
          SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
          localSimpleAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
          blockOpen(HTML.Tag.IMPLIED, localSimpleAttributeSet);
          i = j + 1;
        }
      }
      if (i < paramArrayOfChar.length) {
        addContent(paramArrayOfChar, i, paramArrayOfChar.length - i);
      }
    }
    
    protected void blockOpen(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
    {
      if (impliedP) {
        blockClose(HTML.Tag.IMPLIED);
      }
      inBlock += 1;
      if (!canInsertTag(paramTag, paramMutableAttributeSet, true)) {
        return;
      }
      if (paramMutableAttributeSet.isDefined(IMPLIED)) {
        paramMutableAttributeSet.removeAttribute(IMPLIED);
      }
      lastWasNewline = false;
      paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, paramTag);
      DefaultStyledDocument.ElementSpec localElementSpec = new DefaultStyledDocument.ElementSpec(paramMutableAttributeSet.copyAttributes(), (short)1);
      parseBuffer.addElement(localElementSpec);
    }
    
    protected void blockClose(HTML.Tag paramTag)
    {
      inBlock -= 1;
      if (!foundInsertTag) {
        return;
      }
      if (!lastWasNewline)
      {
        pushCharacterStyle();
        charAttr.addAttribute("CR", Boolean.TRUE);
        addContent(HTMLDocument.NEWLINE, 0, 1, true);
        popCharacterStyle();
        lastWasNewline = true;
      }
      if (impliedP)
      {
        impliedP = false;
        inParagraph = false;
        if (paramTag != HTML.Tag.IMPLIED) {
          blockClose(HTML.Tag.IMPLIED);
        }
      }
      Object localObject1 = parseBuffer.size() > 0 ? (DefaultStyledDocument.ElementSpec)parseBuffer.lastElement() : null;
      if ((localObject1 != null) && (((DefaultStyledDocument.ElementSpec)localObject1).getType() == 1))
      {
        localObject2 = new char[1];
        localObject2[0] = 32;
        addContent((char[])localObject2, 0, 1);
      }
      Object localObject2 = new DefaultStyledDocument.ElementSpec(null, (short)2);
      parseBuffer.addElement(localObject2);
    }
    
    protected void addContent(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      addContent(paramArrayOfChar, paramInt1, paramInt2, true);
    }
    
    protected void addContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (!foundInsertTag) {
        return;
      }
      if ((paramBoolean) && (!inParagraph) && (!inPre))
      {
        blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        inParagraph = true;
        impliedP = true;
      }
      emptyAnchor = false;
      charAttr.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      AttributeSet localAttributeSet = charAttr.copyAttributes();
      DefaultStyledDocument.ElementSpec localElementSpec = new DefaultStyledDocument.ElementSpec(localAttributeSet, (short)3, paramArrayOfChar, paramInt1, paramInt2);
      parseBuffer.addElement(localElementSpec);
      if (parseBuffer.size() > threshold)
      {
        if (threshold <= 10000) {
          threshold *= 5;
        }
        try
        {
          flushBuffer(false);
        }
        catch (BadLocationException localBadLocationException) {}
      }
      if (paramInt2 > 0) {
        lastWasNewline = (paramArrayOfChar[(paramInt1 + paramInt2 - 1)] == '\n');
      }
    }
    
    protected void addSpecialElement(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
    {
      if ((paramTag != HTML.Tag.FRAME) && (!inParagraph) && (!inPre))
      {
        nextTagAfterPImplied = paramTag;
        blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        nextTagAfterPImplied = null;
        inParagraph = true;
        impliedP = true;
      }
      if (!canInsertTag(paramTag, paramMutableAttributeSet, paramTag.isBlock())) {
        return;
      }
      if (paramMutableAttributeSet.isDefined(IMPLIED)) {
        paramMutableAttributeSet.removeAttribute(IMPLIED);
      }
      emptyAnchor = false;
      paramMutableAttributeSet.addAttributes(charAttr);
      paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, paramTag);
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = ' ';
      DefaultStyledDocument.ElementSpec localElementSpec = new DefaultStyledDocument.ElementSpec(paramMutableAttributeSet.copyAttributes(), (short)3, arrayOfChar, 0, 1);
      parseBuffer.addElement(localElementSpec);
      if (paramTag == HTML.Tag.FRAME) {
        lastWasNewline = true;
      }
    }
    
    void flushBuffer(boolean paramBoolean)
      throws BadLocationException
    {
      int i = getLength();
      int j = parseBuffer.size();
      if ((paramBoolean) && ((insertTag != null) || (insertAfterImplied)) && (j > 0))
      {
        adjustEndSpecsForPartialInsert();
        j = parseBuffer.size();
      }
      DefaultStyledDocument.ElementSpec[] arrayOfElementSpec = new DefaultStyledDocument.ElementSpec[j];
      parseBuffer.copyInto(arrayOfElementSpec);
      if ((i == 0) && (insertTag == null) && (!insertAfterImplied)) {
        create(arrayOfElementSpec);
      } else {
        insert(offset, arrayOfElementSpec);
      }
      parseBuffer.removeAllElements();
      offset += getLength() - i;
      flushCount += 1;
    }
    
    private void adjustEndSpecsForPartialInsert()
    {
      int i = parseBuffer.size();
      int j;
      if (insertTagDepthDelta < 0) {
        for (j = insertTagDepthDelta; (j < 0) && (i >= 0) && (((DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(i - 1)).getType() == 2); j++) {
          parseBuffer.removeElementAt(--i);
        }
      }
      if ((flushCount == 0) && ((!insertAfterImplied) || (!wantsTrailingNewline)))
      {
        j = 0;
        if ((pushDepth > 0) && (((DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(0)).getType() == 3)) {
          j++;
        }
        j += popDepth + pushDepth;
        int k = 0;
        int m = j;
        while ((j < i) && (((DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(j)).getType() == 3))
        {
          j++;
          k++;
        }
        if (k > 1)
        {
          while ((j < i) && (((DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(j)).getType() == 2)) {
            j++;
          }
          if (j == i)
          {
            char[] arrayOfChar = ((DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(m + k - 1)).getArray();
            if ((arrayOfChar.length == 1) && (arrayOfChar[0] == HTMLDocument.NEWLINE[0]))
            {
              j = m + k - 1;
              while (i > j) {
                parseBuffer.removeElementAt(--i);
              }
            }
          }
        }
      }
      if (wantsTrailingNewline) {
        for (j = parseBuffer.size() - 1; j >= 0; j--)
        {
          DefaultStyledDocument.ElementSpec localElementSpec = (DefaultStyledDocument.ElementSpec)parseBuffer.elementAt(j);
          if (localElementSpec.getType() == 3)
          {
            if (localElementSpec.getArray()[(localElementSpec.getLength() - 1)] == '\n') {
              break;
            }
            SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
            localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            parseBuffer.insertElementAt(new DefaultStyledDocument.ElementSpec(localSimpleAttributeSet, (short)3, HTMLDocument.NEWLINE, 0, 1), j + 1);
            break;
          }
        }
      }
    }
    
    void addCSSRules(String paramString)
    {
      StyleSheet localStyleSheet = getStyleSheet();
      localStyleSheet.addRule(paramString);
    }
    
    void linkCSSStyleSheet(String paramString)
    {
      URL localURL;
      try
      {
        localURL = new URL(base, paramString);
      }
      catch (MalformedURLException localMalformedURLException1)
      {
        try
        {
          localURL = new URL(paramString);
        }
        catch (MalformedURLException localMalformedURLException2)
        {
          localURL = null;
        }
      }
      if (localURL != null) {
        getStyleSheet().importStyleSheet(localURL);
      }
    }
    
    private boolean canInsertTag(HTML.Tag paramTag, AttributeSet paramAttributeSet, boolean paramBoolean)
    {
      if (!foundInsertTag)
      {
        int i = (paramTag == HTML.Tag.IMPLIED) && (!inParagraph) && (!inPre) ? 1 : 0;
        if ((i != 0) && (nextTagAfterPImplied != null))
        {
          if (insertTag != null)
          {
            boolean bool = isInsertTag(nextTagAfterPImplied);
            if ((!bool) || (!insertInsertTag)) {
              return false;
            }
          }
        }
        else if (((insertTag != null) && (!isInsertTag(paramTag))) || ((insertAfterImplied) && ((paramAttributeSet == null) || (paramAttributeSet.isDefined(IMPLIED)) || (paramTag == HTML.Tag.IMPLIED)))) {
          return false;
        }
        foundInsertTag(paramBoolean);
        if (!insertInsertTag) {
          return false;
        }
      }
      return true;
    }
    
    private boolean isInsertTag(HTML.Tag paramTag)
    {
      return insertTag == paramTag;
    }
    
    private void foundInsertTag(boolean paramBoolean)
    {
      foundInsertTag = true;
      if ((!insertAfterImplied) && ((popDepth > 0) || (pushDepth > 0))) {
        try
        {
          if ((offset == 0) || (!getText(offset - 1, 1).equals("\n")))
          {
            SimpleAttributeSet localSimpleAttributeSet = null;
            int j = 1;
            if (offset != 0)
            {
              localObject1 = getCharacterElement(offset - 1);
              AttributeSet localAttributeSet = ((Element)localObject1).getAttributes();
              if (localAttributeSet.isDefined(StyleConstants.ComposedTextAttribute))
              {
                j = 0;
              }
              else
              {
                Object localObject2 = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
                if ((localObject2 instanceof HTML.Tag))
                {
                  HTML.Tag localTag = (HTML.Tag)localObject2;
                  if ((localTag == HTML.Tag.IMG) || (localTag == HTML.Tag.HR) || (localTag == HTML.Tag.COMMENT) || ((localTag instanceof HTML.UnknownTag))) {
                    j = 0;
                  }
                }
              }
            }
            if (j == 0)
            {
              localSimpleAttributeSet = new SimpleAttributeSet();
              ((SimpleAttributeSet)localSimpleAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            }
            Object localObject1 = new DefaultStyledDocument.ElementSpec(localSimpleAttributeSet, (short)3, HTMLDocument.NEWLINE, 0, HTMLDocument.NEWLINE.length);
            if (j != 0) {
              ((DefaultStyledDocument.ElementSpec)localObject1).setDirection((short)4);
            }
            parseBuffer.addElement(localObject1);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
      for (int i = 0; i < popDepth; i++) {
        parseBuffer.addElement(new DefaultStyledDocument.ElementSpec(null, (short)2));
      }
      for (i = 0; i < pushDepth; i++)
      {
        DefaultStyledDocument.ElementSpec localElementSpec = new DefaultStyledDocument.ElementSpec(null, (short)1);
        localElementSpec.setDirection((short)5);
        parseBuffer.addElement(localElementSpec);
      }
      insertTagDepthDelta = (depthTo(Math.max(0, offset - 1)) - popDepth + pushDepth - inBlock);
      if (paramBoolean)
      {
        insertTagDepthDelta += 1;
      }
      else
      {
        insertTagDepthDelta -= 1;
        inParagraph = true;
        lastWasNewline = false;
      }
    }
    
    class AnchorAction
      extends HTMLDocument.HTMLReader.CharacterAction
    {
      AnchorAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        emptyAnchor = true;
        super.start(paramTag, paramMutableAttributeSet);
      }
      
      public void end(HTML.Tag paramTag)
      {
        if (emptyAnchor)
        {
          char[] arrayOfChar = new char[1];
          arrayOfChar[0] = '\n';
          addContent(arrayOfChar, 0, 1);
        }
        super.end(paramTag);
      }
    }
    
    class AreaAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      AreaAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        if (lastMap != null) {
          lastMap.addArea(paramMutableAttributeSet.copyAttributes());
        }
      }
      
      public void end(HTML.Tag paramTag) {}
    }
    
    class BaseAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      BaseAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        String str = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.HREF);
        if (str != null) {
          try
          {
            URL localURL = new URL(base, str);
            setBase(localURL);
            hasBaseTag = true;
          }
          catch (MalformedURLException localMalformedURLException) {}
        }
        baseTarget = ((String)paramMutableAttributeSet.getAttribute(HTML.Attribute.TARGET));
      }
    }
    
    public class BlockAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      public BlockAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        blockOpen(paramTag, paramMutableAttributeSet);
      }
      
      public void end(HTML.Tag paramTag)
      {
        blockClose(paramTag);
      }
    }
    
    public class CharacterAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      public CharacterAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        pushCharacterStyle();
        if (!foundInsertTag)
        {
          boolean bool = HTMLDocument.HTMLReader.this.canInsertTag(paramTag, paramMutableAttributeSet, false);
          if ((foundInsertTag) && (!inParagraph)) {
            inParagraph = (impliedP = 1);
          }
          if (!bool) {
            return;
          }
        }
        if (paramMutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
          paramMutableAttributeSet.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
        }
        charAttr.addAttribute(paramTag, paramMutableAttributeSet.copyAttributes());
        if (styleAttributes != null) {
          charAttr.addAttributes(styleAttributes);
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        popCharacterStyle();
      }
    }
    
    class ConvertAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      ConvertAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        pushCharacterStyle();
        if (!foundInsertTag)
        {
          boolean bool = HTMLDocument.HTMLReader.this.canInsertTag(paramTag, paramMutableAttributeSet, false);
          if ((foundInsertTag) && (!inParagraph)) {
            inParagraph = (impliedP = 1);
          }
          if (!bool) {
            return;
          }
        }
        if (paramMutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
          paramMutableAttributeSet.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
        }
        if (styleAttributes != null) {
          charAttr.addAttributes(styleAttributes);
        }
        charAttr.addAttribute(paramTag, paramMutableAttributeSet.copyAttributes());
        StyleSheet localStyleSheet = getStyleSheet();
        if (paramTag == HTML.Tag.B)
        {
          localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_WEIGHT, "bold");
        }
        else if (paramTag == HTML.Tag.I)
        {
          localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_STYLE, "italic");
        }
        else
        {
          Object localObject;
          String str1;
          if (paramTag == HTML.Tag.U)
          {
            localObject = charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
            str1 = "underline";
            str1 = localObject != null ? str1 + "," + localObject.toString() : str1;
            localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.TEXT_DECORATION, str1);
          }
          else if (paramTag == HTML.Tag.STRIKE)
          {
            localObject = charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
            str1 = "line-through";
            str1 = localObject != null ? str1 + "," + localObject.toString() : str1;
            localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.TEXT_DECORATION, str1);
          }
          else if (paramTag == HTML.Tag.SUP)
          {
            localObject = charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
            str1 = "sup";
            str1 = localObject != null ? str1 + "," + localObject.toString() : str1;
            localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.VERTICAL_ALIGN, str1);
          }
          else if (paramTag == HTML.Tag.SUB)
          {
            localObject = charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
            str1 = "sub";
            str1 = localObject != null ? str1 + "," + localObject.toString() : str1;
            localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.VERTICAL_ALIGN, str1);
          }
          else if (paramTag == HTML.Tag.FONT)
          {
            localObject = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.COLOR);
            if (localObject != null) {
              localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.COLOR, (String)localObject);
            }
            str1 = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.FACE);
            if (str1 != null) {
              localStyleSheet.addCSSAttribute(charAttr, CSS.Attribute.FONT_FAMILY, str1);
            }
            String str2 = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.SIZE);
            if (str2 != null) {
              localStyleSheet.addCSSAttributeFromHTML(charAttr, CSS.Attribute.FONT_SIZE, str2);
            }
          }
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        popCharacterStyle();
      }
    }
    
    public class FormAction
      extends HTMLDocument.HTMLReader.SpecialAction
    {
      Object selectModel;
      int optionCount;
      
      public FormAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        if (paramTag == HTML.Tag.INPUT)
        {
          String str = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.TYPE);
          if (str == null)
          {
            str = "text";
            paramMutableAttributeSet.addAttribute(HTML.Attribute.TYPE, "text");
          }
          setModel(str, paramMutableAttributeSet);
        }
        else if (paramTag == HTML.Tag.TEXTAREA)
        {
          inTextArea = true;
          textAreaDocument = new TextAreaDocument();
          paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, textAreaDocument);
        }
        else if (paramTag == HTML.Tag.SELECT)
        {
          int i = HTML.getIntegerAttributeValue(paramMutableAttributeSet, HTML.Attribute.SIZE, 1);
          int j = paramMutableAttributeSet.getAttribute(HTML.Attribute.MULTIPLE) != null ? 1 : 0;
          if ((i > 1) || (j != 0))
          {
            OptionListModel localOptionListModel = new OptionListModel();
            if (j != 0) {
              localOptionListModel.setSelectionMode(2);
            }
            selectModel = localOptionListModel;
          }
          else
          {
            selectModel = new OptionComboBoxModel();
          }
          paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, selectModel);
        }
        if (paramTag == HTML.Tag.OPTION)
        {
          option = new Option(paramMutableAttributeSet);
          Object localObject;
          if ((selectModel instanceof OptionListModel))
          {
            localObject = (OptionListModel)selectModel;
            ((OptionListModel)localObject).addElement(option);
            if (option.isSelected())
            {
              ((OptionListModel)localObject).addSelectionInterval(optionCount, optionCount);
              ((OptionListModel)localObject).setInitialSelection(optionCount);
            }
          }
          else if ((selectModel instanceof OptionComboBoxModel))
          {
            localObject = (OptionComboBoxModel)selectModel;
            ((OptionComboBoxModel)localObject).addElement(option);
            if (option.isSelected())
            {
              ((OptionComboBoxModel)localObject).setSelectedItem(option);
              ((OptionComboBoxModel)localObject).setInitialSelection(option);
            }
          }
          optionCount += 1;
        }
        else
        {
          super.start(paramTag, paramMutableAttributeSet);
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        if (paramTag == HTML.Tag.OPTION)
        {
          option = null;
        }
        else
        {
          if (paramTag == HTML.Tag.SELECT)
          {
            selectModel = null;
            optionCount = 0;
          }
          else if (paramTag == HTML.Tag.TEXTAREA)
          {
            inTextArea = false;
            textAreaDocument.storeInitialText();
          }
          super.end(paramTag);
        }
      }
      
      void setModel(String paramString, MutableAttributeSet paramMutableAttributeSet)
      {
        if ((paramString.equals("submit")) || (paramString.equals("reset")) || (paramString.equals("image")))
        {
          paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, new DefaultButtonModel());
        }
        else
        {
          Object localObject1;
          Object localObject2;
          if ((paramString.equals("text")) || (paramString.equals("password")))
          {
            int i = HTML.getIntegerAttributeValue(paramMutableAttributeSet, HTML.Attribute.MAXLENGTH, -1);
            if (i > 0) {
              localObject1 = new HTMLDocument.FixedLengthDocument(i);
            } else {
              localObject1 = new PlainDocument();
            }
            localObject2 = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.VALUE);
            try
            {
              ((Document)localObject1).insertString(0, (String)localObject2, null);
            }
            catch (BadLocationException localBadLocationException) {}
            paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, localObject1);
          }
          else if (paramString.equals("file"))
          {
            paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, new PlainDocument());
          }
          else if ((paramString.equals("checkbox")) || (paramString.equals("radio")))
          {
            JToggleButton.ToggleButtonModel localToggleButtonModel = new JToggleButton.ToggleButtonModel();
            if (paramString.equals("radio"))
            {
              localObject1 = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.NAME);
              if (radioButtonGroupsMap == null) {
                radioButtonGroupsMap = new HashMap();
              }
              localObject2 = (ButtonGroup)radioButtonGroupsMap.get(localObject1);
              if (localObject2 == null)
              {
                localObject2 = new ButtonGroup();
                radioButtonGroupsMap.put(localObject1, localObject2);
              }
              localToggleButtonModel.setGroup((ButtonGroup)localObject2);
            }
            boolean bool = paramMutableAttributeSet.getAttribute(HTML.Attribute.CHECKED) != null;
            localToggleButtonModel.setSelected(bool);
            paramMutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, localToggleButtonModel);
          }
        }
      }
    }
    
    private class FormTagAction
      extends HTMLDocument.HTMLReader.BlockAction
    {
      private FormTagAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        super.start(paramTag, paramMutableAttributeSet);
        radioButtonGroupsMap = new HashMap();
      }
      
      public void end(HTML.Tag paramTag)
      {
        super.end(paramTag);
        radioButtonGroupsMap = null;
      }
    }
    
    class HeadAction
      extends HTMLDocument.HTMLReader.BlockAction
    {
      HeadAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        inHead = true;
        if (((insertTag == null) && (!insertAfterImplied)) || (insertTag == HTML.Tag.HEAD) || ((insertAfterImplied) && ((foundInsertTag) || (!paramMutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED))))) {
          super.start(paramTag, paramMutableAttributeSet);
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        inHead = (inStyle = 0);
        if (styles != null)
        {
          boolean bool1 = isStyleCSS;
          int i = 0;
          int j = styles.size();
          while (i < j)
          {
            Object localObject = styles.elementAt(i);
            if (localObject == HTML.Tag.LINK)
            {
              handleLink((AttributeSet)styles.elementAt(++i));
              i++;
            }
            else
            {
              String str = (String)styles.elementAt(++i);
              boolean bool2 = str == null ? bool1 : str.equals("text/css");
              for (;;)
              {
                i++;
                if ((i >= j) || (!(styles.elementAt(i) instanceof String))) {
                  break;
                }
                if (bool2) {
                  addCSSRules((String)styles.elementAt(i));
                }
              }
            }
          }
        }
        if (((insertTag == null) && (!insertAfterImplied)) || (insertTag == HTML.Tag.HEAD) || ((insertAfterImplied) && (foundInsertTag))) {
          super.end(paramTag);
        }
      }
      
      boolean isEmpty(HTML.Tag paramTag)
      {
        return false;
      }
      
      private void handleLink(AttributeSet paramAttributeSet)
      {
        String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
        if (str1 == null) {
          str1 = getDefaultStyleSheetType();
        }
        if (str1.equals("text/css"))
        {
          String str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.REL);
          String str3 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TITLE);
          String str4 = (String)paramAttributeSet.getAttribute(HTML.Attribute.MEDIA);
          if (str4 == null) {
            str4 = "all";
          } else {
            str4 = str4.toLowerCase();
          }
          if (str2 != null)
          {
            str2 = str2.toLowerCase();
            if (((str4.indexOf("all") != -1) || (str4.indexOf("screen") != -1)) && ((str2.equals("stylesheet")) || ((str2.equals("alternate stylesheet")) && (str3.equals(defaultStyle))))) {
              linkCSSStyleSheet((String)paramAttributeSet.getAttribute(HTML.Attribute.HREF));
            }
          }
        }
      }
    }
    
    public class HiddenAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      public HiddenAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        addSpecialElement(paramTag, paramMutableAttributeSet);
      }
      
      public void end(HTML.Tag paramTag)
      {
        if (!isEmpty(paramTag))
        {
          SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
          localSimpleAttributeSet.addAttribute(HTML.Attribute.ENDTAG, "true");
          addSpecialElement(paramTag, localSimpleAttributeSet);
        }
      }
      
      boolean isEmpty(HTML.Tag paramTag)
      {
        return (paramTag != HTML.Tag.APPLET) && (paramTag != HTML.Tag.SCRIPT);
      }
    }
    
    public class IsindexAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      public IsindexAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        addSpecialElement(paramTag, paramMutableAttributeSet);
        blockClose(HTML.Tag.IMPLIED);
      }
    }
    
    class LinkAction
      extends HTMLDocument.HTMLReader.HiddenAction
    {
      LinkAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        String str = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.REL);
        if (str != null)
        {
          str = str.toLowerCase();
          if ((str.equals("stylesheet")) || (str.equals("alternate stylesheet")))
          {
            if (styles == null) {
              styles = new Vector(3);
            }
            styles.addElement(paramTag);
            styles.addElement(paramMutableAttributeSet.copyAttributes());
          }
        }
        super.start(paramTag, paramMutableAttributeSet);
      }
    }
    
    class MapAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      MapAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        lastMap = new Map((String)paramMutableAttributeSet.getAttribute(HTML.Attribute.NAME));
        addMap(lastMap);
      }
      
      public void end(HTML.Tag paramTag) {}
    }
    
    class MetaAction
      extends HTMLDocument.HTMLReader.HiddenAction
    {
      MetaAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        Object localObject = paramMutableAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV);
        if (localObject != null)
        {
          localObject = ((String)localObject).toLowerCase();
          if (localObject.equals("content-style-type"))
          {
            String str = (String)paramMutableAttributeSet.getAttribute(HTML.Attribute.CONTENT);
            setDefaultStyleSheetType(str);
            isStyleCSS = "text/css".equals(getDefaultStyleSheetType());
          }
          else if (localObject.equals("default-style"))
          {
            defaultStyle = ((String)paramMutableAttributeSet.getAttribute(HTML.Attribute.CONTENT));
          }
        }
        super.start(paramTag, paramMutableAttributeSet);
      }
      
      boolean isEmpty(HTML.Tag paramTag)
      {
        return true;
      }
    }
    
    class ObjectAction
      extends HTMLDocument.HTMLReader.SpecialAction
    {
      ObjectAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        if (paramTag == HTML.Tag.PARAM) {
          addParameter(paramMutableAttributeSet);
        } else {
          super.start(paramTag, paramMutableAttributeSet);
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        if (paramTag != HTML.Tag.PARAM) {
          super.end(paramTag);
        }
      }
      
      void addParameter(AttributeSet paramAttributeSet)
      {
        String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.NAME);
        String str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
        if ((str1 != null) && (str2 != null))
        {
          DefaultStyledDocument.ElementSpec localElementSpec = (DefaultStyledDocument.ElementSpec)parseBuffer.lastElement();
          MutableAttributeSet localMutableAttributeSet = (MutableAttributeSet)localElementSpec.getAttributes();
          localMutableAttributeSet.addAttribute(str1, str2);
        }
      }
    }
    
    public class ParagraphAction
      extends HTMLDocument.HTMLReader.BlockAction
    {
      public ParagraphAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        super.start(paramTag, paramMutableAttributeSet);
        inParagraph = true;
      }
      
      public void end(HTML.Tag paramTag)
      {
        super.end(paramTag);
        inParagraph = false;
      }
    }
    
    public class PreAction
      extends HTMLDocument.HTMLReader.BlockAction
    {
      public PreAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        inPre = true;
        blockOpen(paramTag, paramMutableAttributeSet);
        paramMutableAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
        blockOpen(HTML.Tag.IMPLIED, paramMutableAttributeSet);
      }
      
      public void end(HTML.Tag paramTag)
      {
        blockClose(HTML.Tag.IMPLIED);
        inPre = false;
        blockClose(paramTag);
      }
    }
    
    public class SpecialAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      public SpecialAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        addSpecialElement(paramTag, paramMutableAttributeSet);
      }
    }
    
    class StyleAction
      extends HTMLDocument.HTMLReader.TagAction
    {
      StyleAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        if (inHead)
        {
          if (styles == null) {
            styles = new Vector(3);
          }
          styles.addElement(paramTag);
          styles.addElement(paramMutableAttributeSet.getAttribute(HTML.Attribute.TYPE));
          inStyle = true;
        }
      }
      
      public void end(HTML.Tag paramTag)
      {
        inStyle = false;
      }
      
      boolean isEmpty(HTML.Tag paramTag)
      {
        return false;
      }
    }
    
    public class TagAction
    {
      public TagAction() {}
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet) {}
      
      public void end(HTML.Tag paramTag) {}
    }
    
    class TitleAction
      extends HTMLDocument.HTMLReader.HiddenAction
    {
      TitleAction()
      {
        super();
      }
      
      public void start(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet)
      {
        inTitle = true;
        super.start(paramTag, paramMutableAttributeSet);
      }
      
      public void end(HTML.Tag paramTag)
      {
        inTitle = false;
        super.end(paramTag);
      }
      
      boolean isEmpty(HTML.Tag paramTag)
      {
        return false;
      }
    }
  }
  
  public static abstract class Iterator
  {
    public Iterator() {}
    
    public abstract AttributeSet getAttributes();
    
    public abstract int getStartOffset();
    
    public abstract int getEndOffset();
    
    public abstract void next();
    
    public abstract boolean isValid();
    
    public abstract HTML.Tag getTag();
  }
  
  static class LeafIterator
    extends HTMLDocument.Iterator
  {
    private int endOffset;
    private HTML.Tag tag;
    private ElementIterator pos;
    
    LeafIterator(HTML.Tag paramTag, Document paramDocument)
    {
      tag = paramTag;
      pos = new ElementIterator(paramDocument);
      endOffset = 0;
      next();
    }
    
    public AttributeSet getAttributes()
    {
      Element localElement = pos.current();
      if (localElement != null)
      {
        AttributeSet localAttributeSet = (AttributeSet)localElement.getAttributes().getAttribute(tag);
        if (localAttributeSet == null) {
          localAttributeSet = localElement.getAttributes();
        }
        return localAttributeSet;
      }
      return null;
    }
    
    public int getStartOffset()
    {
      Element localElement = pos.current();
      if (localElement != null) {
        return localElement.getStartOffset();
      }
      return -1;
    }
    
    public int getEndOffset()
    {
      return endOffset;
    }
    
    public void next()
    {
      nextLeaf(pos);
      while (isValid())
      {
        Element localElement = pos.current();
        if (localElement.getStartOffset() >= endOffset)
        {
          AttributeSet localAttributeSet = pos.current().getAttributes();
          if ((localAttributeSet.isDefined(tag)) || (localAttributeSet.getAttribute(StyleConstants.NameAttribute) == tag))
          {
            setEndOffset();
            break;
          }
        }
        nextLeaf(pos);
      }
    }
    
    public HTML.Tag getTag()
    {
      return tag;
    }
    
    public boolean isValid()
    {
      return pos.current() != null;
    }
    
    void nextLeaf(ElementIterator paramElementIterator)
    {
      paramElementIterator.next();
      while (paramElementIterator.current() != null)
      {
        Element localElement = paramElementIterator.current();
        if (localElement.isLeaf()) {
          break;
        }
        paramElementIterator.next();
      }
    }
    
    void setEndOffset()
    {
      AttributeSet localAttributeSet1 = getAttributes();
      endOffset = pos.current().getEndOffset();
      ElementIterator localElementIterator = (ElementIterator)pos.clone();
      nextLeaf(localElementIterator);
      while (localElementIterator.current() != null)
      {
        Element localElement = localElementIterator.current();
        AttributeSet localAttributeSet2 = (AttributeSet)localElement.getAttributes().getAttribute(tag);
        if ((localAttributeSet2 == null) || (!localAttributeSet2.equals(localAttributeSet1))) {
          break;
        }
        endOffset = localElement.getEndOffset();
        nextLeaf(localElementIterator);
      }
    }
  }
  
  public class RunElement
    extends AbstractDocument.LeafElement
  {
    public RunElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramElement, paramAttributeSet, paramInt1, paramInt2);
    }
    
    public String getName()
    {
      Object localObject = getAttribute(StyleConstants.NameAttribute);
      if (localObject != null) {
        return localObject.toString();
      }
      return super.getName();
    }
    
    public AttributeSet getResolveParent()
    {
      return null;
    }
  }
  
  static class TaggedAttributeSet
    extends SimpleAttributeSet
  {
    TaggedAttributeSet() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HTMLDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */