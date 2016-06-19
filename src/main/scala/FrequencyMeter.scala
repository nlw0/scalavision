class FrequencyMeter(damping: Long = 16L) {
  var period = 100000L
  var lastTime = System.nanoTime()

  def update(time: Long): Unit = {
    period += (time - lastTime - period) / damping
    lastTime = time
  }

  def tick() = update(System.nanoTime())

  override def toString = f"${1e9 / period}%.1f"
}
