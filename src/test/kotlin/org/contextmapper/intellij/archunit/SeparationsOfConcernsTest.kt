package org.contextmapper.intellij.archunit

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class SeparationsOfConcernsTest {
    private lateinit var importedClasses: JavaClasses

    @BeforeEach
    fun setup() {
        importedClasses = ClassFileImporter().importPackages("org.contextmapper.intellij")
    }

    @Test
    fun testActionsDontDependOnLSP4IJOrLangClasses() {
        val rule =
            classes()
                .that()
                .resideInAPackage("..actions..")
                .should()
                .dependOnClassesThat()
                .resideOutsideOfPackages("..lsp4ij..", "..lang..")

        rule.check(importedClasses)
    }

    @Test
    fun testLangClassesDontDependOnPluginClasses() {
        val rule =
            classes()
                .that()
                .resideInAPackage("..lang..")
                .should()
                .dependOnClassesThat()
                .resideOutsideOfPackage("com.contextmapper.intellij..")

        rule.check(importedClasses)
    }

    @Test
    fun testLsp4IJClassesOnlyDependOnUtilsClasses() {
        val rule =
            classes()
                .that()
                .resideInAPackage("..lsp4ij..")
                .should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("..utils..")
                .orShould()
                .onlyDependOnClassesThat()
                .resideOutsideOfPackage("com.contextmapper.intellij..")

        rule.check(importedClasses)
    }
}
