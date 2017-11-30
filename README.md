# Spring-Boot auto configuration for Velocity 2.0 Template Engine

> Just a Spring-Boot starter for Velocity 2.0 Template Engine.

This starter just appends the [Velocity Engine 2.0] (http://velocity.apache.org/engine/2.0/) to Spring Context and you can configure it via spring properties.
Because Velocity Tools is still version 2.0 and incompatible to Velocity 2.0 Template Engine there is no implmentation for tools.

[![Maven Central](https://img.shields.io/maven-central/v/de.chandre.quartz/spring-boot-starter-velocity2.svg)](https://mvnrepository.com/artifact/de.chandre.velocity2)
[![GitHub issues](https://img.shields.io/github/issues/andrehertwig/spring-boot-starter-velocity2.svg)](https://github.com/andrehertwig/spring-boot-starter-velocity2/issues)
[![license](https://img.shields.io/github/license/andrehertwig/spring-boot-starter-velocity2.svg)](https://github.com/andrehertwig/spring-boot-starter-velocity2/blob/develop/LICENSE)

This is just a spare-time project. The usage of this tool (especially in production systems) is at your own risk.

# Content

1. [Requirements, Dependencies](#requirements-dependencies)
2. [Usage](#usage)
3. [Configuration Properties](#configuration-properties)
4. [Additional Things](#additional-things)

## Requirements, Dependencies
* spring-boot
* apache velocity 2.0

Unit-Tested with Spring Boot 1.5.6

## Usage

```xml

<dependency>
	<groupId>de.chandre.velocity2</groupId>
	<artifactId>spring-boot-starter-velocity2</artifactId>
	<version>1.0.0</version>
</dependency>
	
```

Maybe you have to explicitly enable the component scan for the package:
```java

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"your.packages", "de.chandre.velocity2.spring"})
public class MyBootApplication {
 
}
```

## Configuration Properties

For special configuration, please check the [additional-spring-configuration-metadata.json](src/main/resources/META-INF/additional-spring-configuration-metadata.json) 

## Additional Things

If you want to add velocity properties at runtime while application start-up, you are able to do that by implementing the `de.chandre.velocity2.spring.Velocity2PropertiesOverrideHook`.

Example:

```java
@Configuration
@AutoConfigureBefore(Velocity2AutoConfiguration.class)
public class VelocityConfig
{
	private static final Logger LOGGER = LogManager.getFormatterLogger(VelocityConfig.class);
	
	@Bean
	public Velocity2PropertiesOverrideHook velocity2PropertiesOverrideHook() {
		return new Velocity2PropertiesOverrideHook() {

			@Override
			public Properties override(Properties quartzProperties) {
				quartzProperties.put("myKey", "myValue");
				return quartzProperties;
			}
		};
	}
}
```
