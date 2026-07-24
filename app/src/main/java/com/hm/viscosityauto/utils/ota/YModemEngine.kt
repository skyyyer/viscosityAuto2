package com.hm.viscosityauto.utils.ota

import android.util.Log
import com.hm.viscosityauto.utils.SerialManager
import kotlinx.coroutines.delay
class YModemEngine {

    companion object {
        private const val SOH = 0x01
        private const val STX = 0x02
        private const val PACKET_128 = 128
        private const val PACKET_1024 = 1024
        private const val FILL = 0x1A
    }

    private var sequence = 1

    fun reset() {
        sequence = 1
    }

    /**
     * 文件头包
     *
     * data:
     * filename\0filesize\0
     */
    fun createHeaderPacket(
        fileName: String,
        fileSize: Int
    ): ByteArray {

        val data = ByteArray(PACKET_128)

        val nameBytes =
            fileName.toByteArray(Charsets.US_ASCII)

        val sizeBytes =
            fileSize.toString()
                .toByteArray(Charsets.US_ASCII)

        var index = 0

        System.arraycopy(
            nameBytes,
            0,
            data,
            index,
            nameBytes.size
        )

        index += nameBytes.size

        data[index++] = 0x00

        System.arraycopy(
            sizeBytes,
            0,
            data,
            index,
            sizeBytes.size
        )

        index += sizeBytes.size

        data[index] = 0x00

        return createPacket(
            SOH,
            0,
            data
        )
    }


    /**
     * 创建1024数据包
     *
     * 最后一包不足1024
     * 使用0x1A填充
     */
    fun createDataPacket(
        firmware: ByteArray,
        offset: Int
    ): ByteArray {

        val data =
            ByteArray(PACKET_1024) {
                FILL.toByte()
            }

        val remain =
            firmware.size - offset

        val length =
            minOf(
                PACKET_1024,
                remain
            )

        if (length > 0) {
            System.arraycopy(
                firmware,
                offset,
                data,
                0,
                length
            )
        }

        val packet =
            createPacket(
                STX,
                sequence,
                data
            )

        sequence++

        if (sequence > 0xFF) {
            sequence = 0
        }

        return packet
    }


    /**
     * 空结束包
     *
     * SOH
     * 00
     * FF
     * 128*00
     * CRC16
     */
    fun createEmptyPacket(): ByteArray {

        val data =
            ByteArray(PACKET_128) {
                0x00
            }

        return createPacket(
            SOH,
            0,
            data
        )
    }


    /**
     * 生成YMODEM包
     *
     * type:
     * SOH=128
     * STX=1024
     */
    private fun createPacket(
        type: Int,
        seq: Int,
        data: ByteArray
    ): ByteArray {

        val packet =
            ByteArray(
                3 + data.size + 2
            )

        val number =
            seq and 0xFF

        packet[0] =
            type.toByte()

        packet[1] =
            number.toByte()

        packet[2] =
            (number xor 0xFF).toByte()


        System.arraycopy(
            data,
            0,
            packet,
            3,
            data.size
        )


        val crc =
            Crc16Xmodem.calc(data)


        packet[packet.size - 2] =
            (crc shr 8).toByte()

        packet[packet.size - 1] =
            (crc and 0xFF).toByte()


        return packet
    }
}


/**
 * CRC16-CCITT/XMODEM
 *
 * polynomial:
 * 0x1021
 *
 * initial:
 * 0x0000
 */
object Crc16Xmodem {

    fun calc(
        data: ByteArray
    ): Int {

        var crc = 0x0000

        for (b in data) {

            crc =
                crc xor
                        ((b.toInt() and 0xFF) shl 8)


            repeat(8) {

                crc =
                    if ((crc and 0x8000) != 0) {

                        (crc shl 1) xor 0x1021

                    } else {

                        crc shl 1
                    }

                crc =
                    crc and 0xFFFF
            }
        }

        return crc
    }
}