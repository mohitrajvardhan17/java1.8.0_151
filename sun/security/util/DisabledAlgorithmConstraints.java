package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisabledAlgorithmConstraints
  extends AbstractAlgorithmConstraints
{
  private static final Debug debug = Debug.getInstance("certpath");
  public static final String PROPERTY_CERTPATH_DISABLED_ALGS = "jdk.certpath.disabledAlgorithms";
  public static final String PROPERTY_TLS_DISABLED_ALGS = "jdk.tls.disabledAlgorithms";
  public static final String PROPERTY_JAR_DISABLED_ALGS = "jdk.jar.disabledAlgorithms";
  private final String[] disabledAlgorithms;
  private final Constraints algorithmConstraints;
  
  public DisabledAlgorithmConstraints(String paramString)
  {
    this(paramString, new AlgorithmDecomposer());
  }
  
  public DisabledAlgorithmConstraints(String paramString, AlgorithmDecomposer paramAlgorithmDecomposer)
  {
    super(paramAlgorithmDecomposer);
    disabledAlgorithms = getAlgorithms(paramString);
    algorithmConstraints = new Constraints(disabledAlgorithms);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters)
  {
    return checkAlgorithm(disabledAlgorithms, paramString, decomposer);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey)
  {
    return checkConstraints(paramSet, "", paramKey, null);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("No algorithm name specified");
    }
    return checkConstraints(paramSet, paramString, paramKey, paramAlgorithmParameters);
  }
  
  public final void permits(ConstraintsParameters paramConstraintsParameters)
    throws CertPathValidatorException
  {
    permits(paramConstraintsParameters.getAlgorithm(), paramConstraintsParameters);
  }
  
  public final void permits(String paramString1, Key paramKey, AlgorithmParameters paramAlgorithmParameters, String paramString2)
    throws CertPathValidatorException
  {
    permits(paramString1, new ConstraintsParameters(paramString1, paramAlgorithmParameters, paramKey, paramString2 == null ? "generic" : paramString2));
  }
  
  public final void permits(String paramString, ConstraintsParameters paramConstraintsParameters)
    throws CertPathValidatorException
  {
    algorithmConstraints.permits(paramString, paramConstraintsParameters);
  }
  
  public boolean checkProperty(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    for (String str : disabledAlgorithms) {
      if (str.toLowerCase(Locale.ENGLISH).indexOf(paramString) >= 0) {
        return true;
      }
    }
    return false;
  }
  
  private boolean checkConstraints(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters)
  {
    if (paramKey == null) {
      throw new IllegalArgumentException("The key cannot be null");
    }
    if ((paramString != null) && (paramString.length() != 0) && (!permits(paramSet, paramString, paramAlgorithmParameters))) {
      return false;
    }
    if (!permits(paramSet, paramKey.getAlgorithm(), null)) {
      return false;
    }
    return algorithmConstraints.permits(paramKey);
  }
  
  private static abstract class Constraint
  {
    String algorithm;
    Constraint nextConstraint = null;
    
    private Constraint() {}
    
    public boolean permits(Key paramKey)
    {
      return true;
    }
    
    public abstract void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException;
    
    boolean next(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      if (nextConstraint != null)
      {
        nextConstraint.permits(paramConstraintsParameters);
        return true;
      }
      return false;
    }
    
    boolean next(Key paramKey)
    {
      return (nextConstraint != null) && (nextConstraint.permits(paramKey));
    }
    
    String extendedMsg(ConstraintsParameters paramConstraintsParameters)
    {
      return " used with certificate: " + paramConstraintsParameters.getCertificate().getSubjectX500Principal() + (paramConstraintsParameters.getVariant() != "generic" ? ".  Usage was " + paramConstraintsParameters.getVariant() : ".");
    }
    
    static enum Operator
    {
      EQ,  NE,  LT,  LE,  GT,  GE;
      
      private Operator() {}
      
      static Operator of(String paramString)
      {
        switch (paramString)
        {
        case "==": 
          return EQ;
        case "!=": 
          return NE;
        case "<": 
          return LT;
        case "<=": 
          return LE;
        case ">": 
          return GT;
        case ">=": 
          return GE;
        }
        throw new IllegalArgumentException("Error in security property. " + paramString + " is not a legal Operator");
      }
    }
  }
  
  private static class Constraints
  {
    private Map<String, List<DisabledAlgorithmConstraints.Constraint>> constraintsMap = new HashMap();
    
    public Constraints(String[] paramArrayOfString)
    {
      for (String str1 : paramArrayOfString) {
        if ((str1 != null) && (!str1.isEmpty()))
        {
          str1 = str1.trim();
          if (DisabledAlgorithmConstraints.debug != null) {
            DisabledAlgorithmConstraints.debug.println("Constraints: " + str1);
          }
          int k = str1.indexOf(' ');
          String str2 = AlgorithmDecomposer.hashName((k > 0 ? str1.substring(0, k) : str1).toUpperCase(Locale.ENGLISH));
          List localList = (List)constraintsMap.getOrDefault(str2, new ArrayList(1));
          constraintsMap.putIfAbsent(str2, localList);
          if (k <= 0)
          {
            localList.add(new DisabledAlgorithmConstraints.DisabledConstraint(str2));
          }
          else
          {
            String str3 = str1.substring(k + 1);
            Object localObject2 = null;
            int m = 0;
            int n = 0;
            for (String str4 : str3.split("&"))
            {
              str4 = str4.trim();
              Object localObject1;
              if (str4.startsWith("keySize"))
              {
                if (DisabledAlgorithmConstraints.debug != null) {
                  DisabledAlgorithmConstraints.debug.println("Constraints set to keySize: " + str4);
                }
                StringTokenizer localStringTokenizer = new StringTokenizer(str4);
                if (!"keySize".equals(localStringTokenizer.nextToken())) {
                  throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str4);
                }
                localObject1 = new DisabledAlgorithmConstraints.KeySizeConstraint(str2, DisabledAlgorithmConstraints.Constraint.Operator.of(localStringTokenizer.nextToken()), Integer.parseInt(localStringTokenizer.nextToken()));
              }
              else if (str4.equalsIgnoreCase("jdkCA"))
              {
                if (DisabledAlgorithmConstraints.debug != null) {
                  DisabledAlgorithmConstraints.debug.println("Constraints set to jdkCA.");
                }
                if (m != 0) {
                  throw new IllegalArgumentException("Only one jdkCA entry allowed in property. Constraint: " + str1);
                }
                localObject1 = new DisabledAlgorithmConstraints.jdkCAConstraint(str2);
                m = 1;
              }
              else
              {
                Matcher localMatcher;
                if ((str4.startsWith("denyAfter")) && ((localMatcher = Holder.DENY_AFTER_PATTERN.matcher(str4)).matches()))
                {
                  if (DisabledAlgorithmConstraints.debug != null) {
                    DisabledAlgorithmConstraints.debug.println("Constraints set to denyAfter");
                  }
                  if (n != 0) {
                    throw new IllegalArgumentException("Only one denyAfter entry allowed in property. Constraint: " + str1);
                  }
                  int i3 = Integer.parseInt(localMatcher.group(1));
                  int i4 = Integer.parseInt(localMatcher.group(2));
                  int i5 = Integer.parseInt(localMatcher.group(3));
                  localObject1 = new DisabledAlgorithmConstraints.DenyAfterConstraint(str2, i3, i4, i5);
                  n = 1;
                }
                else if (str4.startsWith("usage"))
                {
                  String[] arrayOfString3 = str4.substring(5).trim().split(" ");
                  localObject1 = new DisabledAlgorithmConstraints.UsageConstraint(str2, arrayOfString3);
                  if (DisabledAlgorithmConstraints.debug != null) {
                    DisabledAlgorithmConstraints.debug.println("Constraints usage length is " + arrayOfString3.length);
                  }
                }
                else
                {
                  throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str4);
                }
              }
              if (localObject2 == null) {
                localList.add(localObject1);
              } else {
                nextConstraint = ((DisabledAlgorithmConstraints.Constraint)localObject1);
              }
              localObject2 = localObject1;
            }
          }
        }
      }
    }
    
    private List<DisabledAlgorithmConstraints.Constraint> getConstraints(String paramString)
    {
      return (List)constraintsMap.get(paramString);
    }
    
    public boolean permits(Key paramKey)
    {
      List localList = getConstraints(paramKey.getAlgorithm());
      if (localList == null) {
        return true;
      }
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        DisabledAlgorithmConstraints.Constraint localConstraint = (DisabledAlgorithmConstraints.Constraint)localIterator.next();
        if (!localConstraint.permits(paramKey))
        {
          if (DisabledAlgorithmConstraints.debug != null) {
            DisabledAlgorithmConstraints.debug.println("keySizeConstraint: failed key constraint check " + KeyUtil.getKeySize(paramKey));
          }
          return false;
        }
      }
      return true;
    }
    
    public void permits(String paramString, ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      X509Certificate localX509Certificate = paramConstraintsParameters.getCertificate();
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("Constraints.permits(): " + paramString + " Variant: " + paramConstraintsParameters.getVariant());
      }
      HashSet localHashSet = new HashSet();
      if (paramString != null) {
        localHashSet.addAll(AlgorithmDecomposer.decomposeOneHash(paramString));
      }
      if (localX509Certificate != null) {
        localHashSet.add(localX509Certificate.getPublicKey().getAlgorithm());
      }
      if (paramConstraintsParameters.getPublicKey() != null) {
        localHashSet.add(paramConstraintsParameters.getPublicKey().getAlgorithm());
      }
      Iterator localIterator1 = localHashSet.iterator();
      while (localIterator1.hasNext())
      {
        String str = (String)localIterator1.next();
        List localList = getConstraints(str);
        if (localList != null)
        {
          Iterator localIterator2 = localList.iterator();
          while (localIterator2.hasNext())
          {
            DisabledAlgorithmConstraints.Constraint localConstraint = (DisabledAlgorithmConstraints.Constraint)localIterator2.next();
            localConstraint.permits(paramConstraintsParameters);
          }
        }
      }
    }
    
    private static class Holder
    {
      private static final Pattern DENY_AFTER_PATTERN = Pattern.compile("denyAfter\\s+(\\d{4})-(\\d{2})-(\\d{2})");
      
      private Holder() {}
    }
  }
  
  private static class DenyAfterConstraint
    extends DisabledAlgorithmConstraints.Constraint
  {
    private Date denyAfterDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy");
    
    DenyAfterConstraint(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      algorithm = paramString;
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint read in as:  year " + paramInt1 + ", month = " + paramInt2 + ", day = " + paramInt3);
      }
      Calendar localCalendar = new Calendar.Builder().setTimeZone(TimeZone.getTimeZone("GMT")).setDate(paramInt1, paramInt2 - 1, paramInt3).build();
      if ((paramInt1 > localCalendar.getActualMaximum(1)) || (paramInt1 < localCalendar.getActualMinimum(1))) {
        throw new IllegalArgumentException("Invalid year given in constraint: " + paramInt1);
      }
      if ((paramInt2 - 1 > localCalendar.getActualMaximum(2)) || (paramInt2 - 1 < localCalendar.getActualMinimum(2))) {
        throw new IllegalArgumentException("Invalid month given in constraint: " + paramInt2);
      }
      if ((paramInt3 > localCalendar.getActualMaximum(5)) || (paramInt3 < localCalendar.getActualMinimum(5))) {
        throw new IllegalArgumentException("Invalid Day of Month given in constraint: " + paramInt3);
      }
      denyAfterDate = localCalendar.getTime();
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint date set to: " + dateFormat.format(denyAfterDate));
      }
    }
    
    public void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      Date localDate;
      String str;
      if (paramConstraintsParameters.getJARTimestamp() != null)
      {
        localDate = paramConstraintsParameters.getJARTimestamp().getTimestamp();
        str = "JAR Timestamp date: ";
      }
      else if (paramConstraintsParameters.getPKIXParamDate() != null)
      {
        localDate = paramConstraintsParameters.getPKIXParamDate();
        str = "PKIXParameter date: ";
      }
      else
      {
        localDate = new Date();
        str = "Current date: ";
      }
      if (!denyAfterDate.after(localDate))
      {
        if (next(paramConstraintsParameters)) {
          return;
        }
        throw new CertPathValidatorException("denyAfter constraint check failed: " + algorithm + " used with Constraint date: " + dateFormat.format(denyAfterDate) + "; " + str + dateFormat.format(localDate) + extendedMsg(paramConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      }
    }
    
    public boolean permits(Key paramKey)
    {
      if (next(paramKey)) {
        return true;
      }
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("DenyAfterConstraints.permits(): " + algorithm);
      }
      return denyAfterDate.after(new Date());
    }
  }
  
  private static class DisabledConstraint
    extends DisabledAlgorithmConstraints.Constraint
  {
    DisabledConstraint(String paramString)
    {
      super();
      algorithm = paramString;
    }
    
    public void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      throw new CertPathValidatorException("Algorithm constraints check failed on disabled algorithm: " + algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
    }
    
    public boolean permits(Key paramKey)
    {
      return false;
    }
  }
  
  private static class KeySizeConstraint
    extends DisabledAlgorithmConstraints.Constraint
  {
    private int minSize;
    private int maxSize;
    private int prohibitedSize = -1;
    
    public KeySizeConstraint(String paramString, DisabledAlgorithmConstraints.Constraint.Operator paramOperator, int paramInt)
    {
      super();
      algorithm = paramString;
      switch (DisabledAlgorithmConstraints.1.$SwitchMap$sun$security$util$DisabledAlgorithmConstraints$Constraint$Operator[paramOperator.ordinal()])
      {
      case 1: 
        minSize = 0;
        maxSize = Integer.MAX_VALUE;
        prohibitedSize = paramInt;
        break;
      case 2: 
        minSize = paramInt;
        maxSize = paramInt;
        break;
      case 3: 
        minSize = paramInt;
        maxSize = Integer.MAX_VALUE;
        break;
      case 4: 
        minSize = (paramInt + 1);
        maxSize = Integer.MAX_VALUE;
        break;
      case 5: 
        minSize = 0;
        maxSize = paramInt;
        break;
      case 6: 
        minSize = 0;
        maxSize = (paramInt > 1 ? paramInt - 1 : 0);
        break;
      default: 
        minSize = Integer.MAX_VALUE;
        maxSize = -1;
      }
    }
    
    public void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      Object localObject = null;
      if (paramConstraintsParameters.getPublicKey() != null) {
        localObject = paramConstraintsParameters.getPublicKey();
      } else if (paramConstraintsParameters.getCertificate() != null) {
        localObject = paramConstraintsParameters.getCertificate().getPublicKey();
      }
      if ((localObject != null) && (!permitsImpl((Key)localObject)))
      {
        if (nextConstraint != null)
        {
          nextConstraint.permits(paramConstraintsParameters);
          return;
        }
        throw new CertPathValidatorException("Algorithm constraints check failed on keysize limits. " + algorithm + " " + KeyUtil.getKeySize((Key)localObject) + "bit key" + extendedMsg(paramConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      }
    }
    
    public boolean permits(Key paramKey)
    {
      if ((nextConstraint != null) && (nextConstraint.permits(paramKey))) {
        return true;
      }
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("KeySizeConstraints.permits(): " + algorithm);
      }
      return permitsImpl(paramKey);
    }
    
    private boolean permitsImpl(Key paramKey)
    {
      if (algorithm.compareToIgnoreCase(paramKey.getAlgorithm()) != 0) {
        return true;
      }
      int i = KeyUtil.getKeySize(paramKey);
      if (i == 0) {
        return false;
      }
      if (i > 0) {
        return (i >= minSize) && (i <= maxSize) && (prohibitedSize != i);
      }
      return true;
    }
  }
  
  private static class UsageConstraint
    extends DisabledAlgorithmConstraints.Constraint
  {
    String[] usages;
    
    UsageConstraint(String paramString, String[] paramArrayOfString)
    {
      super();
      algorithm = paramString;
      usages = paramArrayOfString;
    }
    
    public void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      for (String str1 : usages)
      {
        String str2 = null;
        if (str1.compareToIgnoreCase("TLSServer") == 0) {
          str2 = "tls server";
        } else if (str1.compareToIgnoreCase("TLSClient") == 0) {
          str2 = "tls client";
        } else if (str1.compareToIgnoreCase("SignedJAR") == 0) {
          str2 = "plugin code signing";
        }
        if (DisabledAlgorithmConstraints.debug != null)
        {
          DisabledAlgorithmConstraints.debug.println("Checking if usage constraint \"" + str2 + "\" matches \"" + paramConstraintsParameters.getVariant() + "\"");
          ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
          PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
          new Exception().printStackTrace(localPrintStream);
          DisabledAlgorithmConstraints.debug.println(localByteArrayOutputStream.toString());
        }
        if (paramConstraintsParameters.getVariant().compareTo(str2) == 0)
        {
          if (next(paramConstraintsParameters)) {
            return;
          }
          throw new CertPathValidatorException("Usage constraint " + str1 + " check failed: " + algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
      }
    }
  }
  
  private static class jdkCAConstraint
    extends DisabledAlgorithmConstraints.Constraint
  {
    jdkCAConstraint(String paramString)
    {
      super();
      algorithm = paramString;
    }
    
    public void permits(ConstraintsParameters paramConstraintsParameters)
      throws CertPathValidatorException
    {
      if (DisabledAlgorithmConstraints.debug != null) {
        DisabledAlgorithmConstraints.debug.println("jdkCAConstraints.permits(): " + algorithm);
      }
      if (paramConstraintsParameters.isTrustedMatch())
      {
        if (next(paramConstraintsParameters)) {
          return;
        }
        throw new CertPathValidatorException("Algorithm constraints check failed on certificate anchor limits. " + algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DisabledAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */