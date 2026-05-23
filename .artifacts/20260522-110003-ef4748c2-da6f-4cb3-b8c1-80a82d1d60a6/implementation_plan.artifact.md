# Improvement and Feature Addition Plan

This plan addresses the requested UI improvements and new functionalities for the Smart Screen Guard app.

## Proposed Changes

### UI & UX Improvements

#### [HomeScreen.kt](file:///home/waki-assami/AndroidStudioProjects/SmartScreenGuard/app/src/main/java/com/example/smartscreenguard/ui/screens/HomeScreen.kt)
- **Fix Title Overlap**: Replace the custom `Column` in `topBar` with a standard `TopAppBar` or `CenterAlignedTopAppBar` to ensure it respects system bars (battery, clock, etc.).
- **Input Video Playback**:
    - Change the `VideoFile` icon to a `PlayArrow` icon in `VideoPairCard`.
    - Add a click listener to the play icon that triggers a new `onPlayInput` callback.
- **Visual Cleanup**: Standardize the header area.

#### [ResultScreen.kt](file:///home/waki-assami/AndroidStudioProjects/SmartScreenGuard/app/src/main/java/com/example/smartscreenguard/ui/screens/ResultScreen.kt)
- **Download Functionality**: Add a "Download" button (using `Icons.Default.Download`) in the `TopAppBar` or at the bottom of the screen.
- **Download Logic**: Implement logic to copy the video file to the public `Downloads` or `Movies` directory using `MediaStore` or `FileProvider`.

### New Features

#### [MainActivity.kt](file:///home/waki-assami/AndroidStudioProjects/SmartScreenGuard/app/src/main/java/com/example/smartscreenguard/MainActivity.kt)
- **Navigation Update**: Add a new route `input_player/{videoId}` to play the original input video.
- **Rename Functionality**:
    - Add a state variable `showRenameDialog` and `pendingUri`.
    - When a video is picked, show a `Dialog` asking for the title.
    - Update the `videoPickerLauncher` callback to trigger this dialog instead of adding the video immediately.
- **Input Playback Callback**: Pass `onPlayInput` to `HomeScreen` to navigate to the input player.

#### [NEW] [RenameDialog.kt](file:///home/waki-assami/AndroidStudioProjects/SmartScreenGuard/app/src/main/java/com/example/smartscreenguard/ui/components/RenameDialog.kt)
- Create a reusable `AlertDialog` component with a `TextField` for entering the video title.

---

## Verification Plan

### Automated Tests
- No new automated tests are planned as these are primarily UI and integration changes. I will rely on manual verification via the emulator/device.

### Manual Verification
1.  **UI Check**: Verify the title "Smart Screen Guard" is properly positioned and not overlapping the status bar.
2.  **Input Playback**:
    - Click the Play icon on a video card.
    - Verify it opens the player and plays the *input* video.
3.  **Rename Video**:
    - Click the FAB to add a video.
    - Pick a video.
    - Verify a rename dialog appears.
    - Enter a name and confirm.
    - Verify the new video appears in the list with the chosen name.
4.  **Download Video**:
    - Go to the Result screen of any video.
    - Click the Download button.
    - Verify a toast or notification confirms the download (Note: Actual file saving might need Scoped Storage permissions).
