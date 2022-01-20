package kofu

import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.empty
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.isEqual
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import reactor.util.function.Tuples
import java.time.LocalDateTime
import java.util.*

class UserDemoRepository(private val repo: R2dbcEntityOperations) {
    companion object {
        @JvmStatic
        private val userDemoModel by lazy { UserDemo::class.java }
    }

    fun count() = repo.count(empty(), userDemoModel)

    fun findAll() = repo.select(empty(), userDemoModel)

    fun findOne(id: String?) =
        repo.select(userDemoModel)
            .matching(
                query(where("login").`is`(id!!))
            ).one()

    fun deleteAll() =
        repo.delete(userDemoModel)
            .all().then()

    fun insert(userDemo: UserDemo) =
        repo.insert(userDemoModel)
            .using(userDemo)
}

class AuthorityRepository(private val repo: R2dbcEntityOperations) {
    companion object {
        @JvmStatic
        private val authorityModel by lazy { Authority::class.java }
    }

    fun findByRole(id: String?) = repo
        .select(authorityModel)
        .matching(
            query(where("role").`is`(id!!))
        ).one()

    fun findAll() = repo.select(authorityModel).all()
}

class UserRepository(
    private val repo: R2dbcEntityOperations
) {
    companion object {
        @JvmStatic
        private val userModel by lazy { User::class.java }

        @JvmStatic
        private val userAuthorityModel by lazy { UserAuthority::class.java }
    }

    fun save(user: User) = repo
        .insert(userModel).using(user)


    fun saveUserAuthority(userId: UUID, authority: String) = repo
        .insert(userAuthorityModel)
        .using(UserAuthority(userId, authority))


    //"SELECT * FROM `user` WHERE activation_key = :activationKey"
    fun findOneByActivationKey(activationKey: String) = repo
        .select(userModel)
        .matching(
            query(where("activationKey").`is`(activationKey))
        ).one()

    //"SELECT * FROM `user` WHERE reset_key = :resetKey"
    fun findOneByResetKey(resetKey: String) = repo
        .select(userModel)
        .matching(
            query(where("resetKey").`is`(resetKey))
        ).one()

    //"SELECT * FROM `user` WHERE activated = false
    // AND activation_key IS NOT NULL AND created_date < :dateTime"
    fun findAllByNotActivatedInTime(dateTime: LocalDateTime) = repo
        .select(userModel)
        .matching(
            query(
                where("activated").`is`(false)
                    .and(where("activationKey").isNotNull)
                    .and(where("createdDate").lessThan(dateTime))
            )
        ).all()

    //"SELECT * FROM `user` WHERE LOWER(email) = LOWER(:email)"
    fun findOneByEmailIgnoreCase(email: String) = repo
        .select(userModel)
        .matching(
            query(
                where("email").isEqual(email)
                    .ignoreCase(true)
            )
        ).one()

    //"SELECT * FROM `user` WHERE login = :login"
    fun findOneByLogin(login: String) = repo
        .select(userModel)
        .matching(
            query(
                where("login").isEqual(login)
                    .ignoreCase(true)
            )
        ).one()

    // expected query:
    //"SELECT COUNT(DISTINCT id) FROM `user` WHERE login != :anonymousUser"
    fun countAllByLoginNot(anonymousUser: String) = repo
        .select(userModel)
        //current query:
        //"SELECT COUNT(id) FROM `user` WHERE login != :anonymousUser"
        .matching(
            query(
                where("login")
                    .not(anonymousUser).ignoreCase(true)
            )
        )
        .count()

    //"DELETE FROM user_authority"
    fun deleteAllUserAuthorities() = repo.delete(userAuthorityModel)

    fun findOneWithAuthoritiesByLogin(login: String): Mono<User> {
        return findOneWithAuthoritiesBy("login", login)
    }

    fun findOneWithAuthoritiesByEmailIgnoreCase(email: String): Mono<User> {
        return findOneWithAuthoritiesBy("email", email.toLowerCase())
    }

    private fun findOneWithAuthoritiesBy(
        fieldName: String,
        fieldValue: Any
    ): Mono<User> = repo.databaseClient
        .sql("SELECT * FROM `user` u LEFT JOIN user_authority ua ON u.id=ua.id WHERE u.$fieldName = :$fieldName")
        .bind(fieldName, fieldValue)
        .map { row, metadata ->
            return@map Tuples.of(
                repo.dataAccessStrategy.getRowMapper(userModel).apply(row, metadata),
                Optional.ofNullable(row.get("role", String::class.java))
            )
        }.all()
        .collectList()
        .filter { it.isNotEmpty() }
        .map { l ->
            val user = l[0]?.t1
            user?.authorities = l.asSequence().filter { it.t2.isPresent }
                .map {
                    Authority(it.t2.get())
                }.toMutableSet()
            user
        }

    @Suppress("UNUSED_PARAMETER")
    fun findAllByLoginNot(pageable: Pageable, login: String) = repo
        .select(userModel)
        .matching(
            query(
                where("login")
                    .not(login).ignoreCase(true)
            )
        )
        .all()

    fun delete(user: User) = repo.databaseClient
        .sql("DELETE FROM user_authority WHERE id = :id")
        .bind("id", user.id!!)
        .then()
        .then(
            repo.delete(userModel)
                .matching(query(where("id").`is`(user.id!!)))
                .toMono().then()
        )

    fun findById(id: UUID) = repo
        .select(userModel)
        .matching(
            query(
                where("id").`is`(id)
            )
        ).one()
}