package com.example.rpg.frame

/**
 * Starts and stops a frame producer that feeds images into PoseAnalyzer.
 */
interface FrameSource : AutoCloseable {
    fun start()

    fun stop()

    override fun close() {
        stop()
    }
}
