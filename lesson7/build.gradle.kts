plugins {
    id("buildlogic.kotlin-library-conventions")
}
detekt {
    baseline = file("${project.projectDir}/baseline.xml")
    source = files("src/main/kotlin/ru/tbank/education/school/lesson7/practise")
}
dependencies {
    testImplementation(kotlin("test"))
}