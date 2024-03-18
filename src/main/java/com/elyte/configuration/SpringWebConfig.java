package com.elyte.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.elyte.validators.EmailConstraint;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import java.util.List;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.domain.PageRequest;

/**
 * Spring MVC and Thymeleaf configuration.
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:mail/emailconfig.properties")
public class SpringWebConfig implements WebMvcConfigurer, ApplicationContextAware, EnvironmentAware {

    private static final String JAVA_MAIL_FILE = "classpath:mail/javamail.properties";

    private static final String HOST = "mail.server.host";
    private static final String PORT = "mail.server.port";
    private static final String PROTOCOL = "mail.server.protocol";
    private static final String USERNAME = "mail.server.username";
    private static final String PASSWORD = "mail.server.password";

    @Autowired
    private Environment env;

    private ApplicationContext applicationContext;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    private Environment environment;

    // AuthenticationEventPublisher for succesful and failed loggins
    @Bean
    AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(env.getProperty("client.url")).allowedMethods("*").allowCredentials(true).maxAge(24 * 60 * 60);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver phmar = new PageableHandlerMethodArgumentResolver();
        // Set the default size to 12
        phmar.setFallbackPageable(PageRequest.of(0, 12));
        argumentResolvers.add(phmar);

    }
    
    @Bean
    EmailConstraint emailValidator(){
        return new EmailConstraint();
    }


    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    /*
     * SPRING + JAVAMAIL: JavaMailSender instance, configured via .properties files.
     */

    @Bean
    JavaMailSender mailSender() throws IOException {

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Basic mail sender configuration, based on emailconfig.properties
        mailSender.setHost(this.environment.getProperty(HOST));
        mailSender.setPort(Integer.parseInt(this.environment.getProperty(PORT)));
        mailSender.setProtocol(this.environment.getProperty(PROTOCOL));
        mailSender.setUsername(this.environment.getProperty(USERNAME));
        mailSender.setPassword(this.environment.getProperty(PASSWORD));

        // JavaMail-specific mail sender configuration, based on javamail.properties
        final Properties javaMailProperties = new Properties();
        javaMailProperties.load(this.applicationContext.getResource(JAVA_MAIL_FILE).getInputStream());
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;

    }

    /* ******************************************************************* */
    /* GENERAL CONFIGURATION ARTIFACTS */
    /* Static Resources, i18n Messages, Formatters (Conversion Service) */
    /* ******************************************************************* */

    /*
     * Dispatcher configuration for serving static resources
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/images/**").addResourceLocations("/images/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");

    }

    /*
     * Multipart resolver (needed for uploading attachments from web form)
     */
    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /* **************************************************************** */
    /* THYMELEAF-SPECIFIC ARTIFACTS */
    /* TemplateResolver <- TemplateEngine <- ViewResolver */
    /* **************************************************************** */

    @Bean
    SpringResourceTemplateResolver templateResolver() {
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    // @Bean
    // ResourceBundleMessageSource emailMessageSource() {
    // final ResourceBundleMessageSource messageSource = new
    // ResourceBundleMessageSource();
    // messageSource.setBasename("mail/MailMessages");
    // return messageSource;
    // }

    @Bean
    SpringTemplateEngine templateEngine() {

        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        // Resolver for TEXT emails
        templateEngine.addTemplateResolver(textTemplateResolver());
        // Resolver for HTML emails (except the editable one)
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        // Resolver for HTML editable emails (which will be treated as a String)

        // Message source, internationalization specific to emails
        // templateEngine.setTemplateEngineMessageSource(messageSource());

        templateEngine.addTemplateResolver(templateResolver());
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);

        // For template inheritance
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    ThymeleafViewResolver viewResolver() {
        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    private ClassLoaderTemplateResolver textTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setResolvablePatterns(Collections.singleton("text/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ClassLoaderTemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

}