package com.hm.viscosityauto.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.textColorBlue
import java.util.Calendar

/**
 * 时间选择
 */
@Composable
fun TimerPickerView(
    calendar: Calendar = Calendar.getInstance(),
    onConfirm: (Int,Int,Int,Int,Int,Int,) -> Unit,
    onCancel: () -> Unit = {}
) {

    var year by remember {
        mutableIntStateOf(calendar.get(Calendar.YEAR))
    }
    var month by remember {
        mutableIntStateOf(calendar.get(Calendar.MONTH) + 1)
    }
    var day by remember {
        mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH))
    }


    var hour by remember {
        mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY))
    }
    var minute by remember {
        mutableIntStateOf(calendar.get(Calendar.MINUTE))
    }
    var second by remember {
        mutableIntStateOf(calendar.get(Calendar.SECOND))
    }

    var daysOfMonth by remember {
        mutableIntStateOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    }


    Column(
        modifier = Modifier
            .width(500.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.time_edit),
            style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            Alignment.Center
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //  年
                ColumnPicker(
                    modifier = Modifier.width(60.dp),
                    value = year,
                    label = { "${it}" },
                    range = 1920..2060,
                    onValueChange = {
                        year = it
                    }
                )
                Text(
                    text = "-",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue)
                )

                //  月
                ColumnPicker(
                    modifier = Modifier.width(40.dp),
                    value = month,
                    label = { if (it >= 10) it.toString() else "0$it" },
                    range = 1..12,
                    onValueChange = {
                        month = it
                        calendar.set(Calendar.MONTH, it - 1)
                        daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        Log.e("month", "$month   $daysOfMonth")
                    }
                )
                Text(
                    text = "-",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue)
                )

                //  日
                if (day > daysOfMonth) day = daysOfMonth
                ColumnPicker(
                    modifier = Modifier.width(40.dp),
                    value = day,
                    label = { if (it >= 10) it.toString() else "0$it" },
                    range = 1..daysOfMonth,
                    onValueChange = {
                        day = it
                    }
                )

                Spacer(modifier = Modifier.width(50.dp))


                //  时
                ColumnPicker(
                    modifier = Modifier.width(50.dp),
                    value = hour,
                    label = { if (it >= 10) it.toString() else "0$it" },
                    range = 0..23,
                    onValueChange = {
                        hour = it
                    }
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue)
                )
                //  分
                ColumnPicker(
                    modifier = Modifier.width(50.dp),
                    value = minute,
                    label = { if (it >= 10) it.toString() else "0$it" },
                    range = 0..59,
                    onValueChange = {
                        minute = it
                    }
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue)
                )
                //  秒
                ColumnPicker(
                    modifier = Modifier.width(50.dp),
                    value = second,
                    label = { if (it >= 10) it.toString() else "0$it" },
                    range = 0..59,
                    onValueChange = {
                        second = it
                    }
                )

            }

            // 中间两道横线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .align(Alignment.Center)
            ) {
                Divider(Modifier.padding(horizontal = 15.dp))
                Divider(
                    Modifier
                        .padding(horizontal = 15.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            BaseButton(
                title = stringResource(id = R.string.cancel),
                isNegativeStyle = true
            ) {
                onCancel()
            }
            Spacer(modifier = Modifier.width(20.dp))
            BaseButton(title = stringResource(id = R.string.confirm)) {
                onConfirm(year, month, day, hour, minute, second)
            }

        }

    }
}

