package com.freetmp.dolphin.dependency.manager.artifact

import org.eclipse.aether.transfer.AbstractTransferListener
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferResource
import java.io.PrintStream
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
      for((key,value) in downloads)
        append("${getStatus(key.getContentLength(),value)}  ")

      append()
    }
    out.println(sb)
  }

  override fun transferStarted(event: TransferEvent?) {
    super.transferStarted(event)
  }

  override fun transferFailed(event: TransferEvent?) {
    super.transferFailed(event)
  }

  override fun transferCorrupted(event: TransferEvent?) {
    super.transferCorrupted(event)
  }

  override fun transferSucceeded(event: TransferEvent?) {
    super.transferSucceeded(event)
  }

  fun StringBuffer.pad(spaces:Int){

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