plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.3"
val lwjglNatives = "natives-windows" // change if you're on macOS or Linux

repositories {
    mavenCentral()
}

dependencies {
    // Core LWJGL
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("io.github.spair:imgui-java-binding:1.86.10")
    implementation("io.github.spair:imgui-java-lwjgl3:1.86.10")

    implementation("io.github.spair:imgui-java-binding:1.86.10")
    implementation("io.github.spair:imgui-java-lwjgl3:1.86.10")
    runtimeOnly("io.github.spair:imgui-java-natives-windows:1.86.10")

    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")

    // Optional modules you're now missing
    implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")

    runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}