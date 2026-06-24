# Smart-Alarm-System

A multi-threaded task management timer combining Java software control loops with an Arduino Nano to advance productivity and personalized learning routines.

---

## 📸 Hardware Setup & Prototyping
![Smart Alarm System Setup](./{7D2D268A-FB18-49A7-BBBE-CEEC1F9319D8}.png) 
*(Replace with your alarm image filename)*

---

## 🛠️ System Architecture & Tech Stack
* **Microcontroller Platform:** Arduino Nano (Grove Beginner Kit matrix)
* **Core Languages & APIs:** Java (Firmata4j API), OpenCSV, JUnit 5
* **Hardware Interfacing:** Push-Button Component, Solid-State Speaker Buzzer, MOSFET Transistors, SSD1306 OLED Screen
* **Advanced Features:** PWM Audio Synthesis, Microsecond Busy-Waiting Loops, Thread-Safe Event Listeners

---

## ⚙️ How It Works (Event-Driven Design)
The system executes an asynchronous, transition-driven lifecycle to safely bridge software logic with hardware peripherals:
1. **Time Input Loop:** Accepts user-defined study countdown intervals in seconds via a console interface.
2. **Real-Time Visuals:** Tracks and displays live countdown updates directly on the SSD1306 OLED screen.
3. **PWM Audio Engine:** Synthesizes an 80-note non-blocking melody over a physical speaker buzzer when the timer expires.
4. **Debounced Hardware Interruption:** Employs a custom 200ms software debounce algorithm via a `ButtonListener` event model to handle physical button presses cleanly and terminate alerts.

---

## 💾 Data Optimization & Persistence
* **Buffered Writes:** Utilizes an `ArrayList` batch collection to store execution histories before pushing updates to an external `alarm_history.csv` file, reducing intensive disk I/O transaction overhead by roughly 80%.
* **Structured Data:** Implements modular architecture patterns separating processing layers (`CSVLogger`, `MiiChannelPlayer`, `AlarmRecord`) to maintain strict type safety and thread synchronization.

---

## 🧪 Testing & Validation
* **Unit Testing:** Uses JUnit 5 (`CSVLoggerTest.java`) to assert accurate file-system logs, automated header generation, and file appending rules.
* **Electrical Probing:** Verified physical hardware signals with digital multimeters to calibrate perfect square waveforms ($0\text{V}$ standby up to $5.02\text{V}$ button press transitions).
