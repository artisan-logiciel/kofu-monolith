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

class RegistrationUserStory {
    private val client: WebTestClient = bindToServer()
        .baseUrl("http://localhost:8181").build()
    private lateinit var context: ConfigurableApplicationContext
    //chercher les mock reactif plutot que servlet
//    private lateinit var userResourceMock:
    //    org.springframework.test.web.servlet.MockMvc
//    private lateinit var accountResourceMock:
    //    org.springframework.test.web.servlet.MockMvc

    @BeforeAll
    fun beforeAll() {
        context = webApp.run(profiles = "test")
    }

    @BeforeEach
    fun setup() {
//        userResourceMock = standaloneSetup(UserHandler::class.java).build()
//        accountResourceMock = standaloneSetup(AccountHandler::class.java).build()
    }

    private val mapper by lazy { context.getBean<ObjectMapper>() }
    private val jsonManagedUserVM: String by lazy { mapper.writeValueAsString(managedUserVM) }

    @Test
    fun `tranform ManagedUserVM to json`() {
        jsonManagedUserVM.run {
            assert(
                contains("{")
                        && contains("}")
                        && contains(",")
                        && contains(":")
                        && contains(managedUserVM.password)
                        && contains(managedUserVM.login!!)
                        && contains(managedUserVM.email!!)
            )

        }
    }

    @Test
    fun `tranform json to ManagedUserVM`() {
        jsonManagedUserVM.run {
            mapper.readValue<ManagedUserVM>(this).run {
                assert(
                    password.equals(managedUserVM.password)
                            && login.equals(managedUserVM.password)
                            && email.equals(managedUserVM.email)
                )
            }
        }
    }

    @Test
    fun `When POST register with ManagedUserVM on body request then response is created and map model`() {
        client
            .post().uri("/api/register")
            .bodyValue(managedUserVM).exchange()
            .expectStatus().isCreated
            .expectBody<ManagedUserVM>()
            .returnResult().run {
                assert(responseBody?.login.equals(managedUserVM.login)
                        && responseBody?.email.equals(managedUserVM.email)
                        && responseBody?.password.equals(managedUserVM.password))
            }
    }


    @AfterAll
    fun afterAll() {
        context.close()
    }

}



//package khipster.web.rest
//
//import khipster.KhipsterApp
//import khipster.config.DEFAULT_LANGUAGE
//import khipster.config.SYSTEM_ACCOUNT
//import khipster.domain.User
//import khipster.repository.AuthorityRepository
//import khipster.repository.UserRepository
//import khipster.security.ADMIN
//import khipster.security.USER
//import khipster.service.UserService
//import khipster.service.dto.PasswordChangeDTO
//import khipster.service.dto.UserDTO
//import khipster.web.rest.vm.KeyAndPasswordVM
//import khipster.web.rest.vm.ManagedUserVM
//import org.apache.commons.lang3.RandomStringUtils
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.test.context.support.WithMockUser
//import org.springframework.test.web.reactive.server.WebTestClient
//import org.springframework.test.web.reactive.server.expectBody
//import java.time.Instant
//import kotlin.test.assertNotNull
//
///**
// * Integrations tests for the [AccountResource] REST controller.
// */
//@AutoConfigureWebTestClient
//@WithMockUser(value = TEST_USER_LOGIN)
//@SpringBootTest(classes = [KhipsterApp::class])
//class AccountResourceIT {
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Autowired
//    private lateinit var authorityRepository: AuthorityRepository
//
//    @Autowired
//    private lateinit var userService: UserService
//
//    @Autowired
//    private lateinit var passwordEncoder: PasswordEncoder
//
//    @Autowired
//    private lateinit var accountWebTestClient: WebTestClient
//
//    @Test
//    @WithUnauthenticatedMockUser
//    fun testNonAuthenticatedUser() {
//        accountWebTestClient.get().uri("/api/authenticate")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody().isEmpty
//    }
//
//    @Test
//    fun testAuthenticatedUser() {
//        accountWebTestClient
//            .get().uri("/api/authenticate")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody<String>().isEqualTo(TEST_USER_LOGIN)
//    }
//
//    @Test
//    fun testGetExistingAccount() {
//
//        val authorities = mutableSetOf(ADMIN)
//
//        val user = UserDTO(
//            login = TEST_USER_LOGIN,
//            firstName = "john",
//            lastName = "doe",
//            email = "john.doe@jhipster.com",
//            imageUrl = "http://placehold.it/50x50",
//            langKey = "en",
//            authorities = authorities
//        )
//        userService.createUser(user).block()
//
//        accountWebTestClient.get().uri("/api/account")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
//            .expectBody()
//            .jsonPath("\$.login").isEqualTo(TEST_USER_LOGIN)
//            .jsonPath("\$.firstName").isEqualTo("john")
//            .jsonPath("\$.lastName").isEqualTo("doe")
//            .jsonPath("\$.email").isEqualTo("john.doe@jhipster.com")
//            .jsonPath("\$.imageUrl").isEqualTo("http://placehold.it/50x50")
//            .jsonPath("\$.langKey").isEqualTo("en")
//            .jsonPath("\$.authorities").isEqualTo(ADMIN)
//    }
//
//    @Test
//    fun testGetUnknownAccount() {
//        accountWebTestClient.get().uri("/api/account")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterValid() {
//        val validUser = ManagedUserVM().apply {
//            login = "test-register-valid"
//            password = "password"
//            firstName = "Alice"
//            lastName = "Test"
//            email = "test-register-valid@example.com"
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//        assertThat(userRepository.findOneByLogin("test-register-valid").blockOptional().isPresent).isFalse()
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(validUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        assertThat(userRepository.findOneByLogin("test-register-valid").blockOptional().isPresent).isTrue()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterInvalidLogin() {
//        val invalidUser = ManagedUserVM().apply {
//            login = "funky-log(n" // <-- invalid
//            password = "password"
//            firstName = "Funky"
//            lastName = "One"
//            email = "funky@example.com"
//            activated = true
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(invalidUser))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val user = userRepository.findOneByEmailIgnoreCase("funky@example.com").blockOptional()
//        assertThat(user.isPresent).isFalse()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterInvalidEmail() {
//        val invalidUser = ManagedUserVM().apply {
//            login = "bob"
//            password = "password"
//            firstName = "Bob"
//            lastName = "Green"
//            email = "invalid" // <-- invalid
//            activated = true
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(invalidUser))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val user = userRepository.findOneByLogin("bob").blockOptional()
//        assertThat(user.isPresent).isFalse()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterInvalidPassword() {
//        val invalidUser = ManagedUserVM().apply {
//            login = "bob"
//            password = "123" // password with only 3 digits
//            firstName = "Bob"
//            lastName = "Green"
//            email = "bob@example.com"
//            activated = true
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(invalidUser))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val user = userRepository.findOneByLogin("bob").blockOptional()
//        assertThat(user.isPresent).isFalse()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterNullPassword() {
//        val invalidUser = ManagedUserVM().apply {
//            login = "bob"
//            password = null // invalid null password
//            firstName = "Bob"
//            lastName = "Green"
//            email = "bob@example.com"
//            activated = true
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(invalidUser))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val user = userRepository.findOneByLogin("bob").blockOptional()
//        assertThat(user.isPresent).isFalse()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterDuplicateLogin() {
//        // First registration
//        val firstUser = ManagedUserVM().apply {
//            login = "alice"
//            password = "password"
//            firstName = "Alice"
//            lastName = "Something"
//            email = "alice@example.com"
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        // Duplicate login, different email
//        val secondUser = ManagedUserVM().apply {
//            login = firstUser.login
//            password = firstUser.password
//            firstName = firstUser.firstName
//            lastName = firstUser.lastName
//            email = "alice2@example.com"
//            imageUrl = firstUser.imageUrl
//            langKey = firstUser.langKey
//            createdBy = firstUser.createdBy
//            createdDate = firstUser.createdDate
//            lastModifiedBy = firstUser.lastModifiedBy
//            lastModifiedDate = firstUser.lastModifiedDate
//            authorities = firstUser.authorities?.toMutableSet()
//        }
//
//        // First user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(firstUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        // Second (non activated) user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        val testUser = userRepository.findOneByEmailIgnoreCase("alice2@example.com").blockOptional()
//        assertThat(testUser.isPresent).isTrue()
//        testUser.get().activated = true
//        userRepository.save(testUser.get()).block()
//
//        // Second (already activated) user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus().isBadRequest
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterDuplicateEmail() {
//        // First user
//        val firstUser = ManagedUserVM().apply {
//            login = "test-register-duplicate-email"
//            password = "password"
//            firstName = "Alice"
//            lastName = "Test"
//            email = "test-register-duplicate-email@example.com"
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(USER)
//        }
//
//        // Register first user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(firstUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        val testUser1 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional()
//        assertThat(testUser1.isPresent).isTrue()
//
//        // Duplicate email, different login
//        val secondUser = ManagedUserVM().apply {
//            login = "test-register-duplicate-email-2"
//            password = firstUser.password
//            firstName = firstUser.firstName
//            lastName = firstUser.lastName
//            email = firstUser.email
//            imageUrl = firstUser.imageUrl
//            langKey = firstUser.langKey
//            authorities = firstUser.authorities?.toMutableSet()
//        }
//
//        // Register second (non activated) user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        val testUser2 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional()
//        assertThat(testUser2.isPresent).isFalse()
//
//        val testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2").blockOptional()
//        assertThat(testUser3.isPresent).isTrue()
//
//        // Duplicate email - with uppercase email address
//        val userWithUpperCaseEmail = ManagedUserVM().apply {
//            id = firstUser.id
//            login = "test-register-duplicate-email-3"
//            password = firstUser.password
//            firstName = firstUser.firstName
//            lastName = firstUser.lastName
//            email = "TEST-register-duplicate-email@example.com"
//            imageUrl = firstUser.imageUrl
//            langKey = firstUser.langKey
//            authorities = firstUser.authorities?.toMutableSet()
//        }
//
//        // Register third (not activated) user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(userWithUpperCaseEmail))
//            .exchange()
//            .expectStatus().isCreated
//
//        val testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3").blockOptional()
//        assertThat(testUser4.isPresent).isTrue()
//        assertThat(testUser4.get().email).isEqualTo("test-register-duplicate-email@example.com")
//
//        testUser4.get().activated = true
//        userService.updateUser((UserDTO(testUser4.get()))).block()
//
//        // Register 4th (already activated) user
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus().is4xxClientError
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testRegisterAdminIsIgnored() {
//        val validUser = ManagedUserVM().apply {
//            login = "badguy"
//            password = "password"
//            firstName = "Bad"
//            lastName = "Guy"
//            email = "badguy@example.com"
//            activated = true
//            imageUrl = "http://placehold.it/50x50"
//            langKey = DEFAULT_LANGUAGE
//            authorities = setOf(ADMIN)
//        }
//
//        accountWebTestClient.post().uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(validUser))
//            .exchange()
//            .expectStatus().isCreated
//
//        val userDup = userRepository.findOneWithAuthoritiesByLogin("badguy").blockOptional()
//        assertThat(userDup.isPresent).isTrue()
//        assertThat(userDup.get().authorities).hasSize(1)
//            .containsExactly(authorityRepository.findById(USER).block())
//    }
//
//    @Test
//    fun testActivateAccount() {
//        val activationKey = "some activation key"
//        var user = User(
//            login = "activate-account",
//            email = "activate-account@example.com",
//            password = RandomStringUtils.random(60),
//            activated = false,
//            createdBy = SYSTEM_ACCOUNT,
//            activationKey = activationKey
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.get().uri("/api/activate?key={activationKey}", activationKey)
//            .exchange()
//            .expectStatus().isOk
//
//        user = userRepository.findOneByLogin(user.login!!).block()!!
//        assertThat(user.activated).isTrue()
//    }
//
//    @Test
//
//    fun testActivateAccountWithWrongKey() {
//        accountWebTestClient.get().uri("/api/activate?key=wrongActivationKey")
//            .exchange()
//            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    @Test
//    @WithMockUser("save-account")
//    @Throws(Exception::class)
//    fun testSaveAccount() {
//        val user = User(
//            login = "save-account",
//            email = "save-account@example.com",
//            password = RandomStringUtils.random(60),
//            createdBy = SYSTEM_ACCOUNT,
//            activated = true
//        )
//
//        userRepository.save(user).block()
//
//        val userDTO = UserDTO(
//            login = "not-used",
//            firstName = "firstname",
//            lastName = "lastname",
//            email = "save-account@example.com",
//            activated = false,
//            imageUrl = "http://placehold.it/50x50",
//            langKey = DEFAULT_LANGUAGE,
//            authorities = setOf(ADMIN)
//        )
//
//        accountWebTestClient.post().uri("/api/account")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(userDTO))
//            .exchange()
//            .expectStatus().isOk
//
//        val updatedUser = userRepository.findOneWithAuthoritiesByLogin(user?.login!!).block()
//        assertThat(updatedUser?.firstName).isEqualTo(userDTO.firstName)
//        assertThat(updatedUser?.lastName).isEqualTo(userDTO.lastName)
//        assertThat(updatedUser?.email).isEqualTo(userDTO.email)
//        assertThat(updatedUser?.langKey).isEqualTo(userDTO.langKey)
//        assertThat(updatedUser?.password).isEqualTo(user.password)
//        assertThat(updatedUser?.imageUrl).isEqualTo(userDTO.imageUrl)
//        assertThat(updatedUser?.activated).isEqualTo(true)
//        assertThat(updatedUser?.authorities).isEmpty()
//    }
//
//    @Test
//    @WithMockUser("save-invalid-email")
//    fun testSaveInvalidEmail() {
//        val user = User(
//            login = "save-invalid-email",
//            email = "save-invalid-email@example.com",
//            password = RandomStringUtils.random(60),
//            createdBy = SYSTEM_ACCOUNT,
//            activated = true
//        )
//
//        userRepository.save(user).block()
//
//        val userDTO = UserDTO(
//            login = "not-used",
//            firstName = "firstname",
//            lastName = "lastname",
//            email = "invalid email",
//            activated = false,
//            imageUrl = "http://placehold.it/50x50",
//            langKey = DEFAULT_LANGUAGE,
//            authorities = setOf(ADMIN)
//        )
//
//        accountWebTestClient.post().uri("/api/account")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(userDTO))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email").blockOptional()).isNotPresent
//    }
//
//    @Test
//    @WithMockUser("save-existing-email")
//    fun testSaveExistingEmail() {
//        val user = User(
//            login = "save-existing-email",
//            email = "save-existing-email@example.com",
//            password = RandomStringUtils.random(60),
//            createdBy = SYSTEM_ACCOUNT,
//            activated = true
//        )
//
//        userRepository.save(user).block()
//
//        val anotherUser = User(
//            login = "save-existing-email2",
//            email = "save-existing-email2@example.com",
//            password = RandomStringUtils.random(60),
//            createdBy = SYSTEM_ACCOUNT,
//            activated = true
//        )
//
//        userRepository.save(anotherUser).block()
//
//        val userDTO = UserDTO(
//            login = "not-used",
//            firstName = "firstname",
//            lastName = "lastname",
//            email = "save-existing-email2@example.com",
//            activated = false,
//            imageUrl = "http://placehold.it/50x50",
//            langKey = DEFAULT_LANGUAGE,
//            authorities = setOf(ADMIN)
//        )
//
//        accountWebTestClient.post().uri("/api/account")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(userDTO))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin("save-existing-email").block()
//        assertThat(updatedUser.email).isEqualTo("save-existing-email@example.com")
//    }
//
//    @Test
//    @WithMockUser("save-existing-email-and-login")
//    fun testSaveExistingEmailAndLogin() {
//        val user = User(
//            login = "save-existing-email-and-login",
//            email = "save-existing-email-and-login@example.com",
//            password = RandomStringUtils.random(60),
//            createdBy = SYSTEM_ACCOUNT,
//            activated = true
//        )
//
//        userRepository.save(user).block()
//
//        val userDTO = UserDTO(
//            login = "not-used",
//            firstName = "firstname",
//            lastName = "lastname",
//            email = "save-existing-email-and-login@example.com",
//            activated = false,
//            imageUrl = "http://placehold.it/50x50",
//            langKey = DEFAULT_LANGUAGE,
//            authorities = setOf(ADMIN)
//        )
//        // Mark here....
//        accountWebTestClient.post().uri("/api/account")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(userDTO))
//            .exchange()
//            .expectStatus().isOk
//
//        val updatedUser = userRepository.findOneByLogin("save-existing-email-and-login").block()
//        assertNotNull(updatedUser)
//        assertThat(updatedUser.email).isEqualTo("save-existing-email-and-login@example.com")
//    }
//
//    @Test
//    @WithMockUser("change-password-wrong-existing-password")
//    fun testChangePasswordWrongExistingPassword() {
//        val currentPassword = RandomStringUtils.random(60)
//        val user = User(
//            password = passwordEncoder.encode(currentPassword),
//            login = "change-password-wrong-existing-password",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "change-password-wrong-existing-password@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.post().uri("/api/account/change-password")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO("1$currentPassword", "new password")))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").block()
//        assertNotNull(updatedUser)
//        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isFalse()
//        assertThat(passwordEncoder.matches(currentPassword, updatedUser.password)).isTrue()
//    }
//
//    @Test
//    @WithMockUser("change-password")
//    fun testChangePassword() {
//        val currentPassword = RandomStringUtils.random(60)
//        val user = User(
//            password = passwordEncoder.encode(currentPassword),
//            login = "change-password",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "change-password@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.post().uri("/api/account/change-password")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "new password")))
//            .exchange()
//            .expectStatus().isOk
//
//        val updatedUser = userRepository.findOneByLogin("change-password").block()
//        assertNotNull(updatedUser)
//        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isTrue()
//    }
//
//    @Test
//    @WithMockUser("change-password-too-small")
//    fun testChangePasswordTooSmall() {
//        val currentPassword = RandomStringUtils.random(60)
//        val user = User(
//            password = passwordEncoder.encode(currentPassword),
//            login = "change-password-too-small",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "change-password-too-small@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1)
//
//        accountWebTestClient.post().uri("/api/account/change-password")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin("change-password-too-small").block()
//        assertNotNull(updatedUser)
//        assertThat(updatedUser.password).isEqualTo(user.password)
//    }
//
//    @Test
//    @WithMockUser("change-password-too-long")
//    fun testChangePasswordTooLong() {
//        val currentPassword = RandomStringUtils.random(60)
//        val user = User(
//            password = passwordEncoder.encode(currentPassword),
//            login = "change-password-too-long",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "change-password-too-long@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1)
//
//        accountWebTestClient.post().uri("/api/account/change-password")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin("change-password-too-long").block()
//        assertNotNull(updatedUser)
//        assertThat(updatedUser.password).isEqualTo(user.password)
//    }
//
//    @Test
//    @WithMockUser("change-password-empty")
//    fun testChangePasswordEmpty() {
//        val currentPassword = RandomStringUtils.random(60)
//        val user = User(
//            password = passwordEncoder.encode(currentPassword),
//            login = "change-password-empty",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "change-password-empty@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.post().uri("/api/account/change-password")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "")))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin("change-password-empty").block()
//        assertNotNull(updatedUser)
//        assertThat(updatedUser.password).isEqualTo(user.password)
//    }
//
//    @Test
//    fun testRequestPasswordReset() {
//        val user = User(
//            password = RandomStringUtils.random(60),
//            activated = true,
//            login = "password-reset",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "password-reset@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.post().uri("/api/account/reset-password/init")
//            .bodyValue("password-reset@example.com")
//            .exchange()
//            .expectStatus().isOk
//    }
//
//    @Test
//    fun testRequestPasswordResetUpperCaseEmail() {
//        val user = User(
//            password = RandomStringUtils.random(60),
//            activated = true,
//            login = "password-reset-upper-case",
//            createdBy = SYSTEM_ACCOUNT,
//            email = "password-reset-upper-case@example.com"
//        )
//
//        userRepository.save(user).block()
//
//        accountWebTestClient.post().uri("/api/account/reset-password/init")
//            .bodyValue("password-reset-upper-case@EXAMPLE.COM")
//            .exchange()
//            .expectStatus().isOk
//    }
//
//    @Test
//    fun testRequestPasswordResetWrongEmail() {
//        accountWebTestClient.post().uri("/api/account/reset-password/init")
//            .bodyValue("password-reset-wrong-email@example.com")
//            .exchange()
//            .expectStatus().isOk
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testFinishPasswordReset() {
//        val user = User(
//            password = RandomStringUtils.random(60),
//            login = "finish-password-reset",
//            email = "finish-password-reset@example.com",
//            resetDate = Instant.now().plusSeconds(60),
//            createdBy = SYSTEM_ACCOUNT,
//            resetKey = "reset key"
//        )
//
//        userRepository.save(user).block()
//
//        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "new password")
//
//        accountWebTestClient.post().uri("/api/account/reset-password/finish")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
//            .exchange()
//            .expectStatus().isOk
//
//        val updatedUser = userRepository.findOneByLogin(user.login!!).block()
//        assertNotNull(updatedUser)
//        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isTrue()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testFinishPasswordResetTooSmall() {
//        val user = User(
//            password = RandomStringUtils.random(60),
//            login = "finish-password-reset-too-small",
//            email = "finish-password-reset-too-small@example.com",
//            resetDate = Instant.now().plusSeconds(60),
//            createdBy = SYSTEM_ACCOUNT,
//            resetKey = "reset key too small"
//        )
//
//        userRepository.save(user).block()
//
//        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "foo")
//
//        accountWebTestClient.post().uri("/api/account/reset-password/finish")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        val updatedUser = userRepository.findOneByLogin(user.login!!).block()
//        assertNotNull(updatedUser)
//        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isFalse()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testFinishPasswordResetWrongKey() {
//        val keyAndPassword = KeyAndPasswordVM(key = "wrong reset key", newPassword = "new password")
//
//        accountWebTestClient.post().uri("/api/account/reset-password/finish")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
//            .exchange()
//            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//}
//
//

//package khipster.web.rest
//
//import khipster.KhipsterApp
//import khipster.config.SYSTEM_ACCOUNT
//import khipster.domain.Authority
//import khipster.domain.User
//import khipster.repository.UserRepository
//import khipster.security.ADMIN
//import khipster.security.USER
//import khipster.service.dto.UserDTO
//import khipster.service.mapper.UserMapper
//import khipster.web.rest.vm.ManagedUserVM
//import org.apache.commons.lang3.RandomStringUtils
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.security.test.context.support.WithMockUser
//import org.springframework.test.web.reactive.server.WebTestClient
//import java.time.Instant
//import kotlin.test.assertNotNull
//
///**
// * Integration tests for the [UserResource] REST controller.
// */
//@AutoConfigureWebTestClient
//@WithMockUser(authorities = [ADMIN])
//@SpringBootTest(classes = [KhipsterApp::class])
//class UserResourceIT {
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Autowired
//    private lateinit var userMapper: UserMapper
//
//    @Autowired
//    private lateinit var webTestClient: WebTestClient
//
//    private lateinit var user: User
//
//    @BeforeEach
//    fun initTest() {
//        userRepository.deleteAllUserAuthorities().block()
//        userRepository.deleteAll().block()
//        user = createEntity()
//        user.apply {
//            login = DEFAULT_LOGIN
//            email = DEFAULT_EMAIL
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun createUser() {
//        val databaseSizeBeforeCreate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        // Create the User
//        val managedUserVM = ManagedUserVM().apply {
//            login = DEFAULT_LOGIN
//            password = DEFAULT_PASSWORD
//            firstName = DEFAULT_FIRSTNAME
//            lastName = DEFAULT_LASTNAME
//            email = DEFAULT_EMAIL
//            activated = true
//            imageUrl = DEFAULT_IMAGEURL
//            langKey = DEFAULT_LANGKEY
//            authorities = setOf(USER)
//        }
//
//        webTestClient.post().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isCreated
//
//        assertPersistedUsers { userList ->
//            // Validate the User in the database
//            assertThat(userList).hasSize(databaseSizeBeforeCreate + 1)
//            val testUser = userList.first { it.login == DEFAULT_LOGIN }
//            assertThat(testUser.login).isEqualTo(DEFAULT_LOGIN)
//            assertThat(testUser.firstName).isEqualTo(DEFAULT_FIRSTNAME)
//            assertThat(testUser.lastName).isEqualTo(DEFAULT_LASTNAME)
//            assertThat(testUser.email).isEqualTo(DEFAULT_EMAIL)
//            assertThat(testUser.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
//            assertThat(testUser.langKey).isEqualTo(DEFAULT_LANGKEY)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun createUserWithExistingId() {
//        val databaseSizeBeforeCreate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        val managedUserVM = ManagedUserVM().apply {
//            id = 1L
//            login = DEFAULT_LOGIN
//            password = DEFAULT_PASSWORD
//            firstName = DEFAULT_FIRSTNAME
//            lastName = DEFAULT_LASTNAME
//            email = DEFAULT_EMAIL
//            activated = true
//            imageUrl = DEFAULT_IMAGEURL
//            langKey = DEFAULT_LANGKEY
//            authorities = setOf(USER)
//        }
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        webTestClient.post().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        assertPersistedUsers { userList ->
//            // Validate the User in the database
//            assertThat(userList).hasSize(databaseSizeBeforeCreate)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun createUserWithExistingLogin() {
//        // Initialize the database
//        userRepository.save(user).block()
//        val databaseSizeBeforeCreate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        val managedUserVM = ManagedUserVM().apply {
//            login = DEFAULT_LOGIN // this login should already be used
//            password = DEFAULT_PASSWORD
//            firstName = DEFAULT_FIRSTNAME
//            lastName = DEFAULT_LASTNAME
//            email = "anothermail@localhost"
//            activated = true
//            imageUrl = DEFAULT_IMAGEURL
//            langKey = DEFAULT_LANGKEY
//            authorities = setOf(USER)
//        }
//
//        // Create the User
//        webTestClient.post().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeCreate) }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun createUserWithExistingEmail() {
//        // Initialize the database
//        userRepository.save(user).block()
//        val databaseSizeBeforeCreate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        val managedUserVM = ManagedUserVM().apply {
//            login = "anotherlogin"
//            password = DEFAULT_PASSWORD
//            firstName = DEFAULT_FIRSTNAME
//            lastName = DEFAULT_LASTNAME
//            email = DEFAULT_EMAIL // this email should already be used
//            activated = true
//            imageUrl = DEFAULT_IMAGEURL
//            langKey = DEFAULT_LANGKEY
//            authorities = setOf(USER)
//        }
//
//        // Create the User
//        webTestClient.post().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isBadRequest
//
//        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeCreate) }
//    }
//
//    @Test
//    fun getAllUsers() {
//        // Initialize the database
//        userRepository.save(user).block()
//
//        // Get all the users
//        val foundUser = webTestClient.get().uri("/api/users?sort=id,DESC")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .returnResult(UserDTO::class.java).responseBody.blockFirst()
//
//        assertNotNull(foundUser)
//        assertThat(foundUser.login).isEqualTo(DEFAULT_LOGIN)
//        assertThat(foundUser.firstName).isEqualTo(DEFAULT_FIRSTNAME)
//        assertThat(foundUser.lastName).isEqualTo(DEFAULT_LASTNAME)
//        assertThat(foundUser.email).isEqualTo(DEFAULT_EMAIL)
//        assertThat(foundUser.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
//        assertThat(foundUser.langKey).isEqualTo(DEFAULT_LANGKEY)
//    }
//
//    @Test
//    fun getAllUsersSortedByParameters() {
//        // Initialize the database
//        userRepository.save(user).block()
//
//        webTestClient.get().uri("/api/users?sort=resetKey,DESC").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isBadRequest
//        webTestClient.get().uri("/api/users?sort=password,DESC").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isBadRequest
//        webTestClient.get().uri("/api/users?sort=resetKey,id,DESC").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isBadRequest
//        webTestClient.get().uri("/api/users?sort=id,DESC").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk
//    }
//
//    @Test
//    fun getUser() {
//        // Initialize the database
//        userRepository.save(user).block()
//
//        // Get the user
//        webTestClient.get().uri("/api/users/{login}", user.login)
//            .exchange()
//            .expectStatus().isOk
//            .expectHeader().contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("\$.login").isEqualTo(user.login)
//            .jsonPath("\$.firstName").isEqualTo(DEFAULT_FIRSTNAME)
//            .jsonPath("\$.lastName").isEqualTo(DEFAULT_LASTNAME)
//            .jsonPath("\$.email").isEqualTo(DEFAULT_EMAIL)
//            .jsonPath("\$.imageUrl").isEqualTo(DEFAULT_IMAGEURL)
//            .jsonPath("\$.langKey").isEqualTo(DEFAULT_LANGKEY)
//    }
//
//    @Test
//    fun getNonExistingUser() {
//        webTestClient.get().uri("/api/users/unknown")
//            .exchange()
//            .expectStatus().isNotFound
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun updateUser() {
//        // Initialize the database
//        userRepository.save(user).block()
//        val databaseSizeBeforeUpdate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        // Update the user
//        val updatedUser = userRepository.findById(user.id!!).block()
//        assertNotNull(updatedUser)
//
//        val managedUserVM = ManagedUserVM().apply {
//            id = updatedUser.id
//            login = updatedUser.login
//            password = UPDATED_PASSWORD
//            firstName = UPDATED_FIRSTNAME
//            lastName = UPDATED_LASTNAME
//            email = UPDATED_EMAIL
//            activated = updatedUser.activated
//            imageUrl = UPDATED_IMAGEURL
//            langKey = UPDATED_LANGKEY
//            createdBy = updatedUser.createdBy
//            createdDate = updatedUser.createdDate
//            lastModifiedBy = updatedUser.lastModifiedBy
//            lastModifiedDate = updatedUser.lastModifiedDate
//            authorities = setOf(USER)
//        }
//
//        webTestClient.put().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isOk
//
//        assertPersistedUsers { userList ->
//            assertThat(userList).hasSize(databaseSizeBeforeUpdate)
//            val testUser = userList.first { it.id == updatedUser.id }
//            assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME)
//            assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME)
//            assertThat(testUser.email).isEqualTo(UPDATED_EMAIL)
//            assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL)
//            assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun updateUserLogin() {
//        // Initialize the database
//        userRepository.save(user).block()
//        val databaseSizeBeforeUpdate = userRepository.findAll()
//            .collectList().block()!!.size
//
//        // Update the user
//        val updatedUser = userRepository.findById(user.id!!).block()
//        assertNotNull(updatedUser)
//
//        val managedUserVM = ManagedUserVM().apply {
//            id = updatedUser.id
//            login = UPDATED_LOGIN
//            password = UPDATED_PASSWORD
//            firstName = UPDATED_FIRSTNAME
//            lastName = UPDATED_LASTNAME
//            email = UPDATED_EMAIL
//            activated = updatedUser.activated
//            imageUrl = UPDATED_IMAGEURL
//            langKey = UPDATED_LANGKEY
//            createdBy = updatedUser.createdBy
//            createdDate = updatedUser.createdDate
//            lastModifiedBy = updatedUser.lastModifiedBy
//            lastModifiedDate = updatedUser.lastModifiedDate
//            authorities = setOf(USER)
//        }
//
//        webTestClient.put().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isOk
//
//        assertPersistedUsers { userList ->
//            assertThat(userList).hasSize(databaseSizeBeforeUpdate)
//            val testUser = userList.first { it.id == updatedUser.id }
//            assertThat(testUser.login).isEqualTo(UPDATED_LOGIN)
//            assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME)
//            assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME)
//            assertThat(testUser.email).isEqualTo(UPDATED_EMAIL)
//            assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL)
//            assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun updateUserExistingEmail() {
//        // Initialize the database with 2 users
//        userRepository.save(user).block()
//
//        val anotherUser = User(
//            login = "jhipster",
//            password = RandomStringUtils.random(60),
//            activated = true,
//            email = "jhipster@localhost",
//            firstName = "java",
//            lastName = "hipster",
//            imageUrl = "",
//            createdBy = SYSTEM_ACCOUNT,
//            langKey = "en"
//        )
//        userRepository.save(anotherUser).block()
//
//        // Update the user
//        val updatedUser = userRepository.findById(user.id!!).block()
//        assertNotNull(updatedUser)
//
//        val managedUserVM = ManagedUserVM().apply {
//            id = updatedUser.id
//            login = updatedUser.login
//            password = updatedUser.password
//            firstName = updatedUser.firstName
//            lastName = updatedUser.lastName
//            email = "jhipster@localhost" // this email should already be used by anotherUser
//            activated = updatedUser.activated
//            imageUrl = updatedUser.imageUrl
//            langKey = updatedUser.langKey
//            createdBy = updatedUser.createdBy
//            createdDate = updatedUser.createdDate
//            lastModifiedBy = updatedUser.lastModifiedBy
//            lastModifiedDate = updatedUser.lastModifiedDate
//            authorities = setOf(USER)
//        }
//
//        webTestClient.put().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isBadRequest
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun updateUserExistingLogin() {
//        // Initialize the database
//        userRepository.save(user).block()
//
//        val anotherUser = User(
//            login = "jhipster",
//            password = RandomStringUtils.random(60),
//            activated = true,
//            email = "jhipster@localhost",
//            firstName = "java",
//            lastName = "hipster",
//            imageUrl = "",
//            createdBy = SYSTEM_ACCOUNT,
//            langKey = "en"
//        )
//        userRepository.save(anotherUser).block()
//
//        // Update the user
//        val updatedUser = userRepository.findById(user.id!!).block()
//        assertNotNull(updatedUser)
//
//        val managedUserVM = ManagedUserVM().apply {
//            id = updatedUser.id
//            login = "jhipster" // this login should already be used by anotherUser
//            password = updatedUser.password
//            firstName = updatedUser.firstName
//            lastName = updatedUser.lastName
//            email = updatedUser.email
//            activated = updatedUser.activated
//            imageUrl = updatedUser.imageUrl
//            langKey = updatedUser.langKey
//            createdBy = updatedUser.createdBy
//            createdDate = updatedUser.createdDate
//            lastModifiedBy = updatedUser.lastModifiedBy
//            lastModifiedDate = updatedUser.lastModifiedDate
//            authorities = setOf(USER)
//        }
//
//        webTestClient.put().uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(convertObjectToJsonBytes(managedUserVM))
//            .exchange()
//            .expectStatus().isBadRequest
//    }
//
//    @Test
//    fun deleteUser() {
//        // Initialize the database
//        userRepository.save(user).block()
//        val databaseSizeBeforeDelete = userRepository.findAll()
//            .collectList().block()!!.size
//
//        // Delete the user
//        webTestClient.delete().uri("/api/users/{login}", user.login)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isNoContent
//
//        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeDelete - 1) }
//    }
//
//    @Test
//    fun getAllAuthorities() {
//        webTestClient.get().uri("/api/users/authorities")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
//            .expectBody()
//            .jsonPath("\$").isArray
//            .jsonPath("\$[?(@=='" + ADMIN + "')]").hasJsonPath()
//            .jsonPath("\$[?(@=='" + USER + "')]").hasJsonPath()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testUserEquals() {
//        equalsVerifier(User::class)
//        val user1 = User(id = 1L)
//        val user2 = User(id = user1.id)
//        assertThat(user1).isEqualTo(user2)
//        user2.id = 2L
//        assertThat(user1).isNotEqualTo(user2)
//        user1.id = null
//        assertThat(user1).isNotEqualTo(user2)
//    }
//
//    @Test
//    fun testUserDTOtoUser() {
//        val userDTO = UserDTO(
//            id = DEFAULT_ID,
//            login = DEFAULT_LOGIN,
//            firstName = DEFAULT_FIRSTNAME,
//            lastName = DEFAULT_LASTNAME,
//            email = DEFAULT_EMAIL,
//            activated = true,
//            imageUrl = DEFAULT_IMAGEURL,
//            langKey = DEFAULT_LANGKEY,
//            createdBy = DEFAULT_LOGIN,
//            lastModifiedBy = DEFAULT_LOGIN,
//            authorities = setOf(USER)
//        )
//
//        val user = userMapper.userDTOToUser(userDTO)
//        assertNotNull(user)
//        assertThat(user.id).isEqualTo(DEFAULT_ID)
//        assertThat(user.login).isEqualTo(DEFAULT_LOGIN)
//        assertThat(user.firstName).isEqualTo(DEFAULT_FIRSTNAME)
//        assertThat(user.lastName).isEqualTo(DEFAULT_LASTNAME)
//        assertThat(user.email).isEqualTo(DEFAULT_EMAIL)
//        assertThat(user.activated).isEqualTo(true)
//        assertThat(user.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
//        assertThat(user.langKey).isEqualTo(DEFAULT_LANGKEY)
//        assertThat(user.createdBy).isNull()
//        assertThat(user.createdDate).isNotNull()
//        assertThat(user.lastModifiedBy).isNull()
//        assertThat(user.lastModifiedDate).isNotNull()
//        assertThat(user.authorities).extracting("name").containsExactly(USER)
//    }
//
//    @Test
//    fun testUserToUserDTO() {
//        user.id = DEFAULT_ID
//        user.createdBy = DEFAULT_LOGIN
//        user.createdDate = Instant.now()
//        user.lastModifiedBy = DEFAULT_LOGIN
//        user.lastModifiedDate = Instant.now()
//        user.authorities = mutableSetOf(Authority(name = USER))
//
//        val userDTO = userMapper.userToUserDTO(user)
//
//        assertThat(userDTO.id).isEqualTo(DEFAULT_ID)
//        assertThat(userDTO.login).isEqualTo(DEFAULT_LOGIN)
//        assertThat(userDTO.firstName).isEqualTo(DEFAULT_FIRSTNAME)
//        assertThat(userDTO.lastName).isEqualTo(DEFAULT_LASTNAME)
//        assertThat(userDTO.email).isEqualTo(DEFAULT_EMAIL)
//        assertThat(userDTO.isActivated()).isEqualTo(true)
//        assertThat(userDTO.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
//        assertThat(userDTO.langKey).isEqualTo(DEFAULT_LANGKEY)
//        assertThat(userDTO.createdBy).isEqualTo(DEFAULT_LOGIN)
//        assertThat(userDTO.createdDate).isEqualTo(user.createdDate)
//        assertThat(userDTO.lastModifiedBy).isEqualTo(DEFAULT_LOGIN)
//        assertThat(userDTO.lastModifiedDate).isEqualTo(user.lastModifiedDate)
//        assertThat(userDTO.authorities).containsExactly(USER)
//        assertThat(userDTO.toString()).isNotNull()
//    }
//
//    @Test
//    fun testAuthorityEquals() {
//        val authorityA = Authority()
//        assertThat(authorityA).isEqualTo(authorityA)
//        assertThat(authorityA).isNotEqualTo(null)
//        assertThat(authorityA).isNotEqualTo(Any())
//        assertThat(authorityA.hashCode()).isEqualTo(31)
//        assertThat(authorityA.toString()).isNotNull()
//
//        val authorityB = Authority()
//        assertThat(authorityA.name).isEqualTo(authorityB.name)
//
//        authorityB.name = ADMIN
//        assertThat(authorityA).isNotEqualTo(authorityB)
//
//        authorityA.name = USER
//        assertThat(authorityA).isNotEqualTo(authorityB)
//
//        authorityB.name = USER
//        assertThat(authorityA).isEqualTo(authorityB)
//        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode())
//    }
//
//    companion object {
//
//        private const val DEFAULT_LOGIN = "johndoe"
//        private const val UPDATED_LOGIN = "jhipster"
//
//        private const val DEFAULT_ID = 1L
//
//        private const val DEFAULT_PASSWORD = "passjohndoe"
//        private const val UPDATED_PASSWORD = "passjhipster"
//
//        private const val DEFAULT_EMAIL = "johndoe@localhost"
//        private const val UPDATED_EMAIL = "jhipster@localhost"
//
//        private const val DEFAULT_FIRSTNAME = "john"
//        private const val UPDATED_FIRSTNAME = "jhipsterFirstName"
//
//        private const val DEFAULT_LASTNAME = "doe"
//        private const val UPDATED_LASTNAME = "jhipsterLastName"
//
//        private const val DEFAULT_IMAGEURL = "http://placehold.it/50x50"
//        private const val UPDATED_IMAGEURL = "http://placehold.it/40x40"
//
//        private const val DEFAULT_LANGKEY = "en"
//        private const val UPDATED_LANGKEY = "fr"
//
//        /**
//         * Create a User.
//         *
//         * This is a static method, as tests for other entities might also need it,
//         * if they test an entity which has a required relationship to the User entity.
//         */
//        @JvmStatic
//        fun createEntity(): User {
//            return User(
//                login = DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5),
//                password = RandomStringUtils.random(60),
//                activated = true,
//                email = RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL,
//                firstName = DEFAULT_FIRSTNAME,
//                lastName = DEFAULT_LASTNAME,
//                imageUrl = DEFAULT_IMAGEURL,
//                createdBy = SYSTEM_ACCOUNT,
//                langKey = DEFAULT_LANGKEY
//            )
//        }
//    }
//
//    fun assertPersistedUsers(userAssertion: (List<User>) -> Unit) {
//        userAssertion(userRepository.findAll().collectList().block())
//    }
//}


