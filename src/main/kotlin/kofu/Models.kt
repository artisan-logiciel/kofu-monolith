package kofu

import am.ik.yavi.builder.ValidatorBuilder.of
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolations
import am.ik.yavi.fn.Either
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("users")
data class UserDemo(
    @Id
    val login: String,
    val firstname: String,
    val lastname: String
)

@Table("authority")
data class Authority(
    @Id private val role: String? = null
) {
    fun getId() = role
    fun isNew() = true

    companion object {
        private const val serialVersionUID = 1L

        @JvmStatic
        val validator by lazy {
            of<Authority>()
                .konstraint(Authority::role) {
                    notNull().lessThanOrEqual(50)
                }.build()
        }
    }

    fun validate() = validator.validateToEither(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Authority) return false
        if (other.role == null || role == null) return false
        return role == other.role
    }

    override fun hashCode() = 31
}

@Table("user_authority")
data class UserAuthority(
    val id: UUID,
    private val role: String? = null
) {
    companion object {
        private const val serialVersionUID = 1L
    }
}

@Table("`user`")
data class User(
    @Id var id: UUID? = null,
    var login: String? = null,
    var password: String? = null,
    var createdBy: String? = null,
    var createdDate: Instant? = Instant.now(),
    var lastModifiedBy: String? = null,
    val lastModifiedDate: Instant? = Instant.now(),
    var email: String? = null,
    var activated: Boolean = false,
    var langKey: String? = null,
    var activationKey: String? = null,
    var resetKey: String? = null,
    var resetDate: Instant? = null,
    @Transient var authorities: MutableSet<Authority> = mutableSetOf(),
) {
    companion object {
        @JvmStatic
        val validator by lazy {
            of<User>()
                .konstraint(User::login) {
                    notNull().greaterThanOrEqual(1)
                        .lessThanOrEqual(50)
                        .pattern(LOGIN_REGEX)
                }
                .konstraint(User::email) {
                    greaterThanOrEqual(5).lessThanOrEqual(254)
                        .email()
                }
                .konstraint(User::activated) {
                    notNull()
                }
                .konstraint(User::langKey) {
                    greaterThanOrEqual(2).lessThanOrEqual(10)
                }
                .konstraint(User::activationKey) {
                    lessThanOrEqual(20)
                }
                .konstraint(User::resetKey) {
                    lessThanOrEqual(20)
                }
                .build()
        }
    }

    fun validate() = validator.validateToEither(this)
}

data class PasswordChangeDTO(
    var currentPassword: String? = null,
    var newPassword: String? = null
)

open class UserDto(
    @get:JsonIgnore var id: UUID? = null,
    var login: String? = null,
    var email: String? = null,
    @get:JsonIgnore var activated: Boolean = false,
    @get:JsonIgnore var langKey: String? = null,
    @get:JsonIgnore var createdBy: String? = null,
    @get:JsonIgnore var createdDate: Instant? = null,
    @get:JsonIgnore var lastModifiedBy: String? = null,
    @get:JsonIgnore var lastModifiedDate: Instant? = null,
    @get:JsonIgnore var authorities: Set<String>? = null
) {
    constructor(user: User) :
            this(
                user.id,
                user.login,
                user.email,
                user.activated,
                user.langKey,
                user.createdBy,
                user.createdDate,
                user.lastModifiedBy,
                user.lastModifiedDate,
                user.authorities.map(Authority::getId)
                    .filterNotNullTo(mutableSetOf())
            )

    companion object {
        @JvmStatic
        val validator by lazy {
            of<UserDto>()
                .konstraint(UserDto::login) {
                    notBlank().greaterThanOrEqual(1)
                        .lessThanOrEqual(50)
                        .pattern(LOGIN_REGEX)
                }
                .konstraint(UserDto::email) {
                    greaterThanOrEqual(5).lessThanOrEqual(254)
                        .email()
                }
                .konstraint(UserDto::activated) {
                    notNull()
                }
                .konstraint(UserDto::langKey) {
                    greaterThanOrEqual(2).lessThanOrEqual(10)
                }
                .build()
        }
    }

    open fun validate() = validator.validateToEither(this)
    fun isActivated(): Boolean = activated

    override fun toString() = "UserDto{" +
            "login='" + login + '\'' +
            ", email='" + email + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}"
}

class KeyAndPasswordVM(var key: String? = null, var newPassword: String? = null)

class LoginVM(
    var username: String,
    var password: String,
    var isRememberMe: Boolean
) {
    override fun toString() = "LoginVM{" +
            "username='" + username + '\''.toString() +
            ", rememberMe=" + isRememberMe +
            '}'.toString()

    companion object {
        @JvmStatic
        val validator by lazy {
            of<LoginVM>()
                .konstraint(LoginVM::username) {
                    lessThanOrEqual(50)
                        .greaterThanOrEqual(1)
                        .notNull()
                }.konstraint(LoginVM::password) {
                    lessThanOrEqual(100)
                        .greaterThanOrEqual(4)
                        .notNull()
                }.build()
        }
    }

    fun validate() = validator.validateToEither(this)
}

class ManagedUserVM(var password: String) : UserDto() {
    override fun toString() = "ManagedUserVM{${super.toString()}}"

    companion object {
        const val PASSWORD_MIN_LENGTH: Int = 4
        const val PASSWORD_MAX_LENGTH: Int = 100

        @JvmStatic
        val validator by lazy {
            of<ManagedUserVM>()
                .konstraint(ManagedUserVM::password) {
                    lessThanOrEqual(PASSWORD_MAX_LENGTH)
                        .greaterThanOrEqual(PASSWORD_MIN_LENGTH)
                }.build()
        }
    }

    override fun validate(): Either<ConstraintViolations, UserDto> {
        return super.validate().also {
            validator.validateToEither(this)
        }
    }
}