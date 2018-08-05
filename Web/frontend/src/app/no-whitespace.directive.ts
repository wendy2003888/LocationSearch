import { Directive } from '@angular/core';
import { 
    FormControl, 
    NG_VALIDATORS, 
    Validator, 
    ValidatorFn, 
    Validators } from '@angular/forms';


export function noWhitespaceValidator(control: FormControl) {
  let isWhitespace = (control.value).trim().length === 0;
  return !isWhitespace ? null : { 'whitespace': true }
}


@Directive({
  selector: '[appNoWhitespace]',
  providers: [{provide: NG_VALIDATORS, useExisting: NoWhitespaceDirective, multi: true}]
})
export class NoWhitespaceDirective {

  constructor() { }

}
