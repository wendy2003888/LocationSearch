import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AgmCoreModule } from '@agm/core';
import { GoogleMapsAPIWrapper } from '@agm/core/services';
import { AgmDirectionModule } from 'agm-direction';
import { AngularFontAwesomeModule } from 'angular-font-awesome';
import { MatTabsModule} from '@angular/material/tabs';
import { NguiMapModule} from '@ngui/map';
import { LazyLoadImageModule } from 'ng-lazyload-image';

import { SearchService } from './search.service';

import { AppComponent } from './app.component';
import { SearchFormComponent } from './search-form/search-form.component';
import { PipeUtilsPipe } from './pipe-utils.pipe';
import { ListComponent } from './list/list.component';
import { FavoriteComponent } from './favorite/favorite.component';
import { BoardComponent } from './board/board.component';

import { ItemDetailModule } from './item-detail/item-detail.module';
import { NoWhitespaceDirective } from './no-whitespace.directive';

@NgModule({
  declarations: [
    AppComponent,
    SearchFormComponent,
    PipeUtilsPipe,
    ListComponent,
    FavoriteComponent,
    BoardComponent,
    NoWhitespaceDirective
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    ItemDetailModule,
    NgbModule.forRoot(),
    MatTabsModule,
    AngularFontAwesomeModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyBLSnRWiyyT2SJzKoE_X5QSU_FGYLjOnf0',
      libraries: ["places"]
    }),
    AgmDirectionModule,
    NguiMapModule.forRoot({
      apiUrl: 'https://maps.google.com/maps/api/js?key=AIzaSyBLSnRWiyyT2SJzKoE_X5QSU_FGYLjOnf0'
    }),
    LazyLoadImageModule
  ],
  providers: [SearchService, GoogleMapsAPIWrapper],
  bootstrap: [AppComponent]
})

export class AppModule { }
