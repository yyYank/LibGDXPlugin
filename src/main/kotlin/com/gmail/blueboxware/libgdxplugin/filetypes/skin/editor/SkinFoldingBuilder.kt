package com.gmail.blueboxware.libgdxplugin.filetypes.skin.editor

import com.gmail.blueboxware.libgdxplugin.filetypes.skin.SkinElementTypes
import com.gmail.blueboxware.libgdxplugin.filetypes.skin.psi.SkinClassSpecification
import com.gmail.blueboxware.libgdxplugin.filetypes.skin.psi.SkinPsiUtil
import com.gmail.blueboxware.libgdxplugin.filetypes.skin.psi.SkinResource
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange

/*
 *
 * Adapted from https://github.com/JetBrains/intellij-community/tree/ab08c979a5826bf293ae03cd67463941b0066eb8/json
 *
 */
class SkinFoldingBuilder : FoldingBuilder, DumbAware {

  override fun getPlaceholderText(node: ASTNode): String? {

    val type = node.elementType

    if (type == SkinElementTypes.OBJECT) {
      return "{...}"
    } else if (type == SkinElementTypes.ARRAY) {
      return "[...]"
    } else if (type == SkinElementTypes.LINE_COMMENT) {
      return "//..."
    } else if (type == SkinElementTypes.BLOCK_COMMENT) {
      return "/*...*/"
    } else if (type == SkinElementTypes.RESOURCE) {
      return "{ " + ((node.psi as? SkinResource)?.getName() ?: "") + " ...}"
    } else if (type == SkinElementTypes.CLASS_SPECIFICATION) {
      return "{ " + ((node.psi as? SkinClassSpecification)?.classNameAsString ?: "") + " ...}"
    }

    return "..."
  }

  override fun buildFoldRegions(node: ASTNode, document: Document): Array<out FoldingDescriptor> {

    val descriptors = mutableListOf<FoldingDescriptor>()

    collectDescriptorsRecursively(node, document, descriptors)

    return descriptors.toTypedArray()

  }

  override fun isCollapsedByDefault(node: ASTNode) = false

  private fun collectDescriptorsRecursively(node: ASTNode, document: Document, descriptors: MutableList<FoldingDescriptor>) {

    val type = node.elementType

    if ((type == SkinElementTypes.OBJECT
            || type == SkinElementTypes.ARRAY
            || type == SkinElementTypes.BLOCK_COMMENT
            || type == SkinElementTypes.CLASS_SPECIFICATION
            || type == SkinElementTypes.RESOURCE
            ) && spansMultipleLines(node, document)) {
      descriptors.add(FoldingDescriptor(node, node.textRange))
    } else if (type == SkinElementTypes.LINE_COMMENT) {
      val firstNode = SkinPsiUtil.findFurthestSiblingOfSameType(node.psi, false)
      val lastNode = SkinPsiUtil.findFurthestSiblingOfSameType(node.psi, true)
      val start = firstNode.textRange.startOffset
      val end = lastNode.textRange.endOffset
      if (document.getLineNumber(start) != document.getLineNumber(end)) {
        descriptors.add(FoldingDescriptor(node, TextRange(start, end)))
      }
    }

    for (child in node.getChildren(null)) {
      collectDescriptorsRecursively(child, document, descriptors)
    }
  }

  private fun spansMultipleLines(node: ASTNode, document: Document): Boolean {
    val range = node.textRange
    return document.getLineNumber(range.startOffset) < document.getLineNumber(range.endOffset)
  }

}