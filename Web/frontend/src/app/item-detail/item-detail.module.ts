import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AgmCoreModule } from '@agm/core';
import { AgmDirectionModule } from 'agm-direction';
 import { NguiMapModule} from '@ngui/map';
import { LazyLoadImageModule } from 'ng-lazyload-image';


import { ItemDetailComponent } from './item-detail.component';
import { InfoComponent } from './info/info.component';
import { PhotoComponent } from './photo/photo.component';
import { MapComponent } from './map/map.component';
import { ReviewComponent } from './review/review.component';

@NgModule({
  imports: [
    CommonModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    NgbModule,
    AgmCoreModule,
    AgmDirectionModule,
    NguiMapModule,
    LazyLoadImageModule
  ],
  declarations: [
    ItemDetailComponent,
    InfoComponent,
    PhotoComponent,
    MapComponent,
    ReviewComponent
  ],
  exports: [
    ItemDetailComponent
  ]
})
export class ItemDetailModule { }
