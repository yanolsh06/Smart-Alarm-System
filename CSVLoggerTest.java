package k.a;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST SUITE FOR CSVLogger
 *
 * Test Cases:
 * 1. File creation on first log
 * 2. Correct CSV formatting
 * 3. Multi-record appending (not yet implemented)
 * 4. Header generation (not yet implemented)
 *
 * Features:
 * - Uses an isolated test environment (separate test file)
 * - Ensures automatic cleanup after each test
 * - Validates file system changes
 */
class CSVLoggerTest {
    private static final String TEST_FILE = "test_log.csv"; // Test file name
    private CSVLogger logger;

    @BeforeEach
    void setUp() throws Exception {
        logger = new CSVLogger(); // Instantiate CSVLogger

        // Use reflection to modify the CSV file path for testing
        try {
            var field = CSVLogger.class.getDeclaredField("CSV_FILE"); // Access private static field
            field.setAccessible(true);
            field.set(null, TEST_FILE); // Change file path to test file
        } catch (Exception e) {
            throw new RuntimeException("Failed to set test file path", e);
        }

        // Ensure the test file does not exist before the test starts
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void createsFileWhenLogging() throws Exception {
        // Log an alarm entry, triggering file creation
        logger.logAlarm(30, "COMPLETED");

        // Verify that the test file was created
        assertTrue(Files.exists(Paths.get(TEST_FILE)));
    }

    @Test
    void logsCorrectDataFormat() throws Exception {
        // Log an alarm event
        logger.logAlarm(60, "STOPPED");

        // Read the file contents
        String content = Files.readString(Paths.get(TEST_FILE));

        // Check if the expected log entry exists
        assertTrue(content.contains("60,STOPPED"));
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up: delete test file after each test
        Files.deleteIfExists(Paths.get(TEST_FILE));

        // Reset CSVLogger file path to its original value
        try {
            var field = CSVLogger.class.getDeclaredField("CSV_FILE");
            field.setAccessible(true);
            field.set(null, "alarm_history.csv"); // Reset to production file
        } catch (Exception ignored) {}
    }
}
