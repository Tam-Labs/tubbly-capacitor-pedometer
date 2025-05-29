import { WebPlugin } from '@capacitor/core';

import type { CapacitorPedometerPlugin } from './definitions';

export class CapacitorPedometerWeb extends WebPlugin implements CapacitorPedometerPlugin {
  private mockSteps = 0;
  private intervalId: any;

  constructor() {
    super();
    console.log('StepCounterWeb initialized (mock mode)');
  }

  async startCounting(): Promise<void> {
    console.log('Web: startCounting (mock)');
    this.mockSteps = 0; // Reset mock steps on start
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
    // Simulate step changes every 2 seconds
    this.intervalId = setInterval(() => {
      this.mockSteps += Math.floor(Math.random() * 5) + 1; // Add 1-5 steps
      this.notifyListeners('stepCountChange', { count: this.mockSteps });
      console.log('Web: Mock steps updated to', this.mockSteps);
    }, 2000);
  }

  async stopCounting(): Promise<void> {
    console.log('Web: stopCounting (mock)');
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = undefined;
    }
  }

  async getStepCount(): Promise<{ count: number }> {
    console.log('Web: getStepCount (mock)');
    return { count: this.mockSteps };
  }
}
