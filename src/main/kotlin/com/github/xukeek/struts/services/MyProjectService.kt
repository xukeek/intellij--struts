package com.github.xukeek.struts.services

import com.intellij.openapi.project.Project
import com.github.xukeek.struts.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
