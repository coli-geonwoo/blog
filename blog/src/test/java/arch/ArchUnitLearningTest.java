package arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArchUnitLearningTest {

    @Nested
    class PackageDependencyTest {

        @DisplayName("source 패키지 내의 어떤 클래스도 어떤 foo패키지에 의존하면 안된다")
        @Test
        void sourcePackageTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("arch");

            ArchRule dependencyRule = noClasses()
                    .that()
                    .resideInAPackage("..source..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..foo..");

            dependencyRule.check(importedClasses);
        }

        @DisplayName("foo 패키지는 오직 source.one 패키지에서만 참조되어야 함")
        @Test
        void fooPackageTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("arch");

            ArchRule dependencyRule = classes()
                    .that()
                    .resideInAPackage("..foo..")
                    .should()
                    .onlyHaveDependentClassesThat()
                    .resideInAPackage("..source.one..");

            dependencyRule.check(importedClasses);
        }
    }

    @Nested
    class ClassesDependencyTest {

        @DisplayName("*Bar의 패턴을 가진 클래스는 오직 Bar 클래스에서만 참조되어야 한다.")
        @Test
        void barClassTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("arch");

            ArchRule dependencyRule = classes()
                    .that()
                    .haveNameMatching(".*Bar")
                    .should()
                    .onlyHaveDependentClassesThat()
                    .haveSimpleNameEndingWith("Bar");

            dependencyRule.check(importedClasses);
        }
    }

    @Nested
    class ClassContainedTest {

        @DisplayName("Source로 시작하는 클래스들은 모두 source 패키지에 있어야 한다.")
        @Test
        void sourceClassCotainMentTest() {
            JavaClasses importedClasses = new ClassFileImporter()
                    .importPackages("arch");

            ArchRule containRule = classes()
                    .that()
                    .haveSimpleNameStartingWith("Source")
                    .should()
                    .resideInAPackage("..source..");

            containRule.check(importedClasses);
        }
    }

}
