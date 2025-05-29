// src/definitions.ts
export interface CapacitorPedometerPlugin {
  /**
   * Starts counting steps.
   * On iOS, this will start monitoring for pedometer updates.
   * On Android, this will register a sensor listener for the step counter.
   * @returns A Promise that resolves when counting starts, or rejects if there's an error (e.g., permissions).
   */
  startCounting(): Promise<void>;

  /**
   * Stops counting steps.
   * On iOS, this will stop monitoring pedometer updates.
   * On Android, this will unregister the sensor listener.
   * @returns A Promise that resolves when counting stops.
   */
  stopCounting(): Promise<void>;

  /**
   * Gets the current step count since `startCounting()` was called,
   * or since the device was last rebooted/sensor reset on Android,
   * or since the start of the day on iOS (depending on implementation).
   *
   * @returns A Promise that resolves with an object containing the step count.
   */
  getStepCount(): Promise<{ count: number }>;

  // /**
  //  * (Optional) Adds a listener for step count changes.
  //  * This allows for real-time updates without constantly polling.
  //  * @param eventName The name of the event to listen for (e.g., 'stepCountChange').
  //  * @param listenerFunc The callback function to execute when the event fires.
  //  */
  // addListener(
  //   eventName: 'stepCountChange',
  //   listenerFunc: (info: { count: number }) => void,
  // ): Promise<PluginListenerHandle>;

  // /**
  //  * (Optional) Removes a listener.
  //  * @param eventName The name of the event to remove the listener from.
  //  * @param listenerFunc The callback function that was originally registered.
  //  */
  // removeListener(eventName: 'stepCountChange', listenerFunc: (info: { count: number }) => void): Promise<void>;
}
