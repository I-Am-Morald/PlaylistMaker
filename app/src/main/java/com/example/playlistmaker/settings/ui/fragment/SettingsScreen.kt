package com.example.playlistmaker.settings.ui.fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.ui.composeItem.ScreenTitle

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle(stringResource(R.string.settings))
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(all = 16.dp)) {
            SettingsScreenRowWithSwitch(
                partTitle = stringResource(R.string.switch_themes),
                viewModel = viewModel
            )
            SettingsScreenRow(
                partTitle = stringResource(R.string.share),
                iconRes = R.drawable.ic_share_24,
                onClickAction = { viewModel.shareApp() }
            )
            SettingsScreenRow(
                partTitle = stringResource(R.string.support),
                iconRes = R.drawable.ic_support_24,
                onClickAction = { viewModel.supportApp() }
            )
            SettingsScreenRow(
                partTitle = stringResource(R.string.terms),
                iconRes = R.drawable.ic_arrow_forward_24,
                onClickAction = { viewModel.termsApp() }
            )
        }
    }
}

@Composable
private fun SettingsScreenRowWithSwitch(
    partTitle: String,
    viewModel: SettingsViewModel
) {
    val isDarkTheme = viewModel.isDarkTheme.collectAsState().value
    Row(
        modifier = Modifier
            .height(61.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = partTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Switch(
                modifier = Modifier.scale(0.8f),
                checked = isDarkTheme,
                onCheckedChange = { viewModel.switchTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedBorderColor = Color.Transparent,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SettingsScreenRow(
    partTitle: String,
    iconRes: Int = 0,
    onClickAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(61.dp)
            .clickable(onClick = onClickAction),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = partTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Portrait")
@Composable
fun SettingScreenPreview() {
    /*SettingsScreen()*/
}
