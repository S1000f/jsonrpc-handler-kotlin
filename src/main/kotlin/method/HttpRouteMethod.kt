package method

import dto.Request
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpRouteMethod(name: String, endpoint: String) :
    AbstractRouteMethod(name, endpoint) {

    override fun route(endpoint: String, request: Request, params: Any?): String? {
        val payload = request.toJson() ?: return null

        val url = try {
            URL(endpoint)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val protocol = url.protocol

        val conn = if (protocol == "https") {
            url.openConnection() as HttpsURLConnection
        } else {
            url.openConnection() as HttpURLConnection
        }

        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")
        conn.doOutput = true

        conn.outputStream.use { os ->
            OutputStreamWriter(os).use { writer ->
                BufferedWriter(writer).use { bw ->
                    bw.write(payload, 0, payload.length)
                    bw.flush()
                }
            }
        }

        val builder = StringBuilder()

        conn.inputStream.use { input ->
            InputStreamReader(input).use { reader ->
                BufferedReader(reader).use { br ->
                    var responseLine: String?

                    while (br.readLine().also { responseLine = it } != null) {
                        builder.append(responseLine)
                    }
                }
            }
        }

        return builder.toString()
    }

}