import { Component, ElementRef, Renderer2, ViewChild, OnInit, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import {} from '@types/googlemaps';
import { MapsAPILoader } from '@agm/core';

import { Category, DataForm, cate_values} from '../models';
import { SearchService } from '../search.service';
import { noWhitespaceValidator } from '../no-whitespace.directive';


@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})

export class SearchFormComponent implements OnInit {  

  default = {
    keyword: '', 
    category: 'default',
    distance: 10,
    from: 'here',
    location: '',
    };

  dataForm: DataForm;
  myForm: FormGroup;
  cate_values = cate_values;
  categories: Category[] = [];
  cur_loc = '34.0266,-118.2831';
  disable_search = true; 
  autocomplete: any;

  @ViewChild("location") locationEl: ElementRef;
  @Output() inProgress = new EventEmitter<boolean>();
  @Output() cleanBoard = new EventEmitter<boolean>();


  constructor(
    private fb: FormBuilder, 
    private renderer: Renderer2, 
    private el: ElementRef, 
    private searchService: SearchService,
    private mapsAPILoader: MapsAPILoader) {
    this.initData();
    this.createForm();
    this.initLocate();
  }

  ngOnInit() {
    this.mapsAPILoader.load().then(() => {
      // console.log(google);
      // console.log(google.maps.places);
      var _el = this.locationEl.nativeElement;
      this.autocomplete = new google.maps.places.Autocomplete(_el, {
        types: ["geocode"]
      });
      this.autocomplete.addListener("place_changed", () => {
        let place: google.maps.places.PlaceResult = this.autocomplete.getPlace();
          //verify result
          if (place.geometry === undefined || place.geometry === null) {
            return;
          }
          let loc = place.geometry.location.lat() + ',' + place.geometry.location.lng();
          this.myForm.value.location = loc;
          this.cur_loc = loc;
          this.myForm.value.from = 'here';
          // console.log(this.myForm);
      });
    });

    this.myForm.get('from').valueChanges.subscribe(val => {
      let control = this.myForm.get('location');
      val ==='here' ? control.disable(): control.enable();
    });
  }

  createForm() {
    this.myForm = this.fb.group({
      keyword: new FormControl('', [Validators.required, noWhitespaceValidator]),
      category: 'default',
      distance: 10,
      from: 'here',
      location: new FormControl({value: '', disabled: true}, [Validators.required, noWhitespaceValidator]),
    });
  }

  initData() {
    this.categories = [];
    for(var i = 0; i < this.cate_values.length; ++i){
      let cate = new Category(this.cate_values[i], 
          this.cate_values[i].replace('_', ' ').replace(this.cate_values[i][0], this.cate_values[i][0].toUpperCase()));
      this.categories.push(cate);
    }
  }

  initLocate() {
    this.searchService.getIPLocate()
      .subscribe(res => {
        this.disable_search = false;
        this.cur_loc = res['lat'] + ',' + res['lon'];
        this.searchService.setMyLocaation(this.cur_loc);
      },
      error => {
        console.log(error);
      });
  }


  clear() {
    this.cleanBoard.emit(true);
    this.myForm.reset(this.default);
  }


  onSubmit() {
    this.inProgress.emit(true);
    this.dataForm = this.prepareSaveForm();
    this.searchService.reqResult(this.dataForm)
      .subscribe(data => {
        this.searchService.sendResult(data);
      },
      error => {
        console.log(error);
      });
  }

  prepareSaveForm(): DataForm {
    const formModel = this.myForm.value;

    const savedForm: DataForm = {
      keyword: formModel.keyword as string,
      category: formModel.category as string,
      distance: formModel.distance as number,
      from: formModel.from as string,
      location: (formModel.from==='here'? this.cur_loc: formModel.location as string),
    };
    return savedForm;
  }  

}
