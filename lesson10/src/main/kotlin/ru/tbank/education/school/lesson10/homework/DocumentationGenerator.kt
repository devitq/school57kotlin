package ru.tbank.education.school.lesson10.homework

import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object DocumentationGenerator {
    fun generateDoc(obj: Any): String {
        val k = obj::class

        val docClass = k.findAnnotation<DocClass>() ?: return "Нет документации для класса."
        if (k.findAnnotation<InternalApi>() != null) {
            return "Документация скрыта (InternalApi)."
        }

        val ctorParamsByName = k.primaryConstructor?.parameters?.associateBy { it.name } ?: emptyMap()

        val sb = StringBuilder()
        sb.append("=== Документация: ${k.simpleName} ===\n")
        if (docClass.description.isNotBlank()) sb.append("Описание: ${docClass.description}\n")
        if (docClass.author.isNotBlank()) sb.append("Автор: ${docClass.author}\n")
        if (docClass.version.isNotBlank()) sb.append("Версия: ${docClass.version}\n")

        val props = k.memberProperties
            .filter { p ->
                val propHidden = p.findAnnotation<InternalApi>() != null

                val ctorHidden = p.name.let { ctorParamsByName[it]?.findAnnotation<InternalApi>() != null }
                !propHidden && !ctorHidden
            }
            .sortedBy { it.name }

        if (props.isNotEmpty()) {
            sb.append("\n--- Свойства ---\n")
            for (p in props) {
                sb.append("- ${p.name}\n")
                val dp = p.findAnnotation<DocProperty>()
                if (dp != null) {
                    if (dp.description.isNotBlank()) {
                        sb.append("  Описание: ${dp.description}\n")
                    } else {
                        sb.append("  Описание: Не указано\n")
                    }
                    if (dp.example.isNotBlank()) {
                        sb.append("  Пример: ${dp.example}\n")
                    }
                }
            }
        }

        val methodBlacklist = setOf("toString", "equals", "hashCode", "copy")
        val funcs = k.declaredMemberFunctions
            .filter { fn ->
                fn.findAnnotation<InternalApi>() == null &&
                        fn.name !in methodBlacklist &&
                        !fn.name.startsWith("component")
            }
            .sortedBy { it.name }

        if (funcs.isNotEmpty()) {
            sb.append("\n--- Методы ---\n")
            for (f in funcs) {
                val valueParams = f.parameters.filter { it.kind == KParameter.Kind.VALUE }

                val signatureParams = valueParams.joinToString(", ") { param ->
                    val pname = param.name ?: "<arg>"
                    val ptype = param.type.toString().removePrefix("kotlin.")
                    "$pname: $ptype"
                }
                sb.append("- ${f.name}($signatureParams)\n")

                val dm = f.findAnnotation<DocMethod>()
                if (dm != null && dm.description.isNotBlank()) {
                    sb.append("  Описание: ${dm.description}\n")
                }

                if (valueParams.isNotEmpty()) {
                    sb.append("  Параметры:\n")
                    for (param in valueParams) {
                        val pname = param.name ?: "<arg>"
                        val panno = param.findAnnotation<DocParam>()
                        val pdesc = if (panno != null && panno.description.isNotBlank()) {
                            panno.description
                        } else {
                            "Нет описания"
                        }
                        sb.append("    - $pname: $pdesc\n")
                    }
                }

                val returnsDesc = if (dm != null && dm.returns.isNotBlank()) dm.returns else "Нет описания"
                sb.append("  Возвращает: $returnsDesc\n")
            }
        }

        return sb.toString().trimEnd()
    }
}
