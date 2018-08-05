import { Component, OnInit, EventEmitter, Output, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { MapsAPILoader } from '@agm/core';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';


import { SearchService } from '../search.service';
import { mock_detail } from '../mockdata';


@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.css', '../app.component.css']
})

export class ItemDetailComponent implements OnInit {
  @Output() onFail = new EventEmitter<boolean>();
  @Output() setPanel = new EventEmitter<string>();
  @ViewChild("photo") photoEl: ElementRef;
  placesService: any;


  state = 'inactivate';

  mock_detail = mock_detail;
  noResult = false;

  detail: any = {name: '', place_id: ''};
  favorite = {};
  inprogress = true;

  photos = [];
  // itemChange

  constructor(private searchService: SearchService,
    private mapsAPILoader: MapsAPILoader,
    private cdr: ChangeDetectorRef) { 
    this.detail = {name: ''};
    searchService.detailGot$.subscribe(
      res =>{
        if (res["status"] !='OK') {
          this.onFail.emit(true);
        } else {
          if (res["result"]) {
            this.detail = res["result"];
            this.onNoResult(false);
            this.searchService.sentItemChange(true);
          } else {
            this.onNoResult(true);
          }
        }
        // this.cdr.detectChanges();
    });
    searchService.favoriteGot$.subscribe(data => {
      this.favorite = data;
    });
    this.searchService.getFavorites().subscribe(
      data => {
        this.favorite = data;
    });
  }

  ngOnInit() {
  }

  onNoResult(status) {
    this.noResult = status;
  }

  addFavorite() {
    this.searchService.addFavorite(this.detail);
  }

  deleteFavorite() {
    this.searchService.deleteFavorite(this.detail['place_id']);
  }

  detailInprogress(status) {
    this.inprogress = status;
    // this.cdr.detectChanges();
    // console.log(this.inprogress);
  }

  twitter() {
    var webpage = this.detail["website"]? this.detail["website"] : this.detail["url"];
    var url= `https://twitter.com/intent/tweet?text=Check out ${this.detail["name"]} located at ${this.detail["formatted_address"]}. Website: &url=${webpage}&hashtags=TravelAndEntertainmentSearch`;
    url = encodeURI(url);
    window.open(url);
    // console.log(url);
  }

  back() {
    this.setPanel.emit('left');
  }
}
