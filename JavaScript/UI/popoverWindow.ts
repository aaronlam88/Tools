import { EventEmitter, Output } from '@angular/core';

export class PopoverWindow {

  // EventEmitter to let parent know that this window should be closed
  @Output() closeWin = new EventEmitter();

  // on init, window should be open, so set shouldWindowOpen to true
  // set to true if clicked on this window, else set to false
  shoudlWindowOpen = true;

  constructor() {
    window.addEventListener('click', this.tryCloseWindow);
  }

  /**
  * @description try to close this window, if shouldWindownOpen don't close this window, else close it
  **/
  tryCloseWindow = () => {
    if (this.shoudlWindowOpen) {
      // set shouldWindowOpen to false to get ready for the next click event
      this.shoudlWindowOpen = false;
    } else {
      // clicked outsite of this window, should close this window
      this.closeWindow();
    }
  }

  /**
  * @description should be called when clicked on self, this window shouldn't be closed, so set shoudlWindowOpen to true
  **/
  onClick() {
    this.shoudlWindowOpen = true;
  }

  /**
   * @description should be called when trying to close this window
   */
  closeWindow() {
    // emit closeWin event for header to close this window
    this.closeWin.emit(false);
    // removeEventListener to avoid duplicate listener
    window.removeEventListener('click', this.tryCloseWindow);
  }
}
