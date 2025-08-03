package com.sarim.composeshapefittersampleapp.presentation

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(
    packagesOf = [
        DrawingScreenState::class, DrawingScreenToViewModelEvents::class,
        DrawingScreenUseCases::class, DrawingScreenViewModel::class
    ]
)
class PresentationArchitectureTest {

    @ArchTest
    fun packageDependencyTest(importedClasses: JavaClasses) {
        noClasses().that().resideInAPackage("..presentation..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..data..", "..di.."
            )
            .check(importedClasses)
    }

    @ArchTest
    fun classDependencyTest(importedClasses: JavaClasses) {
        classes().that().resideInAPackage("..presentation..")
            .should().onlyHaveDependentClassesThat().resideInAnyPackage(
                "..domain..", "..utils..", "..presentation.."
            )
            .check(importedClasses)
        classes().that().haveSimpleNameEndingWith("ScreenState")
            .should().onlyHaveDependentClassesThat().haveSimpleNameEndingWith("ScreenViewModel")
        classes().that().haveSimpleNameEndingWith("ScreenToViewModelEvents")
            .should().onlyHaveDependentClassesThat().haveSimpleNameEndingWith("ScreenViewModel")
        classes().that().haveSimpleNameEndingWith("ScreenUseCases")
            .should().onlyHaveDependentClassesThat().haveSimpleNameEndingWith("ScreenViewModel")
    }
}