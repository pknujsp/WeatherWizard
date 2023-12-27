package io.github.pknujsp.weatherwizard.core.resource

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.pknujsp.weatherwizard.core.resource.myiconpack.ResizedClear
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(ResizedClear)
    return __AllIcons!!
  }
