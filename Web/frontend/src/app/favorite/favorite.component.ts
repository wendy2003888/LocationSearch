import { Component, OnInit } from '@angular/core';

import { SearchService } from '../search.service';


@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.css', '../app.component.css']
})
export class FavoriteComponent implements OnInit {
  
  head = ['#', 'Category', 'Name', 'Address', 'Favorite', 'Details'];
  favorite = [];
  lastLoaded: string;

  constructor(private searchService: SearchService) {
    searchService.favoriteGot$.subscribe(data => {
        this.favorite = [];
        for (const [key, value] of Object.entries(data)) {
            this.favorite.push(value);
        }
      });

    searchService.placeIdGot$.subscribe(
      placeId =>{
        // console.log(placeId);
        this.lastLoaded = placeId;
    });


  }

  ngOnInit() {
    this.searchService.getFavorites().subscribe(
      data => {
        this.favorite = [];
        for (const [key, value] of Object.entries(data)) {
            this.favorite.push(value);
        }
    });
  }

  detail(place_id: string) {
    this.lastLoaded = place_id;
    this.searchService.sendPlaceId(place_id);
  }

  deleteFavorite(place_id: string) {
    this.searchService.deleteFavorite(place_id);
  }

}
