package org.sumo.klogs

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@Configuration
@PropertySources(
        PropertySource("classpath:shared_application.properties"),
        PropertySource("classpath:shared_\${spring.profiles.active}_application.properties", ignoreResourceNotFound = true))
class SharedConfiguration
