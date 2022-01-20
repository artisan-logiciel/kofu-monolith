package kofu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kofu.TestDataSet.managedUserVM
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.bindToServer
import org.springframework.test.web.reactive.server.expectBody
//import io.github.jhipster.security.RandomUtil
//import khipster.KhipsterApp
//import khipster.config.ANONYMOUS_USER
//import khipster.config.SYSTEM_ACCOUNT
//import khipster.domain.User
//import khipster.repository.UserRepository
//import org.apache.commons.lang3.RandomStringUtils
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.data.domain.PageRequest
//import java.time.Instant
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.temporal.ChronoUnit
//import kotlin.test.assertNotNull

private const val DEFAULT_LOGIN = "johndoe"
private const val DEFAULT_EMAIL = "johndoe@localhost"
private const val DEFAULT_FIRSTNAME = "john"
private const val DEFAULT_LASTNAME = "doe"
private const val DEFAULT_IMAGEURL = "http://placehold.it/50x50"
private const val DEFAULT_LANGKEY = "dummy"

class ServicesTest {
    private val client: WebTestClient = bindToServer().baseUrl("http://localhost:8181").build()
    private lateinit var context: ConfigurableApplicationContext
    private val authRepo by lazy { context.getBean<AuthorityRepository>() }
    private val userRepo by lazy { context.getBean<UserRepository>() }
    private val userService by lazy { context.getBean<UserService>() }
    private lateinit var user: User


    @BeforeAll
    fun beforeAll() {
        context = webApp.run(profiles = "test")
    }

    @BeforeEach
    fun setup() {
//        userResourceMock = standaloneSetup(UserHandler::class.java).build()
//        accountResourceMock = standaloneSetup(AccountHandler::class.java).build()

//        userRepo.deleteAllUserAuthorities().block()
//        userRepository.deleteAll().block()
//        user = User(
//            login = DEFAULT_LOGIN,
//            password = RandomStringUtils.random(60),
//            activated = true,
//            email = DEFAULT_EMAIL,
//            firstName = DEFAULT_FIRSTNAME,
//            lastName = DEFAULT_LASTNAME,
//            imageUrl = DEFAULT_IMAGEURL,
//            createdBy = SYSTEM_ACCOUNT,
//            langKey = DEFAULT_LANGKEY
//        )
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }

}

//@SpringBootTest(classes = [KhipsterApp::class])
//class UserServiceIT {
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Autowired
//    private lateinit var userService: UserService
//
//    private lateinit var user: User
//
//    @BeforeEach
//    fun init() {
//        userRepository.deleteAllUserAuthorities().block()
//        userRepository.deleteAll().block()
//        user = User(
//            login = DEFAULT_LOGIN,
//            password = RandomStringUtils.random(60),
//            activated = true,
//            email = DEFAULT_EMAIL,
//            firstName = DEFAULT_FIRSTNAME,
//            lastName = DEFAULT_LASTNAME,
//            imageUrl = DEFAULT_IMAGEURL,
//            createdBy = SYSTEM_ACCOUNT,
//            langKey = DEFAULT_LANGKEY
//        )
//    }
//
//    @Test
//    fun assertThatUserMustExistToResetPassword() {
//        userRepository.save(user).block()
//        var maybeUser = userService.requestPasswordReset("invalid.login@localhost").blockOptional()
//        assertThat(maybeUser).isNotPresent
//
//        maybeUser = userService.requestPasswordReset(user.email!!).blockOptional()
//        assertThat(maybeUser).isPresent
//        assertThat(maybeUser.orElse(null).email).isEqualTo(user.email)
//        assertThat(maybeUser.orElse(null).resetDate).isNotNull()
//        assertThat(maybeUser.orElse(null).resetKey).isNotNull()
//    }
//
//    @Test
//    fun assertThatOnlyActivatedUserCanRequestPasswordReset() {
//        user.activated = false
//        userRepository.save(user).block()
//
//        val maybeUser = userService.requestPasswordReset(user.login!!).blockOptional()
//        assertThat(maybeUser).isNotPresent
//        userRepository.delete(user).block()
//    }
//
//    @Test
//    fun assertThatResetKeyMustNotBeOlderThan24Hours() {
//        val daysAgo = Instant.now().minus(25, ChronoUnit.HOURS)
//        val resetKey = RandomUtil.generateResetKey()
//        user.activated = true
//        user.resetDate = daysAgo
//        user.resetKey = resetKey
//        userRepository.save(user).block()
//
//        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!).blockOptional()
//        assertThat(maybeUser).isNotPresent
//        userRepository.delete(user).block()
//    }
//
//    @Test
//    fun assertThatResetKeyMustBeValid() {
//        val daysAgo = Instant.now().minus(25, ChronoUnit.HOURS)
//        user.activated = true
//        user.resetDate = daysAgo
//        user.resetKey = "1234"
//        userRepository.save(user).block()
//
//        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!).blockOptional()
//        assertThat(maybeUser).isNotPresent
//        userRepository.delete(user).block()
//    }
//
//    @Test
//    fun assertThatUserCanResetPassword() {
//        val oldPassword = user.password
//        val daysAgo = Instant.now().minus(2, ChronoUnit.HOURS)
//        val resetKey = RandomUtil.generateResetKey()
//        user.activated = true
//        user.resetDate = daysAgo
//        user.resetKey = resetKey
//        userRepository.save(user).block()
//
//        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!).blockOptional()
//        assertThat(maybeUser).isPresent
//        assertThat(maybeUser.orElse(null).resetDate).isNull()
//        assertThat(maybeUser.orElse(null).resetKey).isNull()
//        assertThat(maybeUser.orElse(null).password).isNotEqualTo(oldPassword)
//
//        userRepository.delete(user).block()
//    }
//
//    @Test
//    fun assertThatNotActivatedUsersWithNotNullActivationKeyCreatedBefore3DaysAreDeleted() {
//        val now = Instant.now()
//        user.activated = false
//        user.activationKey = RandomStringUtils.random(20)
//        val dbUser = userRepository.save(user).block()
//        assertNotNull(dbUser)
//        dbUser.createdDate = now.minus(4, ChronoUnit.DAYS)
//        userRepository.save(user).block()
//        val threeDaysAgo = LocalDateTime.ofInstant(now.minus(3, ChronoUnit.DAYS), ZoneOffset.UTC)
//        var users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
//            threeDaysAgo
//        ).collectList().block()
//        assertThat(users).isNotEmpty
//        userService.removeNotActivatedUsers()
//        users =
//            userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
//                threeDaysAgo
//            ).collectList().block()
//        assertThat(users).isEmpty()
//    }
//
//    @Test
//    fun assertThatNotActivatedUsersWithNullActivationKeyCreatedBefore3DaysAreNotDeleted() {
//        val now = Instant.now()
//        user.activated = false
//        val dbUser = userRepository.save(user).block()
//        dbUser.createdDate = now.minus(4, ChronoUnit.DAYS)
//        userRepository.save(user).block()
//        val threeDaysAgo = LocalDateTime.ofInstant(now.minus(3, ChronoUnit.DAYS), ZoneOffset.UTC)
//        val users =
//            userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
//                threeDaysAgo
//            ).collectList().block()
//        assertThat(users).isEmpty()
//        userService.removeNotActivatedUsers()
//        val maybeDbUser = userRepository.findById(dbUser.id).blockOptional()
//        assertThat(maybeDbUser).contains(dbUser)
//    }
//
//    @Test
//    fun assertThatAnonymousUserIsNotGet() {
//        user.login = ANONYMOUS_USER
//        if (!userRepository.findOneByLogin(ANONYMOUS_USER).blockOptional().isPresent) {
//            userRepository.save(user).block()
//        }
//        val pageable = PageRequest.of(0, userRepository.count().block()!!.toInt())
//        val allManagedUsers = userService.getAllManagedUsers(pageable)
//            .collectList().block()
//        assertNotNull(allManagedUsers)
//        assertThat(
//            allManagedUsers.stream()
//                .noneMatch { user -> ANONYMOUS_USER == user.login }
//        )
//            .isTrue()
//    }
//}
//


//import org.springframework.security.core.context.SecurityContext
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.security.test.context.support.WithSecurityContext
//import org.springframework.security.test.context.support.WithSecurityContextFactory
//import kotlin.annotation.Retention
//
//@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
//@Retention(AnnotationRetention.RUNTIME)
//@WithSecurityContext(factory = WithUnauthenticatedMockUser.Factory::class)
//annotation class WithUnauthenticatedMockUser {
//    class Factory : WithSecurityContextFactory<WithUnauthenticatedMockUser?> {
//        override fun createSecurityContext(annotation: WithUnauthenticatedMockUser?): SecurityContext {
//            return SecurityContextHolder.createEmptyContext()
//        }
//    }
//}
//
