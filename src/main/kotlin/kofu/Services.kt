package kofu

//import kofu.Log.log
//import kofu.RandomUtil.generatePassword
//import kofu.RandomUtil.generateResetKey
import kofu.Log.log
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Flux.fromIterable
//import reactor.core.publisher.Mono
//import reactor.core.scheduler.Schedulers.boundedElastic
//import java.time.Instant
//import java.time.Instant.now
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.temporal.ChronoUnit
import java.util.*

@Suppress("UNUSED_PARAMETER")
class UserService(
        private val passwordEncoder: PasswordEncoder,
        private val userRepository: UserRepository,
        private val authorityRepository: AuthorityRepository,
) {

    @Suppress( "UNUSED_VARIABLE")
    fun registerUser(
        userDto: UserDto,
        password: String
    ):  Mono<UserDto>? {
        val login = userDto.login ?: throw IllegalArgumentException("Empty login not allowed")
        val email = userDto.email
        log.info(userDto.toString())
        return userDto.toMono()
    }

    /* : Mono<User> {
       val login = userDto.login ?: throw IllegalArgumentException("Empty login not allowed")
       val email = userDto.email

       return userRepository.findOneByLogin(login.toLowerCase())
           .flatMap { existingUser ->
               if (!existingUser.activated)
                   userRepository.delete(existingUser)
               else throw UsernameAlreadyUsedException()
           }
           .then(userRepository.findOneByEmailIgnoreCase(email!!))
           .flatMap { existingUser ->
               if (!existingUser.activated)
                   userRepository.delete(existingUser)
               else throw EmailAlreadyUsedException()
           }
           .publishOn(boundedElastic())
           .then(
               Mono.fromCallable {
                   User(
                       id = userDto.id!!,
                       password = passwordEncoder.encode(password),
                       login = login.toLowerCase(),
                       firstName = userDto.firstName!!,
                       lastName = userDto.lastName!!,
                       email = email.toLowerCase(),
                       imageUrl = userDto.imageUrl,
                       langKey = userDto.langKey,
                       activated = false,
                       activationKey = RandomUtil.generateActivationKey()
                   )
               }
           )
           .flatMap { newUser ->
               val authorities = mutableSetOf<Authority>()
               authorityRepository.findByRole(ROLE_USER)
                   .map(authorities::add)
                   .thenReturn(newUser)
                   .doOnNext { it.authorities = authorities }
                   .flatMap { saveUser(it) }
                   .doOnNext { log.debug("Created Information for User: $it") }
           }
   }
*/


    fun activateRegistration(key: String): Nothing? = null
    /* =  userRepository
        .findOneByActivationKey(key)
        .flatMap {
            saveUser(it.apply {
                activated = true
                activationKey = null
            })
        }.doOnNext { log.debug("Activated user: $it") } */

    fun completePasswordReset(newPassword: String, key: String): Nothing? = null
    /* = userRepository
       .findOneByResetKey(key)
       .filter {
           it.resetDate
               ?.isAfter(now().minusSeconds(86400))
               ?: false
       }.publishOn(boundedElastic())
       .map {
           it.apply {
               password = passwordEncoder.encode(newPassword)
               resetKey = null
               resetDate = null
           }
       }.flatMap(::saveUser)
       .doFirst {
           log.debug("Reset user password for reset key $key")
       } */

    fun requestPasswordReset(mail: String): Nothing? = null
    /* = userRepository
       .findOneByEmailIgnoreCase(mail)
       .publishOn(boundedElastic())
       .map {
           it.apply {
               resetKey = generateResetKey()
               resetDate = now()
           }
       }.flatMap(::saveUser) */


    fun createUser(userDto: UserDto): Nothing? = null
    /* : Mono<User> {
        val user = User(
            password = null,
            id = userDto.id,
            login = userDto.login?.toLowerCase()!!,
            firstName = userDto.firstName!!,
            lastName = userDto.lastName!!,
            email = userDto.email?.toLowerCase()!!,
            imageUrl = userDto.imageUrl,
            langKey = userDto.langKey ?: DEFAULT_LANGUAGE
        )
        return fromIterable(userDto.authorities ?: mutableSetOf())
            .flatMap<Authority>(authorityRepository::findByRole)
            .doOnNext { user.authorities.add(it) }
            .then(Mono.just(user))
            .publishOn(boundedElastic())
            .map {
                it.apply {
                    password = passwordEncoder.encode(generatePassword())
                    resetKey = generateResetKey()
                    resetDate = now()
                    activated = true
                }
            }.flatMap(::saveUser)
            .doOnNext { log.debug("Changed Information for User: $it") }
    } */

    fun updateUser(userDto: UserDto): Nothing? = null
    /* = userRepository
       .findById(userDto.id!!)
       .flatMap { it ->
           it.apply {
               login = userDto.login!!.toLowerCase()
               firstName = userDto.firstName!!
               lastName = userDto.lastName!!
               email = userDto.email?.toLowerCase()
               imageUrl = userDto.imageUrl
               activated = userDto.activated
               langKey = userDto.langKey
           }
           val managedAuthorities = it.authorities
           managedAuthorities.clear()
           fromIterable(userDto.authorities!!)
               .flatMap(authorityRepository::findByRole)
               .map(managedAuthorities::add)
               .then(Mono.just(it))
       }.flatMap(::saveUser)
       .doOnNext { log.debug("Changed Information for User: $it") }
       .map { UserDto(it) } */

    fun deleteUser(login: String): Nothing? = null
    /* = userRepository
       .findOneByLogin(login)
       .flatMap { userRepository.delete(it).thenReturn(it) }
       .doOnNext { log.debug("Changed Information for User: $it") }
       .then() */

    fun updateUser(
            firstName: String?,
            lastName: String?,
            email: String?,
            langKey: String?,
            imageUrl: String?
    ): Nothing? = null /* = getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .flatMap {
            it.firstName = firstName!!
            it.lastName = lastName!!
            it.email = email?.toLowerCase()
            it.langKey = langKey
            it.imageUrl = imageUrl
            saveUser(it)
        }.doOnNext { log.debug("Changed Information for User: $it") }
        .then() */

    fun saveUser(user: User): Nothing? = null/*  = getCurrentUserLogin()
        .switchIfEmpty(Mono.just(SYSTEM_ACCOUNT))
        .flatMap { login ->
            if (user.createdBy == null) {
                user.createdBy = login
            }
            user.lastModifiedBy = login
            userRepository.save(user).flatMap {
                fromIterable(user.authorities)
                    .flatMap {
                        user.id?.let { it1 ->
                            it.getId()
                                ?.let { it2 ->
                                    userRepository.saveUserAuthority(it1, it2)
                                }
                        }
                    }.then(Mono.just(user))
            }
        } */

    fun changePassword(
            currentClearTextPassword: String,
            newPassword: String
    ): Nothing? = null /* = getCurrentUserLogin()
        .flatMap(userRepository::findOneByLogin)
        .publishOn(boundedElastic())
        .map { user ->
            val currentEncryptedPassword = user.password
            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                throw InvalidPasswordException()
            }
            val encryptedPassword = passwordEncoder.encode(newPassword)
            user.password = encryptedPassword
            user
        }.flatMap { saveUser(it) }
        .doOnNext { log.debug("Changed password for User: $it") }
        .then() */

    fun getAllManagedUsers(pageable: Pageable): Nothing? = null
    /* = userRepository
       .findAllByLoginNot(pageable, ANONYMOUS_USER).map { UserDto(it) } */

    fun countManagedUsers(): Nothing? = null /* = userRepository
        .countAllByLoginNot(ANONYMOUS_USER) */

    fun getUserWithAuthoritiesByLogin(login: String): Nothing? = null /* = userRepository
        .findOneWithAuthoritiesByLogin(login) */

    fun getUserWithAuthorities(): Nothing? = null /* = getCurrentUserLogin()
        .flatMap(userRepository::findOneWithAuthoritiesByLogin) */

    //    @Scheduled(cron = "0 0 1 * * ?")
    fun removeNotActivatedUsers(): Nothing? = null /* {
        removeNotActivatedUsersReactively()!!.blockLast()
    } */

    fun removeNotActivatedUsersReactively(): Nothing? = null/* : Flux<User> {
        return userRepository
            .findAllByNotActivatedInTime(
                LocalDateTime.ofInstant(
                    Instant.now().minus(3, ChronoUnit.DAYS),
                    ZoneOffset.UTC
                )
            ).flatMap { userRepository.delete(it).thenReturn(it) }
            .doOnNext { log.debug("Deleted User: $it") }
    } */

    fun getAuthorities(): Nothing? = null
    /* : Flux<String> = authorityRepository.findAll().map(Authority::getId) */
}

class UserMapper {

//    fun usersToUserDtos(users: List<User?>): MutableList<UserDto> =
//            users.asSequence()
//                    .filterNotNull()
//                    .mapTo(mutableListOf()) { userToUserDto(it) }

//    fun userToUserDto(user: User): UserDto = UserDto(user)

//    fun userDtosToUsers(userDTOs: List<UserDto?>) =
//            userDTOs.asSequence()
//                    .mapNotNullTo(mutableListOf()) { userDtoToUser(it) }

//    fun userDtoToUser(userDto: UserDto?) =
//            when (userDto) {
//                null -> null
//                else -> {
//                    User(
//                            id = userDto.id,
//                            login = userDto.login!!,
//                            email = userDto.email,
//                            activated = userDto.activated,
//                            langKey = userDto.langKey,
//                            authorities = authoritiesFromStrings(userDto.authorities)
//                    )
//                }
//            }

//    private fun authoritiesFromStrings(authoritiesAsString: Set<String>?) =
//            authoritiesAsString?.mapTo(mutableSetOf()) {
//                Authority(role = it)
//            } ?: mutableSetOf()

    fun userFromId(id: UUID?) = id?.let { User(id = it) }
}


@Suppress("UNUSED_PARAMETER")
class MailService {
    fun sendEmail(
            to: String,
            subject: String,
            content: String,
            isMultipart: Boolean,
            isHtml: Boolean
    ): Nothing? = null

    fun sendEmailFromTemplate(
            user: User,
            templateName: String,
            titleKey: String
    ): Nothing? = null

    fun sendActivationEmail(
            user: User
    ): Nothing? = null

    fun sendCreationEmail(
            user: User
    ): Nothing? = null

    fun sendPasswordResetMail(
            user: User
    ): Nothing? = null
}

class EmailAlreadyUsedException : RuntimeException("Email is already in use!") {
    companion object {
        private const val serialVersionUID = 1L
    }
}

class InvalidPasswordException : RuntimeException("Incorrect password") {
    companion object {
        private const val serialVersionUID = 1L
    }
}

class UsernameAlreadyUsedException : RuntimeException("Login name already used!") {
    companion object {
        private const val serialVersionUID = 1L
    }
}