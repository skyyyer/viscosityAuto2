package com.hm.viscosityauto.utils.ota

import android.util.Log
import com.hm.viscosityauto.utils.SerialManager

class YModemEngine(
    private val serial: SerialManager
) {

    private val TAG = "YModemEngine"

    private val PACKET_1024 = 1024
    private val PACKET_128 = 128

    // -------------------------
    // Header包
    // -------------------------
    fun sendHeader(fileName: String, fileSize: Int) {

        val name = fileName.toByteArray()
        val size = fileSize.toString().toByteArray()

        val data = ByteArray(PACKET_128) { 0x00 }

        var p = 0

        name.copyInto(data, p)
        p += name.size
        data[p++] = 0x00

        size.copyInto(data, p)
        p += size.size
        data[p] = 0x00

        sendPacket(0x01, 0, data)
    }

    // -------------------------
    // 数据包
    // -------------------------
    fun sendData(firmware: ByteArray): Int {

        var offset = 0
        var index = 1

        Log.e("sYModemEngine  endData",firmware.size.toString())

        while (offset < firmware.size) {

            val end = minOf(offset + PACKET_1024, firmware.size)
            val chunk = firmware.copyOfRange(offset, end)

            val data = ByteArray(PACKET_1024) { 0x1A }
            chunk.copyInto(data)

            sendPacket(0x02, index, data)

            offset = end
            index++
        }

        return index
    }

    // -------------------------
    // 空header（结束）
    // -------------------------
    fun sendEmptyHeader() {

        val data = ByteArray(PACKET_128) { 0x00 }

        sendPacket(0x01, 0, data)
    }

    // -------------------------
    // YMODEM packet
    // -------------------------
    @OptIn(ExperimentalStdlibApi::class)
    private fun sendPacket(type: Int, index: Int, data: ByteArray) {

        val seq = index and 0xFF
        val inv = 0xFF - seq

        val crc = Crc16.calc(data)

        val packet = ByteArray(3 + data.size + 2)

        packet[0] = type.toByte()
        packet[1] = seq.toByte()
        packet[2] = inv.toByte()

        System.arraycopy(data, 0, packet, 3, data.size)

        packet[packet.size - 2] = (crc shr 8).toByte()
        packet[packet.size - 1] = (crc and 0xFF).toByte()

        serial.sendRawDataWithOutHeadFoot(packet.toHexString())
    }
}