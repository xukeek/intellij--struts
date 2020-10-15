package com.github.xukeek.struts.wrappers

import com.intellij.psi.xml.XmlFile
import java.util.*

class ActionConfig(val xmlFile: XmlFile, private val packageNameSpace: String, private val name: String, val className: String) {
    private val resultConfigs: MutableList<ResultConfig> = ArrayList()

    fun addResultConfig(resultConfig: ResultConfig) {
        resultConfigs.add(resultConfig)
    }

    fun getResultConfigs(): List<ResultConfig> {
        return resultConfigs
    }

    fun getServletFullPath(): String {
        return "$packageNameSpace/$name"
    }

    fun matchServlet(url: String): Boolean {
        val servletPath = getServletFullPath()
        val pureUrl = if (url.contains("_")) url.substring(0, url.indexOf("_")) else url
        return if (name.contains("_")) {
            servletPath.substring(0, servletPath.indexOf("_")) == pureUrl
        } else {
            pureUrl == servletPath
        }
    }

    data class ResultConfig(val name: String, val type: String, val viewPath: String)
}

