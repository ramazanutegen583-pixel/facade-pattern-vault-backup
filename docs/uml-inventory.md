# UML source inventory

Documentation revision: 20 October 2026. Source baseline: commit 1ea6a10.

Generated from Java declarations with the JDK compiler tree API. Production and test Java source files were not changed.

The diagram contains all 9 authored classes, all 21 fields, all 66 methods, and the one explicitly declared constructor. Method signatures include every parameter, return type, and declared throws clause. Java-generated default constructors are not written in the code and are not listed as declarations.

## Class members

| Class | Fields | Methods | Explicit constructors |
| --- | ---: | ---: | ---: |
| HomeTheaterDemo | 0 | 1 | 1 |
| HomeTheaterFacade | 8 | 6 | 0 |
| Amplifier | 3 | 8 | 0 |
| PopcornMaker | 1 | 4 | 0 |
| Projector | 2 | 5 | 0 |
| Screen | 1 | 3 | 0 |
| StreamingPlayer | 3 | 10 | 0 |
| TheaterLights | 1 | 3 | 0 |
| HomeTheaterTest | 2 | 26 | 0 |

## Variables and method details

Fields are class attributes. Parameters appear in method signatures. Local variables are scoped inside method bodies and therefore appear in the register below, rather than as UML class attributes. Initializer expressions, method annotations, source hashes, and line numbers are recorded in code-inventory.json.

### HomeTheaterDemo

Source: `src/main/java/edu/aitu/hometheater/client/HomeTheaterDemo.java`

- `- HomeTheaterDemo()`
- `+ main(args: String[]): void`; static
- Locals in `main`: `HomeTheaterFacade theater` (line 10)

### HomeTheaterFacade

Source: `src/main/java/edu/aitu/hometheater/facade/HomeTheaterFacade.java`

- `- amplifier: Amplifier {readOnly}`; initializer: `new Amplifier()`
- `- player: StreamingPlayer {readOnly}`; initializer: `new StreamingPlayer()`
- `- projector: Projector {readOnly}`; initializer: `new Projector()`
- `- screen: Screen {readOnly}`; initializer: `new Screen()`
- `- lights: TheaterLights {readOnly}`; initializer: `new TheaterLights()`
- `- popcornMaker: PopcornMaker {readOnly}`; initializer: `new PopcornMaker()`
- `- currentMovie: String`
- `- paused: boolean`
- `+ watchMovie(title: String): void`
- `+ pauseMovie(): void`
- `+ resumeMovie(): void`
- `+ endMovie(): void`
- `+ getStatus(): String`
- `- requireSession(): void`
- Locals in `watchMovie`: `String movie` (line 28)

### Amplifier

Source: `src/main/java/edu/aitu/hometheater/subsystem/Amplifier.java`

- `- on: boolean`
- `- volume: int`
- `- surroundSound: boolean`
- `+ turnOn(): void`
- `+ setVolume(volume: int): void`
- `+ enableSurroundSound(): void`
- `+ turnOff(): void`
- `+ isOn(): boolean`
- `+ getVolume(): int`
- `+ isSurroundSoundEnabled(): boolean`
- `- requirePower(): void`

### PopcornMaker

Source: `src/main/java/edu/aitu/hometheater/subsystem/PopcornMaker.java`

- `- on: boolean`
- `+ turnOn(): void`
- `+ pop(): void`
- `+ turnOff(): void`
- `+ isOn(): boolean`

### Projector

Source: `src/main/java/edu/aitu/hometheater/subsystem/Projector.java`

- `- on: boolean`
- `- wideScreen: boolean`
- `+ turnOn(): void`
- `+ enableWideScreen(): void`
- `+ turnOff(): void`
- `+ isOn(): boolean`
- `+ isWideScreen(): boolean`

### Screen

Source: `src/main/java/edu/aitu/hometheater/subsystem/Screen.java`

- `- lowered: boolean`
- `+ lower(): void`
- `+ raise(): void`
- `+ isLowered(): boolean`

### StreamingPlayer

Source: `src/main/java/edu/aitu/hometheater/subsystem/StreamingPlayer.java`

- `- on: boolean`
- `- movie: String`
- `- paused: boolean`
- `+ turnOn(): void`
- `+ play(title: String): void`
- `+ pause(): void`
- `+ resume(): void`
- `+ stop(): void`
- `+ turnOff(): void`
- `+ isOn(): boolean`
- `+ isPlaying(): boolean`
- `+ isPaused(): boolean`
- `- requireMovie(): void`

### TheaterLights

Source: `src/main/java/edu/aitu/hometheater/subsystem/TheaterLights.java`

- `- brightness: int = 100`; initializer: `100`
- `+ dim(brightness: int): void`
- `+ turnOn(): void`
- `+ getBrightness(): int`

### HomeTheaterTest

Source: `src/test/java/edu/aitu/hometheater/HomeTheaterTest.java`

- `- output: ByteArrayOutputStream`
- `- originalOut: PrintStream`
- `~ captureOutput(): void`
- `~ restoreOutput(): void`
- `~ completeSessionReturnsToReady(): void`
- `~ invalidTitleDoesNotActivateDevices(title: String): void`
- `~ titleIsTrimmed(): void`
- `~ anotherMovieCannotReplaceAnActiveSession(): void`
- `~ idleOperationsAreRejectedWithoutDeviceChanges(operation: String): void`
- `~ duplicatePauseAndPrematureResumePreserveState(): void`
- `~ pausedSessionCanEndAndTheSameFacadeCanStartAnother(): void`
- `~ startupOrderConfiguresEquipmentBeforePlayback(): void`
- `~ shutdownStopsPlaybackBeforeTurningDevicesOff(): void`
- `~ pauseAndResumeCoordinateLightingWithPlayback(): void`
- `~ mainProducesTheCompleteEnglishDemoWithoutArguments(): void`
- `~ amplifierCanBeUsedDirectlyAndResetsOnPowerOff(): void`
- `~ invalidVolumeDoesNotChangeThePreviousSetting(volume: int): void`
- `~ lightsAcceptBoundaryValuesAndRestoreFullLighting(brightness: int): void`
- `~ invalidBrightnessPreservesThePreviousSetting(brightness: int): void`
- `~ projectorEnforcesPowerAndResetsItsDisplayMode(): void`
- `~ screenCanBeControlledDirectly(): void`
- `~ popcornMakerRequiresPower(): void`
- `~ playerSupportsItsOwnLifecycleAndRejectsInvalidTransitions(): void`
- `~ playerRejectsBlankMovieTitles(title: String): void`
- `~ subsystemSourcesDoNotReferenceTheFacade(): void throws Exception`
- `~ clientDoesNotImportOrConstructDevices(): void throws Exception`
- `- text(): String`
- `- assertOrdered(messages: String...): void`
- Locals in `completeSessionReturnsToReady`: `HomeTheaterFacade theater` (line 38)
- Locals in `invalidTitleDoesNotActivateDevices`: `HomeTheaterFacade theater` (line 54)
- Locals in `titleIsTrimmed`: `HomeTheaterFacade theater` (line 62)
- Locals in `anotherMovieCannotReplaceAnActiveSession`: `HomeTheaterFacade theater` (line 69); `String before` (line 71)
- Locals in `idleOperationsAreRejectedWithoutDeviceChanges`: `HomeTheaterFacade theater` (line 80)
- Locals in `duplicatePauseAndPrematureResumePreserveState`: `HomeTheaterFacade theater` (line 94); `String before` (line 99)
- Locals in `pausedSessionCanEndAndTheSameFacadeCanStartAnother`: `HomeTheaterFacade theater` (line 107)
- Locals in `shutdownStopsPlaybackBeforeTurningDevicesOff`: `HomeTheaterFacade theater` (line 131)
- Locals in `pauseAndResumeCoordinateLightingWithPlayback`: `HomeTheaterFacade theater` (line 143)
- Locals in `amplifierCanBeUsedDirectlyAndResetsOnPowerOff`: `Amplifier amplifier` (line 165)
- Locals in `invalidVolumeDoesNotChangeThePreviousSetting`: `Amplifier amplifier` (line 183)
- Locals in `lightsAcceptBoundaryValuesAndRestoreFullLighting`: `TheaterLights lights` (line 193)
- Locals in `invalidBrightnessPreservesThePreviousSetting`: `TheaterLights lights` (line 203)
- Locals in `projectorEnforcesPowerAndResetsItsDisplayMode`: `Projector projector` (line 210)
- Locals in `screenCanBeControlledDirectly`: `Screen screen` (line 223)
- Locals in `popcornMakerRequiresPower`: `PopcornMaker maker` (line 233)
- Locals in `playerSupportsItsOwnLifecycleAndRejectsInvalidTransitions`: `StreamingPlayer player` (line 245)
- Locals in `playerRejectsBlankMovieTitles`: `StreamingPlayer player` (line 272)
- Locals in `subsystemSourcesDoNotReferenceTheFacade`: `var files` (line 280); `Path source` (line 281); `inferred path` (line 281); `String code` (line 282)
- Locals in `clientDoesNotImportOrConstructDevices`: `String client` (line 291); `String type` (line 295)
- Locals in `assertOrdered`: `int previous` (line 303); `String message` (line 304); `int position` (line 305)
