package com.donrobo.fpbg

import net.sf.json.JSONObject
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.zip.InflaterInputStream


fun main(args: Array<String>) {
    val blueprints = File("src/main/resources/")

    Arrays.stream(blueprints.listFiles()).filter { f -> f.isFile && f.name.toLowerCase().endsWith(".bp") }.forEach { file ->
        try {
            FileInputStream(file).use { inStream ->
                var string = IOUtils.toString(inStream, "UTF-8")

                val name = file.name.substring(0, file.name.length - 3)

                string = string.substring(1, string.length)
                val bytes = Base64.getDecoder().decode(string)
                InflaterInputStream(ByteArrayInputStream(bytes)).use { decompressionStream ->
                    val array = IOUtils.toByteArray(decompressionStream)
                    val inflatedString = String(array)
                    val json = JSONObject.fromObject(inflatedString)
                    FileUtils.writeStringToFile(File(String.format("src/main/resources/%s.json", name)), json.toString(), "UTF-8")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
