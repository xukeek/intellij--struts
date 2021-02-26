package com.github.xukeek.struts.services

import com.github.xukeek.struts.MyBundle
import com.github.xukeek.struts.wrappers.ActionConfig
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.OpenSourceUtil

class MyApplicationService {

    init {
        println(MyBundle.message("applicationService"))
    }

    fun findActionAndOpenIt(actionUrl: String) {
        val openedProjects = ProjectManager.getInstance().openProjects
        for (i in openedProjects.size - 1 downTo 0) {
            val mappedProject = openedProjects[i]
            val projectService: MyProjectService = mappedProject.getService(MyProjectService::class.java)
            val moduleActionConfigs: List<ActionConfig> = projectService.getActionConfigs()
            for (moduleActionConfig in moduleActionConfigs) {
                if (moduleActionConfig.matchServlet(actionUrl)) {
                    navigate2ClassFile(mappedProject, moduleActionConfig)
                }
            }
        }
    }

    private fun navigate2ClassFile(project: Project, actionConfig: ActionConfig) {
        ApplicationManager.getApplication().invokeLater {
            val actionClassFile = JavaPsiFacade.getInstance(project)
                .findClass(actionConfig.className, GlobalSearchScope.projectScope(project))
            if (actionClassFile != null) {
                OpenSourceUtil.navigate(false, actionClassFile)
            }
        }
    }
}
