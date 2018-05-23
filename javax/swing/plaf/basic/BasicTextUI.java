package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.InsertBreakAction;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.DropLocation;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.swing.text.Keymap;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;

public abstract class BasicTextUI
  extends TextUI
  implements ViewFactory
{
  private static BasicCursor textCursor = new BasicCursor(2);
  private static final EditorKit defaultKit = new DefaultEditorKit();
  transient JTextComponent editor;
  transient boolean painted = false;
  transient RootView rootView = new RootView();
  transient UpdateHandler updateHandler = new UpdateHandler();
  private static final TransferHandler defaultTransferHandler = new TextTransferHandler();
  private final DragListener dragListener = getDragListener();
  private static final Position.Bias[] discardBias = new Position.Bias[1];
  private DefaultCaret dropCaret;
  
  public BasicTextUI() {}
  
  protected Caret createCaret()
  {
    return new BasicCaret();
  }
  
  protected Highlighter createHighlighter()
  {
    return new BasicHighlighter();
  }
  
  protected String getKeymapName()
  {
    String str = getClass().getName();
    int i = str.lastIndexOf('.');
    if (i >= 0) {
      str = str.substring(i + 1, str.length());
    }
    return str;
  }
  
  protected Keymap createKeymap()
  {
    String str1 = getKeymapName();
    Keymap localKeymap1 = JTextComponent.getKeymap(str1);
    if (localKeymap1 == null)
    {
      Keymap localKeymap2 = JTextComponent.getKeymap("default");
      localKeymap1 = JTextComponent.addKeymap(str1, localKeymap2);
      String str2 = getPropertyPrefix();
      Object localObject = DefaultLookup.get(editor, this, str2 + ".keyBindings");
      if ((localObject != null) && ((localObject instanceof JTextComponent.KeyBinding[])))
      {
        JTextComponent.KeyBinding[] arrayOfKeyBinding = (JTextComponent.KeyBinding[])localObject;
        JTextComponent.loadKeymap(localKeymap1, arrayOfKeyBinding, getComponent().getActions());
      }
    }
    return localKeymap1;
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((paramPropertyChangeEvent.getPropertyName().equals("editable")) || (paramPropertyChangeEvent.getPropertyName().equals("enabled"))) {
      updateBackground((JTextComponent)paramPropertyChangeEvent.getSource());
    }
  }
  
  private void updateBackground(JTextComponent paramJTextComponent)
  {
    if (((this instanceof SynthUI)) || ((paramJTextComponent instanceof JTextArea))) {
      return;
    }
    Color localColor1 = paramJTextComponent.getBackground();
    if ((localColor1 instanceof UIResource))
    {
      String str = getPropertyPrefix();
      Color localColor2 = DefaultLookup.getColor(paramJTextComponent, this, str + ".disabledBackground", null);
      Color localColor3 = DefaultLookup.getColor(paramJTextComponent, this, str + ".inactiveBackground", null);
      Color localColor4 = DefaultLookup.getColor(paramJTextComponent, this, str + ".background", null);
      if ((((paramJTextComponent instanceof JTextArea)) || ((paramJTextComponent instanceof JEditorPane))) && (localColor1 != localColor2) && (localColor1 != localColor3) && (localColor1 != localColor4)) {
        return;
      }
      Color localColor5 = null;
      if (!paramJTextComponent.isEnabled()) {
        localColor5 = localColor2;
      }
      if ((localColor5 == null) && (!paramJTextComponent.isEditable())) {
        localColor5 = localColor3;
      }
      if (localColor5 == null) {
        localColor5 = localColor4;
      }
      if ((localColor5 != null) && (localColor5 != localColor1)) {
        paramJTextComponent.setBackground(localColor5);
      }
    }
  }
  
  protected abstract String getPropertyPrefix();
  
  protected void installDefaults()
  {
    String str = getPropertyPrefix();
    Font localFont = editor.getFont();
    if ((localFont == null) || ((localFont instanceof UIResource))) {
      editor.setFont(UIManager.getFont(str + ".font"));
    }
    Color localColor1 = editor.getBackground();
    if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
      editor.setBackground(UIManager.getColor(str + ".background"));
    }
    Color localColor2 = editor.getForeground();
    if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
      editor.setForeground(UIManager.getColor(str + ".foreground"));
    }
    Color localColor3 = editor.getCaretColor();
    if ((localColor3 == null) || ((localColor3 instanceof UIResource))) {
      editor.setCaretColor(UIManager.getColor(str + ".caretForeground"));
    }
    Color localColor4 = editor.getSelectionColor();
    if ((localColor4 == null) || ((localColor4 instanceof UIResource))) {
      editor.setSelectionColor(UIManager.getColor(str + ".selectionBackground"));
    }
    Color localColor5 = editor.getSelectedTextColor();
    if ((localColor5 == null) || ((localColor5 instanceof UIResource))) {
      editor.setSelectedTextColor(UIManager.getColor(str + ".selectionForeground"));
    }
    Color localColor6 = editor.getDisabledTextColor();
    if ((localColor6 == null) || ((localColor6 instanceof UIResource))) {
      editor.setDisabledTextColor(UIManager.getColor(str + ".inactiveForeground"));
    }
    Border localBorder = editor.getBorder();
    if ((localBorder == null) || ((localBorder instanceof UIResource))) {
      editor.setBorder(UIManager.getBorder(str + ".border"));
    }
    Insets localInsets = editor.getMargin();
    if ((localInsets == null) || ((localInsets instanceof UIResource))) {
      editor.setMargin(UIManager.getInsets(str + ".margin"));
    }
    updateCursor();
  }
  
  private void installDefaults2()
  {
    editor.addMouseListener(dragListener);
    editor.addMouseMotionListener(dragListener);
    String str = getPropertyPrefix();
    Caret localCaret = editor.getCaret();
    if ((localCaret == null) || ((localCaret instanceof UIResource)))
    {
      localCaret = createCaret();
      editor.setCaret(localCaret);
      int i = DefaultLookup.getInt(getComponent(), this, str + ".caretBlinkRate", 500);
      localCaret.setBlinkRate(i);
    }
    Highlighter localHighlighter = editor.getHighlighter();
    if ((localHighlighter == null) || ((localHighlighter instanceof UIResource))) {
      editor.setHighlighter(createHighlighter());
    }
    TransferHandler localTransferHandler = editor.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource))) {
      editor.setTransferHandler(getTransferHandler());
    }
  }
  
  protected void uninstallDefaults()
  {
    editor.removeMouseListener(dragListener);
    editor.removeMouseMotionListener(dragListener);
    if ((editor.getCaretColor() instanceof UIResource)) {
      editor.setCaretColor(null);
    }
    if ((editor.getSelectionColor() instanceof UIResource)) {
      editor.setSelectionColor(null);
    }
    if ((editor.getDisabledTextColor() instanceof UIResource)) {
      editor.setDisabledTextColor(null);
    }
    if ((editor.getSelectedTextColor() instanceof UIResource)) {
      editor.setSelectedTextColor(null);
    }
    if ((editor.getBorder() instanceof UIResource)) {
      editor.setBorder(null);
    }
    if ((editor.getMargin() instanceof UIResource)) {
      editor.setMargin(null);
    }
    if ((editor.getCaret() instanceof UIResource)) {
      editor.setCaret(null);
    }
    if ((editor.getHighlighter() instanceof UIResource)) {
      editor.setHighlighter(null);
    }
    if ((editor.getTransferHandler() instanceof UIResource)) {
      editor.setTransferHandler(null);
    }
    if ((editor.getCursor() instanceof UIResource)) {
      editor.setCursor(null);
    }
  }
  
  protected void installListeners() {}
  
  protected void uninstallListeners() {}
  
  protected void installKeyboardActions()
  {
    editor.setKeymap(createKeymap());
    InputMap localInputMap = getInputMap();
    if (localInputMap != null) {
      SwingUtilities.replaceUIInputMap(editor, 0, localInputMap);
    }
    ActionMap localActionMap = getActionMap();
    if (localActionMap != null) {
      SwingUtilities.replaceUIActionMap(editor, localActionMap);
    }
    updateFocusAcceleratorBinding(false);
  }
  
  InputMap getInputMap()
  {
    InputMapUIResource localInputMapUIResource = new InputMapUIResource();
    InputMap localInputMap = (InputMap)DefaultLookup.get(editor, this, getPropertyPrefix() + ".focusInputMap");
    if (localInputMap != null) {
      localInputMapUIResource.setParent(localInputMap);
    }
    return localInputMapUIResource;
  }
  
  void updateFocusAcceleratorBinding(boolean paramBoolean)
  {
    int i = editor.getFocusAccelerator();
    if ((paramBoolean) || (i != 0))
    {
      Object localObject = SwingUtilities.getUIInputMap(editor, 2);
      if ((localObject == null) && (i != 0))
      {
        localObject = new ComponentInputMapUIResource(editor);
        SwingUtilities.replaceUIInputMap(editor, 2, (InputMap)localObject);
        ActionMap localActionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(editor, localActionMap);
      }
      if (localObject != null)
      {
        ((InputMap)localObject).clear();
        if (i != 0) {
          ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "requestFocus");
        }
      }
    }
  }
  
  void updateFocusTraversalKeys()
  {
    EditorKit localEditorKit = getEditorKit(editor);
    if ((localEditorKit != null) && ((localEditorKit instanceof DefaultEditorKit)))
    {
      Set localSet1 = editor.getFocusTraversalKeys(0);
      Set localSet2 = editor.getFocusTraversalKeys(1);
      HashSet localHashSet1 = new HashSet(localSet1);
      HashSet localHashSet2 = new HashSet(localSet2);
      if (editor.isEditable())
      {
        localHashSet1.remove(KeyStroke.getKeyStroke(9, 0));
        localHashSet2.remove(KeyStroke.getKeyStroke(9, 1));
      }
      else
      {
        localHashSet1.add(KeyStroke.getKeyStroke(9, 0));
        localHashSet2.add(KeyStroke.getKeyStroke(9, 1));
      }
      LookAndFeel.installProperty(editor, "focusTraversalKeysForward", localHashSet1);
      LookAndFeel.installProperty(editor, "focusTraversalKeysBackward", localHashSet2);
    }
  }
  
  private void updateCursor()
  {
    if ((!editor.isCursorSet()) || ((editor.getCursor() instanceof UIResource)))
    {
      Cursor localCursor = editor.isEditable() ? textCursor : null;
      editor.setCursor(localCursor);
    }
  }
  
  TransferHandler getTransferHandler()
  {
    return defaultTransferHandler;
  }
  
  ActionMap getActionMap()
  {
    String str = getPropertyPrefix() + ".actionMap";
    ActionMap localActionMap = (ActionMap)UIManager.get(str);
    if (localActionMap == null)
    {
      localActionMap = createActionMap();
      if (localActionMap != null) {
        UIManager.getLookAndFeelDefaults().put(str, localActionMap);
      }
    }
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    localActionMapUIResource.put("requestFocus", new FocusAction());
    if (((getEditorKit(editor) instanceof DefaultEditorKit)) && (localActionMap != null))
    {
      Action localAction = localActionMap.get("insert-break");
      if ((localAction != null) && ((localAction instanceof DefaultEditorKit.InsertBreakAction)))
      {
        TextActionWrapper localTextActionWrapper = new TextActionWrapper((TextAction)localAction);
        localActionMapUIResource.put(localTextActionWrapper.getValue("Name"), localTextActionWrapper);
      }
    }
    if (localActionMap != null) {
      localActionMapUIResource.setParent(localActionMap);
    }
    return localActionMapUIResource;
  }
  
  ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    for (Action localAction : editor.getActions()) {
      localActionMapUIResource.put(localAction.getValue("Name"), localAction);
    }
    localActionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    localActionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    localActionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    return localActionMapUIResource;
  }
  
  protected void uninstallKeyboardActions()
  {
    editor.setKeymap(null);
    SwingUtilities.replaceUIInputMap(editor, 2, null);
    SwingUtilities.replaceUIActionMap(editor, null);
  }
  
  protected void paintBackground(Graphics paramGraphics)
  {
    paramGraphics.setColor(editor.getBackground());
    paramGraphics.fillRect(0, 0, editor.getWidth(), editor.getHeight());
  }
  
  protected final JTextComponent getComponent()
  {
    return editor;
  }
  
  protected void modelChanged()
  {
    ViewFactory localViewFactory = rootView.getViewFactory();
    Document localDocument = editor.getDocument();
    Element localElement = localDocument.getDefaultRootElement();
    setView(localViewFactory.create(localElement));
  }
  
  protected final void setView(View paramView)
  {
    rootView.setView(paramView);
    painted = false;
    editor.revalidate();
    editor.repaint();
  }
  
  protected void paintSafely(Graphics paramGraphics)
  {
    painted = true;
    Highlighter localHighlighter = editor.getHighlighter();
    Caret localCaret = editor.getCaret();
    if (editor.isOpaque()) {
      paintBackground(paramGraphics);
    }
    if (localHighlighter != null) {
      localHighlighter.paint(paramGraphics);
    }
    Rectangle localRectangle = getVisibleEditorRect();
    if (localRectangle != null) {
      rootView.paint(paramGraphics, localRectangle);
    }
    if (localCaret != null) {
      localCaret.paint(paramGraphics);
    }
    if (dropCaret != null) {
      dropCaret.paint(paramGraphics);
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    if ((paramJComponent instanceof JTextComponent))
    {
      editor = ((JTextComponent)paramJComponent);
      LookAndFeel.installProperty(editor, "opaque", Boolean.TRUE);
      LookAndFeel.installProperty(editor, "autoscrolls", Boolean.TRUE);
      installDefaults();
      installDefaults2();
      editor.addPropertyChangeListener(updateHandler);
      Document localDocument = editor.getDocument();
      if (localDocument == null)
      {
        editor.setDocument(getEditorKit(editor).createDefaultDocument());
      }
      else
      {
        localDocument.addDocumentListener(updateHandler);
        modelChanged();
      }
      installListeners();
      installKeyboardActions();
      LayoutManager localLayoutManager = editor.getLayout();
      if ((localLayoutManager == null) || ((localLayoutManager instanceof UIResource))) {
        editor.setLayout(updateHandler);
      }
      updateBackground(editor);
    }
    else
    {
      throw new Error("TextUI needs JTextComponent");
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    editor.removePropertyChangeListener(updateHandler);
    editor.getDocument().removeDocumentListener(updateHandler);
    painted = false;
    uninstallDefaults();
    rootView.setView(null);
    paramJComponent.removeAll();
    LayoutManager localLayoutManager = paramJComponent.getLayout();
    if ((localLayoutManager instanceof UIResource)) {
      paramJComponent.setLayout(null);
    }
    uninstallKeyboardActions();
    uninstallListeners();
    editor = null;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    paint(paramGraphics, paramJComponent);
  }
  
  public final void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if ((rootView.getViewCount() > 0) && (rootView.getView(0) != null))
    {
      Document localDocument = editor.getDocument();
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readLock();
      }
      try
      {
        paintSafely(paramGraphics);
      }
      finally
      {
        if ((localDocument instanceof AbstractDocument)) {
          ((AbstractDocument)localDocument).readUnlock();
        }
      }
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Document localDocument = editor.getDocument();
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = paramJComponent.getSize();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      if ((width > left + right) && (height > top + bottom)) {
        rootView.setSize(width - left - right, height - top - bottom);
      } else if ((width == 0) && (height == 0)) {
        rootView.setSize(2.14748365E9F, 2.14748365E9F);
      }
      width = ((int)Math.min(rootView.getPreferredSpan(0) + left + right, 2147483647L));
      height = ((int)Math.min(rootView.getPreferredSpan(1) + top + bottom, 2147483647L));
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Document localDocument = editor.getDocument();
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = new Dimension();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      width = ((int)rootView.getMinimumSpan(0) + left + right);
      height = ((int)rootView.getMinimumSpan(1) + top + bottom);
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Document localDocument = editor.getDocument();
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = new Dimension();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      width = ((int)Math.min(rootView.getMaximumSpan(0) + left + right, 2147483647L));
      height = ((int)Math.min(rootView.getMaximumSpan(1) + top + bottom, 2147483647L));
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return localDimension;
  }
  
  protected Rectangle getVisibleEditorRect()
  {
    Rectangle localRectangle = editor.getBounds();
    if ((width > 0) && (height > 0))
    {
      x = (y = 0);
      Insets localInsets = editor.getInsets();
      x += left;
      y += top;
      width -= left + right;
      height -= top + bottom;
      return localRectangle;
    }
    return null;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    return modelToView(paramJTextComponent, paramInt, Position.Bias.Forward);
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt, Position.Bias paramBias)
    throws BadLocationException
  {
    Document localDocument = editor.getDocument();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      Rectangle localRectangle1 = getVisibleEditorRect();
      if (localRectangle1 != null)
      {
        rootView.setSize(width, height);
        Shape localShape = rootView.modelToView(paramInt, localRectangle1, paramBias);
        if (localShape != null)
        {
          Rectangle localRectangle2 = localShape.getBounds();
          return localRectangle2;
        }
      }
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return null;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint)
  {
    return viewToModel(paramJTextComponent, paramPoint, discardBias);
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint, Position.Bias[] paramArrayOfBias)
  {
    int i = -1;
    Document localDocument = editor.getDocument();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      Rectangle localRectangle = getVisibleEditorRect();
      if (localRectangle != null)
      {
        rootView.setSize(width, height);
        i = rootView.viewToModel(x, y, localRectangle, paramArrayOfBias);
      }
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return i;
  }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    Document localDocument = editor.getDocument();
    if ((localDocument instanceof AbstractDocument)) {
      ((AbstractDocument)localDocument).readLock();
    }
    try
    {
      if (painted)
      {
        Rectangle localRectangle = getVisibleEditorRect();
        if (localRectangle != null) {
          rootView.setSize(width, height);
        }
        int i = rootView.getNextVisualPositionFrom(paramInt1, paramBias, localRectangle, paramInt2, paramArrayOfBias);
        return i;
      }
    }
    finally
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    return -1;
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
  {
    damageRange(paramJTextComponent, paramInt1, paramInt2, Position.Bias.Forward, Position.Bias.Backward);
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2, Position.Bias paramBias1, Position.Bias paramBias2)
  {
    if (painted)
    {
      Rectangle localRectangle1 = getVisibleEditorRect();
      if (localRectangle1 != null)
      {
        Document localDocument = paramJTextComponent.getDocument();
        if ((localDocument instanceof AbstractDocument)) {
          ((AbstractDocument)localDocument).readLock();
        }
        try
        {
          rootView.setSize(width, height);
          Shape localShape = rootView.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, localRectangle1);
          Rectangle localRectangle2 = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
          editor.repaint(x, y, width, height);
        }
        catch (BadLocationException localBadLocationException) {}finally
        {
          if ((localDocument instanceof AbstractDocument)) {
            ((AbstractDocument)localDocument).readUnlock();
          }
        }
      }
    }
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent)
  {
    return defaultKit;
  }
  
  public View getRootView(JTextComponent paramJTextComponent)
  {
    return rootView;
  }
  
  public String getToolTipText(JTextComponent paramJTextComponent, Point paramPoint)
  {
    if (!painted) {
      return null;
    }
    Document localDocument = editor.getDocument();
    String str = null;
    Rectangle localRectangle = getVisibleEditorRect();
    if (localRectangle != null)
    {
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readLock();
      }
      try
      {
        str = rootView.getToolTipText(x, y, localRectangle);
      }
      finally
      {
        if ((localDocument instanceof AbstractDocument)) {
          ((AbstractDocument)localDocument).readUnlock();
        }
      }
    }
    return str;
  }
  
  public View create(Element paramElement)
  {
    return null;
  }
  
  public View create(Element paramElement, int paramInt1, int paramInt2)
  {
    return null;
  }
  
  private static DragListener getDragListener()
  {
    synchronized (DragListener.class)
    {
      DragListener localDragListener = (DragListener)AppContext.getAppContext().get(DragListener.class);
      if (localDragListener == null)
      {
        localDragListener = new DragListener();
        AppContext.getAppContext().put(DragListener.class, localDragListener);
      }
      return localDragListener;
    }
  }
  
  public static class BasicCaret
    extends DefaultCaret
    implements UIResource
  {
    public BasicCaret() {}
  }
  
  static class BasicCursor
    extends Cursor
    implements UIResource
  {
    BasicCursor(int paramInt)
    {
      super();
    }
    
    BasicCursor(String paramString)
    {
      super();
    }
  }
  
  public static class BasicHighlighter
    extends DefaultHighlighter
    implements UIResource
  {
    public BasicHighlighter() {}
  }
  
  static class DragListener
    extends MouseInputAdapter
    implements DragRecognitionSupport.BeforeDrag
  {
    private boolean dragStarted;
    
    DragListener() {}
    
    public void dragStarting(MouseEvent paramMouseEvent)
    {
      dragStarted = true;
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramMouseEvent.getSource();
      if (localJTextComponent.getDragEnabled())
      {
        dragStarted = false;
        if ((isDragPossible(paramMouseEvent)) && (DragRecognitionSupport.mousePressed(paramMouseEvent))) {
          paramMouseEvent.consume();
        }
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramMouseEvent.getSource();
      if (localJTextComponent.getDragEnabled())
      {
        if (dragStarted) {
          paramMouseEvent.consume();
        }
        DragRecognitionSupport.mouseReleased(paramMouseEvent);
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramMouseEvent.getSource();
      if ((localJTextComponent.getDragEnabled()) && ((dragStarted) || (DragRecognitionSupport.mouseDragged(paramMouseEvent, this)))) {
        paramMouseEvent.consume();
      }
    }
    
    protected boolean isDragPossible(MouseEvent paramMouseEvent)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramMouseEvent.getSource();
      if (localJTextComponent.isEnabled())
      {
        Caret localCaret = localJTextComponent.getCaret();
        int i = localCaret.getDot();
        int j = localCaret.getMark();
        if (i != j)
        {
          Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
          int k = localJTextComponent.viewToModel(localPoint);
          int m = Math.min(i, j);
          int n = Math.max(i, j);
          if ((k >= m) && (k < n)) {
            return true;
          }
        }
      }
      return false;
    }
  }
  
  class FocusAction
    extends AbstractAction
  {
    FocusAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      editor.requestFocus();
    }
    
    public boolean isEnabled()
    {
      return editor.isEditable();
    }
  }
  
  class RootView
    extends View
  {
    private View view;
    
    RootView()
    {
      super();
    }
    
    void setView(View paramView)
    {
      View localView = view;
      view = null;
      if (localView != null) {
        localView.setParent(null);
      }
      if (paramView != null) {
        paramView.setParent(this);
      }
      view = paramView;
    }
    
    public AttributeSet getAttributes()
    {
      return null;
    }
    
    public float getPreferredSpan(int paramInt)
    {
      if (view != null) {
        return view.getPreferredSpan(paramInt);
      }
      return 10.0F;
    }
    
    public float getMinimumSpan(int paramInt)
    {
      if (view != null) {
        return view.getMinimumSpan(paramInt);
      }
      return 10.0F;
    }
    
    public float getMaximumSpan(int paramInt)
    {
      return 2.14748365E9F;
    }
    
    public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      editor.revalidate();
    }
    
    public float getAlignment(int paramInt)
    {
      if (view != null) {
        return view.getAlignment(paramInt);
      }
      return 0.0F;
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      if (view != null)
      {
        Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        setSize(width, height);
        view.paint(paramGraphics, paramShape);
      }
    }
    
    public void setParent(View paramView)
    {
      throw new Error("Can't set parent on root view");
    }
    
    public int getViewCount()
    {
      return 1;
    }
    
    public View getView(int paramInt)
    {
      return view;
    }
    
    public int getViewIndex(int paramInt, Position.Bias paramBias)
    {
      return 0;
    }
    
    public Shape getChildAllocation(int paramInt, Shape paramShape)
    {
      return paramShape;
    }
    
    public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
      throws BadLocationException
    {
      if (view != null) {
        return view.modelToView(paramInt, paramShape, paramBias);
      }
      return null;
    }
    
    public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape)
      throws BadLocationException
    {
      if (view != null) {
        return view.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, paramShape);
      }
      return null;
    }
    
    public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
    {
      if (view != null)
      {
        int i = view.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
        return i;
      }
      return -1;
    }
    
    public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
      throws BadLocationException
    {
      if (paramInt1 < -1) {
        throw new BadLocationException("invalid position", paramInt1);
      }
      if (view != null)
      {
        int i = view.getNextVisualPositionFrom(paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
        if (i != -1) {
          paramInt1 = i;
        } else {
          paramArrayOfBias[0] = paramBias;
        }
      }
      return paramInt1;
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (view != null) {
        view.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (view != null) {
        view.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (view != null) {
        view.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
    
    public Document getDocument()
    {
      return editor.getDocument();
    }
    
    public int getStartOffset()
    {
      if (view != null) {
        return view.getStartOffset();
      }
      return getElement().getStartOffset();
    }
    
    public int getEndOffset()
    {
      if (view != null) {
        return view.getEndOffset();
      }
      return getElement().getEndOffset();
    }
    
    public Element getElement()
    {
      if (view != null) {
        return view.getElement();
      }
      return editor.getDocument().getDefaultRootElement();
    }
    
    public View breakView(int paramInt, float paramFloat, Shape paramShape)
    {
      throw new Error("Can't break root view");
    }
    
    public int getResizeWeight(int paramInt)
    {
      if (view != null) {
        return view.getResizeWeight(paramInt);
      }
      return 0;
    }
    
    public void setSize(float paramFloat1, float paramFloat2)
    {
      if (view != null) {
        view.setSize(paramFloat1, paramFloat2);
      }
    }
    
    public Container getContainer()
    {
      return editor;
    }
    
    public ViewFactory getViewFactory()
    {
      EditorKit localEditorKit = getEditorKit(editor);
      ViewFactory localViewFactory = localEditorKit.getViewFactory();
      if (localViewFactory != null) {
        return localViewFactory;
      }
      return BasicTextUI.this;
    }
  }
  
  class TextActionWrapper
    extends TextAction
  {
    TextAction action = null;
    
    public TextActionWrapper(TextAction paramTextAction)
    {
      super();
      action = paramTextAction;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      action.actionPerformed(paramActionEvent);
    }
    
    public boolean isEnabled()
    {
      return (editor == null) || (editor.isEditable()) ? action.isEnabled() : false;
    }
  }
  
  static class TextTransferHandler
    extends TransferHandler
    implements UIResource
  {
    private JTextComponent exportComp;
    private boolean shouldRemove;
    private int p0;
    private int p1;
    private boolean modeBetween = false;
    private boolean isDrop = false;
    private int dropAction = 2;
    private Position.Bias dropBias;
    
    TextTransferHandler() {}
    
    protected DataFlavor getImportFlavor(DataFlavor[] paramArrayOfDataFlavor, JTextComponent paramJTextComponent)
    {
      DataFlavor localDataFlavor1 = null;
      DataFlavor localDataFlavor2 = null;
      DataFlavor localDataFlavor3 = null;
      String str;
      if ((paramJTextComponent instanceof JEditorPane))
      {
        for (i = 0; i < paramArrayOfDataFlavor.length; i++)
        {
          str = paramArrayOfDataFlavor[i].getMimeType();
          if (str.startsWith(((JEditorPane)paramJTextComponent).getEditorKit().getContentType())) {
            return paramArrayOfDataFlavor[i];
          }
          if ((localDataFlavor1 == null) && (str.startsWith("text/plain"))) {
            localDataFlavor1 = paramArrayOfDataFlavor[i];
          } else if ((localDataFlavor2 == null) && (str.startsWith("application/x-java-jvm-local-objectref")) && (paramArrayOfDataFlavor[i].getRepresentationClass() == String.class)) {
            localDataFlavor2 = paramArrayOfDataFlavor[i];
          } else if ((localDataFlavor3 == null) && (paramArrayOfDataFlavor[i].equals(DataFlavor.stringFlavor))) {
            localDataFlavor3 = paramArrayOfDataFlavor[i];
          }
        }
        if (localDataFlavor1 != null) {
          return localDataFlavor1;
        }
        if (localDataFlavor2 != null) {
          return localDataFlavor2;
        }
        if (localDataFlavor3 != null) {
          return localDataFlavor3;
        }
        return null;
      }
      for (int i = 0; i < paramArrayOfDataFlavor.length; i++)
      {
        str = paramArrayOfDataFlavor[i].getMimeType();
        if (str.startsWith("text/plain")) {
          return paramArrayOfDataFlavor[i];
        }
        if ((localDataFlavor2 == null) && (str.startsWith("application/x-java-jvm-local-objectref")) && (paramArrayOfDataFlavor[i].getRepresentationClass() == String.class)) {
          localDataFlavor2 = paramArrayOfDataFlavor[i];
        } else if ((localDataFlavor3 == null) && (paramArrayOfDataFlavor[i].equals(DataFlavor.stringFlavor))) {
          localDataFlavor3 = paramArrayOfDataFlavor[i];
        }
      }
      if (localDataFlavor2 != null) {
        return localDataFlavor2;
      }
      if (localDataFlavor3 != null) {
        return localDataFlavor3;
      }
      return null;
    }
    
    protected void handleReaderImport(Reader paramReader, JTextComponent paramJTextComponent, boolean paramBoolean)
      throws BadLocationException, IOException
    {
      int j;
      int k;
      Object localObject;
      if (paramBoolean)
      {
        int i = paramJTextComponent.getSelectionStart();
        j = paramJTextComponent.getSelectionEnd();
        k = j - i;
        EditorKit localEditorKit = paramJTextComponent.getUI().getEditorKit(paramJTextComponent);
        localObject = paramJTextComponent.getDocument();
        if (k > 0) {
          ((Document)localObject).remove(i, k);
        }
        localEditorKit.read(paramReader, (Document)localObject, i);
      }
      else
      {
        char[] arrayOfChar = new char['Ѐ'];
        k = 0;
        localObject = null;
        while ((j = paramReader.read(arrayOfChar, 0, arrayOfChar.length)) != -1)
        {
          if (localObject == null) {
            localObject = new StringBuffer(j);
          }
          int m = 0;
          for (int n = 0; n < j; n++) {
            switch (arrayOfChar[n])
            {
            case '\r': 
              if (k != 0)
              {
                if (n == 0) {
                  ((StringBuffer)localObject).append('\n');
                } else {
                  arrayOfChar[(n - 1)] = '\n';
                }
              }
              else {
                k = 1;
              }
              break;
            case '\n': 
              if (k != 0)
              {
                if (n > m + 1) {
                  ((StringBuffer)localObject).append(arrayOfChar, m, n - m - 1);
                }
                k = 0;
                m = n;
              }
              break;
            default: 
              if (k != 0)
              {
                if (n == 0) {
                  ((StringBuffer)localObject).append('\n');
                } else {
                  arrayOfChar[(n - 1)] = '\n';
                }
                k = 0;
              }
              break;
            }
          }
          if (m < j) {
            if (k != 0)
            {
              if (m < j - 1) {
                ((StringBuffer)localObject).append(arrayOfChar, m, j - m - 1);
              }
            }
            else {
              ((StringBuffer)localObject).append(arrayOfChar, m, j - m);
            }
          }
        }
        if (k != 0) {
          ((StringBuffer)localObject).append('\n');
        }
        paramJTextComponent.replaceSelection(localObject != null ? ((StringBuffer)localObject).toString() : "");
      }
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      if (((paramJComponent instanceof JPasswordField)) && (paramJComponent.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE)) {
        return 0;
      }
      return ((JTextComponent)paramJComponent).isEditable() ? 3 : 1;
    }
    
    protected Transferable createTransferable(JComponent paramJComponent)
    {
      exportComp = ((JTextComponent)paramJComponent);
      shouldRemove = true;
      p0 = exportComp.getSelectionStart();
      p1 = exportComp.getSelectionEnd();
      return p0 != p1 ? new TextTransferable(exportComp, p0, p1) : null;
    }
    
    protected void exportDone(JComponent paramJComponent, Transferable paramTransferable, int paramInt)
    {
      if ((shouldRemove) && (paramInt == 2))
      {
        TextTransferable localTextTransferable = (TextTransferable)paramTransferable;
        localTextTransferable.removeText();
      }
      exportComp = null;
    }
    
    public boolean importData(TransferHandler.TransferSupport paramTransferSupport)
    {
      isDrop = paramTransferSupport.isDrop();
      if (isDrop)
      {
        modeBetween = (((JTextComponent)paramTransferSupport.getComponent()).getDropMode() == DropMode.INSERT);
        dropBias = ((JTextComponent.DropLocation)paramTransferSupport.getDropLocation()).getBias();
        dropAction = paramTransferSupport.getDropAction();
      }
      try
      {
        boolean bool = super.importData(paramTransferSupport);
        return bool;
      }
      finally
      {
        isDrop = false;
        modeBetween = false;
        dropBias = null;
        dropAction = 2;
      }
    }
    
    public boolean importData(JComponent paramJComponent, Transferable paramTransferable)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramJComponent;
      int i = modeBetween ? localJTextComponent.getDropLocation().getIndex() : localJTextComponent.getCaretPosition();
      if ((dropAction == 2) && (localJTextComponent == exportComp) && (i >= p0) && (i <= p1))
      {
        shouldRemove = false;
        return true;
      }
      boolean bool1 = false;
      DataFlavor localDataFlavor = getImportFlavor(paramTransferable.getTransferDataFlavors(), localJTextComponent);
      if (localDataFlavor != null) {
        try
        {
          boolean bool2 = false;
          if ((paramJComponent instanceof JEditorPane))
          {
            localObject = (JEditorPane)paramJComponent;
            if ((!((JEditorPane)localObject).getContentType().startsWith("text/plain")) && (localDataFlavor.getMimeType().startsWith(((JEditorPane)localObject).getContentType()))) {
              bool2 = true;
            }
          }
          Object localObject = localJTextComponent.getInputContext();
          if (localObject != null) {
            ((InputContext)localObject).endComposition();
          }
          Reader localReader = localDataFlavor.getReaderForText(paramTransferable);
          Caret localCaret;
          if (modeBetween)
          {
            localCaret = localJTextComponent.getCaret();
            if ((localCaret instanceof DefaultCaret)) {
              ((DefaultCaret)localCaret).setDot(i, dropBias);
            } else {
              localJTextComponent.setCaretPosition(i);
            }
          }
          handleReaderImport(localReader, localJTextComponent, bool2);
          if (isDrop)
          {
            localJTextComponent.requestFocus();
            localCaret = localJTextComponent.getCaret();
            if ((localCaret instanceof DefaultCaret))
            {
              int j = localCaret.getDot();
              Position.Bias localBias = ((DefaultCaret)localCaret).getDotBias();
              ((DefaultCaret)localCaret).setDot(i, dropBias);
              ((DefaultCaret)localCaret).moveDot(j, localBias);
            }
            else
            {
              localJTextComponent.select(i, localJTextComponent.getCaretPosition());
            }
          }
          bool1 = true;
        }
        catch (UnsupportedFlavorException localUnsupportedFlavorException) {}catch (BadLocationException localBadLocationException) {}catch (IOException localIOException) {}
      }
      return bool1;
    }
    
    public boolean canImport(JComponent paramJComponent, DataFlavor[] paramArrayOfDataFlavor)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramJComponent;
      if ((!localJTextComponent.isEditable()) || (!localJTextComponent.isEnabled())) {
        return false;
      }
      return getImportFlavor(paramArrayOfDataFlavor, localJTextComponent) != null;
    }
    
    static class TextTransferable
      extends BasicTransferable
    {
      Position p0;
      Position p1;
      String mimeType;
      String richText;
      JTextComponent c;
      
      TextTransferable(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
      {
        super(null);
        c = paramJTextComponent;
        Document localDocument = paramJTextComponent.getDocument();
        try
        {
          p0 = localDocument.createPosition(paramInt1);
          p1 = localDocument.createPosition(paramInt2);
          plainData = paramJTextComponent.getSelectedText();
          if ((paramJTextComponent instanceof JEditorPane))
          {
            JEditorPane localJEditorPane = (JEditorPane)paramJTextComponent;
            mimeType = localJEditorPane.getContentType();
            if (mimeType.startsWith("text/plain")) {
              return;
            }
            StringWriter localStringWriter = new StringWriter(p1.getOffset() - p0.getOffset());
            localJEditorPane.getEditorKit().write(localStringWriter, localDocument, p0.getOffset(), p1.getOffset() - p0.getOffset());
            if (mimeType.startsWith("text/html")) {
              htmlData = localStringWriter.toString();
            } else {
              richText = localStringWriter.toString();
            }
          }
        }
        catch (BadLocationException localBadLocationException) {}catch (IOException localIOException) {}
      }
      
      void removeText()
      {
        if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
          try
          {
            Document localDocument = c.getDocument();
            localDocument.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
          }
          catch (BadLocationException localBadLocationException) {}
        }
      }
      
      protected DataFlavor[] getRicherFlavors()
      {
        if (richText == null) {
          return null;
        }
        try
        {
          DataFlavor[] arrayOfDataFlavor = new DataFlavor[3];
          arrayOfDataFlavor[0] = new DataFlavor(mimeType + ";class=java.lang.String");
          arrayOfDataFlavor[1] = new DataFlavor(mimeType + ";class=java.io.Reader");
          arrayOfDataFlavor[2] = new DataFlavor(mimeType + ";class=java.io.InputStream;charset=unicode");
          return arrayOfDataFlavor;
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
        return null;
      }
      
      protected Object getRicherData(DataFlavor paramDataFlavor)
        throws UnsupportedFlavorException
      {
        if (richText == null) {
          return null;
        }
        if (String.class.equals(paramDataFlavor.getRepresentationClass())) {
          return richText;
        }
        if (Reader.class.equals(paramDataFlavor.getRepresentationClass())) {
          return new StringReader(richText);
        }
        if (InputStream.class.equals(paramDataFlavor.getRepresentationClass())) {
          return new StringBufferInputStream(richText);
        }
        throw new UnsupportedFlavorException(paramDataFlavor);
      }
    }
  }
  
  class UpdateHandler
    implements PropertyChangeListener, DocumentListener, LayoutManager2, UIResource
  {
    private Hashtable<Component, Object> constraints;
    private boolean i18nView = false;
    
    UpdateHandler() {}
    
    public final void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      String str = paramPropertyChangeEvent.getPropertyName();
      if (((localObject1 instanceof Document)) || ((localObject2 instanceof Document)))
      {
        if (localObject1 != null)
        {
          ((Document)localObject1).removeDocumentListener(this);
          i18nView = false;
        }
        if (localObject2 != null)
        {
          ((Document)localObject2).addDocumentListener(this);
          if ("document" == str)
          {
            setView(null);
            BasicTextUI.this.propertyChange(paramPropertyChangeEvent);
            modelChanged();
            return;
          }
        }
        modelChanged();
      }
      if ("focusAccelerator" == str)
      {
        updateFocusAcceleratorBinding(true);
      }
      else if ("componentOrientation" == str)
      {
        modelChanged();
      }
      else if ("font" == str)
      {
        modelChanged();
      }
      else if ("dropLocation" == str)
      {
        dropIndexChanged();
      }
      else if ("editable" == str)
      {
        BasicTextUI.this.updateCursor();
        modelChanged();
      }
      BasicTextUI.this.propertyChange(paramPropertyChangeEvent);
    }
    
    private void dropIndexChanged()
    {
      if (editor.getDropMode() == DropMode.USE_SELECTION) {
        return;
      }
      JTextComponent.DropLocation localDropLocation = editor.getDropLocation();
      if (localDropLocation == null)
      {
        if (dropCaret != null)
        {
          dropCaret.deinstall(editor);
          editor.repaint(dropCaret);
          dropCaret = null;
        }
      }
      else
      {
        if (dropCaret == null)
        {
          dropCaret = new BasicTextUI.BasicCaret();
          dropCaret.install(editor);
          dropCaret.setVisible(true);
        }
        dropCaret.setDot(localDropLocation.getIndex(), localDropLocation.getBias());
      }
    }
    
    public final void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      Document localDocument = paramDocumentEvent.getDocument();
      Object localObject = localDocument.getProperty("i18n");
      if ((localObject instanceof Boolean))
      {
        localBoolean = (Boolean)localObject;
        if (localBoolean.booleanValue() != i18nView)
        {
          i18nView = localBoolean.booleanValue();
          modelChanged();
          return;
        }
      }
      Boolean localBoolean = painted ? getVisibleEditorRect() : null;
      rootView.insertUpdate(paramDocumentEvent, localBoolean, rootView.getViewFactory());
    }
    
    public final void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      Shape localShape = painted ? getVisibleEditorRect() : null;
      rootView.removeUpdate(paramDocumentEvent, localShape, rootView.getViewFactory());
    }
    
    public final void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      Shape localShape = painted ? getVisibleEditorRect() : null;
      rootView.changedUpdate(paramDocumentEvent, localShape, rootView.getViewFactory());
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent)
    {
      if (constraints != null) {
        constraints.remove(paramComponent);
      }
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return null;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return null;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      if ((constraints != null) && (!constraints.isEmpty()))
      {
        Rectangle localRectangle1 = getVisibleEditorRect();
        if (localRectangle1 != null)
        {
          Document localDocument = editor.getDocument();
          if ((localDocument instanceof AbstractDocument)) {
            ((AbstractDocument)localDocument).readLock();
          }
          try
          {
            rootView.setSize(width, height);
            Enumeration localEnumeration = constraints.keys();
            while (localEnumeration.hasMoreElements())
            {
              Component localComponent = (Component)localEnumeration.nextElement();
              View localView = (View)constraints.get(localComponent);
              Shape localShape = calculateViewPosition(localRectangle1, localView);
              if (localShape != null)
              {
                Rectangle localRectangle2 = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
                localComponent.setBounds(localRectangle2);
              }
            }
          }
          finally
          {
            if ((localDocument instanceof AbstractDocument)) {
              ((AbstractDocument)localDocument).readUnlock();
            }
          }
        }
      }
    }
    
    Shape calculateViewPosition(Shape paramShape, View paramView)
    {
      int i = paramView.getStartOffset();
      View localView = null;
      for (Object localObject = rootView; (localObject != null) && (localObject != paramView); localObject = localView)
      {
        int j = ((View)localObject).getViewIndex(i, Position.Bias.Forward);
        paramShape = ((View)localObject).getChildAllocation(j, paramShape);
        localView = ((View)localObject).getView(j);
      }
      return localView != null ? paramShape : null;
    }
    
    public void addLayoutComponent(Component paramComponent, Object paramObject)
    {
      if ((paramObject instanceof View))
      {
        if (constraints == null) {
          constraints = new Hashtable(7);
        }
        constraints.put(paramComponent, paramObject);
      }
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      return null;
    }
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return 0.5F;
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return 0.5F;
    }
    
    public void invalidateLayout(Container paramContainer) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */