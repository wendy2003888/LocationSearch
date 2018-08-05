import { Component, OnInit, Output, EventEmitter, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';
import * as moment from 'moment';


import { SearchService } from '../../search.service';
import { ReviewInstace } from '../../models';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css', '../../app.component.css'],
  animations: [
    trigger('myFade', [
      state('true' , style({ opacity: 1, transform: 'scale(1.0)' })),
      state('false', style({ opacity: 0, transform: 'scale(0.0)'  })),
      transition('1 => 0', animate('1000ms')),
      transition('0 => 1', animate('1000ms'))
    ])
  ]
})
export class ReviewComponent implements OnInit {
  
  yelpReq: any;
  ggReviews: any;
  yelpReviews: any;
  reviews: any;
  hasRecord = false;
  showYelp = false;
  reviewSource = 'Google Reviews';
  orderType = 'Default Order';
  @Output() inProgress = new EventEmitter<boolean>();



  constructor(private searchService: SearchService,
    private cdr: ChangeDetectorRef) {
    this.reviews = [];
    this.ggReviews = [];
    this.yelpReviews = [];
    this.searchService.getDetail().subscribe(
      data => {
        // console.log("review: ");
        // console.log(data);
        var formattedAddr = data["result"]["formatted_address"].split(",");
        var state_postcode = formattedAddr[formattedAddr.length-2].trim().split(" ");
        this.yelpReq = {
            name: data["result"]["name"],
            city: formattedAddr[formattedAddr.length-3],
            state: state_postcode[0],
            postal_code: state_postcode[1],
            country: 'US'
        }
        if (formattedAddr.length > 3) {
          this.yelpReq['address1'] = formattedAddr[0]; 
        }

        var items = [];
        for (var i = 0; i < data["result"]["reviews"].length; ++i) {
          var cur = data["result"]["reviews"][i];
          var tmp = new ReviewInstace(
            cur.author_name, cur.author_url, 
            cur.profile_photo_url, cur.rating,
            moment.unix(cur.time).format('yyyy-MM-dd HH:mm:ss'), cur.text);
          items.push(tmp);
        }
        this.ggReviews = items;
        this.reviews = this.ggReviews.slice();
        this.hasRecord = (this.ggReviews.length > 0);
        this.reviewSource = 'Google Reviews';
        this.showYelp = false;
        // this.cdr.detectChanges();
      });

      this.searchService.detailGot$.subscribe(
        data => {
          var formattedAddr = data["result"]["formatted_address"].split(",");
          var state_postcode = formattedAddr[formattedAddr.length-2].trim().split(" ");
          this.yelpReq = {
              name: data["result"]["name"],
              city: formattedAddr[formattedAddr.length-3],
              state: state_postcode[0],
              postal_code: state_postcode[1],
              country: 'US'
          }
          if (formattedAddr.length > 3) {
            this.yelpReq['address1'] = formattedAddr[0]; 
          }

          var items = [];

          for (var i = 0; i < data["result"]["reviews"].length; ++i) {
            var cur = data["result"]["reviews"][i];
            var tmp = new ReviewInstace(
              cur.author_name, cur.author_url, 
              cur.profile_photo_url, cur.rating,
              moment.unix(cur.time).format('yyyy-MM-dd HH:mm:ss'), cur.text);
            items.push(tmp);
          }
          this.ggReviews = items;
          this.reviews = this.ggReviews.slice();
          // console.log(this.reviews);
          this.hasRecord = (this.ggReviews.length > 0);
          this.reviewSource = 'Google Reviews';
          this.showYelp = false;
          // this.cdr.detectChanges();
      });

  }

  ngOnInit() {
    this.searchService.itemChangeGot$.subscribe(data => {
      this.searchService.getDetail().subscribe(
      data => {
        // console.log("review: ");
        // console.log(data);
        var formattedAddr = data["result"]["formatted_address"].split(",");
        var state_postcode = formattedAddr[formattedAddr.length-2].trim().split(" ");
        this.yelpReq = {
            name: data["result"]["name"],
            city: formattedAddr[formattedAddr.length-3],
            state: state_postcode[0],
            postal_code: state_postcode[1],
            country: 'US'
        }
        if (formattedAddr.length > 3) {
          this.yelpReq['address1'] = formattedAddr[0]; 
        }

        var items = [];
        for (var i = 0; i < data["result"]["reviews"].length; ++i) {
          var cur = data["result"]["reviews"][i];
          var tmp = new ReviewInstace(
            cur.author_name, cur.author_url, 
            cur.profile_photo_url, cur.rating,
            moment.unix(cur.time).format('yyyy-MM-dd HH:mm:ss'), cur.text);
          items.push(tmp);
        }
        this.ggReviews = items;
        this.reviews = this.ggReviews.slice();
        this.hasRecord = (this.ggReviews.length > 0);
        this.reviewSource = 'Google Reviews';
        this.showYelp = false;
        // this.cdr.detectChanges();
      });

      this.searchService.detailGot$.subscribe(
        data => {
          var formattedAddr = data["result"]["formatted_address"].split(",");
          var state_postcode = formattedAddr[formattedAddr.length-2].trim().split(" ");
          this.yelpReq = {
              name: data["result"]["name"],
              city: formattedAddr[formattedAddr.length-3],
              state: state_postcode[0],
              postal_code: state_postcode[1],
              country: 'US'
          }
          if (formattedAddr.length > 3) {
            this.yelpReq['address1'] = formattedAddr[0]; 
          }

          var items = [];

          for (var i = 0; i < data["result"]["reviews"].length; ++i) {
            var cur = data["result"]["reviews"][i];
            var tmp = new ReviewInstace(
              cur.author_name, cur.author_url, 
              cur.profile_photo_url, cur.rating,
              moment.unix(cur.time).format('yyyy-MM-dd HH:mm:ss'), cur.text);
            items.push(tmp);
          }
          this.ggReviews = items;
          this.reviews = this.ggReviews.slice();
          // console.log(this.reviews);
          this.hasRecord = (this.ggReviews.length > 0);
          this.reviewSource = 'Google Reviews';
          this.showYelp = false;
          // this.cdr.detectChanges();
      });

    });
    

    
  }

  googleReview() {
    this.reviews = this.ggReviews.slice();
    this.hasRecord = (this.ggReviews.length > 0);
    this.reviewSource = 'Google Reviews';
  }

  yelpReview() {
    this.reviewSource = "Yelp Reviews";
    this.inProgress.emit(true);
    this.searchService.reqyelpMatch(this.yelpReq)
    .subscribe(data => {
        if (data["businesses"] && data["businesses"].length > 0) {
          var id = data["businesses"][0].id;
          this.searchService.reqyelpReview(id).subscribe(data => {
            if (data["reviews"]) {
              var items = []
              for (var i = 0; i < data['reviews'].length; ++i) {
                var cur = data["reviews"][i];
                var tmp = new ReviewInstace(
                  cur.user.name, cur.url, 
                  cur.user.image_url, cur.rating,
                  cur.time_created, cur.text);
                items.push(tmp);
              }
              this.reviews = items;
              this.yelpReviews = this.reviews.slice();
              this.inProgress.emit(false);
              this.hasRecord = true;
              this.showYelp = true;
            } else {
              this.inProgress.emit(false);
              this.hasRecord = false;
            }
          });
        } else {
          this.hasRecord = false;
          this.inProgress.emit(false);
        }
    });
  }

  sortReview(orderType) {
    this.orderType = orderType;
    switch(orderType) {
      case 'Default Order':
        this.reviews = this.ggReviews.slice();
        break;
      case 'Highest Rating':
        this.reviews.sort(function(a, b) {return a.rating < b.rating});
        break;
      case 'Lowest Rating':
        this.reviews.sort(function(a, b) {return a.rating > b.rating});
        break;
      case 'Most Recent':
        this.reviews.sort(function(a, b) {return a.time < b.time});
        break;
      case 'Least Recent':
        this.reviews.sort(function(a, b) {return a.time > b.time});
        break;
    }
  }

}
