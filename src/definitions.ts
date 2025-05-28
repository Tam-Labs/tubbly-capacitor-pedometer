export interface CapacitorPedometerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
