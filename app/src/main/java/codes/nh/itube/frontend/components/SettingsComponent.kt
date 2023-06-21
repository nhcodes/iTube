package codes.nh.itube.frontend.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import codes.nh.itube.R
import codes.nh.itube.backend.setting.Setting
import codes.nh.itube.backend.setting.SettingType


@Composable
fun SettingsComponent(
    settings: Map<String, String>,
    onSettingChange: (key: String, value: String) -> Unit
) {

    val settingsByCategory = remember { Setting.values().groupBy { it.category } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(top = 8.dp)
    ) {

        settingsByCategory.forEach { (category, categorySettings) ->

            Text(
                text = stringResource(id = category),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            categorySettings.forEach { setting ->

                val settingValue = Setting.getSettingValueFromSettingsMap(setting, settings)

                val settingTitle = stringResource(id = setting.title)
                val settingDescription = stringResource(id = setting.description)

                when (setting.type) {
                    is SettingType.Switch -> {
                        SwitchSettingComponent(
                            name = settingTitle,
                            description = settingDescription,
                            currentValue = settingValue.toBoolean(),
                            onValueChange = { newValue ->
                                onSettingChange(setting.name, newValue.toString())
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                    is SettingType.Slider -> {
                        SliderSettingComponent(
                            name = settingTitle,
                            description = settingDescription,
                            valueRange = setting.type.range,
                            step = setting.type.step,
                            currentValue = settingValue.toInt(),
                            onValueChange = { newValue ->
                                onSettingChange(setting.name, newValue.toString())
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                    is SettingType.Select -> {
                        SelectSettingComponent(
                            name = settingTitle,
                            description = settingDescription,
                            options = setting.type.options,
                            currentValue = settingValue,
                            onValueChange = { newValue ->
                                onSettingChange(setting.name, newValue)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }

            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                for (setting in Setting.values()) {
                    onSettingChange(setting.name, setting.type.default.toString())
                }
            }
        ) {
            Text(text = stringResource(id = R.string.settings_button_reset))
        }

    }
}

@Composable
private fun SettingInfoComponent(name: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        if (description.isNotEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.5f)
            )
        }
    }
}

@Composable
private fun SwitchSettingComponent(
    name: String,
    description: String,
    currentValue: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {

        SettingInfoComponent(
            name = name,
            description = description,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = currentValue,
            onCheckedChange = {
                onValueChange(it)
            }
        )

    }
}

@Composable
private fun SliderSettingComponent(
    name: String,
    description: String,
    valueRange: ClosedRange<Int>,
    step: Int,
    currentValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var slideValue by remember(currentValue) { mutableStateOf(currentValue) }

    Column(modifier = modifier) {

        SettingInfoComponent(name = name, description = description)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
                value = slideValue.toFloat(),
                onValueChange = {
                    slideValue = (it / step).toInt() * step
                },
                onValueChangeFinished = {
                    onValueChange(slideValue)
                },
                modifier = Modifier.weight(1f)
            )
            val maxCharacters = valueRange.endInclusive.toString().length
            val valueText = slideValue.toString().padStart(maxCharacters)//fixme
            Text(text = valueText, modifier = Modifier.padding(start = 8.dp))

        }

    }
}

@Composable
private fun SelectSettingComponent(
    name: String,
    description: String,
    options: Array<String>,
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var menuOpen by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {

        SettingInfoComponent(name = name, description = description, modifier = Modifier.weight(1f))

        Button(
            onClick = { menuOpen = true }
        ) {

            Text(text = currentValue)

            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            menuOpen = false
                        }
                    )
                }
            }

        }

    }
}
