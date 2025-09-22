package com.hm.viscosityauto.ui.view

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// 自定义视觉变换隐藏光标
class NoCursorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
    }
}