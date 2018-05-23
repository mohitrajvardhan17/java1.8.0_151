package javax.imageio.metadata;

import com.sun.imageio.plugins.common.StandardMetadataFormat;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.ImageTypeSpecifier;

public abstract class IIOMetadataFormatImpl
  implements IIOMetadataFormat
{
  public static final String standardMetadataFormatName = "javax_imageio_1.0";
  private static IIOMetadataFormat standardFormat = null;
  private String resourceBaseName = getClass().getName() + "Resources";
  private String rootName;
  private HashMap elementMap = new HashMap();
  
  public IIOMetadataFormatImpl(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("rootName == null!");
    }
    if ((paramInt < 0) || (paramInt > 5) || (paramInt == 5)) {
      throw new IllegalArgumentException("Invalid value for childPolicy!");
    }
    rootName = paramString;
    Element localElement = new Element();
    elementName = paramString;
    childPolicy = paramInt;
    elementMap.put(paramString, localElement);
  }
  
  public IIOMetadataFormatImpl(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("rootName == null!");
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("minChildren < 0!");
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("minChildren > maxChildren!");
    }
    Element localElement = new Element();
    elementName = paramString;
    childPolicy = 5;
    minChildren = paramInt1;
    maxChildren = paramInt2;
    rootName = paramString;
    elementMap.put(paramString, localElement);
  }
  
  protected void setResourceBaseName(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("resourceBaseName == null!");
    }
    resourceBaseName = paramString;
  }
  
  protected String getResourceBaseName()
  {
    return resourceBaseName;
  }
  
  private Element getElement(String paramString, boolean paramBoolean)
  {
    if ((paramBoolean) && (paramString == null)) {
      throw new IllegalArgumentException("element name is null!");
    }
    Element localElement = (Element)elementMap.get(paramString);
    if ((paramBoolean) && (localElement == null)) {
      throw new IllegalArgumentException("No such element: " + paramString);
    }
    return localElement;
  }
  
  private Element getElement(String paramString)
  {
    return getElement(paramString, true);
  }
  
  private Attribute getAttribute(String paramString1, String paramString2)
  {
    Element localElement = getElement(paramString1);
    Attribute localAttribute = (Attribute)attrMap.get(paramString2);
    if (localAttribute == null) {
      throw new IllegalArgumentException("No such attribute \"" + paramString2 + "\"!");
    }
    return localAttribute;
  }
  
  protected void addElement(String paramString1, String paramString2, int paramInt)
  {
    Element localElement1 = getElement(paramString2);
    if ((paramInt < 0) || (paramInt > 5) || (paramInt == 5)) {
      throw new IllegalArgumentException("Invalid value for childPolicy!");
    }
    Element localElement2 = new Element();
    elementName = paramString1;
    childPolicy = paramInt;
    childList.add(paramString1);
    parentList.add(paramString2);
    elementMap.put(paramString1, localElement2);
  }
  
  protected void addElement(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    Element localElement1 = getElement(paramString2);
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("minChildren < 0!");
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("minChildren > maxChildren!");
    }
    Element localElement2 = new Element();
    elementName = paramString1;
    childPolicy = 5;
    minChildren = paramInt1;
    maxChildren = paramInt2;
    childList.add(paramString1);
    parentList.add(paramString2);
    elementMap.put(paramString1, localElement2);
  }
  
  protected void addChildElement(String paramString1, String paramString2)
  {
    Element localElement1 = getElement(paramString2);
    Element localElement2 = getElement(paramString1);
    childList.add(paramString1);
    parentList.add(paramString2);
  }
  
  protected void removeElement(String paramString)
  {
    Element localElement1 = getElement(paramString, false);
    if (localElement1 != null)
    {
      Iterator localIterator = parentList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Element localElement2 = getElement(str, false);
        if (localElement2 != null) {
          childList.remove(paramString);
        }
      }
      elementMap.remove(paramString);
    }
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean, String paramString3)
  {
    Element localElement = getElement(paramString1);
    if (paramString2 == null) {
      throw new IllegalArgumentException("attrName == null!");
    }
    if ((paramInt < 0) || (paramInt > 4)) {
      throw new IllegalArgumentException("Invalid value for dataType!");
    }
    Attribute localAttribute = new Attribute();
    attrName = paramString2;
    valueType = 1;
    dataType = paramInt;
    required = paramBoolean;
    defaultValue = paramString3;
    attrList.add(paramString2);
    attrMap.put(paramString2, localAttribute);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean, String paramString3, List<String> paramList)
  {
    Element localElement = getElement(paramString1);
    if (paramString2 == null) {
      throw new IllegalArgumentException("attrName == null!");
    }
    if ((paramInt < 0) || (paramInt > 4)) {
      throw new IllegalArgumentException("Invalid value for dataType!");
    }
    if (paramList == null) {
      throw new IllegalArgumentException("enumeratedValues == null!");
    }
    if (paramList.size() == 0) {
      throw new IllegalArgumentException("enumeratedValues is empty!");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      localObject = localIterator.next();
      if (localObject == null) {
        throw new IllegalArgumentException("enumeratedValues contains a null!");
      }
      if (!(localObject instanceof String)) {
        throw new IllegalArgumentException("enumeratedValues contains a non-String value!");
      }
    }
    Object localObject = new Attribute();
    attrName = paramString2;
    valueType = 16;
    dataType = paramInt;
    required = paramBoolean;
    defaultValue = paramString3;
    enumeratedValues = paramList;
    attrList.add(paramString2);
    attrMap.put(paramString2, localObject);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean1, String paramString3, String paramString4, String paramString5, boolean paramBoolean2, boolean paramBoolean3)
  {
    Element localElement = getElement(paramString1);
    if (paramString2 == null) {
      throw new IllegalArgumentException("attrName == null!");
    }
    if ((paramInt < 0) || (paramInt > 4)) {
      throw new IllegalArgumentException("Invalid value for dataType!");
    }
    Attribute localAttribute = new Attribute();
    attrName = paramString2;
    valueType = 2;
    if (paramBoolean2) {
      valueType |= 0x4;
    }
    if (paramBoolean3) {
      valueType |= 0x8;
    }
    dataType = paramInt;
    required = paramBoolean1;
    defaultValue = paramString3;
    minValue = paramString4;
    maxValue = paramString5;
    attrList.add(paramString2);
    attrMap.put(paramString2, localAttribute);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    Element localElement = getElement(paramString1);
    if (paramString2 == null) {
      throw new IllegalArgumentException("attrName == null!");
    }
    if ((paramInt1 < 0) || (paramInt1 > 4)) {
      throw new IllegalArgumentException("Invalid value for dataType!");
    }
    if ((paramInt2 < 0) || (paramInt2 > paramInt3)) {
      throw new IllegalArgumentException("Invalid list bounds!");
    }
    Attribute localAttribute = new Attribute();
    attrName = paramString2;
    valueType = 32;
    dataType = paramInt1;
    required = paramBoolean;
    listMinLength = paramInt2;
    listMaxLength = paramInt3;
    attrList.add(paramString2);
    attrMap.put(paramString2, localAttribute);
  }
  
  protected void addBooleanAttribute(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("TRUE");
    localArrayList.add("FALSE");
    String str = null;
    if (paramBoolean1) {
      str = paramBoolean2 ? "TRUE" : "FALSE";
    }
    addAttribute(paramString1, paramString2, 1, true, str, localArrayList);
  }
  
  protected void removeAttribute(String paramString1, String paramString2)
  {
    Element localElement = getElement(paramString1);
    attrList.remove(paramString2);
    attrMap.remove(paramString2);
  }
  
  protected <T> void addObjectValue(String paramString, Class<T> paramClass, boolean paramBoolean, T paramT)
  {
    Element localElement = getElement(paramString);
    ObjectValue localObjectValue = new ObjectValue();
    valueType = 1;
    classType = paramClass;
    defaultValue = paramT;
    objectValue = localObjectValue;
  }
  
  protected <T> void addObjectValue(String paramString, Class<T> paramClass, boolean paramBoolean, T paramT, List<? extends T> paramList)
  {
    Element localElement = getElement(paramString);
    if (paramList == null) {
      throw new IllegalArgumentException("enumeratedValues == null!");
    }
    if (paramList.size() == 0) {
      throw new IllegalArgumentException("enumeratedValues is empty!");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      localObject = localIterator.next();
      if (localObject == null) {
        throw new IllegalArgumentException("enumeratedValues contains a null!");
      }
      if (!paramClass.isInstance(localObject)) {
        throw new IllegalArgumentException("enumeratedValues contains a value not of class classType!");
      }
    }
    Object localObject = new ObjectValue();
    valueType = 16;
    classType = paramClass;
    defaultValue = paramT;
    enumeratedValues = paramList;
    objectValue = ((ObjectValue)localObject);
  }
  
  protected <T,  extends Comparable<? super T>> void addObjectValue(String paramString, Class<T> paramClass, T paramT, Comparable<? super T> paramComparable1, Comparable<? super T> paramComparable2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Element localElement = getElement(paramString);
    ObjectValue localObjectValue = new ObjectValue();
    valueType = 2;
    if (paramBoolean1) {
      valueType |= 0x4;
    }
    if (paramBoolean2) {
      valueType |= 0x8;
    }
    classType = paramClass;
    defaultValue = paramT;
    minValue = paramComparable1;
    maxValue = paramComparable2;
    objectValue = localObjectValue;
  }
  
  protected void addObjectValue(String paramString, Class<?> paramClass, int paramInt1, int paramInt2)
  {
    Element localElement = getElement(paramString);
    ObjectValue localObjectValue = new ObjectValue();
    valueType = 32;
    classType = paramClass;
    arrayMinLength = paramInt1;
    arrayMaxLength = paramInt2;
    objectValue = localObjectValue;
  }
  
  protected void removeObjectValue(String paramString)
  {
    Element localElement = getElement(paramString);
    objectValue = null;
  }
  
  public String getRootName()
  {
    return rootName;
  }
  
  public abstract boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier);
  
  public int getElementMinChildren(String paramString)
  {
    Element localElement = getElement(paramString);
    if (childPolicy != 5) {
      throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
    }
    return minChildren;
  }
  
  public int getElementMaxChildren(String paramString)
  {
    Element localElement = getElement(paramString);
    if (childPolicy != 5) {
      throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
    }
    return maxChildren;
  }
  
  private String getResource(String paramString, Locale paramLocale)
  {
    if (paramLocale == null) {
      paramLocale = Locale.getDefault();
    }
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
    ResourceBundle localResourceBundle = null;
    try
    {
      localResourceBundle = ResourceBundle.getBundle(resourceBaseName, paramLocale, localClassLoader);
    }
    catch (MissingResourceException localMissingResourceException1)
    {
      try
      {
        localResourceBundle = ResourceBundle.getBundle(resourceBaseName, paramLocale);
      }
      catch (MissingResourceException localMissingResourceException3)
      {
        return null;
      }
    }
    try
    {
      return localResourceBundle.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException2) {}
    return null;
  }
  
  public String getElementDescription(String paramString, Locale paramLocale)
  {
    Element localElement = getElement(paramString);
    return getResource(paramString, paramLocale);
  }
  
  public int getChildPolicy(String paramString)
  {
    Element localElement = getElement(paramString);
    return childPolicy;
  }
  
  public String[] getChildNames(String paramString)
  {
    Element localElement = getElement(paramString);
    if (childPolicy == 0) {
      return null;
    }
    return (String[])childList.toArray(new String[0]);
  }
  
  public String[] getAttributeNames(String paramString)
  {
    Element localElement = getElement(paramString);
    List localList = attrList;
    String[] arrayOfString = new String[localList.size()];
    return (String[])localList.toArray(arrayOfString);
  }
  
  public int getAttributeValueType(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    return valueType;
  }
  
  public int getAttributeDataType(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    return dataType;
  }
  
  public boolean isAttributeRequired(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    return required;
  }
  
  public String getAttributeDefaultValue(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    return defaultValue;
  }
  
  public String[] getAttributeEnumerations(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    if (valueType != 16) {
      throw new IllegalArgumentException("Attribute not an enumeration!");
    }
    List localList = enumeratedValues;
    Iterator localIterator = localList.iterator();
    String[] arrayOfString = new String[localList.size()];
    return (String[])localList.toArray(arrayOfString);
  }
  
  public String getAttributeMinValue(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    if ((valueType != 2) && (valueType != 6) && (valueType != 10) && (valueType != 14)) {
      throw new IllegalArgumentException("Attribute not a range!");
    }
    return minValue;
  }
  
  public String getAttributeMaxValue(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    if ((valueType != 2) && (valueType != 6) && (valueType != 10) && (valueType != 14)) {
      throw new IllegalArgumentException("Attribute not a range!");
    }
    return maxValue;
  }
  
  public int getAttributeListMinLength(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    if (valueType != 32) {
      throw new IllegalArgumentException("Attribute not a list!");
    }
    return listMinLength;
  }
  
  public int getAttributeListMaxLength(String paramString1, String paramString2)
  {
    Attribute localAttribute = getAttribute(paramString1, paramString2);
    if (valueType != 32) {
      throw new IllegalArgumentException("Attribute not a list!");
    }
    return listMaxLength;
  }
  
  public String getAttributeDescription(String paramString1, String paramString2, Locale paramLocale)
  {
    Element localElement = getElement(paramString1);
    if (paramString2 == null) {
      throw new IllegalArgumentException("attrName == null!");
    }
    Attribute localAttribute = (Attribute)attrMap.get(paramString2);
    if (localAttribute == null) {
      throw new IllegalArgumentException("No such attribute!");
    }
    String str = paramString1 + "/" + paramString2;
    return getResource(str, paramLocale);
  }
  
  private ObjectValue getObjectValue(String paramString)
  {
    Element localElement = getElement(paramString);
    ObjectValue localObjectValue = objectValue;
    if (localObjectValue == null) {
      throw new IllegalArgumentException("No object within element " + paramString + "!");
    }
    return localObjectValue;
  }
  
  public int getObjectValueType(String paramString)
  {
    Element localElement = getElement(paramString);
    ObjectValue localObjectValue = objectValue;
    if (localObjectValue == null) {
      return 0;
    }
    return valueType;
  }
  
  public Class<?> getObjectClass(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    return classType;
  }
  
  public Object getObjectDefaultValue(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    return defaultValue;
  }
  
  public Object[] getObjectEnumerations(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    if (valueType != 16) {
      throw new IllegalArgumentException("Not an enumeration!");
    }
    List localList = enumeratedValues;
    Object[] arrayOfObject = new Object[localList.size()];
    return localList.toArray(arrayOfObject);
  }
  
  public Comparable<?> getObjectMinValue(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    if ((valueType & 0x2) != 2) {
      throw new IllegalArgumentException("Not a range!");
    }
    return minValue;
  }
  
  public Comparable<?> getObjectMaxValue(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    if ((valueType & 0x2) != 2) {
      throw new IllegalArgumentException("Not a range!");
    }
    return maxValue;
  }
  
  public int getObjectArrayMinLength(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    if (valueType != 32) {
      throw new IllegalArgumentException("Not a list!");
    }
    return arrayMinLength;
  }
  
  public int getObjectArrayMaxLength(String paramString)
  {
    ObjectValue localObjectValue = getObjectValue(paramString);
    if (valueType != 32) {
      throw new IllegalArgumentException("Not a list!");
    }
    return arrayMaxLength;
  }
  
  private static synchronized void createStandardFormat()
  {
    if (standardFormat == null) {
      standardFormat = new StandardMetadataFormat();
    }
  }
  
  public static IIOMetadataFormat getStandardFormatInstance()
  {
    createStandardFormat();
    return standardFormat;
  }
  
  class Attribute
  {
    String attrName;
    int valueType = 1;
    int dataType;
    boolean required;
    String defaultValue = null;
    List enumeratedValues;
    String minValue;
    String maxValue;
    int listMinLength;
    int listMaxLength;
    
    Attribute() {}
  }
  
  class Element
  {
    String elementName;
    int childPolicy;
    int minChildren = 0;
    int maxChildren = 0;
    List childList = new ArrayList();
    List parentList = new ArrayList();
    List attrList = new ArrayList();
    Map attrMap = new HashMap();
    IIOMetadataFormatImpl.ObjectValue objectValue;
    
    Element() {}
  }
  
  class ObjectValue
  {
    int valueType = 0;
    Class classType = null;
    Object defaultValue = null;
    List enumeratedValues = null;
    Comparable minValue = null;
    Comparable maxValue = null;
    int arrayMinLength = 0;
    int arrayMaxLength = 0;
    
    ObjectValue() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIOMetadataFormatImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */