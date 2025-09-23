import time
import random
import threading
from pynput.mouse import Button, Controller as MouseController
from pynput.keyboard import Listener, Key

# --- Configuration ---
TOGGLE_HOTKEY = Key.f12
# The hotkey to toggle clicking ON and OFF.

MIN_CLICK_DELAY_SEC = 0.5  # Minimum delay between clicks in seconds
MAX_CLICK_DELAY_SEC = 0.6  # Maximum delay between clicks in seconds

# --- Global Variables ---
clicking_enabled = False
mouse = MouseController()
click_thread = None


def clicker_worker():
    print("Clicker thread started.")
    while clicking_enabled:
        # Perform a left mouse click
        mouse.click(Button.left, 1)

        # Wait for a random duration between the min and max delay
        delay = random.uniform(MIN_CLICK_DELAY_SEC, MAX_CLICK_DELAY_SEC)
        time.sleep(delay)
    print("Clicker thread stopped.")


def on_press(key):
    """Callback function to handle the toggle hotkey press."""
    global clicking_enabled, click_thread

    # Check if the pressed key is our designated hotkey
    if key == TOGGLE_HOTKEY:
        clicking_enabled = not clicking_enabled

        if clicking_enabled:
            print(f"--- Hotkey '{TOGGLE_HOTKEY}' pressed: Clicking ENABLED. ---")
            print(f"--- Press '{TOGGLE_HOTKEY}' again to disable. ---")

            # Start the clicking thread if it's not already running
            if click_thread is None or not click_thread.is_alive():
                click_thread = threading.Thread(target=clicker_worker, daemon=True)
                click_thread.start()
        else:
            print(f"--- Hotkey '{TOGGLE_HOTKEY}' pressed: Clicking DISABLED. ---")
            # The thread will stop on its own because its 'while clicking_enabled'
            # loop condition will become false.


def main():
    """Main function to set up the listener and run the script."""
    print("Simple Auto-Clicker is running.")
    print(f"Press '{TOGGLE_HOTKEY}' to start/stop clicking.")
    print("Press Ctrl+C in this window to exit the script completely.")

    # Set up the keyboard listener to watch for the hotkey
    with Listener(on_press=on_press) as listener:
        try:
            # Keep the main thread alive to listen for key presses
            listener.join()
        except KeyboardInterrupt:
            print("\nScript terminated by user (Ctrl+C).")
        finally:
            # --- Cleanup ---
            # Ensure the clicking flag is set to False on exit
            global clicking_enabled
            clicking_enabled = False
            print("Script stopped.")


if __name__ == "__main__":
    main()
