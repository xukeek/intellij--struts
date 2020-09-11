package com.github.xukeek.struts.wrappers

import java.util.*

class ActionConfig(private val packageNameSpace: String, private val name: String, val className: String) {
    private val resultConfigs: MutableList<ResultConfig> = ArrayList()

    fun addResultConfig(resultConfig: ResultConfig) {
        resultConfigs.add(resultConfig)
    }

    fun getResultConfigs(): List<ResultConfig> {
        return resultConfigs
    }

    fun matchServlet(url: String): Boolean {
        val servletPath = "$packageNameSpace/$name"
        val pureUrl = if (url.contains("_")) url.substring(0, url.indexOf("_")) else url
        return if (name.contains("_")) {
            servletPath.substring(0, servletPath.indexOf("_")) == pureUrl
        } else {
            pureUrl == servletPath
        }
    }

    data class ResultConfig(val name: String, val type: String, val viewPath: String)
}

