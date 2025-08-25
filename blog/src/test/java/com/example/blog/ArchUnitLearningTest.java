package com.example.blog;

import com.example.blog.service.ExampleService;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

            JavaClass clazz = importedClasses.get(ExampleService.class);
            System.out.print(clazz.getSimpleName());
        }
    }

}
