package com.yourcompany.appname

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** نقطة انطلاق Hilt لحقن التبعيات في كامل التطبيق */
@HiltAndroidApp
class ZakyAIApp : Application()
