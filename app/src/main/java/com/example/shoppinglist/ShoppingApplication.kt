package com.example.shoppinglist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom [Application] annotated with [HiltAndroidApp] to bootstrap Hilt's
 * dependency graph for the whole app. Registered in AndroidManifest.xml.
 */
@HiltAndroidApp
class ShoppingApplication : Application()
