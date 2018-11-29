import { HostListener, ElementRef } from '@angular/core';

/**
 * this component will set a variable shouldShow base on mouse click event
 * if click inside component: shoudShow = true
 * else shouldShow = fase
 */
export class SelfClickedAware {
  shouldShow = false;

  constructor(private eRef) { }

  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (this.eRef.nativeElement.contains(event.target)) {
      // console.log('clicked inside');
      this.shouldShow = true;
    } else {
      // console.log('clicked outside');
      this.shouldShow = false;
    }
  }
}
