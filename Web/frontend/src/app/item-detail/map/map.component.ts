import { Component, OnInit, ElementRef, ViewChild, Directive, ChangeDetectorRef } from '@angular/core';
import { MapsAPILoader } from '@agm/core';
import { GoogleMapsAPIWrapper } from '@agm/core/services';
import { DirectionsRenderer, StreetViewPanorama } from '@ngui/map';

import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';

import { SearchService } from '../../search.service';
import { noWhitespaceValidator } from '../../no-whitespace.directive';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css', '../../app.component.css']
})
export class MapComponent implements OnInit {

  @ViewChild("from") fromEl: ElementRef;
  // @ViewChild("myMap") mapEl: ElementRef;
  @ViewChild("myMap") mapEl: DirectionsRenderer;
  @ViewChild("myPanel") panelEl: ElementRef;
  @ViewChild(DirectionsRenderer) directionsRendererDirective: DirectionsRenderer;
  @ViewChild(StreetViewPanorama) streetViewPanorama: StreetViewPanorama;

  directionsRenderer: google.maps.DirectionsRenderer;
  directionsResult: google.maps.DirectionsResult;
  direction: any = {
    origin: '',
    destination: '260 Broadway New York NY 10007',
    travelMode: 'WALKING'
  };
  // @Output() inProgress = new EventEmitter<boolean>();

  modeOptions = [
  {name: 'Driving', value: 'DRIVING'},
  {name: 'Bicycling', value: 'BICYCLING'},
  {name: 'Transit', value: 'TRANSIT'},
  {name: 'Walking', value: 'WALKING'}
  ];

  mapForm: FormGroup;
  hasRecord = true;
  addr: any;
  autocomplete: any;
  directionsDisplay: any;
  directionsService: any;
  origin: any;
  destination: any;
  showPanel =  false;
  curView = 'map';

  mapOpts = {
    center: new google.maps.LatLng(34.0266,-118.2831),
    zoom: 13,
  };

  renderOpts = {};

  constructor(
    private searchService: SearchService,
    private fb: FormBuilder,
    private chgDetRef: ChangeDetectorRef,
    private mapsAPILoader: MapsAPILoader,
    private gmapsAPIWrapper: GoogleMapsAPIWrapper) {
    
    
  }

  ngOnInit() {
    // this.initMap();
    // console.log(this.directionsRendererDirective);
    // this.autocomplete = autocomplete;
    this.directionsRendererDirective['initialized$'].subscribe( directionsRenderer => {
      this.directionsRenderer = directionsRenderer;
      // console.log(this.directionsRenderer);
    });

    this.searchService.getMyLocation().subscribe(
      data => {
        var loc = data.split(",");
        this.origin = new google.maps.LatLng(parseFloat(loc[0]), parseFloat(loc[1]));
    });

    this.searchService.getDetail().subscribe(
      data => {
        this.showPanel = false;
        if (data["result"]["geometry"]) {
          var lat = data["result"]["geometry"]["location"]["lat"];
          var lng = data["result"]["geometry"]["location"]["lng"];
          this.destination = new google.maps.LatLng(lat, lng);
          this.addr = `${data["result"]["name"]}, ${data["result"]["formatted_address"]}`;
          this.mapOpts.center = this.destination;
          this.direction.destination = this.destination;
          this.createForm();
          // this.chgDetRef.detectChanges();
          // console.log(this.destination);
        } else {
          this.hasRecord = false;
        }
    });
    this.searchService.detailGot$.subscribe(
      data => {
        this.showPanel = false;
        if (data["result"]["geometry"]) {
          var lat = data["result"]["geometry"]["location"]["lat"];
          var lng = data["result"]["geometry"]["location"]["lng"];
          this.destination = new google.maps.LatLng(lat, lng);
          this.addr = `${data["result"]["name"]}, ${data["result"]["formatted_address"]}`;
          this.mapOpts.center = this.destination;
          this.direction.destination = this.destination;
          this.createForm();
          // console.log(this.destination);
          // this.chgDetRef.detectChanges();
        } else {
          this.hasRecord = false;
        }
    });
  }

  createForm() {
    this.mapForm = this.fb.group({
      from: new FormControl('', [Validators.required, noWhitespaceValidator]), // <--- the FormControl called "name"
      to: this.addr,
      mode: 'DRIVING',
    });
  }

  // initMap() {
  //   this.mapsAPILoader.load().then(() => {
  //     // console.log(google);
  //     // console.log(google.maps.places);
  //     var _el = this.fromEl.nativeElement;
  //     this.autocomplete = new google.maps.places.Autocomplete(_el, {
  //       types: ["geocode"]
  //     });

  //     this.autocomplete.addListener("place_changed", () => {
  //       let place: google.maps.places.PlaceResult = this.autocomplete.getPlace();
  //         //verify result
  //         if (place.geometry === undefined || place.geometry === null) {
  //           return;
  //         }
  //         var lat = place.geometry.location.lat();
  //         var lng = place.geometry.location.lng();
  //         this.origin = new google.maps.LatLng(lat, lng);
  //     });
  //   });
  // }

  showDirection() {
    this.directionsRendererDirective['showDirections'](this.direction);
  }


  toggleView() {
    this.curView  = (this.curView === 'map' ? 'street' : 'map');
  }

  onSubmit() {
    if (!this.origin) {
      if (this.origin.toLowerCase() === 'my location') {

      }
      let geocoder = new google.maps.Geocoder();
      geocoder.geocode({ 'address': this.mapForm.value.from }, (results, status) => {
        // console.log(results);
          if (status === google.maps.GeocoderStatus.OK) {
              this.origin = results[0].geometry.location;
              this.direction = {
                origin: this.origin,
                destination: this.destination,
                travelMode: this.mapForm.value.mode,
                provideRouteAlternatives: true
              };
              this.directionsResult = this.directionsRenderer.getDirections();
              this.directionsRendererDirective['showDirections'](this.direction);
          } else {
              console.error('Error - ', results, ' & Status - ', status);
          }
      });
    } else {
      this.direction = {
        origin: this.origin,
        destination: this.destination,
        travelMode: this.mapForm.value.mode,
        provideRouteAlternatives: true
      };
      this.directionsResult = this.directionsRenderer.getDirections();
      this.directionsRendererDirective['showDirections'](this.direction);
    }

  }

  directionsChanged() {
    this.directionsResult = this.directionsRenderer.getDirections();
    this.chgDetRef.detectChanges();
  }

  placeChanged(place) {
      if (place.geometry === undefined || place.geometry === null) {
        return;
      }
      // var lat = place.geometry.location.lat();
      // var lng = place.geometry.location.lng();
      // this.origin = new google.maps.LatLng(lat, lng);
      this.origin = place.geometry.location;
      this.chgDetRef.detectChanges();
  }

  positionChanged() {
    this.chgDetRef.detectChanges();
  }
}