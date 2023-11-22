package com.ryandens.jlink

import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.util.internal.TextUtil

/**
 * A custom [org.gradle.jvm.application.scripts.TemplateBasedScriptGenerator]
 * that changes the default run behavior so that it maps to the jlink JRE java binary.
 * Also see unixStartScript.txt in src/main/resources
 */
class JlinkAwareUnixStartScriptGenerator : DefaultTemplateBasedStartScriptGenerator(
    TextUtil.getUnixLineSeparator(),
    StartScriptTemplateBindingFactory.unix(),
    utf8ClassPathResource(JlinkAwareUnixStartScriptGenerator::class.java, "unixStartScript.txt"),
)
