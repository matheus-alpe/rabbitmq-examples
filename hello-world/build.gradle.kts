plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.slf4j:slf4j-api:2.0.17")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    // https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
    implementation("com.rabbitmq:amqp-client:5.25.0")
}

tasks.test {
    useJUnitPlatform()
}