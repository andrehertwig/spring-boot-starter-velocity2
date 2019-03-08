package de.chandre.velocity2.spring;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Spring-Boot auto-configuration for Velocity 2
 * @author André Hertwig
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(Velocity2Properties.class)
@ConditionalOnClass(VelocityEngine.class)
public class Velocity2AutoConfiguration {

	private static final Log LOGGER = LogFactory.getLog(Velocity2AutoConfiguration.class);
	
	public static final String VELOCITY_PROPERTIES_BEAN_NAME = "velocityProperties";
	public static final String VELOCITY_ENGINE_BEAN_NAME = "autoVelocityEngine";
	public static final String VELOCITY_TOOLBOX_BEAN_NAME = "autoVelocityToolbox";
	

	@Configuration
	@ConditionalOnProperty(prefix = Velocity2Properties.PREFIX, name = "enabled", havingValue="true", matchIfMissing = true)
	@ConditionalOnMissingBean(name = VELOCITY_ENGINE_BEAN_NAME)
	protected static class VelocityEngineConfiguration {
		
		
		public Properties velocityProperties(ApplicationContext applicationContext, Velocity2Properties properties)
				throws IOException {

			Properties velocityProperties = null;
			
			if (properties.isOverrideConfigLocationProperties()) {
				LOGGER.trace("merge Velocity-Properties with velocity.properties");
				//merge properties from file with springs application properties
				velocityProperties = loadConfigLocationProperties(applicationContext, properties);
				velocityProperties.putAll(properties.getProperties());
			} else if (null != properties.getProperties() && !properties.getProperties().isEmpty()) {
				LOGGER.trace("only unsing Velocity-Properties");
				// only use the spring application properties
				velocityProperties = new Properties();
				velocityProperties.putAll(properties.getProperties());
			}  else {
				LOGGER.trace("only unsing velocity.properties");
				// only use the properties from file
				velocityProperties = loadConfigLocationProperties(applicationContext, properties);
			}
			
			//Call the override hook to possibly change runtime data
			Velocity2PropertiesOverrideHook hook = getVelocityPropOverrideHook(applicationContext);
			if (null != hook) {
				velocityProperties = hook.override(velocityProperties);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Velocity-Properties");
				velocityProperties.entrySet().forEach(entry -> {
					LOGGER.debug("    " + entry.getKey() + " = " + entry.getValue());
				});
			}

			return velocityProperties;
		}
		
		private Properties loadConfigLocationProperties(ApplicationContext applicationContext, 
				Velocity2Properties properties) throws IOException {
			
			String location = properties.getPropertiesConfigLocation();
			if(null == location || location.trim().length() == 0) {
				location = Velocity2Properties.DEFAULT_CONFIG_LOCATION;
				LOGGER.debug("using default 'velocity.properties' from classpath: " + location);
			} else {
				LOGGER.debug("using 'velocity.properties' from location: " + location);
			}
			PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
			propertiesFactoryBean.setLocation(applicationContext.getResource(location));
			propertiesFactoryBean.afterPropertiesSet();
			return propertiesFactoryBean.getObject();
		}
		
		@Bean(name = VELOCITY_ENGINE_BEAN_NAME)
		@ConditionalOnMissingBean(name = VELOCITY_ENGINE_BEAN_NAME)
		public VelocityEngine autoVelocityEngine(ApplicationContext applicationContext, Velocity2Properties properties)
				throws IOException {
			
			LOGGER.debug("creating VelocityEngine");
			VelocityEngine ve = new VelocityEngine();
			ve.setProperties(velocityProperties(applicationContext, properties));
			ve.init();
			return ve;
		}
		
		private Velocity2PropertiesOverrideHook getVelocityPropOverrideHook(ApplicationContext applicationContext) {
			try {
				return applicationContext.getBean(Velocity2PropertiesOverrideHook.class);
			} catch (Exception e) {
				LOGGER.info("no Velocity2PropertiesOverrideHook configured");
				LOGGER.trace(e.getMessage(), e);
			}
			return null;
		}
	}
	
	@Configuration
	@ConditionalOnProperty(prefix = Velocity2Properties.PREFIX, name = "toolbox.enabled", havingValue="true", matchIfMissing = true)
	@ConditionalOnMissingBean(name = VELOCITY_TOOLBOX_BEAN_NAME)
	@ConditionalOnClass(name={"org.apache.velocity.tools.ToolManager"})
	protected static class VelocityToolsConfiguration {
		
		@Bean(name = VELOCITY_TOOLBOX_BEAN_NAME)
		@ConditionalOnMissingBean(name = VELOCITY_TOOLBOX_BEAN_NAME)
		@ConditionalOnBean(name= {VELOCITY_ENGINE_BEAN_NAME})
		public org.apache.velocity.tools.ToolManager autoVelocityToolbox(Velocity2Properties properties,
				@Qualifier(VELOCITY_ENGINE_BEAN_NAME) VelocityEngine velocityEngine) {
			
			if (!properties.getToolbox().isEnabled()) {
				return null;
			}
			
			org.apache.velocity.tools.ToolManager tm = new org.apache.velocity.tools.ToolManager(false, false);
			tm.setVelocityEngine(velocityEngine);
			if (StringUtils.isNoneBlank(properties.getToolbox().getConfigLocation())) {
				LOGGER.info("configuring velocity toolbox to location: " + properties.getToolbox().getConfigLocation());
				tm.configure(properties.getToolbox().getConfigLocation());
			} else {
				LOGGER.info("auto configuringing default velocity toolbox");
				tm.configure(ConfigurationUtils.getDefaultTools());
			}
			return tm;
		}
	}
	
}
