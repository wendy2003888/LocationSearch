import { Component, OnInit, ElementRef, ViewChild, Input, Output, EventEmitter, ChangeDetectorRef} from '@angular/core';
import { MapsAPILoader } from '@agm/core';

import { SearchService } from '../../search.service';

@Component({
  selector: 'app-photo',
  templateUrl: './photo.component.html',
  styleUrls: ['./photo.component.css']
})
export class PhotoComponent implements OnInit {
  hasRecord = true;
  photos: any = [[],[],[],[]];
  detail: any;
  placesService: any;
  @ViewChild("photo") photoEl: ElementRef;
  index = [];

  @Output() inProgress = new EventEmitter<boolean>();
  
  @Input("phts") phts: any;


  constructor(private searchService: SearchService,
    private mapsAPILoader: MapsAPILoader,
    private cdr: ChangeDetectorRef) {
  }

  identify(index, item){
     return item.url; 
  }

  ngOnInit() {
    this.inProgress.emit(true);
    this.searchService.getDetail().subscribe(
      data => {
        // console.log("photo: ");
        // console.log(data);
        this.hasRecord = true;
        this.inProgress.emit(true);
        this.mapsAPILoader.load().then(() => {
          this.placesService = new google.maps.places.PlacesService(this.photoEl.nativeElement);
          this.placesService.getDetails({placeId: data["result"]["place_id"]}, 
            (placeResult, status) => {
            if(status === 'OK') {
              if (placeResult.photos) {
                var tmp = placeResult.photos;
                this.photos = [[],[],[],[]];
                var items = [[],[],[],[]];
                for (var i = 0; i < tmp.length; ++i) {
                    var url = tmp[i].getUrl({
                    'maxWidth': tmp[i].width,
                    'maxHeight': undefined
                    }); 
                    items[i%4].push({id: i, url: url});
                }
                this.photos = items.slice();
                setTimeout(()=>{    //<<<---    using ()=> syntax
                  this.inProgress.emit(false);
                },2000);
                this.hasRecord = true;

              }else {
                setTimeout(()=>{    //<<<---    using ()=> syntax
                  this.inProgress.emit(false);
                },1000);
                this.hasRecord = false;
              }
              // this.cdr.detectChanges();
            }
          });
        });
    });

    this.searchService.detailGot$.subscribe(
      data => {
        this.hasRecord = true;
        this.inProgress.emit(true);
        this.mapsAPILoader.load().then(() => {
          this.placesService = new google.maps.places.PlacesService(this.photoEl.nativeElement);
          this.placesService.getDetails({placeId: data["result"]["place_id"]}, 
            (placeResult, status) => {
            if(status === 'OK') {
              if (placeResult.photos) {
                var tmp = placeResult.photos;
                this.photos = [[],[],[],[]];
                var items = [[],[],[],[]];
                for (var i = 0; i < tmp.length; ++i) {
                    var url = tmp[i].getUrl({
                    'maxWidth': tmp[i].width,
                    'maxHeight': undefined
                    }); 
                    items[i%4].push({id: i, url: url});
                }
                this.photos = items.slice();
                setTimeout(()=>{    //<<<---    using ()=> syntax
                  this.inProgress.emit(false);
                },2000);
                this.hasRecord = true;

              }else {
                setTimeout(()=>{    //<<<---    using ()=> syntax
                  this.inProgress.emit(false);
                },1000);
                this.hasRecord = false;
              }
              // this.cdr.detectChanges();
            }
          });
        });
    });
  }

}
