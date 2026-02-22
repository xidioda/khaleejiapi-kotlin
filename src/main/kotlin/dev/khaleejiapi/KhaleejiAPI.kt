package dev.khaleejiapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Configuration for the KhaleejiAPI client.
 *
 * @param apiKey Your API key from https://khaleejiapi.dev/dashboard/api-keys
 * @param baseUrl Base URL for the API (default: https://khaleejiapi.dev/api/v1)
 * @param timeout Request timeout in milliseconds (default: 30000)
 * @param maxRetries Maximum retry attempts on rate limiting (default: 2)
 */
data class KhaleejiAPIConfig(
    val apiKey: String,
    val baseUrl: String = "https://khaleejiapi.dev/api/v1",
    val timeout: Long = 30_000,
    val maxRetries: Int = 2,
)

/**
 * Standard API response wrapper.
 */
@Serializable
data class ApiResponse<T>(
    val data: T,
    val meta: ResponseMeta? = null,
)

@Serializable
data class ResponseMeta(
    val timestamp: String? = null,
    val cached: Boolean? = null,
)

@Serializable
data class ApiError(
    val error: ApiErrorDetail,
)

@Serializable
data class ApiErrorDetail(
    val code: String,
    val message: String,
)

/**
 * Rate limit information from response headers.
 */
data class RateLimitInfo(
    val limit: Int?,
    val remaining: Int?,
    val reset: Long?,
)

/**
 * Exception thrown by the KhaleejiAPI SDK.
 */
class KhaleejiAPIException(
    val statusCode: Int,
    val errorCode: String,
    override val message: String,
    val rateLimitInfo: RateLimitInfo? = null,
) : Exception(message)

/**
 * Official Kotlin SDK for KhaleejiAPI — the MENA region's developer API platform.
 *
 * ```kotlin
 * val api = KhaleejiAPI("kapi_live_your_key")
 * val result = api.validation.validateEmail("user@example.com")
 * ```
 */
class KhaleejiAPI(private val config: KhaleejiAPIConfig) {

    constructor(apiKey: String) : this(KhaleejiAPIConfig(apiKey = apiKey))

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(this@KhaleejiAPI.json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = config.timeout
            connectTimeoutMillis = 10_000
        }
        defaultRequest {
            header("Authorization", "Bearer ${config.apiKey}")
            header("User-Agent", "khaleejiapi-kotlin/1.0.0")
            contentType(ContentType.Application.Json)
        }
    }

    // Resource namespaces
    val validation = ValidationResource(this)
    val geo = GeoResource(this)
    val finance = FinanceResource(this)
    val communication = CommunicationResource(this)
    val islamic = IslamicResource(this)
    val utility = UtilityResource(this)

    /**
     * Perform a GET request.
     */
    internal suspend inline fun <reified T> get(
        path: String,
        params: Map<String, String?> = emptyMap(),
    ): T {
        return execute {
            httpClient.get("${config.baseUrl}$path") {
                params.forEach { (key, value) ->
                    if (value != null) parameter(key, value)
                }
            }
        }
    }

    /**
     * Perform a POST request.
     */
    internal suspend inline fun <reified T> post(
        path: String,
        body: Any? = null,
    ): T {
        return execute {
            httpClient.post("${config.baseUrl}$path") {
                if (body != null) setBody(body)
            }
        }
    }

    /**
     * Execute a request with retry logic for rate limiting.
     */
    internal suspend inline fun <reified T> execute(
        crossinline block: suspend () -> HttpResponse,
    ): T {
        var lastException: KhaleejiAPIException? = null

        for (attempt in 0..config.maxRetries) {
            if (attempt > 0) {
                val backoff = (1L shl (attempt - 1)) * 1000L
                delay(backoff)
            }

            val response = block()
            val rateLimitInfo = RateLimitInfo(
                limit = response.headers["X-RateLimit-Limit"]?.toIntOrNull(),
                remaining = response.headers["X-RateLimit-Remaining"]?.toIntOrNull(),
                reset = response.headers["X-RateLimit-Reset"]?.toLongOrNull(),
            )

            when (response.status.value) {
                in 200..299 -> {
                    val apiResponse: ApiResponse<T> = response.body()
                    return apiResponse.data
                }
                429 -> {
                    lastException = KhaleejiAPIException(
                        statusCode = 429,
                        errorCode = "RATE_LIMITED",
                        message = "Rate limited. Retry after ${rateLimitInfo.reset ?: 60} seconds",
                        rateLimitInfo = rateLimitInfo,
                    )
                    if (attempt < config.maxRetries) continue
                }
                401 -> throw KhaleejiAPIException(401, "UNAUTHORIZED", "Invalid or missing API key")
                403 -> {
                    val err = tryParseError(response)
                    throw KhaleejiAPIException(403, "FORBIDDEN", err?.error?.message ?: "Forbidden")
                }
                404 -> throw KhaleejiAPIException(404, "NOT_FOUND", "Resource not found")
                else -> {
                    val err = tryParseError(response)
                    throw KhaleejiAPIException(
                        statusCode = response.status.value,
                        errorCode = err?.error?.code ?: "SERVER_ERROR",
                        message = err?.error?.message ?: "Server error (${response.status.value})",
                    )
                }
            }
        }

        throw lastException ?: KhaleejiAPIException(500, "UNKNOWN", "Unknown error")
    }

    private suspend fun tryParseError(response: HttpResponse): ApiError? {
        return try {
            json.decodeFromString<ApiError>(response.bodyAsText())
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Close the HTTP client. Call this when you're done using the SDK.
     */
    fun close() {
        httpClient.close()
    }
}
