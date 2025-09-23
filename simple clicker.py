import time
import random
import threading
from pynput.mouse import Button, Controller as MouseController
from pynput.keyboard import Listener, Key, Controller as KeyboardController

# --- Configuration ---
TOGGLE_HOTKEY = Key.f12
# You can change this to other keys like Key.f7, Key.f8, etc.
# For character keys, use: KeyCode.from_char('c') for the 'c' key.

# MIN_CLICK_DELAY_MS = 450  # crafting
# MAX_CLICK_DELAY_MS = 720
#
MIN_CLICK_DELAY_MS = 1000  # ranging guild
MAX_CLICK_DELAY_MS = 1600


# Convert ms to seconds for time.sleep()
MIN_CLICK_DELAY_SEC = MIN_CLICK_DELAY_MS / 1000.0
MAX_CLICK_DELAY_SEC = MAX_CLICK_DELAY_MS / 1000.0

# --- Humanization Settings ---
# Chance to take a longer, more human-like pause after a number of clicks.
# 0.01 means a 1% chance per click.
PAUSE_CHANCE = 0.01
# The minimum number of clicks before a long pause can even occur.
MIN_CLICKS_BEFORE_PAUSE = 15
# The range for the long pause duration in seconds.
PAUSE_DURATION_MIN_SEC = 0.8
PAUSE_DURATION_MAX_SEC = 2.5

# --- Global Variables ---
script_enabled = False
mouse = MouseController()
keyboard = KeyboardController()
click_thread = None
space_thread = None # Thread for holding the spacebar
clicks_since_last_pause = 0

def clicker_worker():
    """The thread that handles the clicking logic when enabled."""
    global clicks_since_last_pause
    print("Clicker thread started.")

    while script_enabled:
        mouse.click(Button.left, 1)
        clicks_since_last_pause += 1

        # --- Human-like Pause Logic ---
        if random.random() < PAUSE_CHANCE and clicks_since_last_pause > MIN_CLICKS_BEFORE_PAUSE:
            print("Taking a human-like pause...")
            pause_duration = random.uniform(PAUSE_DURATION_MIN_SEC, PAUSE_DURATION_MAX_SEC)
            time.sleep(pause_duration)
            clicks_since_last_pause = 0

        # --- Standard Click Delay ---
        mean_delay = (MIN_CLICK_DELAY_SEC + MAX_CLICK_DELAY_SEC) / 2
        std_dev = (MAX_CLICK_DELAY_SEC - MIN_CLICK_DELAY_SEC) / 6
        delay = random.gauss(mean_delay, std_dev)
        final_delay = max(MIN_CLICK_DELAY_SEC, min(delay, MAX_CLICK_DELAY_SEC))
        time.sleep(final_delay)

    print("Clicker thread stopped.")

def spacebar_worker():
    """
    The thread that handles holding the spacebar down.
    This works by repeatedly sending the 'press' signal, which is more
    reliable for games and applications than a single press event.
    """
    print("Spacebar-hold thread started.")
    while script_enabled:
        keyboard.press(Key.space)
        # A small sleep is necessary to prevent overwhelming the system
        # and to allow the key release to be detected when disabled.
        time.sleep(0.1)
    print("Spacebar-hold thread stopped.")


def on_press(key):
    """Callback function to handle key presses."""
    global script_enabled, click_thread, space_thread

    if key == TOGGLE_HOTKEY:
        script_enabled = not script_enabled

        if script_enabled:
            print(f"--- Hotkey '{TOGGLE_HOTKEY}' pressed: Script ENABLED. ---")
            print(f"--- Press '{TOGGLE_HOTKEY}' again to disable. ---")

            # Start the clicking thread
            if click_thread is None or not click_thread.is_alive():
                global clicks_since_last_pause
                clicks_since_last_pause = 0
                click_thread = threading.Thread(target=clicker_worker, daemon=True)
                click_thread.start()

            # Start the spacebar-holding thread
            if space_thread is None or not space_thread.is_alive():
                space_thread = threading.Thread(target=spacebar_worker, daemon=True)
                space_thread.start()
        else:
            print(f"--- Hotkey '{TOGGLE_HOTKEY}' pressed: Script DISABLED. ---")
            # The threads will stop because their 'while script_enabled' loops will exit.
            # We then send a final release command to ensure the key is up.
            keyboard.release(Key.space)

def main():
    """Main function to set up the listener and run the script."""
    print("Auto-Clicker script is running.")
    print(f"Press '{TOGGLE_HOTKEY}' to start/stop the script.")
    print("Press Ctrl+C in this window to exit the script completely.")

    with Listener(on_press=on_press) as listener:
        try:
            listener.join()
        except KeyboardInterrupt:
            print("\nScript terminated by user (Ctrl+C).")
        finally:
            # --- Cleanup ---
            global script_enabled
            script_enabled = False
            keyboard.release(Key.space)
            print("Spacebar released. Exiting.")

if __name__ == "__main__":
    main()
