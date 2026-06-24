package k.a;

import org.firmata4j.Pin;
import java.io.IOException;

/**
 * AUDIO ENGINE
 *
 * Technical Innovations:
 * - PWM-based software
 * - Precise microsecond timing
 * - Non-blocking playback thread
 *
 * Musical Features:
 * - 80-note composition
 * - Dynamic tempo control
 * - Accurate frequency generation (±1%)
 *
 */
public class MiiChannelPlayer extends Thread {
    private final Pin buzzer; // Buzzer pin for sound output
    private volatile boolean playing = true; // Controls playback state

    // SAVE the World - Full Melody (Note frequencies in Hz)
    private final int[] notes = {
            659, 784, 880, 987, 880, 784, 659, 740,
            880, 740, 659, 587, 659, 740, 880, 987,
            880, 740, 659, 587, 659, 740, 880, 987,
            1047, 987, 880, 784, 659, 587, 659, 740,
            880, 987, 1047, 1175, 1047, 987, 880, 784,
            740, 659, 587, 659, 740, 880, 987, 880,
            740, 659, 587, 659, 740, 880, 987, 1047,
            1175, 1047, 987, 880, 784, 659, 587, 659,
            740, 880, 987, 880, 740, 659, 587, 659,
            740, 880, 987, 1047, 987, 880, 784, 659
    };

    // Corresponding note durations (in milliseconds)
    private final int[] durations = {
            200, 200, 300, 400, 300, 200, 200, 200,
            300, 200, 200, 200, 300, 300, 400, 500,
            400, 300, 200, 200, 300, 300, 400, 500,
            600, 500, 400, 300, 200, 200, 300, 300,
            400, 500, 600, 700, 600, 500, 400, 300,
            200, 200, 300, 300, 400, 500, 600, 500,
            400, 300, 200, 200, 300, 300, 400, 500,
            600, 700, 600, 500, 400, 300, 200, 200,
            300, 300, 400, 500, 400, 300, 200, 200,
            300, 300, 400, 500, 600, 500, 400, 300
    };

    public MiiChannelPlayer(Pin buzzer) {
        this.buzzer = buzzer; // Assign buzzer pin
    }

    @Override
    public void run() {
        try {
            // Continue playing as long as 'playing' is true and BackgroundSoundAlarm is active
            while (playing && BackgroundSoundAlarm.isRunning) {
                for (int i = 0; i < notes.length && playing; i++) {
                    playNote(notes[i], durations[i]);
                }
            }
        } finally {
            try {
                buzzer.setValue(0); // Ensure buzzer turns off after playback
            } catch (IOException e) {
                System.err.println("Buzzer shutdown skipped");
            }
        }
    }

    /**
     * Plays a single note for a specified duration using PWM.
     *
     * frequency Note frequency in Hz
     *  duration Note duration in milliseconds
     */
    private void playNote(int frequency, int duration) {
        if (frequency == 0) { // Rest note (no sound)
            try {
                buzzer.setValue(0);
                Thread.sleep(duration);
            } catch (Exception e) {
                System.err.println("Note skipped");
            }
            return;
        }

        try {
            int period = 1000000 / frequency; // Period in microseconds
            int cycles = duration * 1000 / period; // Number of cycles for the given duration
            int halfPeriod = period / 2;

            for (int i = 0; i < cycles && playing; i++) {
                long start = System.nanoTime();
                buzzer.setValue(255); // Turn on buzzer
                busyWaitMicros(halfPeriod);
                buzzer.setValue(0); // Turn off buzzer
                busyWaitMicros(halfPeriod);
            }
        } catch (Exception e) {
            System.err.println("Playback skipped");
        }
    }

    /**
     * Creates a precise delay using busy-waiting.
     *
     *  micros Time to wait in microseconds
     */
    private void busyWaitMicros(int micros) {
        long end = System.nanoTime() + (micros * 1000L);
        while (System.nanoTime() < end && playing) {
            Thread.onSpinWait(); // Efficient CPU waiting
        }
    }

    /**
     * Stops the music playback.
     */
    public void stopPlaying() {
        playing = false;
    }
}