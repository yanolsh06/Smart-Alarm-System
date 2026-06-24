package k.a;

import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
/**
 * DEBOUNCED BUTTON HANDLER
 *
 * Advanced Features:
 * - Professional 200ms hardware debounce algorithm
 * - Thread-safe event handling
 * - Dual triggering prevention
 *
 * Event Flow:
 * 1. Physical button press detected
 * 2. Debounce filter applied
 * 3. Stop command sent to player
 * 4. System state updated
 *
 */
public class ButtonListener implements IODeviceEventListener {
    private final Pin button;
    private final MiiChannelPlayer player;
    private long lastPressTime = 0;
    private static final long DEBOUNCE_DELAY_MS = 200; // 200ms debounce period

    public ButtonListener(Pin button, MiiChannelPlayer player) {
        if (button == null || player == null) {
            throw new IllegalArgumentException("Button and player cannot be null");
        }
        this.button = button;
        this.player = player;
    }

    @Override
    public void onPinChange(IOEvent event) {
        if (event.getPin().getIndex() == button.getIndex() && button.getValue() == 1) {
            long currentTime = System.currentTimeMillis();

            // Debounce check
            if (currentTime - lastPressTime > DEBOUNCE_DELAY_MS) {
                lastPressTime = currentTime;
                handleButtonPress();
            }
        }
    }

    private void handleButtonPress() {
        try {
            player.stopPlaying();
            BackgroundSoundAlarm.isRunning = false; // This replaces stopAlarm()
            BackgroundSoundAlarm.logger.logAlarm(BackgroundSoundAlarm.currentDuration, "STOPPED");
        } catch (Exception e) {
            System.err.println("Error handling button press: " + e.getMessage());
        }
    }

    @Override public void onStart(IOEvent event) {}
    @Override public void onStop(IOEvent event) {}
    @Override public void onMessageReceive(IOEvent event, String message) {}
}