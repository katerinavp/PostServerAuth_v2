package org.example

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.server.cio.EngineMain
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import kotlinx.coroutines.runBlocking
import org.example.repository.PostRepository
import org.example.repository.PostRepositoryInMemoryWithMutexImpl
import org.example.repository.UserRepository
import org.example.repository.UserRepositoryInMemoryWithMutexImpl
import org.example.route.RoutingV1
import org.example.service.FileService
import org.example.service.JWTTokenService
import org.example.service.PostService
import org.example.service.UserService
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.naming.ConfigurationException

fun main(args: Array<String>): Unit = EngineMain.main(args) // Движок отчечающий за работу

// Тут конфигурируется наш сервер. Для конфигурации Ktor использует фичи.
fun Application.module() {

    // Включаем логирование
    install(CallLogging)

    // Механизм, позволяющий автоматически преобразовывать контент
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting() // Включаем форматирование
            serializeNulls()    // Серилизуем поля, по умолчанию gson пропускает данные поля
        }
    }

    // Обрабатывам ошибки
    install(StatusPages) {
        exception<NotImplementedError> {
            call.respond(HttpStatusCode.NotImplemented)
            throw it
        }

        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
            throw it
        }

        // NotFoundException (ошибка 404)
        exception<NotFoundException> {
            call.respond(HttpStatusCode.NotFound)
            throw it
        }

        // ParameterConversionException (ошибка 400)
        exception<ParameterConversionException> {
            call.respond(HttpStatusCode.BadRequest)
            throw it
        }
    }

    // Внедряем DI
    install(KodeinFeature) {
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("user.upload.dir")?.getString()
            ?: throw ConfigurationException("Upload dir is not specified"))

        bind<PostRepository>() with singleton {
            PostRepositoryInMemoryWithMutexImpl()
        }

        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutexImpl() }

        bind<PostService>() with eagerSingleton { PostService(instance()) }
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
        bind<UserService>() with eagerSingleton {
            UserService(
                repo = instance(),
                tokenService = instance(),
                passwordEncoder = instance()
            ).apply {
                runBlocking {
                    saveNewModel(username = "Test", password = "qwerty")
                }
            }
        }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }

        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }

        bind<RoutingV1>() with eagerSingleton {
            RoutingV1(
                staticPath = instance(tag = "upload-dir"),
                repo = instance(),
                fileService = instance(),
                userService = instance(),
                postService = instance()
            )
        }
    }

    install(Authentication) {
        jwt {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelById(id)
            }
        }
    }

    install(Routing) {

        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}
