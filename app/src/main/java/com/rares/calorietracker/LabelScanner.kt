package com.rares.calorietracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.core.JsonValue
import com.anthropic.errors.AnthropicIoException
import com.anthropic.errors.AnthropicServiceException
import com.anthropic.errors.RateLimitException
import com.anthropic.errors.UnauthorizedException
import com.anthropic.models.messages.Base64ImageSource
import com.anthropic.models.messages.ContentBlockParam
import com.anthropic.models.messages.ImageBlockParam
import com.anthropic.models.messages.JsonOutputFormat
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.OutputConfig
import com.anthropic.models.messages.StopReason
import com.anthropic.models.messages.TextBlockParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream

//values per 100g read from a nutrition label, null = not readable on the photo
data class LabelValues(
    val name: String?,
    val calories: Double?,
    val fat: Double?,
    val carbs: Double?,
    val protein: Double?,
    val salt: Double?
)

class LabelScanException(message: String) : Exception(message)

object LabelScanner {
    private const val MODEL = "claude-haiku-5-5" //reading a few numbers is easy, the small model is fast and cheap
    private const val MAX_IMAGE_SIDE = 1568 //larger images get downscaled by the API anyway

    private const val PROMPT = """This is a photo of a food nutrition label. Read the values per 100g (or per 100ml for drinks).
- If the label only lists values per serving, convert them to per 100g using the serving size printed on the label.
- If the label lists sodium instead of salt, salt = sodium * 2.5.
- Calories in kcal, not kJ.
- Use null for any value that isn't clearly readable on the photo. Never guess a number.
- name: the product name if it is visible on the photo, otherwise null."""

    private val client: AnthropicClient by lazy {
        AnthropicOkHttpClient.builder()
            .apiKey(BuildConfig.ANTHROPIC_API_KEY)
            .build()
    }

    fun isConfigured(): Boolean = BuildConfig.ANTHROPIC_API_KEY.isNotBlank()

    suspend fun scan(context: Context, imageUri: Uri): LabelValues = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            throw LabelScanException("No API key. Add ANTHROPIC_API_KEY to local.properties and rebuild.")
        }
        val base64 = Base64.encodeToString(loadJpeg(context, imageUri), Base64.NO_WRAP)

        val params = MessageCreateParams.builder()
            .model(MODEL)
            .maxTokens(4000L)
            .outputConfig(
                OutputConfig.builder()
                    .effort(OutputConfig.Effort.LOW) //reading a label is simple, low keeps it fast
                    .format(JsonOutputFormat.builder().schema(schema()).build())
                    .build()
            )
            .addUserMessageOfBlockParams(
                listOf(
                    ContentBlockParam.ofImage(
                        ImageBlockParam.builder()
                            .source(
                                Base64ImageSource.builder()
                                    .mediaType(Base64ImageSource.MediaType.IMAGE_JPEG)
                                    .data(base64)
                                    .build()
                            )
                            .build()
                    ),
                    ContentBlockParam.ofText(TextBlockParam.builder().text(PROMPT).build())
                )
            )
            .build()

        val response = try {
            client.messages().create(params)
        } catch (e: UnauthorizedException) {
            throw LabelScanException("The API key is invalid.")
        } catch (e: RateLimitException) {
            throw LabelScanException("Too many requests, try again in a minute.")
        } catch (e: AnthropicServiceException) {
            throw LabelScanException("The AI service returned an error (${e.statusCode()}).")
        } catch (e: AnthropicIoException) {
            throw LabelScanException("No connection to the AI service.")
        }

        if (response.stopReason().orElse(null) == StopReason.REFUSAL) {
            throw LabelScanException("The photo couldn't be processed, try another one.")
        }
        val text = response.content().firstNotNullOfOrNull { block -> block.text().orElse(null)?.text() }
            ?: throw LabelScanException("The AI didn't return any values.")

        val json = JSONObject(text)
        LabelValues(
            name = json.optStringOrNull("name"),
            calories = json.optDoubleOrNull("calories"),
            fat = json.optDoubleOrNull("fat"),
            carbs = json.optDoubleOrNull("carbs"),
            protein = json.optDoubleOrNull("protein"),
            salt = json.optDoubleOrNull("salt")
        )
    }

    private fun schema(): JsonOutputFormat.Schema {
        val nullableNumber = mapOf("anyOf" to listOf(mapOf("type" to "number"), mapOf("type" to "null")))
        val nullableString = mapOf("anyOf" to listOf(mapOf("type" to "string"), mapOf("type" to "null")))
        val fields = listOf("name", "calories", "fat", "carbs", "protein", "salt")
        return JsonOutputFormat.Schema.builder()
            .putAdditionalProperty("type", JsonValue.from("object"))
            .putAdditionalProperty(
                "properties", JsonValue.from(
                    mapOf(
                        "name" to nullableString,
                        "calories" to nullableNumber,
                        "fat" to nullableNumber,
                        "carbs" to nullableNumber,
                        "protein" to nullableNumber,
                        "salt" to nullableNumber
                    )
                )
            )
            .putAdditionalProperty("required", JsonValue.from(fields))
            .putAdditionalProperty("additionalProperties", JsonValue.from(false))
            .build()
    }

    //decodes the photo, downscales it and re-encodes it as jpeg so the upload stays small
    private fun loadJpeg(context: Context, uri: Uri): ByteArray {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //ImageDecoder also applies the camera rotation
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, info, _ ->
                val longest = maxOf(info.size.width, info.size.height)
                if (longest > MAX_IMAGE_SIDE) {
                    val scale = MAX_IMAGE_SIDE.toDouble() / longest
                    decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > MAX_IMAGE_SIDE) sample *= 2
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
            } ?: throw LabelScanException("Couldn't open the photo.")
        }
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        return out.toByteArray()
    }

    private fun JSONObject.optDoubleOrNull(key: String): Double? =
        if (isNull(key)) null else optDouble(key).takeIf { !it.isNaN() }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }
}
