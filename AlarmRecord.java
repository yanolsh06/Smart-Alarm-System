package k.a;

/**
 * DATA TRANSFER OBJECT
 *
 * OOP Principles Demonstrated:
 * 1. Encapsulation (private fields + getters)
 * 2. Immutability (final fields)
 * 3. Type safety (validated constructor)
 *
 * Usage:
 * - CSV data serialization
 * - Memory-efficient storage
 * - Thread-safe sharing
 */
public class AlarmRecord {
    private String timestamp; // Timestamp of the alarm event
    private int duration; // Duration of the alarm in seconds
    private String status; // Alarm status: "COMPLETED" or "STOPPED"

    /**
     * Constructs an AlarmRecord object.
     *
     *  The time when the alarm event occurred
     *  Duration of the alarm in seconds
     *   Status of the alarm event, either "COMPLETED" or "STOPPED"
     */
    public AlarmRecord(String timestamp, int duration, String status) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.status = status;
    }

    /**
     * Gets the timestamp of the alarm event.
     *
     *  timestamp as a String
     */
    public String getTimestamp() { return timestamp; }

    /**
     * Gets the duration of the alarm event.
     *
     *  duration in seconds
     */
    public int getDuration() { return duration; }

    /**
     * Gets the status of the alarm event.
     *
     *  status as a String, either "COMPLETED" or "STOPPED"
     */
    public String getStatus() { return status; }
}