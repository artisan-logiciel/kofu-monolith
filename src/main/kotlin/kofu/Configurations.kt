package kofu

import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.boot.logging.LogLevel.DEBUG
import org.springframework.boot.logging.LogLevel.WARN
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.messageSource
import org.springframework.fu.kofu.r2dbc.dataR2dbc
import org.springframework.fu.kofu.r2dbc.r2dbc
import org.springframework.fu.kofu.webflux.security
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User.withUsername
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager as AppAuthenticationManager

val logConfig = configuration {
    logging {
        level("org.springframework", DEBUG)
        level<DefaultListableBeanFactory>(WARN)
    }
}

val dataConfig = configuration {
    dataR2dbc {
        r2dbc {
            url = "r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1"
            transactional = true
        }
    }

    beans {
        bean {
            ConnectionFactoryInitializer().apply {
                setConnectionFactory(ref())
                setDatabasePopulator(
                    ResourceDatabasePopulator(
                        ClassPathResource("db/tables.sql")
                    )
                )
            }
        }
        bean("transactionManager") {
            R2dbcTransactionManager(
                ref<R2dbcEntityOperations>()
                    .databaseClient.connectionFactory
            )
        }
        bean<AuthorityRepository>("authorityRepository")
        bean<UserRepository>("userRepository")
        bean<UserDemoRepository>("userDemoRepository")
    }
}


val webConfig = configuration {

    messageSource {
        basename = "messages/messages"
    }

    beans {
        bean(::routes, "routes")
        bean<ConfHandler>("confHandler")
        bean<UserDemoHandler>("userDemoHandler")
        bean<UserHandler>("userHandler")
        bean<AccountHandler>("accountHandler")
        bean<MailService>("mailService")
    }

    webFlux {

        port = if (profiles.contains("test")) 8181 else 8080
        codecs {
            string()
            jackson()
        }
        security {
            org.springframework.context.support.beans {
                bean<UserService>("userService") { ref("passwordEncoder") }
                bean<TokenProvider>("tokenProvider")
                bean<JWTFilter>("jwtFilter")
            }
            userDetailsService = MapReactiveUserDetailsService(
                withUsername("john")
                    .password("12345")
                    .authorities(ROLE_USER)
                    .build(),
                withUsername("bill")
                    .password("12345")
                    .roles(ROLE_USER, ROLE_ADMIN)
                    .build()
            )
            passwordEncoder = BCryptPasswordEncoder()
            authenticationManager = AppAuthenticationManager(userDetailsService)
                .apply {
                    setPasswordEncoder(passwordEncoder)
                }
            http {
                authorizeExchange {
                    authorize("/api/conf", permitAll)//, hasAuthority(ROLE_ADMIN))
                    authorize("/api/user/**", permitAll)
                    authorize("/api/register", permitAll)
                    authorize("/api/activate", permitAll)
                    authorize("/api/authenticate", permitAll)
                    authorize("/api/account/reset-password/init", permitAll)
                    authorize("/api/account/reset-password/finish", permitAll)
                    authorize("/api/users/**", permitAll)//, hasAnyAuthority(ROLE_USER, ROLE_ADMIN))
                    authorize("/api/**", authenticated)
                    authorize("/management/health", permitAll)
                    authorize("/management/info", permitAll)
                    authorize("/management/prometheus", permitAll)
                    authorize("/management/**", hasAuthority(ROLE_ADMIN))
                }
                csrf { disable() }
//                val tokenProvider = ref<TokenProvider>("tokenProvider")
            }
        }
    }
}
