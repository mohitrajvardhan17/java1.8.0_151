package javax.swing.text;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import sun.swing.SwingUtilities2;

public class DefaultCaret
  extends Rectangle
  implements Caret, FocusListener, MouseListener, MouseMotionListener
{
  public static final int UPDATE_WHEN_ON_EDT = 0;
  public static final int NEVER_UPDATE = 1;
  public static final int ALWAYS_UPDATE = 2;
  protected EventListenerList listenerList = new EventListenerList();
  protected transient ChangeEvent changeEvent = null;
  JTextComponent component;
  int updatePolicy = 0;
  boolean visible;
  boolean active;
  int dot;
  int mark;
  Object selectionTag;
  boolean selectionVisible;
  Timer flasher;
  Point magicCaretPosition;
  transient Position.Bias dotBias;
  transient Position.Bias markBias;
  boolean dotLTR;
  boolean markLTR;
  transient Handler handler = new Handler();
  private transient int[] flagXPoints = new int[3];
  private transient int[] flagYPoints = new int[3];
  private transient NavigationFilter.FilterBypass filterBypass;
  private static transient Action selectWord = null;
  private static transient Action selectLine = null;
  private boolean ownsSelection;
  private boolean forceCaretPositionChange;
  private transient boolean shouldHandleRelease;
  private transient MouseEvent selectedWordEvent = null;
  private int caretWidth = -1;
  private float aspectRatio = -1.0F;
  
  public DefaultCaret() {}
  
  public void setUpdatePolicy(int paramInt)
  {
    updatePolicy = paramInt;
  }
  
  public int getUpdatePolicy()
  {
    return updatePolicy;
  }
  
  protected final JTextComponent getComponent()
  {
    return component;
  }
  
  protected final synchronized void repaint()
  {
    if (component != null) {
      component.repaint(x, y, width, height);
    }
  }
  
  protected synchronized void damage(Rectangle paramRectangle)
  {
    if (paramRectangle != null)
    {
      int i = getCaretWidth(height);
      x = (x - 4 - (i >> 1));
      y = y;
      width = (9 + i);
      height = height;
      repaint();
    }
  }
  
  protected void adjustVisibility(Rectangle paramRectangle)
  {
    if (component == null) {
      return;
    }
    if (SwingUtilities.isEventDispatchThread()) {
      component.scrollRectToVisible(paramRectangle);
    } else {
      SwingUtilities.invokeLater(new SafeScroller(paramRectangle));
    }
  }
  
  protected Highlighter.HighlightPainter getSelectionPainter()
  {
    return DefaultHighlighter.DefaultPainter;
  }
  
  protected void positionCaret(MouseEvent paramMouseEvent)
  {
    Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = component.getUI().viewToModel(component, localPoint, arrayOfBias);
    if (arrayOfBias[0] == null) {
      arrayOfBias[0] = Position.Bias.Forward;
    }
    if (i >= 0) {
      setDot(i, arrayOfBias[0]);
    }
  }
  
  protected void moveCaret(MouseEvent paramMouseEvent)
  {
    Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = component.getUI().viewToModel(component, localPoint, arrayOfBias);
    if (arrayOfBias[0] == null) {
      arrayOfBias[0] = Position.Bias.Forward;
    }
    if (i >= 0) {
      moveDot(i, arrayOfBias[0]);
    }
  }
  
  public void focusGained(FocusEvent paramFocusEvent)
  {
    if (component.isEnabled())
    {
      if (component.isEditable()) {
        setVisible(true);
      }
      setSelectionVisible(true);
    }
  }
  
  public void focusLost(FocusEvent paramFocusEvent)
  {
    setVisible(false);
    setSelectionVisible((ownsSelection) || (paramFocusEvent.isTemporary()));
  }
  
  private void selectWord(MouseEvent paramMouseEvent)
  {
    if ((selectedWordEvent != null) && (selectedWordEvent.getX() == paramMouseEvent.getX()) && (selectedWordEvent.getY() == paramMouseEvent.getY())) {
      return;
    }
    Action localAction = null;
    ActionMap localActionMap = getComponent().getActionMap();
    if (localActionMap != null) {
      localAction = localActionMap.get("select-word");
    }
    if (localAction == null)
    {
      if (selectWord == null) {
        selectWord = new DefaultEditorKit.SelectWordAction();
      }
      localAction = selectWord;
    }
    localAction.actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
    selectedWordEvent = paramMouseEvent;
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    if (getComponent() == null) {
      return;
    }
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (!paramMouseEvent.isConsumed())
    {
      Object localObject1;
      Object localObject2;
      if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
      {
        if (i == 1)
        {
          selectedWordEvent = null;
        }
        else if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
        {
          selectWord(paramMouseEvent);
          selectedWordEvent = null;
        }
        else if ((i == 3) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
        {
          localObject1 = null;
          localObject2 = getComponent().getActionMap();
          if (localObject2 != null) {
            localObject1 = ((ActionMap)localObject2).get("select-line");
          }
          if (localObject1 == null)
          {
            if (selectLine == null) {
              selectLine = new DefaultEditorKit.SelectLineAction();
            }
            localObject1 = selectLine;
          }
          ((Action)localObject1).actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
        }
      }
      else if ((SwingUtilities.isMiddleMouseButton(paramMouseEvent)) && (i == 1) && (component.isEditable()) && (component.isEnabled()) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)))
      {
        localObject1 = (JTextComponent)paramMouseEvent.getSource();
        if (localObject1 != null) {
          try
          {
            localObject2 = ((JTextComponent)localObject1).getToolkit();
            Clipboard localClipboard = ((Toolkit)localObject2).getSystemSelection();
            if (localClipboard != null)
            {
              adjustCaret(paramMouseEvent);
              TransferHandler localTransferHandler = ((JTextComponent)localObject1).getTransferHandler();
              if (localTransferHandler != null)
              {
                Transferable localTransferable = null;
                try
                {
                  localTransferable = localClipboard.getContents(null);
                }
                catch (IllegalStateException localIllegalStateException)
                {
                  UIManager.getLookAndFeel().provideErrorFeedback((Component)localObject1);
                }
                if (localTransferable != null) {
                  localTransferHandler.importData((JComponent)localObject1, localTransferable);
                }
              }
              adjustFocus(true);
            }
          }
          catch (HeadlessException localHeadlessException) {}
        }
      }
    }
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      if (paramMouseEvent.isConsumed())
      {
        shouldHandleRelease = true;
      }
      else
      {
        shouldHandleRelease = false;
        adjustCaretAndFocus(paramMouseEvent);
        if ((i == 2) && (SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent))) {
          selectWord(paramMouseEvent);
        }
      }
    }
  }
  
  void adjustCaretAndFocus(MouseEvent paramMouseEvent)
  {
    adjustCaret(paramMouseEvent);
    adjustFocus(false);
  }
  
  private void adjustCaret(MouseEvent paramMouseEvent)
  {
    if (((paramMouseEvent.getModifiers() & 0x1) != 0) && (getDot() != -1)) {
      moveCaret(paramMouseEvent);
    } else if (!paramMouseEvent.isPopupTrigger()) {
      positionCaret(paramMouseEvent);
    }
  }
  
  private void adjustFocus(boolean paramBoolean)
  {
    if ((component != null) && (component.isEnabled()) && (component.isRequestFocusEnabled())) {
      if (paramBoolean) {
        component.requestFocusInWindow();
      } else {
        component.requestFocus();
      }
    }
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    if ((!paramMouseEvent.isConsumed()) && (shouldHandleRelease) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      adjustCaretAndFocus(paramMouseEvent);
    }
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    if ((!paramMouseEvent.isConsumed()) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      moveCaret(paramMouseEvent);
    }
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void paint(Graphics paramGraphics)
  {
    if (isVisible()) {
      try
      {
        TextUI localTextUI = component.getUI();
        Rectangle localRectangle1 = localTextUI.modelToView(component, dot, dotBias);
        if ((localRectangle1 == null) || ((width == 0) && (height == 0))) {
          return;
        }
        if ((width > 0) && (height > 0) && (!_contains(x, y, width, height)))
        {
          Rectangle localRectangle2 = paramGraphics.getClipBounds();
          if ((localRectangle2 != null) && (!localRectangle2.contains(this))) {
            repaint();
          }
          damage(localRectangle1);
        }
        paramGraphics.setColor(component.getCaretColor());
        int i = getCaretWidth(height);
        x -= (i >> 1);
        paramGraphics.fillRect(x, y, i, height);
        Document localDocument = component.getDocument();
        if ((localDocument instanceof AbstractDocument))
        {
          Element localElement = ((AbstractDocument)localDocument).getBidiRootElement();
          if ((localElement != null) && (localElement.getElementCount() > 1))
          {
            flagXPoints[0] = (x + (dotLTR ? i : 0));
            flagYPoints[0] = y;
            flagXPoints[1] = flagXPoints[0];
            flagYPoints[1] = (flagYPoints[0] + 4);
            flagXPoints[2] = (flagXPoints[0] + (dotLTR ? 4 : -4));
            flagYPoints[2] = flagYPoints[0];
            paramGraphics.fillPolygon(flagXPoints, flagYPoints, 3);
          }
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
  }
  
  public void install(JTextComponent paramJTextComponent)
  {
    component = paramJTextComponent;
    Document localDocument = paramJTextComponent.getDocument();
    dot = (mark = 0);
    dotLTR = (markLTR = 1);
    dotBias = (markBias = Position.Bias.Forward);
    if (localDocument != null) {
      localDocument.addDocumentListener(handler);
    }
    paramJTextComponent.addPropertyChangeListener(handler);
    paramJTextComponent.addFocusListener(this);
    paramJTextComponent.addMouseListener(this);
    paramJTextComponent.addMouseMotionListener(this);
    if (component.hasFocus()) {
      focusGained(null);
    }
    Number localNumber = (Number)paramJTextComponent.getClientProperty("caretAspectRatio");
    if (localNumber != null) {
      aspectRatio = localNumber.floatValue();
    } else {
      aspectRatio = -1.0F;
    }
    Integer localInteger = (Integer)paramJTextComponent.getClientProperty("caretWidth");
    if (localInteger != null) {
      caretWidth = localInteger.intValue();
    } else {
      caretWidth = -1;
    }
  }
  
  public void deinstall(JTextComponent paramJTextComponent)
  {
    paramJTextComponent.removeMouseListener(this);
    paramJTextComponent.removeMouseMotionListener(this);
    paramJTextComponent.removeFocusListener(this);
    paramJTextComponent.removePropertyChangeListener(handler);
    Document localDocument = paramJTextComponent.getDocument();
    if (localDocument != null) {
      localDocument.removeDocumentListener(handler);
    }
    synchronized (this)
    {
      component = null;
    }
    if (flasher != null) {
      flasher.stop();
    }
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  public void setSelectionVisible(boolean paramBoolean)
  {
    if (paramBoolean != selectionVisible)
    {
      selectionVisible = paramBoolean;
      Highlighter localHighlighter;
      if (selectionVisible)
      {
        localHighlighter = component.getHighlighter();
        if ((dot != mark) && (localHighlighter != null) && (selectionTag == null))
        {
          int i = Math.min(dot, mark);
          int j = Math.max(dot, mark);
          Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
          try
          {
            selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
          }
          catch (BadLocationException localBadLocationException)
          {
            selectionTag = null;
          }
        }
      }
      else if (selectionTag != null)
      {
        localHighlighter = component.getHighlighter();
        localHighlighter.removeHighlight(selectionTag);
        selectionTag = null;
      }
    }
  }
  
  public boolean isSelectionVisible()
  {
    return selectionVisible;
  }
  
  public boolean isActive()
  {
    return active;
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    active = paramBoolean;
    if (component != null)
    {
      TextUI localTextUI = component.getUI();
      if (visible != paramBoolean)
      {
        visible = paramBoolean;
        try
        {
          Rectangle localRectangle = localTextUI.modelToView(component, dot, dotBias);
          damage(localRectangle);
        }
        catch (BadLocationException localBadLocationException) {}
      }
    }
    if (flasher != null) {
      if (visible) {
        flasher.start();
      } else {
        flasher.stop();
      }
    }
  }
  
  public void setBlinkRate(int paramInt)
  {
    if (paramInt != 0)
    {
      if (flasher == null) {
        flasher = new Timer(paramInt, handler);
      }
      flasher.setDelay(paramInt);
    }
    else if (flasher != null)
    {
      flasher.stop();
      flasher.removeActionListener(handler);
      flasher = null;
    }
  }
  
  public int getBlinkRate()
  {
    return flasher == null ? 0 : flasher.getDelay();
  }
  
  public int getDot()
  {
    return dot;
  }
  
  public int getMark()
  {
    return mark;
  }
  
  public void setDot(int paramInt)
  {
    setDot(paramInt, Position.Bias.Forward);
  }
  
  public void moveDot(int paramInt)
  {
    moveDot(paramInt, Position.Bias.Forward);
  }
  
  public void moveDot(int paramInt, Position.Bias paramBias)
  {
    if (paramBias == null) {
      throw new IllegalArgumentException("null bias");
    }
    if (!component.isEnabled())
    {
      setDot(paramInt, paramBias);
      return;
    }
    if (paramInt != dot)
    {
      NavigationFilter localNavigationFilter = component.getNavigationFilter();
      if (localNavigationFilter != null) {
        localNavigationFilter.moveDot(getFilterBypass(), paramInt, paramBias);
      } else {
        handleMoveDot(paramInt, paramBias);
      }
    }
  }
  
  void handleMoveDot(int paramInt, Position.Bias paramBias)
  {
    changeCaretPosition(paramInt, paramBias);
    if (selectionVisible)
    {
      Highlighter localHighlighter = component.getHighlighter();
      if (localHighlighter != null)
      {
        int i = Math.min(paramInt, mark);
        int j = Math.max(paramInt, mark);
        if (i == j)
        {
          if (selectionTag != null)
          {
            localHighlighter.removeHighlight(selectionTag);
            selectionTag = null;
          }
        }
        else {
          try
          {
            if (selectionTag != null)
            {
              localHighlighter.changeHighlight(selectionTag, i, j);
            }
            else
            {
              Highlighter.HighlightPainter localHighlightPainter = getSelectionPainter();
              selectionTag = localHighlighter.addHighlight(i, j, localHighlightPainter);
            }
          }
          catch (BadLocationException localBadLocationException)
          {
            throw new StateInvariantError("Bad caret position");
          }
        }
      }
    }
  }
  
  public void setDot(int paramInt, Position.Bias paramBias)
  {
    if (paramBias == null) {
      throw new IllegalArgumentException("null bias");
    }
    NavigationFilter localNavigationFilter = component.getNavigationFilter();
    if (localNavigationFilter != null) {
      localNavigationFilter.setDot(getFilterBypass(), paramInt, paramBias);
    } else {
      handleSetDot(paramInt, paramBias);
    }
  }
  
  void handleSetDot(int paramInt, Position.Bias paramBias)
  {
    Document localDocument = component.getDocument();
    if (localDocument != null) {
      paramInt = Math.min(paramInt, localDocument.getLength());
    }
    paramInt = Math.max(paramInt, 0);
    if (paramInt == 0) {
      paramBias = Position.Bias.Forward;
    }
    mark = paramInt;
    if ((dot != paramInt) || (dotBias != paramBias) || (selectionTag != null) || (forceCaretPositionChange)) {
      changeCaretPosition(paramInt, paramBias);
    }
    markBias = dotBias;
    markLTR = dotLTR;
    Highlighter localHighlighter = component.getHighlighter();
    if ((localHighlighter != null) && (selectionTag != null))
    {
      localHighlighter.removeHighlight(selectionTag);
      selectionTag = null;
    }
  }
  
  public Position.Bias getDotBias()
  {
    return dotBias;
  }
  
  public Position.Bias getMarkBias()
  {
    return markBias;
  }
  
  boolean isDotLeftToRight()
  {
    return dotLTR;
  }
  
  boolean isMarkLeftToRight()
  {
    return markLTR;
  }
  
  boolean isPositionLTR(int paramInt, Position.Bias paramBias)
  {
    Document localDocument = component.getDocument();
    if (paramBias == Position.Bias.Backward)
    {
      paramInt--;
      if (paramInt < 0) {
        paramInt = 0;
      }
    }
    return AbstractDocument.isLeftToRight(localDocument, paramInt, paramInt);
  }
  
  Position.Bias guessBiasForOffset(int paramInt, Position.Bias paramBias, boolean paramBoolean)
  {
    if (paramBoolean != isPositionLTR(paramInt, paramBias)) {
      paramBias = Position.Bias.Backward;
    } else if ((paramBias != Position.Bias.Backward) && (paramBoolean != isPositionLTR(paramInt, Position.Bias.Backward))) {
      paramBias = Position.Bias.Backward;
    }
    if ((paramBias == Position.Bias.Backward) && (paramInt > 0)) {
      try
      {
        Segment localSegment = new Segment();
        component.getDocument().getText(paramInt - 1, 1, localSegment);
        if ((count > 0) && (array[offset] == '\n')) {
          paramBias = Position.Bias.Forward;
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
    return paramBias;
  }
  
  void changeCaretPosition(int paramInt, Position.Bias paramBias)
  {
    repaint();
    if ((flasher != null) && (flasher.isRunning()))
    {
      visible = true;
      flasher.restart();
    }
    dot = paramInt;
    dotBias = paramBias;
    dotLTR = isPositionLTR(paramInt, paramBias);
    fireStateChanged();
    updateSystemSelection();
    setMagicCaretPosition(null);
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        repaintNewCaret();
      }
    };
    SwingUtilities.invokeLater(local1);
  }
  
  void repaintNewCaret()
  {
    if (component != null)
    {
      TextUI localTextUI = component.getUI();
      Document localDocument = component.getDocument();
      if ((localTextUI != null) && (localDocument != null))
      {
        Rectangle localRectangle;
        try
        {
          localRectangle = localTextUI.modelToView(component, dot, dotBias);
        }
        catch (BadLocationException localBadLocationException)
        {
          localRectangle = null;
        }
        if (localRectangle != null)
        {
          adjustVisibility(localRectangle);
          if (getMagicCaretPosition() == null) {
            setMagicCaretPosition(new Point(x, y));
          }
        }
        damage(localRectangle);
      }
    }
  }
  
  private void updateSystemSelection()
  {
    if (!SwingUtilities2.canCurrentEventAccessSystemClipboard()) {
      return;
    }
    if ((dot != mark) && (component != null) && (component.hasFocus()))
    {
      Clipboard localClipboard = getSystemSelection();
      if (localClipboard != null)
      {
        String str;
        if (((component instanceof JPasswordField)) && (component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE))
        {
          StringBuilder localStringBuilder = null;
          char c = ((JPasswordField)component).getEchoChar();
          int i = Math.min(getDot(), getMark());
          int j = Math.max(getDot(), getMark());
          for (int k = i; k < j; k++)
          {
            if (localStringBuilder == null) {
              localStringBuilder = new StringBuilder();
            }
            localStringBuilder.append(c);
          }
          str = localStringBuilder != null ? localStringBuilder.toString() : null;
        }
        else
        {
          str = component.getSelectedText();
        }
        try
        {
          localClipboard.setContents(new StringSelection(str), getClipboardOwner());
          ownsSelection = true;
        }
        catch (IllegalStateException localIllegalStateException) {}
      }
    }
  }
  
  private Clipboard getSystemSelection()
  {
    try
    {
      return component.getToolkit().getSystemSelection();
    }
    catch (HeadlessException localHeadlessException) {}catch (SecurityException localSecurityException) {}
    return null;
  }
  
  private ClipboardOwner getClipboardOwner()
  {
    return handler;
  }
  
  private void ensureValidPosition()
  {
    int i = component.getDocument().getLength();
    if ((dot > i) || (mark > i)) {
      handleSetDot(i, Position.Bias.Forward);
    }
  }
  
  public void setMagicCaretPosition(Point paramPoint)
  {
    magicCaretPosition = paramPoint;
  }
  
  public Point getMagicCaretPosition()
  {
    return magicCaretPosition;
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  public String toString()
  {
    String str = "Dot=(" + dot + ", " + dotBias + ")";
    str = str + " Mark=(" + mark + ", " + markBias + ")";
    return str;
  }
  
  private NavigationFilter.FilterBypass getFilterBypass()
  {
    if (filterBypass == null) {
      filterBypass = new DefaultFilterBypass(null);
    }
    return filterBypass;
  }
  
  private boolean _contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = width;
    int j = height;
    if ((i | j | paramInt3 | paramInt4) < 0) {
      return false;
    }
    int k = x;
    int m = y;
    if ((paramInt1 < k) || (paramInt2 < m)) {
      return false;
    }
    if (paramInt3 > 0)
    {
      i += k;
      paramInt3 += paramInt1;
      if (paramInt3 <= paramInt1)
      {
        if ((i >= k) || (paramInt3 > i)) {
          return false;
        }
      }
      else if ((i >= k) && (paramInt3 > i)) {
        return false;
      }
    }
    else if (k + i < paramInt1)
    {
      return false;
    }
    if (paramInt4 > 0)
    {
      j += m;
      paramInt4 += paramInt2;
      if (paramInt4 <= paramInt2)
      {
        if ((j >= m) || (paramInt4 > j)) {
          return false;
        }
      }
      else if ((j >= m) && (paramInt4 > j)) {
        return false;
      }
    }
    else if (m + j < paramInt2)
    {
      return false;
    }
    return true;
  }
  
  int getCaretWidth(int paramInt)
  {
    if (aspectRatio > -1.0F) {
      return (int)(aspectRatio * paramInt) + 1;
    }
    if (caretWidth > -1) {
      return caretWidth;
    }
    Object localObject = UIManager.get("Caret.width");
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    handler = new Handler();
    if (!paramObjectInputStream.readBoolean()) {
      dotBias = Position.Bias.Forward;
    } else {
      dotBias = Position.Bias.Backward;
    }
    if (!paramObjectInputStream.readBoolean()) {
      markBias = Position.Bias.Forward;
    } else {
      markBias = Position.Bias.Backward;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeBoolean(dotBias == Position.Bias.Backward);
    paramObjectOutputStream.writeBoolean(markBias == Position.Bias.Backward);
  }
  
  private class DefaultFilterBypass
    extends NavigationFilter.FilterBypass
  {
    private DefaultFilterBypass() {}
    
    public Caret getCaret()
    {
      return DefaultCaret.this;
    }
    
    public void setDot(int paramInt, Position.Bias paramBias)
    {
      handleSetDot(paramInt, paramBias);
    }
    
    public void moveDot(int paramInt, Position.Bias paramBias)
    {
      handleMoveDot(paramInt, paramBias);
    }
  }
  
  class Handler
    implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner
  {
    Handler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (((width == 0) || (height == 0)) && (component != null))
      {
        TextUI localTextUI = component.getUI();
        try
        {
          Rectangle localRectangle = localTextUI.modelToView(component, dot, dotBias);
          if ((localRectangle != null) && (width != 0) && (height != 0)) {
            damage(localRectangle);
          }
        }
        catch (BadLocationException localBadLocationException) {}
      }
      visible = (!visible);
      repaint();
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((getUpdatePolicy() == 1) || ((getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
      {
        if (((paramDocumentEvent.getOffset() <= dot) || (paramDocumentEvent.getOffset() <= mark)) && (selectionTag != null)) {
          try
          {
            component.getHighlighter().changeHighlight(selectionTag, Math.min(dot, mark), Math.max(dot, mark));
          }
          catch (BadLocationException localBadLocationException1)
          {
            localBadLocationException1.printStackTrace();
          }
        }
        return;
      }
      int i = paramDocumentEvent.getOffset();
      int j = paramDocumentEvent.getLength();
      int k = dot;
      int m = 0;
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent))
      {
        setDot(i + j);
        return;
      }
      if (k >= i)
      {
        k += j;
        m = (short)(m | 0x1);
      }
      int n = mark;
      if (n >= i)
      {
        n += j;
        m = (short)(m | 0x2);
      }
      if (m != 0)
      {
        Position.Bias localBias = dotBias;
        if (dot == i)
        {
          Document localDocument = component.getDocument();
          int i1;
          try
          {
            Segment localSegment = new Segment();
            localDocument.getText(k - 1, 1, localSegment);
            i1 = (count > 0) && (array[offset] == '\n') ? 1 : 0;
          }
          catch (BadLocationException localBadLocationException2)
          {
            i1 = 0;
          }
          if (i1 != 0) {
            localBias = Position.Bias.Forward;
          } else {
            localBias = Position.Bias.Backward;
          }
        }
        if (n == k)
        {
          setDot(k, localBias);
          DefaultCaret.this.ensureValidPosition();
        }
        else
        {
          setDot(n, markBias);
          if (getDot() == n) {
            moveDot(k, localBias);
          }
          DefaultCaret.this.ensureValidPosition();
        }
      }
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((getUpdatePolicy() == 1) || ((getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread())))
      {
        i = component.getDocument().getLength();
        dot = Math.min(dot, i);
        mark = Math.min(mark, i);
        if (((paramDocumentEvent.getOffset() < dot) || (paramDocumentEvent.getOffset() < mark)) && (selectionTag != null)) {
          try
          {
            component.getHighlighter().changeHighlight(selectionTag, Math.min(dot, mark), Math.max(dot, mark));
          }
          catch (BadLocationException localBadLocationException)
          {
            localBadLocationException.printStackTrace();
          }
        }
        return;
      }
      int i = paramDocumentEvent.getOffset();
      int j = i + paramDocumentEvent.getLength();
      int k = dot;
      int m = 0;
      int n = mark;
      int i1 = 0;
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent))
      {
        setDot(i);
        return;
      }
      if (k >= j)
      {
        k -= j - i;
        if (k == j) {
          m = 1;
        }
      }
      else if (k >= i)
      {
        k = i;
        m = 1;
      }
      if (n >= j)
      {
        n -= j - i;
        if (n == j) {
          i1 = 1;
        }
      }
      else if (n >= i)
      {
        n = i;
        i1 = 1;
      }
      if (n == k)
      {
        forceCaretPositionChange = true;
        try
        {
          setDot(k, guessBiasForOffset(k, dotBias, dotLTR));
        }
        finally
        {
          forceCaretPositionChange = false;
        }
        DefaultCaret.this.ensureValidPosition();
      }
      else
      {
        Position.Bias localBias1 = dotBias;
        Position.Bias localBias2 = markBias;
        if (m != 0) {
          localBias1 = guessBiasForOffset(k, localBias1, dotLTR);
        }
        if (i1 != 0) {
          localBias2 = guessBiasForOffset(mark, localBias2, markLTR);
        }
        setDot(n, localBias2);
        if (getDot() == n) {
          moveDot(k, localBias1);
        }
        DefaultCaret.this.ensureValidPosition();
      }
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent)
    {
      if ((getUpdatePolicy() == 1) || ((getUpdatePolicy() == 0) && (!SwingUtilities.isEventDispatchThread()))) {
        return;
      }
      if ((paramDocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent)) {
        setDot(paramDocumentEvent.getOffset() + paramDocumentEvent.getLength());
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      if (((localObject1 instanceof Document)) || ((localObject2 instanceof Document)))
      {
        setDot(0);
        if (localObject1 != null) {
          ((Document)localObject1).removeDocumentListener(this);
        }
        if (localObject2 != null) {
          ((Document)localObject2).addDocumentListener(this);
        }
      }
      else
      {
        Object localObject3;
        if ("enabled".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Boolean)paramPropertyChangeEvent.getNewValue();
          if (component.isFocusOwner()) {
            if (localObject3 == Boolean.TRUE)
            {
              if (component.isEditable()) {
                setVisible(true);
              }
              setSelectionVisible(true);
            }
            else
            {
              setVisible(false);
              setSelectionVisible(false);
            }
          }
        }
        else if ("caretWidth".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Integer)paramPropertyChangeEvent.getNewValue();
          if (localObject3 != null) {
            caretWidth = ((Integer)localObject3).intValue();
          } else {
            caretWidth = -1;
          }
          repaint();
        }
        else if ("caretAspectRatio".equals(paramPropertyChangeEvent.getPropertyName()))
        {
          localObject3 = (Number)paramPropertyChangeEvent.getNewValue();
          if (localObject3 != null) {
            aspectRatio = ((Number)localObject3).floatValue();
          } else {
            aspectRatio = -1.0F;
          }
          repaint();
        }
      }
    }
    
    public void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable)
    {
      if (ownsSelection)
      {
        ownsSelection = false;
        if ((component != null) && (!component.hasFocus())) {
          setSelectionVisible(false);
        }
      }
    }
  }
  
  class SafeScroller
    implements Runnable
  {
    Rectangle r;
    
    SafeScroller(Rectangle paramRectangle)
    {
      r = paramRectangle;
    }
    
    public void run()
    {
      if (component != null) {
        component.scrollRectToVisible(r);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DefaultCaret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */