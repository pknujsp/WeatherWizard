package io.github.pknujsp.weatherwizard.core.resource

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.pknujsp.weatherwizard.core.resource.iconpack.ResizedClear
import kotlin.collections.List as ____KtList

public object IconPack

private var __IconPack: ____KtList<ImageVector>? = null

public val IconPack.IconPack: ____KtList<ImageVector>
  get() {
    if (__IconPack != null) {
      return __IconPack!!
    }
    __IconPack= listOf(ResizedClear)
    return __IconPack!!
  }
