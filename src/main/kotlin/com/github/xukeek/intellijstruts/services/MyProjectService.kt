package com.github.xukeek.intellijstruts.services

import com.intellij.openapi.project.Project
import com.github.xukeek.intellijstruts.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
