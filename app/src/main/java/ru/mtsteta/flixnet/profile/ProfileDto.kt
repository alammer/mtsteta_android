package ru.mtsteta.flixnet.profile

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ProfileDto(
    val userName: String? = null,
    val preferables: List<String>? = null,
    val password: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val language: String? = null,
    val darkTheme: Boolean? = false,
    val notification: Boolean? = false,
    val sounds: Boolean? = false,
) : Parcelable
