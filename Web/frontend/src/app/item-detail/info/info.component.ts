import { Component, OnInit, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';

import { SearchService } from '../../search.service';
import * as moment from 'moment';


@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {
  @Output() inProgress = new EventEmitter<boolean>();

  details: object;
  coltitle = ['Address',
          'Phone Number',
          'Price Level',
          'Rating',
          'Google Page',
          'Website',
          'Hours'
        ];
  keys = [
          'formatted_address',
          'international_phone_number',
          'price_level',
          'rating',
          'url',
          'website',
          'opening_hours'
          ]

  valid_data = [];
  rate=3.5;

  constructor(private searchService: SearchService,
    private cdr: ChangeDetectorRef) { 
    // console.log("i");
    searchService.detailGot$.subscribe(
      data => {
        // console.log("info: ");
        // console.log(data);
        this.details = data["result"];
        this.valid_data = [];
        for (var i = 0; i < this.keys.length; ++i) {
          if (this.details[this.keys[i]]) {
            if (this.keys[i]==='price_level') {
              this.valid_data.push([this.coltitle[i], Array( this.details[this.keys[i]]+1 ).join('$')]);
            } else if (this.keys[i]==='opening_hours'){
              var weekday_text = this.details['opening_hours']['weekday_text'];
                var tmp = weekday_text[moment().isoWeekday() - 1];
                var oph = '';
                var cur = this.details[this.keys[i]];
                if (tmp.substr(tmp.length - 2, tmp.length) === 'PM') {
                  oph = tmp.substr(tmp.length - 18, tmp.length).trim();
                } else {
                  oph = tmp.substr(tmp.length - 14, tmp.length).trim();
                }
                var s = (cur['open_now'] ? `Open now: ${oph} ` : 'Closed');
                // var cur = this.details[this.keys[i]];
                // var oph = cur['periods'][moment().isoWeekday() % 7];
                // var s= '';
                // if (oph['time'] && oph['time'] ==='0000') {
                //   s = 'Open 24 hours';
                // } else {
                //   var utc_offset = this.details['utc_offset'];
                //   var open = oph["open"]['time'];
                //   var close = oph["close"]['time'];
                //   var st = moment(open, 'hhmm').utcOffset(utc_offset).format('LT');
                //   var ed = moment(close, 'hhmm').utcOffset(utc_offset).format('LT');
                //   s = (cur['open_now'] ? `Open now: ${st} - ${ed} ` : 'Closed');
                // }
                this.valid_data.push([this.coltitle[i], s]);
            } else {
              this.valid_data.push([this.coltitle[i], this.details[this.keys[i]]]);
            }
          }
        }
        this.inProgress.emit(false);
        // moment().format('LT');
        // console.log(this.valid_data);
    });
  }

  ngOnInit() {
    this.searchService.getDetail().subscribe(
      data => {
        //to do status 非OK  2. 无有效data
        // console.log(data);
        if (data) {
          this.details = data["result"];
          this.valid_data = [];
          for (var i = 0; i < this.keys.length; ++i) {
            if (this.details[this.keys[i]]) {
              if (this.keys[i]==='price_level') {
                this.valid_data.push([this.coltitle[i], Array( this.details[this.keys[i]]+1 ).join('$')]);
              } else if (this.keys[i]==='opening_hours'){
                var weekday_text = this.details['opening_hours']['weekday_text'];
                var tmp = weekday_text[moment().isoWeekday() - 1];
                var oph = '';
                var cur = this.details[this.keys[i]];
                if (tmp.substr(tmp.length - 2, tmp.length) === 'PM') {
                  oph = tmp.substr(tmp.length - 18, tmp.length).trim();
                } else {
                  oph = tmp.substr(tmp.length - 14, tmp.length).trim();
                }
                var s = (cur['open_now'] ? `Open now: ${oph} ` : 'Closed');
                // var cur = this.details[this.keys[i]];
                // var oph = cur['periods'][moment().isoWeekday() % 7];
                // var s= '';
                // if (oph['time'] && oph['time'] ==='0000') {
                //   s = 'Open 24 hours';
                // } else {
                //   var utc_offset = this.details['utc_offset'];
                //   var open = oph["open"]['time'];
                //   var close = oph["close"]['time'];
                //   var st = moment(open, 'hhmm').utcOffset(utc_offset).format('LT');
                //   var ed = moment(close, 'hhmm').utcOffset(utc_offset).format('LT');
                //   s = (cur['open_now'] ? `Open now: ${st} - ${ed} ` : 'Closed');
                // }
                
                  
                  this.valid_data.push([this.coltitle[i], s]);
              } else {
                this.valid_data.push([this.coltitle[i], this.details[this.keys[i]]]);
              }

            }
          }
          this.inProgress.emit(false);
        }
    });
  }
}
