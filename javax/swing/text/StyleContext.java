package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.font.FontUtilities;

public class StyleContext
  implements Serializable, AbstractDocument.AttributeContext
{
  private static StyleContext defaultContext;
  public static final String DEFAULT_STYLE = "default";
  private static Hashtable<Object, String> freezeKeyMap;
  private static Hashtable<String, Object> thawKeyMap;
  private Style styles = new NamedStyle(null);
  private transient FontKey fontSearch = new FontKey(null, 0, 0);
  private transient Hashtable<FontKey, Font> fontTable = new Hashtable();
  private transient Map<SmallAttributeSet, WeakReference<SmallAttributeSet>> attributesPool = Collections.synchronizedMap(new WeakHashMap());
  private transient MutableAttributeSet search = new SimpleAttributeSet();
  private int unusedSets;
  static final int THRESHOLD = 9;
  
  public static final StyleContext getDefaultStyleContext()
  {
    if (defaultContext == null) {
      defaultContext = new StyleContext();
    }
    return defaultContext;
  }
  
  public StyleContext()
  {
    addStyle("default", null);
  }
  
  public Style addStyle(String paramString, Style paramStyle)
  {
    NamedStyle localNamedStyle = new NamedStyle(paramString, paramStyle);
    if (paramString != null) {
      styles.addAttribute(paramString, localNamedStyle);
    }
    return localNamedStyle;
  }
  
  public void removeStyle(String paramString)
  {
    styles.removeAttribute(paramString);
  }
  
  public Style getStyle(String paramString)
  {
    return (Style)styles.getAttribute(paramString);
  }
  
  public Enumeration<?> getStyleNames()
  {
    return styles.getAttributeNames();
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    styles.addChangeListener(paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    styles.removeChangeListener(paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return ((NamedStyle)styles).getChangeListeners();
  }
  
  public Font getFont(AttributeSet paramAttributeSet)
  {
    int i = 0;
    if (StyleConstants.isBold(paramAttributeSet)) {
      i |= 0x1;
    }
    if (StyleConstants.isItalic(paramAttributeSet)) {
      i |= 0x2;
    }
    String str = StyleConstants.getFontFamily(paramAttributeSet);
    int j = StyleConstants.getFontSize(paramAttributeSet);
    if ((StyleConstants.isSuperscript(paramAttributeSet)) || (StyleConstants.isSubscript(paramAttributeSet))) {
      j -= 2;
    }
    return getFont(str, i, j);
  }
  
  public Color getForeground(AttributeSet paramAttributeSet)
  {
    return StyleConstants.getForeground(paramAttributeSet);
  }
  
  public Color getBackground(AttributeSet paramAttributeSet)
  {
    return StyleConstants.getBackground(paramAttributeSet);
  }
  
  public Font getFont(String paramString, int paramInt1, int paramInt2)
  {
    fontSearch.setValue(paramString, paramInt1, paramInt2);
    Object localObject = (Font)fontTable.get(fontSearch);
    if (localObject == null)
    {
      Style localStyle = getStyle("default");
      if (localStyle != null)
      {
        Font localFont = (Font)localStyle.getAttribute("FONT_ATTRIBUTE_KEY");
        if ((localFont != null) && (localFont.getFamily().equalsIgnoreCase(paramString))) {
          localObject = localFont.deriveFont(paramInt1, paramInt2);
        }
      }
      if (localObject == null) {
        localObject = new Font(paramString, paramInt1, paramInt2);
      }
      if (!FontUtilities.fontSupportsDefaultEncoding((Font)localObject)) {
        localObject = FontUtilities.getCompositeFontUIResource((Font)localObject);
      }
      FontKey localFontKey = new FontKey(paramString, paramInt1, paramInt2);
      fontTable.put(localFontKey, localObject);
    }
    return (Font)localObject;
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
  }
  
  public synchronized AttributeSet addAttribute(AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2)
  {
    if (paramAttributeSet.getAttributeCount() + 1 <= getCompressionThreshold())
    {
      search.removeAttributes(search);
      search.addAttributes(paramAttributeSet);
      search.addAttribute(paramObject1, paramObject2);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    }
    MutableAttributeSet localMutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    localMutableAttributeSet.addAttribute(paramObject1, paramObject2);
    return localMutableAttributeSet;
  }
  
  public synchronized AttributeSet addAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
  {
    if (paramAttributeSet1.getAttributeCount() + paramAttributeSet2.getAttributeCount() <= getCompressionThreshold())
    {
      search.removeAttributes(search);
      search.addAttributes(paramAttributeSet1);
      search.addAttributes(paramAttributeSet2);
      reclaim(paramAttributeSet1);
      return getImmutableUniqueSet();
    }
    MutableAttributeSet localMutableAttributeSet = getMutableAttributeSet(paramAttributeSet1);
    localMutableAttributeSet.addAttributes(paramAttributeSet2);
    return localMutableAttributeSet;
  }
  
  public synchronized AttributeSet removeAttribute(AttributeSet paramAttributeSet, Object paramObject)
  {
    if (paramAttributeSet.getAttributeCount() - 1 <= getCompressionThreshold())
    {
      search.removeAttributes(search);
      search.addAttributes(paramAttributeSet);
      search.removeAttribute(paramObject);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    }
    MutableAttributeSet localMutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    localMutableAttributeSet.removeAttribute(paramObject);
    return localMutableAttributeSet;
  }
  
  public synchronized AttributeSet removeAttributes(AttributeSet paramAttributeSet, Enumeration<?> paramEnumeration)
  {
    if (paramAttributeSet.getAttributeCount() <= getCompressionThreshold())
    {
      search.removeAttributes(search);
      search.addAttributes(paramAttributeSet);
      search.removeAttributes(paramEnumeration);
      reclaim(paramAttributeSet);
      return getImmutableUniqueSet();
    }
    MutableAttributeSet localMutableAttributeSet = getMutableAttributeSet(paramAttributeSet);
    localMutableAttributeSet.removeAttributes(paramEnumeration);
    return localMutableAttributeSet;
  }
  
  public synchronized AttributeSet removeAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
  {
    if (paramAttributeSet1.getAttributeCount() <= getCompressionThreshold())
    {
      search.removeAttributes(search);
      search.addAttributes(paramAttributeSet1);
      search.removeAttributes(paramAttributeSet2);
      reclaim(paramAttributeSet1);
      return getImmutableUniqueSet();
    }
    MutableAttributeSet localMutableAttributeSet = getMutableAttributeSet(paramAttributeSet1);
    localMutableAttributeSet.removeAttributes(paramAttributeSet2);
    return localMutableAttributeSet;
  }
  
  public AttributeSet getEmptySet()
  {
    return SimpleAttributeSet.EMPTY;
  }
  
  public void reclaim(AttributeSet paramAttributeSet)
  {
    if (SwingUtilities.isEventDispatchThread()) {
      attributesPool.size();
    }
  }
  
  protected int getCompressionThreshold()
  {
    return 9;
  }
  
  protected SmallAttributeSet createSmallAttributeSet(AttributeSet paramAttributeSet)
  {
    return new SmallAttributeSet(paramAttributeSet);
  }
  
  protected MutableAttributeSet createLargeAttributeSet(AttributeSet paramAttributeSet)
  {
    return new SimpleAttributeSet(paramAttributeSet);
  }
  
  synchronized void removeUnusedSets()
  {
    attributesPool.size();
  }
  
  AttributeSet getImmutableUniqueSet()
  {
    SmallAttributeSet localSmallAttributeSet1 = createSmallAttributeSet(search);
    WeakReference localWeakReference = (WeakReference)attributesPool.get(localSmallAttributeSet1);
    SmallAttributeSet localSmallAttributeSet2;
    if ((localWeakReference == null) || ((localSmallAttributeSet2 = (SmallAttributeSet)localWeakReference.get()) == null))
    {
      localSmallAttributeSet2 = localSmallAttributeSet1;
      attributesPool.put(localSmallAttributeSet2, new WeakReference(localSmallAttributeSet2));
    }
    return localSmallAttributeSet2;
  }
  
  MutableAttributeSet getMutableAttributeSet(AttributeSet paramAttributeSet)
  {
    if (((paramAttributeSet instanceof MutableAttributeSet)) && (paramAttributeSet != SimpleAttributeSet.EMPTY)) {
      return (MutableAttributeSet)paramAttributeSet;
    }
    return createLargeAttributeSet(paramAttributeSet);
  }
  
  public String toString()
  {
    removeUnusedSets();
    String str = "";
    Iterator localIterator = attributesPool.keySet().iterator();
    while (localIterator.hasNext())
    {
      SmallAttributeSet localSmallAttributeSet = (SmallAttributeSet)localIterator.next();
      str = str + localSmallAttributeSet + "\n";
    }
    return str;
  }
  
  public void writeAttributes(ObjectOutputStream paramObjectOutputStream, AttributeSet paramAttributeSet)
    throws IOException
  {
    writeAttributeSet(paramObjectOutputStream, paramAttributeSet);
  }
  
  public void readAttributes(ObjectInputStream paramObjectInputStream, MutableAttributeSet paramMutableAttributeSet)
    throws ClassNotFoundException, IOException
  {
    readAttributeSet(paramObjectInputStream, paramMutableAttributeSet);
  }
  
  public static void writeAttributeSet(ObjectOutputStream paramObjectOutputStream, AttributeSet paramAttributeSet)
    throws IOException
  {
    int i = paramAttributeSet.getAttributeCount();
    paramObjectOutputStream.writeInt(i);
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((localObject1 instanceof Serializable))
      {
        paramObjectOutputStream.writeObject(localObject1);
      }
      else
      {
        localObject2 = freezeKeyMap.get(localObject1);
        if (localObject2 == null) {
          throw new NotSerializableException(localObject1.getClass().getName() + " is not serializable as a key in an AttributeSet");
        }
        paramObjectOutputStream.writeObject(localObject2);
      }
      Object localObject2 = paramAttributeSet.getAttribute(localObject1);
      Object localObject3 = freezeKeyMap.get(localObject2);
      if ((localObject2 instanceof Serializable))
      {
        paramObjectOutputStream.writeObject(localObject3 != null ? localObject3 : localObject2);
      }
      else
      {
        if (localObject3 == null) {
          throw new NotSerializableException(localObject2.getClass().getName() + " is not serializable as a value in an AttributeSet");
        }
        paramObjectOutputStream.writeObject(localObject3);
      }
    }
  }
  
  public static void readAttributeSet(ObjectInputStream paramObjectInputStream, MutableAttributeSet paramMutableAttributeSet)
    throws ClassNotFoundException, IOException
  {
    int i = paramObjectInputStream.readInt();
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Object localObject2 = paramObjectInputStream.readObject();
      if (thawKeyMap != null)
      {
        Object localObject3 = thawKeyMap.get(localObject1);
        if (localObject3 != null) {
          localObject1 = localObject3;
        }
        Object localObject4 = thawKeyMap.get(localObject2);
        if (localObject4 != null) {
          localObject2 = localObject4;
        }
      }
      paramMutableAttributeSet.addAttribute(localObject1, localObject2);
    }
  }
  
  public static void registerStaticAttributeKey(Object paramObject)
  {
    String str = paramObject.getClass().getName() + "." + paramObject.toString();
    if (freezeKeyMap == null)
    {
      freezeKeyMap = new Hashtable();
      thawKeyMap = new Hashtable();
    }
    freezeKeyMap.put(paramObject, str);
    thawKeyMap.put(str, paramObject);
  }
  
  public static Object getStaticAttribute(Object paramObject)
  {
    if ((thawKeyMap == null) || (paramObject == null)) {
      return null;
    }
    return thawKeyMap.get(paramObject);
  }
  
  public static Object getStaticAttributeKey(Object paramObject)
  {
    return paramObject.getClass().getName() + "." + paramObject.toString();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    removeUnusedSets();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    fontSearch = new FontKey(null, 0, 0);
    fontTable = new Hashtable();
    search = new SimpleAttributeSet();
    attributesPool = Collections.synchronizedMap(new WeakHashMap());
    paramObjectInputStream.defaultReadObject();
  }
  
  static
  {
    try
    {
      int i = StyleConstants.keys.length;
      for (int j = 0; j < i; j++) {
        registerStaticAttributeKey(StyleConstants.keys[j]);
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  static class FontKey
  {
    private String family;
    private int style;
    private int size;
    
    public FontKey(String paramString, int paramInt1, int paramInt2)
    {
      setValue(paramString, paramInt1, paramInt2);
    }
    
    public void setValue(String paramString, int paramInt1, int paramInt2)
    {
      family = (paramString != null ? paramString.intern() : null);
      style = paramInt1;
      size = paramInt2;
    }
    
    public int hashCode()
    {
      int i = family != null ? family.hashCode() : 0;
      return i ^ style ^ size;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FontKey))
      {
        FontKey localFontKey = (FontKey)paramObject;
        return (size == size) && (style == style) && (family == family);
      }
      return false;
    }
  }
  
  class KeyBuilder
  {
    private Vector<Object> keys = new Vector();
    private Vector<Object> data = new Vector();
    
    KeyBuilder() {}
    
    public void initialize(AttributeSet paramAttributeSet)
    {
      if ((paramAttributeSet instanceof StyleContext.SmallAttributeSet))
      {
        initialize(attributes);
      }
      else
      {
        keys.removeAllElements();
        data.removeAllElements();
        Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
        while (localEnumeration.hasMoreElements())
        {
          Object localObject = localEnumeration.nextElement();
          addAttribute(localObject, paramAttributeSet.getAttribute(localObject));
        }
      }
    }
    
    private void initialize(Object[] paramArrayOfObject)
    {
      keys.removeAllElements();
      data.removeAllElements();
      int i = paramArrayOfObject.length;
      for (int j = 0; j < i; j += 2)
      {
        keys.addElement(paramArrayOfObject[j]);
        data.addElement(paramArrayOfObject[(j + 1)]);
      }
    }
    
    public Object[] createTable()
    {
      int i = keys.size();
      Object[] arrayOfObject = new Object[2 * i];
      for (int j = 0; j < i; j++)
      {
        int k = 2 * j;
        arrayOfObject[k] = keys.elementAt(j);
        arrayOfObject[(k + 1)] = data.elementAt(j);
      }
      return arrayOfObject;
    }
    
    int getCount()
    {
      return keys.size();
    }
    
    public void addAttribute(Object paramObject1, Object paramObject2)
    {
      keys.addElement(paramObject1);
      data.addElement(paramObject2);
    }
    
    public void addAttributes(AttributeSet paramAttributeSet)
    {
      Object localObject1;
      if ((paramAttributeSet instanceof StyleContext.SmallAttributeSet))
      {
        localObject1 = attributes;
        int i = localObject1.length;
        for (int j = 0; j < i; j += 2) {
          addAttribute(localObject1[j], localObject1[(j + 1)]);
        }
      }
      else
      {
        localObject1 = paramAttributeSet.getAttributeNames();
        while (((Enumeration)localObject1).hasMoreElements())
        {
          Object localObject2 = ((Enumeration)localObject1).nextElement();
          addAttribute(localObject2, paramAttributeSet.getAttribute(localObject2));
        }
      }
    }
    
    public void removeAttribute(Object paramObject)
    {
      int i = keys.size();
      for (int j = 0; j < i; j++) {
        if (keys.elementAt(j).equals(paramObject))
        {
          keys.removeElementAt(j);
          data.removeElementAt(j);
          return;
        }
      }
    }
    
    public void removeAttributes(Enumeration paramEnumeration)
    {
      while (paramEnumeration.hasMoreElements())
      {
        Object localObject = paramEnumeration.nextElement();
        removeAttribute(localObject);
      }
    }
    
    public void removeAttributes(AttributeSet paramAttributeSet)
    {
      Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject1 = localEnumeration.nextElement();
        Object localObject2 = paramAttributeSet.getAttribute(localObject1);
        removeSearchAttribute(localObject1, localObject2);
      }
    }
    
    private void removeSearchAttribute(Object paramObject1, Object paramObject2)
    {
      int i = keys.size();
      for (int j = 0; j < i; j++) {
        if (keys.elementAt(j).equals(paramObject1))
        {
          if (data.elementAt(j).equals(paramObject2))
          {
            keys.removeElementAt(j);
            data.removeElementAt(j);
          }
          return;
        }
      }
    }
  }
  
  class KeyEnumeration
    implements Enumeration<Object>
  {
    Object[] attr;
    int i;
    
    KeyEnumeration(Object[] paramArrayOfObject)
    {
      attr = paramArrayOfObject;
      i = 0;
    }
    
    public boolean hasMoreElements()
    {
      return i < attr.length;
    }
    
    public Object nextElement()
    {
      if (i < attr.length)
      {
        Object localObject = attr[i];
        i += 2;
        return localObject;
      }
      throw new NoSuchElementException();
    }
  }
  
  public class NamedStyle
    implements Style, Serializable
  {
    protected EventListenerList listenerList = new EventListenerList();
    protected transient ChangeEvent changeEvent = null;
    private transient AttributeSet attributes = getEmptySet();
    
    public NamedStyle(String paramString, Style paramStyle)
    {
      if (paramString != null) {
        setName(paramString);
      }
      if (paramStyle != null) {
        setResolveParent(paramStyle);
      }
    }
    
    public NamedStyle(Style paramStyle)
    {
      this(null, paramStyle);
    }
    
    public NamedStyle() {}
    
    public String toString()
    {
      return "NamedStyle:" + getName() + " " + attributes;
    }
    
    public String getName()
    {
      if (isDefined(StyleConstants.NameAttribute)) {
        return getAttribute(StyleConstants.NameAttribute).toString();
      }
      return null;
    }
    
    public void setName(String paramString)
    {
      if (paramString != null) {
        addAttribute(StyleConstants.NameAttribute, paramString);
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
    
    public int getAttributeCount()
    {
      return attributes.getAttributeCount();
    }
    
    public boolean isDefined(Object paramObject)
    {
      return attributes.isDefined(paramObject);
    }
    
    public boolean isEqual(AttributeSet paramAttributeSet)
    {
      return attributes.isEqual(paramAttributeSet);
    }
    
    public AttributeSet copyAttributes()
    {
      NamedStyle localNamedStyle = new NamedStyle(StyleContext.this);
      attributes = attributes.copyAttributes();
      return localNamedStyle;
    }
    
    public Object getAttribute(Object paramObject)
    {
      return attributes.getAttribute(paramObject);
    }
    
    public Enumeration<?> getAttributeNames()
    {
      return attributes.getAttributeNames();
    }
    
    public boolean containsAttribute(Object paramObject1, Object paramObject2)
    {
      return attributes.containsAttribute(paramObject1, paramObject2);
    }
    
    public boolean containsAttributes(AttributeSet paramAttributeSet)
    {
      return attributes.containsAttributes(paramAttributeSet);
    }
    
    public AttributeSet getResolveParent()
    {
      return attributes.getResolveParent();
    }
    
    public void addAttribute(Object paramObject1, Object paramObject2)
    {
      StyleContext localStyleContext = StyleContext.this;
      attributes = localStyleContext.addAttribute(attributes, paramObject1, paramObject2);
      fireStateChanged();
    }
    
    public void addAttributes(AttributeSet paramAttributeSet)
    {
      StyleContext localStyleContext = StyleContext.this;
      attributes = localStyleContext.addAttributes(attributes, paramAttributeSet);
      fireStateChanged();
    }
    
    public void removeAttribute(Object paramObject)
    {
      StyleContext localStyleContext = StyleContext.this;
      attributes = localStyleContext.removeAttribute(attributes, paramObject);
      fireStateChanged();
    }
    
    public void removeAttributes(Enumeration<?> paramEnumeration)
    {
      StyleContext localStyleContext = StyleContext.this;
      attributes = localStyleContext.removeAttributes(attributes, paramEnumeration);
      fireStateChanged();
    }
    
    public void removeAttributes(AttributeSet paramAttributeSet)
    {
      StyleContext localStyleContext = StyleContext.this;
      if (paramAttributeSet == this) {
        attributes = localStyleContext.getEmptySet();
      } else {
        attributes = localStyleContext.removeAttributes(attributes, paramAttributeSet);
      }
      fireStateChanged();
    }
    
    public void setResolveParent(AttributeSet paramAttributeSet)
    {
      if (paramAttributeSet != null) {
        addAttribute(StyleConstants.ResolveAttribute, paramAttributeSet);
      } else {
        removeAttribute(StyleConstants.ResolveAttribute);
      }
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      StyleContext.writeAttributeSet(paramObjectOutputStream, attributes);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      attributes = SimpleAttributeSet.EMPTY;
      StyleContext.readAttributeSet(paramObjectInputStream, this);
    }
  }
  
  public class SmallAttributeSet
    implements AttributeSet
  {
    Object[] attributes;
    AttributeSet resolveParent;
    
    public SmallAttributeSet(Object[] paramArrayOfObject)
    {
      attributes = paramArrayOfObject;
      updateResolveParent();
    }
    
    public SmallAttributeSet(AttributeSet paramAttributeSet)
    {
      int i = paramAttributeSet.getAttributeCount();
      Object[] arrayOfObject = new Object[2 * i];
      Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
      for (int j = 0; localEnumeration.hasMoreElements(); j += 2)
      {
        arrayOfObject[j] = localEnumeration.nextElement();
        arrayOfObject[(j + 1)] = paramAttributeSet.getAttribute(arrayOfObject[j]);
      }
      attributes = arrayOfObject;
      updateResolveParent();
    }
    
    private void updateResolveParent()
    {
      resolveParent = null;
      Object[] arrayOfObject = attributes;
      for (int i = 0; i < arrayOfObject.length; i += 2) {
        if (arrayOfObject[i] == StyleConstants.ResolveAttribute)
        {
          resolveParent = ((AttributeSet)arrayOfObject[(i + 1)]);
          break;
        }
      }
    }
    
    Object getLocalAttribute(Object paramObject)
    {
      if (paramObject == StyleConstants.ResolveAttribute) {
        return resolveParent;
      }
      Object[] arrayOfObject = attributes;
      for (int i = 0; i < arrayOfObject.length; i += 2) {
        if (paramObject.equals(arrayOfObject[i])) {
          return arrayOfObject[(i + 1)];
        }
      }
      return null;
    }
    
    public String toString()
    {
      String str = "{";
      Object[] arrayOfObject = attributes;
      for (int i = 0; i < arrayOfObject.length; i += 2) {
        if ((arrayOfObject[(i + 1)] instanceof AttributeSet)) {
          str = str + arrayOfObject[i] + "=AttributeSet,";
        } else {
          str = str + arrayOfObject[i] + "=" + arrayOfObject[(i + 1)] + ",";
        }
      }
      str = str + "}";
      return str;
    }
    
    public int hashCode()
    {
      int i = 0;
      Object[] arrayOfObject = attributes;
      for (int j = 1; j < arrayOfObject.length; j += 2) {
        i ^= arrayOfObject[j].hashCode();
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof AttributeSet))
      {
        AttributeSet localAttributeSet = (AttributeSet)paramObject;
        return (getAttributeCount() == localAttributeSet.getAttributeCount()) && (containsAttributes(localAttributeSet));
      }
      return false;
    }
    
    public Object clone()
    {
      return this;
    }
    
    public int getAttributeCount()
    {
      return attributes.length / 2;
    }
    
    public boolean isDefined(Object paramObject)
    {
      Object[] arrayOfObject = attributes;
      int i = arrayOfObject.length;
      for (int j = 0; j < i; j += 2) {
        if (paramObject.equals(arrayOfObject[j])) {
          return true;
        }
      }
      return false;
    }
    
    public boolean isEqual(AttributeSet paramAttributeSet)
    {
      if ((paramAttributeSet instanceof SmallAttributeSet)) {
        return paramAttributeSet == this;
      }
      return (getAttributeCount() == paramAttributeSet.getAttributeCount()) && (containsAttributes(paramAttributeSet));
    }
    
    public AttributeSet copyAttributes()
    {
      return this;
    }
    
    public Object getAttribute(Object paramObject)
    {
      Object localObject = getLocalAttribute(paramObject);
      if (localObject == null)
      {
        AttributeSet localAttributeSet = getResolveParent();
        if (localAttributeSet != null) {
          localObject = localAttributeSet.getAttribute(paramObject);
        }
      }
      return localObject;
    }
    
    public Enumeration<?> getAttributeNames()
    {
      return new StyleContext.KeyEnumeration(StyleContext.this, attributes);
    }
    
    public boolean containsAttribute(Object paramObject1, Object paramObject2)
    {
      return paramObject2.equals(getAttribute(paramObject1));
    }
    
    public boolean containsAttributes(AttributeSet paramAttributeSet)
    {
      boolean bool = true;
      Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
      while ((bool) && (localEnumeration.hasMoreElements()))
      {
        Object localObject = localEnumeration.nextElement();
        bool = paramAttributeSet.getAttribute(localObject).equals(getAttribute(localObject));
      }
      return bool;
    }
    
    public AttributeSet getResolveParent()
    {
      return resolveParent;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\StyleContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */