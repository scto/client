package com.looker.core.data.utils

import androidx.core.os.LocaleListCompat
import com.looker.core.common.stripBetween
import java.util.Locale

fun localeListCompat(tag: String): LocaleListCompat = LocaleListCompat.forLanguageTags(tag)

/**
 * Find the Localized value from [Map<String,T>] using [localeList]
 *
 * Returns null if none matches or map or [localeList] is empty
 */
fun <T> Map<String, T>?.localizedValue(localeList: LocaleListCompat): T? {
	if (isNullOrEmpty() || localeList.isEmpty) return null
	val suitableLocale = localeList.suitableLocale(keys)
	return get(suitableLocale)
		?: get("en_US")
		?: get("en-US")
		?: get("en")
		?: values.firstOrNull()
}

/**
 * Retrieve the most suitable Locale from the [keys] using [LocaleListCompat]
 *
 * Returns null if none found
 */
@OptIn(ExperimentalStdlibApi::class)
fun LocaleListCompat.suitableLocale(keys: Set<String>): String? = (0..<size())
	.asSequence()
	.mapNotNull { get(it).suitableTag(keys) }
	.firstOrNull()

/**
 * Get the suitable tag for [Locale] from [keys]
 *
 * Returns null if [keys] are empty or [Locale] in null
 */
fun Locale?.suitableTag(keys: Set<String>): String? {
	if (keys.isEmpty()) return null
	val currentLocale = this ?: return null
	val tag = currentLocale.toLanguageTag()
	val soloTag = currentLocale.language
	val strippedTag = tag.stripBetween("-")

	return if (tag in keys) tag
	else if (strippedTag in keys) strippedTag
	else if (soloTag in keys) soloTag
	// try children of the language
	else keys.find { it.startsWith(soloTag) }
}