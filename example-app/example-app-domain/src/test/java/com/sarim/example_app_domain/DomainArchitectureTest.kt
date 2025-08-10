package com.sarim.example_app_domain

import com.sarim.example_app_domain.model.Settings
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_domain.repository.SettingsRepository
import com.sarim.example_app_domain.repository.ShapesRepository
import com.sarim.example_app_domain.usecase.GetSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.GetSettingsUseCase
import com.sarim.example_app_domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.UpdateSettingsUseCase
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(
    packagesOf = [
        Settings::class, Shape::class,
        SettingsRepository::class, ShapesRepository::class,
        GetSelectedShapeUseCase::class, GetSettingsUseCase::class,
        UpdateSelectedShapeUseCase::class, UpdateSettingsUseCase::class,
    ],
)
class DomainArchitectureTest {
    @ArchTest
    fun packageDependencyTest(importedClasses: JavaClasses) {
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..presentation..",
                "..data..",
                "..ui..",
                "..di..",
            ).check(importedClasses)
    }

    @ArchTest
    fun classDependencyTest(importedClasses: JavaClasses) {
        classes()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .onlyHaveDependentClassesThat()
            .resideInAnyPackage(
                "..domain..",
                "..utils..",
                "..presentation..",
            ).check(importedClasses)
        classes()
            .that()
            .resideInAPackage("..domain.model..")
            .should()
            .onlyHaveDependentClassesThat()
            .resideInAnyPackage(
                "..domain.repository..",
                "..domain.usecase..",
                "..data.repository..",
                "..data.dto..",
            ).orShould()
            .onlyHaveDependentClassesThat()
            .haveSimpleNameEndingWith("ViewModel")
            .check(importedClasses)
    }

    @ArchTest
    fun packageContainmentCheck(importedClasses: JavaClasses) {
        classes()
            .that()
            .haveSimpleNameEndingWith("UseCase")
            .should()
            .resideInAPackage("..usecase..")
            .check(importedClasses)
        classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .resideInAPackage("..domain.repository..")
            .check(importedClasses)
    }

    @ArchTest
    fun layerChecks(importedClasses: JavaClasses) {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Repository")
            .definedBy("..domain.repository..")
            .layer("Use Case")
            .definedBy("..domain.usecase..")
            .whereLayer("Repository")
            .mayOnlyBeAccessedByLayers("Use Case")
            .check(importedClasses)
    }
}
