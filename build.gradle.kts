plugins {
	id("babric-loom") version "1.1.7"
	id("maven-publish")
}

val maven_group: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val archives_base_name: String by project
val next_version: String by project
val artifact_id: String by project


java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	withSourcesJar()
}

group = maven_group
version = next_version

if (!project.hasProperty("releasing")) {
	version = "${version}-SNAPSHOT"
}

tasks.jar {
	archiveBaseName.value(archives_base_name)
}


loom {
	gluedMinecraftJar()
	customMinecraftManifest.set("https://babric.github.io/manifest-polyfill/${property("minecraft_version") as String}.json")
}

repositories {
	maven {
		name = "Babric"
		url = uri("https://maven.glass-launcher.net/babric")
	}
	// Used for mappings.
	maven {
		name = "Glass Releases"
		url = uri("https://maven.glass-launcher.net/releases")
	}
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.glasslauncher:bin:${yarn_mappings}")
	modImplementation("babric:fabric-loader:${loader_version}")

	implementation("org.slf4j:slf4j-api:1.8.0-beta4")
	implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.2")
}

tasks {
	withType<ProcessResources> {
		inputs.property("version", project.version)

		filesMatching("**/fabric.mod.json") {
			expand("version" to project.version)
		}
	}
}

tasks.withType<JavaCompile>().configureEach { options.release = 17 }

tasks.jar {
	from("LICENSE") {
		rename { "${this}_${archives_base_name}"}
	}
}

publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			artifactId = artifact_id
			from(components["java"])
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/Zekromaster/SheepEatGrass") // Github Package
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}