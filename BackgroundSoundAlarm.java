package k.a;

import org.firmata4j.*;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * MAIN ALARM CONTROLLER
 *
 * - Integrates Arduino (Firmata4j) with Java
 * - Manages multiple subsystems: OLED, Buzzer, Button, CSV Logging
 * - Implements state machine for alarm lifecycle:
 *   SETUP -> COUNTDOWN -> ALARM_ACTIVE -> SHUTDOWN
 *
 * Grand Challenge: "Advance Personalized Learning"
 * - Provides focus timer for study sessions
 * - Tracks productivity metrics via CSV logging
 *
 * Hardware Extensions:
 * - Grove Beginner Kit components +
 * - Custom button debounce circuit
 * - OLED display for visual feedback
 *
 * Testing Protocol:
 * 1. JUnit tests for CSV logging
 * 2. Manual hardware validation checklist
 * 3. Stress test (100+ consecutive alarms)
 */
public class BackgroundSoundAlarm {
    public static final int BUZZER_PIN = 3; // Pin connected to the buzzer
    public static final int BUTTON_PIN = 6; // Pin connected to the button
    public static final byte OLED_ADDRESS = 0x3C; // I2C address for the OLED display

    public static CSVLogger logger = new CSVLogger(); // Logger for storing alarm history
    public static int currentDuration; // Stores the current timer duration
    static boolean isRunning = true; // Flag to control the alarm state

    private static SSD1306 oled; // OLED display object
    private static FirmataDevice arduino; // Firmata device object to interface with Arduino
    private static MiiChannelPlayer player; // Object to handle alarm sound
    private static Scanner scanner = new Scanner(System.in); // Scanner for user input

    public static void main(String[] args) {
        try {
            // Initialize CSV file if it does not exist
            if (!Files.exists(Paths.get(CSVLogger.CSV_FILE))) {
                Files.createFile(Paths.get(CSVLogger.CSV_FILE));
            }

            // Initialize the Arduino device
            arduino = new FirmataDevice("COM3"); // Change COM3 to the correct port
            arduino.start();
            arduino.ensureInitializationIsDone();

            // Initialize buzzer and button pins
            Pin buzzer = arduino.getPin(BUZZER_PIN);
            Pin button = arduino.getPin(BUTTON_PIN);
            buzzer.setMode(Pin.Mode.PWM);
            button.setMode(Pin.Mode.INPUT);

            // Initialize OLED display
            I2CDevice i2cDevice = arduino.getI2CDevice(OLED_ADDRESS);
            oled = new SSD1306(i2cDevice, SSD1306.Size.SSD1306_128_64);
            oled.init();

            // Run the main alarm system loop
            runAlarmSystem(buzzer, button);
        } catch (Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
        } finally {
            safeShutdown(); // Ensure safe shutdown on exit
            scanner.close();
        }
    }

    /**
     * Runs the main menu for the alarm system.
     * Allows the user to set a new alarm, view history, or exit the program.
     */
    private static void runAlarmSystem(Pin buzzer, Pin button) throws InterruptedException {
        boolean continueRunning = true;

        while (continueRunning) {
            System.out.println("\n===== Background Sound Alarm =====");
            System.out.println("1. Set new timer");
            System.out.println("2. View alarm history");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                setNewAlarm(buzzer, button);
            } else if ("2".equals(choice)) {
                logger.displayHistory();
            } else if ("3".equals(choice)) {
                continueRunning = false;
                updateDisplay("Goodbye!");
                System.out.println("Program ending...");
            } else {
                System.out.println("Invalid option");
            }
        }
    }

    /**
     * Handles setting up and running a new alarm timer.
     */
    private static void setNewAlarm(Pin buzzer, Pin button) throws InterruptedException {
        System.out.print("Enter timer duration in seconds: ");
        currentDuration = Integer.parseInt(scanner.nextLine());

        updateDisplay("Timer: " + currentDuration + "s");
        System.out.println("Timer started...");

        // Countdown timer loop
        for (int i = currentDuration; i > 0; i--) {
            updateDisplay("Time left: " + i + "s");
            Thread.sleep(1000);
        }

        // Alarm activation
        updateDisplay("ALARM ACTIVE!");
        System.out.println("ALARM! Press button to stop.");

        // Start alarm sound
        player = new MiiChannelPlayer(buzzer);
        new Thread(player).start();

        // Set up button listener
        ButtonListener listener = new ButtonListener(button, player);
        arduino.addEventListener(listener);

        // Wait for button press to stop alarm
        while (isRunning) {
            Thread.sleep(100);
        }

        // Cleanup after alarm stops
        arduino.removeEventListener(listener);
        player.stopPlaying();

        // Log alarm completion
        if (isRunning) {
            logger.logAlarm(currentDuration, "COMPLETED");
        }
        isRunning = true; // Reset alarm state
    }

    /**
     * Updates the OLED display with a given message.
     */
    public static synchronized void updateDisplay(String message) {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().setTextsize(2);
            oled.getCanvas().drawString(10, 20, message);
            oled.display();
        } catch (Exception e) {
            System.err.println("Display update skipped");
        }
    }

    /**
     * Ensures a safe shutdown by clearing the display and stopping the Arduino connection.
     */
    public static void safeShutdown() {
        try {
            if (oled != null) {
                oled.getCanvas().clear();
                oled.display();
            }
            if (arduino != null) {
                arduino.stop();
            }
        } catch (Exception e) {
            System.err.println("Clean shutdown skipped");
        }
    }
}
