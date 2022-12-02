/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.ryandens.jlink

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A simple functional test for the 'com.ryandens.jlink-application-run' plugin.
 */
class JlinkApplicationRunPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }

    @Test fun `can run with custom jre`() {
        setupProject("\"Hello World\"", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(File(projectDir, "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("Hello World"))
    }

    @Test fun `can run with custom jre and configuration cache`() {
        setupProject("\"Hello World\"", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("--configuration-cache", "run")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(File(projectDir, "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("Hello World"))
        assertTrue(result.output.contains("Configuration cache entry stored."))

        val ccResult = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("--configuration-cache", "run")
            .withProjectDir(projectDir).build()

        // verify the configuration cache is used
        assertTrue(ccResult.output.contains("Reusing configuration cache."))
    }

    @Test fun `build fails when using class from module that is not included`() {
        setupProject("java.sql.Statement.class.getName()", "java.base")
        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDir)
        val result = runner.buildAndFail()

        // Verify the result
        assertTrue(File(projectDir, "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("java.lang.NoClassDefFoundError: java/sql/Statement"))
    }

    @Test fun `build succeeds when using class from non-default module that is included`() {
        setupProject("java.sql.Statement.class.getName()", "java.sql")
        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(File(projectDir, "build/jlink-jre/jre/bin/java").exists())
        assertTrue(result.output.contains("java.sql.Statement"))
    }

    @Test fun `build succeeds for distribution when using class from module that is not included`() {
        setupProject("java.sql.Statement.class.getName()", "java.sql")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("installDist", "execStartScript")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(File(projectDir, "build/install/${projectDir.name}/jre/bin/java").exists())
        assertTrue(result.output.contains("java.sql.Statement"))
    }

    @Test fun `build fails for distribution when using class from module that is not included`() {
        setupProject("java.sql.Statement.class.getName()", "java.base")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("installDist", "execStartScript")
        runner.withProjectDir(projectDir)
        val result = runner.buildAndFail()

        // Verify the result
        assertTrue(File(projectDir, "build/install/${projectDir.name}/jre/bin/java").exists())
        assertTrue(result.output.contains("java.lang.NoClassDefFoundError: java/sql/Statement"))
    }

    private fun setupProject(printlnParam: String, module: String) {
        // Setup the test build
        settingsFile.writeText("")
        buildFile.writeText(
            """
  plugins {
      id('application')
      id('com.ryandens.jlink-application')
  }
  
  application {
    mainClass.set("com.ryandens.example.App")
  }
  
  jlinkJre {
    modules = ['$module']
  }
  
  java {
      toolchain {
          languageVersion = JavaLanguageVersion.of(17)
      }
  }
  
  task execStartScript(type: Exec) {
    workingDir '${projectDir.canonicalPath}/build/install/${projectDir.name}/bin/'
    commandLine './${projectDir.name}'
  }
  """
        )

        val file = File(projectDir, "src/main/java/com/ryandens/example/")
        file.mkdirs()
        file.resolve("App.java").writeText(
            """
          package com.ryandens.example;
          
          public final class App {
          
            public static void main(final String[] args) {
              System.out.println($printlnParam);
            }
          
          }
            """.trimIndent()
        )
    }
}
