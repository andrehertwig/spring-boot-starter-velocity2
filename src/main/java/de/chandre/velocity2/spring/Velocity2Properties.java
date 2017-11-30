package de.chandre.velocity2.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Velocity2Properties.PREFIX, ignoreUnknownFields = true)
public class Velocity2Properties {
	
	public static final String PREFIX = "velocity";
	
	public static final String DEFAULT_CONFIG_LOCATION = "classpath:/org/apache/velocity/runtime/defaults/velocity.properties";
	
	public static final String DEFAULT_SUFFIX = ".vm";
	
	/**
	 *  if auto-config is enabled
	 */
	private boolean enabled = true;
	
	private String propertiesConfigLocation;
	private Map<String, String> properties = new HashMap<String, String>();
	private boolean overrideConfigLocationProperties = true;
	
	private Toolbox toolbox = new Toolbox();
	
	/**
	 * if auto configuration is enabled
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * if auto configuration is enabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getPropertiesConfigLocation() {
		return propertiesConfigLocation;
	}

	public void setPropertiesConfigLocation(String propertiesConfigLocation) {
		this.propertiesConfigLocation = propertiesConfigLocation;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public boolean isOverrideConfigLocationProperties() {
		return overrideConfigLocationProperties;
	}

	public void setOverrideConfigLocationProperties(boolean overrideConfigLocationProperties) {
		this.overrideConfigLocationProperties = overrideConfigLocationProperties;
	}
	
	public Toolbox getToolbox() {
		return toolbox;
	}

	public void setToolbox(Toolbox toolbox) {
		this.toolbox = toolbox;
	}

	public static class Toolbox {
		
		private boolean enabled = true;
		
		private String configLocation;
		
		/**
		 * if auto configuration is enabled
		 * @return
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * if auto configuration is enabled
		 * @param enabled
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getConfigLocation() {
			return configLocation;
		}

		public void setConfigLocation(String configLocation) {
			this.configLocation = configLocation;
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Velocity2AutoConfiguration [properties=").append(properties).append("]");
		return builder.toString();
	}
}