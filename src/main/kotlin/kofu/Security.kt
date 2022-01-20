package kofu


import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import kofu.Log.log
import org.apache.commons.lang3.RandomStringUtils.random
import org.springframework.beans.factory.InitializingBean
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets.UTF_8
import java.security.Key
import java.security.SecureRandom
import java.util.*

const val ROLE_ADMIN: String = "ADMIN"
const val ROLE_USER: String = "USER"
const val ROLE_ANONYMOUS: String = "ANONYMOUS"
const val LOGIN_REGEX: String =
    "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$"
const val SYSTEM_ACCOUNT: String = "system"
const val ANONYMOUS_USER: String = "anonymoususer"
const val DEFAULT_LANGUAGE: String = "en"
private const val AUTHORITIES_KEY = "auth"

class TokenProvider(
    private val properties: ApplicationProperties,
    private var key: Key? = null,
    private var tokenValidityInMilliseconds: Long = 0,
    private var tokenValidityInMillisecondsForRememberMe: Long = 0
) : InitializingBean {

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val secret = properties.security.authentication.jwt.secret!!
        key = Keys.hmacShaKeyFor(
            if (!hasLength(secret)) {
                log.warn(
                    "Warning: the JWT key used is not Base64-encoded. " +
                            "We recommend using the `kofu.security.authentication.jwt.base64-secret`" +
                            " key for optimum security."
                )
                secret.toByteArray(UTF_8)
            } else {
                log.debug("Using a Base64-encoded JWT secret key")
                Decoders.BASE64.decode(properties.security.authentication.jwt.base64Secret)
            }
        )
        tokenValidityInMilliseconds =
            1000 * properties.security.authentication.jwt.tokenValidityInSeconds
        tokenValidityInMillisecondsForRememberMe = 1000 * properties.security.authentication.jwt
            .tokenValidityInSecondsForRememberMe
    }

    fun createToken(authentication: Authentication, rememberMe: Boolean): String {
        val now = Date().time
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY,
                authentication.authorities
                    .asSequence()
                    .map { it.authority }
                    .joinToString(separator = ","))
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(
                if (rememberMe) {
                    Date(now + tokenValidityInMillisecondsForRememberMe)
                } else {
                    Date(now + tokenValidityInMilliseconds)
                }
            )
            .serializeToJsonWith(JacksonSerializer())
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        val authorities = claims[AUTHORITIES_KEY].toString().splitToSequence(",")
            .mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }
        return UsernamePasswordAuthenticationToken(
            User(claims.subject, "", authorities),
            token,
            authorities
        )
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
            return true
        } catch (e: JwtException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace. $e")
        } catch (e: IllegalArgumentException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace. $e")
        }
        return false
    }
}

class JWTFilter(private val tokenProvider: TokenProvider) : WebFilter {
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val jwt = resolveToken(exchange.request)
        if (!jwt.isNullOrBlank() && tokenProvider.validateToken(jwt)) {
            return chain.filter(exchange)
                .contextWrite(withAuthentication(tokenProvider.getAuthentication(jwt)))
        }
        return chain.filter(exchange)
    }

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst(AUTHORIZATION_HEADER)
        if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}

//@Component("userDetailsService")
class DomainUserDetailsService(private val userRepository: UserDemoRepository) : ReactiveUserDetailsService {
    //    @Transactional
    override fun findByUsername(login: String): Mono<UserDetails>? {
        log.debug("Authenticating $login")
//        if (EmailValidator().isValid(login, null)) {
//            return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(login)
//                .switchIfEmpty(Mono.error(UsernameNotFoundException("User with email $login was not found in the database")))
//                .map { createSpringSecurityUser(login, it) }
//        }
//        val lowercaseLogin = login.toLowerCase(Locale.ENGLISH)
//        return userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin)
//            .switchIfEmpty(Mono.error(UsernameNotFoundException("User $lowercaseLogin was not found in the database")))
//            .map { createSpringSecurityUser(lowercaseLogin, it) }
        return null
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createSpringSecurityUser(lowercaseLogin: String, user: User):
            org.springframework.security.core.userdetails.User? {
//        if (!user.activated) {
//            throw UserNotActivatedException("User $lowercaseLogin was not activated")
//        }
//        val grantedAuthorities = user.authorities.map { SimpleGrantedAuthority(it.name) }
//        return org.springframework.security.core.userdetails.User(
//            user.login!!,
//            user.password!!,
//            grantedAuthorities
//        )
        return null
    }
}

class UserNotActivatedException(message: String, t: Throwable? = null) : AuthenticationException(message, t) {
    companion object {
        private const val serialVersionUID = 1L
    }
}

fun getCurrentUserLogin(): Mono<String> =
    getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap { Mono.justOrEmpty(extractPrincipal(it)) }

fun extractPrincipal(authentication: Authentication?): String? =
    if (authentication == null) {
        null
    } else when (val principal = authentication.principal) {
        is UserDetails -> principal.username
        is String -> principal
        else -> null
    }

fun getCurrentUserJWT(): Mono<String> =
    getContext()
        .map(SecurityContext::getAuthentication)
        .filter { it.credentials is String }
        .map { it.credentials as String }

fun isAuthenticated(): Mono<Boolean> =
    getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map {
            it.map(GrantedAuthority::getAuthority)
                .none { it == ROLE_ANONYMOUS }
        }

fun isCurrentUserInRole(authority: String): Mono<Boolean> =
    getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map {
            it.map(GrantedAuthority::getAuthority)
                .any { it == authority }
        }


object RandomUtil {
    private const val DEF_COUNT = 20
    private val SECURE_RANDOM: SecureRandom by lazy {
        SecureRandom().apply {
            nextBytes(ByteArray(64))
        }
    }

    private fun generateRandomAlphanumericString() =
        random(
            DEF_COUNT,
            0,
            0,
            true,
            true,
            null,
            SECURE_RANDOM
        )

    fun generatePassword() = generateRandomAlphanumericString()

    fun generateActivationKey() = generateRandomAlphanumericString()

    fun generateResetKey() = generateRandomAlphanumericString()
}