# KhaleejiAPI Kotlin SDK

Official Kotlin SDK for [KhaleejiAPI](https://khaleejiapi.dev) — the MENA region's developer API platform.

## Requirements

- Kotlin 2.0+
- JDK 17+
- Android API 26+ (if targeting Android)

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.khaleejiapi:khaleejiapi-sdk:1.0.0")
}
```

### Gradle (Groovy)

```groovy
implementation 'dev.khaleejiapi:khaleejiapi-sdk:1.0.0'
```

### Maven

```xml
<dependency>
    <groupId>dev.khaleejiapi</groupId>
    <artifactId>khaleejiapi-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

```kotlin
import dev.khaleejiapi.KhaleejiAPI

val api = KhaleejiAPI("kapi_live_your_key_here")

// Validate an email
val email = api.validation.validateEmail("user@example.com")
println(email.valid) // true

// Get prayer times
val prayers = api.islamic.getPrayerTimes(city = "Dubai")
println(prayers.prayers.fajr) // "05:12"

// Exchange rates
val rates = api.finance.getExchangeRates(base = "AED", symbols = listOf("USD", "EUR", "SAR"))
println(rates.rates) // {USD=0.2723, EUR=0.2512, SAR=1.0205}
```

## API Reference

### Validation

```kotlin
// Email validation
val result = api.validation.validateEmail("user@example.com")

// Phone validation
val phone = api.validation.validatePhone("+971501234567", country = "AE")

// IBAN validation
val iban = api.validation.validateIBAN("AE070331234567890123456")

// VAT/TRN validation
val vat = api.validation.validateVAT("100123456700003")

// Emirates ID validation
val eid = api.validation.validateEmiratesID("784-1990-1234567-1")

// Saudi ID validation
val sid = api.validation.validateSaudiID("1012345678")

// Saudi ID batch validation (max 100)
val batch = api.validation.validateSaudiIDBatch(listOf("1012345678", "2098765432"))
```

### Geolocation

```kotlin
// IP geolocation
val ip = api.geo.ipLookup("8.8.8.8")

// Timezone lookup
val tz = api.geo.getTimezone("Dubai")

// Geocoding
val geo = api.geo.geocode("Burj Khalifa, Dubai")
```

### Finance

```kotlin
// Exchange rates
val rates = api.finance.getExchangeRates(base = "AED", symbols = listOf("USD", "EUR"))

// VAT calculation
val vat = api.finance.calculateVAT(amount = 100.0, country = "AE")

// Public holidays
val holidays = api.finance.getHolidays(country = "AE", year = 2026)

// Business days
val days = api.finance.getBusinessDays(country = "AE", from = "2026-01-01", to = "2026-01-31")
```

### Communication

```kotlin
// AI Translation (powered by Google Gemini)
val translation = api.communication.translate(
    text = "Hello, world!",
    target = "ar",
    dialect = "gulf"
)
```

### Islamic

```kotlin
// Hijri calendar conversion
val hijri = api.islamic.convertHijri(today = true)

// Prayer times
val prayers = api.islamic.getPrayerTimes(city = "Mecca", method = "umm_al_qura")

// Arabic text processing
val arabic = api.islamic.processArabic(
    text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
    operation = "removeDiacritics"
)
```

### Utility

```kotlin
// Weather
val weather = api.utility.getWeather("Dubai")

// Fraud check
val fraud = api.utility.fraudCheck(email = "test@example.com", ip = "1.2.3.4")

// URL shortener
val short = api.utility.shortenURL("https://example.com/very-long-url")
```

## Configuration

```kotlin
// Simple initialization
val api = KhaleejiAPI("kapi_live_your_key")

// Full configuration
val config = KhaleejiAPIConfig(
    apiKey = "kapi_live_your_key",
    baseUrl = "https://khaleejiapi.dev/api/v1",
    timeout = 30_000,
    maxRetries = 2,
)
val api = KhaleejiAPI(config)
```

## Error Handling

```kotlin
try {
    val result = api.validation.validateEmail("test@example.com")
} catch (e: KhaleejiAPIException) {
    when (e.statusCode) {
        401 -> println("Check your API key")
        429 -> println("Rate limited. Retry after ${e.rateLimitInfo?.reset ?: 60}s")
        400 -> println("Invalid request: ${e.message}")
        else -> println("Error ${e.statusCode}: ${e.message}")
    }
}
```

## Coroutines

All SDK methods are `suspend` functions designed for Kotlin Coroutines:

```kotlin
// In a coroutine scope
launch {
    val result = api.validation.validateEmail("test@example.com")
    println(result.valid)
}

// With structured concurrency
coroutineScope {
    val email = async { api.validation.validateEmail("test@example.com") }
    val phone = async { api.validation.validatePhone("+971501234567") }
    println("Email valid: ${email.await().valid}, Phone valid: ${phone.await().valid}")
}
```

## Android Usage

```kotlin
// In a ViewModel
class MyViewModel : ViewModel() {
    private val api = KhaleejiAPI("kapi_live_your_key")

    fun validateEmail(email: String) {
        viewModelScope.launch {
            try {
                val result = api.validation.validateEmail(email)
                _emailValid.value = result.valid
            } catch (e: KhaleejiAPIException) {
                _error.value = e.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        api.close()
    }
}
```

## Cleanup

Remember to close the client when done:

```kotlin
api.close()
```

Or use `.use {}`:

```kotlin
KhaleejiAPI("your_key").use { api ->
    val result = api.validation.validateEmail("test@example.com")
}
```

## License

MIT — See [LICENSE](LICENSE) for details.

## Links

- [Documentation](https://khaleejiapi.dev/docs)
- [API Reference](https://khaleejiapi.dev/docs/v1)
- [Dashboard](https://khaleejiapi.dev/dashboard)
- [Status](https://khaleejiapi.dev/status)
