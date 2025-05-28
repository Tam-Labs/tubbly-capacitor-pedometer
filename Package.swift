// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPedometer",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorPedometer",
            targets: ["CapacitorPedometerPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorPedometerPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorPedometerPlugin"),
        .testTarget(
            name: "CapacitorPedometerPluginTests",
            dependencies: ["CapacitorPedometerPlugin"],
            path: "ios/Tests/CapacitorPedometerPluginTests")
    ]
)