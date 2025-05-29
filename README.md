# capacitor-pedometer

Capacitor pedometer

## Install

```bash
npm install capacitor-pedometer
npx cap sync
```

## API

<docgen-index>

* [`startCounting()`](#startcounting)
* [`stopCounting()`](#stopcounting)
* [`getStepCount()`](#getstepcount)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### startCounting()

```typescript
startCounting() => Promise<void>
```

Starts counting steps.
On iOS, this will start monitoring for pedometer updates.
On Android, this will register a sensor listener for the step counter.

--------------------


### stopCounting()

```typescript
stopCounting() => Promise<void>
```

Stops counting steps.
On iOS, this will stop monitoring pedometer updates.
On Android, this will unregister the sensor listener.

--------------------


### getStepCount()

```typescript
getStepCount() => Promise<{ count: number; }>
```

Gets the current step count since `startCounting()` was called,
or since the device was last rebooted/sensor reset on Android,
or since the start of the day on iOS (depending on implementation).

**Returns:** <code>Promise&lt;{ count: number; }&gt;</code>

--------------------

</docgen-api>
