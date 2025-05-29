import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorPedometerPlugin)
public class CapacitorPedometerPlugin: CAPPlugin, CAPBridgedPlugin {
private var pedometer: CMPedometer?
    private var currentStepCount: Int = 0

    // Called when the plugin is initialized.
    override public func load() {
        pedometer = CMPedometer()
    }

    // MARK: - Plugin Methods

    @objc func startCounting(_ call: CAPPluginCall) {
        // Check if pedometer is available on the device
        guard CMPedometer.isPedometerEventTrackingAvailable() else {
            call.reject("Pedometer tracking is not available on this device.")
            return
        }

        // Reset step count when starting
        currentStepCount = 0

        // Start live pedometer updates
        // This will provide updates as steps are taken.
        pedometer?.startUpdates(from: Date()) { [weak self] (pedometerData, error) in
            guard let self = self else { return }

            if let error = error {
                print("Pedometer error: \(error.localizedDescription)")
                // You might want to send this error back to the JS side
                // Using triggerJSEvent for custom events
                self.bridge?.triggerJSEvent(eventName: "stepCountError", target: "document", data: "{ \"message\": \"\(error.localizedDescription)\" }")
                return
            }

            if let data = pedometerData {
                self.currentStepCount = data.numberOfSteps.intValue
                print("Current steps: \(self.currentStepCount)")
                // Emit an event to the JavaScript side with the updated step count
                self.bridge?.triggerJSEvent(eventName: "stepCountChange", target: "document", data: "{ \"count\": \(self.currentStepCount) }")
            }
        }
        call.resolve()
    }

    @objc func stopCounting(_ call: CAPPluginCall) {
        pedometer?.stopUpdates() // Stop live updates
        call.resolve()
    }

    @objc func getStepCount(_ call: CAPPluginCall) {
        // Return the current accumulated step count.
        // Note: For a more robust solution, you might want to query CMPedometer's
        // queryPedometerData(from:to:withHandler:) for a specific time range.
        call.resolve([
            "count": currentStepCount
        ])
    }

    // MARK: - Permissions (Important!)

    // For CoreMotion, you need to add a privacy description to your Info.plist:
    // Key: Privacy - Motion Usage Description
    // Value: Your app needs access to motion data to count your steps.
    //
    // Example Info.plist entry:
    /*
    <key>NSMotionUsageDescription</key>
    <string>Your app needs access to motion data to count your steps.</string>
    */}
