package com.example.blog;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class ArchUnitLearningTest {


    @Nested
    class ImportClass {

        @DisplayName("importPackages로 패키지의 클래스들을 모두 가져올 수 있다")
        @Test
        void importPackagesTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            importedClasses.forEach(System.out::println);
        }

        @DisplayName("importPacakges로 특정 경로의 클래스들을 모두 가져올 수 있다")
        @Test
        void importPathTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPath("build/classes/java/main/com/example/blog/service");

            importedClasses.forEach(System.out::println);
        }

        @DisplayName("가져온 클래스들 중 단일 클래스를 로드할 수 있다")
        @Test
        void importClass() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

//            JavaClass clazz = importedClasses.get(ExampleService.class);
//            System.out.print(clazz.getSimpleName());
        }
    }

    @Nested
    class DependencyTest {

        @DisplayName("서비스 패키지의 클래스는 컨트롤러에서만 접근 가능하다")
        @Test
        void dependencyTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            ArchRule dependencyRule = classes()
                    .that()
                    .resideInAPackage("..service..")
                    .should()
                    .onlyBeAccessed()
                    .byAnyPackage("..controller..");

            dependencyRule.check(importedClasses);
        }

        @DisplayName("서비스 패키지의 클래스는 컨트롤러와 서비스 패키지에서만 접근 가능하다")
        @Test
        void dependencyTest2() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            ArchRule dependencyRule = classes()
                    .that()
                    .resideInAPackage("..service..")
                    .should()
                    .onlyBeAccessed()
                    .byAnyPackage("..controller..", "..service..");

            dependencyRule.check(importedClasses);
        }
    }

    @Nested
    class PackageDependencyTest {

        @DisplayName("source 패키지 내의 어떤 클래스도 어떤 foo패키지에 의존하면 안된다")
        @Test
        void sourcePackageTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com");

            ArchRule dependencyRule = noClasses()
                    .that()
                    .resideInAPackage("..source..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..foo..");

            dependencyRule.check(importedClasses);
        }
    }

    @Nested
    class AnnotationTest {

        @DisplayName("service 패키지 내의 서비스 클래스는 클래스 레벨에서 @Transactional이 붙어있어야 한다")
        @Test
        void sourceAnnotationTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            ArchRule annotaionRule = classes()
                    .that()
                    .resideInAPackage("..service..")
                    .should()
                    .beAnnotatedWith(Transactional.class);

            annotaionRule.check(importedClasses);
        }
    }

    @Nested
    class LayerTest {

        @DisplayName("Controller > Service > Repository 의존성이 유지된다")
        @Test
        void layerTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            ArchRule layerRule = layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("MyController").definedBy("..controller..")
                    .layer("MyService").definedBy("..service..")
                    .layer("MyRepository").definedBy("..repository..")

                    .whereLayer("MyController").mayNotBeAccessedByAnyLayer()
                    .whereLayer("MyService").mayOnlyBeAccessedByLayers("MyController")
                    .whereLayer("MyRepository").mayOnlyBeAccessedByLayers("MyService");

            layerRule.check(importedClasses);
        }
    }

    @Nested
    class CycleTest {

        @DisplayName("순환참조가 생기는지 테스트할 수 있다")
        @Test
        void cylcleTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("com.example.blog");

            ArchRule cycleRule = slices()
                    .matching("com.example.blog.(*)..")
                    .should()
                    .beFreeOfCycles();

            cycleRule.check(importedClasses);
        }
    }

}
