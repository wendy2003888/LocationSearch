import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { SearchService } from '../search.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css', '../app.component.css']
})

export class ListComponent implements OnInit {

  @Output() inProgress = new EventEmitter<boolean>();
  @Output() showBoard = new EventEmitter<boolean>();

  head = ['#', 'Category', 'Name', 'Address', 'Favorite', 'Details'];

  results: object;
  page_cache = [];
  pagetokens = [];
  show_pre = false;
  show_next = false;
  favorite = {};
  lastLoaded: string; 


  constructor(private searchService: SearchService) {
    searchService.favoriteGot$.subscribe(data => {
        this.favorite = data;
      });
  }

  ngOnInit() {
    this.searchService.resultGot$.subscribe(
      data => {
        this.results = data["results"];
        this.pagetokens = [];
        this.page_cache = [];
        this.show_pre = false;
        this.lastLoaded = '';
        if (data["next_page_token"]) {
          this.pagetokens.push(data["next_page_token"]);
          this.show_next = true;
        }
    });

    this.searchService.getFavorites().subscribe(
      data => {
        this.favorite = data;
    });
  }

  detail(place_id: string) {
    this.lastLoaded = place_id;
    this.searchService.sendPlaceId(place_id);
  }

  addFavorite(item) {
    this.searchService.addFavorite(item);
  }

  deleteFavorite(place_id) {
    this.searchService.deleteFavorite(place_id);
  }

  mouseEnter(eventObject, place_id) {
    eventObject.target.setAttribute("style", "background-color: rgb(236, 236, 236)");
  }

  mouseLeave(eventObject, place_id) {
    var color = (this.lastLoaded===place_id ? 'rgb(252,222,137)' : 'transparent');
    eventObject.target.setAttribute("style", `background-color: ${color}`);
  }



  nextPage(pagetoken: string) {
    this.inProgress.emit(true);
    this.page_cache.push(this.results);
    this.show_pre = true;
    this.searchService.reqPage(pagetoken)
      .subscribe(res => {
        this.results = res["results"];
        if (res["next_page_token"]) {
          this.show_next = true;
          this.pagetokens.push(res["next_page_token"]);
        } else {
          this.show_next = false;
          this.pagetokens.push('');
        }
        this.showBoard.emit(true);
      },
      error => {
        console.log(error);
      });
  }

  prePage() {
    this.inProgress.emit(true);
    this.results = this.page_cache.pop();
    this.show_next = true;
    if (this.pagetokens.length) {
      this.pagetokens.pop();
    }
    this.show_pre = (this.pagetokens.length > 1 ? true : false);
    this.showBoard.emit(true);
  }

}
