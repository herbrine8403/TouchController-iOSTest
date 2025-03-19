package top.fifthlight.combine.animation

interface AnimationSpec {
    val duration: Float
    val tweenType: Int

    companion object Base: AnimationSpec {
        override val duration: Float = 1f
        override val tweenType: Int = 0
    }
}
