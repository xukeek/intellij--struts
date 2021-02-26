package com.github.xukeek.struts.extensions

import com.github.xukeek.struts.services.MyApplicationService
import com.google.gson.stream.JsonWriter
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.util.io.BufferExposingByteArrayOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.QueryStringDecoder
import org.apache.commons.lang.StringUtils
import org.jetbrains.ide.RestService
import java.io.IOException
import java.io.OutputStream

class ChromeRestService : RestService() {

    override fun execute(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): String? {
        val byteOut = BufferExposingByteArrayOutputStream()
        val parameters = urlDecoder.parameters()
        val parameterAction = parameters["action"]
        var actionUrl = ""
        if (parameterAction != null && parameterAction.size == 1) {
            actionUrl = getRequestURIFromURL(parameterAction[0])
        }
        if (StringUtils.isNotEmpty(actionUrl)) {
            findActionAndOpenFile(byteOut, actionUrl)
            Notifications.Bus.notify(
                Notification(
                    "Find Action",
                    "Request to find action",
                    actionUrl,
                    NotificationType.INFORMATION
                )
            )
        }
        send(byteOut, request, context)
        return null
    }

    override fun isHostTrusted(request: FullHttpRequest, urlDecoder: QueryStringDecoder): Boolean {
        return true;
    }

    override fun getServiceName(): String {
        return "find struts action"
    }

    @Throws(IOException::class)
    private fun findActionAndOpenFile(out: OutputStream, url: String) {
        val writer: JsonWriter = createJsonWriter(out)
        writer.beginObject()
        val strutsService: MyApplicationService = ServiceManager.getService(MyApplicationService::class.java)
        strutsService.findActionAndOpenIt(url)
        writer.name("info").value("success")
        writer.endObject()
        writer.close()
    }

    private fun getRequestURIFromURL(url: String): String {
        val begin = url.indexOf("/action")
        val end = url.indexOf("?")
        if (begin > 0 && end < 0) {
            return url.substring(begin)
        } else if (begin > 0 && end > 0) {
            return url.substring(begin, end)
        }
        return url
    }
}