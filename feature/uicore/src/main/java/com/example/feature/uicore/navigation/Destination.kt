package com.example.feature.uicore.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
open class Destination(val routeName: String, val icon: AppIcon) : NavKey
