package java.beans;

import com.sun.beans.finder.PrimitiveWrapperMap;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.Window;
import java.awt.font.TextAttribute;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import sun.reflect.misc.ReflectUtil;

class MetaData
{
  private static final Map<String, Field> fields = Collections.synchronizedMap(new WeakHashMap());
  private static Hashtable<String, PersistenceDelegate> internalPersistenceDelegates = new Hashtable();
  private static PersistenceDelegate nullPersistenceDelegate = new NullPersistenceDelegate();
  private static PersistenceDelegate enumPersistenceDelegate = new EnumPersistenceDelegate();
  private static PersistenceDelegate primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
  private static PersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate();
  private static PersistenceDelegate arrayPersistenceDelegate;
  private static PersistenceDelegate proxyPersistenceDelegate;
  
  MetaData() {}
  
  public static synchronized PersistenceDelegate getPersistenceDelegate(Class paramClass)
  {
    if (paramClass == null) {
      return nullPersistenceDelegate;
    }
    if (Enum.class.isAssignableFrom(paramClass)) {
      return enumPersistenceDelegate;
    }
    if (null != XMLEncoder.primitiveTypeFor(paramClass)) {
      return primitivePersistenceDelegate;
    }
    if (paramClass.isArray())
    {
      if (arrayPersistenceDelegate == null) {
        arrayPersistenceDelegate = new ArrayPersistenceDelegate();
      }
      return arrayPersistenceDelegate;
    }
    try
    {
      if (Proxy.isProxyClass(paramClass))
      {
        if (proxyPersistenceDelegate == null) {
          proxyPersistenceDelegate = new ProxyPersistenceDelegate();
        }
        return proxyPersistenceDelegate;
      }
    }
    catch (Exception localException1) {}
    String str1 = paramClass.getName();
    Object localObject1 = (PersistenceDelegate)getBeanAttribute(paramClass, "persistenceDelegate");
    if (localObject1 == null)
    {
      localObject1 = (PersistenceDelegate)internalPersistenceDelegates.get(str1);
      if (localObject1 != null) {
        return (PersistenceDelegate)localObject1;
      }
      internalPersistenceDelegates.put(str1, defaultPersistenceDelegate);
      try
      {
        String str2 = paramClass.getName();
        localObject2 = Class.forName("java.beans.MetaData$" + str2.replace('.', '_') + "_PersistenceDelegate");
        localObject1 = (PersistenceDelegate)((Class)localObject2).newInstance();
        internalPersistenceDelegates.put(str1, localObject1);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Object localObject2 = getConstructorProperties(paramClass);
        if (localObject2 != null)
        {
          localObject1 = new DefaultPersistenceDelegate((String[])localObject2);
          internalPersistenceDelegates.put(str1, localObject1);
        }
      }
      catch (Exception localException2)
      {
        System.err.println("Internal error: " + localException2);
      }
    }
    return (PersistenceDelegate)(localObject1 != null ? localObject1 : defaultPersistenceDelegate);
  }
  
  private static String[] getConstructorProperties(Class<?> paramClass)
  {
    Object localObject = null;
    int i = 0;
    for (Constructor localConstructor : paramClass.getConstructors())
    {
      String[] arrayOfString = getAnnotationValue(localConstructor);
      if ((arrayOfString != null) && (i < arrayOfString.length) && (isValid(localConstructor, arrayOfString)))
      {
        localObject = arrayOfString;
        i = arrayOfString.length;
      }
    }
    return (String[])localObject;
  }
  
  private static String[] getAnnotationValue(Constructor<?> paramConstructor)
  {
    ConstructorProperties localConstructorProperties = (ConstructorProperties)paramConstructor.getAnnotation(ConstructorProperties.class);
    return localConstructorProperties != null ? localConstructorProperties.value() : null;
  }
  
  private static boolean isValid(Constructor<?> paramConstructor, String[] paramArrayOfString)
  {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    if (paramArrayOfString.length != arrayOfClass.length) {
      return false;
    }
    for (String str : paramArrayOfString) {
      if (str == null) {
        return false;
      }
    }
    return true;
  }
  
  private static Object getBeanAttribute(Class<?> paramClass, String paramString)
  {
    try
    {
      return Introspector.getBeanInfo(paramClass).getBeanDescriptor().getValue(paramString);
    }
    catch (IntrospectionException localIntrospectionException) {}
    return null;
  }
  
  static Object getPrivateFieldValue(Object paramObject, String paramString)
  {
    Field localField = (Field)fields.get(paramString);
    if (localField == null)
    {
      int i = paramString.lastIndexOf('.');
      String str1 = paramString.substring(0, i);
      final String str2 = paramString.substring(1 + i);
      localField = (Field)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Field run()
        {
          try
          {
            Field localField = Class.forName(val$className).getDeclaredField(str2);
            localField.setAccessible(true);
            return localField;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            throw new IllegalStateException("Could not find class", localClassNotFoundException);
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            throw new IllegalStateException("Could not find field", localNoSuchFieldException);
          }
        }
      });
      fields.put(paramString, localField);
    }
    try
    {
      return localField.get(paramObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalStateException("Could not get value of the field", localIllegalAccessException);
    }
  }
  
  static
  {
    internalPersistenceDelegates.put("java.net.URI", new PrimitivePersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource", new javax_swing_border_MatteBorder_PersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource", new java_awt_Font_PersistenceDelegate());
    internalPersistenceDelegates.put("javax.swing.KeyStroke", new java_awt_AWTKeyStroke_PersistenceDelegate());
    internalPersistenceDelegates.put("java.sql.Date", new java_util_Date_PersistenceDelegate());
    internalPersistenceDelegates.put("java.sql.Time", new java_util_Date_PersistenceDelegate());
    internalPersistenceDelegates.put("java.util.JumboEnumSet", new java_util_EnumSet_PersistenceDelegate());
    internalPersistenceDelegates.put("java.util.RegularEnumSet", new java_util_EnumSet_PersistenceDelegate());
  }
  
  static final class ArrayPersistenceDelegate
    extends PersistenceDelegate
  {
    ArrayPersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (paramObject2 != null) && (paramObject1.getClass() == paramObject2.getClass()) && (Array.getLength(paramObject1) == Array.getLength(paramObject2));
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Class localClass = paramObject.getClass();
      return new Expression(paramObject, Array.class, "newInstance", new Object[] { localClass.getComponentType(), new Integer(Array.getLength(paramObject)) });
    }
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      int i = Array.getLength(paramObject1);
      for (int j = 0; j < i; j++)
      {
        Integer localInteger = new Integer(j);
        Expression localExpression1 = new Expression(paramObject1, "get", new Object[] { localInteger });
        Expression localExpression2 = new Expression(paramObject2, "get", new Object[] { localInteger });
        try
        {
          Object localObject1 = localExpression1.getValue();
          Object localObject2 = localExpression2.getValue();
          paramEncoder.writeExpression(localExpression1);
          if (!Objects.equals(localObject2, paramEncoder.get(localObject1))) {
            DefaultPersistenceDelegate.invokeStatement(paramObject1, "set", new Object[] { localInteger, localObject1 }, paramEncoder);
          }
        }
        catch (Exception localException)
        {
          paramEncoder.getExceptionListener().exceptionThrown(localException);
        }
      }
    }
  }
  
  static final class EnumPersistenceDelegate
    extends PersistenceDelegate
  {
    EnumPersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1 == paramObject2;
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Enum localEnum = (Enum)paramObject;
      return new Expression(localEnum, Enum.class, "valueOf", new Object[] { localEnum.getDeclaringClass(), localEnum.name() });
    }
  }
  
  static final class NullPersistenceDelegate
    extends PersistenceDelegate
  {
    NullPersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder) {}
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return null;
    }
    
    public void writeObject(Object paramObject, Encoder paramEncoder) {}
  }
  
  static final class PrimitivePersistenceDelegate
    extends PersistenceDelegate
  {
    PrimitivePersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return new Expression(paramObject, paramObject.getClass(), "new", new Object[] { paramObject.toString() });
    }
  }
  
  static final class ProxyPersistenceDelegate
    extends PersistenceDelegate
  {
    ProxyPersistenceDelegate() {}
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Class localClass = paramObject.getClass();
      Proxy localProxy = (Proxy)paramObject;
      InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(localProxy);
      if ((localInvocationHandler instanceof EventHandler))
      {
        EventHandler localEventHandler = (EventHandler)localInvocationHandler;
        Vector localVector = new Vector();
        localVector.add(localClass.getInterfaces()[0]);
        localVector.add(localEventHandler.getTarget());
        localVector.add(localEventHandler.getAction());
        if (localEventHandler.getEventPropertyName() != null) {
          localVector.add(localEventHandler.getEventPropertyName());
        }
        if (localEventHandler.getListenerMethodName() != null)
        {
          localVector.setSize(4);
          localVector.add(localEventHandler.getListenerMethodName());
        }
        return new Expression(paramObject, EventHandler.class, "create", localVector.toArray());
      }
      return new Expression(paramObject, Proxy.class, "newProxyInstance", new Object[] { localClass.getClassLoader(), localClass.getInterfaces(), localInvocationHandler });
    }
  }
  
  static class StaticFieldsPersistenceDelegate
    extends PersistenceDelegate
  {
    StaticFieldsPersistenceDelegate() {}
    
    protected void installFields(Encoder paramEncoder, Class<?> paramClass)
    {
      if ((Modifier.isPublic(paramClass.getModifiers())) && (ReflectUtil.isPackageAccessible(paramClass)))
      {
        Field[] arrayOfField = paramClass.getFields();
        for (int i = 0; i < arrayOfField.length; i++)
        {
          Field localField = arrayOfField[i];
          if (Object.class.isAssignableFrom(localField.getType())) {
            paramEncoder.writeExpression(new Expression(localField, "get", new Object[] { null }));
          }
        }
      }
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      throw new RuntimeException("Unrecognized instance: " + paramObject);
    }
    
    public void writeObject(Object paramObject, Encoder paramEncoder)
    {
      if (paramEncoder.getAttribute(this) == null)
      {
        paramEncoder.setAttribute(this, Boolean.TRUE);
        installFields(paramEncoder, paramObject.getClass());
      }
      super.writeObject(paramObject, paramEncoder);
    }
  }
  
  static final class java_awt_AWTKeyStroke_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_awt_AWTKeyStroke_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)paramObject;
      int i = localAWTKeyStroke.getKeyChar();
      int j = localAWTKeyStroke.getKeyCode();
      int k = localAWTKeyStroke.getModifiers();
      boolean bool = localAWTKeyStroke.isOnKeyRelease();
      Object[] arrayOfObject = null;
      if (i == 65535) {
        arrayOfObject = new Object[] { Integer.valueOf(j), Integer.valueOf(k), !bool ? new Object[] { Integer.valueOf(j), Integer.valueOf(k) } : Boolean.valueOf(bool) };
      } else if (j == 0) {
        if (!bool) {
          arrayOfObject = new Object[] { Character.valueOf(i), k == 0 ? new Object[] { Character.valueOf(i) } : Integer.valueOf(k) };
        } else if (k == 0) {
          arrayOfObject = new Object[] { Character.valueOf(i), Boolean.valueOf(bool) };
        }
      }
      if (arrayOfObject == null) {
        throw new IllegalStateException("Unsupported KeyStroke: " + localAWTKeyStroke);
      }
      Class localClass = localAWTKeyStroke.getClass();
      String str = localClass.getName();
      int m = str.lastIndexOf('.') + 1;
      if (m > 0) {
        str = str.substring(m);
      }
      return new Expression(localAWTKeyStroke, localClass, "get" + str, arrayOfObject);
    }
  }
  
  static final class java_awt_BorderLayout_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    private static final String[] CONSTRAINTS = { "North", "South", "East", "West", "Center", "First", "Last", "Before", "After" };
    
    java_awt_BorderLayout_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      BorderLayout localBorderLayout1 = (BorderLayout)paramObject1;
      BorderLayout localBorderLayout2 = (BorderLayout)paramObject2;
      for (String str : CONSTRAINTS)
      {
        Component localComponent1 = localBorderLayout1.getLayoutComponent(str);
        Component localComponent2 = localBorderLayout2.getLayoutComponent(str);
        if ((localComponent1 != null) && (localComponent2 == null)) {
          invokeStatement(paramObject1, "addLayoutComponent", new Object[] { localComponent1, str }, paramEncoder);
        }
      }
    }
  }
  
  static final class java_awt_CardLayout_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_CardLayout_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      if (getVector(paramObject2).isEmpty())
      {
        Iterator localIterator = getVector(paramObject1).iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          Object[] arrayOfObject = { MetaData.getPrivateFieldValue(localObject, "java.awt.CardLayout$Card.name"), MetaData.getPrivateFieldValue(localObject, "java.awt.CardLayout$Card.comp") };
          invokeStatement(paramObject1, "addLayoutComponent", arrayOfObject, paramEncoder);
        }
      }
    }
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (super.mutatesTo(paramObject1, paramObject2)) && (getVector(paramObject2).isEmpty());
    }
    
    private static Vector<?> getVector(Object paramObject)
    {
      return (Vector)MetaData.getPrivateFieldValue(paramObject, "java.awt.CardLayout.vector");
    }
  }
  
  static final class java_awt_Choice_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_Choice_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      Choice localChoice1 = (Choice)paramObject1;
      Choice localChoice2 = (Choice)paramObject2;
      for (int i = localChoice2.getItemCount(); i < localChoice1.getItemCount(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localChoice1.getItem(i) }, paramEncoder);
      }
    }
  }
  
  static final class java_awt_Component_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_Component_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      Component localComponent1 = (Component)paramObject1;
      Component localComponent2 = (Component)paramObject2;
      if (!(paramObject1 instanceof Window))
      {
        localObject1 = localComponent1.isBackgroundSet() ? localComponent1.getBackground() : null;
        Object localObject2 = localComponent2.isBackgroundSet() ? localComponent2.getBackground() : null;
        if (!Objects.equals(localObject1, localObject2)) {
          invokeStatement(paramObject1, "setBackground", new Object[] { localObject1 }, paramEncoder);
        }
        Object localObject3 = localComponent1.isForegroundSet() ? localComponent1.getForeground() : null;
        Object localObject4 = localComponent2.isForegroundSet() ? localComponent2.getForeground() : null;
        if (!Objects.equals(localObject3, localObject4)) {
          invokeStatement(paramObject1, "setForeground", new Object[] { localObject3 }, paramEncoder);
        }
        Object localObject5 = localComponent1.isFontSet() ? localComponent1.getFont() : null;
        Object localObject6 = localComponent2.isFontSet() ? localComponent2.getFont() : null;
        if (!Objects.equals(localObject5, localObject6)) {
          invokeStatement(paramObject1, "setFont", new Object[] { localObject5 }, paramEncoder);
        }
      }
      Object localObject1 = localComponent1.getParent();
      if ((localObject1 == null) || (((Container)localObject1).getLayout() == null))
      {
        boolean bool1 = localComponent1.getLocation().equals(localComponent2.getLocation());
        boolean bool2 = localComponent1.getSize().equals(localComponent2.getSize());
        if ((!bool1) && (!bool2)) {
          invokeStatement(paramObject1, "setBounds", new Object[] { localComponent1.getBounds() }, paramEncoder);
        } else if (!bool1) {
          invokeStatement(paramObject1, "setLocation", new Object[] { localComponent1.getLocation() }, paramEncoder);
        } else if (!bool2) {
          invokeStatement(paramObject1, "setSize", new Object[] { localComponent1.getSize() }, paramEncoder);
        }
      }
    }
  }
  
  static final class java_awt_Container_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_Container_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      if ((paramObject1 instanceof JScrollPane)) {
        return;
      }
      Container localContainer1 = (Container)paramObject1;
      Component[] arrayOfComponent1 = localContainer1.getComponents();
      Container localContainer2 = (Container)paramObject2;
      Component[] arrayOfComponent2 = localContainer2 == null ? new Component[0] : localContainer2.getComponents();
      Object localObject1 = (localContainer1.getLayout() instanceof BorderLayout) ? (BorderLayout)localContainer1.getLayout() : null;
      Object localObject2 = (paramObject1 instanceof JLayeredPane) ? (JLayeredPane)paramObject1 : null;
      for (int i = arrayOfComponent2.length; i < arrayOfComponent1.length; i++)
      {
        Object[] arrayOfObject = { localObject2 != null ? new Object[] { arrayOfComponent1[i], Integer.valueOf(((JLayeredPane)localObject2).getLayer(arrayOfComponent1[i])), Integer.valueOf(-1) } : localObject1 != null ? new Object[] { arrayOfComponent1[i], ((BorderLayout)localObject1).getConstraints(arrayOfComponent1[i]) } : arrayOfComponent1[i] };
        invokeStatement(paramObject1, "add", arrayOfObject, paramEncoder);
      }
    }
  }
  
  static final class java_awt_Font_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_awt_Font_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Font localFont = (Font)paramObject;
      int i = 0;
      String str = null;
      int j = 0;
      int k = 12;
      Map localMap = localFont.getAttributes();
      HashMap localHashMap = new HashMap(localMap.size());
      Object localObject1 = localMap.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        TextAttribute localTextAttribute = (TextAttribute)((Iterator)localObject1).next();
        Object localObject2 = localMap.get(localTextAttribute);
        if (localObject2 != null) {
          localHashMap.put(localTextAttribute, localObject2);
        }
        if (localTextAttribute == TextAttribute.FAMILY)
        {
          if ((localObject2 instanceof String))
          {
            i++;
            str = (String)localObject2;
          }
        }
        else if (localTextAttribute == TextAttribute.WEIGHT)
        {
          if (TextAttribute.WEIGHT_REGULAR.equals(localObject2))
          {
            i++;
          }
          else if (TextAttribute.WEIGHT_BOLD.equals(localObject2))
          {
            i++;
            j |= 0x1;
          }
        }
        else if (localTextAttribute == TextAttribute.POSTURE)
        {
          if (TextAttribute.POSTURE_REGULAR.equals(localObject2))
          {
            i++;
          }
          else if (TextAttribute.POSTURE_OBLIQUE.equals(localObject2))
          {
            i++;
            j |= 0x2;
          }
        }
        else if ((localTextAttribute == TextAttribute.SIZE) && ((localObject2 instanceof Number)))
        {
          Number localNumber = (Number)localObject2;
          k = localNumber.intValue();
          if (k == localNumber.floatValue()) {
            i++;
          }
        }
      }
      localObject1 = localFont.getClass();
      if (i == localHashMap.size()) {
        return new Expression(localFont, localObject1, "new", new Object[] { str, Integer.valueOf(j), Integer.valueOf(k) });
      }
      if (localObject1 == Font.class) {
        return new Expression(localFont, localObject1, "getFont", new Object[] { localHashMap });
      }
      return new Expression(localFont, localObject1, "new", new Object[] { Font.getFont(localHashMap) });
    }
  }
  
  static final class java_awt_GridBagLayout_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_GridBagLayout_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      if (getHashtable(paramObject2).isEmpty())
      {
        Iterator localIterator = getHashtable(paramObject1).entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          Object[] arrayOfObject = { localEntry.getKey(), localEntry.getValue() };
          invokeStatement(paramObject1, "addLayoutComponent", arrayOfObject, paramEncoder);
        }
      }
    }
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (super.mutatesTo(paramObject1, paramObject2)) && (getHashtable(paramObject2).isEmpty());
    }
    
    private static Hashtable<?, ?> getHashtable(Object paramObject)
    {
      return (Hashtable)MetaData.getPrivateFieldValue(paramObject, "java.awt.GridBagLayout.comptable");
    }
  }
  
  static final class java_awt_Insets_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_awt_Insets_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Insets localInsets = (Insets)paramObject;
      Object[] arrayOfObject = { Integer.valueOf(top), Integer.valueOf(left), Integer.valueOf(bottom), Integer.valueOf(right) };
      return new Expression(localInsets, localInsets.getClass(), "new", arrayOfObject);
    }
  }
  
  static final class java_awt_List_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_List_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      java.awt.List localList1 = (java.awt.List)paramObject1;
      java.awt.List localList2 = (java.awt.List)paramObject2;
      for (int i = localList2.getItemCount(); i < localList1.getItemCount(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localList1.getItem(i) }, paramEncoder);
      }
    }
  }
  
  static final class java_awt_MenuBar_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_MenuBar_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      MenuBar localMenuBar1 = (MenuBar)paramObject1;
      MenuBar localMenuBar2 = (MenuBar)paramObject2;
      for (int i = localMenuBar2.getMenuCount(); i < localMenuBar1.getMenuCount(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localMenuBar1.getMenu(i) }, paramEncoder);
      }
    }
  }
  
  static final class java_awt_MenuShortcut_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_awt_MenuShortcut_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      MenuShortcut localMenuShortcut = (MenuShortcut)paramObject;
      return new Expression(paramObject, localMenuShortcut.getClass(), "new", new Object[] { new Integer(localMenuShortcut.getKey()), Boolean.valueOf(localMenuShortcut.usesShiftModifier()) });
    }
  }
  
  static final class java_awt_Menu_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_awt_Menu_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      Menu localMenu1 = (Menu)paramObject1;
      Menu localMenu2 = (Menu)paramObject2;
      for (int i = localMenu2.getItemCount(); i < localMenu1.getItemCount(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localMenu1.getItem(i) }, paramEncoder);
      }
    }
  }
  
  static final class java_awt_SystemColor_PersistenceDelegate
    extends MetaData.StaticFieldsPersistenceDelegate
  {
    java_awt_SystemColor_PersistenceDelegate() {}
  }
  
  static final class java_awt_font_TextAttribute_PersistenceDelegate
    extends MetaData.StaticFieldsPersistenceDelegate
  {
    java_awt_font_TextAttribute_PersistenceDelegate() {}
  }
  
  static final class java_beans_beancontext_BeanContextSupport_PersistenceDelegate
    extends MetaData.java_util_Collection_PersistenceDelegate
  {
    java_beans_beancontext_BeanContextSupport_PersistenceDelegate() {}
  }
  
  static final class java_lang_Class_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_lang_Class_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Class localClass = (Class)paramObject;
      if (localClass.isPrimitive())
      {
        localObject = null;
        try
        {
          localObject = PrimitiveWrapperMap.getType(localClass.getName()).getDeclaredField("TYPE");
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          System.err.println("Unknown primitive type: " + localClass);
        }
        return new Expression(paramObject, localObject, "get", new Object[] { null });
      }
      if (paramObject == String.class) {
        return new Expression(paramObject, "", "getClass", new Object[0]);
      }
      if (paramObject == Class.class) {
        return new Expression(paramObject, String.class, "getClass", new Object[0]);
      }
      Object localObject = new Expression(paramObject, Class.class, "forName", new Object[] { localClass.getName() });
      loader = localClass.getClassLoader();
      return (Expression)localObject;
    }
  }
  
  static final class java_lang_String_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_lang_String_PersistenceDelegate() {}
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return null;
    }
    
    public void writeObject(Object paramObject, Encoder paramEncoder) {}
  }
  
  static final class java_lang_reflect_Field_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_lang_reflect_Field_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Field localField = (Field)paramObject;
      return new Expression(paramObject, localField.getDeclaringClass(), "getField", new Object[] { localField.getName() });
    }
  }
  
  static final class java_lang_reflect_Method_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_lang_reflect_Method_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Method localMethod = (Method)paramObject;
      return new Expression(paramObject, localMethod.getDeclaringClass(), "getMethod", new Object[] { localMethod.getName(), localMethod.getParameterTypes() });
    }
  }
  
  static final class java_sql_Timestamp_PersistenceDelegate
    extends MetaData.java_util_Date_PersistenceDelegate
  {
    private static final Method getNanosMethod = ;
    
    java_sql_Timestamp_PersistenceDelegate() {}
    
    private static Method getNanosMethod()
    {
      try
      {
        Class localClass = Class.forName("java.sql.Timestamp", true, null);
        return localClass.getMethod("getNanos", new Class[0]);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        return null;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    private static int getNanos(Object paramObject)
    {
      if (getNanosMethod == null) {
        throw new AssertionError("Should not get here");
      }
      try
      {
        return ((Integer)getNanosMethod.invoke(paramObject, new Object[0])).intValue();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      int i = getNanos(paramObject1);
      if (i != getNanos(paramObject2)) {
        paramEncoder.writeStatement(new Statement(paramObject1, "setNanos", new Object[] { Integer.valueOf(i) }));
      }
    }
  }
  
  static final class java_util_AbstractCollection_PersistenceDelegate
    extends MetaData.java_util_Collection_PersistenceDelegate
  {
    java_util_AbstractCollection_PersistenceDelegate() {}
  }
  
  static final class java_util_AbstractList_PersistenceDelegate
    extends MetaData.java_util_List_PersistenceDelegate
  {
    java_util_AbstractList_PersistenceDelegate() {}
  }
  
  static final class java_util_AbstractMap_PersistenceDelegate
    extends MetaData.java_util_Map_PersistenceDelegate
  {
    java_util_AbstractMap_PersistenceDelegate() {}
  }
  
  static class java_util_Collection_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_util_Collection_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      Collection localCollection1 = (Collection)paramObject1;
      Collection localCollection2 = (Collection)paramObject2;
      if (localCollection2.size() != 0) {
        invokeStatement(paramObject1, "clear", new Object[0], paramEncoder);
      }
      Iterator localIterator = localCollection1.iterator();
      while (localIterator.hasNext()) {
        invokeStatement(paramObject1, "add", new Object[] { localIterator.next() }, paramEncoder);
      }
    }
  }
  
  private static abstract class java_util_Collections
    extends PersistenceDelegate
  {
    private java_util_Collections() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      if (!super.mutatesTo(paramObject1, paramObject2)) {
        return false;
      }
      if (((paramObject1 instanceof java.util.List)) || ((paramObject1 instanceof Set)) || ((paramObject1 instanceof Map))) {
        return paramObject1.equals(paramObject2);
      }
      Collection localCollection1 = (Collection)paramObject1;
      Collection localCollection2 = (Collection)paramObject2;
      return (localCollection1.size() == localCollection2.size()) && (localCollection1.containsAll(localCollection2));
    }
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder) {}
    
    static final class CheckedCollection_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedCollection_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "checkedCollection", new Object[] { localArrayList, localObject });
      }
    }
    
    static final class CheckedList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
        LinkedList localLinkedList = new LinkedList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "checkedList", new Object[] { localLinkedList, localObject });
      }
    }
    
    static final class CheckedMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject1 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.keyType");
        Object localObject2 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.valueType");
        HashMap localHashMap = new HashMap((Map)paramObject);
        return new Expression(paramObject, Collections.class, "checkedMap", new Object[] { localHashMap, localObject1, localObject2 });
      }
    }
    
    static final class CheckedRandomAccessList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedRandomAccessList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "checkedList", new Object[] { localArrayList, localObject });
      }
    }
    
    static final class CheckedSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
        HashSet localHashSet = new HashSet((Set)paramObject);
        return new Expression(paramObject, Collections.class, "checkedSet", new Object[] { localHashSet, localObject });
      }
    }
    
    static final class CheckedSortedMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedSortedMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject1 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.keyType");
        Object localObject2 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.valueType");
        TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
        return new Expression(paramObject, Collections.class, "checkedSortedMap", new Object[] { localTreeMap, localObject1, localObject2 });
      }
    }
    
    static final class CheckedSortedSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      CheckedSortedSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
        TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
        return new Expression(paramObject, Collections.class, "checkedSortedSet", new Object[] { localTreeSet, localObject });
      }
    }
    
    static final class EmptyList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      EmptyList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        return new Expression(paramObject, Collections.class, "emptyList", null);
      }
    }
    
    static final class EmptyMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      EmptyMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        return new Expression(paramObject, Collections.class, "emptyMap", null);
      }
    }
    
    static final class EmptySet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      EmptySet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        return new Expression(paramObject, Collections.class, "emptySet", null);
      }
    }
    
    static final class SingletonList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SingletonList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        java.util.List localList = (java.util.List)paramObject;
        return new Expression(paramObject, Collections.class, "singletonList", new Object[] { localList.get(0) });
      }
    }
    
    static final class SingletonMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SingletonMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Map localMap = (Map)paramObject;
        Object localObject = localMap.keySet().iterator().next();
        return new Expression(paramObject, Collections.class, "singletonMap", new Object[] { localObject, localMap.get(localObject) });
      }
    }
    
    static final class SingletonSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SingletonSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        Set localSet = (Set)paramObject;
        return new Expression(paramObject, Collections.class, "singleton", new Object[] { localSet.iterator().next() });
      }
    }
    
    static final class SynchronizedCollection_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedCollection_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedCollection", new Object[] { localArrayList });
      }
    }
    
    static final class SynchronizedList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        LinkedList localLinkedList = new LinkedList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedList", new Object[] { localLinkedList });
      }
    }
    
    static final class SynchronizedMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        HashMap localHashMap = new HashMap((Map)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedMap", new Object[] { localHashMap });
      }
    }
    
    static final class SynchronizedRandomAccessList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedRandomAccessList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedList", new Object[] { localArrayList });
      }
    }
    
    static final class SynchronizedSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        HashSet localHashSet = new HashSet((Set)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedSet", new Object[] { localHashSet });
      }
    }
    
    static final class SynchronizedSortedMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedSortedMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedSortedMap", new Object[] { localTreeMap });
      }
    }
    
    static final class SynchronizedSortedSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      SynchronizedSortedSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
        return new Expression(paramObject, Collections.class, "synchronizedSortedSet", new Object[] { localTreeSet });
      }
    }
    
    static final class UnmodifiableCollection_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableCollection_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableCollection", new Object[] { localArrayList });
      }
    }
    
    static final class UnmodifiableList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        LinkedList localLinkedList = new LinkedList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableList", new Object[] { localLinkedList });
      }
    }
    
    static final class UnmodifiableMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        HashMap localHashMap = new HashMap((Map)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableMap", new Object[] { localHashMap });
      }
    }
    
    static final class UnmodifiableRandomAccessList_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableRandomAccessList_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        ArrayList localArrayList = new ArrayList((Collection)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableList", new Object[] { localArrayList });
      }
    }
    
    static final class UnmodifiableSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        HashSet localHashSet = new HashSet((Set)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableSet", new Object[] { localHashSet });
      }
    }
    
    static final class UnmodifiableSortedMap_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableSortedMap_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableSortedMap", new Object[] { localTreeMap });
      }
    }
    
    static final class UnmodifiableSortedSet_PersistenceDelegate
      extends MetaData.java_util_Collections
    {
      UnmodifiableSortedSet_PersistenceDelegate()
      {
        super();
      }
      
      protected Expression instantiate(Object paramObject, Encoder paramEncoder)
      {
        TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
        return new Expression(paramObject, Collections.class, "unmodifiableSortedSet", new Object[] { localTreeSet });
      }
    }
  }
  
  static class java_util_Date_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_util_Date_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      if (!super.mutatesTo(paramObject1, paramObject2)) {
        return false;
      }
      Date localDate1 = (Date)paramObject1;
      Date localDate2 = (Date)paramObject2;
      return localDate1.getTime() == localDate2.getTime();
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Date localDate = (Date)paramObject;
      return new Expression(localDate, localDate.getClass(), "new", new Object[] { Long.valueOf(localDate.getTime()) });
    }
  }
  
  static final class java_util_EnumMap_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_util_EnumMap_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (super.mutatesTo(paramObject1, paramObject2)) && (getType(paramObject1) == getType(paramObject2));
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return new Expression(paramObject, EnumMap.class, "new", new Object[] { getType(paramObject) });
    }
    
    private static Object getType(Object paramObject)
    {
      return MetaData.getPrivateFieldValue(paramObject, "java.util.EnumMap.keyType");
    }
  }
  
  static final class java_util_EnumSet_PersistenceDelegate
    extends PersistenceDelegate
  {
    java_util_EnumSet_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (super.mutatesTo(paramObject1, paramObject2)) && (getType(paramObject1) == getType(paramObject2));
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return new Expression(paramObject, EnumSet.class, "noneOf", new Object[] { getType(paramObject) });
    }
    
    private static Object getType(Object paramObject)
    {
      return MetaData.getPrivateFieldValue(paramObject, "java.util.EnumSet.elementType");
    }
  }
  
  static final class java_util_Hashtable_PersistenceDelegate
    extends MetaData.java_util_Map_PersistenceDelegate
  {
    java_util_Hashtable_PersistenceDelegate() {}
  }
  
  static class java_util_List_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_util_List_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      java.util.List localList1 = (java.util.List)paramObject1;
      java.util.List localList2 = (java.util.List)paramObject2;
      int i = localList1.size();
      int j = localList2 == null ? 0 : localList2.size();
      if (i < j)
      {
        invokeStatement(paramObject1, "clear", new Object[0], paramEncoder);
        j = 0;
      }
      for (int k = 0; k < j; k++)
      {
        Integer localInteger = new Integer(k);
        Expression localExpression1 = new Expression(paramObject1, "get", new Object[] { localInteger });
        Expression localExpression2 = new Expression(paramObject2, "get", new Object[] { localInteger });
        try
        {
          Object localObject1 = localExpression1.getValue();
          Object localObject2 = localExpression2.getValue();
          paramEncoder.writeExpression(localExpression1);
          if (!Objects.equals(localObject2, paramEncoder.get(localObject1))) {
            invokeStatement(paramObject1, "set", new Object[] { localInteger, localObject1 }, paramEncoder);
          }
        }
        catch (Exception localException)
        {
          paramEncoder.getExceptionListener().exceptionThrown(localException);
        }
      }
      for (k = j; k < i; k++) {
        invokeStatement(paramObject1, "add", new Object[] { localList1.get(k) }, paramEncoder);
      }
    }
  }
  
  static class java_util_Map_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    java_util_Map_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      Map localMap1 = (Map)paramObject1;
      Map localMap2 = (Map)paramObject2;
      Object localObject3;
      if (localMap2 != null) {
        for (localObject3 : localMap2.keySet().toArray()) {
          if (!localMap1.containsKey(localObject3)) {
            invokeStatement(paramObject1, "remove", new Object[] { localObject3 }, paramEncoder);
          }
        }
      }
      ??? = localMap1.keySet().iterator();
      while (((Iterator)???).hasNext())
      {
        Object localObject2 = ((Iterator)???).next();
        Expression localExpression = new Expression(paramObject1, "get", new Object[] { localObject2 });
        localObject3 = new Expression(paramObject2, "get", new Object[] { localObject2 });
        try
        {
          Object localObject4 = localExpression.getValue();
          Object localObject5 = ((Expression)localObject3).getValue();
          paramEncoder.writeExpression(localExpression);
          if (!Objects.equals(localObject5, paramEncoder.get(localObject4))) {
            invokeStatement(paramObject1, "put", new Object[] { localObject2, localObject4 }, paramEncoder);
          } else if ((localObject5 == null) && (!localMap2.containsKey(localObject2))) {
            invokeStatement(paramObject1, "put", new Object[] { localObject2, localObject4 }, paramEncoder);
          }
        }
        catch (Exception localException)
        {
          paramEncoder.getExceptionListener().exceptionThrown(localException);
        }
      }
    }
  }
  
  static final class javax_swing_Box_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_Box_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return (super.mutatesTo(paramObject1, paramObject2)) && (getAxis(paramObject1).equals(getAxis(paramObject2)));
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return new Expression(paramObject, paramObject.getClass(), "new", new Object[] { getAxis(paramObject) });
    }
    
    private Integer getAxis(Object paramObject)
    {
      Box localBox = (Box)paramObject;
      return (Integer)MetaData.getPrivateFieldValue(localBox.getLayout(), "javax.swing.BoxLayout.axis");
    }
  }
  
  static final class javax_swing_DefaultComboBoxModel_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_DefaultComboBoxModel_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      DefaultComboBoxModel localDefaultComboBoxModel = (DefaultComboBoxModel)paramObject1;
      for (int i = 0; i < localDefaultComboBoxModel.getSize(); i++) {
        invokeStatement(paramObject1, "addElement", new Object[] { localDefaultComboBoxModel.getElementAt(i) }, paramEncoder);
      }
    }
  }
  
  static final class javax_swing_DefaultListModel_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_DefaultListModel_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      DefaultListModel localDefaultListModel1 = (DefaultListModel)paramObject1;
      DefaultListModel localDefaultListModel2 = (DefaultListModel)paramObject2;
      for (int i = localDefaultListModel2.getSize(); i < localDefaultListModel1.getSize(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localDefaultListModel1.getElementAt(i) }, paramEncoder);
      }
    }
  }
  
  static final class javax_swing_JFrame_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_JFrame_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      Window localWindow1 = (Window)paramObject1;
      Window localWindow2 = (Window)paramObject2;
      boolean bool1 = localWindow1.isVisible();
      boolean bool2 = localWindow2.isVisible();
      if (bool2 != bool1)
      {
        boolean bool3 = executeStatements;
        executeStatements = false;
        invokeStatement(paramObject1, "setVisible", new Object[] { Boolean.valueOf(bool1) }, paramEncoder);
        executeStatements = bool3;
      }
    }
  }
  
  static final class javax_swing_JMenu_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_JMenu_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      JMenu localJMenu = (JMenu)paramObject1;
      Component[] arrayOfComponent = localJMenu.getMenuComponents();
      for (int i = 0; i < arrayOfComponent.length; i++) {
        invokeStatement(paramObject1, "add", new Object[] { arrayOfComponent[i] }, paramEncoder);
      }
    }
  }
  
  static final class javax_swing_JTabbedPane_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_JTabbedPane_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      JTabbedPane localJTabbedPane = (JTabbedPane)paramObject1;
      for (int i = 0; i < localJTabbedPane.getTabCount(); i++) {
        invokeStatement(paramObject1, "addTab", new Object[] { localJTabbedPane.getTitleAt(i), localJTabbedPane.getIconAt(i), localJTabbedPane.getComponentAt(i) }, paramEncoder);
      }
    }
  }
  
  static final class javax_swing_ToolTipManager_PersistenceDelegate
    extends PersistenceDelegate
  {
    javax_swing_ToolTipManager_PersistenceDelegate() {}
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      return new Expression(paramObject, ToolTipManager.class, "sharedInstance", new Object[0]);
    }
  }
  
  static final class javax_swing_border_MatteBorder_PersistenceDelegate
    extends PersistenceDelegate
  {
    javax_swing_border_MatteBorder_PersistenceDelegate() {}
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      MatteBorder localMatteBorder = (MatteBorder)paramObject;
      Insets localInsets = localMatteBorder.getBorderInsets();
      Object localObject = localMatteBorder.getTileIcon();
      if (localObject == null) {
        localObject = localMatteBorder.getMatteColor();
      }
      Object[] arrayOfObject = { Integer.valueOf(top), Integer.valueOf(left), Integer.valueOf(bottom), Integer.valueOf(right), localObject };
      return new Expression(localMatteBorder, localMatteBorder.getClass(), "new", arrayOfObject);
    }
  }
  
  static final class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate
    extends DefaultPersistenceDelegate
  {
    javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate() {}
    
    protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    {
      super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
      DefaultMutableTreeNode localDefaultMutableTreeNode1 = (DefaultMutableTreeNode)paramObject1;
      DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)paramObject2;
      for (int i = localDefaultMutableTreeNode2.getChildCount(); i < localDefaultMutableTreeNode1.getChildCount(); i++) {
        invokeStatement(paramObject1, "add", new Object[] { localDefaultMutableTreeNode1.getChildAt(i) }, paramEncoder);
      }
    }
  }
  
  static final class sun_swing_PrintColorUIResource_PersistenceDelegate
    extends PersistenceDelegate
  {
    sun_swing_PrintColorUIResource_PersistenceDelegate() {}
    
    protected boolean mutatesTo(Object paramObject1, Object paramObject2)
    {
      return paramObject1.equals(paramObject2);
    }
    
    protected Expression instantiate(Object paramObject, Encoder paramEncoder)
    {
      Color localColor = (Color)paramObject;
      Object[] arrayOfObject = { Integer.valueOf(localColor.getRGB()) };
      return new Expression(localColor, ColorUIResource.class, "new", arrayOfObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\MetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */