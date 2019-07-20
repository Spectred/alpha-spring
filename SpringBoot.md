## Spring Boot 启动原理

### 1. @SpringBootApplication

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }

}
```

#### 1.1 @SpringBootApplication

```java
/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also triggers {@link EnableAutoConfiguration
 * auto-configuration} and {@link ComponentScan component scanning}. This is a convenience
 * annotation that is equivalent to declaring {@code @Configuration},
 * {@code @EnableAutoConfiguration} and {@code @ComponentScan}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	@AliasFor(annotation = EnableAutoConfiguration.class)
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	@AliasFor(annotation = EnableAutoConfiguration.class)
	String[] excludeName() default {};

	/**
	 * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
	 * for a type-safe alternative to String-based package names.
	 * @return base packages to scan
	 * @since 1.3.0
	 */
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] scanBasePackages() default {};

	/**
	 * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 * @return base packages to scan
	 * @since 1.3.0
	 */
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};

}

```



#### 1.2 @SpringBootConfiguration

本质上是@Configuration

```java
/**
 * Indicates that a class provides Spring Boot application
 * {@link Configuration @Configuration}. Can be used as an alternative to the Spring's
 * standard {@code @Configuration} annotation so that configuration can be found
 * automatically (for example in tests).
 * <p>
 * Application should only ever include <em>one</em> {@code @SpringBootConfiguration} and
 * most idiomatic Spring Boot applications will inherit it from
 * {@code @SpringBootApplication}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {

}
```

##### 1.2.1 @Configuration

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

   /**
    * Explicitly specify the name of the Spring bean definition associated with the
    * {@code @Configuration} class. If left unspecified (the common case), a bean
    * name will be automatically generated.
    * <p>The custom name applies only if the {@code @Configuration} class is picked
    * up via component scanning or supplied directly to an
    * {@link AnnotationConfigApplicationContext}. If the {@code @Configuration} class
    * is registered as a traditional XML bean definition, the name/id of the bean
    * element will take precedence.
    * @return the explicit component name, if any (or empty String otherwise)
    * @see AnnotationBeanNameGenerator
    */
   @AliasFor(annotation = Component.class)
   String value() default "";

}
```

```java
@Configuration
public class MockConfiguration{
    @Bean
    public MockService mockService(){
        return new MockServiceImpl(dependencyService());
    }
    
    @Bean
    public DependencyService dependencyService(){
        return new DependencyServiceImpl();
    }
}

// 与以下等效

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"
       default-lazy-init="true">
    <!--bean定义-->
    
    <bean id="mockService" class="..MockServiceImpl">
    		<propery name ="dependencyService" ref="dependencyService" />
		</bean>

		<bean id="dependencyService" class="DependencyServiceImpl"></bean>
</beans>
```





#### 1.3 @EnableAutoConfiguration

借助@Import的帮助，将所有符合自动配置条件的bean定义加载到IoC容器

EnableAutoConfiguration会根据类路径中的jar依赖为项目进行自动配置

```java
/**
 * Enable auto-configuration of the Spring Application Context, attempting to guess and
 * configure beans that you are likely to need. Auto-configuration classes are usually
 * applied based on your classpath and what beans you have defined. For example, if you
 * have {@code tomcat-embedded.jar} on your classpath you are likely to want a
 * {@link TomcatServletWebServerFactory} (unless you have defined your own
 * {@link ServletWebServerFactory} bean).
 * <p>
 * When using {@link SpringBootApplication}, the auto-configuration of the context is
 * automatically enabled and adding this annotation has therefore no additional effect.
 * <p>
 * Auto-configuration tries to be as intelligent as possible and will back-away as you
 * define more of your own configuration. You can always manually {@link #exclude()} any
 * configuration that you never want to apply (use {@link #excludeName()} if you don't
 * have access to them). You can also exclude them via the
 * {@code spring.autoconfigure.exclude} property. Auto-configuration is always applied
 * after user-defined beans have been registered.
 * <p>
 * The package of the class that is annotated with {@code @EnableAutoConfiguration},
 * usually via {@code @SpringBootApplication}, has specific significance and is often used
 * as a 'default'. For example, it will be used when scanning for {@code @Entity} classes.
 * It is generally recommended that you place {@code @EnableAutoConfiguration} (if you're
 * not using {@code @SpringBootApplication}) in a root package so that all sub-packages
 * and classes can be searched.
 * <p>
 * Auto-configuration classes are regular Spring {@link Configuration} beans. They are
 * located using the {@link SpringFactoriesLoader} mechanism (keyed against this class).
 * Generally auto-configuration beans are {@link Conditional @Conditional} beans (most
 * often using {@link ConditionalOnClass @ConditionalOnClass} and
 * {@link ConditionalOnMissingBean @ConditionalOnMissingBean} annotations).
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @see ConditionalOnBean
 * @see ConditionalOnMissingBean
 * @see ConditionalOnClass
 * @see AutoConfigureAfter
 * @see SpringBootApplication
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

   String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

   /**
    * Exclude specific auto-configuration classes such that they will never be applied.
    * @return the classes to exclude
    */
   Class<?>[] exclude() default {};

   /**
    * Exclude specific auto-configuration class names such that they will never be
    * applied.
    * @return the class names to exclude
    * @since 1.3.0
    */
   String[] excludeName() default {};

}
```

##### 1.3.1 @AutoConfigurationPackage

自动配置包

```java
/**
 * Indicates that the package containing the annotated class should be registered with
 * {@link AutoConfigurationPackages}.
 *
 * @author Phillip Webb
 * @since 1.3.0
 * @see AutoConfigurationPackages
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {

}
```

##### 1.3.2 @Import

```java
/**
 * Indicates one or more {@link Configuration @Configuration} classes to import.
 *
 * <p>Provides functionality equivalent to the {@code <import/>} element in Spring XML.
 * Allows for importing {@code @Configuration} classes, {@link ImportSelector} and
 * {@link ImportBeanDefinitionRegistrar} implementations, as well as regular component
 * classes (as of 4.2; analogous to {@link AnnotationConfigApplicationContext#register}).
 *
 * <p>{@code @Bean} definitions declared in imported {@code @Configuration} classes should be
 * accessed by using {@link org.springframework.beans.factory.annotation.Autowired @Autowired}
 * injection. Either the bean itself can be autowired, or the configuration class instance
 * declaring the bean can be autowired. The latter approach allows for explicit, IDE-friendly
 * navigation between {@code @Configuration} class methods.
 *
 * <p>May be declared at the class level or as a meta-annotation.
 *
 * <p>If XML or other non-{@code @Configuration} bean definition resources need to be
 * imported, use the {@link ImportResource @ImportResource} annotation instead.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

   /**
    * {@link Configuration}, {@link ImportSelector}, {@link ImportBeanDefinitionRegistrar}
    * or regular component classes to import.
    */
   Class<?>[] value();

}
```

#### 1.4 @ComponentScan

自动扫描并加载符合条件的组件

```java
/**
 * Configures component scanning directives for use with @{@link Configuration} classes.
 * Provides support parallel with Spring XML's {@code <context:component-scan>} element.
 *
 * <p>Either {@link #basePackageClasses} or {@link #basePackages} (or its alias
 * {@link #value}) may be specified to define specific packages to scan. If specific
 * packages are not defined, scanning will occur from the package of the
 * class that declares this annotation.
 *
 * <p>Note that the {@code <context:component-scan>} element has an
 * {@code annotation-config} attribute; however, this annotation does not. This is because
 * in almost all cases when using {@code @ComponentScan}, default annotation config
 * processing (e.g. processing {@code @Autowired} and friends) is assumed. Furthermore,
 * when using {@link AnnotationConfigApplicationContext}, annotation config processors are
 * always registered, meaning that any attempt to disable them at the
 * {@code @ComponentScan} level would be ignored.
 *
 * <p>See {@link Configuration @Configuration}'s Javadoc for usage examples.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 * @see Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

   /**
    * Alias for {@link #basePackages}.
    * <p>Allows for more concise annotation declarations if no other attributes
    * are needed &mdash; for example, {@code @ComponentScan("org.my.pkg")}
    * instead of {@code @ComponentScan(basePackages = "org.my.pkg")}.
    */
   @AliasFor("basePackages")
   String[] value() default {};

   /**
    * Base packages to scan for annotated components.
    * <p>{@link #value} is an alias for (and mutually exclusive with) this
    * attribute.
    * <p>Use {@link #basePackageClasses} for a type-safe alternative to
    * String-based package names.
    */
   @AliasFor("value")
   String[] basePackages() default {};

   /**
    * Type-safe alternative to {@link #basePackages} for specifying the packages
    * to scan for annotated components. The package of each class specified will be scanned.
    * <p>Consider creating a special no-op marker class or interface in each package
    * that serves no purpose other than being referenced by this attribute.
    */
   Class<?>[] basePackageClasses() default {};

   /**
    * The {@link BeanNameGenerator} class to be used for naming detected components
    * within the Spring container.
    * <p>The default value of the {@link BeanNameGenerator} interface itself indicates
    * that the scanner used to process this {@code @ComponentScan} annotation should
    * use its inherited bean name generator, e.g. the default
    * {@link AnnotationBeanNameGenerator} or any custom instance supplied to the
    * application context at bootstrap time.
    * @see AnnotationConfigApplicationContext#setBeanNameGenerator(BeanNameGenerator)
    */
   Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

   /**
    * The {@link ScopeMetadataResolver} to be used for resolving the scope of detected components.
    */
   Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

   /**
    * Indicates whether proxies should be generated for detected components, which may be
    * necessary when using scopes in a proxy-style fashion.
    * <p>The default is defer to the default behavior of the component scanner used to
    * execute the actual scan.
    * <p>Note that setting this attribute overrides any value set for {@link #scopeResolver}.
    * @see ClassPathBeanDefinitionScanner#setScopedProxyMode(ScopedProxyMode)
    */
   ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

   /**
    * Controls the class files eligible for component detection.
    * <p>Consider use of {@link #includeFilters} and {@link #excludeFilters}
    * for a more flexible approach.
    */
   String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

   /**
    * Indicates whether automatic detection of classes annotated with {@code @Component}
    * {@code @Repository}, {@code @Service}, or {@code @Controller} should be enabled.
    */
   boolean useDefaultFilters() default true;

   /**
    * Specifies which types are eligible for component scanning.
    * <p>Further narrows the set of candidate components from everything in {@link #basePackages}
    * to everything in the base packages that matches the given filter or filters.
    * <p>Note that these filters will be applied in addition to the default filters, if specified.
    * Any type under the specified base packages which matches a given filter will be included,
    * even if it does not match the default filters (i.e. is not annotated with {@code @Component}).
    * @see #resourcePattern()
    * @see #useDefaultFilters()
    */
   Filter[] includeFilters() default {};

   /**
    * Specifies which types are not eligible for component scanning.
    * @see #resourcePattern
    */
   Filter[] excludeFilters() default {};

   /**
    * Specify whether scanned beans should be registered for lazy initialization.
    * <p>Default is {@code false}; switch this to {@code true} when desired.
    * @since 4.1
    */
   boolean lazyInit() default false;


   /**
    * Declares the type filter to be used as an {@linkplain ComponentScan#includeFilters
    * include filter} or {@linkplain ComponentScan#excludeFilters exclude filter}.
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target({})
   @interface Filter {

      /**
       * The type of filter to use.
       * <p>Default is {@link FilterType#ANNOTATION}.
       * @see #classes
       * @see #pattern
       */
      FilterType type() default FilterType.ANNOTATION;

      /**
       * Alias for {@link #classes}.
       * @see #classes
       */
      @AliasFor("classes")
      Class<?>[] value() default {};

      /**
       * The class or classes to use as the filter.
       * <p>The following table explains how the classes will be interpreted
       * based on the configured value of the {@link #type} attribute.
       * <table border="1">
       * <tr><th>{@code FilterType}</th><th>Class Interpreted As</th></tr>
       * <tr><td>{@link FilterType#ANNOTATION ANNOTATION}</td>
       * <td>the annotation itself</td></tr>
       * <tr><td>{@link FilterType#ASSIGNABLE_TYPE ASSIGNABLE_TYPE}</td>
       * <td>the type that detected components should be assignable to</td></tr>
       * <tr><td>{@link FilterType#CUSTOM CUSTOM}</td>
       * <td>an implementation of {@link TypeFilter}</td></tr>
       * </table>
       * <p>When multiple classes are specified, <em>OR</em> logic is applied
       * &mdash; for example, "include types annotated with {@code @Foo} OR {@code @Bar}".
       * <p>Custom {@link TypeFilter TypeFilters} may optionally implement any of the
       * following {@link org.springframework.beans.factory.Aware Aware} interfaces, and
       * their respective methods will be called prior to {@link TypeFilter#match match}:
       * <ul>
       * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
       * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}
       * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
       * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}
       * </ul>
       * <p>Specifying zero classes is permitted but will have no effect on component
       * scanning.
       * @since 4.2
       * @see #value
       * @see #type
       */
      @AliasFor("value")
      Class<?>[] classes() default {};

      /**
       * The pattern (or patterns) to use for the filter, as an alternative
       * to specifying a Class {@link #value}.
       * <p>If {@link #type} is set to {@link FilterType#ASPECTJ ASPECTJ},
       * this is an AspectJ type pattern expression. If {@link #type} is
       * set to {@link FilterType#REGEX REGEX}, this is a regex pattern
       * for the fully-qualified class names to match.
       * @see #type
       * @see #classes
       */
      String[] pattern() default {};

   }

}
```

### 2. SpringApplication执行流程