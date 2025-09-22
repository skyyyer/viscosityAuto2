package com.hm.viscosityauto.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.room.test.TestRecords
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ExportDataUtil {


    fun download(context: Context, usbDrivePath: String, list: List<TestRecords>): Boolean {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet()

        //设置列宽
        sheet.setColumnWidth(0, 2000)
        sheet.setColumnWidth(1, 7000)
        sheet.setColumnWidth(2, 7000)
        sheet.setColumnWidth(3, 7000)
        sheet.setColumnWidth(4, 7000)
        sheet.setColumnWidth(5, 7000)
        sheet.setColumnWidth(6, 7000)
        sheet.setColumnWidth(6, 7000)

        //=================================定义表头属性===============================================
//        val font = wb.createFont() // 生成字体格式设置对象
//        font.fontName = "黑体" // 设置字体黑体
//        font.bold = true // 字体加粗
//        font.fontHeightInPoints = 16.toShort() // 设置字体大小
//        font.color = HSSFFont.COLOR_NORMAL //字体颜色
//        val cellStyle = wb.createCellStyle() // 生成行格式设置对象
//        cellStyle.setBorderBottom(BorderStyle.THIN) // 下边框
//        cellStyle.setBorderLeft(BorderStyle.THIN) // 左边框
//        cellStyle.setBorderRight(BorderStyle.THIN) // 右边框
//        cellStyle.setBorderTop(BorderStyle.THIN) // 上边框
//        cellStyle.setAlignment(HorizontalAlignment.CENTER) // 横向居中对齐
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER) // 纵向居中对齐
//        cellStyle.setFont(font)

        //=================================定义内容属性===============================================
//        val txtContent = wb.createFont() // 生成字体格式设置对象
//        txtContent.fontName = "黑体" // 设置字体黑体
//        txtContent.bold = false // 字体加粗
//        txtContent.fontHeightInPoints = 12.toShort() // 设置字体大小
//        txtContent.color = HSSFFont.COLOR_RED //字体颜色
//        val cellStyleContent = wb.createCellStyle() // 生成行格式设置对象
//        cellStyleContent.wrapText = true
//        cellStyleContent.setBorderBottom(BorderStyle.THIN) // 下边框
//        cellStyleContent.setBorderLeft(BorderStyle.THIN) // 左边框
//        cellStyleContent.setBorderRight(BorderStyle.THIN) // 右边框
//        cellStyleContent.setBorderTop(BorderStyle.THIN) // 上边框
//        cellStyleContent.setAlignment(HorizontalAlignment.CENTER) // 横向居中对齐
//        cellStyleContent.setVerticalAlignment(VerticalAlignment.CENTER) // 纵向居中对齐
//        cellStyleContent.setFont(txtContent)

        //====================================写入数据===============================================
        for (k in 0 until list.size + 1) {
            val row = sheet.createRow(k)
            if (k == 0) {
                val cell0 = row.createCell(0)
                val cell1 = row.createCell(1)
                val cell2 = row.createCell(2)
                val cell3 = row.createCell(3)
                val cell4 = row.createCell(4)
                val cell5 = row.createCell(5)
                val cell6 = row.createCell(6)
                val cell7 = row.createCell(6)

//                cell0.setCellStyle(cellStyle)
//                cell1.setCellStyle(cellStyle)
//                cell2.setCellStyle(cellStyle)
//                cell3.setCellStyle(cellStyle)
//                cell4.setCellStyle(cellStyle)
//                cell5.setCellStyle(cellStyle)
                row.height = 500.toShort()
                cell0.setCellValue(context.getString(R.string.number))
                cell1.setCellValue(context.getString(R.string.duration) + "(s)")
                cell2.setCellValue(context.getString(R.string.temperature) + "(℃)")
                cell3.setCellValue(context.getString(R.string.viscosity_constant) + "(mm²/s²)")
                cell4.setCellValue(context.getString(R.string.viscosity) + "(mm²/s)")
                cell5.setCellValue(context.getString(R.string.test_time))
                cell6.setCellValue(context.getString(R.string.tester))
                cell7.setCellValue(context.getString(R.string.duration_list))

            } else {
                val cell0 = row.createCell(0)
                val cell1 = row.createCell(1)
                val cell2 = row.createCell(2)
                val cell3 = row.createCell(3)
                val cell4 = row.createCell(4)
                val cell5 = row.createCell(5)
                val cell6 = row.createCell(6)
                val cell7 = row.createCell(6)

//                cell0.setCellStyle(cellStyleContent)
//                cell1.setCellStyle(cellStyleContent)
//                cell2.setCellStyle(cellStyleContent)
//                cell3.setCellStyle(cellStyleContent)
//                cell4.setCellStyle(cellStyleContent)
//                cell5.setCellStyle(cellStyleContent)

                row.height = 500.toShort()
                cell0.setCellValue(list[k - 1].testNum)
                cell1.setCellValue(list[k - 1].duration)
                cell2.setCellValue(list[k - 1].temperature)
                cell3.setCellValue(list[k - 1].constant)
                cell4.setCellValue(list[k - 1].viscosity)
                cell5.setCellValue(list[k - 1].date+" "+list[k - 1].time)
                cell6.setCellValue(list[k - 1].tester)
                if (list[k - 1].durationArray.isNotEmpty()) {
                    val lists = Gson().fromJson(
                        list[k - 1].durationArray,
                        Array<DurationModel>::class.java
                    ).toList()

                    var str = ""
                    lists.forEachIndexed { index, it ->
                        if (!it.derelict){
                            str =
                                str + context.getString(R.string.number_start) + (index + 1) + context.getString(
                                    R.string.number_end
                                ) +
                                        "%.2f".format(it.duration) + "(s) \r\n"
//                        it.duration.toString() + "(s)  " + it.temperature + "(℃)\r\n"
                        }
                    }
                    cell7.setCellValue(str)
                } else {
                    cell7.setCellValue("")
                }


            }
        }


        val fileName =
            "/" + context.getString(R.string.export_file_name) + TimeUtils.timestampToCusString() + ".xls"
        val resultPath = Environment.getExternalStorageDirectory().path + fileName
        try {
            val file = File(resultPath)
            val fileOutputStream = FileOutputStream(file)
            wb.write(fileOutputStream)
            fileOutputStream.close()


            return try {
                val fis = FileInputStream(
                    resultPath
                )
                val usbFile = File(usbDrivePath + fileName)
                val fos = FileOutputStream(usbFile)
                var len: Int
                val buff = ByteArray(1024)
                while (fis.read(buff).also { len = it } != -1) {
                    fos.write(buff, 0, len)
                }
                fos.fd.sync()
                fos.close()
                fis.close()
                true
            } catch (e: Exception) {
                Log.e("fxHou1", "Download Fail FileNotFoundException$e")
                e.printStackTrace()
                false
            }


        } catch (e: FileNotFoundException) {
            Log.e("fxHou2", "Download Fail FileNotFoundException$e")
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            Log.e("fxHou3", "Download Fail IOException$e")
            e.printStackTrace()
            return false
        }

    }

}