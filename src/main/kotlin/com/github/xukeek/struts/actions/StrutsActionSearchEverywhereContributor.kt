package com.github.xukeek.struts.actions

import com.github.xukeek.struts.services.MyProjectService
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor
import com.intellij.ide.actions.searcheverywhere.RecentFilesSEContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.util.ProgressIndicatorUtils
import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.util.Processor
import org.jetbrains.annotations.NotNull
import java.util.stream.Collectors


class StrutsActionSearchEverywhereContributor(event: @NotNull AnActionEvent) : RecentFilesSEContributor(event) {

    override fun fetchWeightedElements(pattern: String, progressIndicator: ProgressIndicator, consumer: Processor<in FoundItemDescriptor<Any>>) {
        if (myProject == null) return

        val projectService: MyProjectService = myProject.getService(MyProjectService::class.java)

        val searchString = filterControlSymbols(pattern)
        val preferStartMatches = !searchString.startsWith("*")
        val matcher = createMatcher(searchString, preferStartMatches)
        val history = projectService.getActionConfigs().stream().map { c -> c.xmlFile.virtualFile }.collect(Collectors.toList())

        val res: List<FoundItemDescriptor<Any>> = ArrayList()
        ProgressIndicatorUtils.yieldToPendingWriteActions()

        ProgressIndicatorUtils.runInReadActionWithWriteActionPriority(Test(), progressIndicator)
    }

    private fun createMatcher(searchString: String, preferStartMatches: Boolean): MinusculeMatcher? {
        var builder = NameUtil.buildMatcher("*$searchString")
        if (preferStartMatches) {
            builder = builder.preferringStartMatches()
        }
        return builder.build()
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return StrutsActionSearchEverywhereContributor(initEvent)
        }
    }

    class Test : Runnable {
        override fun run() {

        }
    }
}