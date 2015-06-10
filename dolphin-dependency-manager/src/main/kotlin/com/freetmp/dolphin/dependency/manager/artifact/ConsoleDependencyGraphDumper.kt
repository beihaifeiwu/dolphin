package com.freetmp.dolphin.dependency.manager.artifact

import org.eclipse.aether.graph.DependencyNode
import org.eclipse.aether.graph.DependencyVisitor
import org.eclipse.aether.util.artifact.ArtifactIdUtils.*
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils.*
import org.eclipse.aether.util.graph.transformer.ConflictResolver
import java.io.PrintStream

/**
 * Created by LiuPin on 2015/6/10.
 */
public class ConsoleDependencyGraphDumper(val out: PrintStream = System.out) : DependencyVisitor {

  val childInfos = arrayListOf<ChildInfo>()

  override fun visitLeave(node: DependencyNode?): Boolean {
    if (childInfos.isNotEmpty()) childInfos.remove(childInfos.size() - 1)
    if (childInfos.isNotEmpty()) childInfos.get(childInfos.size() - 1).index++
    return true
  }

  override fun visitEnter(node: DependencyNode?): Boolean {
    out.println("${formatIndentation()}${formatNode(node!!)}")
    childInfos.add(ChildInfo(node.getChildren().size()))
    return true
  }

  fun formatIndentation(): String {
    return StringBuilder {
      childInfos.forEachIndexed { i, childInfo -> append(childInfo.formatIndentation(i == childInfos.size() - 1)) }
    }.toString()
  }

  fun formatNode(node: DependencyNode): String {
    val a = node.getArtifact()
    val d = node.getDependency()
    return StringBuilder {
      append(a)
      if ( d != null && d.getScope().length() > 0) {
        append(" [${d.getScope()}")
        if (d.isOptional()) append(", optional")
        append("]")
      }
      var premanaged = getPremanagedVersion(node)
      if (premanaged != null && premanaged != a.getBaseVersion()) append(" (version managed from $premanaged)")
      premanaged = getPremanagedScope(node)
      if (premanaged != null && premanaged != d.getScope()) append(" (scope managed from $premanaged)")

      val winner = node.getData().get(ConflictResolver.NODE_DATA_WINNER) as DependencyNode?
      if (winner != null && !equalsBaseId(a, winner.getArtifact())) {
        val w = winner.getArtifact()
        append(" (conflicts with ")
        if (toVersionlessId(a).equals(toVersionlessId(w))) append(w.getVersion())
        else append(w)
        append(")")
      }
    }.toString()
  }
}

class ChildInfo(val count: Int) {
  var index: Int = 0

  fun formatIndentation(end: Boolean): String {
    val last = index + 1 >= count
    if (end)
      return if (last) "\\-" else "+-"
    else
      return if (last) "  " else "| "
  }
}