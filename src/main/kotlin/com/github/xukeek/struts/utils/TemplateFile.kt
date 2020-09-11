package com.github.xukeek.struts.utils

import com.intellij.psi.PsiFile
import java.util.regex.Pattern

class TemplateFile(file: PsiFile) : Comparable<TemplateFile> {
    val psiFile = file
    private val paths: Array<String> = file.virtualFile.path.split("views").toTypedArray()
    private val moduleNamePattern: Pattern = Pattern.compile("[/\\\\][a-z]+-[a-z]+[/\\\\]")
    private val matcherPrefix = moduleNamePattern.matcher(paths[0])
    private val matcherSuffix = moduleNamePattern.matcher(paths[1])
    private var fileLocateModuleName: String = ""
    private var logicModuleName: String = ""
    private var relativePath: String = ""
    private val isInJar = !file.isWritable

    init {
        if (matcherPrefix.find()) {
            fileLocateModuleName = matcherPrefix.group(0).replace("[/\\\\]".toRegex(), "")
        }
        if (matcherSuffix.find()) {
            logicModuleName = matcherSuffix.group(0).replace("[/\\\\]".toRegex(), "")
            relativePath = paths[1].substring(matcherSuffix.end())
        }
        else {
            logicModuleName = fileLocateModuleName
            relativePath = paths[1]
        }
    }

    override fun compareTo(other: TemplateFile): Int {
        if (fileLocateModuleName.endsWith("resource")) {
            if (!other.fileLocateModuleName.endsWith("resource")) {
                return -1
            } else if (!this.isInJar && other.isInJar) {
                return -1
            } else if (this.isInJar && !other.isInJar) {
                return 1
            } else if (this.isInJar) {
                return 0
            }
        } else if (other.fileLocateModuleName.endsWith("resource")) {
            return 1
        }
        return 0
    }
}