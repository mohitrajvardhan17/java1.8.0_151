package java.lang.annotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Retention
{
  RetentionPolicy value();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\annotation\Retention.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */