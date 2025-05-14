package com.bianca.clock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bianca.clock.R
import com.bianca.clock.ui.theme.WorkClockTheme

@Composable
fun QuoteBanner(quote: String) {
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    val fontName = GoogleFont("Lobster Two")

    val fontFamily = FontFamily(
        Font(
            googleFont = fontName,
            fontProvider = provider,
            weight = FontWeight.Bold,
            style = FontStyle.Italic
        )
    )



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF8DC), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = quote, fontSize = 16.sp, fontFamily = fontFamily)
    }
}

@Composable
@Preview
fun QuoteBannerPreview() {
    WorkClockTheme {
        QuoteBanner(quote = "保持微笑，專注就會自然發生。")
    }
}