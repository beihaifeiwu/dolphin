package com.freetmp.dolphin.dependency.manager.artifact

import org.eclipse.aether.transfer.AbstractTransferListener
import org.eclipse.aether.transfer.MetadataNotFoundException
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferResource
import java.io.PrintStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by LiuPin on 2015/6/9.
 */
public class ConsoleTransferListener(var out: PrintStream = System.out) : AbstractTransferListener() {

  val downloads = ConcurrentHashMap<TransferResource, Long>()
  var lastLength: Int = 0

  override fun transferInitiated(event: TransferEvent?) {
    val message = if (event!!.getRequestType() == TransferEvent.RequestType.PUT) "Uploading" else "Downloading"
    out.println("$message: ${event.getResource()?.getRepositoryUrl()}${event.getResource()?.getResourceName()}")
  }

  override fun transferProgressed(event: TransferEvent?) {
    val resource = event!!.getResource()
    downloads.put(resource, event.getTransferredBytes())
    val sb = StringBuilder {
      for ((key, value) in downloads)
        append("${getStatus(key.getContentLength(), value)}  ")

      pad(lastLength - length())
      append('\r')
    }
    out.print(sb)
  }

  override fun transferStarted(event: TransferEvent?) {
    super.transferStarted(event)
  }

  override fun transferFailed(event: TransferEvent?) {
    transferCompleted(event!!)
    if ( event.getException() is MetadataNotFoundException)
      event.getException().printStackTrace(out)
  }

  override fun transferCorrupted(event: TransferEvent?) {
    event?.getException()?.printStackTrace()
  }

  override fun transferSucceeded(event: TransferEvent?) {
    transferCompleted(event!!)
    val resource = event.getResource()
    val contentLength = event.getTransferredBytes()
    if (contentLength >= 0) {
      val type = if (event.getRequestType() == TransferEvent.RequestType.PUT) "Uploaded" else "Downloaded"
      val len = if (contentLength >= 1024) "${toKB(contentLength)} KB" else "$contentLength B}"
      var throughput = ""
      val duration = System.currentTimeMillis() - resource.getTransferStartTime()
      if (duration > 0) {
        val kbPerSec = ((contentLength - resource.getResumeOffset()) / 1024.0) / (duration / 1000.0)
        throughput = " at ${DecimalFormat("0.0", DecimalFormatSymbols(Locale.ENGLISH)).format(kbPerSec)} KB/sec"
      }
      out.println("$type: ${resource.getRepositoryUrl()}${resource.getResourceName()} ($len$throughput)")
    }
  }

  fun transferCompleted(event: TransferEvent) {
    downloads.remove(event.getResource())
    out.print(StringBuilder {
      pad(lastLength)
      append('\r')
    })
  }

  fun StringBuilder.pad(spaces: Int): StringBuilder {
    var block = "                                        "
    var mutable = spaces
    while ( mutable > 0) {
      val n = Math.min(spaces, block.length())
      append(block, 0, n)
      mutable -= n
    }
    return this
  }

  fun getStatus(complete: Long, total: Long): String {
    when {
      total >= 1024 -> return "${toKB(complete)}/${toKB(total)} KB"
      total >= 0 -> return "$complete/$total B"
      complete >= 1024 -> return "${toKB(complete)} KB"
      else -> return "$complete B"
    }
  }

  fun toKB(bytes: Long): Long {
    return (bytes + 1023) / 1024
  }

}
