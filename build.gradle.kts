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
val use_github_packages = (project.findProperty("gpr.use") as String? ?: "false").toBoolean();
val gh_username = project.findProperty("gpr.username") as String? ?: System.getenv("GITHUB_ACTOR");
val gh_token = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN");

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

	if (use_github_packages) {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/Zekromaster/*") // Github Package
			credentials {
				username = gh_username
				password = gh_token
			}
		}
	}

	maven(uri("https://jitpack.io"))

	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.glasslauncher:bin:${yarn_mappings}")
	modImplementation("babric:fabric-loader:${loader_version}")

	implementation("org.slf4j:slf4j-api:1.8.0-beta4")
	implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.2")

	if (use_github_packages) {
		modImplementation(include("net.zekromaster.minecraft.sheepeatgrass:sheepeatgrass-api:0.1.0") as Any)
	} else {
		modImplementation(include("com.github.Zekromaster:SheepEatGrass.API:0.1.0") as Any)
	}
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

if (use_github_packages) {
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
					username = gh_username
					password = gh_token
				}
			}
		}
	}
}
