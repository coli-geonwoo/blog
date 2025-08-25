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

}
