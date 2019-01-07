import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

// Prevents shared subproject dependencies from being included in the common jar itself.
// Without this, each subproject that included shared subproject, would include each shared dependency twice.
val bootJar: BootJar by tasks
bootJar.enabled = false
val jar: Jar by tasks
jar.enabled = true
