package com.looker.droidify.download

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import java.io.File

class KtorDownloader(private val client: HttpClient) : Downloader {
	override suspend fun headCall(url: String): Boolean =
		client.head(url).status == HttpStatusCode.OK

	override suspend fun downloadToFile(
		url: String,
		target: File,
		headers: Map<String, Any?>,
		block: ProgressListener
	): Boolean {
		val cacheFileLength = if (target.exists()) target.length().takeIf { it >= 0 } else 0
		val request = request {
			url(url)
			headers {
				headers.forEach { entry ->
					entry.value?.let { append(entry.key, it.toString()) }
					cacheFileLength?.let { append(HttpHeaders.Range, "bytes=${it}-") }
				}
			}
			onDownload(block)
		}
		return try {
			client.prepareGet(request).execute { response ->
				val channel = response.bodyAsChannel()
				while (!channel.isClosedForRead) {
					val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
					while (!packet.isEmpty) {
						val bytes = packet.readBytes()
						target.appendBytes(bytes)
					}
				}
				true
			}
		} catch (e: Exception) {
			false
		}
	}
}