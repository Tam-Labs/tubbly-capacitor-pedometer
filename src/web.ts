import { WebPlugin } from '@capacitor/core';

import type { CapacitorPedometerPlugin } from './definitions';

export class CapacitorPedometerWeb extends WebPlugin implements CapacitorPedometerPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
