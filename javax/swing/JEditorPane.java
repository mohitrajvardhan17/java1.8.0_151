package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Caret;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.CompositeView;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.AccessibleJTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.text.html.HTMLEditorKit;

public class JEditorPane
  extends JTextComponent
{
  private SwingWorker<URL, Object> pageLoader;
  private EditorKit kit;
  private boolean isUserSetEditorKit;
  private Hashtable<String, Object> pageProperties;
  static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
  private Hashtable<String, EditorKit> typeHandlers;
  private static final Object kitRegistryKey = new StringBuffer("JEditorPane.kitRegistry");
  private static final Object kitTypeRegistryKey = new StringBuffer("JEditorPane.kitTypeRegistry");
  private static final Object kitLoaderRegistryKey = new StringBuffer("JEditorPane.kitLoaderRegistry");
  private static final String uiClassID = "EditorPaneUI";
  public static final String W3C_LENGTH_UNITS = "JEditorPane.w3cLengthUnits";
  public static final String HONOR_DISPLAY_PROPERTIES = "JEditorPane.honorDisplayProperties";
  static final Map<String, String> defaultEditorKitMap = new HashMap(0);
  
  public JEditorPane()
  {
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()
    {
      public Component getComponentAfter(Container paramAnonymousContainer, Component paramAnonymousComponent)
      {
        if ((paramAnonymousContainer != JEditorPane.this) || ((!isEditable()) && (getComponentCount() > 0))) {
          return super.getComponentAfter(paramAnonymousContainer, paramAnonymousComponent);
        }
        Container localContainer = getFocusCycleRootAncestor();
        return localContainer != null ? localContainer.getFocusTraversalPolicy().getComponentAfter(localContainer, JEditorPane.this) : null;
      }
      
      public Component getComponentBefore(Container paramAnonymousContainer, Component paramAnonymousComponent)
      {
        if ((paramAnonymousContainer != JEditorPane.this) || ((!isEditable()) && (getComponentCount() > 0))) {
          return super.getComponentBefore(paramAnonymousContainer, paramAnonymousComponent);
        }
        Container localContainer = getFocusCycleRootAncestor();
        return localContainer != null ? localContainer.getFocusTraversalPolicy().getComponentBefore(localContainer, JEditorPane.this) : null;
      }
      
      public Component getDefaultComponent(Container paramAnonymousContainer)
      {
        return (paramAnonymousContainer != JEditorPane.this) || ((!isEditable()) && (getComponentCount() > 0)) ? super.getDefaultComponent(paramAnonymousContainer) : null;
      }
      
      protected boolean accept(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent != JEditorPane.this ? super.accept(paramAnonymousComponent) : false;
      }
    });
    LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
    LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
  }
  
  public JEditorPane(URL paramURL)
    throws IOException
  {
    this();
    setPage(paramURL);
  }
  
  public JEditorPane(String paramString)
    throws IOException
  {
    this();
    setPage(paramString);
  }
  
  public JEditorPane(String paramString1, String paramString2)
  {
    this();
    setContentType(paramString1);
    setText(paramString2);
  }
  
  public synchronized void addHyperlinkListener(HyperlinkListener paramHyperlinkListener)
  {
    listenerList.add(HyperlinkListener.class, paramHyperlinkListener);
  }
  
  public synchronized void removeHyperlinkListener(HyperlinkListener paramHyperlinkListener)
  {
    listenerList.remove(HyperlinkListener.class, paramHyperlinkListener);
  }
  
  public synchronized HyperlinkListener[] getHyperlinkListeners()
  {
    return (HyperlinkListener[])listenerList.getListeners(HyperlinkListener.class);
  }
  
  public void fireHyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == HyperlinkListener.class) {
        ((HyperlinkListener)arrayOfObject[(i + 1)]).hyperlinkUpdate(paramHyperlinkEvent);
      }
    }
  }
  
  public void setPage(URL paramURL)
    throws IOException
  {
    if (paramURL == null) {
      throw new IOException("invalid url");
    }
    URL localURL = getPage();
    if ((!paramURL.equals(localURL)) && (paramURL.getRef() == null)) {
      scrollRectToVisible(new Rectangle(0, 0, 1, 1));
    }
    int i = 0;
    Object localObject1 = getPostData();
    if ((localURL == null) || (!localURL.sameFile(paramURL)) || (localObject1 != null))
    {
      int j = getAsynchronousLoadPriority(getDocument());
      if (j < 0)
      {
        InputStream localInputStream = getStream(paramURL);
        if (kit != null)
        {
          Document localDocument = initializeModel(kit, paramURL);
          j = getAsynchronousLoadPriority(localDocument);
          if (j >= 0)
          {
            setDocument(localDocument);
            synchronized (this)
            {
              pageLoader = new PageLoader(localDocument, localInputStream, localURL, paramURL);
              pageLoader.execute();
            }
            return;
          }
          read(localInputStream, localDocument);
          setDocument(localDocument);
          i = 1;
        }
      }
      else
      {
        if (pageLoader != null) {
          pageLoader.cancel(true);
        }
        pageLoader = new PageLoader(null, null, localURL, paramURL);
        pageLoader.execute();
        return;
      }
    }
    final String str = paramURL.getRef();
    if (str != null)
    {
      if (i == 0) {
        scrollToReference(str);
      } else {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            scrollToReference(str);
          }
        });
      }
      getDocument().putProperty("stream", paramURL);
    }
    firePropertyChange("page", localURL, paramURL);
  }
  
  private Document initializeModel(EditorKit paramEditorKit, URL paramURL)
  {
    Document localDocument = paramEditorKit.createDefaultDocument();
    if (pageProperties != null)
    {
      Enumeration localEnumeration = pageProperties.keys();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        localDocument.putProperty(str, pageProperties.get(str));
      }
      pageProperties.clear();
    }
    if (localDocument.getProperty("stream") == null) {
      localDocument.putProperty("stream", paramURL);
    }
    return localDocument;
  }
  
  private int getAsynchronousLoadPriority(Document paramDocument)
  {
    return (paramDocument instanceof AbstractDocument) ? ((AbstractDocument)paramDocument).getAsynchronousLoadPriority() : -1;
  }
  
  public void read(InputStream paramInputStream, Object paramObject)
    throws IOException
  {
    Object localObject;
    if (((paramObject instanceof HTMLDocument)) && ((kit instanceof HTMLEditorKit)))
    {
      localObject = (HTMLDocument)paramObject;
      setDocument((Document)localObject);
      read(paramInputStream, (Document)localObject);
    }
    else
    {
      localObject = (String)getClientProperty("charset");
      InputStreamReader localInputStreamReader = localObject != null ? new InputStreamReader(paramInputStream, (String)localObject) : new InputStreamReader(paramInputStream);
      super.read(localInputStreamReader, paramObject);
    }
  }
  
  void read(InputStream paramInputStream, Document paramDocument)
    throws IOException
  {
    if (!Boolean.TRUE.equals(paramDocument.getProperty("IgnoreCharsetDirective")))
    {
      paramInputStream = new BufferedInputStream(paramInputStream, 10240);
      paramInputStream.mark(10240);
    }
    try
    {
      String str = (String)getClientProperty("charset");
      localObject = str != null ? new InputStreamReader(paramInputStream, str) : new InputStreamReader(paramInputStream);
      kit.read((Reader)localObject, paramDocument, 0);
    }
    catch (BadLocationException localBadLocationException1)
    {
      throw new IOException(localBadLocationException1.getMessage());
    }
    catch (ChangedCharSetException localChangedCharSetException)
    {
      Object localObject = localChangedCharSetException.getCharSetSpec();
      if (localChangedCharSetException.keyEqualsCharSet()) {
        putClientProperty("charset", localObject);
      } else {
        setCharsetFromContentTypeParameters((String)localObject);
      }
      try
      {
        paramInputStream.reset();
      }
      catch (IOException localIOException)
      {
        paramInputStream.close();
        URL localURL = (URL)paramDocument.getProperty("stream");
        if (localURL != null)
        {
          URLConnection localURLConnection = localURL.openConnection();
          paramInputStream = localURLConnection.getInputStream();
        }
        else
        {
          throw localChangedCharSetException;
        }
      }
      try
      {
        paramDocument.remove(0, paramDocument.getLength());
      }
      catch (BadLocationException localBadLocationException2) {}
      paramDocument.putProperty("IgnoreCharsetDirective", Boolean.valueOf(true));
      read(paramInputStream, paramDocument);
    }
  }
  
  protected InputStream getStream(URL paramURL)
    throws IOException
  {
    final URLConnection localURLConnection = paramURL.openConnection();
    if ((localURLConnection instanceof HttpURLConnection))
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
      localHttpURLConnection.setInstanceFollowRedirects(false);
      Object localObject = getPostData();
      if (localObject != null) {
        handlePostData(localHttpURLConnection, localObject);
      }
      int i = localHttpURLConnection.getResponseCode();
      int j = (i >= 300) && (i <= 399) ? 1 : 0;
      if (j != 0)
      {
        String str = localURLConnection.getHeaderField("Location");
        if (str.startsWith("http", 0)) {
          paramURL = new URL(str);
        } else {
          paramURL = new URL(paramURL, str);
        }
        return getStream(paramURL);
      }
    }
    if (SwingUtilities.isEventDispatchThread()) {
      handleConnectionProperties(localURLConnection);
    } else {
      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
        {
          public void run()
          {
            JEditorPane.this.handleConnectionProperties(localURLConnection);
          }
        });
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException);
      }
    }
    return localURLConnection.getInputStream();
  }
  
  private void handleConnectionProperties(URLConnection paramURLConnection)
  {
    if (pageProperties == null) {
      pageProperties = new Hashtable();
    }
    String str1 = paramURLConnection.getContentType();
    if (str1 != null)
    {
      setContentType(str1);
      pageProperties.put("content-type", str1);
    }
    pageProperties.put("stream", paramURLConnection.getURL());
    String str2 = paramURLConnection.getContentEncoding();
    if (str2 != null) {
      pageProperties.put("content-encoding", str2);
    }
  }
  
  private Object getPostData()
  {
    return getDocument().getProperty("javax.swing.JEditorPane.postdata");
  }
  
  private void handlePostData(HttpURLConnection paramHttpURLConnection, Object paramObject)
    throws IOException
  {
    paramHttpURLConnection.setDoOutput(true);
    DataOutputStream localDataOutputStream = null;
    try
    {
      paramHttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      localDataOutputStream = new DataOutputStream(paramHttpURLConnection.getOutputStream());
      localDataOutputStream.writeBytes((String)paramObject);
    }
    finally
    {
      if (localDataOutputStream != null) {
        localDataOutputStream.close();
      }
    }
  }
  
  public void scrollToReference(String paramString)
  {
    Document localDocument = getDocument();
    if ((localDocument instanceof HTMLDocument))
    {
      HTMLDocument localHTMLDocument = (HTMLDocument)localDocument;
      HTMLDocument.Iterator localIterator = localHTMLDocument.getIterator(HTML.Tag.A);
      while (localIterator.isValid())
      {
        AttributeSet localAttributeSet = localIterator.getAttributes();
        String str = (String)localAttributeSet.getAttribute(HTML.Attribute.NAME);
        if ((str != null) && (str.equals(paramString))) {
          try
          {
            int i = localIterator.getStartOffset();
            Rectangle localRectangle1 = modelToView(i);
            if (localRectangle1 != null)
            {
              Rectangle localRectangle2 = getVisibleRect();
              height = height;
              scrollRectToVisible(localRectangle1);
              setCaretPosition(i);
            }
          }
          catch (BadLocationException localBadLocationException)
          {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
          }
        }
        localIterator.next();
      }
    }
  }
  
  public URL getPage()
  {
    return (URL)getDocument().getProperty("stream");
  }
  
  public void setPage(String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new IOException("invalid url");
    }
    URL localURL = new URL(paramString);
    setPage(localURL);
  }
  
  public String getUIClassID()
  {
    return "EditorPaneUI";
  }
  
  protected EditorKit createDefaultEditorKit()
  {
    return new PlainEditorKit();
  }
  
  public EditorKit getEditorKit()
  {
    if (kit == null)
    {
      kit = createDefaultEditorKit();
      isUserSetEditorKit = false;
    }
    return kit;
  }
  
  public final String getContentType()
  {
    return kit != null ? kit.getContentType() : null;
  }
  
  public final void setContentType(String paramString)
  {
    int i = paramString.indexOf(";");
    Object localObject;
    if (i > -1)
    {
      localObject = paramString.substring(i);
      paramString = paramString.substring(0, i).trim();
      if (paramString.toLowerCase().startsWith("text/")) {
        setCharsetFromContentTypeParameters((String)localObject);
      }
    }
    if ((kit == null) || (!paramString.equals(kit.getContentType())) || (!isUserSetEditorKit))
    {
      localObject = getEditorKitForContentType(paramString);
      if ((localObject != null) && (localObject != kit))
      {
        setEditorKit((EditorKit)localObject);
        isUserSetEditorKit = false;
      }
    }
  }
  
  private void setCharsetFromContentTypeParameters(String paramString)
  {
    try
    {
      int i = paramString.indexOf(';');
      if ((i > -1) && (i < paramString.length() - 1)) {
        paramString = paramString.substring(i + 1);
      }
      if (paramString.length() > 0)
      {
        HeaderParser localHeaderParser = new HeaderParser(paramString);
        String str = localHeaderParser.findValue("charset");
        if (str != null) {
          putClientProperty("charset", str);
        }
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}catch (NullPointerException localNullPointerException) {}catch (Exception localException)
    {
      System.err.println("JEditorPane.getCharsetFromContentTypeParameters failed on: " + paramString);
      localException.printStackTrace();
    }
  }
  
  public void setEditorKit(EditorKit paramEditorKit)
  {
    EditorKit localEditorKit = kit;
    isUserSetEditorKit = true;
    if (localEditorKit != null) {
      localEditorKit.deinstall(this);
    }
    kit = paramEditorKit;
    if (kit != null)
    {
      kit.install(this);
      setDocument(kit.createDefaultDocument());
    }
    firePropertyChange("editorKit", localEditorKit, paramEditorKit);
  }
  
  public EditorKit getEditorKitForContentType(String paramString)
  {
    if (typeHandlers == null) {
      typeHandlers = new Hashtable(3);
    }
    EditorKit localEditorKit = (EditorKit)typeHandlers.get(paramString);
    if (localEditorKit == null)
    {
      localEditorKit = createEditorKitForContentType(paramString);
      if (localEditorKit != null) {
        setEditorKitForContentType(paramString, localEditorKit);
      }
    }
    if (localEditorKit == null) {
      localEditorKit = createDefaultEditorKit();
    }
    return localEditorKit;
  }
  
  public void setEditorKitForContentType(String paramString, EditorKit paramEditorKit)
  {
    if (typeHandlers == null) {
      typeHandlers = new Hashtable(3);
    }
    typeHandlers.put(paramString, paramEditorKit);
  }
  
  public void replaceSelection(String paramString)
  {
    if (!isEditable())
    {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
      return;
    }
    EditorKit localEditorKit = getEditorKit();
    if ((localEditorKit instanceof StyledEditorKit)) {
      try
      {
        Document localDocument = getDocument();
        Caret localCaret = getCaret();
        boolean bool = saveComposedText(localCaret.getDot());
        int i = Math.min(localCaret.getDot(), localCaret.getMark());
        int j = Math.max(localCaret.getDot(), localCaret.getMark());
        if ((localDocument instanceof AbstractDocument))
        {
          ((AbstractDocument)localDocument).replace(i, j - i, paramString, ((StyledEditorKit)localEditorKit).getInputAttributes());
        }
        else
        {
          if (i != j) {
            localDocument.remove(i, j - i);
          }
          if ((paramString != null) && (paramString.length() > 0)) {
            localDocument.insertString(i, paramString, ((StyledEditorKit)localEditorKit).getInputAttributes());
          }
        }
        if (bool) {
          restoreComposedText();
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      }
    } else {
      super.replaceSelection(paramString);
    }
  }
  
  public static EditorKit createEditorKitForContentType(String paramString)
  {
    Hashtable localHashtable = getKitRegisty();
    EditorKit localEditorKit = (EditorKit)localHashtable.get(paramString);
    if (localEditorKit == null)
    {
      String str = (String)getKitTypeRegistry().get(paramString);
      ClassLoader localClassLoader = (ClassLoader)getKitLoaderRegistry().get(paramString);
      try
      {
        Class localClass;
        if (localClassLoader != null) {
          localClass = localClassLoader.loadClass(str);
        } else {
          localClass = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
        }
        localEditorKit = (EditorKit)localClass.newInstance();
        localHashtable.put(paramString, localEditorKit);
      }
      catch (Throwable localThrowable)
      {
        localEditorKit = null;
      }
    }
    if (localEditorKit != null) {
      return (EditorKit)localEditorKit.clone();
    }
    return null;
  }
  
  public static void registerEditorKitForContentType(String paramString1, String paramString2)
  {
    registerEditorKitForContentType(paramString1, paramString2, Thread.currentThread().getContextClassLoader());
  }
  
  public static void registerEditorKitForContentType(String paramString1, String paramString2, ClassLoader paramClassLoader)
  {
    getKitTypeRegistry().put(paramString1, paramString2);
    if (paramClassLoader != null) {
      getKitLoaderRegistry().put(paramString1, paramClassLoader);
    } else {
      getKitLoaderRegistry().remove(paramString1);
    }
    getKitRegisty().remove(paramString1);
  }
  
  public static String getEditorKitClassNameForContentType(String paramString)
  {
    return (String)getKitTypeRegistry().get(paramString);
  }
  
  private static Hashtable<String, String> getKitTypeRegistry()
  {
    loadDefaultKitsIfNecessary();
    return (Hashtable)SwingUtilities.appContextGet(kitTypeRegistryKey);
  }
  
  private static Hashtable<String, ClassLoader> getKitLoaderRegistry()
  {
    loadDefaultKitsIfNecessary();
    return (Hashtable)SwingUtilities.appContextGet(kitLoaderRegistryKey);
  }
  
  private static Hashtable<String, EditorKit> getKitRegisty()
  {
    Hashtable localHashtable = (Hashtable)SwingUtilities.appContextGet(kitRegistryKey);
    if (localHashtable == null)
    {
      localHashtable = new Hashtable(3);
      SwingUtilities.appContextPut(kitRegistryKey, localHashtable);
    }
    return localHashtable;
  }
  
  private static void loadDefaultKitsIfNecessary()
  {
    if (SwingUtilities.appContextGet(kitTypeRegistryKey) == null)
    {
      synchronized (defaultEditorKitMap)
      {
        if (defaultEditorKitMap.size() == 0)
        {
          defaultEditorKitMap.put("text/plain", "javax.swing.JEditorPane$PlainEditorKit");
          defaultEditorKitMap.put("text/html", "javax.swing.text.html.HTMLEditorKit");
          defaultEditorKitMap.put("text/rtf", "javax.swing.text.rtf.RTFEditorKit");
          defaultEditorKitMap.put("application/rtf", "javax.swing.text.rtf.RTFEditorKit");
        }
      }
      ??? = new Hashtable();
      SwingUtilities.appContextPut(kitTypeRegistryKey, ???);
      ??? = new Hashtable();
      SwingUtilities.appContextPut(kitLoaderRegistryKey, ???);
      Iterator localIterator = defaultEditorKitMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        registerEditorKitForContentType(str, (String)defaultEditorKitMap.get(str));
      }
    }
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension1 = super.getPreferredSize();
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport))
    {
      JViewport localJViewport = (JViewport)localContainer;
      TextUI localTextUI = getUI();
      int i = width;
      int j = height;
      int k;
      Dimension localDimension2;
      if (!getScrollableTracksViewportWidth())
      {
        k = localJViewport.getWidth();
        localDimension2 = localTextUI.getMinimumSize(this);
        if ((k != 0) && (k < width)) {
          i = width;
        }
      }
      if (!getScrollableTracksViewportHeight())
      {
        k = localJViewport.getHeight();
        localDimension2 = localTextUI.getMinimumSize(this);
        if ((k != 0) && (k < height)) {
          j = height;
        }
      }
      if ((i != width) || (j != height)) {
        localDimension1 = new Dimension(i, j);
      }
    }
    return localDimension1;
  }
  
  public void setText(String paramString)
  {
    try
    {
      Document localDocument = getDocument();
      localDocument.remove(0, localDocument.getLength());
      if ((paramString == null) || (paramString.equals(""))) {
        return;
      }
      StringReader localStringReader = new StringReader(paramString);
      EditorKit localEditorKit = getEditorKit();
      localEditorKit.read(localStringReader, localDocument, 0);
    }
    catch (IOException localIOException)
    {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    }
    catch (BadLocationException localBadLocationException)
    {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    }
  }
  
  public String getText()
  {
    String str;
    try
    {
      StringWriter localStringWriter = new StringWriter();
      write(localStringWriter);
      str = localStringWriter.toString();
    }
    catch (IOException localIOException)
    {
      str = null;
    }
    return str;
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport))
    {
      JViewport localJViewport = (JViewport)localContainer;
      TextUI localTextUI = getUI();
      int i = localJViewport.getWidth();
      Dimension localDimension1 = localTextUI.getMinimumSize(this);
      Dimension localDimension2 = localTextUI.getMaximumSize(this);
      if ((i >= width) && (i <= width)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight()
  {
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport))
    {
      JViewport localJViewport = (JViewport)localContainer;
      TextUI localTextUI = getUI();
      int i = localJViewport.getHeight();
      Dimension localDimension1 = localTextUI.getMinimumSize(this);
      if (i >= height)
      {
        Dimension localDimension2 = localTextUI.getMaximumSize(this);
        if (i <= height) {
          return true;
        }
      }
    }
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("EditorPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    String str1 = kit != null ? kit.toString() : "";
    String str2 = typeHandlers != null ? typeHandlers.toString() : "";
    return super.paramString() + ",kit=" + str1 + ",typeHandlers=" + str2;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if ((getEditorKit() instanceof HTMLEditorKit))
    {
      if ((accessibleContext == null) || (accessibleContext.getClass() != AccessibleJEditorPaneHTML.class)) {
        accessibleContext = new AccessibleJEditorPaneHTML();
      }
    }
    else if ((accessibleContext == null) || (accessibleContext.getClass() != AccessibleJEditorPane.class)) {
      accessibleContext = new AccessibleJEditorPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJEditorPane
    extends JTextComponent.AccessibleJTextComponent
  {
    protected AccessibleJEditorPane()
    {
      super();
    }
    
    public String getAccessibleDescription()
    {
      String str = accessibleDescription;
      if (str == null) {
        str = (String)getClientProperty("AccessibleDescription");
      }
      if (str == null) {
        str = getContentType();
      }
      return str;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.MULTI_LINE);
      return localAccessibleStateSet;
    }
  }
  
  protected class AccessibleJEditorPaneHTML
    extends JEditorPane.AccessibleJEditorPane
  {
    private AccessibleContext accessibleContext;
    
    public AccessibleText getAccessibleText()
    {
      return new JEditorPane.JEditorPaneAccessibleHypertextSupport(JEditorPane.this);
    }
    
    protected AccessibleJEditorPaneHTML()
    {
      super();
      HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)getEditorKit();
      accessibleContext = localHTMLEditorKit.getAccessibleContext();
    }
    
    public int getAccessibleChildrenCount()
    {
      if (accessibleContext != null) {
        return accessibleContext.getAccessibleChildrenCount();
      }
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if (accessibleContext != null) {
        return accessibleContext.getAccessibleChild(paramInt);
      }
      return null;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      if ((accessibleContext != null) && (paramPoint != null)) {
        try
        {
          AccessibleComponent localAccessibleComponent = accessibleContext.getAccessibleComponent();
          if (localAccessibleComponent != null) {
            return localAccessibleComponent.getAccessibleAt(paramPoint);
          }
          return null;
        }
        catch (IllegalComponentStateException localIllegalComponentStateException)
        {
          return null;
        }
      }
      return null;
    }
  }
  
  static class HeaderParser
  {
    String raw;
    String[][] tab;
    
    public HeaderParser(String paramString)
    {
      raw = paramString;
      tab = new String[10][2];
      parse();
    }
    
    private void parse()
    {
      if (raw != null)
      {
        raw = raw.trim();
        char[] arrayOfChar = raw.toCharArray();
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 1;
        int n = 0;
        int i1 = arrayOfChar.length;
        while (j < i1)
        {
          int i2 = arrayOfChar[j];
          if (i2 == 61)
          {
            tab[k][0] = new String(arrayOfChar, i, j - i).toLowerCase();
            m = 0;
            j++;
            i = j;
          }
          else if (i2 == 34)
          {
            if (n != 0)
            {
              tab[(k++)][1] = new String(arrayOfChar, i, j - i);
              n = 0;
              do
              {
                j++;
              } while ((j < i1) && ((arrayOfChar[j] == ' ') || (arrayOfChar[j] == ',')));
              m = 1;
              i = j;
            }
            else
            {
              n = 1;
              j++;
              i = j;
            }
          }
          else if ((i2 == 32) || (i2 == 44))
          {
            if (n != 0)
            {
              j++;
            }
            else
            {
              if (m != 0) {
                tab[(k++)][0] = new String(arrayOfChar, i, j - i).toLowerCase();
              } else {
                tab[(k++)][1] = new String(arrayOfChar, i, j - i);
              }
              while ((j < i1) && ((arrayOfChar[j] == ' ') || (arrayOfChar[j] == ','))) {
                j++;
              }
              m = 1;
              i = j;
            }
          }
          else
          {
            j++;
          }
        }
        j--;
        if (j > i)
        {
          if (m == 0)
          {
            if (arrayOfChar[j] == '"') {
              tab[(k++)][1] = new String(arrayOfChar, i, j - i);
            } else {
              tab[(k++)][1] = new String(arrayOfChar, i, j - i + 1);
            }
          }
          else {
            tab[k][0] = new String(arrayOfChar, i, j - i + 1).toLowerCase();
          }
        }
        else if (j == i) {
          if (m == 0)
          {
            if (arrayOfChar[j] == '"') {
              tab[(k++)][1] = String.valueOf(arrayOfChar[(j - 1)]);
            } else {
              tab[(k++)][1] = String.valueOf(arrayOfChar[j]);
            }
          }
          else {
            tab[k][0] = String.valueOf(arrayOfChar[j]).toLowerCase();
          }
        }
      }
    }
    
    public String findKey(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 10)) {
        return null;
      }
      return tab[paramInt][0];
    }
    
    public String findValue(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 10)) {
        return null;
      }
      return tab[paramInt][1];
    }
    
    public String findValue(String paramString)
    {
      return findValue(paramString, null);
    }
    
    public String findValue(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        return paramString2;
      }
      paramString1 = paramString1.toLowerCase();
      for (int i = 0; i < 10; i++)
      {
        if (tab[i][0] == null) {
          return paramString2;
        }
        if (paramString1.equals(tab[i][0])) {
          return tab[i][1];
        }
      }
      return paramString2;
    }
    
    public int findInt(String paramString, int paramInt)
    {
      try
      {
        return Integer.parseInt(findValue(paramString, String.valueOf(paramInt)));
      }
      catch (Throwable localThrowable) {}
      return paramInt;
    }
  }
  
  protected class JEditorPaneAccessibleHypertextSupport
    extends JEditorPane.AccessibleJEditorPane
    implements AccessibleHypertext
  {
    LinkVector hyperlinks = new LinkVector(null);
    boolean linksValid = false;
    
    private void buildLinkTable()
    {
      hyperlinks.removeAllElements();
      Document localDocument = getDocument();
      if (localDocument != null)
      {
        ElementIterator localElementIterator = new ElementIterator(localDocument);
        Element localElement;
        while ((localElement = localElementIterator.next()) != null) {
          if (localElement.isLeaf())
          {
            AttributeSet localAttributeSet1 = localElement.getAttributes();
            AttributeSet localAttributeSet2 = (AttributeSet)localAttributeSet1.getAttribute(HTML.Tag.A);
            Object localObject = localAttributeSet2 != null ? (String)localAttributeSet2.getAttribute(HTML.Attribute.HREF) : null;
            if (localObject != null) {
              hyperlinks.addElement(new HTMLLink(localElement));
            }
          }
        }
      }
      linksValid = true;
    }
    
    public JEditorPaneAccessibleHypertextSupport()
    {
      super();
      Document localDocument = getDocument();
      if (localDocument != null) {
        localDocument.addDocumentListener(new DocumentListener()
        {
          public void changedUpdate(DocumentEvent paramAnonymousDocumentEvent)
          {
            linksValid = false;
          }
          
          public void insertUpdate(DocumentEvent paramAnonymousDocumentEvent)
          {
            linksValid = false;
          }
          
          public void removeUpdate(DocumentEvent paramAnonymousDocumentEvent)
          {
            linksValid = false;
          }
        });
      }
    }
    
    public int getLinkCount()
    {
      if (!linksValid) {
        buildLinkTable();
      }
      return hyperlinks.size();
    }
    
    public int getLinkIndex(int paramInt)
    {
      if (!linksValid) {
        buildLinkTable();
      }
      Element localElement = null;
      Document localDocument = getDocument();
      if (localDocument != null)
      {
        int i;
        for (localElement = localDocument.getDefaultRootElement(); !localElement.isLeaf(); localElement = localElement.getElement(i)) {
          i = localElement.getElementIndex(paramInt);
        }
      }
      return hyperlinks.baseElementIndex(localElement);
    }
    
    public AccessibleHyperlink getLink(int paramInt)
    {
      if (!linksValid) {
        buildLinkTable();
      }
      if ((paramInt >= 0) && (paramInt < hyperlinks.size())) {
        return (AccessibleHyperlink)hyperlinks.elementAt(paramInt);
      }
      return null;
    }
    
    public String getLinkText(int paramInt)
    {
      if (!linksValid) {
        buildLinkTable();
      }
      Element localElement = (Element)hyperlinks.elementAt(paramInt);
      if (localElement != null)
      {
        Document localDocument = getDocument();
        if (localDocument != null) {
          try
          {
            return localDocument.getText(localElement.getStartOffset(), localElement.getEndOffset() - localElement.getStartOffset());
          }
          catch (BadLocationException localBadLocationException)
          {
            return null;
          }
        }
      }
      return null;
    }
    
    public class HTMLLink
      extends AccessibleHyperlink
    {
      Element element;
      
      public HTMLLink(Element paramElement)
      {
        element = paramElement;
      }
      
      public boolean isValid()
      {
        return linksValid;
      }
      
      public int getAccessibleActionCount()
      {
        return 1;
      }
      
      public boolean doAccessibleAction(int paramInt)
      {
        if ((paramInt == 0) && (isValid() == true))
        {
          URL localURL = (URL)getAccessibleActionObject(paramInt);
          if (localURL != null)
          {
            HyperlinkEvent localHyperlinkEvent = new HyperlinkEvent(JEditorPane.this, HyperlinkEvent.EventType.ACTIVATED, localURL);
            fireHyperlinkUpdate(localHyperlinkEvent);
            return true;
          }
        }
        return false;
      }
      
      public String getAccessibleActionDescription(int paramInt)
      {
        if ((paramInt == 0) && (isValid() == true))
        {
          Document localDocument = getDocument();
          if (localDocument != null) {
            try
            {
              return localDocument.getText(getStartIndex(), getEndIndex() - getStartIndex());
            }
            catch (BadLocationException localBadLocationException)
            {
              return null;
            }
          }
        }
        return null;
      }
      
      public Object getAccessibleActionObject(int paramInt)
      {
        if ((paramInt == 0) && (isValid() == true))
        {
          AttributeSet localAttributeSet1 = element.getAttributes();
          AttributeSet localAttributeSet2 = (AttributeSet)localAttributeSet1.getAttribute(HTML.Tag.A);
          String str = localAttributeSet2 != null ? (String)localAttributeSet2.getAttribute(HTML.Attribute.HREF) : null;
          if (str != null)
          {
            URL localURL;
            try
            {
              localURL = new URL(getPage(), str);
            }
            catch (MalformedURLException localMalformedURLException)
            {
              localURL = null;
            }
            return localURL;
          }
        }
        return null;
      }
      
      public Object getAccessibleActionAnchor(int paramInt)
      {
        return getAccessibleActionDescription(paramInt);
      }
      
      public int getStartIndex()
      {
        return element.getStartOffset();
      }
      
      public int getEndIndex()
      {
        return element.getEndOffset();
      }
    }
    
    private class LinkVector
      extends Vector<JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink>
    {
      private LinkVector() {}
      
      public int baseElementIndex(Element paramElement)
      {
        for (int i = 0; i < elementCount; i++)
        {
          JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink localHTMLLink = (JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink)elementAt(i);
          if (element == paramElement) {
            return i;
          }
        }
        return -1;
      }
    }
  }
  
  class PageLoader
    extends SwingWorker<URL, Object>
  {
    InputStream in;
    URL old;
    URL page;
    Document doc;
    
    PageLoader(Document paramDocument, InputStream paramInputStream, URL paramURL1, URL paramURL2)
    {
      in = paramInputStream;
      old = paramURL1;
      page = paramURL2;
      doc = paramDocument;
    }
    
    protected URL doInBackground()
    {
      int i = 0;
      try
      {
        if (in == null)
        {
          in = getStream(page);
          if (kit == null)
          {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            URL localURL1 = old;
            return old;
          }
        }
        if (doc == null) {
          try
          {
            SwingUtilities.invokeAndWait(new Runnable()
            {
              public void run()
              {
                doc = JEditorPane.this.initializeModel(kit, page);
                setDocument(doc);
              }
            });
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            localObject1 = old;
            if (i != 0) {
              SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  firePropertyChange("page", old, page);
                }
              });
            }
            return i != 0 ? page : old;
          }
          catch (InterruptedException localInterruptedException)
          {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            localObject1 = old;
            if (i != 0) {
              SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  firePropertyChange("page", old, page);
                }
              });
            }
            return i != 0 ? page : old;
          }
        }
        read(in, doc);
        URL localURL2 = (URL)doc.getProperty("stream");
        Object localObject1 = localURL2.getRef();
        if (localObject1 != null)
        {
          Runnable local2 = new Runnable()
          {
            public void run()
            {
              URL localURL = (URL)getDocument().getProperty("stream");
              String str = localURL.getRef();
              scrollToReference(str);
            }
          };
          SwingUtilities.invokeLater(local2);
        }
        i = 1;
        return old;
      }
      catch (IOException localIOException)
      {
        UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
        return old;
      }
      finally
      {
        if (i != 0) {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              firePropertyChange("page", old, page);
            }
          });
        }
        if (i != 0) {
          tmpTernaryOp = page;
        }
      }
      return old;
    }
  }
  
  static class PlainEditorKit
    extends DefaultEditorKit
    implements ViewFactory
  {
    PlainEditorKit() {}
    
    public ViewFactory getViewFactory()
    {
      return this;
    }
    
    public View create(Element paramElement)
    {
      Document localDocument = paramElement.getDocument();
      Object localObject = localDocument.getProperty("i18n");
      if ((localObject != null) && (localObject.equals(Boolean.TRUE))) {
        return createI18N(paramElement);
      }
      return new WrappedPlainView(paramElement);
    }
    
    View createI18N(Element paramElement)
    {
      String str = paramElement.getName();
      if (str != null)
      {
        if (str.equals("content")) {
          return new PlainParagraph(paramElement);
        }
        if (str.equals("paragraph")) {
          return new BoxView(paramElement, 1);
        }
      }
      return null;
    }
    
    static class PlainParagraph
      extends ParagraphView
    {
      PlainParagraph(Element paramElement)
      {
        super();
        layoutPool = new LogicalView(paramElement);
        layoutPool.setParent(this);
      }
      
      protected void setPropertiesFromAttributes()
      {
        Container localContainer = getContainer();
        if ((localContainer != null) && (!localContainer.getComponentOrientation().isLeftToRight())) {
          setJustification(2);
        } else {
          setJustification(0);
        }
      }
      
      public int getFlowSpan(int paramInt)
      {
        Container localContainer = getContainer();
        if ((localContainer instanceof JTextArea))
        {
          JTextArea localJTextArea = (JTextArea)localContainer;
          if (!localJTextArea.getLineWrap()) {
            return Integer.MAX_VALUE;
          }
        }
        return super.getFlowSpan(paramInt);
      }
      
      protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
      {
        SizeRequirements localSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
        Container localContainer = getContainer();
        if ((localContainer instanceof JTextArea))
        {
          JTextArea localJTextArea = (JTextArea)localContainer;
          if (!localJTextArea.getLineWrap()) {
            minimum = preferred;
          }
        }
        return localSizeRequirements;
      }
      
      static class LogicalView
        extends CompositeView
      {
        LogicalView(Element paramElement)
        {
          super();
        }
        
        protected int getViewIndexAtPosition(int paramInt)
        {
          Element localElement = getElement();
          if (localElement.getElementCount() > 0) {
            return localElement.getElementIndex(paramInt);
          }
          return 0;
        }
        
        protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory)
        {
          return false;
        }
        
        protected void loadChildren(ViewFactory paramViewFactory)
        {
          Element localElement = getElement();
          if (localElement.getElementCount() > 0)
          {
            super.loadChildren(paramViewFactory);
          }
          else
          {
            GlyphView localGlyphView = new GlyphView(localElement);
            append(localGlyphView);
          }
        }
        
        public float getPreferredSpan(int paramInt)
        {
          if (getViewCount() != 1) {
            throw new Error("One child view is assumed.");
          }
          View localView = getView(0);
          return localView.getPreferredSpan(paramInt);
        }
        
        protected void forwardUpdateToView(View paramView, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
        {
          paramView.setParent(this);
          super.forwardUpdateToView(paramView, paramDocumentEvent, paramShape, paramViewFactory);
        }
        
        public void paint(Graphics paramGraphics, Shape paramShape) {}
        
        protected boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle)
        {
          return false;
        }
        
        protected boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle)
        {
          return false;
        }
        
        protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
        {
          return null;
        }
        
        protected void childAllocation(int paramInt, Rectangle paramRectangle) {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JEditorPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */