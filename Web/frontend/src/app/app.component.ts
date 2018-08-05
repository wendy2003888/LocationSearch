import { Component, ViewChild, ElementRef } from '@angular/core';

import { ResultEntity } from './models';

import { MapsAPILoader } from '@agm/core';
import { SearchService } from './search.service';
import * as moment from 'moment';


type PaneType = 'left' | 'right';


import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('slide', [
      state('left', style({ transform: 'translateX(0)' })),
      state('right', style({ transform: 'translateX(-50%)' })),
      transition('* => *', animate(300))
    ])
  ]
})

export class AppComponent {
  title = 'app';

  showBoard = false;
  showFavorite = false;
  inprogress = false;
  noResult = false;
  showFail = false;
  activePane: PaneType = 'left';

  favorite = {};
  results = [];
  resultBtnClass = {'btn' : true, 'btn-primary': true, 'btn-sm': true, 'mx-1': true};
  favoriteBtnClass = {'btn' : true, 'btn-primary': false, 'btn-sm': true, 'mx-1': true};
  openhours = [[],[],[],[],[],[],[]];
  @ViewChild("b0") boardEl0: ElementRef;
  @ViewChild("b1") boardEl1: ElementRef;


  constructor(private searchService: SearchService) { 
    searchService.favoriteGot$.subscribe(data => {
        this.favorite = data;
      });

    searchService.getFavorites().subscribe(
      data => {
        this.favorite = data;
    });

    searchService.resultGot$.subscribe(
      data => {
        // console.log("list: ");
        // console.log(data);
        // this.activePane = 'left';
        if (data["status"]==='ZERO_RESULTS'){
          this.onNoResult(true);
        }else if (data["status"] ==='OK'){
          if (data["results"]) {
            this.onBoard(true);
            this.boardEl0.nativeElement.setAttribute('class', 'carousel-item active');
            this.boardEl1.nativeElement.setAttribute('class', 'carousel-item');
          } else {
            this.onNoResult(true);
          }
        } else {
          this.onFail(true);
        }
    });

    searchService.placeIdGot$.subscribe(
      placeId =>{
        // console.log(placeId);
        searchService.reqDetail(placeId)
            .subscribe(res => {
          this.searchService.sendDetail(res);
          // this.activePane = 'right';
        },
          error => {
            console.log(error);
        }); 
    });


    searchService.detailGot$.subscribe(
      data => {
        var details = data["result"];
        this.openhours = [[],[],[],[],[],[],[]];
        if (details['opening_hours']) {
        var weekday_text = details['opening_hours']['weekday_text'];
        var today = moment().isoWeekday() - 1;
          for (var j = 0; j < weekday_text.length; ++j) {
            var tmp = weekday_text[j];
            var idx = (j - today + 7) % 7;

            if (tmp.substr(tmp.length - 2, tmp.length) === 'PM') {
              this.openhours[idx] = [tmp.substr(0, tmp.length - 19), tmp.substr(tmp.length - 18, tmp.length).trim()];
            } else {
              this.openhours[idx] = [tmp.substr(0, tmp.length - 15), tmp.substr(tmp.length - 14, tmp.length).trim()];
            }

          }
          // console.log(this.openhours);
        }
    });
  }

  cleanBoard() {
    this.inprogress = this.showFail = this.noResult = this.showBoard = this.showFavorite = false;
    this.activePane = 'left';
  }

  clear() {
    this.searchService.sendResult({});
    this.cleanBoard();
  }

  onInProgress(status) {
    this.cleanBoard();
    this.inprogress = status;
  }

  onFail(status) {
    this.cleanBoard();
    this.showFail = status;
  }

  onNoResult(status) {
    this.cleanBoard();
    this.noResult = status;
  }

  onBoard(status) {
    this.cleanBoard();
    this.resultBtnClass['btn-primary'] = true;
    this.favoriteBtnClass['btn-primary'] = false;
    this.showBoard = status;
    this.activePane = 'left';
  }

  onFavorite() {
    this.cleanBoard();
    this.resultBtnClass['btn-primary'] = false;
    this.favoriteBtnClass['btn-primary'] = true;
    if (!Object.keys(this.favorite).length) {
      this.onNoResult(true);
    } else {
      this.showFavorite = true;
      this.activePane = 'left';
    }
  }

  setPanel(panel: PaneType) {
    this.activePane = panel;
  }

  toggle() {
    this.activePane = (this.activePane ==='left' ? 'right' : 'left');
  }

}
