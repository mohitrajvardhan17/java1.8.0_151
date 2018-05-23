package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JViewport;
import javax.swing.SizeRequirements;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Highlighter;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.StyledTextAction;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;

public class HTMLEditorKit
  extends StyledEditorKit
  implements Accessible
{
  private JEditorPane theEditor;
  public static final String DEFAULT_CSS = "default.css";
  private AccessibleContext accessibleContext;
  private static final Cursor MoveCursor = Cursor.getPredefinedCursor(12);
  private static final Cursor DefaultCursor = Cursor.getPredefinedCursor(0);
  private static final ViewFactory defaultFactory = new HTMLFactory();
  MutableAttributeSet input;
  private static final Object DEFAULT_STYLES_KEY = new Object();
  private LinkController linkHandler = new LinkController();
  private static Parser defaultParser = null;
  private Cursor defaultCursor = DefaultCursor;
  private Cursor linkCursor = MoveCursor;
  private boolean isAutoFormSubmission = true;
  public static final String BOLD_ACTION = "html-bold-action";
  public static final String ITALIC_ACTION = "html-italic-action";
  public static final String PARA_INDENT_LEFT = "html-para-indent-left";
  public static final String PARA_INDENT_RIGHT = "html-para-indent-right";
  public static final String FONT_CHANGE_BIGGER = "html-font-bigger";
  public static final String FONT_CHANGE_SMALLER = "html-font-smaller";
  public static final String COLOR_ACTION = "html-color-action";
  public static final String LOGICAL_STYLE_ACTION = "html-logical-style-action";
  public static final String IMG_ALIGN_TOP = "html-image-align-top";
  public static final String IMG_ALIGN_MIDDLE = "html-image-align-middle";
  public static final String IMG_ALIGN_BOTTOM = "html-image-align-bottom";
  public static final String IMG_BORDER = "html-image-border";
  private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";
  private static final String INSERT_UL_HTML = "<ul><li></li></ul>";
  private static final String INSERT_OL_HTML = "<ol><li></li></ol>";
  private static final String INSERT_HR_HTML = "<hr>";
  private static final String INSERT_PRE_HTML = "<pre></pre>";
  private static final NavigateLinkAction nextLinkAction = new NavigateLinkAction("next-link-action");
  private static final NavigateLinkAction previousLinkAction = new NavigateLinkAction("previous-link-action");
  private static final ActivateLinkAction activateLinkAction = new ActivateLinkAction("activate-link-action");
  private static final Action[] defaultActions = { new InsertHTMLTextAction("InsertTable", "<table border=1><tr><td></td></tr></table>", HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableRow", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TABLE, HTML.Tag.TR, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableDataCell", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertUnorderedList", "<ul><li></li></ul>", HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertUnorderedListItem", "<ul><li></li></ul>", HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertOrderedList", "<ol><li></li></ol>", HTML.Tag.BODY, HTML.Tag.OL), new InsertHTMLTextAction("InsertOrderedListItem", "<ol><li></li></ol>", HTML.Tag.OL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.OL), new InsertHRAction(), new InsertHTMLTextAction("InsertPre", "<pre></pre>", HTML.Tag.BODY, HTML.Tag.PRE), nextLinkAction, previousLinkAction, activateLinkAction, new BeginAction("caret-begin", false), new BeginAction("selection-begin", true) };
  private boolean foundLink = false;
  private int prevHypertextOffset = -1;
  private Object linkNavigationTag;
  
  public HTMLEditorKit() {}
  
  public String getContentType()
  {
    return "text/html";
  }
  
  public ViewFactory getViewFactory()
  {
    return defaultFactory;
  }
  
  public Document createDefaultDocument()
  {
    StyleSheet localStyleSheet1 = getStyleSheet();
    StyleSheet localStyleSheet2 = new StyleSheet();
    localStyleSheet2.addStyleSheet(localStyleSheet1);
    HTMLDocument localHTMLDocument = new HTMLDocument(localStyleSheet2);
    localHTMLDocument.setParser(getParser());
    localHTMLDocument.setAsynchronousLoadPriority(4);
    localHTMLDocument.setTokenThreshold(100);
    return localHTMLDocument;
  }
  
  private Parser ensureParser(HTMLDocument paramHTMLDocument)
    throws IOException
  {
    Parser localParser = paramHTMLDocument.getParser();
    if (localParser == null) {
      localParser = getParser();
    }
    if (localParser == null) {
      throw new IOException("Can't load parser");
    }
    return localParser;
  }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt)
    throws IOException, BadLocationException
  {
    if ((paramDocument instanceof HTMLDocument))
    {
      HTMLDocument localHTMLDocument = (HTMLDocument)paramDocument;
      if (paramInt > paramDocument.getLength()) {
        throw new BadLocationException("Invalid location", paramInt);
      }
      Parser localParser = ensureParser(localHTMLDocument);
      ParserCallback localParserCallback = localHTMLDocument.getReader(paramInt);
      Boolean localBoolean = (Boolean)paramDocument.getProperty("IgnoreCharsetDirective");
      localParser.parse(paramReader, localParserCallback, localBoolean == null ? false : localBoolean.booleanValue());
      localParserCallback.flush();
    }
    else
    {
      super.read(paramReader, paramDocument, paramInt);
    }
  }
  
  public void insertHTML(HTMLDocument paramHTMLDocument, int paramInt1, String paramString, int paramInt2, int paramInt3, HTML.Tag paramTag)
    throws BadLocationException, IOException
  {
    if (paramInt1 > paramHTMLDocument.getLength()) {
      throw new BadLocationException("Invalid location", paramInt1);
    }
    Parser localParser = ensureParser(paramHTMLDocument);
    ParserCallback localParserCallback = paramHTMLDocument.getReader(paramInt1, paramInt2, paramInt3, paramTag);
    Boolean localBoolean = (Boolean)paramHTMLDocument.getProperty("IgnoreCharsetDirective");
    localParser.parse(new StringReader(paramString), localParserCallback, localBoolean == null ? false : localBoolean.booleanValue());
    localParserCallback.flush();
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2)
    throws IOException, BadLocationException
  {
    Object localObject;
    if ((paramDocument instanceof HTMLDocument))
    {
      localObject = new HTMLWriter(paramWriter, (HTMLDocument)paramDocument, paramInt1, paramInt2);
      ((HTMLWriter)localObject).write();
    }
    else if ((paramDocument instanceof StyledDocument))
    {
      localObject = new MinimalHTMLWriter(paramWriter, (StyledDocument)paramDocument, paramInt1, paramInt2);
      ((MinimalHTMLWriter)localObject).write();
    }
    else
    {
      super.write(paramWriter, paramDocument, paramInt1, paramInt2);
    }
  }
  
  public void install(JEditorPane paramJEditorPane)
  {
    paramJEditorPane.addMouseListener(linkHandler);
    paramJEditorPane.addMouseMotionListener(linkHandler);
    paramJEditorPane.addCaretListener(nextLinkAction);
    super.install(paramJEditorPane);
    theEditor = paramJEditorPane;
  }
  
  public void deinstall(JEditorPane paramJEditorPane)
  {
    paramJEditorPane.removeMouseListener(linkHandler);
    paramJEditorPane.removeMouseMotionListener(linkHandler);
    paramJEditorPane.removeCaretListener(nextLinkAction);
    super.deinstall(paramJEditorPane);
    theEditor = null;
  }
  
  public void setStyleSheet(StyleSheet paramStyleSheet)
  {
    if (paramStyleSheet == null) {
      AppContext.getAppContext().remove(DEFAULT_STYLES_KEY);
    } else {
      AppContext.getAppContext().put(DEFAULT_STYLES_KEY, paramStyleSheet);
    }
  }
  
  public StyleSheet getStyleSheet()
  {
    AppContext localAppContext = AppContext.getAppContext();
    StyleSheet localStyleSheet = (StyleSheet)localAppContext.get(DEFAULT_STYLES_KEY);
    if (localStyleSheet == null)
    {
      localStyleSheet = new StyleSheet();
      localAppContext.put(DEFAULT_STYLES_KEY, localStyleSheet);
      try
      {
        InputStream localInputStream = getResourceAsStream("default.css");
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "ISO-8859-1"));
        localStyleSheet.loadRules(localBufferedReader, null);
        localBufferedReader.close();
      }
      catch (Throwable localThrowable) {}
    }
    return localStyleSheet;
  }
  
  static InputStream getResourceAsStream(String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InputStream run()
      {
        return HTMLEditorKit.class.getResourceAsStream(val$name);
      }
    });
  }
  
  public Action[] getActions()
  {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  protected void createInputAttributes(Element paramElement, MutableAttributeSet paramMutableAttributeSet)
  {
    paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
    paramMutableAttributeSet.addAttributes(paramElement.getAttributes());
    paramMutableAttributeSet.removeAttribute(StyleConstants.ComposedTextAttribute);
    Object localObject = paramMutableAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if ((localObject instanceof HTML.Tag))
    {
      HTML.Tag localTag = (HTML.Tag)localObject;
      if (localTag == HTML.Tag.IMG)
      {
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.SRC);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.HEIGHT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.WIDTH);
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      }
      else if ((localTag == HTML.Tag.HR) || (localTag == HTML.Tag.BR))
      {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      }
      else if (localTag == HTML.Tag.COMMENT)
      {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.COMMENT);
      }
      else if (localTag == HTML.Tag.INPUT)
      {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Tag.INPUT);
      }
      else if ((localTag instanceof HTML.UnknownTag))
      {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.ENDTAG);
      }
    }
  }
  
  public MutableAttributeSet getInputAttributes()
  {
    if (input == null) {
      input = getStyleSheet().addStyle(null, null);
    }
    return input;
  }
  
  public void setDefaultCursor(Cursor paramCursor)
  {
    defaultCursor = paramCursor;
  }
  
  public Cursor getDefaultCursor()
  {
    return defaultCursor;
  }
  
  public void setLinkCursor(Cursor paramCursor)
  {
    linkCursor = paramCursor;
  }
  
  public Cursor getLinkCursor()
  {
    return linkCursor;
  }
  
  public boolean isAutoFormSubmission()
  {
    return isAutoFormSubmission;
  }
  
  public void setAutoFormSubmission(boolean paramBoolean)
  {
    isAutoFormSubmission = paramBoolean;
  }
  
  public Object clone()
  {
    HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)super.clone();
    if (localHTMLEditorKit != null)
    {
      input = null;
      linkHandler = new LinkController();
    }
    return localHTMLEditorKit;
  }
  
  protected Parser getParser()
  {
    if (defaultParser == null) {
      try
      {
        Class localClass = Class.forName("javax.swing.text.html.parser.ParserDelegator");
        defaultParser = (Parser)localClass.newInstance();
      }
      catch (Throwable localThrowable) {}
    }
    return defaultParser;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (theEditor == null) {
      return null;
    }
    if (accessibleContext == null)
    {
      AccessibleHTML localAccessibleHTML = new AccessibleHTML(theEditor);
      accessibleContext = localAccessibleHTML.getAccessibleContext();
    }
    return accessibleContext;
  }
  
  private static Object getAttrValue(AttributeSet paramAttributeSet, HTML.Attribute paramAttribute)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      Object localObject2 = paramAttributeSet.getAttribute(localObject1);
      if ((localObject2 instanceof AttributeSet))
      {
        Object localObject3 = getAttrValue((AttributeSet)localObject2, paramAttribute);
        if (localObject3 != null) {
          return localObject3;
        }
      }
      else if (localObject1 == paramAttribute)
      {
        return localObject2;
      }
    }
    return null;
  }
  
  private static int getBodyElementStart(JTextComponent paramJTextComponent)
  {
    Element localElement1 = paramJTextComponent.getDocument().getRootElements()[0];
    for (int i = 0; i < localElement1.getElementCount(); i++)
    {
      Element localElement2 = localElement1.getElement(i);
      if ("body".equals(localElement2.getName())) {
        return localElement2.getStartOffset();
      }
    }
    return 0;
  }
  
  static class ActivateLinkAction
    extends TextAction
  {
    public ActivateLinkAction(String paramString)
    {
      super();
    }
    
    private void activateLink(String paramString, HTMLDocument paramHTMLDocument, JEditorPane paramJEditorPane, int paramInt)
    {
      try
      {
        URL localURL1 = (URL)paramHTMLDocument.getProperty("stream");
        URL localURL2 = new URL(localURL1, paramString);
        HyperlinkEvent localHyperlinkEvent = new HyperlinkEvent(paramJEditorPane, HyperlinkEvent.EventType.ACTIVATED, localURL2, localURL2.toExternalForm(), paramHTMLDocument.getCharacterElement(paramInt));
        paramJEditorPane.fireHyperlinkUpdate(localHyperlinkEvent);
      }
      catch (MalformedURLException localMalformedURLException) {}
    }
    
    private void doObjectAction(JEditorPane paramJEditorPane, Element paramElement)
    {
      View localView = getView(paramJEditorPane, paramElement);
      if ((localView != null) && ((localView instanceof ObjectView)))
      {
        Component localComponent = ((ObjectView)localView).getComponent();
        if ((localComponent != null) && ((localComponent instanceof Accessible)))
        {
          AccessibleContext localAccessibleContext = localComponent.getAccessibleContext();
          if (localAccessibleContext != null)
          {
            AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
            if (localAccessibleAction != null) {
              localAccessibleAction.doAccessibleAction(0);
            }
          }
        }
      }
    }
    
    private View getRootView(JEditorPane paramJEditorPane)
    {
      return paramJEditorPane.getUI().getRootView(paramJEditorPane);
    }
    
    private View getView(JEditorPane paramJEditorPane, Element paramElement)
    {
      Object localObject1 = lock(paramJEditorPane);
      try
      {
        View localView1 = getRootView(paramJEditorPane);
        int i = paramElement.getStartOffset();
        if (localView1 != null)
        {
          localView2 = getView(localView1, paramElement, i);
          return localView2;
        }
        View localView2 = null;
        return localView2;
      }
      finally
      {
        unlock(localObject1);
      }
    }
    
    private View getView(View paramView, Element paramElement, int paramInt)
    {
      if (paramView.getElement() == paramElement) {
        return paramView;
      }
      int i = paramView.getViewIndex(paramInt, Position.Bias.Forward);
      if ((i != -1) && (i < paramView.getViewCount())) {
        return getView(paramView.getView(i), paramElement, paramInt);
      }
      return null;
    }
    
    private Object lock(JEditorPane paramJEditorPane)
    {
      Document localDocument = paramJEditorPane.getDocument();
      if ((localDocument instanceof AbstractDocument))
      {
        ((AbstractDocument)localDocument).readLock();
        return localDocument;
      }
      return null;
    }
    
    private void unlock(Object paramObject)
    {
      if (paramObject != null) {
        ((AbstractDocument)paramObject).readUnlock();
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent.isEditable()) || (!(localJTextComponent instanceof JEditorPane))) {
        return;
      }
      JEditorPane localJEditorPane = (JEditorPane)localJTextComponent;
      Document localDocument = localJEditorPane.getDocument();
      if ((localDocument == null) || (!(localDocument instanceof HTMLDocument))) {
        return;
      }
      HTMLDocument localHTMLDocument = (HTMLDocument)localDocument;
      ElementIterator localElementIterator = new ElementIterator(localHTMLDocument);
      int i = localJEditorPane.getCaretPosition();
      Object localObject1 = null;
      Object localObject2 = null;
      Element localElement;
      while ((localElement = localElementIterator.next()) != null)
      {
        String str = localElement.getName();
        AttributeSet localAttributeSet = localElement.getAttributes();
        Object localObject3 = HTMLEditorKit.getAttrValue(localAttributeSet, HTML.Attribute.HREF);
        if (localObject3 != null)
        {
          if ((i >= localElement.getStartOffset()) && (i <= localElement.getEndOffset())) {
            activateLink((String)localObject3, localHTMLDocument, localJEditorPane, i);
          }
        }
        else if (str.equals(HTML.Tag.OBJECT.toString()))
        {
          Object localObject4 = HTMLEditorKit.getAttrValue(localAttributeSet, HTML.Attribute.CLASSID);
          if ((localObject4 != null) && (i >= localElement.getStartOffset()) && (i <= localElement.getEndOffset()))
          {
            doObjectAction(localJEditorPane, localElement);
            return;
          }
        }
      }
    }
  }
  
  static class BeginAction
    extends TextAction
  {
    private boolean select;
    
    BeginAction(String paramString, boolean paramBoolean)
    {
      super();
      select = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      int i = HTMLEditorKit.getBodyElementStart(localJTextComponent);
      if (localJTextComponent != null) {
        if (select) {
          localJTextComponent.moveCaretPosition(i);
        } else {
          localJTextComponent.setCaretPosition(i);
        }
      }
    }
  }
  
  public static class HTMLFactory
    implements ViewFactory
  {
    public HTMLFactory() {}
    
    public View create(Element paramElement)
    {
      AttributeSet localAttributeSet = paramElement.getAttributes();
      Object localObject1 = localAttributeSet.getAttribute("$ename");
      Object localObject2 = localObject1 != null ? null : localAttributeSet.getAttribute(StyleConstants.NameAttribute);
      if ((localObject2 instanceof HTML.Tag))
      {
        localObject3 = (HTML.Tag)localObject2;
        if (localObject3 == HTML.Tag.CONTENT) {
          return new InlineView(paramElement);
        }
        if (localObject3 == HTML.Tag.IMPLIED)
        {
          String str = (String)paramElement.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
          if ((str != null) && (str.equals("pre"))) {
            return new LineView(paramElement);
          }
          return new ParagraphView(paramElement);
        }
        if ((localObject3 == HTML.Tag.P) || (localObject3 == HTML.Tag.H1) || (localObject3 == HTML.Tag.H2) || (localObject3 == HTML.Tag.H3) || (localObject3 == HTML.Tag.H4) || (localObject3 == HTML.Tag.H5) || (localObject3 == HTML.Tag.H6) || (localObject3 == HTML.Tag.DT)) {
          return new ParagraphView(paramElement);
        }
        if ((localObject3 == HTML.Tag.MENU) || (localObject3 == HTML.Tag.DIR) || (localObject3 == HTML.Tag.UL) || (localObject3 == HTML.Tag.OL)) {
          return new ListView(paramElement);
        }
        if (localObject3 == HTML.Tag.BODY) {
          return new BodyBlockView(paramElement);
        }
        if (localObject3 == HTML.Tag.HTML) {
          return new BlockView(paramElement, 1);
        }
        if ((localObject3 == HTML.Tag.LI) || (localObject3 == HTML.Tag.CENTER) || (localObject3 == HTML.Tag.DL) || (localObject3 == HTML.Tag.DD) || (localObject3 == HTML.Tag.DIV) || (localObject3 == HTML.Tag.BLOCKQUOTE) || (localObject3 == HTML.Tag.PRE) || (localObject3 == HTML.Tag.FORM)) {
          return new BlockView(paramElement, 1);
        }
        if (localObject3 == HTML.Tag.NOFRAMES) {
          return new NoFramesView(paramElement, 1);
        }
        if (localObject3 == HTML.Tag.IMG) {
          return new ImageView(paramElement);
        }
        if (localObject3 == HTML.Tag.ISINDEX) {
          return new IsindexView(paramElement);
        }
        if (localObject3 == HTML.Tag.HR) {
          return new HRuleView(paramElement);
        }
        if (localObject3 == HTML.Tag.BR) {
          return new BRView(paramElement);
        }
        if (localObject3 == HTML.Tag.TABLE) {
          return new TableView(paramElement);
        }
        if ((localObject3 == HTML.Tag.INPUT) || (localObject3 == HTML.Tag.SELECT) || (localObject3 == HTML.Tag.TEXTAREA)) {
          return new FormView(paramElement);
        }
        if (localObject3 == HTML.Tag.OBJECT) {
          return new ObjectView(paramElement);
        }
        if (localObject3 == HTML.Tag.FRAMESET)
        {
          if (paramElement.getAttributes().isDefined(HTML.Attribute.ROWS)) {
            return new FrameSetView(paramElement, 1);
          }
          if (paramElement.getAttributes().isDefined(HTML.Attribute.COLS)) {
            return new FrameSetView(paramElement, 0);
          }
          throw new RuntimeException("Can't build a" + localObject3 + ", " + paramElement + ":no ROWS or COLS defined.");
        }
        if (localObject3 == HTML.Tag.FRAME) {
          return new FrameView(paramElement);
        }
        if ((localObject3 instanceof HTML.UnknownTag)) {
          return new HiddenTagView(paramElement);
        }
        if (localObject3 == HTML.Tag.COMMENT) {
          return new CommentView(paramElement);
        }
        if (localObject3 == HTML.Tag.HEAD) {
          new BlockView(paramElement, 0)
          {
            public float getPreferredSpan(int paramAnonymousInt)
            {
              return 0.0F;
            }
            
            public float getMinimumSpan(int paramAnonymousInt)
            {
              return 0.0F;
            }
            
            public float getMaximumSpan(int paramAnonymousInt)
            {
              return 0.0F;
            }
            
            protected void loadChildren(ViewFactory paramAnonymousViewFactory) {}
            
            public Shape modelToView(int paramAnonymousInt, Shape paramAnonymousShape, Position.Bias paramAnonymousBias)
              throws BadLocationException
            {
              return paramAnonymousShape;
            }
            
            public int getNextVisualPositionFrom(int paramAnonymousInt1, Position.Bias paramAnonymousBias, Shape paramAnonymousShape, int paramAnonymousInt2, Position.Bias[] paramAnonymousArrayOfBias)
            {
              return getElement().getEndOffset();
            }
          };
        }
        if ((localObject3 == HTML.Tag.TITLE) || (localObject3 == HTML.Tag.META) || (localObject3 == HTML.Tag.LINK) || (localObject3 == HTML.Tag.STYLE) || (localObject3 == HTML.Tag.SCRIPT) || (localObject3 == HTML.Tag.AREA) || (localObject3 == HTML.Tag.MAP) || (localObject3 == HTML.Tag.PARAM) || (localObject3 == HTML.Tag.APPLET)) {
          return new HiddenTagView(paramElement);
        }
      }
      Object localObject3 = localObject1 != null ? (String)localObject1 : paramElement.getName();
      if (localObject3 != null)
      {
        if (((String)localObject3).equals("content")) {
          return new LabelView(paramElement);
        }
        if (((String)localObject3).equals("paragraph")) {
          return new ParagraphView(paramElement);
        }
        if (((String)localObject3).equals("section")) {
          return new BoxView(paramElement, 1);
        }
        if (((String)localObject3).equals("component")) {
          return new ComponentView(paramElement);
        }
        if (((String)localObject3).equals("icon")) {
          return new IconView(paramElement);
        }
      }
      return new LabelView(paramElement);
    }
    
    static class BodyBlockView
      extends BlockView
      implements ComponentListener
    {
      private Reference<JViewport> cachedViewPort = null;
      private boolean isListening = false;
      private int viewVisibleWidth = Integer.MAX_VALUE;
      private int componentVisibleWidth = Integer.MAX_VALUE;
      
      public BodyBlockView(Element paramElement)
      {
        super(1);
      }
      
      protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
      {
        paramSizeRequirements = super.calculateMajorAxisRequirements(paramInt, paramSizeRequirements);
        maximum = Integer.MAX_VALUE;
        return paramSizeRequirements;
      }
      
      protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
      {
        Container localContainer1 = getContainer();
        Container localContainer2;
        JViewport localJViewport;
        if ((localContainer1 != null) && ((localContainer1 instanceof JEditorPane)) && ((localContainer2 = localContainer1.getParent()) != null) && ((localContainer2 instanceof JViewport)))
        {
          localJViewport = (JViewport)localContainer2;
          Object localObject;
          if (cachedViewPort != null)
          {
            localObject = (JViewport)cachedViewPort.get();
            if (localObject != null)
            {
              if (localObject != localJViewport) {
                ((JViewport)localObject).removeComponentListener(this);
              }
            }
            else {
              cachedViewPort = null;
            }
          }
          if (cachedViewPort == null)
          {
            localJViewport.addComponentListener(this);
            cachedViewPort = new WeakReference(localJViewport);
          }
          componentVisibleWidth = getExtentSizewidth;
          if (componentVisibleWidth > 0)
          {
            localObject = localContainer1.getInsets();
            viewVisibleWidth = (componentVisibleWidth - left - getLeftInset());
            paramInt1 = Math.min(paramInt1, viewVisibleWidth);
          }
        }
        else if (cachedViewPort != null)
        {
          localJViewport = (JViewport)cachedViewPort.get();
          if (localJViewport != null) {
            localJViewport.removeComponentListener(this);
          }
          cachedViewPort = null;
        }
        super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      }
      
      public void setParent(View paramView)
      {
        if ((paramView == null) && (cachedViewPort != null))
        {
          Object localObject;
          if ((localObject = cachedViewPort.get()) != null) {
            ((JComponent)localObject).removeComponentListener(this);
          }
          cachedViewPort = null;
        }
        super.setParent(paramView);
      }
      
      public void componentResized(ComponentEvent paramComponentEvent)
      {
        if (!(paramComponentEvent.getSource() instanceof JViewport)) {
          return;
        }
        JViewport localJViewport = (JViewport)paramComponentEvent.getSource();
        if (componentVisibleWidth != getExtentSizewidth)
        {
          Document localDocument = getDocument();
          if ((localDocument instanceof AbstractDocument))
          {
            AbstractDocument localAbstractDocument = (AbstractDocument)getDocument();
            localAbstractDocument.readLock();
            try
            {
              layoutChanged(0);
              preferenceChanged(null, true, true);
            }
            finally
            {
              localAbstractDocument.readUnlock();
            }
          }
        }
      }
      
      public void componentHidden(ComponentEvent paramComponentEvent) {}
      
      public void componentMoved(ComponentEvent paramComponentEvent) {}
      
      public void componentShown(ComponentEvent paramComponentEvent) {}
    }
  }
  
  public static abstract class HTMLTextAction
    extends StyledEditorKit.StyledTextAction
  {
    public HTMLTextAction(String paramString)
    {
      super();
    }
    
    protected HTMLDocument getHTMLDocument(JEditorPane paramJEditorPane)
    {
      Document localDocument = paramJEditorPane.getDocument();
      if ((localDocument instanceof HTMLDocument)) {
        return (HTMLDocument)localDocument;
      }
      throw new IllegalArgumentException("document must be HTMLDocument");
    }
    
    protected HTMLEditorKit getHTMLEditorKit(JEditorPane paramJEditorPane)
    {
      EditorKit localEditorKit = paramJEditorPane.getEditorKit();
      if ((localEditorKit instanceof HTMLEditorKit)) {
        return (HTMLEditorKit)localEditorKit;
      }
      throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
    }
    
    protected Element[] getElementsAt(HTMLDocument paramHTMLDocument, int paramInt)
    {
      return getElementsAt(paramHTMLDocument.getDefaultRootElement(), paramInt, 0);
    }
    
    private Element[] getElementsAt(Element paramElement, int paramInt1, int paramInt2)
    {
      if (paramElement.isLeaf())
      {
        arrayOfElement = new Element[paramInt2 + 1];
        arrayOfElement[paramInt2] = paramElement;
        return arrayOfElement;
      }
      Element[] arrayOfElement = getElementsAt(paramElement.getElement(paramElement.getElementIndex(paramInt1)), paramInt1, paramInt2 + 1);
      arrayOfElement[paramInt2] = paramElement;
      return arrayOfElement;
    }
    
    protected int elementCountToTag(HTMLDocument paramHTMLDocument, int paramInt, HTML.Tag paramTag)
    {
      int i = -1;
      Element localElement = paramHTMLDocument.getCharacterElement(paramInt);
      while ((localElement != null) && (localElement.getAttributes().getAttribute(StyleConstants.NameAttribute) != paramTag))
      {
        localElement = localElement.getParentElement();
        i++;
      }
      if (localElement == null) {
        return -1;
      }
      return i;
    }
    
    protected Element findElementMatchingTag(HTMLDocument paramHTMLDocument, int paramInt, HTML.Tag paramTag)
    {
      Element localElement1 = paramHTMLDocument.getDefaultRootElement();
      Element localElement2 = null;
      while (localElement1 != null)
      {
        if (localElement1.getAttributes().getAttribute(StyleConstants.NameAttribute) == paramTag) {
          localElement2 = localElement1;
        }
        localElement1 = localElement1.getElement(localElement1.getElementIndex(paramInt));
      }
      return localElement2;
    }
  }
  
  static class InsertHRAction
    extends HTMLEditorKit.InsertHTMLTextAction
  {
    InsertHRAction()
    {
      super("<hr>", null, HTML.Tag.IMPLIED, null, null, false);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        HTMLDocument localHTMLDocument = getHTMLDocument(localJEditorPane);
        int i = localJEditorPane.getSelectionStart();
        Element localElement = localHTMLDocument.getParagraphElement(i);
        if (localElement.getParentElement() != null)
        {
          parentTag = ((HTML.Tag)localElement.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute));
          super.actionPerformed(paramActionEvent);
        }
      }
    }
  }
  
  public static class InsertHTMLTextAction
    extends HTMLEditorKit.HTMLTextAction
  {
    protected String html;
    protected HTML.Tag parentTag;
    protected HTML.Tag addTag;
    protected HTML.Tag alternateParentTag;
    protected HTML.Tag alternateAddTag;
    boolean adjustSelection;
    
    public InsertHTMLTextAction(String paramString1, String paramString2, HTML.Tag paramTag1, HTML.Tag paramTag2)
    {
      this(paramString1, paramString2, paramTag1, paramTag2, null, null);
    }
    
    public InsertHTMLTextAction(String paramString1, String paramString2, HTML.Tag paramTag1, HTML.Tag paramTag2, HTML.Tag paramTag3, HTML.Tag paramTag4)
    {
      this(paramString1, paramString2, paramTag1, paramTag2, paramTag3, paramTag4, true);
    }
    
    InsertHTMLTextAction(String paramString1, String paramString2, HTML.Tag paramTag1, HTML.Tag paramTag2, HTML.Tag paramTag3, HTML.Tag paramTag4, boolean paramBoolean)
    {
      super();
      html = paramString2;
      parentTag = paramTag1;
      addTag = paramTag2;
      alternateParentTag = paramTag3;
      alternateAddTag = paramTag4;
      adjustSelection = paramBoolean;
    }
    
    protected void insertHTML(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, int paramInt1, String paramString, int paramInt2, int paramInt3, HTML.Tag paramTag)
    {
      try
      {
        getHTMLEditorKit(paramJEditorPane).insertHTML(paramHTMLDocument, paramInt1, paramString, paramInt2, paramInt3, paramTag);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException("Unable to insert: " + localIOException);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new RuntimeException("Unable to insert: " + localBadLocationException);
      }
    }
    
    protected void insertAtBoundary(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, int paramInt, Element paramElement, String paramString, HTML.Tag paramTag1, HTML.Tag paramTag2)
    {
      insertAtBoundry(paramJEditorPane, paramHTMLDocument, paramInt, paramElement, paramString, paramTag1, paramTag2);
    }
    
    @Deprecated
    protected void insertAtBoundry(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, int paramInt, Element paramElement, String paramString, HTML.Tag paramTag1, HTML.Tag paramTag2)
    {
      int i = paramInt == 0 ? 1 : 0;
      Object localObject;
      Element localElement;
      if ((paramInt > 0) || (paramElement == null))
      {
        for (localObject = paramHTMLDocument.getDefaultRootElement(); (localObject != null) && (((Element)localObject).getStartOffset() != paramInt) && (!((Element)localObject).isLeaf()); localObject = ((Element)localObject).getElement(((Element)localObject).getElementIndex(paramInt))) {}
        localElement = localObject != null ? ((Element)localObject).getParentElement() : null;
      }
      else
      {
        localElement = paramElement;
      }
      if (localElement != null)
      {
        int j = 0;
        int k = 0;
        if ((i != 0) && (paramElement != null)) {
          localObject = localElement;
        }
        while ((localObject != null) && (!((Element)localObject).isLeaf()))
        {
          localObject = ((Element)localObject).getElement(((Element)localObject).getElementIndex(paramInt));
          j++;
          continue;
          localObject = localElement;
          paramInt--;
          while ((localObject != null) && (!((Element)localObject).isLeaf()))
          {
            localObject = ((Element)localObject).getElement(((Element)localObject).getElementIndex(paramInt));
            j++;
          }
          localObject = localElement;
          paramInt++;
          while ((localObject != null) && (localObject != paramElement))
          {
            localObject = ((Element)localObject).getElement(((Element)localObject).getElementIndex(paramInt));
            k++;
          }
        }
        j = Math.max(0, j - 1);
        insertHTML(paramJEditorPane, paramHTMLDocument, paramInt, paramString, j, k, paramTag2);
      }
    }
    
    boolean insertIntoTag(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, int paramInt, HTML.Tag paramTag1, HTML.Tag paramTag2)
    {
      Element localElement = findElementMatchingTag(paramHTMLDocument, paramInt, paramTag1);
      if ((localElement != null) && (localElement.getStartOffset() == paramInt))
      {
        insertAtBoundary(paramJEditorPane, paramHTMLDocument, paramInt, localElement, html, paramTag1, paramTag2);
        return true;
      }
      if (paramInt > 0)
      {
        int i = elementCountToTag(paramHTMLDocument, paramInt - 1, paramTag1);
        if (i != -1)
        {
          insertHTML(paramJEditorPane, paramHTMLDocument, paramInt, html, i, 0, paramTag2);
          return true;
        }
      }
      return false;
    }
    
    void adjustSelection(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, int paramInt1, int paramInt2)
    {
      int i = paramHTMLDocument.getLength();
      if ((i != paramInt2) && (paramInt1 < i)) {
        if (paramInt1 > 0)
        {
          String str;
          try
          {
            str = paramHTMLDocument.getText(paramInt1 - 1, 1);
          }
          catch (BadLocationException localBadLocationException)
          {
            str = null;
          }
          if ((str != null) && (str.length() > 0) && (str.charAt(0) == '\n')) {
            paramJEditorPane.select(paramInt1, paramInt1);
          } else {
            paramJEditorPane.select(paramInt1 + 1, paramInt1 + 1);
          }
        }
        else
        {
          paramJEditorPane.select(1, 1);
        }
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JEditorPane localJEditorPane = getEditor(paramActionEvent);
      if (localJEditorPane != null)
      {
        HTMLDocument localHTMLDocument = getHTMLDocument(localJEditorPane);
        int i = localJEditorPane.getSelectionStart();
        int j = localHTMLDocument.getLength();
        boolean bool;
        if ((!insertIntoTag(localJEditorPane, localHTMLDocument, i, parentTag, addTag)) && (alternateParentTag != null)) {
          bool = insertIntoTag(localJEditorPane, localHTMLDocument, i, alternateParentTag, alternateAddTag);
        } else {
          bool = true;
        }
        if ((adjustSelection) && (bool)) {
          adjustSelection(localJEditorPane, localHTMLDocument, i, j);
        }
      }
    }
  }
  
  public static class LinkController
    extends MouseAdapter
    implements MouseMotionListener, Serializable
  {
    private Element curElem = null;
    private boolean curElemImage = false;
    private String href = null;
    private transient Position.Bias[] bias = new Position.Bias[1];
    private int curOffset;
    
    public LinkController() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      JEditorPane localJEditorPane = (JEditorPane)paramMouseEvent.getSource();
      if ((!localJEditorPane.isEditable()) && (localJEditorPane.isEnabled()) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
      {
        Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
        int i = localJEditorPane.viewToModel(localPoint);
        if (i >= 0) {
          activateLink(i, localJEditorPane, paramMouseEvent);
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      JEditorPane localJEditorPane = (JEditorPane)paramMouseEvent.getSource();
      if (!localJEditorPane.isEnabled()) {
        return;
      }
      HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)localJEditorPane.getEditorKit();
      int i = 1;
      Cursor localCursor = localHTMLEditorKit.getDefaultCursor();
      if (!localJEditorPane.isEditable())
      {
        Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
        int j = localJEditorPane.getUI().viewToModel(localJEditorPane, localPoint, bias);
        if ((bias[0] == Position.Bias.Backward) && (j > 0)) {
          j--;
        }
        if ((j >= 0) && ((localJEditorPane.getDocument() instanceof HTMLDocument)))
        {
          HTMLDocument localHTMLDocument = (HTMLDocument)localJEditorPane.getDocument();
          Element localElement1 = localHTMLDocument.getCharacterElement(j);
          if (!doesElementContainLocation(localJEditorPane, localElement1, j, paramMouseEvent.getX(), paramMouseEvent.getY())) {
            localElement1 = null;
          }
          if ((curElem != localElement1) || (curElemImage))
          {
            Element localElement2 = curElem;
            curElem = localElement1;
            String str = null;
            curElemImage = false;
            if (localElement1 != null)
            {
              AttributeSet localAttributeSet1 = localElement1.getAttributes();
              AttributeSet localAttributeSet2 = (AttributeSet)localAttributeSet1.getAttribute(HTML.Tag.A);
              if (localAttributeSet2 == null)
              {
                curElemImage = (localAttributeSet1.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMG);
                if (curElemImage) {
                  str = getMapHREF(localJEditorPane, localHTMLDocument, localElement1, localAttributeSet1, j, paramMouseEvent.getX(), paramMouseEvent.getY());
                }
              }
              else
              {
                str = (String)localAttributeSet2.getAttribute(HTML.Attribute.HREF);
              }
            }
            if (str != href)
            {
              fireEvents(localJEditorPane, localHTMLDocument, str, localElement2, paramMouseEvent);
              href = str;
              if (str != null) {
                localCursor = localHTMLEditorKit.getLinkCursor();
              }
            }
            else
            {
              i = 0;
            }
          }
          else
          {
            i = 0;
          }
          curOffset = j;
        }
      }
      if ((i != 0) && (localJEditorPane.getCursor() != localCursor)) {
        localJEditorPane.setCursor(localCursor);
      }
    }
    
    private String getMapHREF(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, int paramInt3)
    {
      Object localObject = paramAttributeSet.getAttribute(HTML.Attribute.USEMAP);
      if ((localObject != null) && ((localObject instanceof String)))
      {
        Map localMap = paramHTMLDocument.getMap((String)localObject);
        if ((localMap != null) && (paramInt1 < paramHTMLDocument.getLength()))
        {
          TextUI localTextUI = paramJEditorPane.getUI();
          Rectangle localRectangle1;
          try
          {
            Rectangle localRectangle2 = localTextUI.modelToView(paramJEditorPane, paramInt1, Position.Bias.Forward);
            Rectangle localRectangle3 = localTextUI.modelToView(paramJEditorPane, paramInt1 + 1, Position.Bias.Backward);
            localRectangle1 = localRectangle2.getBounds();
            localRectangle1.add((localRectangle3 instanceof Rectangle) ? (Rectangle)localRectangle3 : localRectangle3.getBounds());
          }
          catch (BadLocationException localBadLocationException)
          {
            localRectangle1 = null;
          }
          if (localRectangle1 != null)
          {
            AttributeSet localAttributeSet = localMap.getArea(paramInt2 - x, paramInt3 - y, width, height);
            if (localAttributeSet != null) {
              return (String)localAttributeSet.getAttribute(HTML.Attribute.HREF);
            }
          }
        }
      }
      return null;
    }
    
    private boolean doesElementContainLocation(JEditorPane paramJEditorPane, Element paramElement, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((paramElement != null) && (paramInt1 > 0) && (paramElement.getStartOffset() == paramInt1)) {
        try
        {
          TextUI localTextUI = paramJEditorPane.getUI();
          Rectangle localRectangle1 = localTextUI.modelToView(paramJEditorPane, paramInt1, Position.Bias.Forward);
          if (localRectangle1 == null) {
            return false;
          }
          Rectangle localRectangle2 = (localRectangle1 instanceof Rectangle) ? (Rectangle)localRectangle1 : localRectangle1.getBounds();
          Rectangle localRectangle3 = localTextUI.modelToView(paramJEditorPane, paramElement.getEndOffset(), Position.Bias.Backward);
          if (localRectangle3 != null)
          {
            Rectangle localRectangle4 = (localRectangle3 instanceof Rectangle) ? (Rectangle)localRectangle3 : localRectangle3.getBounds();
            localRectangle2.add(localRectangle4);
          }
          return localRectangle2.contains(paramInt2, paramInt3);
        }
        catch (BadLocationException localBadLocationException) {}
      }
      return true;
    }
    
    protected void activateLink(int paramInt, JEditorPane paramJEditorPane)
    {
      activateLink(paramInt, paramJEditorPane, null);
    }
    
    void activateLink(int paramInt, JEditorPane paramJEditorPane, MouseEvent paramMouseEvent)
    {
      Document localDocument = paramJEditorPane.getDocument();
      if ((localDocument instanceof HTMLDocument))
      {
        HTMLDocument localHTMLDocument = (HTMLDocument)localDocument;
        Element localElement = localHTMLDocument.getCharacterElement(paramInt);
        AttributeSet localAttributeSet1 = localElement.getAttributes();
        AttributeSet localAttributeSet2 = (AttributeSet)localAttributeSet1.getAttribute(HTML.Tag.A);
        HyperlinkEvent localHyperlinkEvent = null;
        int i = -1;
        int j = -1;
        if (paramMouseEvent != null)
        {
          i = paramMouseEvent.getX();
          j = paramMouseEvent.getY();
        }
        if (localAttributeSet2 == null) {
          href = getMapHREF(paramJEditorPane, localHTMLDocument, localElement, localAttributeSet1, paramInt, i, j);
        } else {
          href = ((String)localAttributeSet2.getAttribute(HTML.Attribute.HREF));
        }
        if (href != null) {
          localHyperlinkEvent = createHyperlinkEvent(paramJEditorPane, localHTMLDocument, href, localAttributeSet2, localElement, paramMouseEvent);
        }
        if (localHyperlinkEvent != null) {
          paramJEditorPane.fireHyperlinkUpdate(localHyperlinkEvent);
        }
      }
    }
    
    HyperlinkEvent createHyperlinkEvent(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, String paramString, AttributeSet paramAttributeSet, Element paramElement, MouseEvent paramMouseEvent)
    {
      URL localURL1;
      String str1;
      try
      {
        URL localURL2 = paramHTMLDocument.getBase();
        localURL1 = new URL(localURL2, paramString);
        if ((paramString != null) && ("file".equals(localURL1.getProtocol())) && (paramString.startsWith("#")))
        {
          str1 = localURL2.getFile();
          String str2 = localURL1.getFile();
          if ((str1 != null) && (str2 != null) && (!str2.startsWith(str1))) {
            localURL1 = new URL(localURL2, str1 + paramString);
          }
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        localURL1 = null;
      }
      Object localObject;
      if (!paramHTMLDocument.isFrameDocument())
      {
        localObject = new HyperlinkEvent(paramJEditorPane, HyperlinkEvent.EventType.ACTIVATED, localURL1, paramString, paramElement, paramMouseEvent);
      }
      else
      {
        str1 = paramAttributeSet != null ? (String)paramAttributeSet.getAttribute(HTML.Attribute.TARGET) : null;
        if ((str1 == null) || (str1.equals(""))) {
          str1 = paramHTMLDocument.getBaseTarget();
        }
        if ((str1 == null) || (str1.equals(""))) {
          str1 = "_self";
        }
        localObject = new HTMLFrameHyperlinkEvent(paramJEditorPane, HyperlinkEvent.EventType.ACTIVATED, localURL1, paramString, paramElement, paramMouseEvent, str1);
      }
      return (HyperlinkEvent)localObject;
    }
    
    void fireEvents(JEditorPane paramJEditorPane, HTMLDocument paramHTMLDocument, String paramString, Element paramElement, MouseEvent paramMouseEvent)
    {
      URL localURL;
      if (href != null)
      {
        try
        {
          localURL = new URL(paramHTMLDocument.getBase(), href);
        }
        catch (MalformedURLException localMalformedURLException1)
        {
          localURL = null;
        }
        HyperlinkEvent localHyperlinkEvent1 = new HyperlinkEvent(paramJEditorPane, HyperlinkEvent.EventType.EXITED, localURL, href, paramElement, paramMouseEvent);
        paramJEditorPane.fireHyperlinkUpdate(localHyperlinkEvent1);
      }
      if (paramString != null)
      {
        try
        {
          localURL = new URL(paramHTMLDocument.getBase(), paramString);
        }
        catch (MalformedURLException localMalformedURLException2)
        {
          localURL = null;
        }
        HyperlinkEvent localHyperlinkEvent2 = new HyperlinkEvent(paramJEditorPane, HyperlinkEvent.EventType.ENTERED, localURL, paramString, curElem, paramMouseEvent);
        paramJEditorPane.fireHyperlinkUpdate(localHyperlinkEvent2);
      }
    }
  }
  
  static class NavigateLinkAction
    extends TextAction
    implements CaretListener
  {
    private static final FocusHighlightPainter focusPainter = new FocusHighlightPainter(null);
    private final boolean focusBack;
    
    public NavigateLinkAction(String paramString)
    {
      super();
      focusBack = "previous-link-action".equals(paramString);
    }
    
    public void caretUpdate(CaretEvent paramCaretEvent)
    {
      Object localObject = paramCaretEvent.getSource();
      if ((localObject instanceof JTextComponent))
      {
        JTextComponent localJTextComponent = (JTextComponent)localObject;
        HTMLEditorKit localHTMLEditorKit = getHTMLEditorKit(localJTextComponent);
        if ((localHTMLEditorKit != null) && (foundLink))
        {
          foundLink = false;
          localJTextComponent.getAccessibleContext().firePropertyChange("AccessibleHypertextOffset", Integer.valueOf(prevHypertextOffset), Integer.valueOf(paramCaretEvent.getDot()));
        }
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getTextComponent(paramActionEvent);
      if ((localJTextComponent == null) || (localJTextComponent.isEditable())) {
        return;
      }
      Document localDocument = localJTextComponent.getDocument();
      HTMLEditorKit localHTMLEditorKit = getHTMLEditorKit(localJTextComponent);
      if ((localDocument == null) || (localHTMLEditorKit == null)) {
        return;
      }
      ElementIterator localElementIterator = new ElementIterator(localDocument);
      int i = localJTextComponent.getCaretPosition();
      int j = -1;
      label62:
      Element localElement;
      for (int k = -1; (localElement = localElementIterator.next()) != null; k = localElement.getEndOffset())
      {
        String str = localElement.getName();
        AttributeSet localAttributeSet = localElement.getAttributes();
        Object localObject = HTMLEditorKit.getAttrValue(localAttributeSet, HTML.Attribute.HREF);
        if ((!str.equals(HTML.Tag.OBJECT.toString())) && (localObject == null)) {
          break label62;
        }
        int m = localElement.getStartOffset();
        if (focusBack)
        {
          if ((m >= i) && (j >= 0))
          {
            foundLink = true;
            localJTextComponent.setCaretPosition(j);
            moveCaretPosition(localJTextComponent, localHTMLEditorKit, j, k);
            prevHypertextOffset = j;
          }
        }
        else if (m > i)
        {
          foundLink = true;
          localJTextComponent.setCaretPosition(m);
          moveCaretPosition(localJTextComponent, localHTMLEditorKit, m, localElement.getEndOffset());
          prevHypertextOffset = m;
          return;
        }
        j = localElement.getStartOffset();
      }
      if ((focusBack) && (j >= 0))
      {
        foundLink = true;
        localJTextComponent.setCaretPosition(j);
        moveCaretPosition(localJTextComponent, localHTMLEditorKit, j, k);
        prevHypertextOffset = j;
      }
    }
    
    private void moveCaretPosition(JTextComponent paramJTextComponent, HTMLEditorKit paramHTMLEditorKit, int paramInt1, int paramInt2)
    {
      Highlighter localHighlighter = paramJTextComponent.getHighlighter();
      if (localHighlighter != null)
      {
        int i = Math.min(paramInt2, paramInt1);
        int j = Math.max(paramInt2, paramInt1);
        try
        {
          if (linkNavigationTag != null) {
            localHighlighter.changeHighlight(linkNavigationTag, i, j);
          } else {
            linkNavigationTag = localHighlighter.addHighlight(i, j, focusPainter);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
    
    private HTMLEditorKit getHTMLEditorKit(JTextComponent paramJTextComponent)
    {
      if ((paramJTextComponent instanceof JEditorPane))
      {
        EditorKit localEditorKit = ((JEditorPane)paramJTextComponent).getEditorKit();
        if ((localEditorKit instanceof HTMLEditorKit)) {
          return (HTMLEditorKit)localEditorKit;
        }
      }
      return null;
    }
    
    static class FocusHighlightPainter
      extends DefaultHighlighter.DefaultHighlightPainter
    {
      FocusHighlightPainter(Color paramColor)
      {
        super();
      }
      
      public Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
      {
        Color localColor = getColor();
        if (localColor == null) {
          paramGraphics.setColor(paramJTextComponent.getSelectionColor());
        } else {
          paramGraphics.setColor(localColor);
        }
        Object localObject;
        if ((paramInt1 == paramView.getStartOffset()) && (paramInt2 == paramView.getEndOffset()))
        {
          if ((paramShape instanceof Rectangle)) {
            localObject = (Rectangle)paramShape;
          } else {
            localObject = paramShape.getBounds();
          }
          paramGraphics.drawRect(x, y, width - 1, height);
          return (Shape)localObject;
        }
        try
        {
          localObject = paramView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
          Rectangle localRectangle = (localObject instanceof Rectangle) ? (Rectangle)localObject : ((Shape)localObject).getBounds();
          paramGraphics.drawRect(x, y, width - 1, height);
          return localRectangle;
        }
        catch (BadLocationException localBadLocationException) {}
        return null;
      }
    }
  }
  
  public static abstract class Parser
  {
    public Parser() {}
    
    public abstract void parse(Reader paramReader, HTMLEditorKit.ParserCallback paramParserCallback, boolean paramBoolean)
      throws IOException;
  }
  
  public static class ParserCallback
  {
    public static final Object IMPLIED = "_implied_";
    
    public ParserCallback() {}
    
    public void flush()
      throws BadLocationException
    {}
    
    public void handleText(char[] paramArrayOfChar, int paramInt) {}
    
    public void handleComment(char[] paramArrayOfChar, int paramInt) {}
    
    public void handleStartTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt) {}
    
    public void handleEndTag(HTML.Tag paramTag, int paramInt) {}
    
    public void handleSimpleTag(HTML.Tag paramTag, MutableAttributeSet paramMutableAttributeSet, int paramInt) {}
    
    public void handleError(String paramString, int paramInt) {}
    
    public void handleEndOfLineString(String paramString) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HTMLEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */