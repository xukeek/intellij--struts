package com.github.xukeek.struts.xml

import com.github.xukeek.struts.utils.StrutsXmlUtil
import com.github.xukeek.struts.wrappers.ResultXmlTag
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import com.intellij.util.containers.toArray

class ResultFileReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(XmlPatterns.xmlTag().withAttributeValue("type", "template")
                .withLocalName("result").withNamespace("http://struts.apache.org/dtds/struts-2.0.dtd"), ResultFileReferenceProvider())
    }

    inner class ResultFileReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            return arrayOf(ResultFileReference(element as XmlTag))
        }
    }

    internal class ResultFileReference(psiElement: XmlTag) : PsiPolyVariantReferenceBase<XmlTag>(psiElement) {
        override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
            val resultXmlTag = ResultXmlTag(element)
            if (resultXmlTag.valid()) {
                val project = element.project
                //{xxx-system/xxx.ftl}
                if (!resultXmlTag.valid()) {
                    return ResolveResult.EMPTY_ARRAY
                }
                val allMethodReturnFiles: List<PsiFile> = StrutsXmlUtil.getActionViewFiles(project, listOf(resultXmlTag.mappedFilePath))
                val results: MutableList<ResolveResult> = ArrayList(allMethodReturnFiles.size)
                for (returnFile in allMethodReturnFiles) {
                    results.add(PsiElementResolveResult(returnFile))
                }
                return results.toArray(ResolveResult.EMPTY_ARRAY)
            }
            return ResolveResult.EMPTY_ARRAY
        }
    }
}