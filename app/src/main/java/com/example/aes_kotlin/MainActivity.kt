package com.example.aes_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.xor
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private var inputData: EditText? = null
    var doDaiKhoa: EditText? = null
    var keyValue: EditText? = null

    var outputData: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        inputData = findViewById(R.id.input)
        doDaiKhoa = findViewById(R.id.DodaiKhoa)
        keyValue = findViewById(R.id.key)
        outputData = findViewById(R.id.outputData)


    }

    fun btn_Clicked(view: View) {
        var input = inputData!!.text.toString().toByteArray()
        keysize = doDaiKhoa!!.text.toString().toInt()

        //var paddedInput = ByteArray(48) { if (it < input.size) input[it] else 0 }
        // chuyen kieu thanh string de hien thi
        var k = keygeneration()
        var encrypted = encrypt(input, k)
        var encodedData = Base64.encodeToString(encrypted, Base64.DEFAULT)

        val database = Firebase.database
        val myRef = database.getReference("Encrypted data")
        myRef.setValue(encodedData)
        outputData!!.setText(encodedData)

        var keyString = Base64.encodeToString(k, Base64.DEFAULT)
        var myRef1 = database.getReference("Key")
        myRef1.setValue(keyString)

        // var descrypt = decrypt(encrypted, k)
        //  var ttt=String(descrypt)
        keyValue!!.setText(keyString)


    }

    var keysize = 0
    var Nb = 0
    private var Nk = 0
    var Nr = 0
    var w: Array<ByteArray> = Array(60) { ByteArray(4) }
    var t1 = 0.0
    var t2 = 0.0

    val sbox = intArrayOf(
        0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F,
        0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76, 0xCA, 0x82,
        0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C,
        0xA4, 0x72, 0xC0, 0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC,
        0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15, 0x04, 0xC7, 0x23,
        0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27,
        0xB2, 0x75, 0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52,
        0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84, 0x53, 0xD1, 0x00, 0xED,
        0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58,
        0xCF, 0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9,
        0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8, 0x51, 0xA3, 0x40, 0x8F, 0x92,
        0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
        0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E,
        0x3D, 0x64, 0x5D, 0x19, 0x73, 0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A,
        0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB, 0xE0,
        0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62,
        0x91, 0x95, 0xE4, 0x79, 0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E,
        0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08, 0xBA, 0x78,
        0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B,
        0xBD, 0x8B, 0x8A, 0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E,
        0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E, 0xE1, 0xF8, 0x98,
        0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55,
        0x28, 0xDF, 0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41,
        0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16
    )
    private val inv_sbox = intArrayOf(
        0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5,
        0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB, 0x7C, 0xE3,
        0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4,
        0xDE, 0xE9, 0xCB, 0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D,
        0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E, 0x08, 0x2E, 0xA1,
        0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B,
        0xD1, 0x25, 0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4,
        0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92, 0x6C, 0x70, 0x48, 0x50,
        0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D,
        0x84, 0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4,
        0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06, 0xD0, 0x2C, 0x1E, 0x8F, 0xCA,
        0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
        0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF,
        0xCE, 0xF0, 0xB4, 0xE6, 0x73, 0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD,
        0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E, 0x47,
        0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E,
        0xAA, 0x18, 0xBE, 0x1B, 0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79,
        0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4, 0x1F, 0xDD,
        0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27,
        0x80, 0xEC, 0x5F, 0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D,
        0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF, 0xA0, 0xE0, 0x3B,
        0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53,
        0x99, 0x61, 0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1,
        0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D
    )
    private val Rcon = intArrayOf(
        0x8d,
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80,
        0x1b,
        0x36,
        0x6c,
        0xd8,
        0xab,
        0x4d,
        0x9a,
        0x2f,
        0x5e,
        0xbc,
        0x63,
        0xc6,
        0x97,
        0x35,
        0x6a,
        0xd4,
        0xb3,
        0x7d,
        0xfa,
        0xef,
        0xc5,
        0x91,
        0x39,
        0x72,
        0xe4,
        0xd3,
        0xbd,
        0x61,
        0xc2,
        0x9f,
        0x25,
        0x4a,
        0x94,
        0x33,
        0x66,
        0xcc,
        0x83,
        0x1d,
        0x3a,
        0x74,
        0xe8,
        0xcb,
        0x8d,
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80,
        0x1b,
        0x36,
        0x6c,
        0xd8,
        0xab,
        0x4d,
        0x9a,
        0x2f,
        0x5e,
        0xbc,
        0x63,
        0xc6,
        0x97,
        0x35,
        0x6a,
        0xd4,
        0xb3,
        0x7d,
        0xfa,
        0xef,
        0xc5,
        0x91,
        0x39,
        0x72,
        0xe4,
        0xd3,
        0xbd,
        0x61,
        0xc2,
        0x9f,
        0x25,
        0x4a,
        0x94,
        0x33,
        0x66,
        0xcc,
        0x83,
        0x1d,
        0x3a,
        0x74,
        0xe8,
        0xcb,
        0x8d,
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80,
        0x1b,
        0x36,
        0x6c,
        0xd8,
        0xab,
        0x4d,
        0x9a,
        0x2f,
        0x5e,
        0xbc,
        0x63,
        0xc6,
        0x97,
        0x35,
        0x6a,
        0xd4,
        0xb3,
        0x7d,
        0xfa,
        0xef,
        0xc5,
        0x91,
        0x39,
        0x72,
        0xe4,
        0xd3,
        0xbd,
        0x61,
        0xc2,
        0x9f,
        0x25,
        0x4a,
        0x94,
        0x33,
        0x66,
        0xcc,
        0x83,
        0x1d,
        0x3a,
        0x74,
        0xe8,
        0xcb,
        0x8d,
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80,
        0x1b,
        0x36,
        0x6c,
        0xd8,
        0xab,
        0x4d,
        0x9a,
        0x2f,
        0x5e,
        0xbc,
        0x63,
        0xc6,
        0x97,
        0x35,
        0x6a,
        0xd4,
        0xb3,
        0x7d,
        0xfa,
        0xef,
        0xc5,
        0x91,
        0x39,
        0x72,
        0xe4,
        0xd3,
        0xbd,
        0x61,
        0xc2,
        0x9f,
        0x25,
        0x4a,
        0x94,
        0x33,
        0x66,
        0xcc,
        0x83,
        0x1d,
        0x3a,
        0x74,
        0xe8,
        0xcb,
        0x8d,
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80,
        0x1b,
        0x36,
        0x6c,
        0xd8,
        0xab,
        0x4d,
        0x9a,
        0x2f,
        0x5e,
        0xbc,
        0x63,
        0xc6,
        0x97,
        0x35,
        0x6a,
        0xd4,
        0xb3,
        0x7d,
        0xfa,
        0xef,
        0xc5,
        0x91,
        0x39,
        0x72,
        0xe4,
        0xd3,
        0xbd,
        0x61,
        0xc2,
        0x9f,
        0x25,
        0x4a,
        0x94,
        0x33,
        0x66,
        0xcc,
        0x83,
        0x1d,
        0x3a,
        0x74,
        0xe8,
        0xcb
    )

    @Throws(Exception::class)
    fun keygeneration(): ByteArray {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(keysize)
        val key = generator.generateKey()
        return key.encoded
    }

    private fun xor_func(a: ByteArray, b: ByteArray): ByteArray {
        val out = ByteArray(a.size)
        for (i in a.indices) {
            out[i] = (a[i] xor b[i]) as Byte
        }
        return out
    }

    fun generateSubkeys(key: ByteArray): Array<ByteArray> {
        val tmp = Array(Nb * (Nr + 1)) {
            ByteArray(
                4
            )
        }
        var i = 0
        while (i < Nk) {
            tmp[i][0] = key[i * 4]
            tmp[i][1] = key[i * 4 + 1]
            tmp[i][2] = key[i * 4 + 2]
            tmp[i][3] = key[i * 4 + 3]
            i++
        }
        i = Nk
        while (i < Nb * (Nr + 1)) {
            var temp = ByteArray(4)
            for (k in 0..3) temp[k] = tmp[i - 1][k]
            if (i % Nk == 0) {
                temp = SubWord(rotateWord(temp))
                temp[0] = (temp[0] xor (Rcon[i / Nk] and 0xff).toByte())
            } else if (Nk > 6 && i % Nk == 4) {
                temp = SubWord(temp)
            }
            tmp[i] = xor_func(tmp[i - Nk], temp)
            i++
        }
        return tmp
    }


    private fun SubWord(word: ByteArray): ByteArray {
        val result = ByteArray(4)
        for (i in 0 until 4) {
            val index = (word[i].toInt() and 0xFF)
            result[i] = sbox[index].toByte()
        }
        return result
    }


    private fun rotateWord(input: ByteArray): ByteArray {
        val tmp = ByteArray(input.size)
        tmp[0] = input[1]
        tmp[1] = input[2]
        tmp[2] = input[3]
        tmp[3] = input[0]
        return tmp
    }

    private fun AddRoundKey(
        state: Array<ByteArray>,
        w: Array<ByteArray>,
        round: Int
    ): Array<ByteArray> {
        val tmp = Array(state.size) {
            ByteArray(
                state[0].size
            )
        }
        for (c in 0 until Nb) {
            for (l in 0..3) tmp[l][c] = (state[l][c] xor w[round * Nb + c][l]) as Byte
        }
        return tmp
    }

    private fun SubBytes(state: Array<ByteArray>): Array<ByteArray> {
        val tmp = Array(state.size) { ByteArray(state[0].size) }

        for (row in state.indices) {
            for (col in state[row].indices) {
                val index = (state[row][col].toInt() and 0xff)
                tmp[row][col] = sbox[index].toByte()
            }
        }

        return tmp
    }

    private fun InvSubBytes(state: Array<ByteArray>): Array<ByteArray> {
        for (row in 0 until 4) {
            for (col in 0 until Nb) {
                val index = state[row][col].toInt() and 0xff
                state[row][col] = inv_sbox[index].toByte()
            }
        }
        return state
    }


    private fun ShiftRows(state: Array<ByteArray>): Array<ByteArray> {
        val t = ByteArray(4)
        for (r in 1..3) {
            for (c in 0 until Nb) t[c] = state[r][(c + r) % Nb]
            for (c in 0 until Nb) state[r][c] = t[c]
        }
        return state
    }

    private fun InvShiftRows(state: Array<ByteArray>): Array<ByteArray> {
        val t = ByteArray(4)
        for (r in 1..3) {
            for (c in 0 until Nb) t[(c + r) % Nb] = state[r][c]
            for (c in 0 until Nb) state[r][c] = t[c]
        }
        return state
    }

    private fun InvMixColumns(s: Array<ByteArray>): Array<ByteArray> {
        val sp = IntArray(4)
        val b02 = 0x0e.toByte()
        val b03 = 0x0b.toByte()
        val b04 = 0x0d.toByte()
        val b05 = 0x09.toByte()
        for (c in 0..3) {
            sp[0] = (FFMul(b02, s[0][c]) xor FFMul(b03, s[1][c]) xor FFMul(b04, s[2][c]) xor FFMul(
                b05,
                s[3][c]
            )).toInt()
            sp[1] = (FFMul(b05, s[0][c]) xor FFMul(b02, s[1][c]) xor FFMul(b03, s[2][c]) xor FFMul(
                b04,
                s[3][c]
            )).toInt()
            sp[2] = (FFMul(b04, s[0][c]) xor FFMul(b05, s[1][c]) xor FFMul(b02, s[2][c]) xor FFMul(
                b03,
                s[3][c]
            )).toInt()
            sp[3] = (FFMul(b03, s[0][c]) xor FFMul(b04, s[1][c]) xor FFMul(b05, s[2][c]) xor FFMul(
                b02,
                s[3][c]
            )).toInt()
            for (i in 0..3) s[i][c] = sp[i].toByte()
        }
        return s
    }

    private fun MixColumns(s: Array<ByteArray>): Array<ByteArray> {
        val sp = IntArray(4)
        val b02 = 0x02.toByte()
        val b03 = 0x03.toByte()
        for (c in 0..3) {
            sp[0] = (FFMul(b02, s[0][c]) xor FFMul(b03, s[1][c]) xor s[2][c] xor s[3][c]).toInt()
            sp[1] = (s[0][c] xor FFMul(b02, s[1][c]) xor FFMul(b03, s[2][c]) xor s[3][c]).toInt()
            sp[2] = (s[0][c] xor s[1][c] xor FFMul(b02, s[2][c]) xor FFMul(b03, s[3][c])).toInt()
            sp[3] = (FFMul(b03, s[0][c]) xor s[1][c] xor s[2][c] xor FFMul(b02, s[3][c])).toInt()
            for (i in 0..3) s[i][c] = sp[i].toByte()
        }
        return s
    }

    fun FFMul(a: Byte, b: Byte): Byte {
        var aa = a
        var bb = b
        var r: Byte = 0
        var t: Byte
        while (aa.toInt() != 0) {
            if (aa.toInt() and 1 != 0) r = (r xor bb) as Byte
            t = (bb.toInt() and 0x80).toByte()
            bb = (bb.toInt() shl 1).toByte()
            if (t.toInt() != 0) bb = (bb xor 0x1b) as Byte
            aa = ((aa and 0xff.toByte()).toInt() shr 1).toByte()
        }
        return r
    }

    fun encryptBloc(`in`: ByteArray): ByteArray {
        val tmp = ByteArray(`in`.size)
        var state = Array(4) {
            ByteArray(
                Nb
            )
        }
        for (i in `in`.indices) state[i / 4][i % 4] = `in`[i % 4 * 4 + i / 4]
        state = AddRoundKey(state, w, 0)
        for (round in 1 until Nr) {
            state = SubBytes(state)
            state = ShiftRows(state)
            state = MixColumns(state)
            state = AddRoundKey(state, w, round)
        }
        state = SubBytes(state)
        state = ShiftRows(state)
        state = AddRoundKey(state, w, Nr)
        for (i in tmp.indices) tmp[i % 4 * 4 + i / 4] = state[i / 4][i % 4]
        return tmp
    }

    fun decryptBloc(`in`: ByteArray): ByteArray {
        val tmp = ByteArray(`in`.size)
        var state = Array(4) {
            ByteArray(
                Nb
            )
        }
        for (i in `in`.indices) state[i / 4][i % 4] = `in`[i % 4 * 4 + i / 4]
        state = AddRoundKey(state, w, Nr)
        for (round in Nr - 1 downTo 1) {
            state = InvSubBytes(state)
            state = InvShiftRows(state)
            state = AddRoundKey(state, w, round)
            state = InvMixColumns(state)
        }
        state = InvSubBytes(state)
        state = InvShiftRows(state)
        state = AddRoundKey(state, w, 0)
        for (i in tmp.indices) tmp[i % 4 * 4 + i / 4] = state[i / 4][i % 4]
        return tmp
    }

    fun encrypt(`in`: ByteArray, key: ByteArray): ByteArray {
        Nb = 4
        Nk = key.size / 4
        Nr = Nk + 6
        var lenght = 0
        var padding = ByteArray(1)
        var i: Int
        lenght = 16 - `in`.size % 16
        padding = ByteArray(lenght)
        padding[0] = 0x80.toByte()
        i = 1
        while (i < lenght) {
            padding[i] = 0
            i++
        }
        val tmp = ByteArray(`in`.size + lenght)
        var bloc = ByteArray(16)
        w = generateSubkeys(key)
        var count = 0
        i = 0
        while (i < `in`.size + lenght) {
            if (i > 0 && i % 16 == 0) {
                bloc = encryptBloc(bloc)
                System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
            }
            if (i < `in`.size) bloc[i % 16] = `in`[i] else {
                bloc[i % 16] = padding[count % 16]
                count++
            }
            i++
        }
        if (bloc.size == 16) {
            bloc = encryptBloc(bloc)
            System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
        }
        return tmp
    }

    fun decrypt(`in`: ByteArray, key: ByteArray): ByteArray {
        var i: Int
        var tmp = ByteArray(`in`.size)
        var bloc = ByteArray(16)
        Nb = 4
        Nk = key.size / 4
        Nr = Nk + 6
        w = generateSubkeys(key)
        i = 0
        while (i < `in`.size) {
            if (i > 0 && i % 16 == 0) {
                bloc = decryptBloc(bloc)
                System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
            }
            if (i < `in`.size) bloc[i % 16] = `in`[i]
            i++
        }
        bloc = decryptBloc(bloc)
        System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
        tmp = deletePadding(tmp)
        return tmp
    }

    private fun deletePadding(input: ByteArray): ByteArray {
        var count = 0
        var i = input.size - 1
        while (input[i].toInt() == 0) {
            count++
            i--
        }
        val tmp = ByteArray(input.size - count - 1)
        System.arraycopy(input, 0, tmp, 0, tmp.size)
        return tmp
    }

    fun encrypt(`in`: ByteArray, key: ByteArray, n: Int): ByteArray {


        t1 = System.nanoTime().toDouble()
        Nb = 4
        Nk = key.size / 4
        Nr = n
        var lenght = 0
        var padding = ByteArray(1)
        var i: Int
        lenght = 16 - `in`.size % 16
        padding = ByteArray(lenght)
        padding[0] = 0x80.toByte()
        i = 1
        while (i < lenght) {
            padding[i] = 0
            i++
        }
        val tmp = ByteArray(`in`.size + lenght)
        var bloc = ByteArray(16)
        w = generateSubkeys(key)
        var count = 0
        i = 0
        while (i < `in`.size + lenght) {
            if (i > 0 && i % 16 == 0) {
                bloc = encryptBloc(bloc)
                System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
            }
            if (i < `in`.size) bloc[i % 16] = `in`[i] else {
                bloc[i % 16] = padding[count % 16]
                count++
            }
            i++
        }
        if (bloc.size == 16) {
            bloc = encryptBloc(bloc)
            System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
        }
        t2 = System.nanoTime().toDouble()
        return tmp
    }

    fun decrypt(`in`: ByteArray, key: ByteArray, n: Int): ByteArray {
        t1 = System.nanoTime().toDouble()
        var i: Int
        var tmp = ByteArray(`in`.size)
        var bloc = ByteArray(16)
        Nb = 4
        Nk = key.size / 4
        Nr = n
        w = generateSubkeys(key)
        i = 0
        while (i < `in`.size) {
            if (i > 0 && i % 16 == 0) {
                bloc = decryptBloc(bloc)
                System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
            }
            if (i < `in`.size) bloc[i % 16] = `in`[i]
            i++
        }
        bloc = decryptBloc(bloc)
        System.arraycopy(bloc, 0, tmp, i - 16, bloc.size)
        tmp = deletePadding(tmp)
        t2 = System.nanoTime().toDouble()
        return tmp
    }
}