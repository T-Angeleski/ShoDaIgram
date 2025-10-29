package com.shodaigram.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

const val BASE_PACKAGE = "com.shodaigram.backend"

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = [BASE_PACKAGE])
class JpaConfig
