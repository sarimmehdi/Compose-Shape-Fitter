package com.sarim.arch_unit_test

import com.sarim.example_app_data.dto.settings.SettingsDto
import com.sarim.example_app_data.dto.settings.SettingsDtoSerializer
import com.sarim.example_app_data.dto.shape.ShapeDto
import com.sarim.example_app_data.dto.shape.ShapeDtoSerializer
import com.sarim.example_app_data.repository.SettingsRepositoryImpl
import com.sarim.example_app_data.repository.ShapesRepositoryImpl
import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
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
        SettingsDto::class, SettingsDtoSerializer::class,
        ShapeDto::class, ShapeDtoSerializer::class,
        SettingsRepositoryImpl::class, ShapesRepositoryImpl::class,
    ],
)
internal class DataArchitectureTest {
    @ArchTest
    fun packageDependencyTest(importedClasses: JavaClasses) {
        noClasses()
            .that()
            .resideInAPackage("..*data..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..*presentation..",
                "..*di..",
            ).check(importedClasses)
    }

    @ArchTest
    fun packageContainmentCheck(importedClasses: JavaClasses) {
        classes()
            .that()
            .haveSimpleNameEndingWith("Dto")
            .should()
            .resideInAPackage("..dto..")
            .check(importedClasses)
        classes()
            .that()
            .haveSimpleNameEndingWith("DtoSerializer")
            .should()
            .resideInAPackage("..dto..")
            .check(importedClasses)
        classes()
            .that()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .should()
            .resideInAPackage("..*data.repository..")
            .check(importedClasses)
    }

    @ArchTest
    fun layerChecks(importedClasses: JavaClasses) {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Repository")
            .definedBy("..*data.repository..")
            .whereLayer("Repository")
            .mayNotBeAccessedByAnyLayer()
            .check(importedClasses)
    }

    @ArchTest
    fun inheritanceCheck(importedClasses: JavaClasses) {
        classes()
            .that()
            .implement(
                object : DescribedPredicate<JavaClass>("an interface ending with 'Repository'") {
                    override fun test(input: JavaClass?): Boolean =
                        input != null && input.isInterface && input.simpleName.endsWith("Repository")
                },
            ).should()
            .haveSimpleNameEndingWith("RepositoryImpl")
            .check(importedClasses)
    }
}
