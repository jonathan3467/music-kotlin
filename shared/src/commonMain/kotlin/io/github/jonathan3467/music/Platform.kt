package io.github.jonathan3467.music

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform