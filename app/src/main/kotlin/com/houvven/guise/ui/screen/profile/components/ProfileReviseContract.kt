package com.houvven.guise.ui.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import com.houvven.guise.data.repository.profile.AndroidIdRandomRepo
import com.houvven.guise.data.repository.profile.MobileNetworkTypeRepo
import com.houvven.guise.data.repository.profile.NetworkType
import com.houvven.guise.data.repository.profile.ProfilesPlaceholderRepo
import com.houvven.guise.data.repository.profile.ProfilesSuggestRepo
import com.houvven.guise.data.repository.profile.ProfilesSuggestRepo_Enum
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.profile.item.PropertiesProfile
import org.koin.java.KoinJavaComponent.inject

typealias Profiles = HookProfiles

sealed class ProfileReviseContract {
    open val span = ProfileReviseColumSpan.DEFAULT
}

enum class ProfileReviseColumSpan {
    FULL,
    DEFAULT
}

class ProfileReviseHeader(
    val title: @Composable () -> String
) : ProfileReviseContract() {
    override val span = ProfileReviseColumSpan.FULL
}

sealed class ProfileReviseEditor : ProfileReviseContract() {

    data object None : ProfileReviseEditor()

    sealed class Editor<T> : ProfileReviseEditor() {
        abstract val label: @Composable () -> String
        abstract val value: Profiles.() -> T?
        abstract val onValueClear: Profiles.() -> Profiles

        open val display: (T?) -> String = { it.toString() }
        open val validator: (T?) -> Boolean = { true }

        val placeholder get() = ProfilesPlaceholderRepo.get(value).run(display)

        fun isEdited(profiles: Profiles) = value.invoke(profiles) != null
    }

    class Text(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> String?,
        val onValueChange: Profiles.(String?) -> Profiles,
        val suggestRepo: ProfilesSuggestRepo? = null,
        override val onValueClear: Profiles.() -> Profiles = { onValueChange(null) }
    ) : Editor<String>()

    class TextNumber<T : Number>(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> T?,
        val onValueChange: Profiles.(T?) -> Profiles,
        val stringToNumber: (String) -> T?,
        val suggestRepo: ProfilesSuggestRepo? = null,
        override val onValueClear: Profiles.() -> Profiles = { onValueChange(null) },
    ) : Editor<T>()

    open class Enum<T>(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> T?,
        override val onValueClear: Profiles.() -> Profiles,
        val options: ProfilesSuggestRepo_Enum.() -> List<ProfileSuggest<T>>,
        open val onSelectedChange: Profiles.(ProfileSuggest<T>) -> Profiles,
    ) : Editor<T>()

    class BooleanEnum(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> Boolean?,
        override val onValueClear: Profiles.() -> Profiles,
        override val onSelectedChange: Profiles.(ProfileSuggest<Boolean>) -> Profiles
    ) : Enum<Boolean>(
        label = label,
        value = value,
        onValueClear = onValueClear,
        onSelectedChange = onSelectedChange,
        options = { boolean }
    )
}


private val PropertiesReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.system_properties) },
    // Brand
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.brand) },
        value = { properties.brand },
        onValueChange = { properties { copy(brand = it) } }
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.manufacturer) },
        value = { properties.manufacturer },
        onValueChange = { properties { copy(manufacturer = it) } },
    ),
    // Model
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.model) },
        value = { properties.model },
        onValueChange = { properties { copy(model = it) } },
    ),
    // Device Code
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.device) },
        value = { properties.device },
        onValueChange = { properties { copy(device = it) } },
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.product) },
        value = { properties.product },
        onValueChange = { properties { copy(product = it) } }
    ),
    // Characteristic
    ProfileReviseEditor.Enum(
        label = { stringResource(id = R.string.characteristic) },
        value = { properties.characteristics },
        options = { characteristics },
        onValueClear = { properties { copy(characteristics = null) } },
        onSelectedChange = { properties { copy(characteristics = it.value) } },
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.build_display_id) },
        value = { properties.displayId },
        onValueChange = { properties { copy(displayId = it) } }
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.fingerprint) },
        value = { properties.fingerprint },
        onValueChange = { properties { copy(fingerprint = it) } }
    )
)


private val PackageInfoReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.app_info) },
    // App Version Name
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.version_name) },
        value = { versionName },
        onValueChange = { copy(versionName = it) },
    ),
    // App Version Code
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.version_code) },
        value = { versionCode },
        onValueChange = { copy(versionCode = it) },
        stringToNumber = { it.toIntOrNull() }
    )
)

private val ResourceConfigReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.resource_configuration) },
    ProfileReviseEditor.Enum(
        label = { stringResource(id = R.string.language) },
        value = { language },
        options = { language },
        onValueClear = { copy(language = null) },
        onSelectedChange = { copy(language = it.value) }
    ),
    ProfileReviseEditor.BooleanEnum(
        label = { stringResource(id = R.string.night_mode) },
        value = { nightMode },
        onValueClear = { copy(nightMode = null) },
        onSelectedChange = { copy(nightMode = it.value) }
    ),
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.density_dpi) },
        value = { densityDpi },
        onValueChange = { copy(densityDpi = it) },
        stringToNumber = { it.toIntOrNull() }
    ),
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.font_scale) },
        value = { fontScale },
        onValueChange = { copy(fontScale = it) },
        stringToNumber = { it.toFloatOrNull() }
    )
)

private val LocationReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.location) },
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.longitude) },
        value = { longitude },
        onValueChange = { copy(longitude = it) },
        stringToNumber = { it.toDoubleOrNull() }
    ),
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.latitude) },
        value = { latitude },
        onValueChange = { copy(latitude = it) },
        stringToNumber = { it.toDoubleOrNull() }
    )
)

private val BaseStationReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.base_station) },
    ProfileReviseEditor.TextNumber(
        label = { "Cid" },
        value = { cid },
        onValueChange = { copy(cid = it) },
        stringToNumber = { it.toLongOrNull() }
    ),
    ProfileReviseEditor.TextNumber(
        label = { "Lac/Tac" },
        value = { lac },
        onValueChange = { copy(lac = it) },
        stringToNumber = { it.toIntOrNull() }
    ),
    ProfileReviseEditor.TextNumber(
        label = { "Pci" },
        value = { pci },
        onValueChange = { copy(pci = it) },
        stringToNumber = { it.toIntOrNull() }
    )
)

private val NetworkReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.network_info) },
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.network_type) },
        value = { networkType },
        onValueChange = { copy(networkType = it) },
        stringToNumber = { it.toIntOrNull() },
        suggestRepo = inject<NetworkType>(NetworkType::class.java).value
    ),
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.mobile_network_type) },
        value = { mobileNetType },
        onValueChange = { copy(mobileNetType = it) },
        stringToNumber = { it.toIntOrNull() },
        suggestRepo = MobileNetworkTypeRepo
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.xxxnetwork_type) },
        value = { wifiMac },
        onValueChange = { properties { copy(manufacturer = it) } },
    ),
   ProfileReviseEditor.Text(
            label = { stringResource(id = R.string.wifissidnetwork_type) },
            value = { wifiSsid },
            onValueChange = { properties { copy(manufacturer = it) } },
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.wifiBssidnetwork_type) },
        value = { wifiBssid },
        onValueChange = { properties { copy(manufacturer = it) } },
    )
)

private val IdentityReviseItems = listOf(
    ProfileReviseHeader { stringResource(id = R.string.identity) },
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.ssaid) },
        value = { ssaid },
        onValueChange = { copy(ssaid = it) },
        suggestRepo = AndroidIdRandomRepo
    )
)

val ProfilesReviseItemsDef = listOf(
    PropertiesReviseItems,
    NetworkReviseItems,
    LocationReviseItems,
    BaseStationReviseItems,
    PackageInfoReviseItems,
    ResourceConfigReviseItems,
    IdentityReviseItems
).flatten()

private fun Profiles.properties(function: PropertiesProfile.() -> PropertiesProfile) =
    copy(properties = properties.function())