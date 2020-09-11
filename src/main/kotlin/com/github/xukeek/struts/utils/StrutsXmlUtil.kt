package com.github.xukeek.struts.utils

import com.github.xukeek.struts.wrappers.ActionConfig
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomElement
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.NotNull
import java.util.*

object StrutsXmlUtil {
    internal interface Struts : DomElement {
        val packages: List<Package?>?
    }

    internal interface Package : DomElement {
        val actions: List<Action?>?
    }

    internal interface Action : DomElement {
        val results: List<Result?>?
    }

    internal interface Result : DomElement {
        val name: String?
        val value: String?
    }

    fun buildConfigs(xmlFile: XmlFile): List<ActionConfig> {
        val result: MutableList<ActionConfig> = ArrayList()
        val document = xmlFile.document
        if (document != null) {
            val strutsRootTag = document.rootTag
            if (strutsRootTag != null) {
                val packageTags = strutsRootTag.findSubTags("package")
                for (packageTag in packageTags) {
                    val actionTags = packageTag.findSubTags("action")
                    for (actionTag in actionTags) {
                        val nameSpace = packageTag.getAttribute("namespace")?.value
                        val name = actionTag.getAttribute("name")?.value
                        val classStr = actionTag.getAttribute("class")?.value
                        if (nameSpace != null && name != null && classStr != null) {
                            val config = ActionConfig(nameSpace, name, classStr)
                            val resultTags = actionTag.findSubTags("result")
                            for (resultTag in resultTags) {
                                val resultName = resultTag.getAttribute("name")?.value
                                val resultType = resultTag.getAttribute("type")?.value
                                if (resultName != null && resultType != null) {
                                    config.addResultConfig(ActionConfig.ResultConfig(resultName, resultType, resultTag.value.trimmedText))
                                }
                            }
                            result.add(config)
                        }
                    }
                }
            }
        }
        return result
    }

    fun getActionViewFiles(@NotNull project: Project, filePath: List<String>): List<PsiFile> {
        val result: MutableList<PsiFile> = ArrayList()
        for (s in filePath) {
            if (StringUtils.isNotEmpty(s)) {
                val filePaths = s.split("[/\\\\]").toTypedArray()
                val fileName = filePaths[filePaths.size - 1]
                val fileRelativeModulePath = s.substring(filePaths[0].length)
                val fileRelativeModule = findFileRelativeModule(filePaths)
                if (StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(fileRelativeModule)) {
                    val files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project))
                    val f: TemplateFile? = files.filter { f ->
                        val path = f.virtualFile.path
                        path.contains(fileRelativeModulePath) && path.contains(fileRelativeModule!!)
                    }.map { f -> TemplateFile(f) }.min()!!
                    if (f != null) result.add(f.psiFile)
                }
            }
        }
        return result
    }

    private fun findFileRelativeModule(filePaths: Array<String>): String? {
        return if (filePaths[0].startsWith("{") && filePaths[0].endsWith("}")) {
            filePaths[0].substring(1, filePaths[0].length - 1)
        } else null
    }
}