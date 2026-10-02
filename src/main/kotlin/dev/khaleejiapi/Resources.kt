package dev.khaleejiapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Validation APIs: email, phone, IBAN, VAT/TRN, Emirates ID, Saudi ID
 */
class ValidationResource internal constructor(private val client: KhaleejiAPI) {

    // MARK: - Email

    @Serializable
    data class EmailResult(
        val valid: Boolean,
        val email: String,
        val checks: EmailChecks? = null,
        val suggestion: String? = null,
        val domain: String? = null,
        val deliverabilityScore: Int? = null,
        val provider: EmailProvider? = null,
        val spf: Boolean? = null,
        val dmarc: Boolean? = null,
        val normalized: String? = null,
    )

    @Serializable
    data class EmailChecks(
        val format: Boolean? = null,
        val mx: Boolean? = null,
        val disposable: Boolean? = null,
        val role: Boolean? = null,
        val freeProvider: Boolean? = null,
    )

    @Serializable
    data class EmailProvider(
        val name: String? = null,
        val type: String? = null,
    )

    /** Validate an email address */
    suspend fun validateEmail(email: String): EmailResult {
        return client.get("/email/validate", mapOf("email" to email))
    }

    // MARK: - Phone

    @Serializable
    data class PhoneResult(
        val valid: Boolean,
        val phone: String,
        val formatted: String? = null,
        val type: String? = null,
        val country: PhoneCountry? = null,
        val carrier: PhoneCarrier? = null,
        val formats: PhoneFormats? = null,
        val areaCode: String? = null,
        val areaName: String? = null,
        val portingNote: String? = null,
        val nationalNumber: String? = null,
    )

    @Serializable
    data class PhoneCountry(
        val name: String? = null,
        val code: String? = null,
        val dialCode: String? = null,
    )

    @Serializable
    data class PhoneCarrier(
        val name: String? = null,
        val mcc: String? = null,
        val mnc: String? = null,
    )

    @Serializable
    data class PhoneFormats(
        val e164: String? = null,
        val international: String? = null,
        val local: String? = null,
        val rfc3966: String? = null,
    )

    /** Validate a phone number */
    suspend fun validatePhone(phone: String, country: String? = null): PhoneResult {
        return client.get("/phone/validate", mapOf("phone" to phone, "country" to country))
    }

    // MARK: - IBAN

    @Serializable
    data class IBANResult(
        val valid: Boolean,
        val iban: String,
        val country: String? = null,
        val bankName: String? = null,
        val bankCode: String? = null,
    )

    /** Validate an IBAN */
    suspend fun validateIBAN(iban: String): IBANResult {
        return client.get("/iban/validate", mapOf("iban" to iban))
    }

    // MARK: - VAT/TRN

    @Serializable
    data class VATResult(
        val valid: Boolean,
        val trn: String? = null,
        val tin: String? = null,
        val country: VATCountry? = null,
        val authority: VATAuthority? = null,
        val vatRate: Double? = null,
        val vatRateNote: String? = null,
        val format: String? = null,
        val checkDigitValid: Boolean? = null,
    )

    @Serializable
    data class VATCountry(
        val code: String? = null,
        val name: String? = null,
        val nameAr: String? = null,
    )

    @Serializable
    data class VATAuthority(
        val name: String? = null,
        val nameAr: String? = null,
        val website: String? = null,
    )

    /** Validate a VAT/TRN number */
    suspend fun validateVAT(trn: String, countryCode: String? = null): VATResult {
        return client.get("/vat/validate", mapOf("trn" to trn, "country" to countryCode))
    }

    // MARK: - Emirates ID

    @Serializable
    data class EmiratesIDResult(
        val valid: Boolean,
        val id: String? = null,
        val emiratesId: String? = null,
        val formatted: String? = null,
        val components: EmiratesIDComponents? = null,
        val details: EmiratesIDDetails? = null,
        val authority: EmiratesIDAuthority? = null,
        val message: String? = null,
    )

    @Serializable
    data class EmiratesIDComponents(
        val nationalityCode: String? = null,
        val countryCode: String? = null,
        val birthYear: Int? = null,
        val sequenceNumber: String? = null,
        val checkDigit: Int? = null,
    )

    @Serializable
    data class EmiratesIDDetails(
        val birthYear: Int? = null,
        val estimatedAge: Int? = null,
        val ageRange: String? = null,
        val generation: String? = null,
    )

    @Serializable
    data class EmiratesIDAuthority(
        val name: String? = null,
        val nameAr: String? = null,
        val website: String? = null,
    )

    /** Validate a UAE Emirates ID */
    suspend fun validateEmiratesID(id: String): EmiratesIDResult {
        return client.get("/emirates-id/validate", mapOf("id" to id))
    }

    // MARK: - Saudi ID

    @Serializable
    data class SaudiIDResult(
        val id: String,
        val valid: Boolean,
        val type: String? = null,
        @SerialName("typeAr") val typeAr: String? = null,
        val nationality: String? = null,
        val nationalityAr: String? = null,
        val description: String? = null,
        val descriptionAr: String? = null,
        val details: SaudiIDDetails? = null,
        val authority: SaudiIDAuthority? = null,
        val errors: List<String>? = null,
    )

    @Serializable
    data class SaudiIDDetails(
        val estimatedBirthYearHijri: Int? = null,
        val estimatedBirthYearGregorian: Int? = null,
        val estimatedAge: Int? = null,
        val ageRange: String? = null,
        val generation: String? = null,
        val checkDigit: Int? = null,
    )

    @Serializable
    data class SaudiIDAuthority(
        val name: String? = null,
        val nameAr: String? = null,
        val website: String? = null,
    )

    @Serializable
    data class SaudiIDBatchResult(
        val results: List<SaudiIDResult>,
        val summary: BatchSummary,
    )

    @Serializable
    data class BatchSummary(
        val total: Int,
        val valid: Int,
        val invalid: Int,
    )

    /** Validate a Saudi National ID or Iqama */
    suspend fun validateSaudiID(id: String): SaudiIDResult {
        return client.get("/saudi-id/validate", mapOf("id" to id))
    }

    /** Batch validate Saudi IDs (max 100) */
    suspend fun validateSaudiIDBatch(ids: List<String>): SaudiIDBatchResult {
        @Serializable
        data class Body(val ids: List<String>)
        return client.post("/saudi-id/validate", Body(ids))
    }
}

/**
 * Geolocation APIs: IP lookup, timezone, geocoding
 */
class GeoResource internal constructor(private val client: KhaleejiAPI) {

    @Serializable
    data class IPResult(
        val ip: String,
        val country: String? = null,
        val countryName: String? = null,
        val city: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val isp: String? = null,
    )

    /** Look up IP geolocation data */
    suspend fun ipLookup(ip: String? = null): IPResult {
        return client.get("/ip/lookup", mapOf("ip" to ip))
    }

    @Serializable
    data class TimezoneResult(
        val location: String? = null,
        val timezone: String,
        val utcOffset: String? = null,
        val dstActive: Boolean? = null,
        val currentTime: String? = null,
    )

    /** Get timezone data for a location */
    suspend fun getTimezone(location: String): TimezoneResult {
        return client.get("/timezone", mapOf("location" to location))
    }

    @Serializable
    data class GeocodeResult(
        val results: List<GeocodeItem> = emptyList(),
        val attribution: String? = null,
    )

    @Serializable
    data class GeocodeItem(
        val name: String? = null,
        val nameAr: String? = null,
        val lat: Double,
        val lng: Double,
        val country: String? = null,
        val countryAr: String? = null,
        val countryCode: String? = null,
        val type: String? = null,
        val address: GeocodeAddress? = null,
        val osmId: Long? = null,
        val importance: Double? = null,
        val boundingBox: GeocodeBoundingBox? = null,
    )

    @Serializable
    data class GeocodeAddress(
        val road: String? = null,
        val neighbourhood: String? = null,
        val city: String? = null,
        val state: String? = null,
        val postcode: String? = null,
        val full: String? = null,
    )

    @Serializable
    data class GeocodeBoundingBox(
        val south: Double,
        val north: Double,
        val west: Double,
        val east: Double,
    )

    /** Geocode an address or coordinates */
    suspend fun geocode(q: String, country: String? = null, lang: String? = null): GeocodeResult {
        return client.get("/geocode", mapOf("q" to q, "country" to country, "lang" to lang))
    }
}

/**
 * Finance APIs: exchange rates, VAT calculation, holidays, business days
 */
class FinanceResource internal constructor(private val client: KhaleejiAPI) {

    @Serializable
    data class ExchangeRatesResult(
        val base: String,
        val rates: Map<String, Double>,
        val source: String? = null,
    )

    /** Get exchange rates */
    suspend fun getExchangeRates(base: String = "AED", symbols: List<String>? = null): ExchangeRatesResult {
        return client.get("/exchange/rates", mapOf(
            "base" to base,
            "symbols" to symbols?.joinToString(","),
        ))
    }

    @Serializable
    data class VATCalcResult(
        val country: String,
        val vatRate: Double,
        val inputAmount: Double,
        val baseAmount: Double,
        val vatAmount: Double,
        val totalAmount: Double,
        val currency: String,
    )

    /** Calculate VAT */
    suspend fun calculateVAT(amount: Double, country: String = "AE", inclusive: Boolean = false): VATCalcResult {
        return client.get("/vat/calculate", mapOf(
            "amount" to amount.toString(),
            "country" to country,
            "inclusive" to inclusive.toString(),
        ))
    }

    @Serializable
    data class HolidaysResult(
        val country: String,
        val countryName: String? = null,
        val year: Int,
        val weekends: List<String>? = null,
        val holidays: List<Holiday>,
        val totalDays: Int? = null,
        val availableYears: List<Int>? = null,
        val nextHoliday: NextHoliday? = null,
    )

    @Serializable
    data class Holiday(
        val name: String,
        val nameAr: String? = null,
        val date: String,
        val endDate: String? = null,
        val type: String,
        val sector: String? = null,
        val dayOfWeek: String? = null,
        val daysUntil: Int? = null,
        val isPast: Boolean? = null,
        val note: String? = null,
    )

    @Serializable
    data class NextHoliday(
        val name: String? = null,
        val date: String? = null,
        val daysUntil: Int? = null,
    )

    /** Get public holidays for a GCC country */
    suspend fun getHolidays(
        country: String = "AE",
        year: Int? = null,
        mode: String? = null,
        date: String? = null,
        month: Int? = null,
    ): HolidaysResult {
        return client.get("/holidays", mapOf(
            "country" to country,
            "year" to year?.toString(),
            "mode" to mode,
            "date" to date,
            "month" to month?.toString(),
        ))
    }

    @Serializable
    data class BusinessDaysResult(
        val businessDays: Int? = null,
        val totalDays: Int? = null,
        val isBusinessDay: Boolean? = null,
        val from: String? = null,
        val to: String? = null,
        val resultDate: String? = null,
    )

    /** Calculate business days */
    suspend fun getBusinessDays(
        country: String = "AE",
        date: String? = null,
        from: String? = null,
        to: String? = null,
        add: Int? = null,
    ): BusinessDaysResult {
        return client.get("/business-days", mapOf(
            "country" to country,
            "date" to date,
            "from" to from,
            "to" to to,
            "add" to add?.toString(),
        ))
    }
}

/**
 * Communication APIs: AI-powered translation
 */
class CommunicationResource internal constructor(private val client: KhaleejiAPI) {

    @Serializable
    data class TranslationResult(
        val text: String? = null,
        val translated: String? = null,
        val from: String? = null,
        val to: String? = null,
        val detectedLanguage: String? = null,
    )

    @Serializable
    private data class TranslateBody(
        val text: String,
        val target: String,
        val source: String? = null,
        val formality: String? = null,
        val dialect: String? = null,
    )

    /** Translate text using AI (Google Gemini) */
    suspend fun translate(
        text: String,
        target: String,
        source: String? = null,
        formality: String? = null,
        dialect: String? = null,
    ): TranslationResult {
        return client.post("/translate", TranslateBody(text, target, source, formality, dialect))
    }
}

/**
 * Islamic APIs: Hijri calendar, prayer times, Arabic text processing
 */
class IslamicResource internal constructor(private val client: KhaleejiAPI) {

    @Serializable
    data class HijriResult(
        val gregorian: HijriDate,
        val hijri: HijriDate,
        val direction: String,
    )

    @Serializable
    data class HijriDate(
        val date: String,
        val year: Int,
        val month: Int,
        val day: Int,
        val monthName: String? = null,
        val monthNameAr: String? = null,
        val dayOfWeek: String? = null,
        val dayOfWeekAr: String? = null,
    )

    /** Convert between Gregorian and Hijri calendars */
    suspend fun convertHijri(date: String? = null, hijri: String? = null, today: Boolean = false): HijriResult {
        return client.get("/hijri/convert", mapOf(
            "date" to date,
            "hijri" to hijri,
            "today" to if (today) "true" else null,
        ))
    }

    @Serializable
    data class PrayerTimesResult(
        val location: PrayerLocation? = null,
        val date: String,
        val prayers: Prayers,
        val qibla: Qibla? = null,
        val method: PrayerMethod? = null,
        val school: String? = null,
    )

    @Serializable
    data class PrayerLocation(
        val lat: Double,
        val lng: Double,
        val city: String? = null,
        val country: String? = null,
    )

    @Serializable
    data class Prayers(
        val fajr: String,
        val sunrise: String,
        val dhuhr: String,
        val asr: String,
        val maghrib: String,
        val isha: String,
    )

    @Serializable
    data class Qibla(
        val direction: Double,
        val compass: String? = null,
    )

    @Serializable
    data class PrayerMethod(
        val name: String,
    )

    /** Get prayer times for a location */
    suspend fun getPrayerTimes(
        city: String? = null,
        lat: Double? = null,
        lng: Double? = null,
        date: String? = null,
        method: String = "mwl",
        school: String = "shafi",
    ): PrayerTimesResult {
        return client.get("/prayer-times", mapOf(
            "city" to city,
            "lat" to lat?.toString(),
            "lng" to lng?.toString(),
            "date" to date,
            "method" to method,
            "school" to school,
        ))
    }

    @Serializable
    data class ArabicResult(
        val operation: String,
        val original: String? = null,
        val text: String? = null,
        val result: String? = null,
        val removedCount: Int? = null,
        val count: Int? = null,
        val words: List<String>? = null,
        val score: Double? = null,
        val label: String? = null,
        val script: String? = null,
    )

    @Serializable
    private data class ArabicBody(
        val text: String,
        val operation: String,
        val options: ArabicOptions? = null,
    )

    @Serializable
    private data class ArabicOptions(
        val direction: String? = null,
    )

    /** Process Arabic text */
    suspend fun processArabic(
        text: String,
        operation: String,
        direction: String? = null,
    ): ArabicResult {
        return client.post("/arabic/process", ArabicBody(
            text, operation, direction?.let { ArabicOptions(it) }
        ))
    }
}

/**
 * Utility APIs: weather, QR code, URL shortener, fraud check
 */
class UtilityResource internal constructor(private val client: KhaleejiAPI) {

    @Serializable
    data class WeatherResult(
        val city: String? = null,
        val temperature: Double? = null,
        val humidity: Int? = null,
        val condition: String? = null,
        val windSpeed: Double? = null,
    )

    /** Get weather for a city */
    suspend fun getWeather(city: String): WeatherResult {
        return client.get("/weather", mapOf("city" to city))
    }

    @Serializable
    data class FraudResult(
        val riskScore: Int,
        val riskLevel: String,
        val recommendation: String? = null,
        val signals: List<FraudSignal>? = null,
        val ipIntelligence: FraudIpIntelligence? = null,
        val crossFieldAnalysis: List<FraudCrossField>? = null,
    )

    @Serializable
    data class FraudSignal(
        val field: String? = null,
        val risk: String? = null,
        val score: Int? = null,
        val reason: String? = null,
    )

    @Serializable
    data class FraudIpIntelligence(
        val country: String? = null,
        val isTorExitNode: Boolean? = null,
        val isAnonymousVpn: Boolean? = null,
        val isPublicProxy: Boolean? = null,
        val isHostingProvider: Boolean? = null,
        val isResidentialProxy: Boolean? = null,
        val isp: String? = null,
        val organization: String? = null,
    )

    @Serializable
    data class FraudCrossField(
        val type: String? = null,
        val risk: String? = null,
        val detail: String? = null,
    )

    @Serializable
    private data class FraudBody(
        val ip: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val name: String? = null,
    )

    /** Check for fraud */
    suspend fun fraudCheck(ip: String? = null, email: String? = null, phone: String? = null, name: String? = null): FraudResult {
        return client.post("/fraud/check", FraudBody(ip, email, phone, name))
    }

    @Serializable
    data class ShortenResult(
        val code: String,
        val shortUrl: String,
        val originalUrl: String,
    )

    @Serializable
    private data class ShortenBody(
        val url: String,
        val customCode: String? = null,
    )

    /** Shorten a URL */
    suspend fun shortenURL(url: String, customCode: String? = null): ShortenResult {
        return client.post("/url/shorten", ShortenBody(url, customCode))
    }
}
