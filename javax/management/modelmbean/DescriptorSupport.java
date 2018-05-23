package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ReflectUtil;

public class DescriptorSupport
  implements Descriptor
{
  private static final long oldSerialVersionUID = 8071560848919417985L;
  private static final long newSerialVersionUID = -6292969195866300415L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("descriptor", HashMap.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("descriptor", HashMap.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static final String serialForm;
  private transient SortedMap<String, Object> descriptorMap;
  private static final String currClass = "DescriptorSupport";
  private static final String[] entities;
  private static final Map<String, Character> entityToCharMap;
  private static final String[] charToEntityMap;
  
  public DescriptorSupport()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "DescriptorSupport()", "Constructor");
    }
    init(null);
  }
  
  public DescriptorSupport(int paramInt)
    throws MBeanException, RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields = " + paramInt + ")", "Constructor");
    }
    if (paramInt <= 0)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields)", "Illegal arguments: initNumFields <= 0");
      }
      String str = "Descriptor field limit invalid: " + paramInt;
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(str);
      throw new RuntimeOperationsException(localIllegalArgumentException, str);
    }
    init(null);
  }
  
  public DescriptorSupport(DescriptorSupport paramDescriptorSupport)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(Descriptor)", "Constructor");
    }
    if (paramDescriptorSupport == null) {
      init(null);
    } else {
      init(descriptorMap);
    }
  }
  
  public DescriptorSupport(String paramString)
    throws MBeanException, RuntimeOperationsException, XMLParseException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = '" + paramString + "')", "Constructor");
    }
    if (paramString == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = null)", "Illegal arguments");
      }
      localObject1 = new IllegalArgumentException("String in parameter is null");
      throw new RuntimeOperationsException((RuntimeException)localObject1, "String in parameter is null");
    }
    String str1 = paramString.toLowerCase();
    if ((!str1.startsWith("<descriptor>")) || (!str1.endsWith("</descriptor>"))) {
      throw new XMLParseException("No <descriptor>, </descriptor> pair");
    }
    init(null);
    Object localObject1 = new StringTokenizer(paramString, "<> \t\n\r\f");
    int i = 0;
    int j = 0;
    Object localObject2 = null;
    Object localObject3 = null;
    while (((StringTokenizer)localObject1).hasMoreTokens())
    {
      String str2 = ((StringTokenizer)localObject1).nextToken();
      if (str2.equalsIgnoreCase("FIELD"))
      {
        i = 1;
      }
      else if (str2.equalsIgnoreCase("/FIELD"))
      {
        if ((localObject2 != null) && (localObject3 != null))
        {
          localObject2 = ((String)localObject2).substring(((String)localObject2).indexOf('"') + 1, ((String)localObject2).lastIndexOf('"'));
          Object localObject4 = parseQuotedFieldValue((String)localObject3);
          setField((String)localObject2, localObject4);
        }
        localObject2 = null;
        localObject3 = null;
        i = 0;
      }
      else if (str2.equalsIgnoreCase("DESCRIPTOR"))
      {
        j = 1;
      }
      else if (str2.equalsIgnoreCase("/DESCRIPTOR"))
      {
        j = 0;
        localObject2 = null;
        localObject3 = null;
        i = 0;
      }
      else if ((i != 0) && (j != 0))
      {
        int k = str2.indexOf("=");
        String str3;
        if (k > 0)
        {
          str3 = str2.substring(0, k);
          String str4 = str2.substring(k + 1);
          if (str3.equalsIgnoreCase("NAME"))
          {
            localObject2 = str4;
          }
          else if (str3.equalsIgnoreCase("VALUE"))
          {
            localObject3 = str4;
          }
          else
          {
            String str5 = "Expected `name' or `value', got `" + str2 + "'";
            throw new XMLParseException(str5);
          }
        }
        else
        {
          str3 = "Expected `keyword=value', got `" + str2 + "'";
          throw new XMLParseException(str3);
        }
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(XMLString)", "Exit");
    }
  }
  
  public DescriptorSupport(String[] paramArrayOfString, Object[] paramArrayOfObject)
    throws RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Constructor");
    }
    if ((paramArrayOfString == null) || (paramArrayOfObject == null) || (paramArrayOfString.length != paramArrayOfObject.length))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Illegal arguments");
      }
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Null or invalid fieldNames or fieldValues");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Null or invalid fieldNames or fieldValues");
    }
    init(null);
    for (int i = 0; i < paramArrayOfString.length; i++) {
      setField(paramArrayOfString[i], paramArrayOfObject[i]);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Exit");
    }
  }
  
  public DescriptorSupport(String... paramVarArgs)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Constructor");
    }
    init(null);
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return;
    }
    init(null);
    for (int i = 0; i < paramVarArgs.length; i++) {
      if ((paramVarArgs[i] != null) && (!paramVarArgs[i].equals("")))
      {
        int j = paramVarArgs[i].indexOf("=");
        if (j < 0)
        {
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: field does not have '=' as a name and value separator");
          }
          localObject = new IllegalArgumentException("Field in invalid format: no equals sign");
          throw new RuntimeOperationsException((RuntimeException)localObject, "Field in invalid format: no equals sign");
        }
        String str = paramVarArgs[i].substring(0, j);
        Object localObject = null;
        if (j < paramVarArgs[i].length()) {
          localObject = paramVarArgs[i].substring(j + 1);
        }
        if (str.equals(""))
        {
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: fieldName is empty");
          }
          IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Field in invalid format: no fieldName");
          throw new RuntimeOperationsException(localIllegalArgumentException, "Field in invalid format: no fieldName");
        }
        setField(str, localObject);
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Exit");
    }
  }
  
  private void init(Map<String, ?> paramMap)
  {
    descriptorMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if (paramMap != null) {
      descriptorMap.putAll(paramMap);
    }
  }
  
  public synchronized Object getFieldValue(String paramString)
    throws RuntimeOperationsException
  {
    if ((paramString == null) || (paramString.equals("")))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName)", "Illegal arguments: null field name");
      }
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Fieldname requested is null");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Fieldname requested is null");
    }
    Object localObject = descriptorMap.get(paramString);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName = " + paramString + ")", "Returns '" + localObject + "'");
    }
    return localObject;
  }
  
  public synchronized void setField(String paramString, Object paramObject)
    throws RuntimeOperationsException
  {
    IllegalArgumentException localIllegalArgumentException;
    if ((paramString == null) || (paramString.equals("")))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments: null or empty field name");
      }
      localIllegalArgumentException = new IllegalArgumentException("Field name to be set is null or empty");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Field name to be set is null or empty");
    }
    if (!validateField(paramString, paramObject))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments");
      }
      String str = "Field value invalid: " + paramString + "=" + paramObject;
      localIllegalArgumentException = new IllegalArgumentException(str);
      throw new RuntimeOperationsException(localIllegalArgumentException, str);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Entry: setting '" + paramString + "' to '" + paramObject + "'");
    }
    descriptorMap.put(paramString, paramObject);
  }
  
  public synchronized String[] getFields()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Entry");
    }
    int i = descriptorMap.size();
    String[] arrayOfString = new String[i];
    Set localSet = descriptorMap.entrySet();
    int j = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Returning " + i + " fields");
    }
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localEntry == null)
      {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Element is null");
        }
      }
      else
      {
        Object localObject = localEntry.getValue();
        if (localObject == null) {
          arrayOfString[j] = ((String)localEntry.getKey() + "=");
        } else if ((localObject instanceof String)) {
          arrayOfString[j] = ((String)localEntry.getKey() + "=" + localObject.toString());
        } else {
          arrayOfString[j] = ((String)localEntry.getKey() + "=(" + localObject.toString() + ")");
        }
      }
      j++;
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Exit");
    }
    return arrayOfString;
  }
  
  public synchronized String[] getFieldNames()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Entry");
    }
    int i = descriptorMap.size();
    String[] arrayOfString = new String[i];
    Set localSet = descriptorMap.entrySet();
    int j = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Returning " + i + " fields");
    }
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((localEntry == null) || (localEntry.getKey() == null))
      {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Field is null");
        }
      }
      else {
        arrayOfString[j] = ((String)localEntry.getKey()).toString();
      }
      j++;
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Exit");
    }
    return arrayOfString;
  }
  
  public synchronized Object[] getFieldValues(String... paramVarArgs)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Entry");
    }
    int i = paramVarArgs == null ? descriptorMap.size() : paramVarArgs.length;
    Object[] arrayOfObject = new Object[i];
    int j = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Returning " + i + " fields");
    }
    if (paramVarArgs == null)
    {
      Iterator localIterator = descriptorMap.values().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        arrayOfObject[(j++)] = localObject;
      }
    }
    else
    {
      for (j = 0; j < paramVarArgs.length; j++) {
        if ((paramVarArgs[j] == null) || (paramVarArgs[j].equals(""))) {
          arrayOfObject[j] = null;
        } else {
          arrayOfObject[j] = getFieldValue(paramVarArgs[j]);
        }
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Exit");
    }
    return arrayOfObject;
  }
  
  public synchronized void setFields(String[] paramArrayOfString, Object[] paramArrayOfObject)
    throws RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Entry");
    }
    if ((paramArrayOfString == null) || (paramArrayOfObject == null) || (paramArrayOfString.length != paramArrayOfObject.length))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Illegal arguments");
      }
      IllegalArgumentException localIllegalArgumentException1 = new IllegalArgumentException("fieldNames and fieldValues are null or invalid");
      throw new RuntimeOperationsException(localIllegalArgumentException1, "fieldNames and fieldValues are null or invalid");
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].equals("")))
      {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Null field name encountered at element " + i);
        }
        IllegalArgumentException localIllegalArgumentException2 = new IllegalArgumentException("fieldNames is null or invalid");
        throw new RuntimeOperationsException(localIllegalArgumentException2, "fieldNames is null or invalid");
      }
      setField(paramArrayOfString[i], paramArrayOfObject[i]);
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Exit");
    }
  }
  
  public synchronized Object clone()
    throws RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "clone()", "Entry");
    }
    return new DescriptorSupport(this);
  }
  
  public synchronized void removeField(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return;
    }
    descriptorMap.remove(paramString);
  }
  
  public synchronized boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Descriptor)) {
      return false;
    }
    if ((paramObject instanceof ImmutableDescriptor)) {
      return paramObject.equals(this);
    }
    return new ImmutableDescriptor(descriptorMap).equals(paramObject);
  }
  
  public synchronized int hashCode()
  {
    int i = descriptorMap.size();
    return Util.hashCode((String[])descriptorMap.keySet().toArray(new String[i]), descriptorMap.values().toArray(new Object[i]));
  }
  
  public synchronized boolean isValid()
    throws RuntimeOperationsException
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Entry");
    }
    Set localSet = descriptorMap.entrySet();
    if (localSet == null)
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns false (null set)");
      }
      return false;
    }
    String str1 = (String)getFieldValue("name");
    String str2 = (String)getFieldValue("descriptorType");
    if ((str1 == null) || (str2 == null) || (str1.equals("")) || (str2.equals(""))) {
      return false;
    }
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((localEntry != null) && (localEntry.getValue() != null)) {
        if (!validateField(((String)localEntry.getKey()).toString(), localEntry.getValue().toString()))
        {
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Field " + (String)localEntry.getKey() + "=" + localEntry.getValue() + " is not valid");
          }
          return false;
        }
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns true");
    }
    return true;
  }
  
  private boolean validateField(String paramString, Object paramObject)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return false;
    }
    String str = "";
    int i = 0;
    if ((paramObject != null) && ((paramObject instanceof String)))
    {
      str = (String)paramObject;
      i = 1;
    }
    int j = (paramString.equalsIgnoreCase("Name")) || (paramString.equalsIgnoreCase("DescriptorType")) ? 1 : 0;
    if ((j != 0) || (paramString.equalsIgnoreCase("SetMethod")) || (paramString.equalsIgnoreCase("GetMethod")) || (paramString.equalsIgnoreCase("Role")) || (paramString.equalsIgnoreCase("Class")))
    {
      if ((paramObject == null) || (i == 0)) {
        return false;
      }
      return (j == 0) || (!str.equals(""));
    }
    long l;
    if (paramString.equalsIgnoreCase("visibility"))
    {
      if ((paramObject != null) && (i != 0)) {
        l = toNumeric(str);
      } else if ((paramObject instanceof Integer)) {
        l = ((Integer)paramObject).intValue();
      } else {
        return false;
      }
      return (l >= 1L) && (l <= 4L);
    }
    if (paramString.equalsIgnoreCase("severity"))
    {
      if ((paramObject != null) && (i != 0)) {
        l = toNumeric(str);
      } else if ((paramObject instanceof Integer)) {
        l = ((Integer)paramObject).intValue();
      } else {
        return false;
      }
      return (l >= 0L) && (l <= 6L);
    }
    if (paramString.equalsIgnoreCase("PersistPolicy")) {
      return (paramObject != null) && (i != 0) && ((str.equalsIgnoreCase("OnUpdate")) || (str.equalsIgnoreCase("OnTimer")) || (str.equalsIgnoreCase("NoMoreOftenThan")) || (str.equalsIgnoreCase("Always")) || (str.equalsIgnoreCase("Never")) || (str.equalsIgnoreCase("OnUnregister")));
    }
    if ((paramString.equalsIgnoreCase("PersistPeriod")) || (paramString.equalsIgnoreCase("CurrencyTimeLimit")) || (paramString.equalsIgnoreCase("LastUpdatedTimeStamp")) || (paramString.equalsIgnoreCase("LastReturnedTimeStamp")))
    {
      if ((paramObject != null) && (i != 0)) {
        l = toNumeric(str);
      } else if ((paramObject instanceof Number)) {
        l = ((Number)paramObject).longValue();
      } else {
        return false;
      }
      return l >= -1L;
    }
    if (paramString.equalsIgnoreCase("log")) {
      return ((paramObject instanceof Boolean)) || ((i != 0) && ((str.equalsIgnoreCase("T")) || (str.equalsIgnoreCase("true")) || (str.equalsIgnoreCase("F")) || (str.equalsIgnoreCase("false"))));
    }
    return true;
  }
  
  public synchronized String toXMLString()
  {
    StringBuilder localStringBuilder = new StringBuilder("<Descriptor>");
    Set localSet = descriptorMap.entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      Object localObject = localEntry.getValue();
      String str2 = null;
      if ((localObject instanceof String))
      {
        String str3 = (String)localObject;
        if ((!str3.startsWith("(")) || (!str3.endsWith(")"))) {
          str2 = quote(str3);
        }
      }
      if (str2 == null) {
        str2 = makeFieldValue(localObject);
      }
      localStringBuilder.append("<field name=\"").append(str1).append("\" value=\"").append(str2).append("\"></field>");
    }
    localStringBuilder.append("</Descriptor>");
    return localStringBuilder.toString();
  }
  
  private static boolean isMagic(char paramChar)
  {
    return (paramChar < charToEntityMap.length) && (charToEntityMap[paramChar] != null);
  }
  
  private static String quote(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++) {
      if (isMagic(paramString.charAt(j)))
      {
        i = 1;
        break;
      }
    }
    if (i == 0) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int k = 0; k < paramString.length(); k++)
    {
      char c = paramString.charAt(k);
      if (isMagic(c)) {
        localStringBuilder.append(charToEntityMap[c]);
      } else {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static String unquote(String paramString)
    throws XMLParseException
  {
    if ((!paramString.startsWith("\"")) || (!paramString.endsWith("\""))) {
      throw new XMLParseException("Value must be quoted: <" + paramString + ">");
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramString.length() - 1;
    for (int j = 1; j < i; j++)
    {
      char c = paramString.charAt(j);
      int k;
      Character localCharacter;
      if ((c == '&') && ((k = paramString.indexOf(';', j + 1)) >= 0) && ((localCharacter = (Character)entityToCharMap.get(paramString.substring(j, k + 1))) != null))
      {
        localStringBuilder.append(localCharacter);
        j = k;
      }
      else
      {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static String makeFieldValue(Object paramObject)
  {
    if (paramObject == null) {
      return "(null)";
    }
    Class localClass = paramObject.getClass();
    try
    {
      localClass.getConstructor(new Class[] { String.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      String str2 = "Class " + localClass + " does not have a public constructor with a single string arg";
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(str2);
      throw new RuntimeOperationsException(localIllegalArgumentException, "Cannot make XML descriptor");
    }
    catch (SecurityException localSecurityException) {}
    String str1 = quote(paramObject.toString());
    return "(" + localClass.getName() + "/" + str1 + ")";
  }
  
  private static Object parseQuotedFieldValue(String paramString)
    throws XMLParseException
  {
    paramString = unquote(paramString);
    if (paramString.equalsIgnoreCase("(null)")) {
      return null;
    }
    if ((!paramString.startsWith("(")) || (!paramString.endsWith(")"))) {
      return paramString;
    }
    int i = paramString.indexOf('/');
    if (i < 0) {
      return paramString.substring(1, paramString.length() - 1);
    }
    String str1 = paramString.substring(1, i);
    Constructor localConstructor;
    try
    {
      ReflectUtil.checkPackageAccess(str1);
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      Class localClass = Class.forName(str1, false, localClassLoader);
      localConstructor = localClass.getConstructor(new Class[] { String.class });
    }
    catch (Exception localException1)
    {
      throw new XMLParseException(localException1, "Cannot parse value: <" + paramString + ">");
    }
    String str2 = paramString.substring(i + 1, paramString.length() - 1);
    try
    {
      return localConstructor.newInstance(new Object[] { str2 });
    }
    catch (Exception localException2)
    {
      String str3 = "Cannot construct instance of " + str1 + " with arg: <" + paramString + ">";
      throw new XMLParseException(localException2, str3);
    }
  }
  
  public synchronized String toString()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Entry");
    }
    String str = "";
    String[] arrayOfString = getFields();
    if ((arrayOfString == null) || (arrayOfString.length == 0))
    {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Empty Descriptor");
      }
      return str;
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Printing " + arrayOfString.length + " fields");
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (i == arrayOfString.length - 1) {
        str = str.concat(arrayOfString[i]);
      } else {
        str = str.concat(arrayOfString[i] + ", ");
      }
    }
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Exit returning " + str);
    }
    return str;
  }
  
  private long toNumeric(String paramString)
  {
    try
    {
      return Long.parseLong(paramString);
    }
    catch (Exception localException) {}
    return -2L;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Map localMap = (Map)Util.cast(localGetField.get("descriptor", null));
    init(null);
    if (localMap != null) {
      descriptorMap.putAll(localMap);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    boolean bool = "1.0".equals(serialForm);
    if (bool) {
      localPutField.put("currClass", "DescriptorSupport");
    }
    Object localObject = descriptorMap;
    if (((SortedMap)localObject).containsKey("targetObject"))
    {
      localObject = new TreeMap(descriptorMap);
      ((SortedMap)localObject).remove("targetObject");
    }
    HashMap localHashMap;
    if ((bool) || ("1.2.0".equals(serialForm)) || ("1.2.1".equals(serialForm)))
    {
      localHashMap = new HashMap();
      Iterator localIterator = ((SortedMap)localObject).entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localHashMap.put(((String)localEntry.getKey()).toLowerCase(), localEntry.getValue());
      }
    }
    else
    {
      localHashMap = new HashMap((Map)localObject);
    }
    localPutField.put("descriptor", localHashMap);
    paramObjectOutputStream.writeFields();
  }
  
  static
  {
    String str1 = null;
    int j = 0;
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      str1 = (String)AccessController.doPrivileged(localGetPropertyAction);
      j = "1.0".equals(str1);
    }
    catch (Exception localException) {}
    serialForm = str1;
    if (j != 0)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 8071560848919417985L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6292969195866300415L;
    }
    entities = new String[] { " &#32;", "\"&quot;", "<&lt;", ">&gt;", "&&amp;", "\r&#13;", "\t&#9;", "\n&#10;", "\f&#12;" };
    entityToCharMap = new HashMap();
    int i = 0;
    char c;
    for (j = 0; j < entities.length; j++)
    {
      c = entities[j].charAt(0);
      if (c > i) {
        i = c;
      }
    }
    charToEntityMap = new String[i + 1];
    for (int k = 0; k < entities.length; k++)
    {
      c = entities[k].charAt(0);
      String str2 = entities[k].substring(1);
      charToEntityMap[c] = str2;
      entityToCharMap.put(str2, Character.valueOf(c));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\DescriptorSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */