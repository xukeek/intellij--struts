package com.github.xukeek.struts.actions

import com.intellij.ide.actions.searcheverywhere.ClassSearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.NotNull


class StrutsActionSearchEverywhereContributor(event: @NotNull AnActionEvent) : ClassSearchEverywhereContributor(event) {

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return StrutsActionSearchEverywhereContributor(initEvent)
        }
    }
}